package model.items;

import model.GameState;
import model.Player;
import model.ennemis.Ennemi;

public class EncreToxique extends Item {
  @Override
  public String getName() {
    return "Encre Toxique";
  }

  @Override
  public boolean canBeUsed(Player player, GameState gameState) {
    // Peut être utilisé seulement s'il y a au moins une pile d'ennemis non vide
    return gameState.atLeastOneEnnemiOnPiles();
  }

  @Override
  public void use(Player player, GameState gameState) {
    // Vainc automatiquement un ennemi de la 1ere pile non vide pour gagner sa récompense
    Ennemi ennemi = null;
    if (!gameState.getPile1().isEmpty()) {
      ennemi = gameState.getPile1().poll();
    } else if (!gameState.getPile2().isEmpty()) {
      ennemi = gameState.getPile2().poll();
    } else if (!gameState.getPile3().isEmpty()) {
      ennemi = gameState.getPile3().poll();
    }

    if (ennemi != null) {
      // Appliquer la récompense de l'ennemi au joueur
      ennemi.getReward().apply(gameState);
    }
    gameState.getPlayer().removeItem(this);
  }
}
