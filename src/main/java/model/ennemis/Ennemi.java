package model.ennemis;

import java.util.ArrayList;
import java.util.List;

import model.Dice;
import model.GameState;
import model.effets.bonus.BonusEffect;
import model.effets.ennemi.EnnemyEffect;
import model.recompenses.Reward;

/**
 * Représente un ennemi dans le jeu 20 Strong.
 */
public class Ennemi {

  private final String name;
  private final int classValue;
  private final int life;
  private final int attack;
  private final List<EnnemyEffect> effects;
  private final int forcedActivations;
  private final boolean forcedActivationMandatory;
  private final Reward reward;
  private final List<Dice> assignedDice;

  private int pileNumber;
  private boolean defeated;
  private int currentLife;

  // ===== Constructeur =====

  public Ennemi(EnnemiType type, int pileNumber) {
    this.classValue = type.classValue;
    this.name = type.name;
    this.life = type.life;
    currentLife = type.life;
    this.attack = type.attack;
    this.effects = type.effets;
    this.forcedActivations = type.forcedActivations;
    this.forcedActivationMandatory = type.forcedActivationMandatory;
    this.reward = type.getReward();
    this.pileNumber = pileNumber;

    this.assignedDice = new ArrayList<>();
    this.defeated = false;
  }

  // ===== Logique de combat =====

  /**
   * Calcule si l'ennemi est vaincu en fonction des dégâts assignés.
   */

  public boolean isDefeated(GameState gameState) {
    return currentLife <= 0;
  }

  public void assignDice(Dice dice) {
    assignedDice.add(dice);
  }

  public void unassignDice(Dice dice) {
    assignedDice.remove(dice);
  }

  // ===== Getters =====

  public String getName() {
    return name;
  }

  public int getLife() {
    return life;
  }

  public int getAttack() {
    return attack;
  }

  public List<EnnemyEffect> getEffects() {
    return effects;
  }

  public int getForcedActivations() {
    return forcedActivations;
  }

  public boolean isForcedActivationMandatory() {
    return forcedActivationMandatory;
  }

  public Reward getReward() {
    return reward;
  }

  public List<Dice> getAssignedDice() {
    return assignedDice;
  }

  public int getPileNumber() {
    return pileNumber;
  }

  public boolean isDefeatedFlag() {
    return defeated;
  }

  // ===== Setters =====

  public void setPileNumber(int pileNumber) {
    this.pileNumber = pileNumber;
  }

  public void setDefeated(boolean defeated) {
    this.defeated = defeated;
  }

  // ===== Représentation textuelle =====

  @Override
  public String toString() {
    return String.format("Ennemi{%s, PV=%d, ATK=%d, pile=%d}", name, life, attack, pileNumber);
  }

  public int getClassValue() {
    return classValue;
  }

  public int getCurrentLife() {
    return currentLife;
  }

  public void setCurrentLife(int currentLife) {
    this.currentLife = currentLife;
  }

  public void computeCurrentLife(GameState gameState) {
    int totalDamage = assignedDice.stream()
        .mapToInt(Dice::getLastRoll)
        .sum();

    int bonusDamage = 0;
    for (BonusEffect bonusEffect : gameState.getBonusEffectsTurn()) {
      bonusDamage += bonusEffect.getBonusDamage(gameState, this);
    }

    currentLife = Math.max(0, life - (totalDamage + bonusDamage));
  }
}
