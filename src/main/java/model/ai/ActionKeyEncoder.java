package model.ai;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import model.elements.GameAction;
import model.elements.GamePhase;
import model.ennemis.Ennemi;

public class ActionKeyEncoder {

  public static String encodeDecideGiveUpMissionAction(GameAction action) {
    if (action == null || action.getPileNumber() < 0) return "NONE";
    return "GIVE UP MISSION : " + action.isGiveUpMission();
  }

  public static String encodeDecidePileM10Action(GameAction action) {
    if (action == null || action.getPileNumber() < 0) return "NONE";
    return "MISSION M10 : PILE_" + action.getPileNumber();
  }

  public static String encodeActivateBossAction(GameAction action) {
    if (action == null) return "NONE";
    return action.isActivateBoss() ? "ACTIVATE_BOSS" : "NONE";
  }

  public static String encodeActivateAction(GameAction action) {
    if (action == null || action.getTarget() == null) return "NONE";
    return "ACTIVATE:PILE_" + action.getTarget().getPileNumber();
  }

  public static String encodeEngageActions(List<GameAction> actions) {
    if (actions.isEmpty()) return "NONE";
    return actions.stream()
        .map(a -> a.getDice() == null ? "NONE" : a.getDice().getColor().name())
        .collect(Collectors.joining(","));
  }

  public static String encodeAssignActions(List<GameAction> actions) {
    if (actions.isEmpty()) return "NONE";
    return actions.stream()
        .map(ActionKeyEncoder::formatAssignAction)
        .collect(Collectors.joining(","));
  }

  public static String encodeThrowItemAction(GameAction action) {
    if (action.getItem() == null) return "NONE";
    return action.getItem().getName();
  }

  private static String formatAssignAction(GameAction action) {
    String dicePart = action.getDice() == null ? "NONE" : action.getDice().getColor().name();
    String rollPart = String.valueOf(action.getDice().getLastRoll());
    String targetPart = action.getTarget() != null ? action.getTarget().getName() : "NONE";
    return dicePart + ":" + rollPart + "->" + targetPart;
  }

  public static String encodeEngageAction(List<GameAction> comboActions, int engagementNumber) {
    String prefix = "ENGAGE" + engagementNumber + ":";
    if (comboActions.isEmpty()) return prefix + "NONE";
    // compter le nombre de dés engagés par couleur
    Map<String, Long> colorCounts = comboActions.stream()
        .filter(a -> a.getDice() != null)
        .collect(Collectors.groupingBy(
            a -> a.getDice().getColor().name(),
            Collectors.counting()
        ));
    // Générer une chaîne triée pour garantir l’unicité de la clé
    String encoded = colorCounts.entrySet().stream()
        .sorted(Map.Entry.comparingByKey())
        .map(e -> e.getKey() + "=" + e.getValue())
        .collect(Collectors.joining(";"));
    return prefix + encoded;
  }

  public static String encodeAssignActions(List<GameAction> comboActions, int engagementNumber) {
    String prefix = "ASSIGN" + engagementNumber + ":";
    if (comboActions.isEmpty()) return prefix + "NONE";
    String encoded = comboActions.stream()
        .filter(a -> a.getDice() != null && a.getTarget() != null)
        .map(a -> a.getDice().getColor().name() + ":" + a.getDice().getLastRoll() + "->" + a.getTarget().getName())
        .sorted() // ordre alphabétique pour unicité
        .collect(Collectors.joining(";"));
    return prefix + encoded;
  }

  public static String encodeUseItemsAction(List<GameAction> actions, GamePhase phase) {
    String prefix = phase.name() + ":";
    String encoded = "";
    if (actions.isEmpty()) {
      encoded = "NONE";
    } else {
      encoded = actions.stream()
          .filter(a -> a.getItem() != null)
          .map(a -> a.getItem().getName())
          .sorted()
          .collect(Collectors.joining(";"));
    }
    return prefix + encoded;
  }

  public static String encodeRemoveItemAction(GameAction action) {
    String prefix = "THROW_ITEM:";
    if (action == null || action.getItem() == null) return prefix + "NONE";
    return prefix + action.getItem().getName();
  }

  public static String encodeLunoculationAction(GameAction action) {
    String prefix = "EFFECT_TO_DESACTIVATE:";
    // fonction de l'ennemi et de l'effet
    if (action == null || action.getItem() == null) return prefix + "NONE";
    return prefix + action.getTarget().getName() + ":" + action.getEnnemyEffect().getName();
  }

  public static String encodeDropNonMandatoryEnnemiAction(GameAction action) {
    String prefix = "ENNEMI_TO_DROP:";
    // num pile
    if (action == null || action.getTarget() == null) return prefix + "NONE";
    return prefix + "PILE_" + action.getPileNumber();
  }
}
