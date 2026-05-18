package model.effets.ennemi;

import model.DiceColor;
import model.GameState;
import model.Player;
import model.ennemis.Ennemi;

public class RemoveFirstEnnemisIfRedOrPurpleAssignedEffect implements EnnemyEffect {
  private boolean activated = true;

  @Override
  public EnnemyEffectType getType() {
    return EnnemyEffectType.SUBSEQUENT;
  }

  @Override
  public void apply(Player player, GameState gameState, Ennemi ennemi) {
    // vérifier s'il y a un dé rouge ou violet assigné
    boolean hasRedOrPurpleAssignedDice = ennemi.getAssignedDice().stream()
        .anyMatch(dice -> dice.getColor() == DiceColor.ROUGE || dice.getColor() == DiceColor.VIOLET);

    if (hasRedOrPurpleAssignedDice) {
      // retirer le premier ennemi de chaque pile
      if (!gameState.getPile1().isEmpty()) {
        gameState.getPile1().remove();
      }
      if (!gameState.getPile2().isEmpty()) {
        gameState.getPile2().remove();
      }
      if (!gameState.getPile3().isEmpty()) {
        gameState.getPile3().remove();
      }
    }
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
