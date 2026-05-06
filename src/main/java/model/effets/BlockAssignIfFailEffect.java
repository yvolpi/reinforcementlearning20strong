package model.effets;

import model.Dice;
import model.GameState;
import model.Player;

public class BlockAssignIfFailEffect implements EnnemyEffect {

  @Override
  public EnnemyEffectType getType() {
    return null;
  }

  @Override
  public void apply(Player player, GameState gameState) {

  }

  @Override
  public boolean isDiceAssignable(GameState gameState, Dice dice) {
    // false s'il existe un dé engagé en échec
    return gameState.getEngagedDices().stream()
        .noneMatch(d -> d.getLastRoll() == 0);
  }
}
