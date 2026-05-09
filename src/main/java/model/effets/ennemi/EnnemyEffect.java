package model.effets.ennemi;

import model.Dice;
import model.GameState;
import model.Player;
import model.ennemis.Ennemi;

public interface EnnemyEffect {
  EnnemyEffectType getType();
  default void apply(Player player, GameState gameState, Ennemi ennemi) {
    // Par défaut, les effets n'ont pas d'impact immédiat sur le joueur ou l'état du jeu
  }

  default boolean canEngage(Dice dice) {
    return true; // Par défaut, tous les dés peuvent être engagés
  }

  default boolean isDiceAssignable(GameState gameState, Dice dice) {
    return true; // Par défaut, tous les dés peuvent être potentiellement assignés à l'ennemi
  }

  default void applyAfterEngagementAndRoll(GameState gameState, Ennemi ennemi) {
    // Par défaut, les ennemis ne font rien de spécial après l'engagement
  }

  default void receiveDamage(int damage) {
      // Par défaut, les ennemis ne font rien de spécial quand ils reçoivent des dégâts
  }

  default boolean canAssignDice(GameState gameState, Dice dice) {
    return true; // Par défaut, tous les dés peuvent être assignés
  }

  default void applyBeforeEngagement(GameState gameState) {
    // Par défaut, les ennemis ne font rien de spécial avant l'engagement
  }

  default void applyBeforeAllEngagement(GameState gameState) {
    // Par défaut, les ennemis ne font rien de spécial avant l'engagement
  }

}
