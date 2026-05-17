package model.recompenses;

import model.Player;
import model.items.PistoletEtourdissant;

public class PistoletEtourdissantReward implements Reward {
  @Override
  public void apply(model.GameState gameState) {
    Player player = gameState.getPlayer();
    if (player.getItems().size() < player.getStrategy()) {
      player.addItem(new PistoletEtourdissant());
    }
  }

  @Override
  public RewardType getType() {
    return RewardType.CONSUMABLE;
  }
}
