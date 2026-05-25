package model.missions;

import model.GameState;

public class M12 extends Mission {
  private int count;

  public M12() {
    super();
    count = 0;
  }

  @Override
  public String getName() {
    return "Vantardise";
  }

  @Override
  public String getDescription() {
    return "Ignore 2 récompenses instantanées";
  }

  public void onIgnoreReward(GameState gameState) {
    count++;
    gameState.setAvancementMissions(gameState.getAvancementMissions() + 1);
    if (count >= 2) {
      setSuccess(true);
    }
  }
}
