package model.effets.ennemi;

public class MaxEngagedDicePerEngageEffect implements EnnemyEffect {
  private final int maxEngagedDicePerEngage;
  private boolean activated = true;

  public MaxEngagedDicePerEngageEffect(int maxEngagedDicePerEngage) {
    this.maxEngagedDicePerEngage = maxEngagedDicePerEngage;
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

  public int getMaxEngagedDicePerEngage() {
    return maxEngagedDicePerEngage;
  }
}
