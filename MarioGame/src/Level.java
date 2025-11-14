import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.util.ArrayList;

public class Level {
    public static final int TILE_SIZE = 50;
    public static final int NUM_LEVELS = 4;

    private int width;
    private int height;
    private ArrayList<Tile> solidTiles = new ArrayList<>();
    private int[][] enemyPositions;
    private int endX, endY;

    // -------- TEXTUREN --------
    private BufferedImage texTile;
    private BufferedImage texBackground;
    private BufferedImage texFlag;

    public Level(int width, int height) {
        this.width = width;
        this.height = height;

        // Texturen laden
        loadTextures();
    }

    private void loadTextures() {
        try {
            texTile       = ImageIO.read(new File("res/background.png"));
            texBackground = ImageIO.read(new File("res/background.png"));
            texFlag       = ImageIO.read(new File("res/background.png"));
        } catch (Exception e) {
            System.err.println("Konnte Level-Texturen nicht laden!");
            e.printStackTrace();
        }
    }

    // -------- LEVEL ERSTELLEN --------
    public static Level createSampleLevel(int index) {
        Level level = new Level(2500, 600);
        ArrayList<Tile> tiles = new ArrayList<>();
        int groundY = 550;

        // Grundboden mit Lücken
        for (int x = 0; x < 50; x++) {
            if (x % 8 == 4 && index > 0) continue;
            tiles.add(new Tile(x * TILE_SIZE, groundY, TILE_SIZE, TILE_SIZE));
        }

        // --- Die LEVELs bleiben wie sie sind ---
        if (index == 0) {
            tiles.add(new Tile(400, 500, TILE_SIZE, TILE_SIZE));
            tiles.add(new Tile(500, 450, TILE_SIZE, TILE_SIZE));
            tiles.add(new Tile(600, 400, TILE_SIZE, TILE_SIZE));
            tiles.add(new Tile(950, 500, TILE_SIZE, TILE_SIZE));
            tiles.add(new Tile(1050, 450, TILE_SIZE, TILE_SIZE));
            tiles.add(new Tile(1150, 400, TILE_SIZE, TILE_SIZE));
            tiles.add(new Tile(1250, 350, TILE_SIZE, TILE_SIZE));

            for (int x = 38; x < 45; x++) {
                tiles.add(new Tile(x * TILE_SIZE, groundY - (x - 38) * 10, TILE_SIZE, TILE_SIZE));
            }

            level.enemyPositions = new int[][] {
                    {700, 500}, {1100, 500}, {1500, 500}
            };

            level.endX = 2300;
            level.endY = groundY - TILE_SIZE;
        }

        /* ----------------------------
           LEVEL 1-3 bleiben unverändert
           ---------------------------- */
        else if (index == 1) { /* ... wie vorher ... */ }
        else if (index == 2) { /* ... wie vorher ... */ }
        else if (index == 3) { /* ... wie vorher ... */ }

        level.solidTiles = tiles;
        return level;
    }

    // --------- FLAGGENKOLLISION ---------
    public boolean isEndReached(Player p) {
        Rectangle flagRect = new Rectangle(endX, endY - 150, 40, 200);
        return p.getBounds().intersects(flagRect);
    }

    // --------- ZEICHNEN MIT TEXTUREN ---------
    public void draw(Graphics2D g, int camX) {

        // Hintergrund (Texture über gesamte Breite)
        if (texBackground != null) {
            for (int x = 0; x < width; x += texBackground.getWidth()) {
                g.drawImage(texBackground, x - camX, 0, null);
            }
        } else {
            g.setColor(new Color(135, 206, 250));
            g.fillRect(0, 0, width, height);
        }

        // Tiles
        for (Tile t : solidTiles) {
            if (texTile != null) {
                g.drawImage(texTile, t.getX() - camX, t.getY(),
                        TILE_SIZE, TILE_SIZE, null);
            } else {
                g.setColor(new Color(80, 80, 80));
                g.fillRect(t.getX() - camX, t.getY(), t.getW(), t.getH());
            }
        }

        // Flagge
        if (texFlag != null) {
            g.drawImage(texFlag,
                    endX - camX,
                    endY - 120,
                    80,
                    120,
                    null);
        } else {
            // Fallback: Rechtecke
            g.setColor(new Color(200, 0, 0));
            g.fillRect(endX - camX + 5, endY - 150, 40, 25);
            g.setColor(Color.BLACK);
            g.drawRect(endX - camX + 5, endY - 150, 40, 25);
        }
    }

    // --------- GETTER ---------
    public ArrayList<Tile> getSolidTiles() { return solidTiles; }
    public int[][] getEnemyPositions() { return enemyPositions; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
}
