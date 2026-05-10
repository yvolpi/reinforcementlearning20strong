package model.recompenses;

import model.GameState;
import model.Player;
import model.items.IchorVerite;
import model.items.MegaEpinephrine;

public class IchorVeriteReward implements Reward {

  @Override
  public void apply(GameState gameState) {
    Player player = gameState.getPlayer();
    if (player.getItems().size() < player.getStrategy()) {
      player.addItem(new IchorVerite());
    }
  }

  @Override
  public RewardType getType() {
    return RewardType.PERMANENT;
  }
}
