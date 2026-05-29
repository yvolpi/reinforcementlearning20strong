package model.recompenses;

import java.util.Comparator;
import java.util.List;
import model.Dice;
import model.DiceState;

public class RecoverDicesReward implements Reward {
  int amountDices;

  public RecoverDicesReward(int amountDices) {
    this.amountDices = amountDices;
  }

  @Override
  public void apply(model.GameState gameState) {
    // dés à récupérer (priorité aux dés les plus forts)

    List<Dice> sortedDice = gameState.getExhaustedDice().stream()
        .sorted(Comparator.comparingInt(Dice::getStrengthRanking).reversed())
        .limit(amountDices)
        .toList();

    for (Dice dice : sortedDice) {
      dice.setState(DiceState.RESERVE);
      gameState.getExhaustedDice().remove(dice);
      gameState.getDicePool().add(dice);
    }


  }

  @Override
  public RewardType getType() {
    return RewardType.INSTANT;
  }
}
