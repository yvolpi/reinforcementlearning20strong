package model;

import java.util.Arrays;
import java.util.Random;

public class Dice {
  private int[] faces; // 6 faces, valeurs possibles : 0, 1, 2
  private DiceState state;
  private int lastRoll;
  private DiceColor color;

  public Dice(DiceColor color) {
    this.faces = color.getFaces();
    this.state = DiceState.RESERVE;
    this.color = color;
  }

  @Override
  public Dice clone() {
    Dice clone = new Dice(this.color);
    clone.faces = Arrays.copyOf(this.faces, 6);
    clone.state = this.state;
    clone.lastRoll = this.lastRoll;
    return clone;
  }

  public DiceState getState() { return state; }
  public void setState(DiceState state) { this.state = state; }
  public int[] getFaces() { return Arrays.copyOf(faces, 6); }
  public int getLastRoll() {
    return lastRoll;
  }

  public void roll(Random random) {
    int index = random.nextInt(6);
    lastRoll = faces[index];
  }

  public DiceColor getColor() {
    return color;
  }

  public void setToFail() {
    lastRoll = faces[0]; // suppose que la première face est toujours 0
  }

  public void setToHit() {
    lastRoll = faces[4]; // suppose que la cinquième face est toujours 1
  }

  public void setToCriticalHit() {
    lastRoll = faces[5]; // suppose que la sixième face est toujours 2
  }

}
