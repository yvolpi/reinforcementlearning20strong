package builders;

import java.util.ArrayList;
import java.util.List;
import model.Dice;
import model.GameState;

public class GameStateBuilder {
  private List<Dice> exhaustedDices = new ArrayList<>();
  private List<Dice> dicePool = new ArrayList<>();

  public GameStateBuilder() {
  }

  public GameStateBuilder withDicePool(List<Dice> dicePool) {
    this.dicePool = dicePool;
    return this;
  }


  public GameStateBuilder withExhaustedDices(List<Dice> exhaustedDices) {
    this.exhaustedDices = exhaustedDices;
    return this;
  }


  public GameState build() {
    GameState gameState = new GameState(null, dicePool, null, null, null, null, null, null);

    exhaustedDices.forEach(dice -> {
      gameState.getExhaustedDice().add(dice);
    });

    return gameState;
  }
}
