package model.effets.bonus;

import model.Dice;
import model.GameState;
import model.ennemis.Ennemi;

public class SacrificeEffect implements BonusEffect {
  Dice sacrificeDice;
  Ennemi targetEnnemi;

  public SacrificeEffect(Dice sacrificeDice, Ennemi targetEnnemi) {
    this.sacrificeDice = sacrificeDice;
    this.targetEnnemi = targetEnnemi;
  }

  public int getBonusDamage(GameState gameState, Ennemi ennemi) {
    if (targetEnnemi != null && targetEnnemi.equals(ennemi) && sacrificeDice != null) {
      return sacrificeDice.getStrengthRanking() + 1;
    }
    return 0;
  }

}
