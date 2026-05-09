package model.recompenses;

import model.Player;
import model.items.Pizza;
import model.items.SushiSpatial;

public class PizzaReward implements Reward {

  @Override
  public void apply(model.GameState gameState) {
    Player player = gameState.getPlayer();
    if (player.getItems().size() < player.getStrategy()) {
      player.addItem(new Pizza());
    }
  }

  @Override
  public RewardType getType() {
    return RewardType.PERMANENT;
  }

}
