package model.missions;

import model.GameState;

public class M2 extends Mission {
  private int count;

  public M2() {
    super();
    this.count = 0;
  }

  @Override
  public String getName() {
    return "Etablir une base secrète";
  }

  @Override
  public String getDescription() {
    return "Epuiser jusqu à 4 touches critiques non assignées";
  }

  public void onExhaustCriticalHit(GameState gameState) {
    count++;
    if (count >= 4) {
      setSuccess(true);
    }
  }

  public int getNumberOfCriticalHitsToExhaust() {
    return 4 - count;
  }
}
