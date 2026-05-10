package model.recompenses;

import model.GameState;

public class IncreaseStrategyRewerd implements Reward {

  @Override
  public void apply(GameState gameState) {
    gameState.getPlayer().setStrategy(gameState.getPlayer().getStrategy() + 1);
  }

  @Override
  public RewardType getType() {
    return RewardType.INSTANT;
  }
}