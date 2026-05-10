package model.items;

import model.GameState;
import model.Player;

/**
 Annule l'effet d'un ennemi de classe 1 ou 2
 */

public class Lunoculation extends Item {


  @Override
  public String getName() {
    return "Luniculation";
  }

  public boolean canBeUsed(Player player, GameState gameState) {
    // si le jeu contient au moins un ennemi actif c1 ou c2
    return gameState.getActiveEnnemis()        .stream()
        .anyMatch(ennemi -> ennemi.getClassValue() == 1 || ennemi.getClassValue() == 2);
  }

  @Override
  public void use(Player player, GameState gameState) {
    player.removeItem(this);

  }
}
