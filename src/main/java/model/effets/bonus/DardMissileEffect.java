package model.effets.bonus;

import model.Dice;
import model.DiceColor;
import model.GameState;
import model.ennemis.Ennemi;

public class DardMissileEffect implements BonusEffect {

  public int getBonusDamage(GameState gameState, Ennemi ennemi) {
    // +1 par touche normale bleu s'il y a au moins 8 dés épuisés
    if (gameState.getExhaustedDice().size() < 8) {
      return 0;
    }

    int bonusDamage = 0;
    for (Dice dice : ennemi.getAssignedDice()) {
      if (dice.getColor() == DiceColor.BLEU && dice.isNormalHit()) {
        bonusDamage++;
      }
    }
    return bonusDamage;
  }

}
