package model.effets.bonus;

import model.GameState;
import model.ennemis.Ennemi;

public interface BonusEffect {

  default String getName() {
    return this.getClass().getSimpleName();
  }

  default int getBonusDamage(GameState gameState, Ennemi ennemi) {
    // Default implementation does nothing
    return 0;
  }

  default void recoverDice(GameState gameState) {
    // Default implementation does nothing
  }

}
