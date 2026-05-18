package model.recompenses;

import model.GameState;
import model.Player;
import model.items.SacrificeSolaire;

public class SacrificeSolaireReward implements Reward {

  @Override
  public void apply(GameState gameState) {
    Player player = gameState.getPlayer();
    if (player.getItems().size() < player.getStrategy()) {
      player.addItem(new SacrificeSolaire());
    }
  }

  @Override
  public RewardType getType() {
    return RewardType.PERMANENT;
  }
}
