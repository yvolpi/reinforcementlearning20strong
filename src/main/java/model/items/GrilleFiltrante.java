package model.items;

public class GrilleFiltrante extends Item {

  @Override
  public String getName() {
    return "GrilleFiltrante";
  }

  @Override
  public boolean canBeUsed(model.Player player, model.GameState gameState) {
    // Ne peut pas être utilisé après l'activation du boss
    return gameState.isActivatedBoss();
  }

  @Override
  public void use(model.Player player, model.GameState gameState) {
    // Révèle la première carte de la pile de boss. Si le boss est déjà révélé, on l'enlève de la pile
    if (gameState.getBossPile().isEmpty()) {
      return; // Pas de boss à révéler
    }

    if (gameState.isRevealedBoss()) {
      // Si le boss est déjà révélé, on l'enlève de la pile
      gameState.getBossPile().remove();
    } else {
      // Sinon, on révèle le boss
      gameState.setRevealedBoss(true);
    }


  }

}
