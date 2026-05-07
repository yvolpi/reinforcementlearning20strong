package model.ai;

import java.util.List;
import java.util.stream.Collectors;

import model.elements.GameAction;
import model.ennemis.Ennemi;

public class ActionKeyEncoder {

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

  public static String encodeActivateAction(GameAction action) {
    if (action == null || action.getTarget() == null)
      return "NONE";
    Ennemi target = action.getTarget();
    return "ACTIVATE:PILE_" + target.getPileNumber();
  }
}
