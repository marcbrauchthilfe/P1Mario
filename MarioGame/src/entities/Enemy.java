package entities;

import levels.Level;
import levels.Tile;
import utils.Zoom;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Enemy {
    public static final int ENEMY_HEIGHT = (int) (32 * Zoom.SCALE);
    public final int ENEMY_WIDTH = (int) (32 * Zoom.SCALE);
    protected Level level;
    protected double x;
    protected double y;
    protected BufferedImage sprite = null;
    protected boolean flipX = false;

    public Enemy(double x, double y, Level level) {
        this.x = x;
        this.y = y;
        this.level = level;
        try {
            sprite = ImageIO.read(new File("res/Geist_V3.png"));
        } catch (IOException e) {
            System.err.println("entities.MovingEnemy sprite loading failed.");
        }
    }

    public void update() {

        // ==========================
        // 1) GRAVITY (immer zuerst!)
        // ==========================
        double nextY = y + 4;  // Fallgeschwindigkeit

        boolean onGround = false;
        for (Tile t : level.getSolidTiles()) {
            Rectangle test = new Rectangle((int) x, (int) nextY, ENEMY_WIDTH, ENEMY_HEIGHT);
            if (test.intersects(t.getRect())) {
                // auf Boden setzen
                y = t.getY() - ENEMY_HEIGHT;
                onGround = true;
                break;
            }
        }

        if (!onGround) {
            // frei fallender Gegner → FALLEN
            y = nextY;
        }
    }


    public Rectangle getBounds() {
        return new Rectangle((int) Math.round(x), (int) Math.round(y), ENEMY_WIDTH, ENEMY_HEIGHT);
    }

    public int getY() {
        return (int) Math.round(y);
    }

    public void draw(Graphics2D g, int camX) {
        int dx = (int) Math.round(x) - camX;
        int dy = (int) Math.round(y);

        if (sprite != null) {
            if (!flipX) {
                g.drawImage(sprite, dx, dy, ENEMY_WIDTH, ENEMY_HEIGHT, null);
            } else {
                g.drawImage(sprite, dx + ENEMY_WIDTH, dy, dx, dy + ENEMY_HEIGHT, 0, 0, sprite.getWidth(), sprite.getHeight(), null);
            }
        } else {
            g.setColor(new Color(255, 140, 0));
            g.fillOval(dx, dy, ENEMY_WIDTH, ENEMY_HEIGHT);
            g.setColor(Color.BLACK);
            g.drawOval(dx, dy, ENEMY_WIDTH, ENEMY_HEIGHT);
        }
    }
}