package model.items;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import model.Dice;
import model.DiceColor;
import model.GameState;
import model.Player;
import model.elements.GamePhase;

public class SecondeChance extends Item {

  @Override
  public String getName() {
    return "Seconde Chance";
  }

  @Override
  public boolean canBeUsed(Player player, GameState gameState) {
    // Le Second Chance peut être utilisé uniquement lorsqu'il y a 2 échecs de même couleur
    if (gameState.getPhase() != GamePhase.USE_ITEM_BEFORE_ASSIGN) {
      return false; // Ne peut être utilisé que pendant la phase d'utilisation d'objets avant l'assignation des dés
    }
    long countYellowFailures = gameState.getEngagedDices().stream()
        .filter(dice -> dice.getColor() == model.DiceColor.JAUNE && dice.getLastRoll() == 0)
        .count();
    if (countYellowFailures >= 2) {
      return true;
    }
    long countGreenFailures = gameState.getEngagedDices().stream()
        .filter(dice -> dice.getColor() == model.DiceColor.VERT && dice.getLastRoll() == 0)
        .count();
    if (countGreenFailures >= 2) {
      return true;
    }

    long countBlueFailures = gameState.getEngagedDices().stream()
        .filter(dice -> dice.getColor() == model.DiceColor.BLEU && dice.getLastRoll() == 0)
        .count();
    if (countBlueFailures >= 2) {
      return true;
    }

    long countPurpleFailures = gameState.getEngagedDices().stream()
        .filter(dice -> dice.getColor() == model.DiceColor.VIOLET && dice.getLastRoll() == 0)
        .count();
    if (countPurpleFailures >= 2) {
      return true;
    }

    long countRedFailures = gameState.getEngagedDices().stream()
        .filter(dice -> dice.getColor() == model.DiceColor.ROUGE && dice.getLastRoll() == 0)
        .count();
    return countRedFailures >= 2;
  }

  @Override
  public boolean isConsummable() {
    return false;
  }

  @Override
  public void use(Player player, GameState gameState) {

    Map<DiceColor, List<Dice>> diceFailed = new TreeMap<>();

    for (Dice dice: gameState.getEngagedDices()) {
      if (dice.getLastRoll() == 0) {
        diceFailed.computeIfAbsent(dice.getColor(), k -> new java.util.ArrayList<>()).add(dice);
      }
    }

    // parcourir les couleurs et prendre celles qui ont au moins 2 dés

    List<DiceColor> colorsToSelect = diceFailed.entrySet().stream()
        .filter(entry -> entry.getValue().size() >= 2)
        .map(Map.Entry::getKey)
        // rangé selon priorityColor
        .sorted(Comparator.comparingInt(DiceColor::getStrengthRanking))
        .toList();

    // On prend la première couleur de la liste (celle avec la plus haute priorité)
    if (!colorsToSelect.isEmpty()) {
      DiceColor selectedColor = colorsToSelect.get(0);
      List<Dice> diceToChangeToHit = diceFailed.get(selectedColor);
      // On ne transforme que 2 dés en touches réussies
      for (int i = 0; i < 2; i++) {
        diceToChangeToHit.get(i).setToHit();
      }

    } else {
      throw new IllegalStateException("La Seconde Chance ne peut être utilisée que s'il y a au moins 2 échecs de la même couleur, or aucune couleur n'a 2 échecs");
    }

    markAsUsed();
  }

  private int priorityColor(DiceColor diceColor) {
    return switch (diceColor) {
      case JAUNE -> 5;
      case VERT -> 4;
      case BLEU -> 3;
      case VIOLET -> 2;
      case ROUGE -> 1;
    };
  }


}
