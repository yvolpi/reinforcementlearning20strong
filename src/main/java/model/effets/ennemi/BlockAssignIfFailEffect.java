package model.effets.ennemi;

import model.Dice;
import model.GameState;
import model.Player;
import model.ennemis.Ennemi;

public class BlockAssignIfFailEffect implements EnnemyEffect {
  private boolean activated = true;

  @Override
  public EnnemyEffectType getType() {
    return EnnemyEffectType.PERMANENT;
  }

  @Override
  public void apply(Player player, GameState gameState, Ennemi ennemi) {

  }

  @Override
  public boolean isDiceAssignable(GameState gameState, Dice dice) {
    // false s'il existe un dé engagé en échec
    return gameState.getEngagedDices().stream()
        .noneMatch(d -> d.getLastRoll() == 0);
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
