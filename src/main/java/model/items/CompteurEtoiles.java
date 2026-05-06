package model.items;

import java.util.Comparator;
import java.util.List;
import model.Dice;
import model.DiceColor;
import model.DiceState;
import model.GameState;
import model.Player;

public class CompteurEtoiles extends Item {

  @Override
  public String getName() {
    return "CompteurEtoiles";
  }

  @Override
  public void use(Player player, GameState gameState) {
    List<Dice> engagedDiceWithFail = gameState.getEngagedDices().stream()
        .filter(d -> d.getState() == DiceState.ENGAGE && d.getLastRoll() == 0)
        .toList();

    List<Dice> engagedDiceWithHit = gameState.getEngagedDices().stream()
        .filter(d -> d.getState() == DiceState.ENGAGE && d.getLastRoll() > 0 && d.getColor() != DiceColor.ROUGE)
        .toList();
    // On doit choisir le dé le plus fort dans engagedDiceWithHit et le dé le plus faible dans engagedDiceWithFail (JAUNE < VERT < BLEU < VIOLET)

    Dice failDice = engagedDiceWithFail.stream()
        .min(Comparator.comparingInt(d -> getDiceRank(d.getColor())))
        .orElse(null);

    Dice hitDice = engagedDiceWithHit.stream()
        .max(Comparator.comparingInt(d -> getDiceRank(d.getColor())))
        .orElse(null);

    if (failDice != null && hitDice != null) {
      // Appliquer l'effet : échanger les résultats
      failDice.setToHit();
      hitDice.setToFail();
    }

    markAsUsed();

  }


  @Override
  public boolean canBeUsed(Player player, GameState gameState) {
    if (isUsedThisTurn) return false; // L'item ne peut être utilisé qu'une seule fois par tour
    //si parmi les dés engagés non assignés, l'un d'eux fait un échec, et un autre fait une touche
    List<Dice> engagedDiceWithFail = gameState.getEngagedDices().stream()
        .filter(d -> d.getState() == DiceState.ENGAGE && d.getLastRoll() == 0)
        .toList();

    List<Dice> engagedDiceWithHit = gameState.getEngagedDices().stream()
        .filter(d -> d.getState() == DiceState.ENGAGE && d.getLastRoll() > 0 && d.getColor() != DiceColor.ROUGE)
        .toList();

    return !engagedDiceWithFail.isEmpty() && !engagedDiceWithHit.isEmpty();
  }

  @Override
  public List<List<Dice>> availableDiceSelections(GameState gameState) {
    List<Dice> engagedDiceWithFail = gameState.getEngagedDices().stream()
        .filter(d -> d.getState() == DiceState.ENGAGE && d.getLastRoll() == 0)
        .toList();

    List<Dice> engagedDiceWithHit = gameState.getEngagedDices().stream()
        .filter(d -> d.getState() == DiceState.ENGAGE && d.getLastRoll() > 0 && d.getColor() != DiceColor.ROUGE)
        .toList();
    // On doit choisir un dé de engagedDiceWithFail et un dé de engagedDiceWithHit
    List<List<Dice>> choices = new java.util.ArrayList<>();
    for (Dice failDice : engagedDiceWithFail) {
      for (Dice hitDice : engagedDiceWithHit) {
        choices.add(List.of(failDice, hitDice));
      }
    }
    return choices;
  }

  // Classement des couleurs pour la faiblesse (plus le chiffre est élevé, plus le dé est faible)
  private int getDiceRank(DiceColor color) {
    return switch (color) {
      case JAUNE  -> 1;
      case VERT   -> 2;
      case BLEU   -> 3;
      case VIOLET -> 4;
      default     -> Integer.MAX_VALUE; // ROUGE ou autre, à exclure
    };
  }
}
