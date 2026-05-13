package model.recompenses;

import model.Player;

public class AntenneDeSourcierReward implements Reward {
  @Override
  public void apply(model.GameState gameState) {
    Player player = gameState.getPlayer();
    if (player.getItems().size() < player.getStrategy()) {
      player.addItem(new model.items.AntenneDeSourcier());
    }
  }

  @Override
  public RewardType getType() {
    return RewardType.CONSUMABLE;
  }

}
