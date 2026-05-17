package model.items;

import java.util.Comparator;
import model.Dice;
import model.DiceState;
import model.GameState;
import model.Player;
import model.ennemis.Ennemi;

public class PistoletEtourdissant extends Item {

  @Override
  public String getName() {
    return "Pistolet Etourdissant";
  }

  public boolean canBeUsed(Player player, GameState gameState) {
    // L'objet peut être utilisé s'il y a un ennemi actif de classe 2
    return gameState.getActiveEnnemis().stream().anyMatch(e -> !e.isDefeatedFlag() && e.getClassValue() == 2);
  }

  @Override
  public void use(Player player, GameState gameState) {
    // 1. Sélectionner un ennemi de classe 2 non vaincu
    Ennemi target = gameState.getActiveEnnemis().stream()
        .filter(e -> !e.isDefeatedFlag() && e.getClassValue() == 2)
        // le plus résistant
        .max(Comparator.comparingInt(Ennemi::getCurrentLife))
        .orElseThrow(() -> new IllegalStateException("Aucun ennemi de classe 2 non vaincu trouvé"));

    // 2. Appliquer l'effet d'étourdissement à l'ennemi ciblé
    for (Dice dice : target.getAssignedDice()) {
      dice.setState(DiceState.EPUISE);
      gameState.getEngagedDices().remove(dice);
      gameState.getExhaustedDice().add(dice);
    }
    gameState.getActiveEnnemis().remove(target);

    // 3. Retirer l'objet de l'inventaire du joueur
    player.removeItem(this);
  }
}
