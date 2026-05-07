package model.elements;

import java.util.ArrayList;
import java.util.List;
import model.Dice;
import model.ennemis.Ennemi;
import model.items.Item;

public class GameAction {
  private GamePhase type;
  private int pileNumber; // pour ACTIVATE_PILE
  private Dice dice;      // pour ENGAGE_DICE ou ASSIGN_DICE
  private Ennemi target;
  private Item item;
  // ... autres champs selon le type d’action
  private List<Dice> diceList;

  public GameAction(GamePhase type, Dice dice) {
    this.type = type;
    this.dice = dice;
    this.target = null;
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
}
