package model.recompenses;

import model.Player;
import model.items.SecondeChance;

public class SecondeChanceReward implements Reward {
  @Override
  public void apply(model.GameState gameState) {
    Player player = gameState.getPlayer();
    if (player.getItems().size() < player.getStrategy()) {
      player.addItem(new SecondeChance());
    }
  }

  @Override
  public RewardType getType() {
    return RewardType.PERMANENT;
  }
}