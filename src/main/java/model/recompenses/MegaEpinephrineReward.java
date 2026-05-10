package model.recompenses;

import model.Player;
import model.items.MegaEpinephrine;

public class MegaEpinephrineReward implements Reward {

  @Override
  public void apply(model.GameState gameState) {
    Player player = gameState.getPlayer();
    if (player.getItems().size() < player.getStrategy()) {
      player.addItem(new MegaEpinephrine());
    }
  }

  @Override
  public RewardType getType() {
    return RewardType.PERMANENT;
  }
}
