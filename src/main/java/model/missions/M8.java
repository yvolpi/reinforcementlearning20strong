package model.missions;

import model.GameState;

public class M8 extends Mission {

  public M8() {
    super();
    this.canBeAbandoned = true;
  }

  @Override
  public String getName() {
    return "Tenir bon à tout prix";
  }

  @Override
  public String getDescription() {
    return "Activez au moins 3 ennemis pendant une même activation";
  }

  public void afterActivation(GameState gameState) {
    if (gameState.getActiveEnnemis().size() >= 3) {
      setSuccess(true);
      gameState.setAvancementMissions(gameState.getAvancementMissions() + 1);
    }
  }
}
