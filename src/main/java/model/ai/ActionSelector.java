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
import model.elements.GameAction;
import model.elements.GamePhase;
import model.ennemis.Ennemi;
import model.items.Item;

/**
 * Responsable de la sélection d'actions : exploration aléatoire et exploitation de la Q-table.
 */
public class ActionSelector {

  private final Random random;
  private final GameStateEncoder encoder;

  public ActionSelector(Random random, GameStateEncoder encoder) {
    this.random = random;
    this.encoder = encoder;
  }

  // ===== Exploration =====

  public GameAction exploreActivateAction(List<Ennemi> availableEnemies) {
    Ennemi randomEnnemi = availableEnemies.get(random.nextInt(availableEnemies.size()));
    return new GameAction(GamePhase.ACTIVATE_PILE, randomEnnemi);
  }

  public List<GameAction> exploreEngageActions(List<Dice> availableDice) {
    // exception s'il y a un doublon
    Set<Dice> set = new HashSet<>(availableDice);
    if (set.size() != availableDice.size()) {
      throw new IllegalStateException("Doublon détecté dans ActionSelector.exploreEngageActions : " + availableDice);
    }

    List<GameAction> actions = new ArrayList<>();
    List<Dice> shuffled = new ArrayList<>(availableDice);
    Collections.shuffle(shuffled, random);
    int nbDiceToEngage =  random.nextInt(availableDice.size() + 1); // 0 à tous les dés

    for (int i = 0; i < nbDiceToEngage; i++) {
      actions.add(new GameAction(GamePhase.ENGAGE_DICE, shuffled.get(i)));
    }

    //System.out.println("Exploration : " + nbDiceToEngage + " dés engagés.");

    return actions;
  }

  public List<GameAction> exploreAssignActions(List<Dice> assignableDice, List<Ennemi> activeEnnemis) {
    List<GameAction> actions = new ArrayList<>();
    for (Dice dice : assignableDice) {
      if (!activeEnnemis.isEmpty() && random.nextDouble() < 0.5) {
        Ennemi randomEnemy = activeEnnemis.get(random.nextInt(activeEnnemis.size()));
        actions.add(new GameAction(GamePhase.ASSIGN_DICE, dice, randomEnemy));
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
}
