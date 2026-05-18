package model.recompenses;

import java.util.Comparator;
import java.util.List;
import model.Dice;
import model.DiceState;

public class RecoverDicesAndLifeReward implements Reward {
  int amount;

  public RecoverDicesAndLifeReward(int amount) {
    this.amount = amount;
  }

  @Override
  public void apply(model.GameState gameState) {
    model.Player player = gameState.getPlayer();
    // gain de vie
    player.gainLife(amount);

    // dés à récupérer (priorité aux dés les plus forts)

    List<Dice> sortedDice = gameState.getExhaustedDice().stream()
        .sorted(Comparator.comparingInt(Dice::getStrengthRanking))
        .limit(amount)
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
