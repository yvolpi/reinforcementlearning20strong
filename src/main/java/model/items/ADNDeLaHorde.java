package model.items;

import java.util.Comparator;
import model.Dice;
import model.DiceState;
import model.GameState;
import model.Player;

public class ADNDeLaHorde extends Item {

  @Override
  public String getName() {
    return "ADNDeLaHorde";
  }

  public boolean canBeUsed(Player player, GameState gameState) {
    // S'il y a au moins un dé engagé qui donne une touche normale et au moins un dé dans la réserve
    boolean hasEngagedNormalHit = gameState.getEngagedDices().stream()
        .anyMatch(d -> d.getState() == model.DiceState.ENGAGE && d.isNormalHit());
    boolean hasDiceInReserve = !gameState.getDicePool().isEmpty();
    return hasEngagedNormalHit && hasDiceInReserve;
  }

  @Override
  public boolean isConsummable() {
    return false;
  }


  @Override
  public void use(model.Player player, model.GameState gameState) {
    // 1. Sélectionner un dé engagé qui donne une touche normale (le plus faible en priorité
    model.Dice selectedDice = gameState.getEngagedDices().stream()
        .filter(d -> d.getState() == model.DiceState.ENGAGE && d.isNormalHit())
        .min(Comparator.comparingInt(Dice::getStrengthRanking))
        .orElseThrow(() -> new IllegalStateException("Aucun dé engagé donnant une touche normale trouvé"));

    // 2. Récupérer un dé de la réserve (le plus faible en priorité)
    model.Dice diceFromReserve = gameState.getDicePool().stream()
        .min(Comparator.comparingInt(Dice::getStrengthRanking))
        .orElseThrow(() -> new IllegalStateException("Aucun dé dans la réserve trouvé"));

    selectedDice.setToCriticalHit();
    diceFromReserve.setState(DiceState.EPUISE);
    gameState.getDicePool().remove(diceFromReserve);
    gameState.getExhaustedDice().add(diceFromReserve);

    isUsedThisTurn = true;
  }
}
