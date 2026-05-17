package model.effets.bonus;

import java.util.ArrayList;
import java.util.List;
import model.Dice;
import model.DiceColor;
import model.DiceState;
import model.GameState;

public class MiniTerraformeuseEffect implements BonusEffect {
  @Override
  public void recoverDice(GameState gameState) {
    // Récupère un dé jaune et/ou un dé vert
    List<Dice> diceToRecover = new ArrayList<>();

    //dé jaune
    gameState.getExhaustedDice()        .stream()
        .filter(d -> d.getColor() == DiceColor.JAUNE)
        .findFirst()
        .ifPresent(diceToRecover::add);

    //dé vert
    gameState.getExhaustedDice()        .stream()
        .filter(d -> d.getColor() == DiceColor.VERT)
        .findFirst()
        .ifPresent(diceToRecover::add);

    for (Dice dice : diceToRecover) {
      dice.setState(DiceState.RESERVE);
      gameState.getExhaustedDice().remove(dice);
      gameState.getDicePool().add(dice);
    }
  }

}
