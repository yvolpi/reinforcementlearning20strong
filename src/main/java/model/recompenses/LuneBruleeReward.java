package model.recompenses;

import model.Player;
import model.items.LuneBrulee;

public class LuneBruleeReward implements Reward {
  @Override
  public void apply(model.GameState gameState) {
    Player player = gameState.getPlayer();
    if (player.getItems().size() < player.getStrategy()) {
      player.addItem(new LuneBrulee());
      if (player.getRecovery() > 1) {
        player.setRecovery(player.getRecovery() - 1);
      }
    }
  }

  @Override
  public RewardType getType() {
    return RewardType.PERMANENT;
  }
}
