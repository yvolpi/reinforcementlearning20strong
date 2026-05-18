package model.items;

import java.util.List;
import java.util.stream.Collectors;
import model.Dice;
import model.DiceColor;
import model.GameState;
import model.Player;
import model.elements.GamePhase;

public class BouteilleDEau extends Item {

  @Override
  public String getName() {
    return "Bouteille eau";
  }

  @Override
  public boolean canBeUsed(Player player, GameState gameState) {
    // si le joueur a au moins un dé bleu épuisé
    return gameState.getPhase() == GamePhase.USE_ITEM_BEFORE_ENGAGE
        && gameState.getExhaustedDice().stream()
        .anyMatch(dice -> dice.getColor() == DiceColor.BLEU);
  }

  @Override
  public void use(Player player, GameState gameState) {
    // récupère tous les dés bleus
    List<Dice> diceToRecover = gameState.getExhaustedDice().stream()
        .filter(dice -> dice.getColor() == DiceColor.BLEU)
        .toList();

    for (Dice dice : diceToRecover) {
      dice.setState(model.DiceState.RESERVE);
      gameState.getExhaustedDice().remove(dice);
      gameState.getDicePool().add(dice);
    }

    player.getItems().remove(this);
  }
}
