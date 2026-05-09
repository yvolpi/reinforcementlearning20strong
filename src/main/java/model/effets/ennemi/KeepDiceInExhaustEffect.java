package model.effets.ennemi;

import model.GameState;
import model.Player;
import model.ennemis.Ennemi;

public class KeepDiceInExhaustEffect implements EnnemyEffect {

  public KeepDiceInExhaustEffect() {
  }

  @Override
  public EnnemyEffectType getType() {
    return EnnemyEffectType.PERMANENT;
  }

  @Override
  public void apply(Player player, GameState gameState, Ennemi ennemi) {

  }

}
