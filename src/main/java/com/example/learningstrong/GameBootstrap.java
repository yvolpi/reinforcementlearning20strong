package com.example.learningstrong;

import model.GameState;
import model.ai.GameAi;
import model.elements.GameInitializer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class GameBootstrap implements CommandLineRunner {

  private static final int NB_GAMES = 2000;

  @Override
  public void run(String... args) {
    GameAi ai = new GameAi(0L);
    Integer bestReward = null;

    for (int i = 1; i <= NB_GAMES; i++) {
      System.out.println("=== Partie " + i + " ===");
      GameState game = GameInitializer.createInitialGameState(0L);

      GameLoop loop = new GameLoop(ai);
      int totalReward = loop.runGameLoop(game, ai);

      ai.learnFromExperience();
      if (bestReward == null || totalReward > bestReward) {
        bestReward = totalReward;
        System.out.println("🎉 Nouvelle meilleure récompense : " + bestReward);
      }
      ai.decayEpsilon();
      System.out.println("Récompense totale : " + totalReward);
    }
    System.out.println("Record Récompense  : " + bestReward);
    System.out.println("epsilon : " + ai.getLearner().getEpsilon());
  }
}
