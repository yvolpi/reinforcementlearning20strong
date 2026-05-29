package model.items;

import model.GameState;
import model.Player;
import model.effets.bonus.IchorVeriteEffect;
import model.elements.GamePhase;

public class IchorVerite extends Item {

  @Override
  public boolean canBeUsed(Player player, GameState gameState) {
    return gameState.getPhase() == GamePhase.USE_ITEM_BEFORE_ENGAGE;
  }

  @Override
  public boolean isConsummable() {
    return false;
  }

  @Override
  public String getName() {
    return "IchorVerite";
  }

  @Override
  public void use(Player player, GameState gameState) {
    gameState.addBonusEffectTurn(new IchorVeriteEffect());
  }
}
