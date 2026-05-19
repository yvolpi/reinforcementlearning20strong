package model.missions;

import java.util.ArrayList;
import java.util.List;
import model.Dice;
import model.DiceColor;
import model.GameState;
import model.elements.GameAction;
import model.elements.GamePhase;
import model.ennemis.Ennemi;

public class M11 extends Mission {

  public M11() {
    super();
    this.canBeAbandoned = true;
  }

  @Override
  public String getName() {
    return "Union chromatique";
  }

  @Override
  public String getDescription() {
    return "Assignez 5 couleurs de dés à un même ennemi";
  }

  @Override
  public void onAssign(GameState gameState, Dice dice, Ennemi ennemi) {
    List<DiceColor> colorsUsed = new ArrayList<>();
    for (Dice d: ennemi.getAssignedDice()) {
      if (!colorsUsed.contains(d.getColor())) {
        colorsUsed.add(d.getColor());
      }
    }

    if (!colorsUsed.contains(dice.getColor())) {
      colorsUsed.add(dice.getColor());
      if (colorsUsed.size() >= 5) {
        setSuccess(true);
      }
    }
  }
}
