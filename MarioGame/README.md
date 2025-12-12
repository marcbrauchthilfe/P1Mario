Vibe-Coding Jump & Run Game
================================

Requirements:
- Java SE 18 or higher
- No additional libraries required (uses only standard Java Swing)
- Compile all .java files and run the Main class to start the application

Files:
```text
src/
│
├─ entities/        # Spielfiguren & Gegner
│  ├─ Enemy
│  ├─ MovingEnemy
│  └─ Player
│
├─ levels/          # Levelarchitektur & Plattformen
│  ├─ Level
│  ├─ MovingPlatform
│  └─ Tile
│
├─ main/            # Hauptlogik & Programmstart
│  ├─ Game
│  ├─ GamePanel
│  ├─ Main
│  └─ Storage
│
├─ ui/              # Menü und Bildschirmverwaltung
│  ├─ LoadingScreens
│  └─ MenuManager
│
└─ utils/           # Hilfsklassen
   ├─ GameState
   └─ Zoom

res/                # Ressourcen (Grafiken usw.)
```



How to compile:
> javac *.java

> java Main

Controls:
- A / Left Arrow — Move left
- D / Right Arrow — Move right
- W / Space — Jump
- Esc — Return to Menu

Levels:
1. Easy jump & run; stationary enemies
2. Easy jump & run; stationary enemies + one moving enemy
3. Jump & run with ground gaps; stationary and moving enemies
4. Jump & run; stationary and moving enemies
5. Jump & run with moving platforms + ground gaps; moving enemies
6. Hard jump & run with moving platforms + ground gaps; moving enemies
7. Hard jump & run with moving platforms + ground gaps; stationary enemies
8. Hard jump & run with moving platforms + ground gaps; stationary enemies