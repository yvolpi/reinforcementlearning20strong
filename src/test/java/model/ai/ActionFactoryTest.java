package model.ai;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import model.Dice;
import model.DiceColor;
import model.DiceState;
import model.elements.GameAction;
import model.elements.GamePhase;
import model.ennemis.Ennemi;
import model.ennemis.EnnemiType;
import model.items.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ActionFactoryTest {

  private ActionFactory factory;

  @BeforeEach
  void setUp() {
    factory = new ActionFactory();
  }

  @Test
  void createPossibleActivateActions_returnsOneActionPerEnnemi() {
    Ennemi e1 = new Ennemi(EnnemiType.ARACHNOPOULPE, 1);
    Ennemi e2 = new Ennemi(EnnemiType.ASSERVI, 2);

    List<GameAction> actions = factory.createPossibleActivateActions(List.of(e1, e2));

    assertThat(actions).hasSize(2);
    assertThat(actions).allMatch(a -> a.getType() == GamePhase.ACTIVATE_PILE);
    assertThat(actions.get(0).getTarget()).isEqualTo(e1);
    assertThat(actions.get(1).getTarget()).isEqualTo(e2);
  }

  @Test
  void createPossibleEngageActions_includesNullActionAtEnd() {
    Dice d1 = new Dice(DiceColor.BLEU);
    Dice d2 = new Dice(DiceColor.ROUGE);

    List<GameAction> actions = factory.createPossibleEngageActions(List.of(d1, d2));

    assertThat(actions).hasSize(3);
    assertThat(actions.get(2)).isNull();
    assertThat(actions.get(0).getDice()).isEqualTo(d1);
  }

  @Test
  void createPossibleAssignActions_returnsCartesianProductPlusNull() {
    Dice d1 = new Dice(DiceColor.BLEU);
    Ennemi e1 = new Ennemi(EnnemiType.ARACHNOPOULPE, 1);
    Ennemi e2 = new Ennemi(EnnemiType.ASSERVI, 2);

    List<GameAction> actions = factory.createPossibleAssignActions(List.of(d1), List.of(e1, e2));

    // 1 dé x 2 ennemis + 1 null = 3
    assertThat(actions).hasSize(3);
    assertThat(actions.get(2)).isNull();
  }

  @Test
  void createPossibleRemoveItemActions_alwaysIncludesDoNothingAction() {
    List<GameAction> actions = factory.createPossibleRemoveItemActions(List.of());

    assertThat(actions).hasSize(1);
    assertThat(actions.get(0).getItem()).isNull();
  }
}
