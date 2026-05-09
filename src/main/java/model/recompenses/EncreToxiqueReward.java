package model.recompenses;

import model.GameState;

public class EncreToxiqueReward implements Reward {

  @Override
  public void apply(GameState gameState) {
    var player = gameState.getPlayer();
    if (player.getItems().size() <= player.getStrategy()) {
      player.getItems().add(new model.items.EncreToxique());
    }
  }

  @Override
  public RewardType getType() {
    return RewardType.CONSUMABLE;
  }

}
