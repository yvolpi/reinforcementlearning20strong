package model.recompenses;

import model.GameState;
import model.Player;
import model.items.JusInsecte;

public class JusInsecteReward implements Reward {

  @Override
  public void apply(GameState gameState) {
    Player player = gameState.getPlayer();
    if (player.getItems().size() < player.getStrategy()) {
      player.addItem(new JusInsecte());
    }
  }

  @Override
  public RewardType getType() {
    return RewardType.CONSUMABLE;
  }
}
