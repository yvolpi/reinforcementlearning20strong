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
  private List<List<GameAction>> actionsInTurn;
  private List<List<String>> encodedActionsInTurn;
  private Map<String, String> mapEncodedStatesAndActionsThisTurn;

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
    this.actionsInTurn = new ArrayList<>();
    this.encodedActionsInTurn = new ArrayList<>();
    mapEncodedStatesAndActionsThisTurn = new HashMap<>();
  }

  // ===== Décisions =====

  public GameAction choosePileToActivate(List<Ennemi> availableEnemies, GameState gameState) {
    if (availableEnemies.isEmpty()) {
      throw new IllegalArgumentException("Aucun ennemi disponible pour activation");
    }
    String encodedState = encoder.encodeStateWithPile(gameState);
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
    actionsInTurn.add(List.of(action));
    encodedActionsInTurn.add(List.of(ActionKeyEncoder.encodeActivateAction(action)));
    mapEncodedStatesAndActionsThisTurn.put(encodedState, ActionKeyEncoder.encodeActivateAction(action));
    return action;
  }

  public List<GameAction> chooseActionsToEngage(List<Dice> availableDice,
      List<Ennemi> activeEnnemis,
      GameState gameState) {
    List<GameAction> actions;
    String state = encoder.encodeStateForEngage(gameState);

    // Exploration
    if (learner.shouldExplore(random.nextDouble())) {
      actions = selector.exploreEngageActions(availableDice);
    } else {
      String bestLearnedEngageActions = learner.getBestLearnedActionsFromState(state);
      if (bestLearnedEngageActions == null) {
        actions = selector.exploreEngageActions(availableDice);
      } else {
        actions = ActionDecoder.decodeEngageAction(availableDice, bestLearnedEngageActions);
      }
    }

    String encodedActions = encoder.encodeEngageAction(actions, gameState.getEngageAssignStep());
    mapEncodedStatesAndActionsThisTurn.put(state, encodedActions);

    return actions;
  }

  public List<GameAction> chooseActionsToAssign(List<Dice> assignableDice,
      List<Ennemi> activeEnnemis,
      GameState gameState) {
    List<GameAction> actions;
    String state = encoder.encodeStateForAssign(gameState);

    boolean lastAssignPhase = gameState.getEngageAssignStep() == gameState.getPlayer().getStrategy();

    if (learner.shouldExplore(random.nextDouble())) {
      // Exploration
      if (lastAssignPhase) {
        actions = naiveAssignAllDice(assignableDice, activeEnnemis);
      } else {
        actions = selector.exploreAssignActions(assignableDice, activeEnnemis);
      }
    } else {
      // Exploitation
      String bestLearnedAssignActions = learner.getBestLearnedActionsFromState(state);
      if (bestLearnedAssignActions == null) {
        // Si aucune action apprise, fallback sur assignation naïve
        if (lastAssignPhase) {
          actions = naiveAssignAllDice(assignableDice, activeEnnemis);
        } else {
          actions = selector.exploreAssignActions(assignableDice, activeEnnemis);
        }
      } else {
        actions = ActionDecoder.decodeAssignActions(assignableDice, bestLearnedAssignActions, activeEnnemis);
      }
    }

    lastActions = actions;
    String encodedActions = encoder.encodeAssignActions(actions, gameState.getEngageAssignStep());
    mapEncodedStatesAndActionsThisTurn.put(state, encodedActions);
    return actions;
  }

  private List<GameAction> naiveAssignAllDice(List<Dice> assignableDice, List<Ennemi> activeEnnemis) {
    List<GameAction> actions = new ArrayList<>();
    List<Ennemi> aliveEnnemis = activeEnnemis.stream()
        .filter(e -> !e.isDefeatedFlag())
        .toList();
    if (!aliveEnnemis.isEmpty()) {
      for (Dice dice : assignableDice) {
        actions.add(new GameAction(GamePhase.ASSIGN_DICE, dice, aliveEnnemis.get(
            random.nextInt(aliveEnnemis.size())
        )));
      }
    }
    return actions;
  }

  public List<GameAction> chooseActionsToUse(List<Item> usableItems, GameState gameState) {
    List<GameAction> actions;
    if (learner.shouldExplore(random.nextDouble())) {
      actions = selector.exploreUseActions(usableItems, gameState.getPhase());
    } else {
      String state = encoder.encodeState(gameState);
      List<GameAction> possible = factory.createPossibleUseActions(usableItems, gameState);
      GameAction best = selector.findBestUseItemAction(possible, learner.getActionValues(state));
      actions = best != null ? List.of(best) : new ArrayList<>();
    }
    lastActions = actions;
    return actions;
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

  public void learnFromTurnExperience(int turnExp) {
    learner.learnFromLocalExperience(turnExp, mapEncodedStatesAndActionsThisTurn);
  }

  public void decayEpsilon() {
    learner.decayEpsilon();
  }

  // ===== Encodage (délégation pour usage externe) =====

  public String encodeState(GameState gameState) {
    return encoder.encodeState(gameState);
  }

  public String encodeGlobalState(GameState gameState) {
    return encoder.encodeGlobalState(gameState);
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
  public QLearner getLearner() { return learner; }
  public void resetActionsInTurn() { this.actionsInTurn = new ArrayList<>(); }

}
