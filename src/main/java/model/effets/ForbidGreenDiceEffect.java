package model.effets;

import model.Dice;
import model.DiceColor;
import model.GameState;
import model.Player;

public class ForbidGreenDiceEffect implements EnnemyEffect {
  @Override
  public EnnemyEffectType getType() {
    return EnnemyEffectType.PERMANENT;
  }

  @Override
  public void apply(Player player, GameState gameState) {
    // Pas d'effet direct, la contrainte s'applique lors de l'engagement des dés
  }

  // Méthode utilitaire pour la logique d'engagement
  @Override
  public boolean canEngage(Dice dice) {
    return dice.getColor() != DiceColor.VERT;
  }
}
