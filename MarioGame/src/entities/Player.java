package entities;

import levels.Level;
import levels.MovingPlatform;
import levels.Tile;
import main.GamePanel;
import utils.GameState;
import utils.Zoom;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class Player {
    public static final int PLAYER_WIDTH = (int) (32 * Zoom.SCALE);
    public static final int PLAYER_HEIGHT = (int) (48 * Zoom.SCALE);
    private static final int HORIZONTAL_COLLISION_PADDING = (int) (1 * Zoom.SCALE);

    private final Level level;
    private final double MOVE_SPEED = 2.5 * Zoom.SCALE;
    private final double JUMP_SPEED = -11 * Zoom.SCALE;

    private final double startX, startY;
    private double x, y;
    private double vx, vy;
    private boolean onGround = false;

    private double coyoteTimer = 0.0;
    private double jumpBufferTimer = 0.0;

    private BufferedImage sprite = null;
    private boolean flipX = false;

    private MovingPlatform currentPlatform = null;
    private double platformPrevX = 0;
    private double platformPrevY = 0;

    public Player(double startX, double startY, Level level) {
        this.x = startX;
        this.y = startY;
        this.startX = startX;
        this.startY = startY;
        this.level = level;
    }

    public void loadSprite(String path) {
        try {
            sprite = ImageIO.read(new File(path));
        } catch (Exception e) {
            System.err.println("Sprite konnte nicht geladen werden: " + path);
        }
    }

    public boolean isFalling() {
        return vy > 0 && !onGround;
    }

    public void update(double dt) {
        if (dt <= 0) return;
        double GRAVITY = 0.6 * Zoom.SCALE;
        double MAX_FALL = 14.0 * Zoom.SCALE;
        double COYOTE_TIME = 0.12;

        if (coyoteTimer > 0) coyoteTimer -= dt;
        if (jumpBufferTimer > 0) jumpBufferTimer -= dt;

        // Schwerkraft
        vy += GRAVITY * dt * 60;
        if (vy > MAX_FALL) vy = MAX_FALL;

        // Horizontale Bewegung + Kollisionsabfrage
        x += vx * dt * 60;
        Rectangle hb = getBounds();
        for (Tile t : level.getSolidTiles()) {
            if (hb.intersects(t.getRect())) {
                if (vx > 0) x = t.getX() - PLAYER_WIDTH;
                else if (vx < 0) x = t.getX() + t.getW();
                vx = 0;
                hb = getBounds();
            }
        }

        // Vertikale Bewegung + Kollisionsabfrage
        y += vy * dt * 60;
        onGround = false;

        Rectangle verticalBounds = new Rectangle(getX() + HORIZONTAL_COLLISION_PADDING, getY(), PLAYER_WIDTH - (2 * HORIZONTAL_COLLISION_PADDING), PLAYER_HEIGHT);

        MovingPlatform newPlatform = null;

        for (Tile t : level.getSolidTiles()) {
            Rectangle tileRect = t.getRect();
            if (verticalBounds.intersects(tileRect)) {
                if (vy > 0) { // Landen
                    y = tileRect.y - PLAYER_HEIGHT;
                    vy = 0;
                    onGround = true;
                    coyoteTimer = COYOTE_TIME;

                    if (t instanceof MovingPlatform mp) {
                        newPlatform = mp;
                    }
                } else if (vy < 0) { // Kopfstoß
                    y = tileRect.y + tileRect.height;
                    vy = 0;
                }
            }
        }

        // Plattformbewegung anwenden
        if (newPlatform != null) {
            if (currentPlatform == newPlatform) {
                // Unterschied zur letzten Position berechnen
                double dx = newPlatform.getX() - platformPrevX;
                double dy = newPlatform.getY() - platformPrevY;
                x += dx;
                y += dy;
            }
            platformPrevX = newPlatform.getX();
            platformPrevY = newPlatform.getY();
        }
        currentPlatform = newPlatform;

        // Jump-buffering + Coyote Time
        if (jumpBufferTimer > 0 && (onGround || coyoteTimer > 0)) {
            doJump();
            jumpBufferTimer = 0;
            coyoteTimer = 0;
        }

        // levels.Level-Grenzen
        if (x < 0) x = 0;
        if (x + PLAYER_WIDTH > level.getWidth()) x = level.getWidth() - PLAYER_WIDTH;

        // Unter den Boden fallen
        if (y > level.getHeight() + 300) {
            GamePanel.subLive();
            if (GamePanel.lives <= 0) {
                GamePanel.state = GameState.GAME_OVER_SCREEN;
            } else {
                respawn();
            }
        }
    }

    // Steuerung
    public void moveLeft() {
        vx = -MOVE_SPEED;
        flipX = true;
    }

    public void moveRight() {
        vx = MOVE_SPEED;
        flipX = false;
    }

    public void stopHorizontal() {
        vx = 0;
    }

    public void pressJump() {
        jumpBufferTimer = 0.12;
    }

    public void releaseJump() {
        if (vy < 0) vy *= 0.6;
    }

    private void doJump() {
        vy = JUMP_SPEED;
        onGround = false;
        currentPlatform = null;
    }

    public void bounceAfterStomp() {
        vy = JUMP_SPEED / 2;
    }

    public void respawn() {
        x = startX;
        y = startY;
        vx = 0;
        vy = 0;
        currentPlatform = null;
        platformPrevX = 0;
        platformPrevY = 0;
    }

    // BOUNDS + POSITION
    public Rectangle getBounds() {
        return new Rectangle((int) Math.round(x), (int) Math.round(y), PLAYER_WIDTH, PLAYER_HEIGHT);
    }

    public int getX() {
        return (int) Math.round(x);
    }

    public int getY() {
        return (int) Math.round(y);
    }

    // Zeichnen
    public void draw(Graphics2D g, int camX) {
        int drawX = getX() - camX;
        int drawY = getY();

        if (sprite != null) {
            if (!flipX) {
                g.drawImage(sprite, drawX, drawY, PLAYER_WIDTH, PLAYER_HEIGHT, null);
            } else {
                g.drawImage(sprite, drawX + PLAYER_WIDTH, drawY, drawX, drawY + PLAYER_HEIGHT, 0, 0, sprite.getWidth(), sprite.getHeight(), null);
            }
        } else {
            g.setColor(new Color(200, 30, 30));
            g.fillRoundRect(drawX, drawY, PLAYER_WIDTH, PLAYER_HEIGHT, 6, 6);
            g.setColor(Color.WHITE);
            g.fillOval(drawX + 8, drawY + 8, 8, 8);
            g.setColor(Color.BLACK);
            g.fillOval(drawX + 10, drawY + 10, 3, 3);
        }
    }
}
