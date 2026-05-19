package model.missions;

import java.util.List;
import model.Dice;
import model.GameState;
import model.elements.GameAction;
import model.ennemis.Ennemi;

public abstract class Mission {
  private boolean isSuccess;
  boolean canBeAbandoned;

  public Mission() {
    this.isSuccess = false;
    this.canBeAbandoned = false;
  }

  public abstract String getName();

  public abstract String getDescription();

  public void afterActivation(GameState gameState) {
    // Par défaut, ne fait rien à la fin du tour
  }

  public void onEngage(GameState gameState, List<GameAction> engageActions) {
    // Par défaut, ne fait rien lorsque le joueur subit des dégâts
  }

  public void onUseItem(GameState gameState) {
    // Par défaut, ne fait rien lorsque le joueur subit des dégâts
  }

  public void onAssign(GameState gameState, Dice dice, Ennemi ennemi) {
    // Par défaut, ne fait rien lorsque le joueur subit des dégâts
  }

  public void onDamageTaken(GameState gameState) {
    // Par défaut, ne fait rien lorsque le joueur subit des dégâts
  }

  public void onKillEnnemi(GameState gameState, Ennemi ennemi) {
    // Par défaut, ne fait rien lorsque le joueur subit des dégâts
  }

  public void onExtraDropEnnemi(GameState gameState) {
    // Par défaut, ne fait rien à la fin du tour
  }

  public void onEndTurn(GameState gameState) {
    // Par défaut, ne fait rien à la fin du tour
  }

  public boolean isSuccess() {
    return isSuccess;
  }

  public void setSuccess(boolean success) {
    this.isSuccess = success;
  }

}
