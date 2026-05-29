package model.recompenses;

import builders.GameStateBuilder;
import java.util.ArrayList;
import java.util.List;
import model.Dice;
import model.DiceColor;
import model.GameState;
import org.junit.jupiter.api.Test;

class RecoverDicesRewardTest {

  @Test
  void should_recover_strongest_dices() {
    List<Dice> exhaustedDices = List.of(
        new Dice(DiceColor.JAUNE),
        new Dice(DiceColor.BLEU),
        new Dice(DiceColor.ROUGE)
    );

    GameState gameState = new GameStateBuilder()
        .withDicePool(new ArrayList<>())
        .withExhaustedDices(exhaustedDices)
        .build();

    RecoverDicesReward recoverDicesReward = new RecoverDicesReward(2);

    recoverDicesReward.apply(gameState);

      assert gameState.getDicePool().size() == 2 : "Le nombre de dés récupérés est incorrect";
      assert gameState.getExhaustedDice().size() == 1 : "Le nombre de dés épuisés restants est incorrect";
      assert gameState.getDicePool().stream().anyMatch(d -> d.getColor() == DiceColor.ROUGE) : "Le dé rouge n'a pas été récupéré";
      assert gameState.getDicePool().stream().anyMatch(d -> d.getColor() == DiceColor.BLEU) : "Le dé bleu n'a pas été récupéré";


  }

}