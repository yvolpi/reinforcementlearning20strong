package model.items;

import model.GameState;
import model.Player;
import model.elements.GamePhase;

/**
 Au début de chaque tour, si le joueur a moins de 4 pv, il regagne 1 pv.
 */
public class Pizza extends Item {

  @Override
  public String getName() {
    return "Pizza";
  }

  @Override
  public boolean canBeUsed(model.Player player, model.GameState gameState) {
    // Son bonus est automatique
    return false;
  }

  @Override
  public boolean isConsummable() {
    return false;
  }

  @Override
  public void use(Player player, GameState gameState) {
  }

  public void triggeredBeforeActivationPhase(GameState gameState) {
    Player player = gameState.getPlayer();
    if(player.getLife() < 4) {
      player.gainLife(1);
    }
  }
}
