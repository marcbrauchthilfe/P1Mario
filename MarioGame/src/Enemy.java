import java.awt.*;

public class Enemy {
    private final int w = 32, h = 32;
    private final Level level;
    private double x, y;
    private double vx = 1.2;

    public Enemy(double x, double y, Level level) {
        this.x = x;
        this.y = y;
        this.level = level;
    }

    public void update() {

        // ==========================
        // 1) GRAVITY (immer zuerst!)
        // ==========================
        double nextY = y + 4;  // Fallgeschwindigkeit

        boolean onGround = false;
        for (Tile t : level.getSolidTiles()) {
            Rectangle test = new Rectangle((int) x, (int) nextY, w, h);
            if (test.intersects(t.getRect())) {
                // auf Boden setzen
                y = t.getY() - h;
                onGround = true;
                break;
            }
        }

        if (!onGround) {
            // frei fallender Gegner → FALLEN
            y = nextY;
            return; // solange er fällt NICHTS anderes tun
        }


        // =========================================
        // 2) ANTI-FALL-KANTEN-ERKENNUNG (nur wenn Boden!)
        // =========================================
        int frontX = (int) (x + (vx > 0 ? w + 2 : -2));
        int belowY = (int) (y + h + 2);

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
            return;
        }


        // ==========================
        // 3) HORIZONTALE BEWEGUNG
        // ==========================
        double nextX = x + vx;
        Rectangle future = new Rectangle((int) nextX, (int) y, w, h);

        Tile blockingTile = null;
        for (Tile t : level.getSolidTiles()) {
            if (future.intersects(t.getRect())) {
                blockingTile = t;
                break;
            }
        }

        if (blockingTile != null) {
            // HOCHKLETTERN VERHINDERN
            if (blockingTile.getY() < y + h - 4) {
                vx = -vx;
                return;
            }
            // normale Kollision
            vx = -vx;
            return;
        }

        // Wenn alles ok → bewegen
        x = nextX;
    }


    public Rectangle getBounds() {
        return new Rectangle((int) Math.round(x), (int) Math.round(y), w, h);
    }

    public int getY() {
        return (int) Math.round(y);
    }

    public void draw(Graphics2D g, int camX) {
        int dx = (int) Math.round(x) - camX;
        int dy = (int) Math.round(y);

        g.setColor(new Color(255, 140, 0));
        g.fillOval(dx, dy, w, h);
        g.setColor(Color.BLACK);
        g.drawOval(dx, dy, w, h);
    }
}
