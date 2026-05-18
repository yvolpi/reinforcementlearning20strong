package model.recompenses;

import model.Player;
import model.items.BouteilleDEau;

public class BouteilleDEauReward implements Reward {
  @Override
  public void apply(model.GameState gameState) {
    Player player = gameState.getPlayer();
    if (player.getItems().size() < player.getStrategy()) {
      player.addItem(new BouteilleDEau());
    }
  }

  @Override
  public RewardType getType() {
    return RewardType.CONSUMABLE;
  }
}
