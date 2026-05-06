package model.ai;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Responsable de la Q-table, des paramètres d'apprentissage et de l'algorithme Q-learning.
 */
public class QLearner {

  private static final double DEFAULT_LEARNING_RATE  = 0.1;
  private static final double DEFAULT_DISCOUNT_FACTOR = 0.95;
  private static final double DEFAULT_EPSILON         = 1.0;
  private static final double EPSILON_DECAY           = 2.0 / 3.0;

  private final Map<String, Map<String, Double>> qTable;
  private final double learningRate;
  private final double discountFactor;
  private double epsilon;

  private final List<Experience> experiences;

  public QLearner() {
    this(DEFAULT_LEARNING_RATE, DEFAULT_DISCOUNT_FACTOR, DEFAULT_EPSILON);
  }

  public QLearner(double learningRate, double discountFactor, double epsilon) {
    this.qTable = new HashMap<>();
    this.learningRate = learningRate;
    this.discountFactor = discountFactor;
    this.epsilon = epsilon;
    this.experiences = new ArrayList<>();
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
  public void learnFromExperience() {
    double futureMaxQ = 0.0;

    for (int i = experiences.size() - 1; i >= 0; i--) {
      Experience exp = experiences.get(i);
      Map<String, Double> stateActions = qTable.computeIfAbsent(exp.state(), k -> new HashMap<>());
      double oldQ = stateActions.getOrDefault(exp.action(), 0.0);
      double newQ = oldQ + learningRate * (exp.reward() + discountFactor * futureMaxQ - oldQ);
      stateActions.put(exp.action(), newQ);
      futureMaxQ = stateActions.values().stream().max(Double::compareTo).orElse(0.0);
    }

    experiences.clear();
  }

  public void decayEpsilon() {
    epsilon *= EPSILON_DECAY;
  }

  // ===== Getters =====

  public Map<String, Map<String, Double>> getQTable()      { return qTable; }
  public List<Experience> getExperiences()                 { return experiences; }
  public double getEpsilon()                               { return epsilon; }
}

