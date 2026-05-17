package model.effets.ennemi;

import java.util.Comparator;
import java.util.List;
import model.Dice;
import model.GameState;
import model.ennemis.Ennemi;

public class SolodroneEffect implements EnnemyEffect {
  private boolean activated = true;

  @Override
  public EnnemyEffectType getType() {
    return EnnemyEffectType.PERMANENT;
  }

  @Override
  public void applyAfterEngagementAndRoll(GameState gameState, Ennemi ennemi) {
    // Pour chaque échec, il faut relancer les touches
    // en priorité les touches normales des dés les plus forts
    // les touches critiques en dernier
    int countFail = (int) gameState.getEngagedDices()
        .stream()
        .filter(dice -> dice.getState() == model.DiceState.ENGAGE && dice.getLastRoll() == 0)
        .count();

    List<Dice> diceToReroll = gameState.getEngagedDices()
        .stream()
        .filter(dice -> dice.getState() == model.DiceState.ENGAGE && dice.getLastRoll() > 0)
        .sorted(
            Comparator
                // 1. Touche normale avant critique
                .comparingInt((Dice d) -> d.getLastRoll() == 1 ? 0 : 1)
                // 2. Priorité couleur (Rouge > Violet > ...)
                .thenComparingInt(Dice::getStrengthRanking).reversed()
        ) // tri décroissant pour avoir les dés les plus forts en premier
        .limit(countFail)
        .toList();

    for (Dice dice : diceToReroll) {
      dice.roll(gameState.getRandom());
    }

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
