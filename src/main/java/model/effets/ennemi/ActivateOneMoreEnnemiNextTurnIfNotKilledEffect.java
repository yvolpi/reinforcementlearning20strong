package model.effets.ennemi;

import model.GameState;
import model.Player;
import model.ennemis.Ennemi;

public class ActivateOneMoreEnnemiNextTurnIfNotKilledEffect implements EnnemyEffect {
  private boolean activated = true;

  @Override
  public EnnemyEffectType getType() {
    return EnnemyEffectType.SUBSEQUENT;
  }

  public void apply(Player player, GameState gameState, Ennemi ennemi) {
    if (!ennemi.isDefeated()) {
      gameState.setActivateOneMoreEnnemiNextTurn(true);
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
