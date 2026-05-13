package model.recompenses;

import model.Player;
import model.items.Replicateur;

public class ReplicateurReward implements Reward {

  @Override
  public void apply(model.GameState gameState) {
    // No specific action needed for this reward
    Player player = gameState.getPlayer();
    if (player.getItems().size() < player.getStrategy()) {
      player.addItem(new Replicateur());
    }
  }

  @Override
  public RewardType getType() {
    return RewardType.CONSUMABLE;
  }
}
