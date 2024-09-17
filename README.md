# Project Overview
This project is a multiplayer strategy game developed in Java. It allows 2 to 4 players (humans and/or bots) to compete on a map composed of colored tiles. Each player controls a base, soldiers, and structures, aiming to conquer territories, manage resources, and eliminate opponents to win the game.

![Application screenshot 1](https://github.com/ericbeaubrun/portfolio/blob/master/public/resources/projects/conquete1.gif)

---

# Victory and Defeat Conditions

- Win Condition : Destroy all enemy bases or conquer all of their territories.
- Lose Condition : Lose your base or all your territories.

![Application screenshot 1](https://github.com/ericbeaubrun/portfolio/blob/master/public/resources/projects/conquete2.gif)

---

# Main Features

## Player and AI management:
- Players, whether human or AI, control territories, gold, soldiers, and structures, which they can upgrade during the game.
  
## AI Difficulty Levels:
- Normal: Standard AI behavior.
- Unfair: The AI gains additional resources and is more challenging to defeat.
  
## Save and Load:
- The game includes the ability to save progress and load previously saved games.
  
## Player Statistics:
- Displayed in real-time, including base health, gold earned per turn, and the number of territories owned.
- Visual Charts and Graphs:
- Graphs show player progression, territory distribution, and military strength.

![Application screenshot 1](https://github.com/ericbeaubrun/portfolio/blob/master/public/resources/projects/conquete3.gif)

---

# Game Elements

1. Base:
The main hub for soldier production. If it is destroyed, the player loses.

2. House:
Increases the player's gold income per turn.

3. Towers:
Offensive Tower which increases the attack of adjacent soldiers and defensive Tower which increases the defense of adjacent soldiers.

4. Trees:
Affect the player’s gold income based on their location (neutral or within a player’s zone).

5. Soldiers and Combat:
One type of soldier with evolvable levels, improving health and attack power.

6. Fusion Mechanism: Combine soldiers to create more powerful units.

7. Economy and Resource Management :
Earn gold by constructing houses, destroying trees in neutral or enemy zones, and conquering territories. Efficient resource management is essential to strengthen your army and expand your territory.

![Application screenshot 1](https://github.com/ericbeaubrun/portfolio/blob/master/public/resources/projects/conquete4.gif)

---

# Dependencies
The project uses the following libraries:
- JFreeChart (4.12+)
- Log4j (1.2+)
- JUnit5 (4.5.6+)
  
>[!NOTE]
> All dependencies are included in the /libs folder.

