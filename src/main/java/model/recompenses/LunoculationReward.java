package model.recompenses;

import model.GameState;
import model.Player;
import model.items.Lunoculation;
import model.items.Pizza;

public class LunoculationReward implements Reward {


  @Override
  public void apply(GameState gameState) {
    Player player = gameState.getPlayer();
    if (player.getItems().size() < player.getStrategy()) {
      player.addItem(new Lunoculation());
    }
  }

  @Override
  public RewardType getType() {
    return RewardType.CONSUMABLE; // ou une autre catégorie selon le design du jeu
  }

}
