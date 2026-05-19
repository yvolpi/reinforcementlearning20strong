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
  public void onAssign(GameState gameState, GameAction assignAction) {
    if (assignAction.getType() != GamePhase.ASSIGN_DICE) {
      throw new IllegalArgumentException("L'action doit être de type ASSIGN_DICE");
    }
    Ennemi targetEnnemi = assignAction.getTarget();

    List<DiceColor> colorsUsed = new ArrayList<>();
    for (Dice dice: targetEnnemi.getAssignedDice()) {
      if (!colorsUsed.contains(dice.getColor())) {
        colorsUsed.add(dice.getColor());
      }
    }

    Dice diceToAssign = assignAction.getDice();

    if (!colorsUsed.contains(diceToAssign.getColor())) {
      colorsUsed.add(diceToAssign.getColor());
      if (colorsUsed.size() >= 5) {
        setSuccess(true);
      }
    }
  }
}
