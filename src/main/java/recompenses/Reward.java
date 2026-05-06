package recompenses;


import model.GameState;

public interface Reward {
  public default String getName() {
    return this.getClass().getSimpleName();
  }

  void apply(GameState gameState);


  RewardType getType();
  

}
