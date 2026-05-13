package model.recompenses;

import model.items.FilAMassacre;

public class FilAMassacreReward implements Reward {
  @Override
  public void apply(model.GameState gameState) {
    gameState.getPlayer().addItem(new FilAMassacre());
  }

  @Override
  public RewardType getType() {
    return RewardType.PERMANENT;
  }
}
