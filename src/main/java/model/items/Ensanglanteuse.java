package model.items;

import model.GameState;
import model.Player;
import model.effets.bonus.EnsanglanteuseEffect;

public class Ensanglanteuse extends Item {

  @Override
  public String getName() {
    return "Ensanglanteuse";
  }

  @Override
  public void use(Player player, GameState gameState) {
    // pendant le tour, les touches des dés rouges font +2 et leurs touches critiques +1
    gameState.addBonusEffectTurn(new EnsanglanteuseEffect());

    gameState.getPlayer().removeItem(this);
  }
}
