import java.awt.*;
import java.util.ArrayList;

public class Level {
    public static final int TILE_SIZE = 50;
    public static final int NUM_LEVELS = 4;

    private int width;
    private int height;
    private ArrayList<Tile> solidTiles = new ArrayList<>();
    private int[][] enemyPositions;
    private int endX, endY;

    public Level(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public static Level createSampleLevel(int index) {
        Level level = new Level(2500, 600);
        ArrayList<Tile> tiles = new ArrayList<>();
        int groundY = 550;

        // Grundboden mit kleinen Lücken
        for (int x = 0; x < 50; x++) {
            if (x % 8 == 4 && index > 0) continue; // kleine Lücken in höheren Levels
            tiles.add(new Tile(x * TILE_SIZE, groundY, TILE_SIZE, TILE_SIZE));
        }

        // -----------------------
        // LEVEL 1 – einfaches Springen & Bewegung
        // -----------------------
        if (index == 0) {
            tiles.add(new Tile(400, 500, TILE_SIZE, TILE_SIZE));
            tiles.add(new Tile(500, 450, TILE_SIZE, TILE_SIZE));
            tiles.add(new Tile(600, 400, TILE_SIZE, TILE_SIZE));

            // neue Sektion 2 (nach 900px)
            tiles.add(new Tile(950, 500, TILE_SIZE, TILE_SIZE));
            tiles.add(new Tile(1050, 450, TILE_SIZE, TILE_SIZE));
            tiles.add(new Tile(1150, 400, TILE_SIZE, TILE_SIZE));
            tiles.add(new Tile(1250, 350, TILE_SIZE, TILE_SIZE));

            // Endzone mit leichter Rampe
            for (int x = 38; x < 45; x++) {
                tiles.add(new Tile(x * TILE_SIZE, groundY - (x - 38) * 10, TILE_SIZE, TILE_SIZE));
            }

            level.enemyPositions = new int[][] {
                    {700, 500}, {1100, 500}, {1500, 500}
            };

            level.endX = 2300;
            level.endY = groundY - TILE_SIZE;
        }

        // -----------------------
        // LEVEL 2 – Doppelsprünge, Hindernisse, kleine Gruben
        // -----------------------
        else if (index == 1) {
            tiles.add(new Tile(400, 500, TILE_SIZE, TILE_SIZE));
            tiles.add(new Tile(500, 450, TILE_SIZE, TILE_SIZE));
            tiles.add(new Tile(600, 400, TILE_SIZE, TILE_SIZE));
            tiles.add(new Tile(750, 400, TILE_SIZE, TILE_SIZE));
            tiles.add(new Tile(900, 350, TILE_SIZE, TILE_SIZE));

            // zweite Hälfte mit erhöhten Plattformen
            for (int i = 0; i < 5; i++) {
                tiles.add(new Tile(1200 + i * 70, 450 - (i % 2) * 50, TILE_SIZE, TILE_SIZE));
            }

            tiles.add(new Tile(1650, 350, TILE_SIZE, TILE_SIZE));
            tiles.add(new Tile(1750, 400, TILE_SIZE, TILE_SIZE));
            tiles.add(new Tile(1900, 450, TILE_SIZE, TILE_SIZE));

            level.enemyPositions = new int[][] {
                    {500, 500}, {850, 500}, {1300, 500}, {1700, 500}
            };

            level.endX = 2400;
            level.endY = groundY - TILE_SIZE;
        }

        // -----------------------
        // LEVEL 3 – Gegnerpfade & anspruchsvollere Sprünge
        // -----------------------
        else if (index == 2) {
            // erste Sektion
            tiles.add(new Tile(400, 500, TILE_SIZE, TILE_SIZE));
            tiles.add(new Tile(500, 450, TILE_SIZE, TILE_SIZE));
            tiles.add(new Tile(600, 400, TILE_SIZE, TILE_SIZE));

            // tiefe Lücke + Plattform darüber
            for (int x = 13; x < 17; x++) {
                int finalX = x;
                tiles.removeIf(t -> t.getX() / TILE_SIZE == finalX);
            }
            tiles.add(new Tile(700, 350, TILE_SIZE, TILE_SIZE));
            tiles.add(new Tile(800, 300, TILE_SIZE, TILE_SIZE));
            tiles.add(new Tile(900, 350, TILE_SIZE, TILE_SIZE));

            // zweite Sektion – gestaffelte Höhen
            tiles.add(new Tile(1200, 450, TILE_SIZE, TILE_SIZE));
            tiles.add(new Tile(1300, 400, TILE_SIZE, TILE_SIZE));
            tiles.add(new Tile(1400, 350, TILE_SIZE, TILE_SIZE));
            tiles.add(new Tile(1500, 400, TILE_SIZE, TILE_SIZE));
            tiles.add(new Tile(1600, 450, TILE_SIZE, TILE_SIZE));

            level.enemyPositions = new int[][] {
                    {600, 500}, {1000, 500}, {1450, 500}, {1800, 500}
            };

            level.endX = 2400;
            level.endY = groundY - TILE_SIZE;
        }

        // -----------------------
        // LEVEL 4 – Finale: mehr Vertikalität & lange Sprungpassagen
        // -----------------------
        else if (index == 3) {
            tiles.add(new Tile(400, 500, TILE_SIZE, TILE_SIZE));
            tiles.add(new Tile(500, 450, TILE_SIZE, TILE_SIZE));
            tiles.add(new Tile(600, 400, TILE_SIZE, TILE_SIZE));
            tiles.add(new Tile(750, 350, TILE_SIZE, TILE_SIZE));

            // mittlere Plattform-Linie
            for (int i = 0; i < 7; i++) {
                tiles.add(new Tile(1000 + i * 100, 400 - (i % 2) * 50, TILE_SIZE, TILE_SIZE));
            }

            // finale Rampe hoch zur Flagge
            for (int i = 0; i < 6; i++) {
                tiles.add(new Tile(1800 + i * 50, groundY - i * 30, TILE_SIZE, TILE_SIZE));
            }

            level.enemyPositions = new int[][] {
                    {650, 500}, {1100, 500}, {1400, 500}, {1700, 500}, {2000, 500}
            };

            level.endX = 2450;
            level.endY = groundY - 150;
        }

        level.solidTiles = tiles;
        return level;
    }

    public boolean isEndReached(Player p) {
        Rectangle flagRect = new Rectangle(endX, endY - 150, 40, 200);
        return p.getBounds().intersects(flagRect);
    }

    public void draw(Graphics2D g, int camX) {
        // Himmel
        g.setColor(new Color(135, 206, 250));
        g.fillRect(0, 0, width, height);

        // Boden
        g.setColor(new Color(80, 80, 80));
        for (Tile t : solidTiles) {
            g.fillRect(t.getX() - camX, t.getY(), t.getW(), t.getH());
        }

        // Flagge
        g.setColor(new Color(50, 50, 50));
        g.fillRect(endX - camX - 10, endY + TILE_SIZE - 20, 60, 20);

        g.setColor(new Color(200, 200, 200));
        g.fillRect(endX - camX, endY - 150, 5, 150);

        g.setColor(Color.RED);
        g.fillRect(endX - camX + 5, endY - 150, 40, 25);
        g.setColor(Color.BLACK);
        g.drawRect(endX - camX + 5, endY - 150, 40, 25);
    }

    public ArrayList<Tile> getSolidTiles() { return solidTiles; }
    public int[][] getEnemyPositions() { return enemyPositions; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
}
