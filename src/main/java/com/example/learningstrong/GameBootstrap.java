package com.example.learningstrong;

import model.GameState;
import model.ai.GameAi;
import model.elements.GameInitializer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class GameBootstrap implements CommandLineRunner {

  private static final int NB_GAMES = 20;

  @Override
  public void run(String... args) {
    GameAi ai = new GameAi(0L);

    for (int i = 0; i < NB_GAMES; i++) {
      System.out.println("=== Partie " + (i + 1) + " ===");
      GameState game = GameInitializer.createInitialGameState(0L);

      GameLoop loop = new GameLoop(ai);
      int totalReward = loop.runGameLoop(game);

      ai.learnFromExperience();
      System.out.println("Récompense totale : " + totalReward);
    }
  }
}
