package model.effets.ennemi;

import model.Dice;
import model.DiceColor;
import model.ennemis.Ennemi;

public class YellowAndGreenHitsDoOneMoreDamageEffect implements EnnemyEffect {
  private boolean activated = true;

  @Override
  public EnnemyEffectType getType() {
    return EnnemyEffectType.PERMANENT;
  }

  @Override
  public int getModifiedDamage(Ennemi ennemi, Dice dice) {
    if ((dice.getColor() == DiceColor.JAUNE || dice.getColor() == DiceColor.VERT) && dice.isNormalHit()) {
      return 1; // Les hits jaunes et verts font 1 point de dégâts supplémentaire
    }
    return 0; // Pas de modification pour les autres dés ou types de hits
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
