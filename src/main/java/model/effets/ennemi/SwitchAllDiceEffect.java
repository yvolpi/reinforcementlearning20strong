package model.effets.ennemi;

import model.Dice;
import model.DiceColor;
import model.GameState;
import model.ennemis.Ennemi;

public class SwitchAllDiceEffect implements EnnemyEffect {
  private boolean activated = true;

  @Override
  public EnnemyEffectType getType() {
    return EnnemyEffectType.PERMANENT;
  }

  @Override
  public void applyAfterEngagementAndRoll(GameState gameState, Ennemi ennemi) {
    gameState.getEngagedDices().forEach(dice -> {
      if (dice.getColor() != DiceColor.ROUGE) {
        switchDiceResult(dice);
      }
    });
  }

  @Override
  public boolean isActivated() {
    return activated;
  }

  @Override
  public void desactivate() {
    activated = false;
  }

  private void switchDiceResult(Dice dice) {
    int lastRoll = dice.getLastRoll();
    if (lastRoll == 0) {
      dice.setToHit();
    } else {
      dice.setToFail();
    }

  }
}
