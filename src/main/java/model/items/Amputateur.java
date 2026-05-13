package model.items;

import java.util.Comparator;
import java.util.List;
import model.Dice;
import model.GameState;
import model.Player;
import model.ennemis.Ennemi;

public class Amputateur extends Item {

  @Override
  public String getName() {
    return "Amputateur";
  }

  @Override
  public boolean canBeUsed(Player player, GameState gameState) {
    // s'il y a un ennemi c1 non vaincu et s'il reste au moins 2 dés dans la réserve
    return gameState.getActiveEnnemis().stream().anyMatch(e -> !e.isDefeatedFlag() && e.getClassValue() == 1)
        && gameState.getDicePool().size() >= 2;
  }

  @Override
  public void use(Player player, GameState gameState) {
    // 1. Sélectionner les 2 dés les plus faibles à bannir
    List<Dice> toRemove = gameState.getDicePool().stream()
        .sorted(Comparator.comparingInt(Dice::getStrengthRanking))
        .limit(2)
        .toList();

    // 2. Les retirer de la réserve
    gameState.getDicePool().removeAll(toRemove);

    // 3. Chercher l'ennemi de classe 1 le plus résistant
    Ennemi target = gameState.getActiveEnnemis().stream()
        .filter(e -> !e.isDefeatedFlag() && e.getClassValue() == 1)
        .max((e1, e2) -> Integer.compare(e1.getCurrentLife(), e2.getCurrentLife()))
        .orElseThrow(() -> new IllegalStateException("Aucun ennemi de classe 1 non vaincu trouvé"));

    target.setDefeated(true);

    player.removeItem(this);

  }
}
