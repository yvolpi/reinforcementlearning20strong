package model.ai;

import java.util.List;
import model.Dice;
import model.elements.GameAction;
import model.elements.GamePhase;
import model.ennemis.Ennemi;
import model.items.Item;

public class ActionDecoder {

  public static List<GameAction> decodeEngageAction(List<Dice> availableDice,String encoded) {
    String[] parts = encoded.split(":");
    int numEngagement = Integer.parseInt(parts[0].replace("ENGAGE", ""));
    if (parts.length < 2 || parts[1].equals("NONE")) {
      return List.of(); // Aucun dé engagé
    }
    String[] colorCounts = parts[1].split(";");
    List<GameAction> actions = new java.util.ArrayList<>();
    List<Dice> pool = new java.util.ArrayList<>(availableDice);

    for (String colorCount : colorCounts) {
      if (colorCount.isEmpty()) continue;
      String[] cc = colorCount.split("=");
      if (cc.length != 2) continue;
      String color = cc[0];
      int count = Integer.parseInt(cc[1]);
      for (int i = 0; i < count; i++) {
        // Cherche un dé de la bonne couleur dans la pool
        for (java.util.Iterator<Dice> it = pool.iterator(); it.hasNext(); ) {
          Dice d = it.next();
          if (d.getColor().name().equals(color)) {
            // À adapter selon ton constructeur GameAction
            actions.add(new GameAction( GamePhase.ENGAGE_DICE,d));
            it.remove();
            break;
          }
        }
      }
    }
    return actions;
  }

  public static List<GameAction> decodeAssignActions(List<Dice> assignableDice, String encodedActions, List<Ennemi> ennemis) {
    List<GameAction> actions = new java.util.ArrayList<>();
    if (encodedActions == null || encodedActions.isEmpty()) return actions;

    //crée une copie de la liste assignableDice modifiable
    List<Dice> copyAssignableDice = new java.util.ArrayList<>(assignableDice); // copie modifiable


    // On enlève le préfixe "ASSIGNx:"
    String[] prefixSplit = encodedActions.split(":", 2);
    if (prefixSplit.length < 2 || prefixSplit[1].equals("NONE")) return actions;

    String actionsPart = prefixSplit[1]; // ex: GREEN:1->Orc;GREEN:2->Gobelin
    String[] assignments = actionsPart.split(";");

    for (String assignment : assignments) {
      if (assignment.isEmpty()) continue;
      String[] mainParts = assignment.split("->");
      if (mainParts.length != 2) continue;
      String dicePart = mainParts[0]; // ex: GREEN:2
      String targetName = mainParts[1];

      String[] diceParts = dicePart.split(":");
      if (diceParts.length != 2) continue;
      String color = diceParts[0];
      int value;
      try {
        value = Integer.parseInt(diceParts[1]);
      } catch (NumberFormatException e) {
        continue;
      }

      // Trouve le dé correspondant dans copyAssignableDice
      Dice foundDice = null;
      for (Dice d : copyAssignableDice) {
        if (d.getColor().name().equals(color) && d.getLastRoll() == value) {
          foundDice = d;
          break;
        }
      }
      if (foundDice == null) continue;

      // Trouve la cible correspondante
      Ennemi foundTarget = null;
      for (Ennemi e : ennemis) {
        if (e.getName().equals(targetName)) {
          foundTarget = e;
          break;
        }
      }
      if (foundTarget == null) continue;

      actions.add(new GameAction(GamePhase.ASSIGN_DICE, foundDice, foundTarget));
      // ne pas oublier d'enlever le dé à copyAssignableDice car un dé ne peut être être assigné qu'une fois
      copyAssignableDice.remove(foundDice);
    }
    return actions;
  }

  public static List<GameAction> decodeUseActions(List<Item> usableItems, String encodedActions, GamePhase phase) {
    List<GameAction> actions = new java.util.ArrayList<>();
    if (encodedActions == null || encodedActions.isEmpty()) return actions;

    // On enlève le préfixe "USE_ITEM:" ou "PHASE:"
    String[] prefixSplit = encodedActions.split(":", 2);
    if (prefixSplit.length < 2 || prefixSplit[1].equals("NONE")) return actions;

    List<Item> copyUsableItems = new java.util.ArrayList<>(usableItems); // copie modifiable

    String itemsPart = prefixSplit[1]; // ex: Potion de soin;Elixir de force
    String[] itemNames = itemsPart.split(";");

    for (String itemName : itemNames) {
      if (itemName.isEmpty()) continue;
      // Trouve l'item correspondant dans copyUsableItems
      Item foundItem = null;
      for (Item i : copyUsableItems) {
        if (i.getName().equals(itemName)) {
          foundItem = i;
          break;
        }
      }
      if (foundItem != null) {
        actions.add(new GameAction(phase, foundItem));
        copyUsableItems.remove(foundItem); // éviter d'utiliser le même item plusieurs fois
      }
    }
    return actions;
  }


}
