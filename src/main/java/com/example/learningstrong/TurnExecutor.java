package com.example.learningstrong;

import model.GameState;
import model.ai.GameAi;
import model.elements.GamePhase;
import model.elements.GameService;

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
    game.setEngageAssignStep(1);
    game.resetBonusEffectTurn();

    GameService.triggeredAutomaticItemsEffectBeforeActivation(game);

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
      phaseExecutor.executeActivatePhase(game);
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
      if (game.isVictory()) return;
    }

    // Phases finales
    GameService.sufferDamagePhase(game);
    GameService.applyEnnemisSubsequentEffects(game);
    GameService.exhaustionPhase(game);
    GameService.recoverDicePhase(game, ai);
    GameService.applyPendingRewards(game, ai);
    game.setPhase(GamePhase.CLEAR);
    if (game.isActivatedBoss()) {
      GameService.clearDeadNonBossEnnemis(game);
    } else {
      phaseExecutor.executeClear(game);
    }
  }
}
