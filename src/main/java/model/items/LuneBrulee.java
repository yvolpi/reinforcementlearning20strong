package model.items;

import model.effets.bonus.LuneBruleeEffect;

public class LuneBrulee extends Item {
  @Override
  public String getName() {
    return "Lune Brulee";
  }

  @Override
  public boolean canBeUsed(model.Player player, model.GameState gameState) {
    return false; // Cet objet ne peut pas être utilisé, son effet est automatique
  }

  @Override
  public void use(model.Player player, model.GameState gameState) {
  }

  @Override
  public void triggeredBeforeActivationPhase(model.GameState gameState) {
    gameState.getBonusEffectsTurn().add(new LuneBruleeEffect());
  }
}
