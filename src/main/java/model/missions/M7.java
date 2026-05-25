package model.missions;

import model.GameState;

public class M7 extends Mission {
  private int count;

  public M7() {
    super();
    this.count = 0;
  }

  @Override
  public String getName() {
    return "Tactique d intimidation";
  }

  @Override
  public String getDescription() {
    return "Défaussez un ennemi non obligatoire supplémentaire 3 fois.";
  }

  @Override
  public void onExtraDropEnnemi(GameState gameState) {
    count ++;
    gameState.setAvancementMissions(gameState.getAvancementMissions() + 1);
    if (count >= 3) {
      setSuccess(true);
    }
  }
}
