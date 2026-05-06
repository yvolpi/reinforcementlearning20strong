package model.items;

import java.util.ArrayList;
import java.util.List;
import model.Dice;
import model.DiceState;
import model.GameState;
import model.Player;

public class SushiSpatial extends Item {

  @Override
  public String getName() {
    return "SushiSpatial";
  }

  @Override
  public void use(Player player, GameState gameState) {
    //gameState.checkIfErrorBetweenPoolAndEngagedAndExhaustedDice();
    List<Dice> exhausted = new ArrayList<>(gameState.getExhaustedDice());
    List<Dice> toRecover = new ArrayList<>();
    for (Dice dice : exhausted) {
      dice.roll(gameState.getRandom()); // suppose que roll() retourne le nombre de dégâts
      if (dice.getLastRoll() >= 1) {
        toRecover.add(dice);
        if (gameState.getDicePool().contains(dice)) {
          throw new IllegalStateException("Le dé est à la fois dans le pool et dans les dés épuisés !");
        }
      }
    }
    for (Dice dice : toRecover) {
      dice.setState(DiceState.RESERVE);
      gameState.getDicePool().add(dice);
    }
    gameState.getExhaustedDice().removeAll(toRecover);
    // Optionnel : retirer SushiSpatial de l'inventaire du joueur après usage
    gameState.getPlayer().removeItem(this);
  }
}
