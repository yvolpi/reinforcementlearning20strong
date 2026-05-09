package model.effets.ennemi;

import model.GameState;
import model.Player;
import model.ennemis.Ennemi;

public class BanishFailedDiceEffect implements EnnemyEffect {
  private boolean activated = true;

  @Override
  public EnnemyEffectType getType() {
    return EnnemyEffectType.SUBSEQUENT;
  }

  public void apply(Player player, GameState gameState, Ennemi ennemi) {
    gameState.getEngagedDices().removeIf(dice -> dice.getLastRoll() == 0);
  }

  @Override
  public boolean isActivated() {
    return activated;
  }

  @Override
  public void desactivate() {
    activated = false;
  }

}
