package model.recompenses;

import model.Player;
import model.items.FusilBlaster;

public class FusilBlasterReward implements Reward {

  @Override
  public void apply(model.GameState gameState) {
    Player player = gameState.getPlayer();
    if (player.getItems().size() < player.getStrategy()) {
      player.addItem(new FusilBlaster());
    }
  }

  @Override
  public RewardType getType() {
    return RewardType.CONSUMABLE; // ou une autre catégorie selon le design du jeu
  }
}
