package model.effets.ennemi;

import model.DiceState;
import model.GameState;
import model.Player;
import model.ennemis.Ennemi;
import model.recompenses.FleauLunaireReward;

public class LarveAMoelleEffect implements EnnemyEffect {
  private boolean activated = true;

  @Override
  public EnnemyEffectType getType() {
    return EnnemyEffectType.SUBSEQUENT;
  }

  @Override
  public void apply(Player player, GameState gameState, Ennemi ennemi) {
    if (!ennemi.isDefeatedFlag()) {
      ennemi.setReward(new FleauLunaireReward());
      // tous les dés assignés à cet ennemi seront épuisés puis enlevés de l'ennemi
      for (var dice : ennemi.getAssignedDice()) {
        dice.setState(DiceState.EPUISE);
        gameState.getEngagedDices().remove(dice);
        gameState.getExhaustedDice().add(dice);
      }
      ennemi.getAssignedDice().clear();
      gameState.getActiveEnnemis().remove(ennemi);

      // puis l'ennemi va sur la première pile non vide à droite
      if (!gameState.getPile3().isEmpty()) {
        ennemi.setPileNumber(3);
        gameState.getPile3().push(ennemi);
      } else if (!gameState.getPile2().isEmpty()) {
        ennemi.setPileNumber(2);
        gameState.getPile2().push(ennemi);
      } else if (!gameState.getPile1().isEmpty()) {
        ennemi.setPileNumber(1);
        gameState.getPile1().push(ennemi);
      }

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
