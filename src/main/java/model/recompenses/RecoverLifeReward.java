package model.recompenses;

import model.GameState;
import model.Player;

public class RecoverLifeReward implements Reward {
  private final int amount;

  public RecoverLifeReward(int amount) {
    this.amount = amount;
  }

  @Override
  public void apply(GameState gameState) {
    Player player = gameState.getPlayer();
    player.gainLife(amount);
  }

  @Override
  public RewardType getType() {
    return RewardType.INSTANT;
  }
}