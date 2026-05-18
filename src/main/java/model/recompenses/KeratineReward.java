package model.recompenses;

import model.Player;
import model.items.Keratine;

public class KeratineReward implements Reward {
  private final int amount;

  public KeratineReward(int amount) {
    this.amount = amount;
  }

  @Override
  public void apply(model.GameState gameState) {
    Player player = gameState.getPlayer();
    if (player.getItems().size() < player.getStrategy()) {
      player.addItem(new Keratine(amount));
    }
  }

  @Override
  public RewardType getType() {
    return RewardType.CONSUMABLE;
  }
}
