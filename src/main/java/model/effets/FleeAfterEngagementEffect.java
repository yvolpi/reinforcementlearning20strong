package model.effets;

import model.DiceState;
import model.GameState;
import model.Player;
import model.ennemis.Ennemi;

public class FleeAfterEngagementEffect implements EnnemyEffect {

  @Override
  public EnnemyEffectType getType() {
    return EnnemyEffectType.PERMANENT;
  }

  @Override
  public void apply(Player player, GameState gameState) {

  }

  @Override
  public void applyAfterEngagementAndRoll(GameState gameState, Ennemi ennemi) {
    if (ennemi.isDefeatedFlag()) return;
    // fuit si 4 dés engagés non assignés ont donné une touche
    long countHits = gameState.getEngagedDices().stream()
        .filter(dice -> dice.getState() == DiceState.ENGAGE && dice.getLastRoll() > 0)
        .count();
    if (countHits >= 4) {
      // les dés assignés à cet ennemi sont épuisés
      ennemi.getAssignedDice().forEach(dice -> {
        dice.setState(DiceState.EPUISE);
        gameState.getExhaustedDice().add(dice);
        // enlever le dé parmi les dés engagés
        gameState.getEngagedDices().remove(dice);
      });

      // l'ennemi fuit
      gameState.getActiveEnnemis().remove(ennemi);
    }

  }

}
