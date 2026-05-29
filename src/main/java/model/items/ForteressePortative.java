package model.items;

import model.Dice;

public class ForteressePortative extends Item {
  @Override
  public String getName() {
    return "Forteresse Portative";
  }

  @Override
  public boolean isConsummable() {
    return false;
  }

  @Override
  public void use(model.Player player, model.GameState gameState) {
    // le joueur récupère tous ses dés épuisés
    for (Dice dice : gameState.getExhaustedDice()) {
      dice.setState(model.DiceState.RESERVE);
      gameState.getDicePool().add(dice);
    }
    gameState.getExhaustedDice().clear();
  }

}
