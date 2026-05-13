package model.recompenses;

import model.Player;
import model.items.Stimulant;

public class StimulantReward implements Reward {
  int amountLifeGain;

  public StimulantReward(int amountLifeGain) {
    this.amountLifeGain = amountLifeGain;
  }


  @Override
  public void apply(model.GameState gameState) {
    Player player = gameState.getPlayer();
    if (player.getItems().size() < player.getStrategy()) {
      player.addItem(new Stimulant(amountLifeGain));
    }
  }

  @Override
  public RewardType getType() {
    return RewardType.CONSUMABLE;
  }
}
