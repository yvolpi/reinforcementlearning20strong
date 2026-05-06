package model.effets;

import model.GameState;
import model.Player;

public class SkipRecoverPhaseEffect implements EnnemyEffect {
  private final EnnemyEffectType type;

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
  public void apply(Player player, GameState gameState) {
    // L'effet est géré dans GameService.recoverDicePhase
  }
}
