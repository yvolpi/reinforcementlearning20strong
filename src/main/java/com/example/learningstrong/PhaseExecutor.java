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

  public void executeDecideActiveBoss(GameState game) {
    GameAction action = ai.decideBossActivation(game);
    if (action.isActivateBoss()) {
      GameService.activateBoss(game);
    }
  }

  public void executeActivatePhase(GameState game) {
    if (game.isVictory()) return;
    game.setPhase(GamePhase.ACTIVATE_PILE);
    game.resetNdEnnemisKilled();
    game.setNbEnnemisToActivate(1);

    while (game.getNbEnnemisToAvtivate() > 0 && game.atLeastOneEnnemiOnPiles()) {

      List<Ennemi> availableEnemies = GameService.getAvailableEnemiesForActivation(game);
      GameAction action = ai.choosePileToActivate(availableEnemies, game);
      Ennemi newEnnemi = GameService.activateEnemy(game, action.getTarget().getPileNumber());

      game.setNbEnnemisToActivate(game.getNbEnnemisToAvtivate() - 1 + newEnnemi.getForcedActivations());

    }
  }

  public void executeUsagePhase(GameState game) {
    List<Item> usableItems = game.getPlayer().getItems().stream()
        .filter(item -> item.canBeUsed(game.getPlayer(), game))
        .collect(Collectors.toList());

    List<GameAction> actionsToUse = ai.chooseActionsToUse(usableItems, game);
    GameService.useItemsPhase(game, actionsToUse);
  }

  public void executeEngagePhase(GameState game) {
    List<Dice> availableDice = game.getAvailableDiceToEngage();
    Set<Dice> set = new HashSet<>(availableDice);
    if (set.size() != availableDice.size()) {
      throw new IllegalStateException("Doublon détecté dans availableDice : " + availableDice);
    }

    List<GameAction> engageActions = ai.chooseActionsToEngage(availableDice, game.getActiveEnnemis(), game);
    game.checkIfErrorBetweenPoolAndEngagedAndExhaustedDice();
    GameService.engageDicePhase(game, engageActions);
    game.checkIfErrorBetweenPoolAndEngagedAndExhaustedDice();
  }

  public void executeAssignPhase(GameState game) {
    List<Dice> assignableDice = game.getAvailableDiceToAssign();

    List<GameAction> assignActions = ai.chooseActionsToAssign(assignableDice, game.getActiveEnnemis(), game);
    GameService.assignDicePhase(game, assignActions);
  }

  public void executeClear(GameState game) {
    GameService.clearActiveZone(game);
  }
}
