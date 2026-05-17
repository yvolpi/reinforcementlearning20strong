package model.effets.ennemi;

import model.DiceState;
import model.GameState;
import model.ennemis.Ennemi;

public class LoseLifePerFailEffect implements EnnemyEffect {
  private boolean activated = true;


  @Override
  public EnnemyEffectType getType() {
    return null;
  }


  @Override
  public void applyAfterEngagementAndRoll(GameState gameState, Ennemi ennemi) {
    long nbDiceFailed = gameState.getEngagedDices()
        .stream()
        .filter(dice -> dice.getState() == DiceState.ENGAGE && dice.getLastRoll() == 0)
        .count();
    if (nbDiceFailed > 0) {
      int damage = (int) nbDiceFailed;
      gameState.getPlayer().loseLife(damage);
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
