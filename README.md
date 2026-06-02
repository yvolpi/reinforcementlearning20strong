# Getting Started

Paramètres:
GameBootstrap : NB_GAMES, GAME_RANDOM, IA_RANDOM (pour les tirages et les stratégies aléatoires)
GameInitializer : INITIAL_AVATAR, INITIAL_LIFE, INITIAL_STRATEGY, INITIAL_RECOVERY, INITIAL_ENNEMIS_NUMBER_PER_PILE, INITIAL_MISSIONS_NUMBER

GameInitializer.createDicePool() : c'est là où sont créés les dés du joueur.
On peut créer un dé prédéfini en fonction de la couleur : exemple : new Dice(DiceColor.VERT);
On peut aussi créer un dé personnalisé : il faut : un nom, une couleur, une map de faces avec leurs nombres, et les seuils de touches simples et critiques. Exemple :

```java
Map<Integer, Integer> faces = new HashMap<>();
    faces.put(0,3);
    faces.put(1,3);
    faces.put(2,1);

    CustomDice customDice = new CustomDice("BLEU2", DiceColor.BLEU, 3, 6, faces);
    dicePool.add(customDice);
```
Cela donne un dé bleu avec 3 faces à 0, 3 faces à 1 et 1 face à 2. Le seuil de touche simple est à 3 et le seuil de touche critique est à 6.

# Run

Lancer LearningStrongApplication.java:

Vous aurez un fichier de logs "last_game_history.txt" qui contiendra l'historique de la dernière partie.

Exemple de log :

```
StateAction[encodedState=TURN, action=1]
StateAction[encodedState=PV:2|RESERVE:1|APOTRE_ESSAIM;ACOLYTE_ESSAIM;APOTRE_ESSAIM|MISSION:Etablir une base secrète, action=ACTIVATE:PILE_2]
StateAction[encodedState=ENGAGE1|PV:2|STRATEGIE:2|RESERVE:1|ENGAGES:0|ACOLYTE_ESSAIM:4:PERMANENT:1|MISSION:Etablir une base secrète, action=ENGAGE1:JAUNE=1]
StateAction[encodedState=PV:2|JAUNE:0|ACOLYTE_ESSAIM:4:PERMANENT:1|MISSION:Etablir une base secrète, action=]
StateAction[encodedState=ENGAGE2|PV:2|STRATEGIE:2|RESERVE:0|ENGAGES:1|ACOLYTE_ESSAIM:4:PERMANENT:1|MISSION:Etablir une base secrète, action=ENGAGE2:NONE]
StateAction[encodedState=PV:2|JAUNE:0|ACOLYTE_ESSAIM:4:PERMANENT:1|MISSION:Etablir une base secrète, action=]
StateAction[encodedState=LE JOUEUR SUBIT, action=2 DEGATS]
```