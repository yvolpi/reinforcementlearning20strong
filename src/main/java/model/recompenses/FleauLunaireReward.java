package model.recompenses;

import model.Player;
import model.items.FleauLunaire;

public class FleauLunaireReward implements Reward {

  @Override
  public void apply(model.GameState gameState) {
    Player player = gameState.getPlayer();
    if (player.getItems().size() < player.getStrategy()) {
      player.addItem(new FleauLunaire());
    }
  }

  @Override
  public RewardType getType() {
    return RewardType.CONSUMABLE;
  }
}
