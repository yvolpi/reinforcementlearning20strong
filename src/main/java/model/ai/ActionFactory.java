package model.ai;

import java.util.ArrayList;
import java.util.List;
import model.Dice;
import model.GameState;
import model.elements.GameAction;
import model.elements.GamePhase;
import model.ennemis.Ennemi;
import model.items.Item;

/**
 * Responsable de la création de la liste des actions possibles pour chaque phase.
 */
public class ActionFactory {

  public List<GameAction> createPossibleActivateActions(List<Ennemi> availableEnemies) {
    List<GameAction> actions = new ArrayList<>();
    for (Ennemi ennemi : availableEnemies) {
      actions.add(new GameAction(GamePhase.ACTIVATE_PILE, ennemi));
    }
    return actions;
  }

  public List<GameAction> createPossibleEngageActions(List<Dice> availableDice) {
    List<GameAction> actions = new ArrayList<>();
    for (Dice dice : availableDice) {
      actions.add(new GameAction(GamePhase.ENGAGE_DICE, dice));
    }
    actions.add(null); // Action "ne rien engager"
    return actions;
  }

  public List<GameAction> createPossibleAssignActions(List<Dice> assignableDice, List<Ennemi> activeEnnemis) {
    List<GameAction> actions = new ArrayList<>();
    for (Dice dice : assignableDice) {
      for (Ennemi ennemi : activeEnnemis) {
        actions.add(new GameAction(GamePhase.ASSIGN_DICE, dice, ennemi));
      }
    }
    actions.add(null); // Action "ne rien assigner"
    return actions;
  }

  public List<GameAction> createPossibleUseActions(List<Item> usableItems, GameState gameState) {
    List<GameAction> actions = new ArrayList<>();
    for (Item item : usableItems) {
      actions.add(new GameAction(gameState.getPhase(), item));
    }
    actions.add(null); // Action "ne rien utiliser"
    return actions;
  }

  public List<GameAction> createPossibleRemoveItemActions(List<Item> items) {
    List<GameAction> actions = new ArrayList<>();
    actions.add(new GameAction(GamePhase.THROW_ITEM, (Item) null));
    items.forEach(item -> actions.add(new GameAction(GamePhase.THROW_ITEM, item)));
    return actions;

  }

}
