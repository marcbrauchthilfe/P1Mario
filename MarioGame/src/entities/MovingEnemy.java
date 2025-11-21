package entities;

import levels.Level;
import levels.Tile;
import utils.Zoom;

import java.awt.*;

public class MovingEnemy extends Enemy {
    public static final int ENEMY_HEIGHT = (int) (32 * Zoom.SCALE);
    public final int ENEMY_WIDTH = (int) (32 * Zoom.SCALE);
    private final Level level;
    private final double y;
    private double x;
    private double vx = 1.2 * Zoom.SCALE;
    private boolean flipX = false;

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
        int frontX = (int) (x + (vx > 0 ? ENEMY_WIDTH + 2 : -2));
        int belowY = (int) (y + ENEMY_HEIGHT + 2);

        boolean groundAhead = false;
        for (Tile t : level.getSolidTiles()) {
            if (t.getRect().contains(frontX, belowY)) {
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
        double nextX = x + vx;
        Rectangle future = new Rectangle((int) nextX, (int) y, ENEMY_WIDTH, ENEMY_HEIGHT);

        Tile blockingTile = null;
        for (Tile t : level.getSolidTiles()) {
            if (future.intersects(t.getRect())) {
                blockingTile = t;
                break;
            }
        }

        if (blockingTile != null) {
            // HOCHKLETTERN VERHINDERN
            if (blockingTile.getY() < y + ENEMY_HEIGHT - 4) {
                vx = -vx;
                flipX = !flipX;
                return;
            }
            // normale Kollision
            flipX = !flipX;
            vx = -vx;
            return;
        }

        // Wenn alles ok → bewegen
        x = nextX;
    }
}