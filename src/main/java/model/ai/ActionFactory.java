package model.ai;

import java.util.ArrayList;
import java.util.List;
import model.Dice;
import model.GameState;
import model.effets.ennemi.EnnemyEffect;
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

  public List<GameAction> createPossibleRemoveItemActions(List<Item> items) {
    List<GameAction> actions = new ArrayList<>();
    actions.add(new GameAction(GamePhase.THROW_ITEM, (Item) null));
    items.forEach(item -> actions.add(new GameAction(GamePhase.THROW_ITEM, item)));
    return actions;
  }

  public List<GameAction> createPossibleDesactivateEffectActions(List<Ennemi> ennemis) {
    List<GameAction> actions = new ArrayList<>();
    for (Ennemi ennemi : ennemis) {
      for (EnnemyEffect effect : ennemi.getEffects()) {
        if (effect.isActivated()) {
          actions.add(new GameAction(effect, ennemi));
        }
      }
    }
    return actions;
  }

}
