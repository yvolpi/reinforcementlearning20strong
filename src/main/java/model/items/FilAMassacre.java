package model.items;

import model.GameState;
import model.Player;
import model.effets.bonus.FilAMassacreEffect;

public class FilAMassacre extends Item {
  @Override
  public String getName() {
    return "Fil a Massacre";
  }

  @Override
  public boolean canBeUsed(model.Player player, model.GameState gameState) {
    // Son bonus est automatique
    return false;
  }

  @Override
  public void use(Player player, GameState gameState) {
  }

  public void triggeredBeforeActivationPhase(GameState gameState) {
    gameState.getBonusEffectsTurn().add(new FilAMassacreEffect());
  }
}
