import java.awt.*;
import java.awt.image.BufferedImage;

public class Tile {
    private final int x;
    private final int y;
    private final int w;
    private final int h;
    private BufferedImage texture;

    public Tile(int x, int y, int w, int h,  BufferedImage texture) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.texture = texture;
    }

    public void draw(Graphics2D g, int camX) {
        if (texture != null) {
            g.drawImage(texture, x - camX, y, w, h, null);
        } else {
            g.setColor(new Color(150, 75, 0));
            g.fillRect(x - camX, y, w, h);
        }
    }

    public Rectangle getRect() {
        return new Rectangle(x, y, w, h);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getW() {
        return w;
    }

    public int getH() {
        return h;
    }
}
