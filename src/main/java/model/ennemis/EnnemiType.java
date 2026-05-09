package model.ennemis;

import static model.effets.ennemi.EnnemyEffectType.SUBSEQUENT;

import java.util.List;
import model.effets.ennemi.AttackAjustmentEffect;
import model.effets.ennemi.BanishDiceIfKilledEffect;
import model.effets.ennemi.BanishFailedDiceEffect;
import model.effets.ennemi.BlockAssignIfFailEffect;
import model.effets.ennemi.EnnemyEffect;
import model.effets.ennemi.ExhaustHitWhenAssignCritHitEffect;
import model.effets.ennemi.FleeAfterEngagementEffect;
import model.effets.ennemi.ForbidGreenDiceEffect;
import model.effets.ennemi.KeepDiceInExhaustEffect;
import model.effets.ennemi.LimitedDamageEffect;
import model.effets.ennemi.MaxEngagedDicePerTurnEffect;
import model.effets.ennemi.RevealBossEffect;
import model.effets.ennemi.SkipRecoverPhaseEffect;
import model.recompenses.CompteurEtoilesRewerd;
import model.recompenses.EncreToxiqueReward;
import model.recompenses.EnsanglanteuseReward;
import model.recompenses.IncreaseRecoveryReward;
import model.recompenses.LunoculationReward;
import model.recompenses.PizzaReward;
import model.recompenses.RecoverLifeReward;
import model.recompenses.Reward;
import model.recompenses.SushiSpatialReward;

public enum EnnemiType {
  // classe 3 = boss
  ACOLYTE_ESSAIM(1,4,2, List.of(new SkipRecoverPhaseEffect()), 0, false,
      new RecoverLifeReward(2)),
  APOTRE_ESSAIM(1,3,1, List.of(new SkipRecoverPhaseEffect(SUBSEQUENT)), 0, false,
      new RecoverLifeReward(2)),
  ARACHNOPOULPE(
      1, // classe
      2, // pv
      3, // attaque
      List.of(), // effet d’activation spécial
      1, // forcedActivations (oblige à activer un autre ennemi)
      false, // forcedActivationMandatory
      new SushiSpatialReward() // récompense spéciale (voir ci-dessous)
  ),
  ASSERVI(2,4,2, List.of(new FleeAfterEngagementEffect()), 1, false,
      new CompteurEtoilesRewerd()),
  CALAMARAIGNEE(2,7,4, List.of(new RevealBossEffect()), 0, false,
      new EncreToxiqueReward()),
  CAMOUFLARD(1,2,1, List.of(new BanishDiceIfKilledEffect()), 0, false,
      new IncreaseRecoveryReward(1)
      ),
  CIVIL_ASSERVI(1,1,3, List.of(new MaxEngagedDicePerTurnEffect(3)), 1, true,
      new EnsanglanteuseReward()),
  DUODRONE(1,4,2, List.of(new ExhaustHitWhenAssignCritHitEffect()), 0, false,
      new PizzaReward()),

  //  escrocafard : 3,2, c1, non obligatoire, effet : subséquent : bannir tous les dés engagés en échec.
  //  récompense : lunoculation : annulez le ou les effets d'un ennemi de classe 1 ou 2
  ESCROCAFARD(1,3,2, List.of(new BanishFailedDiceEffect(), new ForbidGreenDiceEffect()), 0, false,
      new LunoculationReward()),


  SENTINELLE_PERDUE(1,3,1, List.of(new ForbidGreenDiceEffect()), 0, false,
      new IncreaseRecoveryReward(1)),

  // Boss
  BETE_ALPHA(3,12,2, List.of(new BlockAssignIfFailEffect(), new KeepDiceInExhaustEffect()), 2, false,
      null),

  MONARQUE_RUCHE(3,8,1, List.of(new LimitedDamageEffect(3), new AttackAjustmentEffect(1), new KeepDiceInExhaustEffect()), 2, false,
      null)

  ;

  /*
  Autres ennemis : les 3 autres boss

  mère des spores : 9,0, c3, +1 activation, obligatoire, effet : Après le lancer de dé, vous perdez 1 pv par échec. A chaque étape assigner, vous ne pouvez assigner qu'un dé par couleur
  seigneur de l'essaim : 0,3, +3 activations, c3, obligatoire, effet : s'il reste des ennemis c1 ou c2 invaincus, les touches infligent 0 dégat à ce boss



  plus les autres ennemis non boss :
  escrocafard : 3,2, c1, non obligatoire, effet : subséquent : bannir tous les dés engagés en échec.
  récompense : lunoculation : annulez le ou les effets d'un ennemi de classe 1 ou 2

  fanatique de l'essaim : 5,2, c2, +1 activation, non obligatoire, effet : après chaque lancer, les touches autre que ROUGE deviennent des échecs et vice-versa.
  récompense : fusil blaster : épuiser 3 dés pour vaincre immédiatement un ennemi de classe 1 ou 2

  gros scarab : 4,2, c1, effet : vous ne pouvez assigner des touches aux ennemis que par paires de dés de même couleur
  récompense : 1 point de stratégie

  guêpe chercheuse : 4,2, c1, effet : pour chaque dé assigné à cet ennemi, vous devez défausser un ennemi d'une pile
  récompense : 2 points de vie

  guêpe déchainée : 5,3, c2, +0 activation, non obligatoire, effet : après chaque lancer, vous pouvez assigner un maximum de 2 dés aux ennemis
  récompense : méga-épinéphrine : permanent : après chaque étape de lancer, s'il y a au moins 3 couleurs différentes, on peut échanger un échec en touche

  ichorkyste : 4,0, c2, +0 activation, obligatoire, effet : on ne peut assigner qu'un maximum de 2 dés à cet ennemi
  récompense : ichor de vérité : permanent : pendant l'étape lancer, vous n'êtes pas obligé de relancer les touches non assignés

  larve à moelle : 2,4, c2, +0 activation, non obligatoire, effet : subséquent : si cet ennemi n'est pas vaincu, il retourne sur sa pile et sa récompense devient : fléau lunaire Consommable : vous pouvez diminuer le nb d'ennemis à activer de 1
  récompense : aucune

  locuste de chair : 6,2, c1, +1 activation, non obligatoire, effet : subséquent: maximum : si cet ennemi n'est pas vaincu, banissez le plus fort dé de votre réserve (s’il y en a)
  récompense : 1 point de stratégie

  mâchoire massacreuse : 2,2, c2, +2 activations, non obligatoire, effet : à chaque étape assigner, vous ne pouvez vaincre qu'un ennemi
  récompense : fil à massacre : continue : quand vous vainquez un ennemi c2, vous récupérez 1 pv

  mâchoire sanglante : 4,1, c1, +0 activation, non obligatoire, effet : les touches simples rouges et violettes font 0 dégât
  récompense : tranche-éclipse : continu: vous pouvez épuiser un dé de la réserve. Les touches des dés jaunes font +1 dégât

  mante égorgeuse : 4,1, c1, +0 activation, non obligatoire, effet : les touches critiques ne font pas de dégâts
  récompense : stimulant : consommable : vous récupérez 3 pv

  mante purulente : 5,3, c2, +0 activation, non obligatoire, effet : vous ne pouvez utiliser aucun item
  récompense : amputateur : consommable : banissez les dés les plus faibles de votre réserve pour vaincre un ennemi c1

  marée de larves : 3,1, c1, +2 activations, non obligatoire, effet : vous ne pouvez pas assigner de dé violet aux ennemis
  récompense : réplicateur : consommable : quand vous gagnez une récompense immédiate, vous la gagnez une seconde fois

  militaire asservi : 5,1, c1, +1 activation, non obligatoire, effet : fuit si un lancer de dés donne au moins 2 échecs
  récompense : antenne de sourcier : consommable : épuiser un dé de votre réserve pour régler les pv d'un ennemi c1 ou c2 à 2

  mouche des cratères : 6,1, c1, +0 activation, non obligatoire, effet : les touches jaunes et vertes font +1 dégât
  récompense : récupérez 2 dés

  mouche éventreuse : 6,1, c1, +0 activation, non obligatoire, effet : spécial : quand il est vaincu, un autre ennemi c1 ou c2 est vaincu
  récompense : rayon tracteur : consommable : récupérer 3 dés

  mouche trucideuse : 8,1, c2, +0 activation, non obligatoire, effet : subséquent : si vous avez subi au moins 1 dégât, activez 1 ennemi de plus au prochain tour
  récompense : forteresse portative : consommable : vous récupérez tous les dés épuisés

  parakyste : 3,1,c1, +1 activation, obligatoire, effet : vous ne pouvez assigner qu'une couleur de dé à chaque ennemi
  récompense : pistolet étourdissant : consommable : défaussez un ennemi c2

  pistoguepe : 7,2, c2, +0 activation, non obligatoire, effet : à chaque étape assigner, vous ne pouvez assigner qu'un dé par couleur
  récompense : dard-missile : permanent : si la zone d'épuisement contient au moins 8 dés, les touches bleues font +1 dégât

  plasmakyste : 6,2, c2, +0 activation, obligatoire, effet : cet ennemi ne peut être vaincu qu'à condition d'avoir au moins 4 couleurs de dés assignés
  récompense : adn de la horde : permanent : 1 fois par tour, vous pouvez épuiser un dé en réserve pour transformer une touche en touche critique

  porte spores chercheur : 2,2, c1, +0 activation, non obligatoire, effet : vous ne pouvez engager qu'un maximum de 4 dés
  récompense : grille filtrante : consommable : retourner la 1ere carte de la pile boss.

  porte spores expectorant : 6,2, c2, +0 activation, non obligatoire, effet : vous ne pouvez engager qu'un maximum de 8 dés. Quand vous assignez un dé autre jaune, vous devez aussi assiigner un dé jaune
  récompense : grille filtrante : consommable : retourner la 1ere carte de la pile boss.

  porte spores frénétique : 3,1, c1, +0 activation, non obligatoire, effet : vous ne pouvez engager qu'un maximum de 5 dés
  récompense : grille filtrante : consommable : retourner la 1ere carte de la pile boss.

  rampecratère : 3,1, c1, +1 activation, non obligatoire, effet : subséquent : après chaque lancer, le joueur perd 1 pv par échec
  récompense : jus d'insecte : consommable : récupérez 1 pv par ennemi actif

  riposteur de l'essaim : 5,2, c2, +1 activation, obligatoire, effet : vous ne pouvez pas assigner de touche critique aux ennemis
  récompense : relique de scab : consommable : vous récupérez 5 pv

  rodecratère : 5,2, c2, +0 activation, non obligatoire, effet : après chaque lancer, vous perdez 1 pv pour chaque couleur avec au moins 2 touches
  récompense : mini terraformeuse : permanent : après chaque étape récupérer, vous récupérez 1 dé jaune et un dé vert de la zone épuisée s'il y en a.

  sentinelle assimilée : 3,2,c1, +1 activation, non obligatoire, effet : pour chaque dé que vous engagez, vous devez engager tous les dés de la même couleur
  récompense : 1 point de stratégie

  sentinelle lunaire : 8,2, c2, +0 activation, non obligatoire, effet : aucun
  récompense : récupérez jusqu'à 5 dés

  solodrone : 4,1, c1, +1 activation, obligatoire, effet : après chaque lancer, pour chaque échec, vous devez relancer une touche
  récompense : 1 point de récupération

  spore de guerre totale : 5,3, c2, +0 activation, obligatoire, effet : subséquent : lancez les 3 dés les plus faibles : sur 2 touches vous devez bannir un dé violet
  récompense : lune brûlée : permanent : -1 point de récupération. Au début de chaque stratégie, renvoyez tous les échecs engagés

  spore guerrière : 3,1, c1, +0 activation, non obligatoire, effet : au lancer, vous ne pouvez pas relancer les échecs
  récompense : vous récupérez 2 dés

  tas de vers : 3,3, c1, +0 activation, non obligatoire, effet : subséquent : si des dés rouges ou violets ont été assignés, défaussez le 1er ennemi de chaque pile
  récompense : bouteille d'eau : consommable : vous récupérez tous les dés bleus de la zone épuisée

  tortue mortier : 7,3, c2, +0 activation, non obligatoire, effet : aucun
  récompense : 2 pv et récupérez 2 dés

  tortueuse : 6,1, c1, +1 activation, non obligatoire, effet : aucun
  récompense : supplément de kératine : consommable : épuisez 2 échecs bleus pour régler la récupération à 3 et récupérer 3 dés

  triodrone : 6,2,c2, +0 activation, non obligatoire, effet : vous ne pouvez assigner des dés aux ennemis que si la zone épuisée contient au moins un dé de la même couleur
  récompense : seconde chance : permanent : 1 fois par tour, vous pouvez transformer une paire d'échecs de même couleur en touches

  vaincafard : 5,3, c2, +1 activation, non obligatoire, effet : vous ne pouvez engager que 3 dés maximum par engagement
  récompense : sacrifice solaire ; permanent : banissez un dé de votre réserve pour infliger à un ennemi c1 ou c2 un nb de dégâts égal au nb de touches du dé sacrifié




   */

  public final String name = this.name();
  public final int classValue;
  public final int life;
  public final int attack;
  public final List<EnnemyEffect> effets;
  public final int forcedActivations;
  public final boolean forcedActivationMandatory;
  private Reward reward;

  EnnemiType(int classValue, int life, int attack, List<EnnemyEffect> effets, int forcedActivations, boolean forcedActivationMandatory,
      Reward reward) {
    this.classValue = classValue;
    this.life = life;
    this.attack = attack;
    this.effets = effets;
    this.forcedActivations = forcedActivations;
    this.forcedActivationMandatory = forcedActivationMandatory;
    this.reward = reward;
  }

  public Reward getReward() {
    return reward;
  }

}
