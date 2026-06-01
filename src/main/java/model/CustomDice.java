package model;

import java.util.Arrays;
import java.util.Map;
import model.random.CustomRandom;

public class CustomDice extends Dice {
  private String name;
  private int indexNormalHit;
  private int indexCriticalHit;

  public CustomDice(String name, DiceColor color, int indexNormalHit, int indexCriticalHit, Map<Integer, Integer> nbFacesByScore) {
     super(color);
     this.name = name;

    // Ordre stable par score (0,1,2,...) pour garantir les mêmes index.
    Map<Integer, Integer> ordered = new java.util.TreeMap<>(nbFacesByScore);

    int totalFaces = 0;
    for (Map.Entry<Integer, Integer> e : ordered.entrySet()) {
      int nb = e.getValue();
      if (nb < 0) throw new IllegalArgumentException("Nombre de faces négatif pour score " + e.getKey());
      totalFaces += nb;
    }

    if (totalFaces <= 0) {
      throw new IllegalArgumentException("Le dé doit avoir au moins une face");
    }
    if (indexNormalHit < 0 || indexNormalHit >= totalFaces ||
        indexCriticalHit < 0 || indexCriticalHit >= totalFaces) {
      throw new IllegalArgumentException("indexNormalHit/indexCriticalHit hors bornes");
    }


    this.indexNormalHit = indexNormalHit;
    this.indexCriticalHit = indexCriticalHit;
    int[] faces = new int[totalFaces];
    int cumul = 0;
    for (Map.Entry<Integer, Integer> e : ordered.entrySet()) {
      int score = e.getKey();
      int nbFaces = e.getValue();
      for (int i = 0; i < nbFaces; i++) {
        faces[cumul++] = score;
      }
    }
    this.faces = faces;

  }

  // constructeur interne de copie (privé)
  private CustomDice(String name, DiceColor color, int indexNormalHit, int indexCriticalHit, int[] faces) {
    super(color);
    this.name = name;
    this.indexNormalHit = indexNormalHit;
    this.indexCriticalHit = indexCriticalHit;
    this.faces = Arrays.copyOf(faces, faces.length);
  }

  @Override
  @SuppressWarnings("MethodDoesntCallSuperMethod")
  public CustomDice clone() {
    CustomDice clone = new CustomDice(this.name, this.getColor(), this.indexNormalHit, this.indexCriticalHit, faces);
     clone.setState(this.getState());
     clone.lastRoll = this.lastRoll;
    return clone;
  }

  @Override
  public String getName() {
    return name;
  }

  public int[] getFaces() { return Arrays.copyOf(faces, faces.length); }

  public void roll(CustomRandom random) {
    int index = random.nextInt(faces.length);
    lastRoll = faces[index];
  }

  public boolean isNormalHit() {
    return lastRoll == faces[indexNormalHit];
  }

  public boolean isCriticHit() {
    return lastRoll == faces[indexCriticalHit];
  }

  public void setToHit() {
    lastRoll = faces[indexNormalHit];
  }

  public void setToCriticalHit() {
    lastRoll = faces[indexCriticalHit];
  }

}
