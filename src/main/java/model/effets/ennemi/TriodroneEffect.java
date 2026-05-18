package model.effets.ennemi;

import model.Dice;
import model.GameState;

public class TriodroneEffect implements EnnemyEffect {
  private boolean activated = true;

  @Override
  public EnnemyEffectType getType() {
    return EnnemyEffectType.PERMANENT;
  }

  public boolean isDiceAssignable(GameState gameState, Dice dice) {
    // Avec le triodrone, un dé ne peut être assigné que s'il y a au moins un dé de la même couleur épuisé
    return gameState.getExhaustedDice().stream()
        .anyMatch(d -> d.getColor() == dice.getColor());
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
