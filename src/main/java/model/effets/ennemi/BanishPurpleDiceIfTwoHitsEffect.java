package model.effets.ennemi;

import java.util.Comparator;
import java.util.List;
import model.Dice;
import model.DiceColor;
import model.DiceState;
import model.GameState;
import model.Player;
import model.ennemis.Ennemi;

public class BanishPurpleDiceIfTwoHitsEffect implements EnnemyEffect {
  private boolean activated = true;

  @Override
  public EnnemyEffectType getType() {
    return EnnemyEffectType.SUBSEQUENT;
  }

  @Override
  public void apply(Player player, GameState gameState, Ennemi ennemi) {
    // Utiliser les 3 dés engagés les plus faibles pour les lancer
    List<Dice> weakestEngagedDices = gameState.getEngagedDices().stream()
        .filter(dice -> dice.getState() == model.DiceState.ENGAGE)
        .sorted(Comparator.comparingInt(Dice::getStrengthRanking))
        .limit(3)
        .toList();

    // les lancer s'il y a au moins 2 dés
    if (weakestEngagedDices.size() > 1) {
      int countHit = 0;
      for (Dice dice : weakestEngagedDices) {
        dice.roll(gameState.getRandom());
        if (dice.getLastRoll() > 0) {
          countHit++;
        }
      }

      if (countHit >= 2) {
        // bannir un dé violet
        Dice purpleDiceToBanish = searchPurpleDice(gameState);

        if (purpleDiceToBanish != null) {
          DiceState diceState = purpleDiceToBanish.getState();
          if (diceState == DiceState.EPUISE) {
            gameState.getExhaustedDice().remove(purpleDiceToBanish);
          } else if (diceState == DiceState.RESERVE) {
            gameState.getDicePool().remove(purpleDiceToBanish);
          } else if (diceState == DiceState.ENGAGE) {
            gameState.getEngagedDices().remove(purpleDiceToBanish);
          } else if (diceState == DiceState.ASSIGNE) {
            // chercher l'ennemi à qui est assigné ce dé
            Ennemi assignedEnnemi = gameState.getActiveEnnemis().stream()
                .filter(ennemi1 -> ennemi1.getAssignedDice().contains(purpleDiceToBanish))
                .findFirst()
                .orElse(null);
            if (assignedEnnemi == null) {
              throw new IllegalStateException("Le dé violet à bannir est dans l'état ASSIGNE mais aucun ennemi n'est assigné à ce dé");
            }
            assignedEnnemi.getAssignedDice().remove(purpleDiceToBanish);
            gameState.getEngagedDices().remove(purpleDiceToBanish);
          }
        }

      }
    }
  }


  @Override
  public boolean isActivated() {
    return activated;
  }

  @Override
  public void desactivate() {
    activated = false;
  }


  private Dice searchPurpleDice(GameState gameState) {
    //priorité : dans les dés épuisés, puis dans la réserve, puis engagés
    return gameState.getExhaustedDice().stream()
        .filter(dice -> dice.getColor() == DiceColor.VIOLET)
        .findFirst()
        .or(() -> gameState.getDicePool().stream()
            .filter(dice -> dice.getColor() == model.DiceColor.VIOLET)
            .findFirst())
        .or(() -> gameState.getEngagedDices().stream()
            .filter(dice -> dice.getColor() == model.DiceColor.VIOLET)
            .findFirst())
        .orElse(null);
  }
}
