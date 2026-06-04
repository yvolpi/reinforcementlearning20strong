package model.ai;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import builders.GameStateBuilder;
import java.util.List;
import java.util.Map;
import model.Avatar;
import model.Dice;
import model.DiceColor;
import model.GameState;
import model.Player;
import model.elements.GameAction;
import model.elements.GamePhase;
import model.ennemis.Ennemi;
import model.ennemis.EnnemiType;
import model.random.CustomRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ActionSelectorTest {

  private ActionSelector selector;
  private GameStateEncoder encoder;

  @BeforeEach
  void setUp() {
    encoder = new GameStateEncoder();
    selector = new ActionSelector(new CustomRandom(41,2,1,0), encoder);
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
    GameState gameState = new GameState(null, List.of(d1, d2, d3), null, null, null, null, null, new CustomRandom(41,2,1,0));


    List<GameAction> actions = selector.exploreEngageActions(List.of(d1, d2, d3), gameState);

    assertThat(actions.size()).isBetween(0, 3);
  }

  @Test
  void exploreEngageActions_throwsOnDuplicateDice() {
    Dice d = new Dice(DiceColor.BLEU);
    GameState gameState = new GameState(null, List.of(d), null, null, null, null, null, new CustomRandom(41,2,1,0));


    assertThatThrownBy(() -> selector.exploreEngageActions(List.of(d, d), gameState))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("Doublon");
  }

  // ===== exploreAssignActions =====

  @Test
  void exploreAssignActions_emptyEnnemis_returnsEmpty() {
    Dice d = new Dice(DiceColor.BLEU);

    List<GameAction> actions = selector.exploreAssignActions(
        new GameState(null,null,null,null,null,null,null,null)
        ,List.of(d), List.of(), 1.0);

    assertThat(actions).isEmpty();
  }

  @Test
  void should_not_assign_failed_yellow_on_class_1_ennemi_with_becket() {
    selector = new ActionSelector(new CustomRandom(3,2,0,0), encoder);

    Dice d = new Dice(DiceColor.JAUNE);
    GameState gameState = new GameStateBuilder()
        .withPlayer(new Player(Avatar.BECKET, 1, 1, 1))
        .build();
    gameState.getActiveEnnemis().add(new Ennemi(EnnemiType.CIVIL_ASSERVI, 1));
    gameState.getActiveEnnemis().add(new Ennemi(EnnemiType.LARVE_A_MOELLE, 1));

    List<GameAction> actions = selector.exploreAssignActions(gameState, List.of(d), gameState.getActiveEnnemis(), 1.0);

    assertThat(actions.getFirst().getTarget().getName()).isNotEqualTo(EnnemiType.CIVIL_ASSERVI.name);

  }

  // ===== exploreItemToThrowAction =====

  @Test
  void exploreItemToThrowAction_emptyItems_returnsNullItemAction() {
    GameAction action = selector.exploreItemToThrowAction(List.of());

    assertThat(action).isNotNull();
    assertThat(action.getItem()).isNull();
    assertThat(action.getType()).isEqualTo(GamePhase.THROW_ITEM);
  }
}
