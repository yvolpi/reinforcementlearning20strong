package model;

public enum DiceColor {
  JAUNE(new int[]{0, 0, 0, 0, 1, 2}),
  VERT(new int[]{0, 0, 0, 1, 1, 2}),
  BLEU(new int[]{0, 0, 1, 1, 1, 2}),
  VIOLET(new int[]{0, 1, 1, 1, 1, 2}),
  ROUGE(new int[]{1, 1, 1, 1, 1, 2});

  private final int[] faces;

  DiceColor(int[] faces) {
    this.faces = faces;
  }

  public int[] getFaces() {
    return faces.clone();
  }

  public int getStrengthRanking() {
    return switch (this) {
      case JAUNE  -> 1;
      case VERT   -> 2;
      case BLEU   -> 3;
      case VIOLET -> 4;
      case ROUGE  -> 5;
    };
  }
}
