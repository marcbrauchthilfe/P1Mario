package entities;

import levels.Level;
import levels.Tile;
import utils.Zoom;

import java.awt.*;

public class MovingEnemy extends Enemy {

    private double vx = 1.2 * Zoom.SCALE;

    public MovingEnemy(double x, double y, Level level) {
        super(x, y, level);

        this.x = x;
        this.y = y;
        this.level = level;
    }

    @Override
    public void update() {
        super.update();

        // =========================================
        // 2) ANTI-FALL-KANTEN-ERKENNUNG (nur wenn Boden!)
        // =========================================
        Rectangle frontFoot = new Rectangle(
                (int)(x + (vx > 0 ? ENEMY_WIDTH : -4)),
                (int)(y + ENEMY_HEIGHT),
                4,           // Breite der Abfrage
                4            // Höhe der Abfrage
        );


        boolean groundAhead = false;
        for (Tile t : level.getSolidTiles()) {
            if (frontFoot.intersects(t.getRect())) {
                groundAhead = true;
                break;
            }
        }


        if (!groundAhead) {
            // keine Kante → umdrehen
            vx = -vx;
            flipX = !flipX;
            return;
        }

        // ==========================
        // 3) HORIZONTALE BEWEGUNG
        // ==========================
        int sensorX = (int)(x + (vx > 0 ? ENEMY_WIDTH + 1 : -5));
        int sensorY = (int)y + 4;        // kleine Höhe von oben weg, nicht von der Mitte
        int sensorH = ENEMY_HEIGHT - 8;  // nicht über den Boden ziehen

        Rectangle sideSensor = new Rectangle(sensorX, sensorY, 4, sensorH);

        boolean wallAhead = false;
        for (Tile t : level.getSolidTiles()) {
            if (sideSensor.intersects(t.getRect())) {
                wallAhead = true;
                break;
            }
        }

        if (wallAhead) {
            // Wand → wenden
            vx = -vx;
            flipX = !flipX;
            return;
        }

        // Wenn alles ok → bewegen
        x += vx;
    }
}