package model.effets.ennemi;

import model.GameState;
import model.Player;
import model.ennemis.Ennemi;

public class SkipRecoverPhaseEffect implements EnnemyEffect {
  private final EnnemyEffectType type;
  private boolean activated = true;

  // Constructeur par défaut : PERMANENT
  public SkipRecoverPhaseEffect() {
    this.type = EnnemyEffectType.PERMANENT;
  }

  // Constructeur pour choisir le type
  public SkipRecoverPhaseEffect(EnnemyEffectType type) {
    this.type = type;
  }

  @Override
  public EnnemyEffectType getType() {
    return type;
  }

  @Override
  public void apply(Player player, GameState gameState, Ennemi ennemi) {
    // L'effet est géré dans GameService.recoverDicePhase
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
