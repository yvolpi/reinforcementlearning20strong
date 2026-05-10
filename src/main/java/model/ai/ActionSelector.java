package model.ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import model.Dice;
import model.DiceColor;
import model.GameState;
import model.effets.ennemi.EnnemyEffect;
import model.effets.ennemi.ExhaustHitWhenAssignCritHitEffect;
import model.effets.ennemi.MaxAssignDiceEffect;
import model.effets.ennemi.MustAssignPairDiceEffect;
import model.elements.GameAction;
import model.elements.GamePhase;
import model.ennemis.Ennemi;
import model.ennemis.EnnemiEffectCouple;
import model.items.Item;
import model.random.CustomRandom;

/**
 * Responsable de la sélection d'actions : exploration aléatoire et exploitation de la Q-table.
 */
public class ActionSelector {

  private final CustomRandom random;
  private final GameStateEncoder encoder;

  public ActionSelector(CustomRandom random, GameStateEncoder encoder) {
    this.random = random;
    this.encoder = encoder;
  }

  // ===== Exploration =====

  public GameAction exploreActivateAction(List<Ennemi> availableEnemies) {
    Ennemi randomEnnemi = availableEnemies.get(random.nextInt(availableEnemies.size()));
    return new GameAction(GamePhase.ACTIVATE_PILE, randomEnnemi);
  }

  public List<GameAction> exploreEngageActions(List<Dice> availableDice, GameState gameState) {
    int nbMaxDiceToEngage = gameState.getMaxEngagedDicePerTurn() - gameState.getEngagedDices().size();
    // exception s'il y a un doublon
    Set<Dice> set = new HashSet<>(availableDice);
    if (set.size() != availableDice.size()) {
      throw new IllegalStateException("Doublon détecté dans ActionSelector.exploreEngageActions : " + availableDice);
    }

    List<GameAction> actions = new ArrayList<>();
    List<Dice> shuffled = new ArrayList<>(availableDice);
    random.shuffle(shuffled);
    int nbDiceToEngage =  random.nextInt(availableDice.size() + 1); // 0 à tous les dés
    nbDiceToEngage = Math.min(nbDiceToEngage, nbMaxDiceToEngage); // ne pas dépasser le maximum autorisé
    for (int i = 0; i < nbDiceToEngage; i++) {
      actions.add(new GameAction(GamePhase.ENGAGE_DICE, shuffled.get(i)));
    }

    //System.out.println("Exploration : " + nbDiceToEngage + " dés engagés.");

    return actions;
  }

  public List<GameAction> exploreAssignActions(List<Dice> assignableDice, List<Ennemi> activeEnnemis, double assignRate) {
    if (activeEnnemis.isEmpty()) return List.of();

    boolean mustExhaustOnCrit = hasMustExhaustOnCritEffect(activeEnnemis);
    boolean mustAssignByPair  = hasMustAssignByPairEffect(activeEnnemis);
    Integer assignLimit = getMaxAssignDiceEffectValue(activeEnnemis);

    List<Dice> exhausted  = new ArrayList<>();
    List<Dice> assigned   = new ArrayList<>();
    List<Dice> remaining  = new ArrayList<>(assignableDice);
    List<GameAction> actions = new ArrayList<>();

    for (Dice dice : assignableDice) {
      if (isUnavailable(dice, exhausted, assigned)) continue;
      if (random.nextDouble() >= assignRate) continue;
      if (assigned.size() >= assignLimit) break;

      if (mustAssignByPair) {
        if (assigned.size() >= assignLimit - 1) break; // S'assurer qu'on a la place d'assigner les 2 dés
        tryAssignPair(dice, activeEnnemis, remaining, assigned, exhausted, mustExhaustOnCrit, actions);
      } else {
        tryAssignSingle(dice, activeEnnemis, remaining, assigned, exhausted, mustExhaustOnCrit, actions);
      }
    }
    return actions;
  }

  public List<GameAction> exploreUseActions(List<Item> usableItems, GamePhase phase) {
    List<GameAction> actions = new ArrayList<>();
    for (Item item : usableItems) {
      if (random.nextDouble() < 0.5) {
        actions.add(new GameAction(phase, item));
      }
    }
    return actions;
  }

  public GameAction exploreItemToThrowAction(List<Item> items) {
    if (items.isEmpty()) return new GameAction(GamePhase.THROW_ITEM, (Item) null);
    int idx = random.nextInt(items.size() + 1); // 0 à items.size() inclus
    if (idx == items.size()) {
      // Choix : ne rien jeter
      return new GameAction(GamePhase.THROW_ITEM, (Item) null);
    } else {
      // Choix : jeter l’item à l’index idx
      return new GameAction(GamePhase.THROW_ITEM, items.get(idx));
    }
  }

  public GameAction exploreLunoculationEffectAction(List<Ennemi> ennemis) {
    List<EnnemiEffectCouple> ennemiEffects = new ArrayList<>();
    for (Ennemi ennemi : ennemis) {
      if (ennemi.getClassValue() == 1 || ennemi.getClassValue() == 2) {
        for (EnnemyEffect ennemiEffect : ennemi.getEffects()) {
          if (ennemiEffect.isActivated()) {
            ennemiEffects.add(new EnnemiEffectCouple(ennemi, ennemiEffect));
          }
        }
      }
    }
    if (ennemiEffects.isEmpty()) {
      return new GameAction((EnnemyEffect) null, null);
    } else {
      EnnemiEffectCouple randomEffect = ennemiEffects.get(random.nextInt(ennemiEffects.size()));
      return new GameAction(randomEffect.effect(), randomEffect.ennemi());
    }

  }


  // ===== Exploitation =====

  public GameAction findBestDecideActivateBossAction(List<GameAction> possibleActions,
      Map<String, Double> actionValues) {
    GameAction bestAction = null;
    double bestValue = Double.NEGATIVE_INFINITY;
    for (GameAction action : possibleActions) {
      double value = actionValues.getOrDefault(ActionKeyEncoder.encodeActivateBossAction(action), 0.0);
      if (value > bestValue) {
        bestValue = value;
        bestAction = action;
      }
    }
    return bestAction;
  }

  public GameAction findBestActivateAction(List<GameAction> possibleActions,
      Map<String, Double> actionValues) {
    GameAction bestAction = null;
    double bestValue = Double.NEGATIVE_INFINITY;
    for (GameAction action : possibleActions) {
      double value = actionValues.getOrDefault(ActionKeyEncoder.encodeActivateAction(action), 0.0);
      if (value > bestValue) {
        bestValue = value;
        bestAction = action;
      }
    }
    return bestAction;
  }

  public GameAction findBestEngageAction(List<GameAction> possibleActions,
      Map<String, Double> actionValues) {
    GameAction bestAction = null;
    double bestValue = Double.NEGATIVE_INFINITY;
    for (GameAction action : possibleActions) {
      double value = actionValues.getOrDefault(encoder.encodeEngageAction(action), 0.0);
      if (value > bestValue) {
        bestValue = value;
        bestAction = action;
      }
    }
    return bestAction;
  }

  public GameAction findBestLunoculationAction(List<GameAction> possibleActions,
      Map<String, Double> actionValues) {
    GameAction bestAction = null;
    double bestValue = Double.NEGATIVE_INFINITY;
    for (GameAction action : possibleActions) {
      double value = actionValues.getOrDefault(ActionKeyEncoder.encodeActivateAction(action), 0.0);
      if (value > bestValue) {
        bestValue = value;
        bestAction = action;
      }
    }
    return bestAction;
  }

  // ===== Utilitaires =====

  public int getDiceColorPriority(DiceColor color) {
    return switch (color) {
      case ROUGE  -> 5;
      case VIOLET -> 4;
      case BLEU   -> 3;
      case VERT   -> 2;
      case JAUNE  -> 1;
    };
  }

  // ---- Effets actifs ----

  private boolean hasMustExhaustOnCritEffect(List<Ennemi> ennemis) {
    return ennemis.stream()
        .filter(e -> !e.isDefeatedFlag())
        .flatMap(e -> e.getEffects().stream())
        .anyMatch(effect -> effect instanceof ExhaustHitWhenAssignCritHitEffect);
  }

  private boolean hasMustAssignByPairEffect(List<Ennemi> ennemis) {
    return ennemis.stream()
        .filter(e -> !e.isDefeatedFlag())
        .flatMap(e -> e.getEffects().stream())
        .anyMatch(effect -> effect instanceof MustAssignPairDiceEffect);
  }

  private Integer getMaxAssignDiceEffectValue(List<Ennemi> ennemis) {
    return ennemis.stream()
        .filter(e -> !e.isDefeatedFlag())
        .flatMap(e -> e.getEffects().stream())
        .filter(effect -> effect instanceof MaxAssignDiceEffect)
        .map(effect -> ((MaxAssignDiceEffect) effect).getMaxDice())
        .min(Integer::compareTo)
        .orElse(Integer.MAX_VALUE);
  }

  private void tryAssignSingle(Dice dice, List<Ennemi> activeEnnemis,
      List<Dice> remaining, List<Dice> assigned, List<Dice> exhausted,
      boolean mustExhaustOnCrit, List<GameAction> actions) {

    if (!prepareExhaustIfCrit(dice, null, remaining, assigned, exhausted, mustExhaustOnCrit)) return;

    Ennemi target = pickRandomEnemy(activeEnnemis);
    actions.add(new GameAction(GamePhase.ASSIGN_DICE, dice, target));
    markAssigned(dice, assigned, remaining);
  }

  // ---- Assignation par paire ----

  private void tryAssignPair(Dice dice, List<Ennemi> activeEnnemis,
      List<Dice> remaining, List<Dice> assigned, List<Dice> exhausted,
      boolean mustExhaustOnCrit, List<GameAction> actions) {

    Dice partner = findPartner(dice, remaining, assigned, exhausted);
    if (partner == null) return;

    if (!prepareExhaustIfCrit(dice, null, remaining, assigned, exhausted, mustExhaustOnCrit)) return;
    if (!prepareExhaustIfCrit(partner, dice, remaining, assigned, exhausted, mustExhaustOnCrit)) return;

    Ennemi target = pickRandomEnemy(activeEnnemis);

    actions.add(new GameAction(GamePhase.ASSIGN_DICE, dice, target));
    actions.add(new GameAction(GamePhase.ASSIGN_DICE, partner, target));
    markAssigned(dice, assigned, remaining);
    markAssigned(partner, assigned, remaining);
  }

  // ---- Utilitaires ----

  private boolean isUnavailable(Dice dice, List<Dice> exhausted, List<Dice> assigned) {
    return exhausted.contains(dice) || assigned.contains(dice);
  }

  private Dice findPartner(Dice dice, List<Dice> remaining, List<Dice> assigned, List<Dice> exhausted) {
    return remaining.stream()
        .filter(d -> d.getColor() == dice.getColor()
            && !d.equals(dice)
            && !assigned.contains(d)
            && !exhausted.contains(d))
        .findFirst().orElse(null);
  }


  /**
   * Si le dé est un critique et que l'effet l'exige, on choisit un dé à épuiser.
   * Retourne false si l'assignation doit être annulée.
   *
   * @param forbidden dé qui ne peut pas être choisi comme cible d'épuisement (ex: le partenaire déjà choisi)
   */
  private boolean prepareExhaustIfCrit(Dice dice, Dice forbidden,
      List<Dice> remaining, List<Dice> assigned, List<Dice> exhausted,
      boolean mustExhaustOnCrit) {

    if (!mustExhaustOnCrit || dice.getLastRoll() != dice.getFaces()[5]) return true;

    Dice toExhaust = pickDiceToExhaust(remaining, dice, assigned, exhausted);
    if (toExhaust == null || toExhaust.equals(forbidden)) return false;

    exhausted.add(toExhaust);
    remaining.remove(toExhaust);
    return true;
  }

  private void markAssigned(Dice dice, List<Dice> assigned, List<Dice> remaining) {
    assigned.add(dice);
    remaining.remove(dice);
  }

  private Ennemi pickRandomEnemy(List<Ennemi> activeEnnemis) {
    return activeEnnemis.get(random.nextInt(activeEnnemis.size()));
  }

  private Dice pickDiceToExhaust(List<Dice> assignableDice, Dice current, List<Dice> diceBeingAssigned, List<Dice> diceBecomeExhausted) {
    List<Dice> candidates = new ArrayList<>();
    for (Dice d : assignableDice) {
      if (!d.equals(current) && !diceBeingAssigned.contains(d) && !diceBecomeExhausted.contains(d)) {
        candidates.add(d);
      }
    }
    if (candidates.isEmpty()) return null;
    return candidates.get(random.nextInt(candidates.size()));
  }
}
