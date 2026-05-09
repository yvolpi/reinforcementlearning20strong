package model.effets.ennemi;

import model.GameState;

/**
 * Effet qui limite le nombre maximum de dés pouvant être engagés par tour.
 * Si plusieurs effets similaires sont actifs, la limite la plus basse s'applique.
 */

public class MaxEngagedDicePerTurnEffect implements EnnemyEffect {
  private final int maxEngagedDicePerTurn;
  private boolean activated = true;

  public MaxEngagedDicePerTurnEffect(int maxEngagedDicePerTurn) {
    this.maxEngagedDicePerTurn = maxEngagedDicePerTurn;
  }

  @Override
  public EnnemyEffectType getType() {
    return EnnemyEffectType.PERMANENT;
  }

  public int getMaxEngagedDicePerTurn() {
    return maxEngagedDicePerTurn;
  }

  @Override
  public void applyBeforeEngagement(GameState gameState) {
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
