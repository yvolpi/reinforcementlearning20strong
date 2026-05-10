package model.effets.ennemi;

import model.Dice;
import model.DiceColor;
import model.DiceState;
import model.GameState;

public class MustAssignPairDiceEffect implements EnnemyEffect {
  private boolean activated = true;

  @Override
  public EnnemyEffectType getType() {
    return EnnemyEffectType.PERMANENT;
  }

  @Override
  public boolean isDiceAssignable(GameState gameState, Dice dice) {
    // si le dé est le seul dé de sa couleur qui donne une touche alors il ne peut pas être assigné
    int countDicesHitOfDiceColor = 0;
    DiceColor diceColor = dice.getColor();
    for (Dice d : gameState.getEngagedDices()) {
      if (d.getState() == DiceState.ENGAGE && d.getColor() == diceColor && d.getLastRoll() > 0) {
        countDicesHitOfDiceColor++;
      }
    }
    return countDicesHitOfDiceColor > 1;
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
