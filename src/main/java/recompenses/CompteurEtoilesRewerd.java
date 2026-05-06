package recompenses;

import model.GameState;
import model.Player;
import model.items.CompteurEtoiles;

public class CompteurEtoilesRewerd implements Reward {

  @Override
  public void apply(GameState gameState) {
    Player player = gameState.getPlayer();
    if (player.getItems().size() < player.getStrategy()) {
      player.addItem(new CompteurEtoiles());
    }
  }

  @Override
  public RewardType getType() {
    return RewardType.PERMANENT;
  }
}
