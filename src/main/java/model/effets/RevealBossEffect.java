package model.effets;

import model.GameState;
import model.Player;

public class RevealBossEffect implements EnnemyEffect {

  @Override
  public EnnemyEffectType getType() {
    return EnnemyEffectType.SUBSEQUENT;
  }

  @Override
  public void apply(Player player, GameState gameState) {
    if (gameState.isRevealedBoss() && gameState.getBossPile().size() > 1) {
      // Enlever le premier boss de la pile
      gameState.getBossPile().remove();
    }
    gameState.setRevealedBoss(true);

  }
}
