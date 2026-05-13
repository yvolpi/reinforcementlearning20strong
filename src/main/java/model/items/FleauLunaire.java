package model.items;

import model.elements.GamePhase;

public class FleauLunaire extends Item {

  @Override
  public boolean canBeUsed(model.Player player, model.GameState gameState) {
    return gameState.getPhase() == GamePhase.USE_ITEM_BEFORE_ACTIVATE;
  }

  @Override
  public String getName() {
    return "Fleau Lunaire";
  }

  @Override
  public void use(model.Player player, model.GameState gameState) {
    // si vous devez activer d'autres ennemis, vous en aurez un en moins à activer
    gameState.getBonusEffectsTurn().add(new model.effets.bonus.FleauLunaireEffect());

    player.removeItem(this);
  }
}
