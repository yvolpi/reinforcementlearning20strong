package model.items;

import model.GameState;
import model.Player;
import model.elements.GamePhase;

public class JusInsecte extends Item {

  @Override
  public String getName() {
    return "Jus Insecte";
  }

  @Override
  public boolean canBeUsed(Player player, GameState gameState) {
    // Le Jus Insecte peut être utilisé à tout moment
    return gameState.getPhase() == GamePhase.USE_ITEM_BEFORE_ENGAGE;
  }

  @Override
  public void use(Player player, GameState gameState) {
    player.gainLife(gameState.getActiveEnnemis().size()); // Restaure des points de vie égaux au nombre d'ennemis actifs

  }
}
