package model.effets.ennemi;

import model.Dice;
import model.DiceColor;
import model.GameState;

public class ForbidRedAndPurpleHitsEffect implements EnnemyEffect {
  private boolean activated = true;

  @Override
  public EnnemyEffectType getType() {
    return EnnemyEffectType.PERMANENT;
  }

  public boolean isDiceAssignable(GameState gameState, Dice dice) {
    // Les touches normales rouges et violettes ne peuvent pas être assignées
    if (dice.getColor() == DiceColor.ROUGE || dice.getColor() == DiceColor.VIOLET) {
      if (dice.isNormalHit()) {
        return false;
      }
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
