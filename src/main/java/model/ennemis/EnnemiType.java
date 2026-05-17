package model.ennemis;

import static model.effets.ennemi.EnnemyEffectType.SUBSEQUENT;

import java.util.List;
import model.effets.ennemi.*;
import model.recompenses.*;

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
  ASSERVI(2,4,2, List.of(new FleeAfterFourHitEffect()), 1, false,
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
  ESCROCAFARD(1,3,2, List.of(new BanishFailedDiceEffect(), new ForbidGreenDiceToEngageEffect()), 0, false,
      new LunoculationReward()),

  FANATIQUE_ESSAIM(2,5,2, List.of(new SwitchAllDiceEffect()), 1, false,
      new FusilBlasterReward()),

  GROS_SCARAB(1,4,2, List.of(new MustAssignPairDiceEffect()), 0, false,
      new IncreaseStrategyRewerd()),

  GUEPE_CHERCHEUSE(1,4,2, List.of(new GuepeChercheuseEffect(), new MaxEngagedDicePerTurnEffect(3)), 0, true,
      new RecoverLifeReward(2)),

  GUEPE_DECHAINEE(2,5,3, List.of(new MaxAssignDiceEffect(2)), 0, false,
      new MegaEpinephrineReward()),

  ICHORKYSTE(2,4,0, List.of(new MaxAssignDiceToItEffect(2)), 0, true,
      new IchorVeriteReward()),

  LARVE_A_MOELLE(2,2,4, List.of(new LarveAMoelleEffect()), 0, false,
      null),

  LOCUSTE_DE_CHAIR(1,6,2, List.of(new BanishDiceIfNotKilledEffect()), 1, false,
      new IncreaseStrategyRewerd()),

  MACHOIRE_MASSACREUSE(2,2,2, List.of(new MaxOneEnnemiToKillEffect()), 2, false,
      new FilAMassacreReward()),

  MACHOIRE_SANGLANTE(1,4,1, List.of(new ForbidRedAndPurpleHitsEffect()), 0, false,
      new TrancheEclipseReward()),

  MANTE_EGORGEUSE(1,4,1, List.of(new ForbidCriticHitsEffect()), 0, false,
      new StimulantReward(3)),

  MANTE_PURULENTE(2,5,3, List.of(new BlockUseItemsEffect()), 0, false,
      new AmputateurReward()),

  MAREE_DE_LARVES(1,3,1, List.of(new ForbidPurpleDiceToAssignEffect()), 2, false,
      new ReplicateurReward()),

  MILITAIRE_ASSERVI(1,5,1, List.of(new FleeIfAtLeastTwoFailsEffect()), 1, false,
      new AntenneDeSourcierReward()),

  MOUCHE_DES_CRATERES(1,6,1, List.of(new YellowAndGreenHitsDoOneMoreDamageEffect()), 0, false,
      new RecoverDicesReward(2)),

  MOUCHE_EVENTREUSE(1,6,1, List.of(new KillEnnemiWhenKilledEffect()), 0, false,
      new RayonTracteurReward(3)),

  MOUCHE_TRUCIDEUSE(2,8,1, List.of(new ActivateOneMoreEnnemiNextTurnIfNotKilledEffect()), 0, false,
      new ForteressePortativeReward()),

  PARAKYSTE(1,3,1, List.of(new ForbidMultipleColorsToAssignEffect()), 1, true,
      new PistoletEtourdissantReward()),

  PISTOGUEPE(2,7,2, List.of(new LimitOneDicePerColorToAssignEffect()), 0, false,
      new DardMissileReward()),

  PLASMAKYSTE(2,6,2, List.of(new PlasmakysteEffect()), 0, true,
      new ADNDeLaHordeReward()),

  PORTE_SPORES_CHERCHEUR(1,2,2, List.of(new MaxEngagedDicePerTurnEffect(4)), 0, false,
      new GrilleFiltranteReward()),

  PORTE_SPORES_EXPECTORANT(2,6,2, List.of(new PorteSporeExpectorantEffect()), 0, false,
      new GrilleFiltranteReward()),

  PORTE_SPORES_FRENETIQUE(3,1,1, List.of(new MaxEngagedDicePerTurnEffect(5)), 0, false,
      new GrilleFiltranteReward()),

  RAMPECRATERE(1,3,1, List.of(new LoseLifePerTwoFailsEffect()), 1, false,
      new JusInsecteReward()),

  RIPOSTEUR_ESSAIM(2,5,2, List.of(new ForbidCriticHitsEffect()), 1, true,
      new StimulantReward(5)),

  RODECRATERE(2,5,1, List.of(new LoseLifePerTwoHitsColorEffect()), 0, false,
      new MiniTerraformeuseReward()),

  SENTINELLE_ASSIMILEE(1,3,2, List.of(new EngageAllSameColorDiceEffect()), 1, false,
      new IncreaseStrategyRewerd()),

  SENTINELLE_LUNAIRE(2,8,2, List.of(), 0, false,
      new RecoverDicesReward(5)),

  SENTINELLE_PERDUE(1,3,1, List.of(new ForbidGreenDiceToEngageEffect()), 0, false,
      new IncreaseRecoveryReward(1)),

  SOLODRONE(1,4,1, List.of(new SolodroneEffect()), 1, true,
      new IncreaseRecoveryReward(1)),

  SPORE_DE_GUERRE_TOTALE(2,5,3, List.of(new BanishPurpleDiceIfTwoHitsEffect()), 0, true,
      new LuneBruleeReward()),

  //spore guerrière : 3,1, c1, +0 activation, non obligatoire, effet : au lancer, vous ne pouvez pas relancer les échecs
  //  récompense : vous récupérez 2 dés
  SPORE_GUERRIERE(3,1,1, List.of(new ForbidRerollFailsEffect()), 0, false,
      new RecoverDicesReward(2))


   ,

  // Boss
  BETE_ALPHA(3,12,2, List.of(new BlockAssignIfFailEffect(), new KeepDiceInExhaustEffect()), 2, false,
      null),

  MERE_DES_SPORES(3,9,0, List.of(new LoseLifePerFailEffect(), new ForbidMultipleColorsToAssignEffect()), 1, true,
      null),

  MONARQUE_RUCHE(3,8,1, List.of(new LimitedDamageEffect(3), new AttackAjustmentEffect(1), new KeepDiceInExhaustEffect()), 2, false,
      null),

  SEIGNEUR_DE_LESSAIM(3,9,3, List.of(new ForbidDamageIfEnnemiAliveEffect()), 3, true,
      null)
  ;

  /*

Autres ennemis :

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
