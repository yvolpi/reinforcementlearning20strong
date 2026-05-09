package model.recompenses;

import model.GameState;
import model.Player;
import model.items.SushiSpatial;

public class EnsanglanteuseReward implements Reward {

  @Override
  public void apply(GameState gameState) {
    Player player = gameState.getPlayer();
    if (player.getItems().size() < player.getStrategy()) {
      player.addItem(new SushiSpatial());
    }
  }

  @Override
  public RewardType getType() {
    return RewardType.CONSUMABLE;
  }
}
