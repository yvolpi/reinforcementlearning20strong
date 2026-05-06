package model.ai;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

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
    QLearner learner = new QLearner(0.1, 0.95, 0.0); // epsilon = 0
    assertThat(learner.shouldExplore(0.5)).isFalse();
  }

  @Test
  void getActionValues_unknownState_returnsEmptyMap() {
    assertThat(qLearner.getActionValues("UNKNOWN_STATE")).isEmpty();
  }

  @Test
  void learnFromExperience_updatesQTableCorrectly() {
    // Ajout d'une expérience simple : reward = 10, pas de futur
    qLearner.getExperiences().add(new Experience("S1", "A1", 10.0, "S2"));

    qLearner.learnFromExperience();

    // newQ = 0 + 0.1 * (10 + 0.95*0 - 0) = 1.0
    double qValue = qLearner.getQTable().get("S1").get("A1");
    assertThat(qValue).isCloseTo(1.0, within(0.001));
  }

  @Test
  void learnFromExperience_clearsExperiencesAfterLearning() {
    qLearner.getExperiences().add(new Experience("S1", "A1", 5.0, "S2"));

    qLearner.learnFromExperience();

    assertThat(qLearner.getExperiences()).isEmpty();
  }

  @Test
  void learnFromExperience_chainedRewards_propagatesBackward() {
    // Deux expériences : S1->A1 (reward=0), S2->A2 (reward=10)
    // L'ordre dans la liste est chronologique, l'apprentissage se fait à l'envers
    qLearner.getExperiences().add(new Experience("S1", "A1", 0.0, "S2"));
    qLearner.getExperiences().add(new Experience("S2", "A2", 10.0, "S3"));

    qLearner.learnFromExperience();

    double qS2 = qLearner.getQTable().get("S2").get("A2");
    double qS1 = qLearner.getQTable().get("S1").get("A1");

    assertThat(qS2).isCloseTo(1.0, within(0.001)); // 0 + 0.1*(10+0.95*0-0) = 1.0
    assertThat(qS1).isGreaterThan(0.0); // propagation de la future récompense
  }

  @Test
  void decayEpsilon_reducesEpsilonByDecayFactor() {
    double initialEpsilon = qLearner.getEpsilon();

    qLearner.decayEpsilon();

    assertThat(qLearner.getEpsilon()).isCloseTo(initialEpsilon * (2.0 / 3.0), within(0.0001));
  }
}
