package model.effets.bonus;

import model.GameState;
import model.ennemis.Ennemi;

public interface BonusEffect {

  public default String getName() {
    return this.getClass().getSimpleName();
  }

  public default int getBonusDamage(GameState gameState, Ennemi ennemi) {
    // Default implementation does nothing
    return 0;
  }

}
