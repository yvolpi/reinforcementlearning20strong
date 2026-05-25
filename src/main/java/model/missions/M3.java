package model.missions;

import model.GameState;

public class M3 extends Mission {

  public M3() {
    super();
    this.canBeAbandoned = true;

  }

  @Override
  public String getName() {
    return "Face cachée de la lune";
  }

  @Override
  public String getDescription() {
    return "Finissez le tour avec 1 pv.";
  }

  @Override
  public void onDamageTaken(GameState gameState) {
    if (gameState.getPlayer().getLife() == 1) {
      gameState.setAvancementMissions(gameState.getAvancementMissions() + 1);
      setSuccess(true);
    }
  }
}
