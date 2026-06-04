package builders;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import model.Avatar;
import model.Dice;
import model.GameState;
import model.Player;
import model.random.CustomRandom;

public class GameStateBuilder {
  private List<Dice> exhaustedDices = new ArrayList<>();
  private List<Dice> dicePool = new ArrayList<>();
  private CustomRandom random = new CustomRandom(3,2,0,0);
  private Player player = new Player(Avatar.VALKYRIE, 0, 0, 0);

  public GameStateBuilder() {
  }

  public GameStateBuilder withPlayer(Player player) {
    this.player = player;
    return this;
  }

  public GameStateBuilder withDicePool(List<Dice> dicePool) {
    this.dicePool = dicePool;
    return this;
  }


  public GameStateBuilder withExhaustedDices(List<Dice> exhaustedDices) {
    this.exhaustedDices = exhaustedDices;
    return this;
  }

  public GameStateBuilder withRandom(CustomRandom random) {
    this.random = random;
    return this;
  }


  public GameState build() {
    GameState gameState = new GameState(player, dicePool, null, null, null, null, new LinkedList<>(), random);

    exhaustedDices.forEach(dice -> {
      gameState.getExhaustedDice().add(dice);
    });

    return gameState;
  }
}
