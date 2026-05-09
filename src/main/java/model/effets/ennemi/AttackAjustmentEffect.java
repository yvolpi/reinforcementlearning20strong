package model.effets.ennemi;

import model.GameState;
import model.Player;
import model.ennemis.Ennemi;

public class AttackAjustmentEffect implements EnnemyEffect {
  private int attackAjustment;
  private boolean activated = true;

  public AttackAjustmentEffect(int attackAjustment) {
    this.attackAjustment = attackAjustment;
  }

  public int getAttackAjustment() {
    return attackAjustment;
  }

  @Override
  public EnnemyEffectType getType() {
    return EnnemyEffectType.PERMANENT;
  }

  @Override
  public void apply(Player player, GameState gameState, Ennemi ennemi) {

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
