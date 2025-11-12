import java.awt.*;

public class Tile {
    private int x, y, w, h;

    public Tile(int x, int y, int w, int h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    public void draw(Graphics2D g, int camX) {
        g.setColor(new Color(150, 75, 0)); // brauner Boden
        g.fillRect(x - camX, y, w, h);
        g.setColor(Color.BLACK);
        g.drawRect(x - camX, y, w, h);
    }

    public Rectangle getRect() {
        return new Rectangle(x, y, w, h);
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getW() { return w; }
    public int getH() { return h; }
}
