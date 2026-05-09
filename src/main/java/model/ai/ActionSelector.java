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

  public List<GameAction> exploreAssignActions(List<Dice> assignableDice, List<Ennemi> activeEnnemis) {
    List<GameAction> actions = new ArrayList<>();

    boolean mustExhaustHitWhenAssignCritHitEffect =
        activeEnnemis.stream().anyMatch(ennemi -> !ennemi.isDefeatedFlag() &&
            ennemi.getEffects().stream().anyMatch(effect -> effect instanceof ExhaustHitWhenAssignCritHitEffect));

    List<Dice> diceBecomeExhausted = new ArrayList<>();
    List<Dice> diceBeingAssigned = new ArrayList<>();

    for (Dice dice : assignableDice) {
      if (diceBecomeExhausted.contains(dice)) {
        continue; // ce dé est déjà épuisé, on ne peut pas lui assigner une touche critique
      }
      if (!activeEnnemis.isEmpty() && random.nextDouble() < 0.5) {
        if (mustExhaustHitWhenAssignCritHitEffect && dice.getLastRoll() == dice.getFaces()[5]) {
          // parcourir la liste assignableDice pour tirer un dé à épuiser. Si c'est lui même, ou si le dé est dans diceBecomeExhausted, alors la touche critique ne sera pas assigné
          Dice diceToExhaust = pickDiceToExhaust(assignableDice, dice, diceBeingAssigned, diceBecomeExhausted);
          if (diceToExhaust == null) {
            // la touche critique n'est pas assignée, on continue comme si de rien n'était
            continue;
          }
          diceBecomeExhausted.add(diceToExhaust);
        }
        Ennemi randomEnemy = activeEnnemis.get(random.nextInt(activeEnnemis.size()));
        actions.add(new GameAction(GamePhase.ASSIGN_DICE, dice, randomEnemy));
        diceBeingAssigned.add(dice);
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
