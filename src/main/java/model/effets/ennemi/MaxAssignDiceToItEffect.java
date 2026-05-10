package model.effets.ennemi;

import model.Dice;
import model.GameState;
import model.ennemis.Ennemi;

public class MaxAssignDiceToItEffect implements EnnemyEffect {
  private boolean activated = true;
  private final int maxDice;

  public MaxAssignDiceToItEffect(int maxDice) {
    this.maxDice = maxDice;
  }

  @Override
  public boolean canAssignDiceToThisEnnemi(GameState gameState, Dice dice, Ennemi ennemi) {
    return ennemi.getAssignedDice().size() < maxDice;
  }

  @Override
  public EnnemyEffectType getType() {
    return EnnemyEffectType.PERMANENT;
  }

  @Override
  public boolean isActivated() {
    return activated;
  }

  @Override
  public void desactivate() {
    activated = false;
  }

  public int getMaxDice() {
    return maxDice;
  }
}
