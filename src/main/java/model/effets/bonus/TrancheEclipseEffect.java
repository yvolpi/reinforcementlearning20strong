package model.effets.bonus;

import model.Dice;
import model.DiceColor;
import model.GameState;
import model.ennemis.Ennemi;

public class TrancheEclipseEffect implements BonusEffect {

  public TrancheEclipseEffect() {
  }

  public int getBonusDamage(GameState gameState, Ennemi ennemi) {
    // +1 par touche normale jaune
    int bonusDamage = 0;
    for (Dice dice : ennemi.getAssignedDice()) {
      if (dice.getColor() == DiceColor.JAUNE && dice.isNormalHit()) {
        bonusDamage++;
      }
    }
    return bonusDamage;
  }
}
