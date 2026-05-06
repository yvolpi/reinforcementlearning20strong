package model.effets;

import model.GameState;
import model.Player;

public class KeepDiceInExhaustEffect implements EnnemyEffect {

  public KeepDiceInExhaustEffect() {
  }

  @Override
  public EnnemyEffectType getType() {
    return EnnemyEffectType.PERMANENT;
  }

  @Override
  public void apply(Player player, GameState gameState) {

  }

}
