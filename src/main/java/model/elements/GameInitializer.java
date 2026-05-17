package model.elements;

import static model.ennemis.EnnemiType.*;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import java.util.Random;
import model.Dice;
import model.DiceColor;
import model.GameState;
import model.Player;
import model.ennemis.Ennemi;
import model.ennemis.EnnemiType;
import model.random.CustomRandom;

/**
 * Fabrique pour créer l'état initial du jeu.
 */
public class GameInitializer {

  private static final int INITIAL_LIFE = 2;
  private static final int INITIAL_STRATEGY = 2;
  private static final int INITIAL_RECOVERY = 2;

  private static final int DICE_PER_COLOR = 4;

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
      PORTE_SPORES_FRENETIQUE
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
    return new GameState(
        player,
        dicePool,
        piles.get(0),
        piles.get(1),
        piles.get(2),
        bossPile,
        customRandom
    );
  }

  private static Player createPlayer() {
    return new Player(INITIAL_LIFE, INITIAL_STRATEGY, INITIAL_RECOVERY);
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

    for (DiceColor color : standardColors) {
      for (int i = 0; i < DICE_PER_COLOR; i++) {
        dicePool.add(new Dice(color));
      }
    }

    // Ajouter le dé rouge unique
    dicePool.add(new Dice(DiceColor.ROUGE));

    return dicePool;
  }

  private static List<Deque<Ennemi>> createEnemyPiles(CustomRandom random) {
    Deque<Ennemi> pile1 = new LinkedList<>();
    Deque<Ennemi> pile2 = new LinkedList<>();
    Deque<Ennemi> pile3 = new LinkedList<>();

    // Mélanger les ennemis et les répartir dans les piles
    List<EnnemiType> shuffledEnnemis = new ArrayList<>(ennemis);
    random.shuffle(shuffledEnnemis);
    // Chaque pile doit avoir le même nombre d'ennemis, à 1 près
    int total = shuffledEnnemis.size();
    int pileSize = total / 3;
    int remainder = total % 3; // nombre de piles qui auront un ennemi en plus

    int index = 0;
    for (int i = 0; i < pileSize + (remainder > 0 ? 1 : 0); i++, index++) {
      pile1.add(new Ennemi(shuffledEnnemis.get(index), 1));
    }
    for (int i = 0; i < pileSize + (remainder > 1 ? 1 : 0); i++, index++) {
      pile2.add(new Ennemi(shuffledEnnemis.get(index), 2));
    }
    for (int i = 0; i < pileSize; i++, index++) {
      pile3.add(new Ennemi(shuffledEnnemis.get(index), 3));
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
}
