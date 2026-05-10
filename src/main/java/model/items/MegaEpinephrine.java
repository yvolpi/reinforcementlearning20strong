package model.items;

import java.util.Comparator;
import model.Dice;
import model.DiceColor;
import model.GameState;
import model.Player;

public class MegaEpinephrine extends Item {

  @Override
  public String getName() {
    return "MegaEpinephrine";
  }

  public boolean canBeUsed(Player player, GameState gameState) {
    // si parmi les dés engagés on a au moins 3 couleurs différente et s'il y a au moins 1 échec
    long distinctColors = gameState.getEngagedDices().stream()
        .filter(d -> d.getState() == model.DiceState.ENGAGE)
        .map(Dice::getColor)
        .distinct()
        .count();
    long failedEngagedDiceCount = gameState.getEngagedDices().stream()
        .filter(d -> d.getState() == model.DiceState.ENGAGE && d.getLastRoll() == 0)
        .count();
    return distinctColors >= 3 && failedEngagedDiceCount >= 1;
  }

  @Override
  public void use(Player player, GameState gameState) {
    // Transforme un dé en échec en touche (le plus faible en priorité
    Dice failedDice = gameState.getEngagedDices().stream()
        .filter(d -> d.getState() == model.DiceState.ENGAGE && d.getLastRoll() == 0)
        .min(Comparator.comparingInt((Dice d) -> getDiceColorPriority(d.getColor())))
        .orElseThrow(() -> new IllegalStateException("Aucun dé en échec trouvé"));
    failedDice.setToHit();
  }

  private int getDiceColorPriority(DiceColor color) {
    return switch (color) {
      case JAUNE  -> 1;
      case VERT   -> 2;
      case BLEU   -> 3;
      case VIOLET -> 4;
      case ROUGE  -> 5;
    };
  }
}
