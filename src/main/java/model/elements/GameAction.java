package model.elements;

import static model.elements.GamePhase.NEW_MISSION;

import java.util.ArrayList;
import java.util.List;
import model.Dice;
import model.effets.ennemi.EnnemyEffect;
import model.ennemis.Ennemi;
import model.items.Item;

public class GameAction {
  private GamePhase type;
  private boolean activateBoss; // pour ACTIVATE_BOSS
  private int pileNumber; // pour ACTIVATE_PILE
  private Dice dice;      // pour ENGAGE_DICE ou ASSIGN_DICE
  private Ennemi target;
  private EnnemyEffect ennemyEffect;
  private Item item;
  // ... autres champs selon le type d’action
  private List<Dice> diceList;
  private boolean giveUpMission;

  public GameAction(int pileNumber) {
    this.type = NEW_MISSION;
    this.pileNumber = pileNumber;
  }

  public GameAction(GamePhase type, Dice dice) {
    this.type = type;
    this.dice = dice;
    this.target = null;
  }

  public GameAction(EnnemyEffect ennemyEffect, Ennemi target) {
    this.ennemyEffect = ennemyEffect;
    this.target = target;
  }

  public GameAction(boolean activateBoss) {
    this.activateBoss = activateBoss;
  }

  public GameAction(GamePhase type, Dice dice, Ennemi target) {
    this.type = type;
    this.dice = dice;
    this.target = target;
  }

  public GameAction(GamePhase type, Item item) {
    this.type = type;
    this.item = item;
  }

  public GameAction(GamePhase type, Ennemi randomEnnemi) {
    this.type = type;
    this.target = randomEnnemi;
  }

  public GameAction(GamePhase phase,boolean giveUpMission) {
    this.giveUpMission = giveUpMission;
    this.type = phase;
  }

  public boolean isActivateBoss() {
    return activateBoss;
  }

  public GamePhase getType() {
    return type;
  }

  public Dice getDice() {
    return dice;
  }

  public Ennemi getTarget() {
    return target;
  }

  public Item getItem() {
    return item;
  }

  public EnnemyEffect getEnnemyEffect() {
    return ennemyEffect;
  }

  public int getPileNumber() {
    return pileNumber;
  }

  public boolean isGiveUpMission() {
    return giveUpMission;
  }
}
