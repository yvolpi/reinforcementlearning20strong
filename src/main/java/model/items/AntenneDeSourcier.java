package model.items;

import java.util.Comparator;
import model.Dice;
import model.DiceState;
import model.ennemis.Ennemi;

public class AntenneDeSourcier extends Item {
  @Override
  public String getName() {
    return "Antenne de sourcier";
  }

  @Override
  public boolean canBeUsed(model.Player player, model.GameState gameState) {
    // uniquement avant engagement + nécessite un ennemi c1 ou c2 et un dé de la réserve
    if (gameState.getPhase() != model.elements.GamePhase.USE_ITEM_BEFORE_ENGAGE) {
      return false;
    }
    boolean hasEnemyC1OrC2 = gameState.getActiveEnnemis().stream()
        .anyMatch(e -> e.getClassValue() == 1 || e.getClassValue() == 2);
    boolean hasDiceInReserve = !gameState.getDicePool().isEmpty();
    return hasEnemyC1OrC2 && hasDiceInReserve;
  }

  @Override
  public boolean isConsummable() {
    return false;
  }

  @Override
  public void use(model.Player player, model.GameState gameState) {
    // Épuise un dé de la réserve (le plus faible)
    Dice dice = gameState.getDicePool()        .stream()
        .min((d1, d2) -> Integer.compare(d1.getStrengthRanking(), d2.getStrengthRanking()))
        .orElseThrow(() -> new IllegalStateException("Aucun dé disponible dans la réserve"));
    dice.setState(DiceState.EPUISE);
    gameState.getExhaustedDice().add(dice);
    gameState.getDicePool().remove(dice);

    // Trouve l’ennemi le plus résistant (plus de PVs)
    Ennemi target = gameState.getActiveEnnemis().stream()
        .filter(e -> e.getClassValue() == 1 || e.getClassValue() == 2)
        .max(Comparator.comparingInt(Ennemi::getLife))
        .orElse(null);
    if (target != null) {
      target.setLife(2);
    }
  }
}
