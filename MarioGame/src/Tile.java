import java.awt.*;
import java.awt.image.BufferedImage;

public class Tile {
    public final BufferedImage texture;
    private final int w;
    private final int h;
    public int y;
    protected int x;

    public Tile(int x, int y, int w, int h, BufferedImage texture) {
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
            g.setColor(new Color(40, 123, 241));
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

}
