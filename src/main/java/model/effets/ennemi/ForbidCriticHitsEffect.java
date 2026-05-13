package model.effets.ennemi;

import model.Dice;
import model.GameState;

public class ForbidCriticHitsEffect implements EnnemyEffect {
  private boolean activated = true;

  @Override
  public EnnemyEffectType getType() {
    return EnnemyEffectType.PERMANENT;
  }

  @Override
  public boolean canAssignDice(GameState gameState, Dice dice) {
    if (dice.isCriticHit()) {
      return false;
    }
    return true;
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
