package model.ai;

import java.util.Comparator;
import java.util.stream.Collectors;
import model.DiceState;
import model.GameState;
import model.elements.GameAction;
import model.ennemis.Ennemi;
import model.items.Item;
import model.missions.Mission;
import model.recompenses.Reward;

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
    // mission active
    Mission activeMission = gameState.getActiveMission();
    sb.append("|MISSION:").append(activeMission != null ? activeMission.getName() : "NONE");

    return sb.toString();
  }

  public String encodeGlobalState(GameState gameState) {
    // stats joueur, items, ennemis sur chaque pile, dés disponibles
    StringBuilder sb = new StringBuilder();
    sb.append(gameState.getPlayer().getLife()).append("|");
    sb.append(gameState.getPlayer().getStrategy()).append("|");
    sb.append(gameState.getPlayer().getRecovery()).append("|");

    // items
    String playerItems = gameState.getPlayer().getItems().stream()
        .map(Item::getName)
        .collect(Collectors.joining(";"));
    sb.append(playerItems).append("|");

    long reserveDiceCount = gameState.getDicePool().stream()
        .filter(d -> d.getState() == DiceState.RESERVE)
        .count();
    sb.append(reserveDiceCount).append("|");
    // nb d'ennemis par pile
    long pile1Count = gameState.getPile1() != null ? gameState.getPile1().size() : 0;
    long pile2Count = gameState.getPile2() != null ? gameState.getPile2().size() : 0;
    long pile3Count = gameState.getPile3() != null ? gameState.getPile3().size() : 0;
    sb.append(pile1Count).append("|").append(pile2Count).append("|").append(pile3Count).append("|");

    //1ers ennemis de chaque piles

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
    // mission active
    Mission activeMission = gameState.getActiveMission();
    sb.append("|MISSION:").append(activeMission != null ? activeMission.getName() : "NONE");

    return sb.toString();
  }

  public String encodeStateForM10(GameState gameState) {
    StringBuilder sb = new StringBuilder();
    sb.append("CHOIXPILEM10|");
    // Stats du joueur
    sb.append(gameState.getPlayer().getLife()).append("|");
    sb.append(gameState.getPlayer().getStrategy()).append("|");
    sb.append(gameState.getPlayer().getRecovery()).append("|");

    // nb d'ennemis dans chaque pile + 1er ennemi de chaque pile
    String enemies = "";
    if (gameState.getPile1() != null && !gameState.getPile1().isEmpty()) {
      enemies += gameState.getPile1().peek().getName() + ":PILE1:" + gameState.getPile1().size() + ";";
    }
    if (gameState.getPile2() != null && !gameState.getPile2().isEmpty()) {
      enemies += gameState.getPile2().peek().getName() + ":PILE2:" + gameState.getPile2().size() + ";";
    }
    if (gameState.getPile3() != null && !gameState.getPile3().isEmpty()) {
      enemies += gameState.getPile3().peek().getName() + ":PILE3:" + gameState.getPile3().size() + ";";
    }
    sb.append(enemies);


    return sb.toString();
  }

  public String encodeStateForDropEnnemi(GameState gameState) {
    StringBuilder sb = new StringBuilder();
    sb.append("CHOIXPILETODROP|");
    // Stats du joueur
    sb.append(gameState.getPlayer().getLife()).append("|");
    sb.append(gameState.getPlayer().getStrategy()).append("|");
    sb.append(gameState.getPlayer().getRecovery()).append("|");

    // nb d'ennemis dans chaque pile + 1er ennemi de chaque pile
    String enemies = "";
    if (gameState.getPile1() != null && !gameState.getPile1().isEmpty()) {
      enemies += gameState.getPile1().peek().getName() + ":PILE1:" + gameState.getPile1().size() + ";";
    }
    if (gameState.getPile2() != null && !gameState.getPile2().isEmpty()) {
      enemies += gameState.getPile2().peek().getName() + ":PILE2:" + gameState.getPile2().size() + ";";
    }
    if (gameState.getPile3() != null && !gameState.getPile3().isEmpty()) {
      enemies += gameState.getPile3().peek().getName() + ":PILE3:" + gameState.getPile3().size() + ";";
    }
    sb.append(enemies);


    return sb.toString();
  }

  public String encodeStateForEngage(GameState gameState) {
    StringBuilder sb = new StringBuilder();
    sb.append("ENGAGE").append(gameState.getEngageAssignStep()).append("|");
    sb.append(gameState.getPlayer().getLife()).append("|");
    sb.append(gameState.getPlayer().getStrategy()).append("|");
    long reserveDice = gameState.getDicePool().stream()
        .filter(d -> d.getState() == DiceState.RESERVE)
        .count();
    long engagedDice = gameState.getDicePool().stream()
        .filter(d -> d.getState() == DiceState.ENGAGE)
        .count();
    sb.append(reserveDice).append("|").append(engagedDice).append("|");
    String enemies = gameState.getActiveEnnemis().stream()
        .filter(e -> !e.isDefeatedFlag())
        .map(e -> e.getName() + ":" + e.getCurrentLife() + ":" +
            e.getEffects().stream()
                .map(effect -> effect.getType().name() + (effect.isActivated() ? ":1" : ":0"))
                .collect(Collectors.joining(",")))
        .collect(Collectors.joining(";"));
    sb.append(enemies);
    // mission active
    Mission activeMission = gameState.getActiveMission();
    sb.append("|MISSION:").append(activeMission != null ? activeMission.getName() : "NONE");

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
        .map(e -> e.getName() + ":" + e.getCurrentLife() + ":" +
            e.getEffects().stream()
                .map(effect -> effect.getType().name() + (effect.isActivated() ? ":1" : ":0"))
                .collect(Collectors.joining(",")))
        .collect(Collectors.joining(";"));
    sb.append(enemies);
    // mission active
    Mission activeMission = gameState.getActiveMission();
    sb.append("|MISSION:").append(activeMission != null ? activeMission.getName() : "NONE");

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
    // mission active
    Mission activeMission = gameState.getActiveMission();
    sb.append("|MISSION:").append(activeMission != null ? activeMission.getName() : "NONE");

    return sb.toString();
  }

  public String encodeStateWithBoss(GameState gameState) {
    StringBuilder sb = new StringBuilder();
    sb.append(gameState.getPlayer().getLife()).append("|");
    sb.append(gameState.getPlayer().getStrategy()).append("|");
    sb.append(gameState.getPlayer().getRecovery()).append("|");
    long reserveDiceCount = gameState.getDicePool().stream()
        .filter(d -> d.getState() == DiceState.RESERVE)
        .count();
    sb.append(reserveDiceCount).append("|");
    // boss révélé ?
    if (gameState.isRevealedBoss()) {
      sb.append(gameState.getBossPile().peek().getName()).append(":BOSS");
    } else {
      sb.append("UNKNOWN_BOSS");
    }
    sb.append(";");
    String pile1 = !gameState.getPile1().isEmpty() ? gameState.getPile1().peek().getName() : "NONE";
    String pile2 = !gameState.getPile2().isEmpty() ? gameState.getPile2().peek().getName() : "NONE";
    String pile3 = !gameState.getPile3().isEmpty() ? gameState.getPile3().peek().getName() : "NONE";
    sb.append(pile1).append(";").append(pile2).append(";").append(pile3);
    // nombre de missions
    sb.append("|MISSION NUMBER:").append(gameState.numberOfMissions());

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
    // mission active
    Mission activeMission = gameState.getActiveMission();
    sb.append("|MISSION:").append(activeMission != null ? activeMission.getName() : "NONE");

    return sb.toString();
  }

  public String encodeStateForLunoculation(GameState gameState) {
    // dépend des ennemis actifs, avec leurs effets actifs
    StringBuilder sb = new StringBuilder();
    sb.append("LUNOCULATION|");
    gameState.getActiveEnnemis().forEach(ennemi -> {
      sb.append(ennemi.getName()).append(":").append(ennemi.getClassValue()).append(":");
      String effects = ennemi.getEffects().stream()
          .map(effect -> effect.getType().name() + (effect.isActivated() ? ":1" : ":0"))
          .collect(Collectors.joining(","));
      sb.append(effects).append(";");
    });
    return sb.toString();
  }
}
