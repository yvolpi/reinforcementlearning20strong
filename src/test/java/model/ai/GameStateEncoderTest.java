package model.ai;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayDeque;
import java.util.List;
import model.Dice;
import model.DiceColor;
import model.GameState;
import model.Player;
import model.elements.GameAction;
import model.elements.GamePhase;
import model.ennemis.Ennemi;
import model.ennemis.EnnemiType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GameStateEncoderTest {

  private GameStateEncoder encoder;

  @BeforeEach
  void setUp() {
    encoder = new GameStateEncoder();
  }

  @Test
  void encodeState_returnsLifeAndReserveDiceAndEnemies() {
    GameState gs = mock(GameState.class);
    Player player = mock(Player.class);
    Dice reserveDice = new Dice(DiceColor.BLEU); // state = RESERVE par défaut
    Ennemi ennemi = new Ennemi(EnnemiType.ARACHNOPOULPE, 1);

    when(gs.getPlayer()).thenReturn(player);
    when(player.getLife()).thenReturn(10);
    when(gs.getDicePool()).thenReturn(List.of(reserveDice));
    when(gs.getActiveEnnemis()).thenReturn(List.of(ennemi));

    String encoded = encoder.encodeState(gs);

    assertThat(encoded).startsWith("10|1|");
    assertThat(encoded).contains(ennemi.getName());
  }

  @Test
  void encodeActivateAction_nullAction_returnsNONE() {
    assertThat(ActionKeyEncoder.encodeActivateAction(null)).isEqualTo("NONE");
  }

  @Test
  void encodeActivateAction_validAction_returnsPileNumber() {
    Ennemi ennemi = new Ennemi(EnnemiType.ARACHNOPOULPE, 2);
    GameAction action = new GameAction(GamePhase.ACTIVATE_PILE, ennemi);

    assertThat(ActionKeyEncoder.encodeActivateAction(action)).isEqualTo("ACTIVATE:PILE_2");
  }

  @Test
  void encodeEngageAction_nullAction_returnsNONE() {
    assertThat(encoder.encodeEngageAction(null)).isEqualTo("NONE");
  }

  @Test
  void encodeEngageAction_validAction_returnsDiceColor() {
    Dice dice = new Dice(DiceColor.VIOLET);
    GameAction action = new GameAction(GamePhase.ENGAGE_DICE, dice);

    assertThat(encoder.encodeEngageAction(action)).isEqualTo("ENGAGE:VIOLET");
  }

  @Test
  void encodeStateWithPile_noPilesPopulated_returnsNONE() {
    GameState gs = mock(GameState.class);
    Player player = mock(Player.class);
    when(gs.getPlayer()).thenReturn(player);
    when(player.getLife()).thenReturn(5);
    when(gs.getDicePool()).thenReturn(List.of());
    when(gs.getPile1()).thenReturn(new ArrayDeque<>());
    when(gs.getPile2()).thenReturn(new ArrayDeque<>());
    when(gs.getPile3()).thenReturn(new ArrayDeque<>());

    String result = encoder.encodeStateWithPile(gs);

    assertThat(result).isEqualTo("5|0|NONE;NONE;NONE");
  }
}
