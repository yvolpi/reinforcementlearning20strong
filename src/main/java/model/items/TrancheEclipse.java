package model.items;

import model.Dice;
import model.DiceColor;
import model.DiceState;
import model.GameState;
import model.Player;
import model.effets.bonus.TrancheEclipseEffect;
import model.elements.GamePhase;

public class TrancheEclipse extends Item {

  @Override
  public String getName() {
    return "Tranche Eclipse";
  }

  @Override
  public boolean canBeUsed(Player player, GameState gameState) {
    return gameState.getPhase() == GamePhase.USE_ITEM_BEFORE_ENGAGE
        && gameState.getDicePool().stream().anyMatch(d -> d.getColor() != DiceColor.JAUNE);
  }

  @Override
  public boolean isConsummable() {
    return false;
  }

  @Override
  public void use(Player player, GameState gameState) {
    // le joueur épuise le plus faible dé de sa réserve excepté le dé jaune
    // conséquense : les touches normales jaunes font +1 dégat
    gameState.getDicePool().stream()
        .filter(d -> d.getColor() != DiceColor.JAUNE)
        .min((d1, d2) -> Integer.compare(d1.getStrengthRanking(), d2.getStrengthRanking()))
        .ifPresent(dice -> {
          gameState.getDicePool().remove(dice);
          dice.setState(DiceState.EPUISE);
          gameState.getExhaustedDice().add(dice);
          gameState.getBonusEffectsTurn().add(new TrancheEclipseEffect());
        });
    markAsUsed();
  }
}
