package model.missions;

import model.GameState;
import model.ennemis.Ennemi;

public class M5 extends Mission {
  private int count = 0;

  public M5() {
    super();
    count = 0;
  }

  @Override
  public String getName() {
    return "Plus dure sera la chute";
  }

  @Override
  public String getDescription() {
    return "Vainquez 4 ennemis de classe 2";
  }

  @Override
  public void onKillEnnemi(GameState gameState, Ennemi ennemi) {
    if (ennemi.getClassValue() == 2) {
      count++;
      if (count >= 4) {
        setSuccess(true);
      }
    }
  }
}
