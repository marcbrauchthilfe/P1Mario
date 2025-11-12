import java.awt.*;

public class Enemy {
    private double x, y;
    private double vx = 1.2;
    private final int w = 32, h = 32;
    private final Level level;

    public Enemy(double x, double y, Level level) {
        this.x = x; this.y = y; this.level = level;
    }

    public void update() {
        // simple horizontal movement, reverse on collision or edge
        x += vx;

        Rectangle bounds = getBounds();
        boolean collided = false;
        for (Tile t : level.getSolidTiles()) {
            if (t.getRect().intersects(bounds)) {
                collided = true;
                break;
            }
        }
        if (collided) {
            // step back and reverse
            x -= vx;
            vx = -vx;
            x += vx;
        }

        // simple gravity to sit on platforms
        double nextY = y + 4;
        boolean onGround = false;
        for (double sy = y; sy <= nextY; sy += 1.0) {
            Rectangle test = new Rectangle((int)x, (int)sy, w, h);
            boolean hit = false;
            for (Tile t : level.getSolidTiles()) {
                if (test.intersects(t.getRect())) {
                    y = t.getY() - h;
                    hit = true;
                    onGround = true;
                    break;
                }
            }
            if (hit) break;
        }
        if (!onGround) y = nextY;
    }

    public Rectangle getBounds() { return new Rectangle((int)Math.round(x), (int)Math.round(y), w, h); }
    public int getY() { return (int)Math.round(y); }

    public void draw(Graphics2D g, int camX) {
        int dx = (int)Math.round(x) - camX;
        int dy = (int)Math.round(y);
        g.setColor(new Color(255, 140, 0));
        g.fillOval(dx, dy, w, h);
        g.setColor(Color.BLACK);
        g.drawOval(dx, dy, w, h);
    }
}
