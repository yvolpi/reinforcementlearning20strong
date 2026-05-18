package model.items;

import java.util.Comparator;
import model.GameState;
import model.Player;
import model.effets.bonus.SacrificeEffect;
import model.ennemis.Ennemi;

public class SacrificeSolaire extends Item {

  @Override
  public String getName() {
    return "Sacrifice Solaire";
  }

  @Override
  public boolean canBeUsed(Player player, GameState gameState) {
    // Le Sacrifice Solaire peut être utilisé avant chaque étape assigner, s'il y a au moins 1 ennemi actif de classe 1 ou 2 vivant et s'il y a au moins 1 dé dans la réserve
    return gameState.getPhase() == model.elements.GamePhase.USE_ITEM_BEFORE_ASSIGN
        && gameState.getActiveEnnemis().stream().anyMatch(ennemi -> !ennemi.isDefeated() && (ennemi.getClassValue() == 1 || ennemi.getClassValue() == 2))
        && !gameState.getDicePool().isEmpty();
  }

  @Override
  public void use(Player player, GameState gameState) {
    // Le joueur bannit un dé de sa réserve pour infliger un certain nombre de dégâts à un ennemi actif de classe 1 ou 2 vivant

    // cible : ennemi c1 ou c2 qui a le plus de pvs
    Ennemi targetEnnemi = gameState.getActiveEnnemis().stream()
        .filter(ennemi -> !ennemi.isDefeated() && (ennemi.getClassValue() == 1 || ennemi.getClassValue() == 2))
        .max(Comparator.comparingInt(Ennemi::getCurrentLife))
        .orElseThrow(() -> new IllegalStateException("Il n'y a aucun ennemi de classe 1 ou 2 vivant"));

    // Dé à bannir
    if (gameState.getDicePool().isEmpty()) {
      throw new IllegalStateException("Il n'y a aucun dé dans la réserve à bannir");
    }
    model.Dice diceToBanish = gameState.getDicePool().get(gameState.getRandom().nextInt(gameState.getDicePool().size()));
    gameState.getDicePool().remove(diceToBanish);

    gameState.getBonusEffectsTurn().add(new SacrificeEffect(diceToBanish, targetEnnemi));


  }
}