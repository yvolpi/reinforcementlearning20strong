package model.items;

import java.util.List;
import model.Dice;
import model.GameState;
import model.Player;

public abstract class Item {
  boolean isUsedThisTurn;

  public abstract String getName();

  public boolean isUsed() {
    return isUsedThisTurn;
  }

  public void markAsUsed() {
    this.isUsedThisTurn = true;
  }

  public void resetUsage() {
    this.isUsedThisTurn = false;
  }

  public abstract void use(Player player, GameState gameState);

  public void use(List<Dice> selectedDice) {
    // Par défaut, ne fait rien avec les dés sélectionnés
  }

  public boolean canBeUsed(Player player, GameState gameState) {
    return true; // Par défaut, l'objet peut être utilisé
  }

  public List<List<Dice>> availableDiceSelections(GameState gameState) {
    return List.of(); // Par défaut, aucun dé à choisir
  }

  public void triggeredBeforeActivationPhase(GameState gameState) {
    // Par défaut, ne fait rien avant la phase d'activation
  }

}
