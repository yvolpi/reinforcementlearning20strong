package model.elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.stream.Collectors;
import model.Dice;
import model.DiceColor;
import model.DiceState;
import model.GameState;
import model.Player;
import model.ai.GameAi;
import model.effets.ennemi.EnnemyEffect;
import model.effets.ennemi.EnnemyEffectType;
import model.effets.ennemi.GuepeChercheuseEffect;
import model.effets.ennemi.KeepDiceInExhaustEffect;
import model.effets.ennemi.SkipRecoverPhaseEffect;
import model.ennemis.Ennemi;
import model.ennemis.EnnemiType;
import model.items.Item;
import model.recompenses.Reward;
import model.recompenses.RewardType;

/**
 * Service gérant les différentes phases du jeu 20 Strong.
 */
public class GameService {

  public static List<Ennemi> getAvailableEnemiesForActivation(GameState gameState) {
    List<Ennemi> mandatoryEnemies = gameState.getMandatoryEnnemies();
    if (!mandatoryEnemies.isEmpty()) {
      return mandatoryEnemies;
    }
    return gameState.getFirstEnemiesOfEachNonEmptyPile();
  }

  public static void triggeredAutomaticItemsEffectBeforeActivation(GameState gameState) {
    for (Item item : gameState.getPlayer().getItems()) {
      item.triggeredBeforeActivationPhase(gameState);
    }
  }

  /**
   * Active un ennemi depuis une pile donnée.
   */
  public static Ennemi activateEnemy(GameState gameState, int pileNumber) {
    Ennemi ennemi = pollEnemyFromPile(gameState, pileNumber);

    if (ennemi == null) {
      throw new IllegalStateException("La pile " + pileNumber + " est vide.");
    }

    gameState.addActiveEnnemi(ennemi);
    return ennemi;
  }

  private static Ennemi pollEnemyFromPile(GameState gameState, int pileNumber) {
    return switch (pileNumber) {
      case 1 -> gameState.getPile1().poll();
      case 2 -> gameState.getPile2().poll();
      case 3 -> gameState.getPile3().poll();
      default -> throw new IllegalArgumentException("Numéro de pile invalide : " + pileNumber);
    };
  }

  /**
   * Active le boss
   */

  public static void activateBoss(GameState gameState) {
    Ennemi boss = gameState.getBossPile().poll();
    gameState.addActiveEnnemi(boss);

    // Récupère le nombre d'ennemis à attirer (gardes du boss)
    int nbToActivate = boss.getForcedActivations();

    // moins une activation s'il y a le bonus FleauLunaire
    if (gameState.getBonusEffectsTurn().stream().anyMatch(effect -> effect.getName().equals("FleauLunaireEffect"))) {
      nbToActivate = Math.max(0, nbToActivate - 1);
    }

    List<EnnemiType> allPossibleEnnemis = GameInitializer.ennemis;

    // mélanger puis en tirer en fonction du nb d'activations supplémentaire du boss
    List<EnnemiType> shuffled = new ArrayList<>(allPossibleEnnemis);
    gameState.getRandom().shuffle(shuffled);

    // Sélectionne les nbToActivate premiers
    for (int i = 0; i < nbToActivate && i < shuffled.size(); i++) {
      EnnemiType type = shuffled.get(i);
      Ennemi garde = new Ennemi(type, 0); // 0 = niveau, à adapter si besoin
      gameState.addActiveEnnemi(garde);
    }
    gameState.setActivatedBoss(true);

  }

  // ===== Phase d'usage d'objets =====

  /**
   * Phase d'usage d'objets : les objets choisis par l'IA sont utilisés.
   */
  public static void useItemsPhase(GameState gameState, List<GameAction> actions, GameAi ai) {
    // inventaire du joueur
    /*System.out.println("Inventaire du joueur : " + gameState.getPlayer().getItems().stream()
        .map(Item::getName)
        .collect(Collectors.joining(", ")));*/
    for (GameAction action : actions) {
      if ((action.getType() == GamePhase.USE_ITEM_BEFORE_ENGAGE
          || action.getType() == GamePhase.USE_ITEM_BEFORE_ASSIGN)
          && action.getItem() != null) {
        //System.out.println("Utilisation de l'objet : " + action.getItem().getName());
        action.getItem().use(gameState.getPlayer(), gameState);
        if (action.getItem().getName().equals("Lunoculation")) {
          // à l'ia de décider quel effet ennemi il désactive
          GameAction lunoculationAction = ai.chooseEnnemiEffectToDesactivate(gameState);
          lunoculationAction.getTarget().getEffects().stream()
              .filter(ennemyEffect -> ennemyEffect.getName().equals(lunoculationAction.getEnnemyEffect().getName()))
              .forEach(EnnemyEffect::desactivate);

        }

      }
    }
  }

  // ===== Phase d'Engagement =====

  /**
   * Phase d'engagement : les dés choisis par l'IA sont engagés, puis lancés.
   */
  public static void engageDicePhase(GameState gameState, List<GameAction> actions) {
    gameState.checkIfErrorBetweenPoolAndEngagedAndExhaustedDice();
    if (gameState.getBonusEffectsTurn().stream().anyMatch(effect -> effect.getClass().getSimpleName().equals("IchorVeriteEffect"))) {
      // ne pas relancer les touches, excepté celle du dé rouge
      List<Dice> dicesNotToReroll = gameState.getEngagedDices().stream()
          .filter(dice -> (dice.getColor() == DiceColor.ROUGE && dice.getLastRoll() == dice.getFaces()[5])
              || (dice.getColor() != DiceColor.ROUGE && dice.getLastRoll() != 0))
          .toList();

      engageSelectedDice(gameState, actions);
      rollEngagedDice(gameState, dicesNotToReroll);
    } else {
      rollEngagedDice(gameState);
    }
    new ArrayList<>(gameState.getActiveEnnemis()).forEach(ennemi ->
        {
          //System.out.println("Effets après engagement et lancer pour l'ennemi : " + ennemi.getName());
          ennemi.getEffects()
              .stream()
              .filter(EnnemyEffect::isActivated)
              .forEach(effect -> effect.applyAfterEngagementAndRoll(gameState, ennemi));
          gameState.checkIfErrorBetweenPoolAndEngagedAndExhaustedDice();
        }
    );
    gameState.checkIfErrorBetweenPoolAndEngagedAndExhaustedDice();
    gameState.setPhase(GamePhase.ASSIGN_DICE);
  }

  private static void engageSelectedDice(GameState gameState, List<GameAction> actions) {
    for (GameAction action : actions) {
      if (action.getType() == GamePhase.ENGAGE_DICE && action.getDice() != null) {
        Dice dice = action.getDice();
        dice.setState(DiceState.ENGAGE);
        gameState.getEngagedDices().add(dice);
        gameState.getDicePool().remove(dice);
      }
    }
  }

  private static void rollEngagedDice(GameState gameState) {
    gameState.getEngagedDices().stream()
        .filter(dice -> dice.getState() == DiceState.ENGAGE)
        .forEach(dice -> dice.roll(gameState.getRandom()));
  }

  private static void rollEngagedDice(GameState gameState, List<Dice> diceWhichDoNotRoll) {
    gameState.getEngagedDices().stream()
        .filter(dice -> dice.getState() == DiceState.ENGAGE && !diceWhichDoNotRoll.contains(dice))
        .forEach(dice -> dice.roll(gameState.getRandom()));
  }

  // ===== Phase d'Assignation =====

  /**
   * Phase d'assignation : les dés engagés ayant fait 1+ dégâts sont assignés aux ennemis.
   */

  public static void assignDicePhase(GameState gameState, List<GameAction> actions) {
    List<Dice> assignableDice = getAssignableDice(gameState);
    Map<Dice, Ennemi> assignments = new ArrayList<>(actions).stream()
        .filter(action -> action.getType() == GamePhase.ASSIGN_DICE)
        .filter(action -> action.getDice() != null && action.getTarget() != null)
        .filter(action -> assignableDice.contains(action.getDice()))
        .collect(Collectors.toMap(GameAction::getDice, GameAction::getTarget));

    processAssignments(gameState, assignments);

    gameState.setEngageAssignStep(gameState.getEngageAssignStep() + 1);
  }

  private static List<Dice> getAssignableDice(GameState gameState) {
    return gameState.getEngagedDices().stream()
        .filter(dice -> dice.getLastRoll() >= 1)
        .filter(dice -> dice.getState() == DiceState.ENGAGE)
        .collect(Collectors.toList());
  }

  private static void processAssignments(GameState gameState, Map<Dice, Ennemi> assignments) {
    for (Map.Entry<Dice, Ennemi> entry : assignments.entrySet()) {
      Dice dice = entry.getKey();
      Ennemi ennemi = entry.getValue();
      if (ennemi.isDefeatedFlag()) {
        // dé gaspillé
        gameState.setWastedDiceThisTurn(gameState.getWastedDiceThisTurn() + 1);
      }

      assignDiceToEnemy(gameState, dice, ennemi);
      checkAndApplyDefeat(gameState, ennemi);
    }
  }

  private static void assignDiceToEnemy(GameState gameState, Dice dice, Ennemi ennemi) {
    for (EnnemyEffect effect : ennemi.getEffects()) {
      if (effect.isActivated() && (!effect.canAssignDice(gameState, dice) || !effect.canAssignDiceToThisEnnemi(gameState, dice, ennemi))) {
        // Refuser l’assignation
        return;
      }
    }
    for (EnnemyEffect effect : ennemi.getEffects()) {
      if (effect.isActivated()) {
        effect.receiveDamage(dice.getLastRoll());
      }
    }
    dice.setState(DiceState.ASSIGNE);
    ennemi.assignDice(dice);

  }

  private static void checkAndApplyDefeat(GameState gameState, Ennemi ennemi) {
    ennemi.computeCurrentLife(gameState);
    if (ennemi.isDefeated(gameState) && !ennemi.isDefeatedFlag()) {
      ennemi.setDefeated(true);
      if(gameState.getNbEnnemisKilled() > 0 && Objects.equals(ennemi.getName(),
          EnnemiType.ARACHNOPOULPE.name())) {
        int toRemove = 2;
        List<Deque<Ennemi>> piles = List.of(
            gameState.getPile1(),
            gameState.getPile2(),
            gameState.getPile3()
        );
        for (Queue<Ennemi> pile : piles) {
          while (toRemove > 0 && !pile.isEmpty()) {
            pile.remove();
            toRemove--;
          }
          if (toRemove == 0) break;
        }
      }
      if(gameState.getNbEnnemisKilled() == 0 && Objects.equals(ennemi.getName(),
          EnnemiType.CIVIL_ASSERVI.name())) {
        // Si le civil asservi est le premier ennemi vaincu, il retourne sur la première pile non vide
        // Pénalité
        gameState.setPenalityKillCivilAsserviFirst(true);

        //les dés assignés à cet ennemi sont épuisés
        ennemi.getAssignedDice().forEach(dice -> {
          dice.setState(DiceState.EPUISE);
          gameState.getExhaustedDice().add(dice);
          gameState.getEngagedDices().remove(dice);
        });
        gameState.getActiveEnnemis().remove(ennemi); // on le retire temporairement de la zone active pour éviter les interactions avec les effets des ennemis actifs


        if (!gameState.getPile1().isEmpty()) {
          gameState.getPile1().addFirst(ennemi);
          ennemi.setPileNumber(1);

        } else if (!gameState.getPile2().isEmpty()) {
          gameState.getPile2().addFirst(ennemi);
          ennemi.setPileNumber(2);
        } else if (!gameState.getPile3().isEmpty()) {
          gameState.getPile3().add(ennemi);
          ennemi.setPileNumber(3);
        } else {
          gameState.getActiveEnnemis().remove(ennemi);
        }
      }
      if (ennemi.getClassValue() == 3) {
        // Le boss est vaincu
        gameState.setBosskilled(true);
      }
      gameState.setNbEnnemisKilled(gameState.getNbEnnemisKilled() + 1);
      applyInstantReward(gameState, ennemi);
    }
  }

  private static void applyInstantReward(GameState gameState, Ennemi ennemi) {
    Reward reward = ennemi.getReward();
    if (reward == null) {
      return;
    }

    if (reward.getType() == RewardType.INSTANT) {
      reward.apply(gameState);
    }
    // si le joueur a le réplicateur et s'il n'y a pas d'ennemi actif vivant avec BlockUseItemsEffect :
    // son réplicateur est défaussé et il gagne la récompense de l'ennemi vaincu une seconde fois

    Item replicateur = gameState.getPlayer().getItems().stream()
        .filter(item -> item.getName().equals("Replicateur"))
        .findFirst()
        .orElse(null);
    if (replicateur != null
        && gameState.getActiveEnnemis().stream()
        .filter(blockerEnnemi -> !blockerEnnemi.isDefeatedFlag())
        .flatMap(blockerEnnemi -> blockerEnnemi.getEffects().stream())
        .noneMatch(effect -> effect.isActivated() && effect.getClass().getSimpleName().equals("BlockUseItemsEffect"))) {
      reward.apply(gameState);
      gameState.getPlayer().removeItem(replicateur);
    }
  }

  // ===== Phase de Dégâts =====

  /**
   * Le joueur subit les dégâts des ennemis actifs non vaincus.
   */
  public static void sufferDamagePhase(GameState gameState) {
    int totalDamage = calculateTotalDamage(gameState);
    gameState.getPlayer().loseLife(totalDamage);

    System.out.println("Le joueur subit " + totalDamage + " dégâts.");
  }

  public static void applyEnnemisSubsequentEffects(GameState gameState) {
    for (Ennemi ennemi : gameState.getActiveEnnemis()) {
      for (EnnemyEffect effect : ennemi.getEffects()) {
        if (effect.getType() == EnnemyEffectType.SUBSEQUENT && effect.isActivated()) {
          effect.apply(gameState.getPlayer(), gameState, ennemi);
        }
      }
    }
  }

  private static int calculateTotalDamage(GameState gameState) {
    boolean monarquePresent = gameState.getActiveEnnemis().stream()
        .anyMatch(ennemi -> !ennemi.isDefeatedFlag() && EnnemiType.MONARQUE_RUCHE.name().equals(ennemi.getName()));

    if (monarquePresent) {
      // Tous les ennemis survivants infligent 1 dégât
      return (int) gameState.getActiveEnnemis().stream()
          .filter(ennemi -> !ennemi.isDefeatedFlag())
          .count();
    }

    return gameState.getActiveEnnemis().stream()
        .filter(ennemi -> !ennemi.isDefeatedFlag())
        .mapToInt(Ennemi::getAttack)
        .sum();
  }

  // ===== Phase d'Épuisement =====

  /**
   * Tous les dés engagés passent en zone épuisée.
   */
  public static void exhaustionPhase(GameState gameState) {
    //gameState.checkIfErrorBetweenPoolAndEngagedAndExhaustedDice();
    List<Dice> engagedDice = gameState.getEngagedDices();

    List<Dice> toExhaust = new ArrayList<>();

    for (Dice dice : engagedDice) {
      boolean keepEngaged = false;
      if (dice.getState() != DiceState.ASSIGNE) {
        // dé non assigné => gaspillage
        gameState.setWastedDiceThisTurn(gameState.getWastedDiceThisTurn() + 1);
      } else {
        for (Ennemi ennemi : gameState.getActiveEnnemis()) {
          if (ennemi.getAssignedDice().contains(dice)) {
            for (EnnemyEffect effect : ennemi.getEffects()) {
              if (effect instanceof KeepDiceInExhaustEffect) {
                keepEngaged = true;
                break;
              }
            }
            if (!keepEngaged) {
              ennemi.getAssignedDice().remove(dice);
            }
          }
        }
      }
      if (!keepEngaged) {
        dice.setState(DiceState.EPUISE);
        gameState.getExhaustedDice().add(dice);
        toExhaust.add(dice);
      }
    }
    engagedDice.removeAll(toExhaust); // Seuls les dés non épuisés restent engagés
    gameState.setPhase(GamePhase.RECOVER_DICE);
  }

  // ===== Phase de Récupération =====

  /**
   * L'IA choisit des dés épuisés à récupérer selon la statistique récupération du joueur.
   */
  public static void recoverDicePhase(GameState gameState, GameAi ai) {
    if (!canRecoverDices(gameState)) {
      return;
    }

    int recoveryAmount = gameState.getPlayer().getRecovery();
    List<Dice> exhaustedDice = gameState.getExhaustedDice();

    if (exhaustedDice.isEmpty() || recoveryAmount <= 0) {
      return;
    }

    List<Dice> diceToRecover = ai.chooseDiceToRecover(exhaustedDice, recoveryAmount);

    diceToRecover.forEach(dice -> {
      dice.setState(DiceState.RESERVE);
      gameState.getDicePool().add(dice);
    });

    exhaustedDice.removeAll(diceToRecover);
    gameState.setPhase(GamePhase.ACTIVATE_PILE);
  }

  // ===== Phase de récompenses =====

  public static void applyPendingRewards(GameState gameState, GameAi ai) {
    for (Ennemi ennemi : gameState.getActiveEnnemis()) {
      Reward reward = ennemi.getReward();
      if (ennemi.isDefeatedFlag()
          && reward != null
          && reward.getType() != RewardType.INSTANT) {
        if (gameState.getPlayer().getStrategy() > gameState.getPlayer().getItems().size()) {
          reward.apply(gameState);
        } else {
          // l'ia décide d'enlever un item ou pas pour récupérer la nouvelle récompense
          executeItemManager(gameState, ai, reward);
          if (gameState.getPlayer().getStrategy() > gameState.getPlayer().getItems().size()) {
            reward.apply(gameState);
          }
        }

      }
    }
  }

  public static void executeItemManager(GameState game, GameAi ai, Reward reward) {
    GameAction actionChoisie = ai.chooseItemToRemove(game, reward);
    GameService.applyItemToRemove(game, actionChoisie);

  }

  public static void applyItemToRemove(GameState gameState, GameAction action) {
    if (action.getItem() != null) {
      gameState.getPlayer().removeItem(action.getItem());
    }
  }


  // ===== Phase d'Entretien =====

  /**
   * Vide la zone des ennemis actifs en fin de tour.
   */
  public static void clearActiveZone(GameState gameState) {
    gameState.getActiveEnnemis().clear();
  }

  // ===== Évaluation =====

  /**
   * Évalue la récompense d'un tour basé sur les changements de PV et de dés en réserve.
   */
  public static int evaluateTurn(GameState previous, GameState current) {
    Player currentPlayer = current.getPlayer();
    int deltaLife = currentPlayer.getLife() - previous.getPlayer().getLife();
    int gainDes = currentPlayer.getRecovery() - current.getWastedDiceThisTurn();
    int penalityForKillCivilAsserviFirst = current.isPenalityKillCivilAsserviFirst()? -50 : 0;

    // Pondération : perte de PV très pénalisante
    return penalityForKillCivilAsserviFirst + 10 * deltaLife + gainDes + 5 * currentPlayer.getStrategy() + 4 * currentPlayer.getRecovery();
  }

  // ===== Utilitaires =====

  /**
   * Vérifie si un dé est interdit par les effets des ennemis actifs.
   */
  public static boolean isDiceAutorized(Dice dice, List<Ennemi> activeEnnemis) {
    for (Ennemi ennemi : activeEnnemis) {
      for (EnnemyEffect effect : ennemi.getEffects()) {
        if (shouldSkipEffect(ennemi, effect)) {
          continue;
        }
        if (!effect.canEngage(dice)) {
          return false;
        }
      }
    }
    return true;
  }

  private static boolean shouldSkipEffect(Ennemi ennemi, EnnemyEffect effect) {
    // Les effets permanents ne s'appliquent plus si l'ennemi est vaincu
    return (EnnemyEffectType.PERMANENT.equals(effect.getType()) && ennemi.isDefeatedFlag()) || !effect.isActivated();
  }

  private static boolean canRecoverDices(GameState gameState) {
    for (Ennemi ennemi : gameState.getActiveEnnemis()) {
      for (EnnemyEffect effect : ennemi.getEffects()) {
        if (effect instanceof SkipRecoverPhaseEffect) {
          if (effect.getType() == EnnemyEffectType.SUBSEQUENT) {
            // S'applique même si l'ennemi est vaincu
            return false;
          } else if (!ennemi.isDefeatedFlag()) {
            // S'applique seulement si l'ennemi est vivant
            return false;
          }
        }
      }
    }
    return true;
  }

  public static void clearDeadNonBossEnnemis(GameState game) {
    game.getActiveEnnemis().removeIf(ennemi -> ennemi.isDefeatedFlag() && ennemi.getClassValue() != 3);
  }

  public static void activeEffectsBeforeAllEngagement(GameState game) {
    for (int i=0; i<game.getActiveEnnemis().size(); i++) {
      Ennemi ennemi = game.getActiveEnnemis().get(i);
      if (!ennemi.isDefeatedFlag()) {
        for (EnnemyEffect effect : ennemi.getEffects()) {
          if (effect.isActivated()) {
            effect.applyBeforeAllEngagement(game);
          }
        }
      }
    }
  }

  public static void activeEffectsBeforeEngagement(GameState game) {
    for (int i=0; i<game.getActiveEnnemis().size(); i++) {
      Ennemi ennemi = game.getActiveEnnemis().get(i);
      if (!ennemi.isDefeatedFlag()) {
        for (EnnemyEffect effect : ennemi.getEffects()) {
          if (effect.isActivated()) {
            effect.applyBeforeEngagement(game);
          }
        }
      }
    }
  }
}
