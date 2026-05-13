package model.effets.ennemi;

import model.Dice;
import model.DiceColor;
import model.GameState;
import model.Player;
import model.ennemis.Ennemi;

public class ForbidGreenDiceToEngageEffect implements EnnemyEffect {
  private boolean activated = true;

  @Override
  public EnnemyEffectType getType() {
    return EnnemyEffectType.PERMANENT;
  }

  @Override
  public void apply(Player player, GameState gameState, Ennemi ennemi) {
    // Pas d'effet direct, la contrainte s'applique lors de l'engagement des dés
  }

  // Méthode utilitaire pour la logique d'engagement
  @Override
  public boolean canEngage(Dice dice) {
    return dice.getColor() != DiceColor.VERT;
  }

  @Override
  public boolean isActivated() {
    return activated;
  }

  @Override
  public void desactivate() {
    activated = false;
  }
}
