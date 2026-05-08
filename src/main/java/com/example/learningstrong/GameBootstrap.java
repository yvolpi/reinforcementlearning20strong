package com.example.learningstrong;

import java.util.ArrayList;
import java.util.List;
import model.GameState;
import model.ai.Experience;
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
    List<List<Experience>> bestGameExperiences = new ArrayList<>();


    for (int i = 1; i <= NB_GAMES; i++) {
      System.out.println("=== Partie " + i + " ===");
      GameState game = GameInitializer.createInitialGameState(0L);

      GameLoop loop = new GameLoop(ai);
      int totalReward = loop.runGameLoop(game, ai);

      if (bestReward == null || totalReward > bestReward) {
        bestReward = totalReward;
        ai.updateBestGame();
        System.out.println("🎉 Nouvelle meilleure récompense : " + bestReward);
      }
      if (totalReward > 0) {
        // victoire
        bestGameExperiences = new ArrayList<>(ai.getLearner().getExperiences()); // copie profonde si besoin
        System.out.println("superbe victoire ! En " + bestGameExperiences.size() + " tours, récompense : " + totalReward);
      }
      ai.learnFromExperience(totalReward);
      if (totalReward > 0) {
        // victoire
        System.out.println("superbe victoire ! En " + bestGameExperiences.size() + " tours, récompense : " + totalReward);

        for (int j = 0; j<bestGameExperiences.size(); j++) {
          System.out.println("tour " + (j+1));
          Experience firstBestExp = bestGameExperiences.get(j).getFirst();
          Double qValue = ai.getLearner().getQTable().get(firstBestExp.state()).get(firstBestExp.action());
          System.out.println(" - Meilleur premier coup : " + firstBestExp.action() + ", Q-value : " + qValue);
        }
      }
      ai.decayEpsilon();
      System.out.println("Récompense totale : " + totalReward);
    }
    System.out.println("Record Récompense  : " + bestReward);
    System.out.println("epsilon : " + ai.getLearner().getEpsilon());
    // afficher les meilleurs premiers coups
    bestGameExperiences = ai.getLearner().getExperiencesInBestGame();
    for (int i = 0; i< bestGameExperiences.size(); i++) {
      System.out.println("tour " + (i+1));
      Experience firstBestExp = bestGameExperiences.get(i).getFirst();
      Double qValue = ai.getLearner().getQTable().get(firstBestExp.state()).get(firstBestExp.action());
      System.out.println(" - Meilleur premier coup : " + firstBestExp.action() + ", Q-value : " + qValue);
    }
  }
}
