package model.effets.bonus;

import model.Dice;
import model.DiceColor;
import model.GameState;
import model.ennemis.Ennemi;

public class OrloEffect implements BonusEffect {

  @Override
  public int getBonusDamage(GameState gameState, Ennemi ennemi) {
    // Applique +1 aux échecs jaunes si l'ennemi est de classe 2
    int bonusDamage = 0;
    if (ennemi.getClassValue() == 2 ) {
      for (Dice dice : ennemi.getAssignedDice()) {
        if (dice.getColor() == DiceColor.JAUNE) {
          if (dice.getLastRoll() == 0) {
            bonusDamage++; // échec +1
          }
        }
      }
    }

    return bonusDamage;
  }

}
