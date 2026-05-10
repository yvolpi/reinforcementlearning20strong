package model.effets.ennemi;

import model.Dice;
import model.DiceState;
import model.GameState;
import model.Player;
import model.ennemis.Ennemi;

public class GuepeChercheuseEffect implements EnnemyEffect {
  private boolean activated = true;

  @Override
  public EnnemyEffectType getType() {
    return EnnemyEffectType.SUBSEQUENT;
  }

  @Override
  public void apply(Player player, GameState gameState, Ennemi ennemi) {
    // Pour chaque dé assigné, le joueur doit choisir une pile pour défausser un ennemi
    // priorité : ennemi qui a le plus de pv, puis pile1 > pile2 > pile3

    int countNbEnnemiToDiscard = 0;
    for (Dice dice: gameState.getEngagedDices()) {
      if (dice.getState() == DiceState.ASSIGNE) {
        countNbEnnemiToDiscard++;
      }
    }

    for (int i = 0; i < countNbEnnemiToDiscard; i++) {
      // On trouve l'ennemi avec le plus de PV sur le dessus de chaque pile non vide
      if (gameState.getPile1().isEmpty() && gameState.getPile2().isEmpty() && gameState.getPile3()
          .isEmpty()) {
        break; // Si toutes les piles sont vides, on arrête
      }
      Ennemi highestPVEnnemi = searchTopEnnemiWithHighestLife(gameState);
      if (highestPVEnnemi != null) {
        // On défausse cet ennemi
        int pileNumber = highestPVEnnemi.getPileNumber();
        switch (pileNumber) {
          case 1:
            gameState.getPile1().pop();
            break;
          case 2:
            gameState.getPile2().pop();
            break;
          case 3:
            gameState.getPile3().pop();
            break;
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

  private Ennemi searchTopEnnemiWithHighestLife(GameState gameState) {
    Ennemi top1 = gameState.getPile1().isEmpty() ? null : gameState.getPile1().peek();
    Ennemi top2 = gameState.getPile2().isEmpty() ? null : gameState.getPile2().peek();
    Ennemi top3 = gameState.getPile3().isEmpty() ? null : gameState.getPile3().peek();

    // Priorité à "CIVIL_ASSERVI"
    if (top1 != null && "CIVIL_ASSERVI".equals(top1.getName())) return top1;
    if (top2 != null && "CIVIL_ASSERVI".equals(top2.getName())) return top2;
    if (top3 != null && "CIVIL_ASSERVI".equals(top3.getName())) return top3;

    // Recherche du max PV avec priorité pile1 > pile2 > pile3 en cas d'égalité
    Ennemi result = null;
    int maxPV = Integer.MIN_VALUE;

    if (top1 != null && top1.getLife() > maxPV) {
      result = top1;
      maxPV = top1.getLife();
    }
    if (top2 != null) {
      if (top2.getLife() > maxPV) {
        result = top2;
        maxPV = top2.getLife();
      }
      // priorité pile1 > pile2 en cas d'égalité, donc ne pas écraser result si égalité
    }
    if (top3 != null) {
      if (top3.getLife() > maxPV) {
        result = top3;
        maxPV = top3.getLife();
      }
      // priorité pile1 > pile2 > pile3 en cas d'égalité, donc ne pas écraser result si égalité
    }
    return result;
  }

}
