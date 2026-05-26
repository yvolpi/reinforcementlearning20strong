package model.ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import model.Dice;
import model.DiceColor;
import model.GameState;
import model.effets.ennemi.EngageAllSameColorDiceEffect;
import model.effets.ennemi.EnnemyEffect;
import model.effets.ennemi.ExhaustHitWhenAssignCritHitEffect;
import model.effets.ennemi.ForbidMultipleColorsToAssignEffect;
import model.effets.ennemi.MaxAssignDiceEffect;
import model.effets.ennemi.MaxEngagedDicePerEngageEffect;
import model.effets.ennemi.MaxOneEnnemiToKillEffect;
import model.effets.ennemi.MustAssignPairDiceEffect;
import model.effets.ennemi.PorteSporeExpectorantEffect;
import model.elements.GameAction;
import model.elements.GamePhase;
import model.ennemis.Ennemi;
import model.ennemis.EnnemiEffectCouple;
import model.items.Item;
import model.missions.M11;
import model.random.CustomRandom;

/**
 * Responsable de la sélection d'actions : exploration aléatoire et exploitation de la Q-table.
 */
public class ActionSelector {

  private final CustomRandom random;

  public ActionSelector(CustomRandom random, GameStateEncoder encoder) {
    this.random = random;
  }

  // ===== Exploration =====

  public GameAction exploreActivateAction(List<Ennemi> availableEnemies) {
    Ennemi randomEnnemi = availableEnemies.get(random.nextInt(availableEnemies.size()));
    return new GameAction(GamePhase.ACTIVATE_PILE, randomEnnemi);
  }

  public List<GameAction> exploreEngageActions(List<Dice> availableDice, GameState gameState) {
    int nbMaxDiceToEngage = gameState.getMaxEngagedDicePerTurn() - gameState.getEngagedDices().size();
    int maxEngagedDicePerEngageEffect = maxEngagedDicePerEngageEffect(availableDice.size(), gameState.getActiveEnnemis());
    nbMaxDiceToEngage = Math.min(nbMaxDiceToEngage, maxEngagedDicePerEngageEffect);

    // exception s'il y a un doublon
    Set<Dice> set = new HashSet<>(availableDice);
    if (set.size() != availableDice.size()) {
      throw new IllegalStateException("Doublon détecté dans ActionSelector.exploreEngageActions : " + availableDice);
    }

    boolean hasMustEngageAllDiceColorEffect = hasMustEngageAllDiceColorEffect(gameState.getActiveEnnemis());

    List<GameAction> actions = new ArrayList<>();

    if (hasMustEngageAllDiceColorEffect) {
      int nbDiceBeingEngaged = 0;
      // 1. Grouper les dés par couleur
      Map<DiceColor, List<Dice>> diceByColor = new TreeMap<>();
      for (Dice dice : availableDice) {
        diceByColor.computeIfAbsent(dice.getColor(), k -> new ArrayList<>()).add(dice);
      }

      List<DiceColor> orderedColors = diceByColor.keySet().stream()
          .sorted(Comparator.comparingInt(DiceColor::getStrengthRanking)) // trier par ordre de force
          .toList();

      for (DiceColor diceColor : orderedColors) {
        if (nbDiceBeingEngaged + diceByColor.get(diceColor).size() > nbMaxDiceToEngage ) {
          continue; // on ne peut pas engager tous les dés de cette couleur sans dépasser la limite, on les ignore
        }
        if (random.nextDouble() < 0.5) { // 50% de chances d'engager les dés de cette couleur
          for (Dice dice : diceByColor.get(diceColor)) {
            actions.add(new GameAction(GamePhase.ENGAGE_DICE, dice));
            nbDiceBeingEngaged++;
          }
        }
      }

    } else {
      List<Dice> shuffled = new ArrayList<>(availableDice);
      random.shuffle(shuffled);
      int nbDiceToEngage =  random.nextInt(availableDice.size() + 1); // 0 à tous les dés
      nbDiceToEngage = Math.min(nbDiceToEngage, nbMaxDiceToEngage); // ne pas dépasser le maximum autorisé
      for (int i = 0; i < nbDiceToEngage; i++) {
        actions.add(new GameAction(GamePhase.ENGAGE_DICE, shuffled.get(i)));
      }
    }

    //System.out.println("Exploration : " + nbDiceToEngage + " dés engagés.");

    return actions;
  }

  public List<GameAction> exploreAssignActions(GameState gameState, List<Dice> assignableDice, List<Ennemi> activeEnnemis, double assignRate) {
    if (activeEnnemis.isEmpty()) return List.of();

    boolean mustExhaustOnCrit               = hasMustExhaustOnCritEffect(activeEnnemis);
    boolean mustAssignByPair                = hasMustAssignByPairEffect(activeEnnemis);
    Integer assignLimit                     = getMaxAssignDiceEffectValue(activeEnnemis);
    boolean maxOneEnnemiToKill              = getMaxOneEnnemiToKillEffect(activeEnnemis);
    boolean mustAssignOneDiceColorPerEnnemi = hasMustAssignOneDiceColorPerEnnemi(activeEnnemis);

    List<Dice> exhausted = new ArrayList<>();
    List<Dice> assigned  = new ArrayList<>();
    List<Dice> remaining = new ArrayList<>(assignableDice);
    List<GameAction> actions = new ArrayList<>();

    Map<Ennemi, DiceColor> couleurParEnnemi = new HashMap<>();
    for (Ennemi ennemi : activeEnnemis) {
      if (!ennemi.getAssignedDice().isEmpty()) {
        couleurParEnnemi.put(ennemi, ennemi.getAssignedDice().getFirst().getColor());
      }
    }

    Ennemi uniqueTarget = null;

    for (Dice dice : assignableDice) {
      if (isUnavailable(dice, exhausted, assigned)) continue;
      if (random.nextDouble() >= assignRate) continue;
      if (assigned.size() >= assignLimit) break;

      // Filtre les cibles valides : si la contrainte est inactive, validTargets == activeEnnemis
      List<Ennemi> validTargets = new ArrayList<>();
      if (activeEnnemis.size() > 1) {
        // on ne doit pas prendre le seigneur de l'essaim (invincible tant qu'il reste d'autres ennemis)
        validTargets = activeEnnemis.stream()
            .filter(e -> !e.getName().equals("SEIGNEUR_DE_LESSAIM"))
            .toList();
      } else {
        validTargets = activeEnnemis;
      }

      boolean isActiveMissionM11 = gameState.getActiveMission() != null && gameState.getActiveMission() instanceof M11;

      validTargets = mustAssignOneDiceColorPerEnnemi
          ? validTargets.stream()
            .filter(e -> !couleurParEnnemi.containsKey(e)
                         || couleurParEnnemi.get(e) == dice.getColor())
            .toList()
          : validTargets;

      if (!isActiveMissionM11) {
        // on ne doit pas assigner de dé aux ennemis vaincus
        validTargets = validTargets.stream()
            .filter(e -> !e.isDefeatedFlag())
            .toList();
      }

      if (validTargets.isEmpty()) continue;

      // Un seul endroit pour initialiser la cible unique
      if (maxOneEnnemiToKill && uniqueTarget == null) {
        uniqueTarget = pickRandomEnemy(validTargets);
      }

      Ennemi target = maxOneEnnemiToKill ? uniqueTarget : pickRandomEnemy(validTargets);

      // Décision du type d'assignation
      boolean needsYellowCompanion = target.getEffects().stream()
          .anyMatch(effect -> effect instanceof PorteSporeExpectorantEffect && effect.isActivated())
          && dice.getColor() != DiceColor.JAUNE;

      if (mustAssignByPair) {
        if (assigned.size() >= assignLimit - 1) break;
        if (needsYellowCompanion && dice.getColor() != DiceColor.JAUNE) {
          // Contrainte impossible à satisfaire : on ne génère pas l'action
          continue;
        }

        tryAssignPair(dice, target, remaining, assigned, exhausted,
            mustExhaustOnCrit, actions, couleurParEnnemi);
      } else if (needsYellowCompanion) {
        if (assigned.size() >= assignLimit - 1) break; // il faut de la place pour 2 dés
        tryAssignSingleWithYellow(dice, target, remaining, assigned, exhausted,
            mustExhaustOnCrit, actions, couleurParEnnemi);
      } else {
        tryAssignSingle(dice, target, remaining, assigned, exhausted,
            mustExhaustOnCrit, actions, couleurParEnnemi);
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

  public GameAction exploreDropNonMandatoryEnnemiAction(List<Ennemi> nonMandatoryEnnemis) {
    return new GameAction(nonMandatoryEnnemis.get(random.nextInt(nonMandatoryEnnemis.size())).getPileNumber());
  }


  // ===== Exploitation =====

  public GameAction findBestDecideGiveUpMissionAction(List<GameAction> possibleActions, Map<String, Double> actionValues) {
    GameAction bestAction = null;
    double bestValue = Double.NEGATIVE_INFINITY;
    for (GameAction action : possibleActions) {
      double value = actionValues.getOrDefault(ActionKeyEncoder.encodeDecideGiveUpMissionAction(action), 0.0);
      if (value > bestValue) {
        bestValue = value;
        bestAction = action;
      }
    }
    return bestAction;
  }

  public GameAction findBestDecidePileM10Action(List<GameAction> possibleActions, Map<String, Double> actionValues) {
    GameAction bestAction = null;
    double bestValue = Double.NEGATIVE_INFINITY;
    for (GameAction action : possibleActions) {
      double value = actionValues.getOrDefault(ActionKeyEncoder.encodeDecidePileM10Action(action), 0.0);
      if (value > bestValue) {
        bestValue = value;
        bestAction = action;
      }
    }
    return bestAction;
  }

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

  public GameAction findBestDropNonMandatoryEnnemiAction(List<GameAction> possibleActions, Map<String, Double> actionValues) {
    GameAction bestAction = null;
    double bestValue = Double.NEGATIVE_INFINITY;
    for (GameAction action : possibleActions) {
      double value = actionValues.getOrDefault(ActionKeyEncoder.encodeDropNonMandatoryEnnemiAction(action), 0.0);
      if (value > bestValue) {
        bestValue = value;
        bestAction = action;
      }
    }
    return bestAction;
  }

  // ===== Utilitaires =====

  // ---- Effets actifs ----

  private int maxEngagedDicePerEngageEffect(int defaultMax, List<Ennemi> ennemis) {
    return ennemis.stream()
        .filter(e -> !e.isDefeatedFlag())
        .flatMap(e -> e.getEffects().stream())
        .filter(effect -> effect instanceof MaxEngagedDicePerEngageEffect && effect.isActivated())
        .map(effect -> ((MaxEngagedDicePerEngageEffect) effect).getMaxEngagedDicePerEngage())
        .min(Integer::compareTo)
        .orElse(defaultMax);
  }

  private boolean hasMustEngageAllDiceColorEffect(List<Ennemi> ennemis) {
    return ennemis.stream()
        .filter(e -> !e.isDefeatedFlag())
        .flatMap(e -> e.getEffects().stream())
        .anyMatch(effect -> effect instanceof EngageAllSameColorDiceEffect && effect.isActivated());
  }

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

  private boolean getMaxOneEnnemiToKillEffect(List<Ennemi> ennemis) {
    for (Ennemi ennemi : ennemis) {
      if (!ennemi.isDefeatedFlag()) {
        for (var effect : ennemi.getEffects()) {
          if (effect instanceof MaxOneEnnemiToKillEffect && effect.isActivated()) {
            return true;
          }
        }
      }
    }
    return false;
  }

  private boolean hasMustAssignOneDiceColorPerEnnemi(List<Ennemi> ennemis) {
    for (Ennemi ennemi : ennemis) {
      if (!ennemi.isDefeatedFlag()) {
        for (var effect : ennemi.getEffects()) {
          if (effect instanceof ForbidMultipleColorsToAssignEffect && effect.isActivated()) {
            return true;
          }
        }
      }
    }
    return false;
  }


  private void tryAssignSingle(Dice dice, Ennemi target,
      List<Dice> remaining, List<Dice> assigned, List<Dice> exhausted,
      boolean mustExhaustOnCrit, List<GameAction> actions,
      Map<Ennemi, DiceColor> ennemiDiceColorMap) {

    if (!prepareExhaustIfCrit(dice, null, remaining, assigned, exhausted, mustExhaustOnCrit)) return;
    actions.add(new GameAction(GamePhase.ASSIGN_DICE, dice, target));
    markAssigned(dice, assigned, remaining);
    ennemiDiceColorMap.put(target, dice.getColor());
  }

  // ---- Assignation par paire ----

  private void tryAssignPair(Dice dice, Ennemi target,
      List<Dice> remaining, List<Dice> assigned, List<Dice> exhausted,
      boolean mustExhaustOnCrit, List<GameAction> actions,
      Map<Ennemi, DiceColor> ennemiDiceColorMap) {

    Dice partner = findPartner(dice, remaining, assigned, exhausted);
    if (partner == null) return;
    if (!prepareExhaustIfCrit(dice, null, remaining, assigned, exhausted, mustExhaustOnCrit)) return;
    if (!prepareExhaustIfCrit(partner, dice, remaining, assigned, exhausted, mustExhaustOnCrit)) return;
    actions.add(new GameAction(GamePhase.ASSIGN_DICE, dice, target));
    actions.add(new GameAction(GamePhase.ASSIGN_DICE, partner, target));
    markAssigned(dice, assigned, remaining);
    markAssigned(partner, assigned, remaining);
    ennemiDiceColorMap.put(target, dice.getColor());
  }

  private void tryAssignSingleWithYellow(Dice dice, Ennemi target,
      List<Dice> remaining, List<Dice> assigned, List<Dice> exhausted,
      boolean mustExhaustOnCrit, List<GameAction> actions,
      Map<Ennemi, DiceColor> ennemiDiceColorMap) {

    // Chercher un dé jaune disponible
    Dice yellowDice = remaining.stream()
        .filter(d -> d.getColor() == DiceColor.JAUNE
            && !assigned.contains(d)
            && !exhausted.contains(d)
            && d.getLastRoll() >= 1)
        .findFirst().orElse(null);

    if (yellowDice == null) {
      // Contrainte impossible à satisfaire : on ne génère pas l'action
      return;
    }

    // On doit vérifier séparément si chacun des deux dés (le principal et le jaune) déclenche un effet critique.*
    // Pour chaque dé, on interdit d’épuiser l’autre (car il va aussi être assigné dans ce tour).
    // Si l’un des deux effets ne peut pas être appliqué (pas de dé à épuiser), on annule toute l’assignation.

    if (!prepareExhaustIfCrit(dice, yellowDice, remaining, assigned, exhausted, mustExhaustOnCrit)) return;
    if (!prepareExhaustIfCrit(yellowDice, dice, remaining, assigned, exhausted, mustExhaustOnCrit)) return;

    actions.add(new GameAction(GamePhase.ASSIGN_DICE, dice, target));
    actions.add(new GameAction(GamePhase.ASSIGN_DICE, yellowDice, target));
    markAssigned(dice, assigned, remaining);
    markAssigned(yellowDice, assigned, remaining);
    // Pas de mise à jour de couleurParEnnemi : le PorteSporeExpectorant
    // accepte plusieurs couleurs par nature
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

    if (!mustExhaustOnCrit || !dice.isCriticHit()) return true;

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
