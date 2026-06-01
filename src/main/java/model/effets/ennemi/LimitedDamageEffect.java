package model.effets.ennemi;

import model.Dice;
import model.GameState;
import model.Player;
import model.ennemis.Ennemi;

public class LimitedDamageEffect implements EnnemyEffect {
  private int degatsRecusCeTour;
  private int maxDamage;
  private boolean activated = true;

  public LimitedDamageEffect(int maxDamage) {
    this.degatsRecusCeTour = 0;
    this.maxDamage = maxDamage;
  }

  @Override
  public EnnemyEffectType getType() {
    return EnnemyEffectType.PERMANENT;
  }

  @Override
  public void apply(Player player, GameState gameState, Ennemi ennemi) {
  }

  @Override
  public void receiveDamage(int damage) {
    int degatsRestants = maxDamage - degatsRecusCeTour;
    int degatsAppliques = Math.min(damage, Math.max(degatsRestants, 0));
    degatsRecusCeTour += degatsAppliques;
  }

  @Override
  public boolean canAssignDice(GameState gameState, Dice dice) {
    int degatsRestants = maxDamage - degatsRecusCeTour;
    if (degatsRestants < dice.getLastRoll()) {
      return false; // Ne peut pas assigner ce dé car il dépasserait le maximum de dégâts
    }
    return true; // Peut assigner ce dé
  }

  @Override
  public void applyBeforeAllEngagement(GameState gameState) {
    degatsRecusCeTour = 0; // Réinitialise les dégâts reçus au début de chaque tour
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
