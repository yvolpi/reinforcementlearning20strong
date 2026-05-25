package model.ai;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import model.Dice;
import model.DiceState;
import model.GameState;
import model.effets.ennemi.EnnemyEffect;
import model.elements.GameAction;
import model.elements.GamePhase;
import model.ennemis.Ennemi;
import model.items.Item;
import model.random.CustomRandom;
import model.recompenses.Reward;

/**
 * Façade de l'IA : orchestre l'encodage, la sélection d'actions et l'apprentissage Q-learning.
 */
public class GameAi {

  private final QLearner learner;
  private final GameStateEncoder encoder;
  private final ActionSelector selector;
  private final ActionFactory factory;
  private final CustomRandom random;

  private List<GameAction> lastActions;
  private List<List<GameAction>> actionsInTurn;
  private Map<String, String> mapEncodedStatesAndActionsThisTurn;

  // ===== Constructeurs =====

  public GameAi(CustomRandom random) {
    this(new QLearner(), random);
  }

  private GameAi(QLearner learner, CustomRandom random) {
    this.learner  = learner;
    this.encoder  = new GameStateEncoder();
    this.random   = random;
    this.selector = new ActionSelector(random, encoder);
    this.factory  = new ActionFactory();
    this.lastActions = new ArrayList<>();
    this.actionsInTurn = new ArrayList<>();
    mapEncodedStatesAndActionsThisTurn = new HashMap<>();
  }

  // ===== Décisions =====

  public GameAction decidePileForM10(GameState gameState) {
    String encodedState = encoder.encodeStateForM10(gameState);
    GameAction action;
    List<GameAction> possible = new ArrayList<>();
    if (!gameState.getPile1().isEmpty()) {
      possible.add(new GameAction(1));
    }
    if (!gameState.getPile2().isEmpty()) {
      possible.add(new GameAction(2));
    }
    if (!gameState.getPile3().isEmpty()) {
      possible.add(new GameAction(3));
    }
    if (learner.shouldExplore(random.nextDouble())) {
      action = possible.get(random.nextInt(possible.size()));
    } else {

      action = selector.findBestDecidePileM10Action(possible, learner.getActionValues(encodedState));
      if (action == null) action = possible.get(random.nextInt(possible.size()));
    }

    mapEncodedStatesAndActionsThisTurn.put(encodedState, ActionKeyEncoder.encodeDecidePileM10Action(action));
    return action;
  }

  public GameAction decideBossActivation(GameState gameState) {
    String encodedState = encoder.encodeStateWithBoss(gameState);
    GameAction action;
    if (learner.shouldExplore(random.nextDouble())) {
      action = new GameAction(random.nextDouble() < 0.5);
    } else {
      List<GameAction> possible = List.of(new GameAction(true), new GameAction(false));
      action = selector.findBestDecideActivateBossAction(possible, learner.getActionValues(encodedState));
      if (action == null) action = new GameAction(random.nextDouble() < 0.5);
    }

    mapEncodedStatesAndActionsThisTurn.put(encodedState, ActionKeyEncoder.encodeActivateBossAction(action));
    return action;
  }

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
      actions = selector.exploreEngageActions(availableDice, gameState);
    } else {
      String bestLearnedEngageActions = learner.getBestLearnedActionsFromState(state);
      if (bestLearnedEngageActions == null) {
        actions = selector.exploreEngageActions(availableDice, gameState);
      } else {
        actions = ActionDecoder.decodeEngageAction(availableDice, bestLearnedEngageActions);
      }
    }

    String encodedActions = ActionKeyEncoder.encodeEngageAction(actions, gameState.getEngageAssignStep());
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
        actions = selector.exploreAssignActions(gameState, assignableDice, activeEnnemis, 1.0);
      } else {
        actions = selector.exploreAssignActions(gameState, assignableDice, activeEnnemis, 0.5);
      }

    } else {
      // Exploitation
      String bestLearnedAssignActions = learner.getBestLearnedActionsFromState(state);
      if (bestLearnedAssignActions == null) {
        // Si aucune action apprise, fallback sur assignation naïve
        if (lastAssignPhase) {
          actions = selector.exploreAssignActions(gameState, assignableDice, activeEnnemis, 1.0);
        } else {
          actions = selector.exploreAssignActions(gameState, assignableDice, activeEnnemis, 0.5);
        }
      } else {
        actions = ActionDecoder.decodeAssignActions(assignableDice, bestLearnedAssignActions, activeEnnemis);
        //vérifier qu'on n'assigne pas 2 fois le même dé
        Set<Dice> assignedDice = actions.stream()
            .map(GameAction::getDice)
            .collect(Collectors.toSet());
        if (assignedDice.size() != actions.size()) {
          throw new IllegalStateException("Doublon de dé détecté dans assignActions : " + actions);
        }
      }
    }

    lastActions = actions;
    String encodedActions = ActionKeyEncoder.encodeAssignActions(actions, gameState.getEngageAssignStep());
    mapEncodedStatesAndActionsThisTurn.put(state, encodedActions);
    return actions;
  }

  public List<GameAction> chooseActionsToUse(List<Item> usableItems, GameState gameState) {
    List<GameAction> actions;
    String state = encoder.encodeGlobalState(gameState);
    if (learner.shouldExplore(random.nextDouble())) {
      // exploration
      actions = selector.exploreUseActions(usableItems, gameState.getPhase());
    } else {
      String bestComboUseItems = learner.getBestLearnedActionsFromState(state);
      if (bestComboUseItems == null) {
        // exploration
        actions = selector.exploreUseActions(usableItems, gameState.getPhase());
      } else {
        // décoder bestComboUseItems
        actions = ActionDecoder.decodeUseActions(usableItems, bestComboUseItems, gameState.getPhase());
      }
    }
    lastActions = actions;
    String encodedActions = ActionKeyEncoder.encodeUseItemsAction(actions, gameState.getPhase());
    mapEncodedStatesAndActionsThisTurn.put(state, encodedActions);
    return actions;
  }

  public List<Dice> chooseDiceToRecover(List<Dice> exhaustedDice, int maxToRecover) {
    return exhaustedDice.stream()
        .sorted(Comparator.comparingInt(d -> -selector.getDiceColorPriority(d.getColor())))
        .limit(maxToRecover)
        .collect(Collectors.toList());
  }

  public GameAction chooseItemToRemove(GameState gameState, Reward reward) {

    String state = encoder.encodeStateForItemsManagement(gameState, reward);
    GameAction action;
    List<Item> items = gameState.getPlayer().getItems();

    if (learner.shouldExplore(random.nextDouble())) {
      action = selector.exploreItemToThrowAction(items);
    } else {
      List<GameAction> possibleItemsToRemove = factory.createPossibleRemoveItemActions(items);
      action = selector.findBestActivateAction(possibleItemsToRemove, learner.getActionValues(state));
      if (action == null) action = selector.exploreItemToThrowAction(items);
    }
    lastActions = List.of(action);
    String encodedActions = ActionKeyEncoder.encodeRemoveItemAction(action);
    mapEncodedStatesAndActionsThisTurn.put(state, encodedActions);
    return action;
  }

  // ===== Décisions lors de l'usage de certains objets =====

  // Lunoculation
  public GameAction chooseEnnemiEffectToDesactivate(GameState gameState) {
    GameAction action;
    String state = encoder.encodeStateForLunoculation(gameState);

    if (learner.shouldExplore(random.nextDouble())) {
      action = selector.exploreLunoculationEffectAction(gameState.getActiveEnnemis());
    } else {
      List<GameAction> possibleEffectsToDesactivate = factory.createPossibleDesactivateEffectActions(gameState.getActiveEnnemis());
      action = selector.findBestLunoculationAction(possibleEffectsToDesactivate, learner.getActionValues(state));
    }

    String encodedActions = ActionKeyEncoder.encodeLunoculationAction(action);
    mapEncodedStatesAndActionsThisTurn.put(state, encodedActions);
    return action;
  }

  public GameAction chooseNonMandatoryEnnemiToDrop(List<Ennemi> nonMandatoryEnnemis, GameState gameState) {
    GameAction action;
    String state = encoder.encodeStateForDropEnnemi(gameState);
    if (learner.shouldExplore(random.nextDouble())) {
      action = selector.exploreDropNonMandatoryEnnemiAction(nonMandatoryEnnemis);
    } else {
      List<GameAction> possibleActions = factory.createPossibleDropNonMandatoryEnnemiActions(nonMandatoryEnnemis);
      action = selector.findBestDropNonMandatoryEnnemiAction(possibleActions, learner.getActionValues(state));
      if (action == null) action = selector.exploreDropNonMandatoryEnnemiAction(nonMandatoryEnnemis);
    }
    String encodedActions = ActionKeyEncoder.encodeDropNonMandatoryEnnemiAction(action);
    mapEncodedStatesAndActionsThisTurn.put(state, encodedActions);
    return action;
  }

  // ===== Apprentissage =====

  public void learnFromExperience(int finalScore) {
    learner.learnFromExperience(finalScore);
  }

  public void learnFromTurnExperience(int turnExp) {
    learner.learnFromLocalExperience(turnExp, mapEncodedStatesAndActionsThisTurn);
  }

  public void decayEpsilon() {
    learner.decayEpsilon();
  }

  public void updateBestGame() {
    learner.updateBestExperience();
  }

  public void resetMapEncodedStatesAndActionsThisTurn() {
    mapEncodedStatesAndActionsThisTurn = new HashMap<>();
  }

  // ===== Getters =====

  public QLearner getLearner() { return learner; }


}
