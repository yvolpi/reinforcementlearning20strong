package model.ai;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import model.Dice;
import model.GameState;
import model.elements.GameAction;
import model.elements.GamePhase;
import model.ennemis.Ennemi;
import model.items.Item;
import recompenses.Reward;

/**
 * Façade de l'IA : orchestre l'encodage, la sélection d'actions et l'apprentissage Q-learning.
 */
public class GameAi {

  private final QLearner learner;
  private final GameStateEncoder encoder;
  private final ActionSelector selector;
  private final ActionFactory factory;
  private final Random random;

  private List<GameAction> lastActions;

  // ===== Constructeurs =====

  public GameAi(Long seed) {
    this(new QLearner(), seed);
  }

  private GameAi(QLearner learner, Long seed) {
    this.learner  = learner;
    this.encoder  = new GameStateEncoder();
    this.random   = new Random(seed);
    this.selector = new ActionSelector(random, encoder);
    this.factory  = new ActionFactory();
    this.lastActions = new ArrayList<>();
  }

  // ===== Décisions =====

  public GameAction choosePileToActivate(List<Ennemi> availableEnemies, GameState gameState) {
    if (availableEnemies.isEmpty()) {
      throw new IllegalArgumentException("Aucun ennemi disponible pour activation");
    }
    GameAction action;
    if (learner.shouldExplore(random.nextDouble())) {
      action = selector.exploreActivateAction(availableEnemies);
    } else {
      String state = encoder.encodeStateWithPile(gameState);
      List<GameAction> possible = factory.createPossibleActivateActions(availableEnemies);
      action = selector.findBestActivateAction(possible, learner.getActionValues(state));
      if (action == null) action = selector.exploreActivateAction(availableEnemies);
    }
    lastActions = List.of(action);
    return action;
  }

  public List<GameAction> chooseActionsToEngage(List<Dice> availableDice,
      List<Ennemi> activeEnnemis,
      GameState gameState) {
    if (learner.shouldExplore(random.nextDouble())) {
      return selector.exploreEngageActions(availableDice);
    }
    String state = encoder.encodeStateForEngage(gameState);
    List<GameAction> possible = factory.createPossibleEngageActions(availableDice);
    GameAction best = selector.findBestEngageAction(possible, learner.getActionValues(state));
    if (best == null) return selector.selectRandomEngageAction(availableDice);
    return List.of(best);
  }

  public List<GameAction> chooseActionsToAssign(List<Dice> assignableDice,
      List<Ennemi> activeEnnemis,
      GameState gameState) {
    List<GameAction> actions;
    if (learner.shouldExplore(random.nextDouble())) {
      //System.out.println("Exploration : assignation aléatoire");
      // Si c'est la dernière phase d'assignation du tour, on assigne tout
      boolean lastAssignPhase = gameState.getEngageAssignStep() == gameState.getPlayer().getStrategy();
      if (lastAssignPhase) {
        actions = new ArrayList<>();
        List<Ennemi> aliveEnnemis = activeEnnemis.stream()
            .filter(e -> !e.isDefeatedFlag())
            .toList();
        if (!aliveEnnemis.isEmpty()) {
          //System.out.println("Assignation naïve : " + assignableDice.size() + " dés à assigner sur " + aliveEnnemis.size() + " ennemis actifs");
          for (Dice dice : assignableDice) {
            // Choix naïf : tous sur le premier ennemi actif (à adapter selon ta logique)
            actions.add(new GameAction(GamePhase.ASSIGN_DICE, dice, aliveEnnemis.get(
                random.nextInt(aliveEnnemis.size()
                ))));
          }
        }

        lastActions = actions;
        return actions;
      }
      actions = selector.exploreAssignActions(assignableDice, activeEnnemis);

    } else {
      //System.out.println("Exploitation : assignation basée sur Q-values");
      String state = encoder.encodeStateForAssign(gameState);
      List<GameAction> possible = factory.createPossibleAssignActions(assignableDice, activeEnnemis);
      GameAction best = selector.findBestAssignAction(possible, learner.getActionValues(state));
      actions = best != null ? List.of(best) : new ArrayList<>();
    }
    lastActions = actions;
    return actions;
  }

  public List<GameAction> chooseActionsToUse(List<Item> usableItems, GameState gameState) {
    List<GameAction> actions;
    if (learner.shouldExplore(random.nextDouble())) {
      actions = selector.exploreUseActions(usableItems, gameState.getPhase());
    } else {
      String state = encoder.encodeState(gameState);
      List<GameAction> possible = factory.createPossibleUseActions(usableItems, gameState);
      GameAction best = selector.findBestAssignAction(possible, learner.getActionValues(state));
      actions = best != null ? List.of(best) : new ArrayList<>();
    }
    lastActions = actions;
    return actions;
  }

  public Map<Dice, Ennemi> chooseDiceAssignment(List<Dice> engagedDice, List<Ennemi> activeEnnemis) {
    Map<Dice, Ennemi> assignments = new HashMap<>();
    if (activeEnnemis.isEmpty()) return assignments;
    Ennemi targetEnemy = activeEnnemis.getFirst();
    for (Dice dice : engagedDice) {
      assignments.put(dice, targetEnemy);
    }
    return assignments;
  }

  public List<Dice> chooseDiceToRecover(List<Dice> exhaustedDice, int maxToRecover) {
    return exhaustedDice.stream()
        .sorted(Comparator.comparingInt(d -> -selector.getDiceColorPriority(d.getColor())))
        .limit(maxToRecover)
        .collect(Collectors.toList());
  }

  public GameAction chooseItemToRemove(GameState gameState, Reward reward) {

    GameAction action;
    List<Item> items = gameState.getPlayer().getItems();

    if (learner.shouldExplore(random.nextDouble())) {
      action = selector.exploreItemToThrowAction(items);
    } else {
      String state = encoder.encodeStateForItemsManagement(gameState, reward);
      List<GameAction> possibleItemsToRemove = factory.createPossibleRemoveItemActions(items);
      action = selector.findBestActivateAction(possibleItemsToRemove, learner.getActionValues(state));
      if (action == null) action = selector.exploreItemToThrowAction(items);
    }
    lastActions = List.of(action);
    return action;
  }

  // ===== Apprentissage =====

  public void learnFromExperience() {
    learner.learnFromExperience();
  }

  public void decayEpsilon() {
    learner.decayEpsilon();
  }

  // ===== Encodage (délégation pour usage externe) =====

  public String encodeState(GameState gameState) {
    return encoder.encodeState(gameState);
  }

  public String encodeStateForEngage(GameState gameState) {
    return encoder.encodeStateForEngage(gameState);
  }

  public String encodeStateForAssign(GameState gameState) {
    return encoder.encodeStateForAssign(gameState);
  }

  public String encodeStateWithPile(GameState gameState) {
    return encoder.encodeStateWithPile(gameState);
  }

  public String encodeStateForItemsManagement(GameState gameState, Reward reward) {
    return encoder.encodeStateForItemsManagement(gameState, reward);
  }

  // ===== Getters =====

  public List<Experience> getExperiences()                         { return learner.getExperiences(); }
  public List<GameAction> getLastActions()                   { return lastActions; }
  public Map<String, Map<String, Double>> getQTable()              { return learner.getQTable(); }
}
