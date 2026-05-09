package model.ai;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Responsable de la Q-table, des paramètres d'apprentissage et de l'algorithme Q-learning.
 */
public class QLearner {

  public static final double DEFAULT_LEARNING_RATE  = 1.0;
  public static final double DEFAULT_DISCOUNT_FACTOR = 0.95;
  public static final double DEFAULT_EPSILON         = 1.0;
  public static final double EPSILON_DECAY           = 499.0 / 500.0;

  private final Map<String, Map<String, Double>> qTable;
  private final double learningRate;
  private final double discountFactor;
  private double epsilon;

  private final List<List<Experience>> experiencesInBestGame;

  private final List<List<Experience>> experiences;

  public QLearner() {
    this(DEFAULT_LEARNING_RATE, DEFAULT_DISCOUNT_FACTOR, DEFAULT_EPSILON);
  }

  public QLearner(double learningRate, double discountFactor, double epsilon) {
    this.qTable = new HashMap<>();
    this.learningRate = learningRate;
    this.discountFactor = discountFactor;
    this.epsilon = epsilon;
    this.experiences = new ArrayList<>();
    experiencesInBestGame = new ArrayList<>();
  }

  public boolean shouldExplore(double randomValue) {
    return randomValue < epsilon;
  }

  public Map<String, Double> getActionValues(String stateKey) {
    return qTable.getOrDefault(stateKey, new HashMap<>());
  }

  /**
   * Applique l'algorithme de Q-learning sur les expériences accumulées (ordre inverse).
   */
  public void learnFromExperience(int finalScore) {
    double endGameFactor = 1.0;
     final double trueLearningRate =
         finalScore < 0 ? learningRate * 0.01 : learningRate; // pénaliser les mauvaises parties
    for (int i= experiences.size() - 1; i >= 0; i--) {
      endGameFactor *= discountFactor; // pour donner plus de poids aux expériences récentes
      List<Experience> experiencesAtTurn = experiences.get(i);

      final double endGameFactorForLambda = endGameFactor; // variable finale pour la lambda

      experiencesAtTurn.forEach(exp -> {
        Map<String, Double> stateActions = qTable.computeIfAbsent(exp.state(), k -> new HashMap<>());
        double oldQ = stateActions.getOrDefault(exp.action(), 0.0);
        double newQ = oldQ + trueLearningRate * (exp.reward() + endGameFactorForLambda * finalScore - oldQ);
        stateActions.put(exp.action(), newQ);
      });
    }
    experiences.clear();
  }

  public String getBestLearnedActionsFromState(String encodedState) {
    Map<String, Double> learnedActions = qTable.getOrDefault(encodedState, new HashMap<>());

    return learnedActions.entrySet().stream()
        .max(Map.Entry.comparingByValue())
        .map(Map.Entry::getKey)
        .orElse(null);

  }

  /**
   * Applique l'algorithme de Q-learning sur les expériences locales.
   */
  public void learnFromLocalExperience(int turnExp, Map<String, String> statesAndActionsForThisTurn) {
    // turnExp = récompense du tour
    // statesAndActionsForThisTurn : clé = état du jeu, value = action ou combo d'actions prises
    List<Experience> experiencesThisTurn = new ArrayList<>();

    for (Map.Entry<String, String> entry : statesAndActionsForThisTurn.entrySet()) {
      experiencesThisTurn.add(new Experience(entry.getKey(), entry.getValue(), turnExp));
    }

    experiences.add(experiencesThisTurn);
  }

  public void decayEpsilon() {
    epsilon *= EPSILON_DECAY;
  }

  public void updateBestExperience() {
    experiencesInBestGame.clear();
    experiencesInBestGame.addAll(experiences);
  }

  // ===== Getters =====

  public Map<String, Map<String, Double>> getQTable()      { return qTable; }
  public double getEpsilon()                               { return epsilon; }
  public List<List<Experience>> getExperiences()           { return experiences; }
  public List<List<Experience>> getExperiencesInBestGame() { return experiencesInBestGame; }
}

