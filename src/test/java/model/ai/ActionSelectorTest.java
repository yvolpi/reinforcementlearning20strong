package model.ai;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Map;
import java.util.Random;
import model.Dice;
import model.DiceColor;
import model.elements.GameAction;
import model.elements.GamePhase;
import model.ennemis.Ennemi;
import model.ennemis.EnnemiType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ActionSelectorTest {

  private ActionSelector selector;
  private GameStateEncoder encoder;

  @BeforeEach
  void setUp() {
    encoder = new GameStateEncoder();
    selector = new ActionSelector(new Random(42), encoder);
  }

  // ===== exploreActivateAction =====

  @Test
  void exploreActivateAction_returnsActionWithEnnemi() {
    Ennemi e = new Ennemi(EnnemiType.ARACHNOPOULPE, 1);

    GameAction action = selector.exploreActivateAction(List.of(e));

    assertThat(action).isNotNull();
    assertThat(action.getTarget()).isEqualTo(e);
    assertThat(action.getType()).isEqualTo(GamePhase.ACTIVATE_PILE);
  }

  // ===== exploreEngageActions =====

  @Test
  void exploreEngageActions_returnsBetweenZeroAndAllDice() {
    Dice d1 = new Dice(DiceColor.BLEU);
    Dice d2 = new Dice(DiceColor.ROUGE);
    Dice d3 = new Dice(DiceColor.VERT);

    List<GameAction> actions = selector.exploreEngageActions(List.of(d1, d2, d3));

    assertThat(actions.size()).isBetween(0, 3);
  }

  @Test
  void exploreEngageActions_throwsOnDuplicateDice() {
    Dice d = new Dice(DiceColor.BLEU);

    assertThatThrownBy(() -> selector.exploreEngageActions(List.of(d, d)))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("Doublon");
  }

  // ===== exploreAssignActions =====

  @Test
  void exploreAssignActions_emptyEnnemis_returnsEmpty() {
    Dice d = new Dice(DiceColor.BLEU);

    List<GameAction> actions = selector.exploreAssignActions(List.of(d), List.of());

    assertThat(actions).isEmpty();
  }

  // ===== exploreItemToThrowAction =====

  @Test
  void exploreItemToThrowAction_emptyItems_returnsNullItemAction() {
    GameAction action = selector.exploreItemToThrowAction(List.of());

    assertThat(action).isNotNull();
    assertThat(action.getItem()).isNull();
    assertThat(action.getType()).isEqualTo(GamePhase.THROW_ITEM);
  }

  // ===== getDiceColorPriority =====

  @Test
  void getDiceColorPriority_rougeIsHighest() {
    assertThat(selector.getDiceColorPriority(DiceColor.ROUGE))
        .isGreaterThan(selector.getDiceColorPriority(DiceColor.VIOLET));
  }

  @Test
  void getDiceColorPriority_jauneIsLowest() {
    assertThat(selector.getDiceColorPriority(DiceColor.JAUNE)).isEqualTo(1);
  }

  // ===== findBestEngageAction =====

  @Test
  void findBestEngageAction_returnsActionWithHighestQValue() {
    Dice rouge = new Dice(DiceColor.ROUGE);
    Dice bleu  = new Dice(DiceColor.BLEU);
    GameAction actionRouge = new GameAction(GamePhase.ENGAGE_DICE, rouge);
    GameAction actionBleu  = new GameAction(GamePhase.ENGAGE_DICE, bleu);

    Map<String, Double> qValues = Map.of(
        "ENGAGE:ROUGE", 5.0,
        "ENGAGE:BLEU",  1.0
    );

    GameAction best = selector.findBestEngageAction(List.of(actionRouge, actionBleu), qValues);

    assertThat(best.getDice().getColor()).isEqualTo(DiceColor.ROUGE);
  }
}
