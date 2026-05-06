package model.ai;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import model.Dice;
import model.DiceColor;
import model.elements.GameAction;
import model.elements.GamePhase;
import model.ennemis.Ennemi;
import model.ennemis.EnnemiType;
import org.junit.jupiter.api.Test;

class ActionKeyEncoderTest {

  @Test
  void encodeEngageActions_emptyList_returnsNONE() {
    assertThat(ActionKeyEncoder.encodeEngageActions(List.of())).isEqualTo("NONE");
  }

  @Test
  void encodeEngageActions_withDice_returnsDiceColors() {
    Dice dice = new Dice(DiceColor.ROUGE);
    GameAction action = new GameAction(GamePhase.ENGAGE_DICE, dice);

    String result = ActionKeyEncoder.encodeEngageActions(List.of(action));

    assertThat(result).isEqualTo("ROUGE");
  }

  @Test
  void encodeEngageActions_withNullDice_returnsNONE() {
    GameAction action = new GameAction(GamePhase.ENGAGE_DICE, (Dice) null);

    String result = ActionKeyEncoder.encodeEngageActions(List.of(action));

    assertThat(result).isEqualTo("NONE");
  }

  @Test
  void encodeAssignActions_emptyList_returnsNONE() {
    assertThat(ActionKeyEncoder.encodeAssignActions(List.of())).isEqualTo("NONE");
  }

  @Test
  void encodeAssignActions_withDiceAndTarget_returnsFormattedString() {
    Dice dice = new Dice(DiceColor.BLEU);
    dice.setToHit();
    Ennemi target = new Ennemi(EnnemiType.ARACHNOPOULPE, 1);
    GameAction action = new GameAction(GamePhase.ASSIGN_DICE, dice, target);

    String result = ActionKeyEncoder.encodeAssignActions(List.of(action));

    assertThat(result).isEqualTo("BLEU:1->ARACHNOPOULPE");
  }

  @Test
  void encodeThrowItemAction_nullItem_returnsNONE() {
    GameAction action = new GameAction(GamePhase.THROW_ITEM, (model.items.Item) null);

    assertThat(ActionKeyEncoder.encodeThrowItemAction(action)).isEqualTo("NONE");
  }
}
