package recompenses;

import model.GameState;
import model.Player;

public class IncreaseRecoveryReward implements Reward {
  private int recoveryIncrease;

  public IncreaseRecoveryReward(int recoveryIncrease) {
    this.recoveryIncrease = recoveryIncrease;
  }

  @Override
  public void apply(GameState gameState) {
    Player player = gameState.getPlayer();
      player.setRecovery(player.getRecovery() + recoveryIncrease);
  }

  @Override
  public RewardType getType() {
    return RewardType.INSTANT;
  }
}
