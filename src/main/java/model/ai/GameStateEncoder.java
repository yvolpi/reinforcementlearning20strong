package model.ai;

import java.util.Comparator;
import java.util.stream.Collectors;
import model.DiceState;
import model.GameState;
import model.elements.GameAction;
import model.ennemis.Ennemi;
import model.items.Item;
import recompenses.Reward;

/**
 * Responsable de l'encodage de l'état du jeu et des actions en clés de la Q-table.
 */
public class GameStateEncoder {

  public String encodeState(GameState gameState) {
    StringBuilder sb = new StringBuilder();
    sb.append(gameState.getPlayer().getLife()).append("|");
    long reserveDiceCount = gameState.getDicePool().stream()
        .filter(d -> d.getState() == DiceState.RESERVE)
        .count();
    sb.append(reserveDiceCount).append("|");
    String enemies = gameState.getActiveEnnemis().stream()
        .map(Ennemi::getName)
        .collect(Collectors.joining(";"));
    sb.append(enemies);
    return sb.toString();
  }

  public String encodeStateForEngage(GameState gameState) {
    StringBuilder sb = new StringBuilder();
    sb.append(gameState.getPlayer().getLife()).append("|");
    long reserveDice = gameState.getDicePool().stream()
        .filter(d -> d.getState() == DiceState.RESERVE)
        .count();
    long engagedDice = gameState.getDicePool().stream()
        .filter(d -> d.getState() == DiceState.ENGAGE)
        .count();
    sb.append(reserveDice).append("|").append(engagedDice).append("|");
    String enemies = gameState.getActiveEnnemis().stream()
        .filter(e -> !e.isDefeatedFlag())
        .map(e -> e.getName() + ":" + e.getCurrentLife())
        .collect(Collectors.joining(";"));
    sb.append(enemies);
    return sb.toString();
  }

  public String encodeStateForAssign(GameState gameState) {
    StringBuilder sb = new StringBuilder();
    sb.append(gameState.getPlayer().getLife()).append("|");

    String assignableDice = gameState.getAvailableDiceToAssign().stream()
        .sorted(Comparator.comparing(d -> d.getColor().name())) // ordre déterministe
        .map(d -> d.getColor().name() + ":" + d.getLastRoll())
        .collect(Collectors.joining(";"));
    sb.append(assignableDice).append("|");

    String enemies = gameState.getActiveEnnemis().stream()
        .filter(e -> !e.isDefeatedFlag())
        .map(e -> e.getName() + ":" + e.getCurrentLife())
        .collect(Collectors.joining(";"));
    sb.append(enemies);
    return sb.toString();
  }

  public String encodeStateForItemsManagement(GameState gameState, Reward reward) {
    StringBuilder sb = new StringBuilder();
    sb.append(gameState.getPlayer().getLife()).append("|");
    long reserveDice = gameState.getDicePool().stream()
        .filter(d -> d.getState() == DiceState.RESERVE)
        .count();
    long engagedDice = gameState.getDicePool().stream()
        .filter(d -> d.getState() == DiceState.ENGAGE)
        .count();
    sb.append(reserveDice).append("|").append(engagedDice).append("|");
    String playerItems = gameState.getPlayer().getItems().stream()
        .map(Item::getName)
        .collect(Collectors.joining(";"));
    sb.append(playerItems).append("|");
    // ennemis des premières piles + boss s'il est révélé
    String enemies = "";
    if (gameState.getPile1() != null && !gameState.getPile1().isEmpty()) {
      enemies += gameState.getPile1().peek().getName() + ":PILE1;";
    }
    if (gameState.getPile2() != null && !gameState.getPile2().isEmpty()) {
      enemies += gameState.getPile2().peek().getName() + ":PILE2;";
    }
    if (gameState.getPile3() != null && !gameState.getPile3().isEmpty()) {
      enemies += gameState.getPile3().peek().getName() + ":PILE3;";
    }
    if (gameState.isRevealedBoss()) {
      enemies += gameState.getBossPile().peek().getName() + ":BOSS;";
    }

    sb.append(enemies);
    // Ajout de la récompense en attente
    sb.append(reward != null ? reward.getName() : "NONE");
    return sb.toString();
  }


  public String encodeStateWithPile(GameState gameState) {
    StringBuilder sb = new StringBuilder();
    sb.append(gameState.getPlayer().getLife()).append("|");
    long reserveDiceCount = gameState.getDicePool().stream()
        .filter(d -> d.getState() == DiceState.RESERVE)
        .count();
    sb.append(reserveDiceCount).append("|");
    String pile1 = !gameState.getPile1().isEmpty() ? gameState.getPile1().peek().getName() : "NONE";
    String pile2 = !gameState.getPile2().isEmpty() ? gameState.getPile2().peek().getName() : "NONE";
    String pile3 = !gameState.getPile3().isEmpty() ? gameState.getPile3().peek().getName() : "NONE";
    sb.append(pile1).append(";").append(pile2).append(";").append(pile3);
    return sb.toString();
  }

  public String encodeActivateAction(GameAction action) {
    if (action == null || action.getTarget() == null) return "NONE";
    return "ACTIVATE:PILE_" + action.getTarget().getPileNumber();
  }

  public String encodeEngageAction(GameAction action) {
    if (action == null) return "NONE";
    return "ENGAGE:" + action.getDice().getColor().name();
  }

  public String encodeAssignAction(GameAction action) {
    if (action == null) return "NONE";
    String dicePart = action.getDice().getColor().name();
    String targetPart = action.getTarget() != null ? action.getTarget().getName() : "NONE";
    return "ASSIGN:" + dicePart + "->" + targetPart;
  }
}
