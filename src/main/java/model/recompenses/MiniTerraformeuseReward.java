package model.recompenses;

import model.Player;
import model.items.MiniTerraformeuse;

public class MiniTerraformeuseReward implements Reward {
  @Override
  public void apply(model.GameState gameState) {
    Player player = gameState.getPlayer();
    if (player.getItems().size() < player.getStrategy()) {
      player.addItem(new MiniTerraformeuse());
    }
  }

  @Override
  public RewardType getType() {
    return RewardType.PERMANENT;
  }
}
