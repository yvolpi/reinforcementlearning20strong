package model.effets.ennemi;

import model.GameState;

public class PorteSporeExpectorantEffect implements EnnemyEffect {
  private boolean activated = true;

  @Override
  public EnnemyEffectType getType() {
    return EnnemyEffectType.PERMANENT;
  }

  @Override
  public void applyBeforeEngagement(GameState gameState) {
    int maxEngagedDicePerTurn = 8;
    gameState.setMaxEngagedDicePerTurn(Math.min(maxEngagedDicePerTurn, gameState.getMaxEngagedDicePerTurn()));
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
