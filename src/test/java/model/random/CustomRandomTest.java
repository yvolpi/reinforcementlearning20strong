package model.random;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;


import java.util.ArrayList;
import java.util.List;
import model.Dice;
import model.DiceColor;
import org.junit.jupiter.api.Test;

class CustomRandomTest {

  @Test
  void shouldShuffleListDice() {
    Dice dice1 = new Dice(DiceColor.JAUNE);
    Dice dice2 = new Dice(DiceColor.VERT);

    List<Dice> dices = new ArrayList<>();
    dices.add(dice1);
    dices.add(dice2);
    CustomRandom customRandom = new CustomRandom(3, 1, 1, 2);
    // customRandom donnera 1 puis 2
    customRandom.shuffle(dices);

    assertThat(dices.get(0)).isEqualTo(dice2);
    assertThat(dices.get(1)).isEqualTo(dice1);
  }

}