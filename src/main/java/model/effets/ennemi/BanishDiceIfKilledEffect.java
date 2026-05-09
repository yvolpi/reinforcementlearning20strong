package model.effets.ennemi;

import model.GameState;
import model.Player;
import model.ennemis.Ennemi;

public class BanishDiceIfKilledEffect implements EnnemyEffect {


  @Override
  public EnnemyEffectType getType() {
    return EnnemyEffectType.SUBSEQUENT;
  }

  @Override
  public void apply(Player player, GameState gameState, Ennemi ennemi) {
    if (ennemi.isDefeatedFlag()) {
      // Les dés assignés sont bannis
      gameState.getEngagedDices().removeAll(ennemi.getAssignedDice());
    }

  }
}
