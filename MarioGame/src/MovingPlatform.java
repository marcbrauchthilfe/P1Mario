import java.awt.*;
import java.awt.image.BufferedImage;

public class MovingPlatform extends Tile {

    private final double w, h;
    private final double startX, startY;
    private final double endX, endY;
    private final double speed;
    private double x, y;
    private int direction = 1; // nur eine Richtung entlang der Linie

    private double velX = 0;
    private double velY = 0;

    public MovingPlatform(double x, double y, double width, double height, double endX, double endY, double speed, BufferedImage texture) {
        super((int) x, (int) y, (int) width, (int) height, texture);
        this.x = x;
        this.y = y;
        this.w = width;
        this.h = height;
        this.startX = x;
        this.startY = y;
        this.endX = endX;
        this.endY = endY;
        this.speed = speed;
    }

    public void update(double dt) {
        // Vektor von Start zu End
        double dx = endX - startX;
        double dy = endY - startY;
        double dist = Math.sqrt(dx * dx + dy * dy);
        if (dist == 0) dist = 1;

        double normX = dx / dist;
        double normY = dy / dist;

        // Bewegung entlang der Linie
        velX = normX * speed * direction * dt * 60;
        velY = normY * speed * direction * dt * 60;

        x += velX;
        y += velY;

        // Prüfe Abstand von Startpunkt
        double proj = ((x - startX) * dx + (y - startY) * dy) / (dist * dist);
        if (proj >= 1) direction = -1;
        else if (proj <= 0) direction = 1;

        super.x = (int) Math.round(x);
        super.y = (int) Math.round(y);
    }

    public double getVelocityX() {
        return velX;
    }

    public double getVelocityY() {
        return velY;
    }

    @Override
    public void draw(Graphics2D g, int camX) {
        g.setColor(new Color(180, 180, 180, 150));
        int sx = (int) (startX - camX + w / 2);
        int sy = (int) (startY + h / 2);
        int ex = (int) (endX - camX + w / 2);
        int ey = (int) (endY + h / 2);
        g.setStroke(new BasicStroke(3));
        g.drawLine(sx, sy, ex, ey);

        g.drawImage(texture, (int) x - camX, (int) y, (int) w, (int) h, null);
    }
}
