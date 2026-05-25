package model.missions;

import model.GameState;

public class M1 extends Mission {
  private int count;

  public M1() {
    super();
    this.count = 0;
  }

  @Override
  public String getName() {
    return "Dans la gueule du loup";
  }

  @Override
  public String getDescription() {
    return "Subissez au moins 1 dégat par tour 3 fois.";
  }

  @Override
  public void onDamageTaken(GameState gameState) {
    count++;
    gameState.setAvancementMissions(gameState.getAvancementMissions() + 1);
    if (count >= 3) {
      setSuccess(true);
    }
  }
}
