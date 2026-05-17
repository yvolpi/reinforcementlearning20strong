package model.effets.ennemi;

import java.util.Map;
import java.util.stream.Collectors;
import model.Dice;
import model.DiceColor;
import model.DiceState;
import model.GameState;
import model.ennemis.Ennemi;

public class LoseLifePerTwoHitsColorEffect implements EnnemyEffect {
  private boolean activated = true;

  @Override
  public EnnemyEffectType getType() {
    return EnnemyEffectType.PERMANENT;
  }

  @Override
  public void applyAfterEngagementAndRoll(GameState gameState, Ennemi ennemi) {
    // perte de pv pour tous les 2 dés de même couleur qui donnent une touche
    Map<DiceColor, Long> colorHitsCount = gameState.getEngagedDices()
        .stream()
        .filter(dice -> dice.getState() == DiceState.ENGAGE && dice.getLastRoll() > 0)
        .collect(Collectors.groupingBy(Dice::getColor, Collectors.counting()));

    colorHitsCount.forEach((color, count) -> {
      if (count >= 2) {
        int damage = (int) (count / 2);
        gameState.getPlayer().loseLife(damage);
      }
    });
  }


  @Override
  public boolean isActivated() {
    return activated;
  }

  @Override
  public void desactivate() {
    activated = false;
  }
}
