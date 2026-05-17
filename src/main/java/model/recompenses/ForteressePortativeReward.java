package model.recompenses;

import model.Player;
import model.items.ForteressePortative;

public class ForteressePortativeReward implements Reward {
  @Override
  public void apply(model.GameState gameState) {
    Player player = gameState.getPlayer();
    if (player.getItems().size() < player.getStrategy()) {
      player.addItem(new ForteressePortative());
    }
  }

  @Override
  public RewardType getType() {
    return RewardType.CONSUMABLE;
  }

}
