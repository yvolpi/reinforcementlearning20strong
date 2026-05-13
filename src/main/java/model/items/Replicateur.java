package model.items;

import model.GameState;
import model.Player;

public class Replicateur extends Item {

  @Override
  public String getName() {
    return "Replicateur";
  }

  @Override
  public boolean canBeUsed(Player player, GameState gameState) {
    // seulement quand le joueur recevra une récompense INSTANT pour la dupliquer
    return false;
  }

  @Override
  public void use(Player player, GameState gameState) {

  }
}
