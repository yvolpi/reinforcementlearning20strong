package model.items;

import java.util.List;
import model.Dice;
import model.DiceColor;
import model.GameState;
import model.Player;
import model.elements.GamePhase;

public class Keratine extends Item {
  private final int amount;

  public Keratine(int amount) {
    this.amount = amount;
  }

  @Override
  public String getName() {
    return "Keratine" + amount;
  }

  @Override
  public boolean canBeUsed(Player player, GameState gameState) {
    // uniquement s'il y a au moins 2 échecs bleus
    return gameState.getPhase() == GamePhase.USE_ITEM_BEFORE_ASSIGN
        && gameState.getEngagedDices().stream()
        .filter(dice -> dice.getColor() == DiceColor.BLEU && dice.getLastRoll() == 0)
        .count() >= 2;
  }

  @Override
  public void use(Player player, GameState gameState) {
    // épuiser 2 dés bleus qui ont donné un échec
    List<Dice> diceToExhaust = gameState.getEngagedDices().stream()
        .filter(dice -> dice.getColor() == DiceColor.BLEU && dice.getLastRoll() == 0)
        .limit(2)
        .toList();

    for (Dice dice : diceToExhaust) {
      dice.setState(model.DiceState.EPUISE);
      gameState.getEngagedDices().remove(dice);
      gameState.getExhaustedDice().add(dice);
    }

    //La récupération passe à amount
    if (player.getRecovery() < amount) {
      player.setRecovery(amount);
    }

    // le joueur récupère jusqu'à amount dés épuisés (priorité aux dés les plus forts)

    List<Dice> diceToRecover = gameState.getExhaustedDice().stream()
        .sorted((d1, d2) -> Integer.compare(d2.getStrengthRanking(), d1.getStrengthRanking()))
        .limit(amount)
        .toList();

    for (Dice dice : diceToRecover) {
      dice.setState(model.DiceState.RESERVE);
      gameState.getExhaustedDice().remove(dice);
      gameState.getDicePool().add(dice);
    }

    player.getItems().remove(this);

  }
}
