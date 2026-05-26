package model.items;

import java.util.Comparator;
import java.util.List;
import model.Dice;
import model.DiceColor;
import model.DiceState;
import model.GameState;
import model.Player;

public class FusilBlaster extends Item {

  @Override
  public String getName() {
    return "Fusil Blaster";
  }

  @Override
  public boolean canBeUsed(Player player, GameState gameState) {
    // Peut être utilisé s'il y a au moins 3 dés engagés en échec
    long failedEngagedDiceCount = gameState.getEngagedDices().stream()
        .filter(d -> d.getLastRoll() == 0)
        .count();
    return failedEngagedDiceCount >= 3;
  }

  @Override
  public void use(Player player, GameState gameState) {
    // on épuise de préférence les échecs des 3 dés les plus faibles
    List<Dice> diceToExhaust = gameState.getEngagedDices().stream()
        .filter(d -> d.getLastRoll() == 0)
        .sorted(Comparator.comparingInt(Dice::getStrengthRanking))
        .limit(3)
        .toList();

    diceToExhaust.forEach(d -> {
      d.setState(DiceState.EPUISE);
      gameState.getEngagedDices().remove(d);
      gameState.getExhaustedDice().add(d);
    });

    player.removeItem(this);
  }

}
