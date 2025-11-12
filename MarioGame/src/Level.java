import java.awt.*;
import java.util.ArrayList;

public class Level {
    public static final int TILE_SIZE = 50;
    public static final int NUM_LEVELS = 4;

    private final int width;
    private final int height;
    private ArrayList<Tile> solidTiles = new ArrayList<>();
    private int[][] enemyPositions;
    private int endX, endY; // Flaggenposition

    public Level(int width, int height) {
        this.width = width;
        this.height = height;
    }

    // ----------------------
    // Level-Daten erstellen
    // ----------------------
    public static Level createSampleLevel(int index) {
        Level level = new Level(2000, 600);
        ArrayList<Tile> tiles = new ArrayList<>();
        int groundY = 550;

        // Grundboden
        for (int x = 0; x < 40; x++) {
            tiles.add(new Tile(x * TILE_SIZE, groundY, TILE_SIZE, TILE_SIZE));
        }

        // ======= LEVEL 1 =======
        if (index == 0) {
            tiles.add(new Tile(300, 450, TILE_SIZE, TILE_SIZE));
            tiles.add(new Tile(400, 400, TILE_SIZE, TILE_SIZE));
            tiles.add(new Tile(500, 350, TILE_SIZE, TILE_SIZE));

            level.enemyPositions = new int[][]{{600, 500}, {900, 500}};

            level.endX = 1800;
            level.endY = groundY - TILE_SIZE;
        }

        // ======= LEVEL 2 =======
        else if (index == 1) {
            for (int x = 0; x < 20; x++) {
                tiles.add(new Tile(x * TILE_SIZE, groundY, TILE_SIZE, TILE_SIZE));
            }
            tiles.add(new Tile(600, 450, TILE_SIZE, TILE_SIZE));
            tiles.add(new Tile(650, 400, TILE_SIZE, TILE_SIZE));
            tiles.add(new Tile(700, 350, TILE_SIZE, TILE_SIZE));

            level.enemyPositions = new int[][]{{400, 500}, {900, 500}, {1200, 500}};

            level.endX = 1800;
            level.endY = groundY - TILE_SIZE;
        }

        // ======= LEVEL 3 =======
        else if (index == 2) {
            for (int x = 0; x < 30; x++) {
                tiles.add(new Tile(x * TILE_SIZE, groundY, TILE_SIZE, TILE_SIZE));
            }
            tiles.add(new Tile(500, 450, TILE_SIZE, TILE_SIZE));
            tiles.add(new Tile(550, 400, TILE_SIZE, TILE_SIZE));
            tiles.add(new Tile(900, 400, TILE_SIZE, TILE_SIZE));

            level.enemyPositions = new int[][]{{800, 500}, {1000, 500}};

            level.endX = 1800;
            level.endY = groundY - TILE_SIZE;
        }

        // ======= LEVEL 4 =======
        else if (index == 3) {
            for (int x = 0; x < 40; x++) {
                tiles.add(new Tile(x * TILE_SIZE, groundY, TILE_SIZE, TILE_SIZE));
            }
            tiles.add(new Tile(400, 500, TILE_SIZE, TILE_SIZE));
            tiles.add(new Tile(500, 450, TILE_SIZE, TILE_SIZE));
            tiles.add(new Tile(600, 400, TILE_SIZE, TILE_SIZE));

            level.enemyPositions = new int[][]{{700, 500}, {1000, 500}, {1400, 500}};

            level.endX = 1800;
            level.endY = groundY - TILE_SIZE;
        }

        level.solidTiles = tiles;
        return level;
    }

    // ----------------------
    // Flaggen-Ende erkennen
    // ----------------------
    public boolean isEndReached(Player p) {
        Rectangle flagRect = new Rectangle(endX, endY - 150, 30, 200);
        return p.getBounds().intersects(flagRect);
    }

    // ----------------------
    // Zeichnen des Levels
    // ----------------------
    public void draw(Graphics2D g, int camX) {
        // Hintergrundfarbe
        g.setColor(new Color(130, 200, 255));
        g.fillRect(0, 0, width, height);

        // Bodenblöcke
        g.setColor(new Color(70, 70, 70));
        for (Tile t : solidTiles) {
            g.fillRect(t.getX() - camX, t.getY(), t.getW(), t.getH());
        }

        // Flaggenbasis (dunkler Block)
        g.setColor(new Color(50, 50, 50));
        g.fillRect(endX - camX - 10, endY + TILE_SIZE - 20, 60, 20);

        // Stange (grau)
        g.setColor(new Color(200, 200, 200));
        g.fillRect(endX - camX, endY + TILE_SIZE - 20 - 150, 5, 150);

        // Flagge (rot)
        g.setColor(Color.RED);
        g.fillRect(endX - camX + 5, endY + TILE_SIZE - 20 - 150, 40, 25);

        // Fahnenumrandung
        g.setColor(Color.BLACK);
        g.drawRect(endX - camX + 5, endY + TILE_SIZE - 20 - 150, 40, 25);
    }

    // ----------------------
    // Getter
    // ----------------------
    public ArrayList<Tile> getSolidTiles() {
        return solidTiles;
    }

    public int[][] getEnemyPositions() {
        return enemyPositions;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
