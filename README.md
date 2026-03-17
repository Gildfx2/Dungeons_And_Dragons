# 🐉 Dungeons & Dragons - OOP RPG Game

![Banner Image Placeholder](https://github.com/Gildfx2/Dungeons_And_Dragons/blob/233d52dd57d3fc6cc94276c8e0cbd6e316d3d8c3/D%26D_main.png)

A turn-based 2D dungeon crawler RPG developed in **Java**. This project was built with a strong emphasis on **Object-Oriented Programming (OOP) principles, clean architecture, and standard Design Patterns**. It features a robust game engine decoupled from the user interface, supporting complex combat mechanics, diverse character classes, and dynamic enemy AI.

While initially designed with a CLI, the game now features a fully functional **Java Swing GUI** with retro pixel-art graphics.

## 🏗️ Architecture & Design Patterns

The core strength of this project lies in its scalable and decoupled architecture:

* **Visitor Pattern & Double Dispatch:** Used extensively to handle collisions and interactions on the game board. When a unit attempts to move into a tile, it "visits" it. This elegantly resolves whether the interaction results in a movement (visiting an empty tile), an invalid action (visiting a wall), or a combat engagement (a player visiting an enemy, or vice versa) without using brittle `instanceof` checks.
* **Factory Method:** The `TileFactory` class is responsible for instantiating the correct objects (Walls, Empty tiles, Players, Enemies, Traps) based on the characters parsed from the level's text file.
* **Observer Pattern / Callbacks:** The game engine uses callbacks (`MessageCallback`, `InputProvider`) to communicate with the outside world. This completely decouples the logic from the presentation layer, allowing the game to seamlessly run on both a CLI and a graphical GUI.
* **Polymorphism & Inheritance:** A deep hierarchy where all entities derive from a base `Tile` and `Unit` class. Player classes (Warrior, Mage, Rogue, Hunter) and Enemy classes (Monsters, Bosses, Traps) override specific behaviors like leveling up, resource management, and combat ticks.

## ⚔️ Game Mechanics

The game engine strictly follows the mechanics outlined in the core requirements:

* **Turn-Based Game Loop:** The game operates on "ticks". During a tick, the player takes an action, followed by all living enemies.
* **Combat System:** Deterministic yet dynamic combat. Attack and Defense points are calculated, and damage is dealt based on `(Attacker's ATK roll) - (Defender's DEF roll)`.
* **Hero Classes:**
    * 🗡️ **Warrior:** High health and defense. Uses a *Cooldown (CD)* mechanic for the "Avenger's Strike" ability, which heals the warrior and damages nearby enemies.
    * 🧙‍♂️ **Mage:** Uses *Mana* to cast "Blizzard", hitting random enemies within a specific range.
    * 🥷 **Rogue:** Uses *Energy* (regenerates every tick) to unleash "Fan of Knives" on surrounding enemies.
    * 🏹 **Hunter:** Uses *Arrows* for ranged attacks. Can shoot arrows at enemies from a distance and replenishes them through passive ticks.
* **Dynamic Enemies:**
    * **Monsters:** Have varying movement behaviors (random wandering vs. chasing the player based on vision range).
    * **Traps:** Invisible enemies that toggle their visibility based on specific tick counts, punishing players who step on them or stand too close.
* **Progression:** Players earn Experience (EXP) by defeating enemies. Leveling up restores health and boosts base stats (ATK, DEF, HP) depending on the specific class archetype.

## 🖥️ Graphical User Interface (GUI)

The presentation layer translates the backend logic into a visually engaging experience:
* **Dynamic Rendering:** The board scales dynamically to fit the screen, translating ASCII characters (like `#`, `.`, `@`) into detailed pixel-art sprites.
* **Visual Feedback:** Real-time health bars rendered directly above enemies, directional sprite facing (left/right), and dynamic wall shading.
* **Combat Log:** A styled, scrollable text pane that logs every action, damage dealt, and level-up event using custom fonts and color-coded text.
* **Popups:** Custom-styled modal dialogs for game-over and victory events.

## 📸 Screenshots

| Main Menu | Gameplay & Combat |
| :---: | :---: |
| ![Main Menu Placeholder](https://github.com/Gildfx2/Dungeons_And_Dragons/blob/1104b6aa1ff845b4cccba5e96fd9fe035ee5889f/main_menu_img.png) | ![Gameplay Placeholder](Insert_Link_To_Action_Screenshot) |
| *Hero selection screen with class details.* | *Dungeon exploration with active combat and UI stats.* |

## 🚀 How to Run

### Prerequisites
* Java Development Kit (JDK) 8 or higher.

## 🎮 Controls

* **W / S / A / D** or **Arrow Keys:** Move and Attack (bump into enemies).
* **E:** Cast Class Special Ability.
* **Q:** Skip Turn / Rest (Regenerates specific resources).

## 🛠️ Built With
* **Java** (Core Game Engine)
* **Java Swing & AWT** (Graphical Interface)

---
*Started as an Advanced Object-Oriented Programming university project, and further extended with a fully functional retro GUI.*
