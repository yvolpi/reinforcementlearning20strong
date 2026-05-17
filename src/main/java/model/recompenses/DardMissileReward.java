package model.recompenses;

import model.Player;
import model.items.DardMissile;

public class DardMissileReward implements Reward {
  @Override
  public void apply(model.GameState gameState) {
    Player player = gameState.getPlayer();
    if (player.getItems().size() < player.getStrategy()) {
      player.addItem(new DardMissile());
    }
  }

  @Override
  public RewardType getType() {
    return RewardType.PERMANENT;
  }
}
