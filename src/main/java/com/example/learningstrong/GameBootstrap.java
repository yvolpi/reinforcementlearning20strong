package com.example.learningstrong;

import java.util.ArrayList;
import java.util.List;
import model.GameState;
import model.ai.Experience;
import model.ai.GameAi;
import model.elements.GameInitializer;
import model.random.CustomRandom;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class GameBootstrap implements CommandLineRunner {

  private static final int NB_GAMES = 2000;

  //Custom random for game
  private static final int GAME_RANDOM_SEED = 0; // seed fixe pour la reproductibilité
  private static final int GAME_RANDOM_MODULO = 3;
  private static final int GAME_RANDOM_COEFFICIENT = 2;
  private static final int GAME_RANDOM_INCREMENT = 1;
  //Custom random for ai
  private static final int AI_RANDOM_SEED = 0; // seed fixe pour la reproductibilité
  private static final int AI_RANDOM_MODULO = 3;
  private static final int AI_RANDOM_COEFFICIENT = 2;
  private static final int AI_RANDOM_INCREMENT = 1;

  @Override
  public void run(String... args) {
    CustomRandom aiCustomRandom = new CustomRandom(AI_RANDOM_MODULO, AI_RANDOM_COEFFICIENT, AI_RANDOM_INCREMENT, AI_RANDOM_SEED);

    GameAi ai = new GameAi(aiCustomRandom);
    Integer bestReward = null;
    List<List<Experience>> bestGameExperiences = new ArrayList<>();
    int nbVictories = 0;

    for (int i = 1; i <= NB_GAMES; i++) {
      System.out.println("=== Partie " + i + " ===");
      CustomRandom gameCustomRandom = new CustomRandom(GAME_RANDOM_MODULO, GAME_RANDOM_COEFFICIENT, GAME_RANDOM_INCREMENT, GAME_RANDOM_SEED);
      GameState game = GameInitializer.createInitialGameState(gameCustomRandom);

      GameLoop loop = new GameLoop(ai);
      int totalReward = loop.runGameLoop(game, ai);

      if (bestReward == null || totalReward > bestReward) {
        bestReward = totalReward;
        ai.updateBestGame();
        System.out.println("🎉 Nouvelle meilleure récompense : " + bestReward);
      }
      if (totalReward > 0) {
        // victoire
        nbVictories++;
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
    System.out.println("Nombre de victoires : " + nbVictories + " sur " + NB_GAMES + " parties");
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
