package model.effets.ennemi;

public class ForbidPurpleDiceToAssignEffect implements EnnemyEffect {
  private boolean activated = true;

  @Override
  public EnnemyEffectType getType() {
    return EnnemyEffectType.PERMANENT;
  }

  @Override
  public boolean isDiceAssignable(model.GameState gameState, model.Dice dice) {
    // Les dés violets ne peuvent pas être assignés
    if (dice.getColor() == model.DiceColor.VIOLET) {
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
