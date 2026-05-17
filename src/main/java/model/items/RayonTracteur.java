package model.items;

import java.util.List;
import model.Dice;
import model.DiceState;

public class RayonTracteur extends Item {
  int amountDiceToRecover;

  public RayonTracteur(int amountDiceToRecover) {
    this.amountDiceToRecover = amountDiceToRecover;
  }

  @Override
  public String getName() {
    return "RayonTracteur";
  }

  @Override
  public void use(model.Player player, model.GameState gameState) {
    // Récupère les dés les plus forts
    List<Dice> diceToRecover = gameState.getExhaustedDice().stream()
        .sorted((d1, d2) -> Integer.compare(d2.getStrengthRanking(), d1.getStrengthRanking()))
        .limit(amountDiceToRecover)
        .toList();

    for (Dice dice : diceToRecover) {
      gameState.getExhaustedDice().remove(dice);
      dice.setState(DiceState.RESERVE);
      gameState.getDicePool().add(dice);

    }
  }

}
