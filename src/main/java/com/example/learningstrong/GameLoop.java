package com.example.learningstrong;

import model.GameState;
import model.ai.GameAi;
import model.elements.GamePhase;
import model.elements.GameService;
import model.ennemis.Ennemi;

public class GameLoop {

  private static final int VICTORY_BONUS = 10_000;
  private static final int BASE_DEFEAT_MALUS = 10_000;
  private static final int MALUS_REDUCTION_PER_TURN = 400;
  private static final int MINIMUM_DEFEAT_MALUS = 1_000;

  private final GameAi ai;
  private final TurnExecutor turnExecutor;

  public GameLoop(GameAi ai) {
    this.ai = ai;
    this.turnExecutor = new TurnExecutor(ai);
  }

  public int runGameLoop(GameState game) {
    int turn = 0;
    int totalReward = 0;
    GameState previousState = game.clone();

    while (!game.isVictory() && !game.isDefeat()) {
      turn++;
      System.out.println("--- Tour " + turn + " ---");

      turnExecutor.executeTurn(game);
      displayTurnSummary(game, turn);

      int turnReward = GameService.evaluateTurn(previousState, game);
      totalReward += turnReward;
      previousState = game.clone();
    }

    totalReward += computeEndGameReward(game, turn);
    return totalReward;
  }

  private int computeEndGameReward(GameState game, int turns) {
    if (game.isVictory()) {
      System.out.println("🎉 Félicitations, victoire en " + turns + " tours !");
      return VICTORY_BONUS;
    } else {
      int malus = Math.max(MINIMUM_DEFEAT_MALUS, BASE_DEFEAT_MALUS - MALUS_REDUCTION_PER_TURN * turns);
      System.out.println("💀 Défaite après " + turns + " tours.");
      return -malus;
    }
  }

  private void displayTurnSummary(GameState game, int turn) {
    System.out.println("État du jeu après le tour " + turn + " :");
    System.out.println("  - PV du joueur : " + game.getPlayer().getLife());
    System.out.println("  - Dés en réserve : " + game.getDicePool().size());
    System.out.println("  - Dés épuisés : " + game.getExhaustedDice().size());
    if (game.isActivatedBoss()) {
      Ennemi activatedBoss = game.getActivatedBoss();
      System.out.println("  - Boss actif : " + activatedBoss.getName() + " (PV: " + activatedBoss.getCurrentLife() + ", nb des assignes : " + activatedBoss.getAssignedDice().size() + ")");
      if (game.getActiveEnnemis().size() > 1) {
        System.out.println("  - Ennemis actifs :");
        for (Ennemi ennemi : game.getActiveEnnemis()) {
          if (ennemi != activatedBoss) {
            System.out.println("    - " + ennemi.getName() + " (PV: " + ennemi.getCurrentLife() + ", nb des assignes : " + ennemi.getAssignedDice().size() + ")");
          }
        }
      }
    }
  }
}
