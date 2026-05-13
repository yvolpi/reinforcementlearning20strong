package model.effets.ennemi;

import model.Dice;
import model.GameState;
import model.Player;
import model.ennemis.Ennemi;

public class BanishDiceIfNotKilledEffect implements EnnemyEffect {
  private boolean activated = true;

  @Override
  public EnnemyEffectType getType() {
    return EnnemyEffectType.SUBSEQUENT;
  }

  @Override
  public void apply(Player player, GameState gameState, Ennemi ennemi) {
    if (!ennemi.isDefeatedFlag() && !gameState.getEngagedDices().isEmpty()) {
      // Le plus fort dé de la réserve est banni si l'ennemi n'est pas vaincu
      // utiliser strengthRanking pour trouver le plus fort dé de la réserve
      Dice strongestDice = null;
      for (Dice dice : gameState.getEngagedDices()) {
        if (strongestDice == null || dice.getStrengthRanking() > strongestDice.getStrengthRanking()) {
          strongestDice = dice;
        }
      }
      if (strongestDice != null) {
        gameState.getEngagedDices().remove(strongestDice);
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
