package model.missions;

import model.GameState;

public class M10 extends Mission {
  private int count;
  private Integer numberPile;

  public M10() {
    super();
    count = 0;
    numberPile = null;
  }

  @Override
  public String getName() {
    return "Traque impitoyable";
  }

  @Override
  public String getDescription() {
    return "Choisissez 1 pile d'ennemis non vide : elle devient obligatoire pendant 5 tours";
  }

  public Integer getNumberPile() {
    return numberPile;
  }

  public void setNumberPile(int numberPile) {
    this.numberPile = numberPile;
  }

  public void onEndTurn(GameState gameState) {
    count++;
    if (count >= 5) {
      setSuccess(true);
    }
  }
}
