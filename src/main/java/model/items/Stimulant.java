package model.items;

import model.GameState;
import model.Player;

public class Stimulant extends Item {
  private final int nbPvsGain;

  public Stimulant(int nbPvsGain) {
    this.nbPvsGain = nbPvsGain;
  }

  @Override
  public String getName() {
    return "Stimulant" + nbPvsGain;
  }

  @Override
  public void use(Player player, GameState gameState) {

    player.gainLife(nbPvsGain);
    player.removeItem(this);
  }
}
