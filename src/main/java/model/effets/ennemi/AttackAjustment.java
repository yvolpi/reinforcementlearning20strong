package model.effets.ennemi;

import model.GameState;
import model.Player;
import model.ennemis.Ennemi;

public class AttackAjustment implements EnnemyEffect {
  private int attackAjustment;

  public AttackAjustment(int attackAjustment) {
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
}
