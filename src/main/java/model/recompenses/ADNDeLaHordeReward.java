package model.recompenses;

import model.Player;
import model.items.ADNDeLaHorde;

public class ADNDeLaHordeReward implements Reward {
  @Override
  public void apply(model.GameState gameState) {
    Player player = gameState.getPlayer();
    if (player.getItems().size() < player.getStrategy()) {
      player.addItem(new ADNDeLaHorde());
    }
  }

  @Override
  public RewardType getType() {
    return RewardType.PERMANENT;
  }
}
