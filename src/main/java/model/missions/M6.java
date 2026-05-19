package model.missions;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import model.Dice;
import model.DiceColor;
import model.GameState;
import model.elements.GameAction;
import model.elements.GamePhase;

public class M6 extends Mission {

  @Override
  public String getName() {
    return "Sonner l alerte";
  }

  @Override
  public String getDescription() {
    return "Engagez tous les dés d'au moins 3 couleurs différentes lors d'une même phase engager.";
  }

  @Override
  public void onEngage(GameState gameState, List<GameAction> actions) {
    // récupérer les dés dans actions et les classer par couleur.
    Map<DiceColor, Integer> colorCountToEngage = actions.stream()
        .filter(action -> action.getType() == GamePhase.ENGAGE_DICE)
        .map(GameAction::getDice)
        .collect(Collectors.groupingBy(Dice::getColor, Collectors.summingInt(d -> 1)));

    // Faire de même pour la réserve
    Map<DiceColor, Integer> colorCountInPool = gameState.getDicePool().stream()
        .collect(Collectors.groupingBy(Dice::getColor, Collectors.summingInt(d -> 1)));

    int colorCount = 0;
    // pour chaque couleur de dé dans colorCountToEngage, il faut vérifier si on en a le même nombre

    for (Map.Entry<DiceColor, Integer> entry : colorCountToEngage.entrySet()) {
      DiceColor color = entry.getKey();
      int countToEngage = entry.getValue();
      int countInPool = colorCountInPool.getOrDefault(color, 0);

      if (countToEngage > 0 && countInPool == countToEngage) {
        colorCount++;
      }
    }
    if (colorCount >= 3) {
      setSuccess(true);
    }
  }
}
