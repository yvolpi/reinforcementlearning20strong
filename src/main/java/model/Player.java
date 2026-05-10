package model;

import java.util.ArrayList;
import java.util.List;
import model.items.Item;

public class Player {
  private int life;
  private int strategy;
  private int recovery;
  private final List<Item> items;

  public Player(int life, int strategy, int recovery) {
    this.life = life;
    this.strategy = strategy;
    this.recovery = recovery;
    items = new ArrayList<>();
  }

  @Override
  public Player clone() {
    return new Player(this.life, this.strategy, this.recovery);
  }

  // Getters
  public int getLife() { return life; }
  public int getStrategy() { return strategy; }
  public int getRecovery() { return recovery; }
  public List<Item> getItems() { return new ArrayList<>(items); }

  // Setters
  public void setRecovery(int recovery) { this.recovery = recovery; }
  public void setStrategy(int strategy) { this.strategy = strategy; }

  // Méthodes utilitaires (exemples)
  public void loseLife(int amount) { this.life = Math.max(0, this.life - amount); }
  public void gainLife(int amount) { this.life += amount; }
  public void addItem(Item item) {
    items.add(item);
  }

  public void removeItem(Item item) {
    items.remove(item);
  }

  public void resetUsableItems() {
    items.forEach(Item::resetUsage);
  }

}

