package model;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import model.effets.bonus.BonusEffect;
import model.effets.ennemi.EnnemyEffect;
import model.elements.GamePhase;
import model.elements.GameService;
import model.ennemis.Ennemi;
import model.random.CustomRandom;

/**
 * Représente l'état complet du jeu à un instant donné.
 */
public class GameState implements Cloneable {

  // ===== État du joueur =====
  private Player player;

  // ===== Zones de dés =====
  private List<Dice> dicePool;           // Dés en réserve
  private List<Dice> engagedDices;       // Dés engagés ce tour
  private List<Dice> exhaustedDices;     // Dés épuisés

  // ===== Piles d'ennemis =====
  private Deque<Ennemi> pile1;
  private Deque<Ennemi> pile2;
  private Deque<Ennemi> pile3;
  private Queue<Ennemi> bossPile; // (optionnel, si tu veux gérer un boss séparément)
  private List<Ennemi> activeEnnemis;
  private int nbEnnemisKilled;
  private int nbEnnemisToAvtivate;
  private int wastedDiceThisTurn;
  private boolean penalityKillCivilAsserviFirst;
  private boolean revealedBoss;
  private boolean activatedBoss;
  private boolean bosskilled;

  // ===== État du tour =====
  private GamePhase phase;
  private int engageAssignStep;
  private int maxEngagedDicePerTurn;
  private List<BonusEffect> bonusEffectsTurn;

  // ===== Générateur pseudo-aléatoire =====
  private final CustomRandom random;

  // ===== Constructeur =====

  public GameState(Player player, List<Dice> dicePool,
      Deque<Ennemi> pile1, Deque<Ennemi> pile2, Deque<Ennemi> pile3,
      Queue<Ennemi> bossPile,
      CustomRandom random) {
    this.player = player;
    this.dicePool = dicePool;
    maxEngagedDicePerTurn = dicePool.size();
    bonusEffectsTurn = new ArrayList<>();
    this.pile1 = pile1;
    this.pile2 = pile2;
    this.pile3 = pile3;
    this.bossPile = bossPile;
    revealedBoss = false;
    activatedBoss = false;
    bosskilled = false;

    this.random = random;

    this.engagedDices = new ArrayList<>();
    this.exhaustedDices = new ArrayList<>();
    this.activeEnnemis = new ArrayList<>();
    this.phase = GamePhase.ACTIVATE_PILE;
    this.engageAssignStep = 0;
  }

  // ===== Clonage =====

  @Override
  public GameState clone() {
    List<Dice> clonedDicePool = cloneDiceList(dicePool);

    GameState clone = new GameState(
        player.clone(),
        clonedDicePool,
        new LinkedList<>(pile1),
        new LinkedList<>(pile2),
        new LinkedList<>(pile3),
        new LinkedList<>(bossPile),
        random
    );

    clone.engagedDices = cloneDiceList(engagedDices);
    clone.exhaustedDices = cloneDiceList(exhaustedDices);
    clone.activeEnnemis = new ArrayList<>(activeEnnemis);
    clone.phase = phase;
    clone.engageAssignStep = engageAssignStep;

    return clone;
  }

  private List<Dice> cloneDiceList(List<Dice> original) {
    return original.stream()
        .map(Dice::clone)
        .collect(Collectors.toList());
  }

  // ===== Gestion des ennemis actifs =====

  public void addActiveEnnemi(Ennemi ennemi) {
    activeEnnemis.add(ennemi);
  }

  public List<Ennemi> getMandatoryEnnemies() {
    List<Ennemi> mandatory = new ArrayList<>();

    addIfMandatory(pile1.peek(), mandatory);
    addIfMandatory(pile2.peek(), mandatory);
    addIfMandatory(pile3.peek(), mandatory);

    return mandatory;
  }

  private void addIfMandatory(Ennemi ennemi, List<Ennemi> list) {
    if (ennemi != null && ennemi.isForcedActivationMandatory()) {
      list.add(ennemi);
    }
  }

  public List<Ennemi> getFirstEnemiesOfEachNonEmptyPile() {
    List<Ennemi> result = new ArrayList<>();

    if (!pile1.isEmpty()) result.add(pile1.peek());
    if (!pile2.isEmpty()) result.add(pile2.peek());
    if (!pile3.isEmpty()) result.add(pile3.peek());

    return result;
  }

  // ===== Conditions de fin =====

  public boolean isVictory() {
    return bosskilled;
  }

  public boolean isDefeat() {
    return player.getLife() <= 0;
  }

  // ===== Utilitaires pour les dés =====

  public List<Dice> getAvailableDiceToEngage() {
    Set<Dice> set = new HashSet<>(dicePool);
    if (set.size() != dicePool.size()) {
      throw new IllegalStateException("Doublon détecté dans dicePool : " + dicePool);
    }
    return dicePool.stream()
        .filter(dice -> dice.getState() == DiceState.RESERVE)
        .filter(dice -> GameService.isDiceAutorized(dice, activeEnnemis))
        .collect(Collectors.toList());
  }

  public List<Dice> getAvailableDiceToAssign() {
    // dés engagés non assignés qui font au moins 1 dégât

    return engagedDices.stream()
        .filter(dice -> dice.getState() == DiceState.ENGAGE)
        .filter(dice -> dice.getLastRoll() >= 1)
        .filter(this::isDiceAssignable)
        .collect(Collectors.toList());
  }

  private boolean isDiceAssignable(Dice dice) {
    for (Ennemi ennemi : activeEnnemis) {
      if (!ennemi.isDefeatedFlag()) {
        for (EnnemyEffect effect : ennemi.getEffects()) {
          if (effect.isActivated() && !effect.isDiceAssignable(this, dice)) {
            return false;
          }
        }
      }
    }
    // Aucun effet n'interdit l'assignation
    return true;
  }


  public Ennemi getActiveEnnemi(int pileNumber) {
    return switch (pileNumber) {
      case 1 -> pile1.peek();
      case 2 -> pile2.peek();
      case 3 -> pile3.peek();
      default -> throw new IllegalArgumentException("Numéro de pile invalide : " + pileNumber);
    };
  }

  public boolean canActiveBoss() {
    return pile1.isEmpty() || pile2.isEmpty() || pile3.isEmpty();
  }

  public boolean forceActiveBoss() {
    return pile1.isEmpty() && pile2.isEmpty() && pile3.isEmpty();
  }

  // ===== Getters =====

  public Player getPlayer() {
    return player;
  }

  public List<Dice> getDicePool() {
    return dicePool;
  }

  public List<Dice> getEngagedDices() {
    return engagedDices;
  }

  public List<Dice> getExhaustedDice() {
    return exhaustedDices;
  }

  public Deque<Ennemi> getPile1() {
    return pile1;
  }

  public Deque<Ennemi> getPile2() {
    return pile2;
  }

  public Deque<Ennemi> getPile3() {
    return pile3;
  }

  public Queue<Ennemi> getBossPile() {
    return bossPile;
  }

  public List<Ennemi> getActiveEnnemis() {
    return activeEnnemis;
  }

  public GamePhase getPhase() {
    return phase;
  }

  public int getEngageAssignStep() {
    return engageAssignStep;
  }

  public CustomRandom getRandom() {
    return random;
  }

  // ===== Setters =====

  public void setPhase(GamePhase phase) {
    this.phase = phase;
  }

  public void setEngageAssignStep(int step) {
    this.engageAssignStep = step;
  }

  public void incrementNdEnnemisKilled() {
    this.nbEnnemisKilled++;
  }

  public int getNbEnnemisKilled() {
    return nbEnnemisKilled;
  }

  public void resetNdEnnemisKilled() {
    this.nbEnnemisKilled = 0;
  }

  public int getNbEnnemisToAvtivate() {
    return nbEnnemisToAvtivate;
  }

  public void setNbEnnemisToActivate(int nb) {
    this.nbEnnemisToAvtivate = nb;
  }

  public void setWastedDiceThisTurn(int n) { this.wastedDiceThisTurn = n; }

  public int getWastedDiceThisTurn() { return wastedDiceThisTurn; }

  public boolean isPenalityKillCivilAsserviFirst() {
    return penalityKillCivilAsserviFirst;
  }

  public void setPenalityKillCivilAsserviFirst(boolean penalityKillCivilAsserviFirst) {
    this.penalityKillCivilAsserviFirst = penalityKillCivilAsserviFirst;
  }

  public boolean isRevealedBoss() {
    return revealedBoss;
  }

  public void setRevealedBoss(boolean revealedBoss) {
    this.revealedBoss = revealedBoss;
  }

  public boolean isActivatedBoss() {
    return activatedBoss;
  }

  public void setActivatedBoss(boolean activatedBoss) {
    this.activatedBoss = activatedBoss;
  }

  public boolean isBosskilled() {
    return bosskilled;
  }

  public void setBosskilled(boolean bosskilled) {
    this.bosskilled = bosskilled;
  }

  public int getMaxEngagedDicePerTurn() {
    return maxEngagedDicePerTurn;
  }

  public void setMaxEngagedDicePerTurn(int maxEngagedDicePerTurn) {
    this.maxEngagedDicePerTurn = maxEngagedDicePerTurn;
  }

  public List<BonusEffect> getBonusEffectsTurn() {
    return bonusEffectsTurn;
  }

  public void addBonusEffectTurn(BonusEffect effect) {
    this.bonusEffectsTurn.add(effect);
  }

  public boolean atLeastOneEnnemiOnPiles() {
    return !pile1.isEmpty() || !pile2.isEmpty() || !pile3.isEmpty();
  }

  public Ennemi getActivatedBoss() {
    return activeEnnemis.stream()
        .filter(e -> e.getClassValue() == 3)
        .findFirst()
        .orElse(null);
  }

  public boolean allActiveEnnemisDefeated() {
    return getActiveEnnemis()
        .stream().allMatch(Ennemi::isDefeatedFlag);
  }


  // ===== Représentation textuelle =====

  @Override
  public String toString() {
    return String.format(
        "GameState{phase=%s, PV=%d, réserve=%d, épuisés=%d, ennemisActifs=%d}",
        phase,
        player.getLife(),
        dicePool.size(),
        exhaustedDices.size(),
        activeEnnemis.size()
    );
  }

  public void setNbEnnemisKilled(int nbEnnemisKilled) {
    this.nbEnnemisKilled = nbEnnemisKilled;
  }

  public void checkIfErrorBetweenPoolAndEngagedAndExhaustedDice() {
    Set<Dice> poolSet = new HashSet<>(dicePool);
    Set<Dice> exhaustedSet = new HashSet<>(exhaustedDices);
    Set<Dice> engagedSet = new HashSet<>(engagedDices);
    if (poolSet.size() != dicePool.size()) {
      throw new IllegalStateException("Doublon détecté dans dicePool : " + dicePool);
    }
    if (exhaustedSet.size() != exhaustedDices.size()) {
      throw new IllegalStateException("Doublon détecté dans exhaustedDices : " + exhaustedDices);
    }
    if (engagedSet.size() != engagedDices.size()) {
      throw new IllegalStateException("Doublon détecté dans engagedDices : " + engagedDices);
    }


    for (Dice d : exhaustedDices) {
      if (poolSet.contains(d)) {
        throw new IllegalStateException("Dé trouvé à la fois dans dicePool et exhaustedDices : " + d);
      }
      if (engagedDices.contains(d)) {
        throw new IllegalStateException("Dé trouvé à la fois dans engagedDices et exhaustedDices : " + d);
      }
    }
    for (Dice d : engagedDices) {
      if (exhaustedDices.contains(d)) {
        throw new IllegalStateException("Dé trouvé à la fois dans engagedDices et exhaustedDices : " + d);
      }
    }
  }
}
