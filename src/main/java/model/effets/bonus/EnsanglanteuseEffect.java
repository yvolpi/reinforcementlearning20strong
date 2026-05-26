package model.effets.bonus;

import model.Dice;
import model.DiceColor;
import model.GameState;
import model.ennemis.Ennemi;

public class EnsanglanteuseEffect implements BonusEffect {

  @Override
  public int getBonusDamage(GameState gameState, Ennemi ennemi) {
    // Applique +2 aux touches des dés rouges et +1 aux touches critiques des dés rouges
    int bonusDamage = 0;
    for (Dice dice : ennemi.getAssignedDice()) {
      if (dice.getColor() == DiceColor.ROUGE) {
        if (dice.isCriticHit()) {
          bonusDamage++; // touche critique +1
        } else {
          bonusDamage += 2; // touche normale +2
        }
      }
    }
    return bonusDamage;
  }

}
