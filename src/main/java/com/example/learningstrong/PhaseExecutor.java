package com.example.learningstrong;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import model.Dice;
import model.GameState;
import model.ai.ActionKeyEncoder;
import model.ai.Experience;
import model.ai.GameAi;
import model.elements.GameAction;
import model.elements.GamePhase;
import model.elements.GameService;
import model.ennemis.Ennemi;
import model.items.Item;

public class PhaseExecutor {

  private final GameAi ai;

  public PhaseExecutor(GameAi ai) {
    this.ai = ai;
  }

  public void executeActivatePhase(GameState game) {
    if (game.isVictory()) return;
    game.setPhase(GamePhase.ACTIVATE_PILE);
    game.resetNdEnnemisKilled();
    game.setNbEnnemisToActivate(1);

    while (game.getNbEnnemisToAvtivate() > 0 && game.atLeastOneEnnemiOnPiles()) {
      String stateBefore = ai.encodeStateWithPile(game);

      List<Ennemi> availableEnemies = GameService.getAvailableEnemiesForActivation(game);
      GameAction action = ai.choosePileToActivate(availableEnemies, game);
      Ennemi newEnnemi = GameService.activateEnemy(game, action.getTarget().getPileNumber());

      game.setNbEnnemisToActivate(game.getNbEnnemisToAvtivate() - 1 + newEnnemi.getForcedActivations());

      String stateAfter = ai.encodeStateWithPile(game);
      String actionKey = "ACTIVATE:PILE_" + action.getTarget().getPileNumber();
      ai.getExperiences().add(new Experience(stateBefore, actionKey, 0.0, stateAfter));
    }
    game.setWastedDiceThisTurn(0);
  }

  public void executeUsagePhase(GameState game) {
    String stateBefore = ai.encodeState(game);
    List<Item> usableItems = game.getPlayer().getItems().stream()
        .filter(item -> item.canBeUsed(game.getPlayer(), game))
        .collect(Collectors.toList());

    List<GameAction> actionsToUse = ai.chooseActionsToUse(usableItems, game);
    GameService.useItemsPhase(game, actionsToUse);

    String stateAfter = ai.encodeState(game);
    String actionKey = ActionKeyEncoder.encodeEngageActions(actionsToUse);
    ai.getExperiences().add(new Experience(stateBefore, actionKey, 0.0, stateAfter));
  }

  public void executeEngagePhase(GameState game) {
    String stateBefore = ai.encodeStateForEngage(game);
    List<Dice> availableDice = game.getAvailableDiceToEngage();
    Set<Dice> set = new HashSet<>(availableDice);
    if (set.size() != availableDice.size()) {
      throw new IllegalStateException("Doublon détecté dans availableDice : " + availableDice);
    }

    List<GameAction> engageActions = ai.chooseActionsToEngage(availableDice, game.getActiveEnnemis(), game);
    game.checkIfErrorBetweenPoolAndEngagedAndExhaustedDice();
    GameService.engageDicePhase(game, engageActions);
    game.checkIfErrorBetweenPoolAndEngagedAndExhaustedDice();

    String stateAfter = ai.encodeStateForEngage(game);
    String actionKey = ActionKeyEncoder.encodeEngageActions(engageActions);
    ai.getExperiences().add(new Experience(stateBefore, actionKey, 0.0, stateAfter));
  }

  public void executeAssignPhase(GameState game) {
    String stateBefore = ai.encodeStateForAssign(game);
    List<Dice> assignableDice = game.getAvailableDiceToAssign();

    List<GameAction> assignActions = ai.chooseActionsToAssign(assignableDice, game.getActiveEnnemis(), game);
    GameService.assignDicePhase(game, assignActions);

    String stateAfter = ai.encodeStateForAssign(game);
    String actionKey = ActionKeyEncoder.encodeAssignActions(assignActions);
    ai.getExperiences().add(new Experience(stateBefore, actionKey, 0.0, stateAfter));
  }

  public void executeClear(GameState game) {
    String stateBefore = ai.encodeStateWithPile(game);
    GameService.clearActiveZone(game);
    String stateAfter = ai.encodeStateWithPile(game);
    ai.getExperiences().add(new Experience(stateBefore, "CLEAR_PHASE", 0.0, stateAfter));
  }
}
