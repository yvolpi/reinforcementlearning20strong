package model.effets.ennemi;

public class MaxAssignDiceEffect implements EnnemyEffect {
  private boolean activated = true;
  private final int maxDice;

  public MaxAssignDiceEffect(int maxDice) {
    this.maxDice = maxDice;
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
