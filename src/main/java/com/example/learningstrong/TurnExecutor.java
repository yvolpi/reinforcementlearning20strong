package com.example.learningstrong;

import model.Avatar;
import model.GameState;
import model.ai.GameAi;
import model.ai.StateAction;
import model.effets.bonus.OrloEffect;
import model.elements.GamePhase;
import model.elements.GameService;
import model.missions.M10;
import model.missions.Mission;

public class TurnExecutor {

  private final GameAi ai;
  private final PhaseExecutor phaseExecutor;

  public TurnExecutor(GameAi ai) {
    this.ai = ai;
    this.phaseExecutor = new PhaseExecutor(ai);
  }

  public void executeTurn(GameState game) {
    // Reset usable items
    game.getPlayer().resetUsableItems();
    game.setWastedDiceThisTurn(0);
    game.setPenalityKillCivilAsserviFirst(false);
    game.setPenaltyGiveUpMission(false);
    game.setEngageAssignStep(1);
    game.resetBonusEffectTurn();

    if (game.getPlayer().getAvatar().equals(Avatar.ORLO)) {
      game.getBonusEffectsTurn().add(new OrloEffect());
    }

    GameService.triggeredAutomaticItemsEffectBeforeActivation(game);
    // Mission remplie ?
    if (game.getActiveMission() != null && game.getActiveMission().isSuccess()) {
      game.removeActiveMission();
    }

    // Si le boss n'est pas activé et que la mission active peut être abandonnée, le joueur peut l'abandonner
    if (!game.isActivatedBoss() && game.getActiveMission() != null && game.getActiveMission().canBeAbandoned()) {
      game.setPhase(GamePhase.GIVE_UP_MISSION);
      phaseExecutor.executeAbandonMissionPhase(game);

    }


    // Phase 1 : Activation
    if (game.playerCanUseItem()) {
      game.setPhase(GamePhase.USE_ITEM_BEFORE_ACTIVATE);
      phaseExecutor.executeUsagePhase(game);
    }

    game.setPhase(GamePhase.ACTIVATE_PILE);
    if (!game.isActivatedBoss() && game.forceActiveBoss()) {
      GameService.activateBoss(game);
    } else if (!game.isActivatedBoss() && game.canActiveBoss()) {
      // c'est à l'ia de décider s'il active le boss maintenant
      phaseExecutor.executeDecideActiveBoss(game);
    }
    if (!game.isActivatedBoss()) {
      Mission mission = game.getActiveMission();
      if (mission instanceof M10 && game.atLeastOneEnnemiOnPiles() && ((M10) mission).getNumberPile() == null) {
        phaseExecutor.executeChoosePileForM10(game);
      }
      if (game.getPlayer().getAvatar().equals(Avatar.RAVEN)) {
        phaseExecutor.executeChoosePilesRaven(game);
      }
      phaseExecutor.executeActivatePhase(game);
      if (mission != null) {
        mission.afterActivation(game);
      }
    }

    game.resetNdEnnemisKilled();

    GameService.activeEffectsBeforeAllEngagement(game);

    // Phase 2-3 : Boucle engagement/assignation
    int strategy = game.getPlayer().getStrategy();
    for (int step = 0; step < strategy; step++) {
      //si tous les ennemis actifs sont morts, on peut passer au sufferDamagePhase
      if (game.allActiveEnnemisDefeated()) {
        break;
      }
      game.checkIfErrorBetweenPoolAndEngagedAndExhaustedDice();
      //System.out.println("--- Étape " + (step + 1) + " de la stratégie --- use item");
      // check si le joueur peut utiliser un item avant d'engager les dés
      if (game.playerCanUseItem()) {
        game.setPhase(GamePhase.USE_ITEM_BEFORE_ENGAGE);
        phaseExecutor.executeUsagePhase(game);
        // si game contient le bonus lunaire, le joueur récupère tous les dés engagés qui ont donné un échec
        GameService.applyBonusLunaire(game);

      }
      game.checkIfErrorBetweenPoolAndEngagedAndExhaustedDice();
      //System.out.println("--- Étape " + (step + 1) + " de la stratégie --- engage dice");
      game.setMaxEngagedDicePerTurn(game.getDicePool().size());
      GameService.activeEffectsBeforeEngagement(game);
      game.setPhase(GamePhase.ENGAGE_DICE);
      phaseExecutor.executeEngagePhase(game);
      game.checkIfErrorBetweenPoolAndEngagedAndExhaustedDice();
      //System.out.println("--- Étape " + (step + 1) + " de la stratégie --- use item");
      if (game.playerCanUseItem()) {
        game.setPhase(GamePhase.USE_ITEM_BEFORE_ASSIGN);
        phaseExecutor.executeUsagePhase(game);
      }
      game.checkIfErrorBetweenPoolAndEngagedAndExhaustedDice();
      //System.out.println("--- Étape " + (step + 1) + " de la stratégie --- assign dice");
      game.setPhase(GamePhase.ASSIGN_DICE);
      phaseExecutor.executeAssignPhase(game);
      if (game.isVictory()) {
        ai.getHistory().add(new StateAction("Le joueur a gagné.", "Le boss est vaincu."));
        return;
      }
    }

    // Phases finales
    GameService.sufferDamagePhase(game, ai);
    if (game.isDefeat()) {
      ai.getHistory().add(new StateAction("Le joueur a perdu.", "Le héros est vaincu."));
      return;
    }
    GameService.applyEnnemisSubsequentEffects(game);
    GameService.exhaustionPhase(game, ai);
    GameService.recoverDicePhase(game, ai);
    GameService.applyPendingRewards(game, ai);
    game.setPhase(GamePhase.CLEAR);
    if (game.isActivatedBoss()) {
      GameService.clearDeadNonBossEnnemis(game);
    } else {
      phaseExecutor.executeClear(game);
      phaseExecutor.executeDropNonMandatoryEnnemis(game);
    }
  }
}
