package model.recompenses;

import model.GameState;
import model.Player;
import model.items.RayonTracteur;

public class RayonTracteurReward implements Reward {
  int amountDiceToRecover;

  public RayonTracteurReward(int amountDiceToRecover) {
    this.amountDiceToRecover = amountDiceToRecover;
  }


  @Override
  public void apply(GameState gameState) {
    Player player = gameState.getPlayer();
    if (player.getItems().size() < player.getStrategy()) {
      player.addItem(new RayonTracteur(amountDiceToRecover));
    }
  }

  @Override
  public RewardType getType() {
    return RewardType.CONSUMABLE;
  }
}
