package com.example.learningstrong;

import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;
import model.Dice;
import model.DiceState;
import model.GameState;
import model.ai.GameAi;
import model.ai.StateAction;
import model.elements.GameAction;
import model.elements.GameInitializer;
import model.elements.GamePhase;
import model.elements.GameService;
import model.ennemis.Ennemi;
import model.items.Item;
import model.missions.M10;
import model.missions.M2;
import model.missions.Mission;

public class PhaseExecutor {

  private final GameAi ai;

  public PhaseExecutor(GameAi ai) {
    this.ai = ai;
  }

  public void executeChoosePilesRaven(GameState game) {
    List<GameAction> actions = ai.decidePilesForRaven(game);
    for (GameAction action : actions) {
      int pileNumber = action.getPileNumber();
      // récupérer l'ennemi du dessus de la pile pour le mettre en-dessous
      Queue<Ennemi> pile = getPileByNumber(game, pileNumber);
      Ennemi ennemi = pile.poll();
      if (ennemi != null) {
        pile.add(ennemi);
      }
    }
  }

  public void executeChoosePileForM10(GameState game) {
    Mission mission = game.getActiveMission();
    if (!(mission instanceof M10)) {
      throw new IllegalStateException("Mission active n'est pas M10, ne peut pas choisir une pile pour M10");
    }
    GameAction action = ai.decidePileForM10(game);
    ((M10) mission).setNumberPile(action.getPileNumber());
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
    game.setNbEnnemisToActivate(1);
    if (game.isActivateOneMoreEnnemiNextTurn()) {
      game.setNbEnnemisToActivate(game.getNbEnnemisToAvtivate() + 1);
      game.setActivateOneMoreEnnemiNextTurn(false);
    }

    while (game.getNbEnnemisToAvtivate() > 0 && game.atLeastOneEnnemiOnPiles()) {

      List<Ennemi> availableEnemies = GameService.getAvailableEnemiesForActivation(game);
      GameAction action = ai.choosePileToActivate(availableEnemies, game);
      Ennemi newEnnemi = GameService.activateEnemy(game, action.getTarget().getPileNumber());

      game.setNbEnnemisToActivate(game.getNbEnnemisToAvtivate() - 1 + newEnnemi.getForcedActivations());

    }

    while (game.getNbEnnemisToAvtivate() > 0) {
      // Si on doit encore activer des ennemis mais qu'il n'y en a plus à activer, on active des ennemis aléatoires
      Ennemi ennemi = new Ennemi(GameInitializer.ennemis.get(game.getRandom().nextInt(GameInitializer.ennemis.size())), 1);
      Ennemi newEnnemi = GameService.activateEnemy(game, ennemi);
      game.setNbEnnemisToActivate(game.getNbEnnemisToAvtivate() - 1 + newEnnemi.getForcedActivations());
    }
  }

  public void executeUsagePhase(GameState game) {
    // enlever l'effet bonus IchorVeriteEffect s'il y est
    game.getBonusEffectsTurn().removeIf(effect -> effect.getClass().getSimpleName().equals("IchorVeriteEffect"));


    List<Item> usableItems = game.getPlayer().getItems().stream()
        .filter(item -> item.canBeUsed(game.getPlayer(), game))
        .collect(Collectors.toList());

    List<GameAction> actionsToUse = ai.chooseActionsToUse(usableItems, game);
    GameService.useItemsPhase(game, actionsToUse, ai);
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

    if (!assignableDice.isEmpty()) {
      List<GameAction> assignActions = ai.chooseActionsToAssign(assignableDice, game.getActiveEnnemis(), game);
      GameService.assignDicePhase(game, assignActions);
    } else {
      ai.getHistory().add(new StateAction(ai.getEncoder().encodeStateForEndEngage(game), "")); // ajouter une action "ne rien assigner" dans l'historique de l'ia
    }

    // M2 = épuiser des touches critiques non assignées
    Mission mission = game.getActiveMission();
    if (mission instanceof M2) {
      List<Dice> criticalHitDicesToExhaust =
      game.getEngagedDices()          .stream()
          .filter(dice -> dice.getState() == DiceState.ENGAGE && dice.isCriticHit())
          .limit(((M2) mission).getNumberOfCriticalHitsToExhaust())
          .toList();
      for (Dice dice : criticalHitDicesToExhaust) {
        dice.setState(DiceState.EPUISE);
        game.getEngagedDices().remove(dice);
        game.getExhaustedDice().add(dice);
        ((M2) mission).onExhaustCriticalHit(game);
      }
    }
    game.setEngageAssignStep(game.getEngageAssignStep() + 1);
  }

  public void executeClear(GameState game) {
    GameService.clearActiveZone(game);
  }

  public void executeDropNonMandatoryEnnemis(GameState game) {
    GameService.dropNonMandatoryEnnemis(game, ai);
    game.getActiveMission().onEndTurn(game);
  }

  public void executeAbandonMissionPhase(GameState game) {
    GameAction action = ai.decideAbandonMission(game);
    if (action.isGiveUpMission()) {
      game.setPenaltyGiveUpMission(true);
    }
  }

  private Queue<Ennemi> getPileByNumber(GameState game, int pileNumber) {
    return switch (pileNumber) {
      case 1 -> game.getPile1();
      case 2 -> game.getPile2();
      case 3 -> game.getPile3();
      default -> throw new IllegalStateException("Numéro de pile invalide pour Raven : " + pileNumber);
    };
  }
}
