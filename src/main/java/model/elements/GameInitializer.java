package model.elements;

import static model.ennemis.EnnemiType.*;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import java.util.Random;
import model.Avatar;
import model.CustomDice;
import model.Dice;
import model.DiceColor;
import model.GameState;
import model.Player;
import model.ennemis.Ennemi;
import model.ennemis.EnnemiType;
import model.items.Item;
import model.missions.Mission;
import model.missions.MissionSupplier;
import model.random.CustomRandom;

/**
 * Fabrique pour créer l'état initial du jeu.
 */
public class GameInitializer {
  private static final Avatar INITIAL_AVATAR = Avatar.VALKYRIE;
  private static final int INITIAL_LIFE = 2;
  private static final int INITIAL_STRATEGY = 2;
  private static final int INITIAL_RECOVERY = 2;
  private static final int INITIAL_ENNEMIS_NUMBER_PER_PILE = 1;

  private static final int INITIAL_MISSIONS_NUMBER = 1;

  public static final List<EnnemiType> ennemis = List.of(
      ACOLYTE_ESSAIM,
      APOTRE_ESSAIM,
      ARACHNOPOULPE,
      ASSERVI,
      CALAMARAIGNEE,
      CAMOUFLARD,
      CIVIL_ASSERVI,
      DUODRONE,
      ESCROCAFARD,
      FANATIQUE_ESSAIM,
      GROS_SCARAB,
      GUEPE_CHERCHEUSE,
      GUEPE_DECHAINEE,
      ICHORKYSTE,
      LARVE_A_MOELLE,
      LOCUSTE_DE_CHAIR,
      MACHOIRE_MASSACREUSE,
      MACHOIRE_SANGLANTE,
      MANTE_EGORGEUSE,
      MANTE_PURULENTE,
      MAREE_DE_LARVES,
      MILITAIRE_ASSERVI,
      MOUCHE_DES_CRATERES,
      MOUCHE_EVENTREUSE,
      MOUCHE_TRUCIDEUSE,
      PARAKYSTE,
      PISTOGUEPE,
      PLASMAKYSTE,
      PORTE_SPORES_CHERCHEUR,
      PORTE_SPORES_EXPECTORANT,
      PORTE_SPORES_FRENETIQUE,
      RAMPECRATERE,
      RIPOSTEUR_ESSAIM,
      RODECRATERE,
      SENTINELLE_ASSIMILEE,
      SENTINELLE_LUNAIRE,
      SENTINELLE_PERDUE,
      SOLODRONE,
      SPORE_DE_GUERRE_TOTALE,
      SPORE_GUERRIERE,
      TAS_DE_VERS,
      TORTUE_MORTIER,
      TORTUEUSE,
      TRIODRONE,
      VAINCAFARD
  );

  public static final List<EnnemiType> bossList = List.of(
      BETE_ALPHA,
      MONARQUE_RUCHE
  );

  private GameInitializer() {
    // Classe utilitaire non instanciable
  }

  /**
   * Crée un nouvel état de jeu avec la configuration initiale.
   */
  public static GameState createInitialGameState(CustomRandom customRandom) {
    Player player = createPlayer();
    List<Dice> dicePool = createDicePool();
    List<Deque<Ennemi>> piles = createEnemyPiles(customRandom);
    Queue<Ennemi> bossPile = createBossPile(customRandom);
    Queue<Mission> missions = createMissions(customRandom);

    return new GameState(
        player,
        dicePool,
        piles.get(0),
        piles.get(1),
        piles.get(2),
        bossPile,
        missions,
        customRandom
    );
  }

  private static Player createPlayer() {
    return new Player(INITIAL_AVATAR, INITIAL_LIFE, INITIAL_STRATEGY, INITIAL_RECOVERY);
  }

  private static List<Dice> createDicePool() {
    List<Dice> dicePool = new ArrayList<>();

    // Ajouter les dés de chaque couleur (sauf rouge)
    DiceColor[] standardColors = {
        DiceColor.JAUNE,
        DiceColor.VERT,
        DiceColor.BLEU,
        DiceColor.VIOLET
    };

    /*for (DiceColor color : standardColors) {
      for (int i = 0; i < DICE_PER_COLOR; i++) {
        dicePool.add(new Dice(color));
      }
    }

    // Ajouter le dé rouge unique
    dicePool.add(new Dice(DiceColor.ROUGE));*/

    dicePool.add(new Dice(DiceColor.JAUNE));

    Map<Integer, Integer> faces = new HashMap<>();
    faces.put(0,3);
    faces.put(1,3);
    faces.put(2,1);

    CustomDice customDice = new CustomDice("BLEU2", DiceColor.BLEU, 3, 6, faces);
    dicePool.add(customDice);
    return dicePool;
  }

  private static List<Deque<Ennemi>> createEnemyPiles(CustomRandom random) {
    Deque<Ennemi> pile1 = new LinkedList<>();
    Deque<Ennemi> pile2 = new LinkedList<>();
    Deque<Ennemi> pile3 = new LinkedList<>();

    // Chaque pile doit avoir le même nombre d'ennemis, à 1 près

    for (int i=0; i< INITIAL_ENNEMIS_NUMBER_PER_PILE; i++) {
      Ennemi e1 = new Ennemi(ennemis.get(random.nextInt(ennemis.size())), 1);
      Ennemi e2 = new Ennemi(ennemis.get(random.nextInt(ennemis.size())), 2);
      Ennemi e3 = new Ennemi(ennemis.get(random.nextInt(ennemis.size())), 3);

      if (i >= 15) {
        int upgradeLevel = i / 15;
        e1.upgradeBasicLife(upgradeLevel, random);
        e2.upgradeBasicLife(upgradeLevel, random);
        e3.upgradeBasicLife(upgradeLevel, random);
      }

      pile1.add(e1);
      pile2.add(e2);
      pile3.add(e3);
    }

    return List.of(pile1, pile2, pile3);
  }

  private static Queue<Ennemi> createBossPile(CustomRandom random) {
    List<EnnemiType> shuffledBosses = new ArrayList<>(bossList);
    random.shuffle(shuffledBosses);
    Queue<Ennemi> bossPile = new LinkedList<>();
    for (EnnemiType bossType : shuffledBosses) {
      bossPile.add(new Ennemi(bossType, 0)); // Les boss n'ont pas de pile spécifique, on peut leur attribuer 0 ou une valeur spéciale
    }
    return bossPile;
  }

  private static Queue<Mission> createMissions(CustomRandom random) {
    Queue<Mission> missions = new LinkedList<>();
    for (int i = 0; i < INITIAL_MISSIONS_NUMBER; i++) {
      missions.add(MissionSupplier.createMission(random.nextInt(MissionSupplier.NUMBER_OF_MISSIONS) + 1));
    }
    return missions;
  }
}
