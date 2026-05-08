package model.ai;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class QLearnerTest {

  private QLearner qLearner;

  @BeforeEach
  void setUp() {
    qLearner = new QLearner();
  }

  @Test
  void shouldExplore_returnsTrue_whenRandomBelowEpsilon() {
    // epsilon initial = 1.0 → toujours explorer
    assertThat(qLearner.shouldExplore(0.99)).isTrue();
  }

  @Test
  void shouldExplore_returnsFalse_whenRandomAboveEpsilon() {
    QLearner learner = new QLearner(0.1, 0.5, 0.0); // epsilon = 0
    assertThat(learner.shouldExplore(0.5)).isFalse();
  }

  @Test
  void getActionValues_unknownState_returnsEmptyMap() {
    assertThat(qLearner.getActionValues("UNKNOWN_STATE")).isEmpty();
  }

  @Test
  void learnFromExperience_updatesQTableCorrectly() {
    // Une expérience : reward = 10 pour le tour, finalScore = 0
    // newQ = 0 + 0.1 * (10 + discountFactor * 0 - 0) = 1.0
    qLearner.learnFromLocalExperience(10, Map.of("S1", "A1"));

    qLearner.learnFromExperience(0);

    double qValue = qLearner.getQTable().get("S1").get("A1");
    assertThat(qValue).isCloseTo(1.0, within(0.001));
  }

  @Test
  void learnFromExperience_clearsExperiencesAfterLearning() {
    qLearner.learnFromLocalExperience(5, Map.of("S1", "A1"));

    qLearner.learnFromExperience(0);

    // Après apprentissage, la Q-table est bien remplie et les expériences vidées
    // On vérifie que l'apprentissage a bien eu lieu (Q-table non vide)
    assertThat(qLearner.getQTable()).isNotEmpty();
    // On vérifie qu'un 2e appel à learnFromExperience ne modifie pas la Q-table
    Map<String, Double> qBefore = Map.copyOf(qLearner.getQTable().get("S1"));
    qLearner.learnFromExperience(100);
    assertThat(qLearner.getQTable().get("S1")).isEqualTo(qBefore);
  }

  @Test
  void learnFromExperience_chainedRewards_propagatesBackward() {
    // Tour 1 : S1->A1 (reward=0), Tour 2 : S2->A2 (reward=10)
    // L'apprentissage se fait à l'envers (Tour 2 d'abord, puis Tour 1)
    qLearner.learnFromLocalExperience(0, Map.of("S1", "A1"));  // tour 1
    qLearner.learnFromLocalExperience(10, Map.of("S2", "A2")); // tour 2

    qLearner.learnFromExperience(0);

    double qS2 = qLearner.getQTable().get("S2").get("A2");
    double qS1 = qLearner.getQTable().get("S1").get("A1");

    // Tour 2 (dernier) : endGameFactor = discountFactor^1 = 0.5
    // newQ(S2,A2) = 0 + 0.1 * (10 + 0.5*0 - 0) = 1.0
    assertThat(qS2).isCloseTo(1.0, within(0.001));

    // Tour 1 : endGameFactor = discountFactor^2 = 0.25
    // newQ(S1,A1) = 0 + 0.1 * (0 + 0.25*0 - 0) = 0.0 (reward=0, finalScore=0)
    assertThat(qS1).isCloseTo(0.0, within(0.001));
  }

  @Test
  void learnFromExperience_withFinalScore_propagatesCorrectly() {
    // Tour 1 : reward = 0, finalScore = 100
    // endGameFactor tour 1 : discountFactor^2 = 0.25 (2 tours, traité en 2e)
    // endGameFactor tour 2 : discountFactor^1 = 0.5 (dernier tour, traité en 1er)
    qLearner.learnFromLocalExperience(0, Map.of("S1", "A1"));  // tour 1
    qLearner.learnFromLocalExperience(0, Map.of("S2", "A2")); // tour 2

    qLearner.learnFromExperience(100);

    double qS2 = qLearner.getQTable().get("S2").get("A2");
    double qS1 = qLearner.getQTable().get("S1").get("A1");

    double expectedScoreS2 = 0 + QLearner.DEFAULT_LEARNING_RATE * (0 + QLearner.DEFAULT_DISCOUNT_FACTOR * 100 - 0);
    double expectedScoreS1 = 0 + QLearner.DEFAULT_LEARNING_RATE * (0 + Math.pow(QLearner.DEFAULT_DISCOUNT_FACTOR, 2) * 100 - 0);

    // newQ(S2,A2) = 0 + 0.1 * (0 + 0.5*100 - 0) = 5.0
    assertThat(qS2).isCloseTo(expectedScoreS2, within(0.001));
    // newQ(S1,A1) = 0 + 0.1 * (0 + 0.25*100 - 0) = 2.5
    assertThat(qS1).isCloseTo(expectedScoreS1, within(0.001));
  }

  @Test
  void decayEpsilon_reducesEpsilonByDecayFactor() {
    double initialEpsilon = qLearner.getEpsilon();

    qLearner.decayEpsilon();

    assertThat(qLearner.getEpsilon()).isCloseTo(initialEpsilon * QLearner.EPSILON_DECAY, within(0.0001));
  }
}
