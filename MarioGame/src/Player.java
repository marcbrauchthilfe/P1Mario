import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class Player {
    private final int w = 32, h = 48;
    private final Level level;
    private final double GRAVITY = 0.6;
    private final double MOVE_SPEED = 3.2;
    private final double JUMP_SPEED = -12.5;
    private final double MAX_FALL = 14.0;
    private final double COYOTE_TIME = 0.12;
    private final double JUMP_BUFFER_TIME = 0.12;
    private final double startX, startY;

    private double x, y;
    private double vx, vy;
    private boolean onGround = false;
    private double coyoteTimer = 0.0;
    private double jumpBufferTimer = 0.0;

    // ------- SPRITE -------
    private BufferedImage sprite = null;
    private boolean flipX = false;

    public Player(double startX, double startY, Level level) {
        this.x = startX;
        this.y = startY;
        this.startX = startX;
        this.startY = startY;
        this.level = level;
    }

    // Bild direkt aus Datei laden
    public void loadSprite(String path) {
        try {
            sprite = ImageIO.read(new File(path));
        } catch (Exception e) {
            System.err.println("Sprite konnte nicht geladen werden: " + path);
        }
    }

    public void update(double dt) {
        if (coyoteTimer > 0) coyoteTimer -= dt;
        if (jumpBufferTimer > 0) jumpBufferTimer -= dt;

        vy += GRAVITY;
        if (vy > MAX_FALL) vy = MAX_FALL;

        // --- horizontale Bewegung ---
        x += vx;
        Rectangle hb = getBounds();
        for (Tile t : level.getSolidTiles()) {
            if (hb.intersects(t.getRect())) {
                if (vx > 0) x = t.getX() - w;
                else if (vx < 0) x = t.getX() + t.getW();
                vx = 0;
                hb = getBounds();
            }
        }

        // --- vertikale Bewegung ---
        y += vy;
        onGround = false;
        for (Tile t : level.getSolidTiles()) {
            Rectangle tileRect = t.getRect();
            if (getBounds().intersects(tileRect)) {
                if (vy > 0) {
                    y = tileRect.y - h;
                    vy = 0;
                    onGround = true;
                    coyoteTimer = COYOTE_TIME;
                } else if (vy < 0) {
                    y = tileRect.y + tileRect.height;
                    vy = 0;
                }
            }
        }

        // --- Sprungpuffer ---
        if (jumpBufferTimer > 0 && (onGround || coyoteTimer > 0)) {
            doJump();
            jumpBufferTimer = 0;
            coyoteTimer = 0;
        }

        // --- Levelgrenzen ---
        if (x < 0) x = 0;
        if (x + w > level.getWidth()) x = level.getWidth() - w;
        if (y > level.getHeight() + 300) {
            respawn();
            GamePanel.subLive();
        }
    }

    // -------- Bewegung ----------
    public void moveLeft() {
        vx = -MOVE_SPEED;
        flipX = true;   // Sprite spiegeln
    }

    public void moveRight() {
        vx = MOVE_SPEED;
        flipX = false;  // Normal
    }

    public void stopHorizontal() {
        vx = 0;
    }

    public void pressJump() {
        jumpBufferTimer = JUMP_BUFFER_TIME;
    }

    public void releaseJump() {
        if (vy < 0) vy *= 0.6;
    }

    private void doJump() {
        vy = JUMP_SPEED;
        onGround = false;
    }

    public void bounceAfterStomp() {
        vy = JUMP_SPEED / 2;
    }

    public void respawn() {
        x = startX;
        y = startY;
        vx = 0;
        vy = 0;
    }

    public boolean isFalling() {
        return vy > 0;
    }

    // -------- Bounds / Position ----------
    public Rectangle getBounds() {
        return new Rectangle((int) Math.round(x), (int) Math.round(y), w, h);
    }

    public int getY() {
        return (int) Math.round(y);
    }

    public int getX() {
        return (int) Math.round(x);
    }

    // -------- Draw ----------
    public void draw(Graphics2D g, int camX) {
        int drawX = getX() - camX;
        int drawY = getY();

        if (sprite != null) {
            // Sprite zeichnen (links/rechts gespiegelt)
            if (!flipX) {
                g.drawImage(sprite, drawX, drawY, w, h, null);
            } else {
                g.drawImage(sprite, drawX + w, drawY, drawX, drawY + h,   // gespiegeltes Ziel
                        0, 0, sprite.getWidth(), sprite.getHeight(), null);
            }
        } else {
            // Fallback: alte Player-Zeichnung
            g.setColor(new Color(200, 30, 30));
            g.fillRoundRect(drawX, drawY, w, h, 6, 6);

            g.setColor(Color.WHITE);
            g.fillOval(drawX + 8, drawY + 8, 8, 8);

            g.setColor(Color.BLACK);
            g.fillOval(drawX + 10, drawY + 10, 3, 3);
        }
    }
}
