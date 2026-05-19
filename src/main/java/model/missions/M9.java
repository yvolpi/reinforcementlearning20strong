package model.missions;

import model.GameState;

public class M9 extends Mission {
  private int count = 0;

  public M9() {
    super();
    count = 0;
  }

  @Override
  public String getName() {
    return "Test sur le terrain";
  }

  @Override
  public String getDescription() {
    return "Utilisez 3 objets";
  }

  @Override
  public void onUseItem(GameState gameState) {
    count++;
    if (count >= 3) {
      setSuccess(true);
    }
  }
}
