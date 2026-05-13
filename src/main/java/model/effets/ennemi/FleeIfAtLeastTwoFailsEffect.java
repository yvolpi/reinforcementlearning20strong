package model.effets.ennemi;

import model.DiceState;
import model.GameState;
import model.ennemis.Ennemi;

public class FleeIfAtLeastTwoFailsEffect implements EnnemyEffect {
  private boolean activated = true;

  @Override
  public EnnemyEffectType getType() {
    return EnnemyEffectType.PERMANENT;
  }

  @Override
  public void applyAfterEngagementAndRoll(GameState gameState, Ennemi ennemi) {
    if (ennemi.isDefeatedFlag()) return;
    // fuit si 2 dés engagés non assignés ont donné un échec (0)
    long countFails = gameState.getEngagedDices().stream()
        .filter(dice -> dice.getState() == DiceState.ENGAGE && dice.getLastRoll() == 0)
        .count();
    if (countFails >= 2) {
      // les dés assignés à cet ennemi sont épuisés
      gameState.checkIfErrorBetweenPoolAndEngagedAndExhaustedDice();
      //System.out.println("FleeAfterEngagementEffect : l'ennemi " + ennemi.getName() + " fuit car " + countHits + " dés engagés ont touché");
      //System.out.println(gameState.getExhaustedDice());
      //System.out.println(gameState.getEngagedDices());
      ennemi.getAssignedDice().forEach(dice -> {
        if (gameState.getExhaustedDice().contains(dice)) {
          throw new IllegalStateException("Le dé " + dice + " est déjà épuisé, ne peut pas être épuisé à nouveau");
        }
        dice.setState(DiceState.EPUISE);
        gameState.getExhaustedDice().add(dice);
        // enlever le dé parmi les dés engagés
        gameState.getEngagedDices().remove(dice);
      });
      gameState.checkIfErrorBetweenPoolAndEngagedAndExhaustedDice();

      // l'ennemi fuit
      gameState.getActiveEnnemis().remove(ennemi);
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
