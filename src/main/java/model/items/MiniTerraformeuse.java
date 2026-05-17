package model.items;

import model.GameState;
import model.effets.bonus.MiniTerraformeuseEffect;

public class MiniTerraformeuse extends Item {
  @Override
  public String getName() {
    return "Mini Terraformeuse";
  }

  @Override
  public boolean canBeUsed(model.Player player, model.GameState gameState) {
    return false; // Cet objet ne peut pas être utilisé, son effet est automatique
  }

  @Override
  public void use(model.Player player, model.GameState gameState) {
  }

  public void triggeredBeforeActivationPhase(GameState gameState) {
    gameState.getBonusEffectsTurn().add(new MiniTerraformeuseEffect());
  }
}
