package model.missions;

import model.GameState;
import model.ennemis.Ennemi;

public class M4 extends Mission {
  private int count = 0;

  public M4() {
    super();
    count = 0;
  }

  @Override
  public String getName() {
    return "La quantité avant tout";
  }

  @Override
  public String getDescription() {
    return "Vainquez 6 ennemis de classe 1";
  }

  @Override
  public void onKillEnnemi(GameState gameState, Ennemi ennemi) {
    if (ennemi.getClassValue() == 1) {
      count++;
      gameState.setAvancementMissions(gameState.getAvancementMissions() + 1);
      if (count >= 6) {
        setSuccess(true);
      }
    }
  }
}
