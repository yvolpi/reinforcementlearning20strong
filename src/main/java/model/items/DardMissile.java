package model.items;

import model.GameState;
import model.Player;
import model.effets.bonus.DardMissileEffect;

public class DardMissile extends Item {

  @Override
  public String getName() {
    return "DardMissile";
  }

  @Override
  public boolean isConsummable() {
    return false;
  }

  @Override
  public void use(Player player, GameState gameState) {

  }

  public void triggeredBeforeActivationPhase(GameState gameState) {
    gameState.getBonusEffectsTurn().add(new DardMissileEffect());
  }
}
