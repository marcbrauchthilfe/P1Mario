import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

public class Level {
    public static final int NUM_LEVELS = 8;
    private static final int BASE_TILE_SIZE = 32;
    // TILE_SIZE wird korrekt mit dem Skalierungsfaktor multipliziert
    public static final int TILE_SIZE = (int) (BASE_TILE_SIZE * Zoom.SCALE);
    public static final int groundY = GamePanel.HEIGHT - TILE_SIZE;
    // Annahme: PLAYER_HEIGHT ist in Player.java korrekt skaliert
    private static final int PLAYER_HEIGHT = Player.PLAYER_HEIGHT;
    private static BufferedImage groundTexture;
    private static BufferedImage blockTexture;
    private static BufferedImage background;

    // Ressourcen werden nur einmal beim Start geladen (sauberer)
    static {
        try {
            groundTexture = ImageIO.read(new File("res/ground_V2.png"));
            blockTexture = ImageIO.read(new File("res/block_V2.png"));
            background = ImageIO.read(new File("res/background_V2.png"));
        } catch (Exception e) {
            System.err.println("Fehler beim Laden der Level-Texturen.");
        }
    }

    private final int width;
    private final int height;
    private ArrayList<Tile> solidTiles = new ArrayList<>();
    private int[][] enemyPositions;
    private int endX, endY;

    // Konstruktor ist jetzt nur für die Level-Dimensionen zuständig
    public Level(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public static Level createSampleLevel(int index) {
        // Level-Dimensionen skalieren
        Level level = new Level((int) (2500 * Zoom.SCALE), (int) (600 * Zoom.SCALE));
        ArrayList<Tile> tiles = new ArrayList<>();

        int spawnY = groundY - TILE_SIZE;

        // Grundboden mit kleinen Lücken
        for (int x = 0; x < 60; x++) {
            //if (x % 8 == 4 && index > 0) continue;
            tiles.add(new Tile(x * TILE_SIZE, groundY, TILE_SIZE, TILE_SIZE, groundTexture));
        }

        // -----------------------
        // LEVEL 1 – einfaches Springen & Bewegung
        // -----------------------
        if (index == 0) {
            tiles.add(new Tile(8 * TILE_SIZE, groundY - TILE_SIZE, TILE_SIZE, TILE_SIZE, blockTexture));
            tiles.add(new Tile(10 * TILE_SIZE, groundY - TILE_SIZE - PLAYER_HEIGHT, TILE_SIZE, TILE_SIZE, blockTexture));
            tiles.add(new Tile(12 * TILE_SIZE, groundY - TILE_SIZE - 2 * PLAYER_HEIGHT, TILE_SIZE, TILE_SIZE, blockTexture));

            // neue Sektion 2
            tiles.add(new Tile(19 * TILE_SIZE, groundY - TILE_SIZE, TILE_SIZE, TILE_SIZE, blockTexture));
            tiles.add(new Tile(21 * TILE_SIZE, groundY - TILE_SIZE - PLAYER_HEIGHT, TILE_SIZE, TILE_SIZE, blockTexture));
            tiles.add(new Tile(23 * TILE_SIZE, groundY - TILE_SIZE - 2 * PLAYER_HEIGHT, TILE_SIZE, TILE_SIZE, blockTexture));
            tiles.add(new Tile(25 * TILE_SIZE, groundY - TILE_SIZE - 3 * PLAYER_HEIGHT, TILE_SIZE, TILE_SIZE, blockTexture));

            // Endzone mit leichter Rampe: 10 Pixel Steigung/Höhe skaliert
            int rampStep = (int) (10 * Zoom.SCALE);
            for (int x = 38; x < 45; x++) {
                tiles.add(new Tile(x * TILE_SIZE, groundY - (x - 38) * rampStep - rampStep, TILE_SIZE, rampStep, blockTexture));
            }

            level.enemyPositions = new int[][]{{14 * TILE_SIZE, spawnY}, {22 * TILE_SIZE, spawnY}, {30 * TILE_SIZE, spawnY}};

            level.endX = 48 * TILE_SIZE;
            level.endY = groundY - TILE_SIZE;
        }

        // -----------------------
        // LEVEL 2 – Doppelsprünge, Hindernisse, kleine Gruben
        // -----------------------
        else if (index == 1) {
            tiles.add(new Tile(8 * TILE_SIZE, groundY - TILE_SIZE, TILE_SIZE, TILE_SIZE, blockTexture));
            tiles.add(new Tile(10 * TILE_SIZE, groundY - TILE_SIZE - PLAYER_HEIGHT, TILE_SIZE, TILE_SIZE, blockTexture));
            tiles.add(new Tile(12 * TILE_SIZE, groundY - TILE_SIZE - 2 * PLAYER_HEIGHT, TILE_SIZE, TILE_SIZE, blockTexture));
            tiles.add(new Tile(15 * TILE_SIZE, groundY - TILE_SIZE - 2 * PLAYER_HEIGHT, TILE_SIZE, TILE_SIZE, blockTexture));
            tiles.add(new Tile(18 * TILE_SIZE, groundY - TILE_SIZE - 3 * PLAYER_HEIGHT, TILE_SIZE, TILE_SIZE, blockTexture));

            // zweite Hälfte mit erhöhten Plattformen: 50 Pixel Offset skaliert
            int platformOffset = (int) (50 * Zoom.SCALE);
            for (int i = 0; i < 5; i++) {
                tiles.add(new Tile((int) (24 * TILE_SIZE + i * TILE_SIZE * 1.4), groundY - TILE_SIZE - PLAYER_HEIGHT - (i % 2) * platformOffset, TILE_SIZE, TILE_SIZE, blockTexture));
            }

            tiles.add(new Tile(33 * TILE_SIZE, groundY - 2 * TILE_SIZE - 2 * PLAYER_HEIGHT, TILE_SIZE, TILE_SIZE, blockTexture));
            tiles.add(new Tile(35 * TILE_SIZE, groundY - 2 * TILE_SIZE - PLAYER_HEIGHT, TILE_SIZE, TILE_SIZE, blockTexture));
            tiles.add(new Tile(38 * TILE_SIZE, groundY - 2 * TILE_SIZE, TILE_SIZE, TILE_SIZE, blockTexture));
            tiles.add(new Tile(40 * TILE_SIZE, groundY - TILE_SIZE, TILE_SIZE, TILE_SIZE, blockTexture));

            level.enemyPositions = new int[][]{{10 * TILE_SIZE, spawnY}, {17 * TILE_SIZE, spawnY}, {26 * TILE_SIZE, spawnY}, {34 * TILE_SIZE, spawnY}};

            level.endX = 48 * TILE_SIZE;
            level.endY = groundY - TILE_SIZE;
        }

        // -----------------------
        // LEVEL 3 – Gegnerpfade & anspruchsvollere Sprünge
        // -----------------------
        else if (index == 2) {
            // ... (Hier sind keine weiteren Pixel-Offsets zu skalieren, da nur TILE_SIZE und PLAYER_HEIGHT verwendet werden, die bereits skaliert sind)

            tiles.add(new Tile(8 * TILE_SIZE, groundY - TILE_SIZE, TILE_SIZE, TILE_SIZE, blockTexture));
            tiles.add(new Tile(10 * TILE_SIZE, groundY - TILE_SIZE - PLAYER_HEIGHT, TILE_SIZE, TILE_SIZE, blockTexture));
            tiles.add(new Tile(12 * TILE_SIZE, groundY - TILE_SIZE - 2 * PLAYER_HEIGHT, TILE_SIZE, TILE_SIZE, blockTexture));

            // tiefe Lücke + Plattform darüber
            for (int x = 13; x < 17; x++) {
                int finalX = x;
                tiles.removeIf(t -> t.getX() / TILE_SIZE == finalX);
            }
            tiles.add(new Tile(14 * TILE_SIZE, groundY - TILE_SIZE - 3 * PLAYER_HEIGHT, TILE_SIZE, TILE_SIZE, blockTexture));
            tiles.add(new Tile(16 * TILE_SIZE, groundY - TILE_SIZE - 4 * PLAYER_HEIGHT, TILE_SIZE, TILE_SIZE, blockTexture));
            tiles.add(new Tile(18 * TILE_SIZE, groundY - TILE_SIZE - 3 * PLAYER_HEIGHT, TILE_SIZE, TILE_SIZE, blockTexture));

            // zweite Sektion – gestaffelte Höhen
            tiles.add(new Tile(24 * TILE_SIZE, groundY - TILE_SIZE - PLAYER_HEIGHT, TILE_SIZE, TILE_SIZE, blockTexture));
            tiles.add(new Tile(26 * TILE_SIZE, groundY - TILE_SIZE - 2 * PLAYER_HEIGHT, TILE_SIZE, TILE_SIZE, blockTexture));
            tiles.add(new Tile(28 * TILE_SIZE, groundY - TILE_SIZE - 3 * PLAYER_HEIGHT, TILE_SIZE, TILE_SIZE, blockTexture));
            tiles.add(new Tile(30 * TILE_SIZE, groundY - TILE_SIZE - 2 * PLAYER_HEIGHT, TILE_SIZE, TILE_SIZE, blockTexture));
            tiles.add(new Tile(32 * TILE_SIZE, groundY - TILE_SIZE - PLAYER_HEIGHT, TILE_SIZE, TILE_SIZE, blockTexture));
            tiles.add(new Tile(49 * TILE_SIZE, groundY - TILE_SIZE, TILE_SIZE, TILE_SIZE, null));

            level.enemyPositions = new int[][]{{11 * TILE_SIZE, spawnY}, {22 * TILE_SIZE, spawnY}, {29 * TILE_SIZE, spawnY}, {38 * TILE_SIZE, spawnY}};

            level.endX = 48 * TILE_SIZE;
            level.endY = groundY - TILE_SIZE;
        }

        // -----------------------
        // LEVEL 4 – Finale: mehr Vertikalitaet & lange Sprungpassagen
        // -----------------------
        else if (index == 3) {
            tiles.add(new Tile(8 * TILE_SIZE, groundY - TILE_SIZE, TILE_SIZE, TILE_SIZE, blockTexture));
            tiles.add(new Tile(10 * TILE_SIZE, groundY - TILE_SIZE - PLAYER_HEIGHT, TILE_SIZE, TILE_SIZE, blockTexture));
            tiles.add(new Tile(12 * TILE_SIZE, groundY - TILE_SIZE - 2 * PLAYER_HEIGHT, TILE_SIZE, TILE_SIZE, blockTexture));
            tiles.add(new Tile(15 * TILE_SIZE, groundY - TILE_SIZE - 3 * PLAYER_HEIGHT, TILE_SIZE, TILE_SIZE, blockTexture));

            // mittlere Plattform-Linie: 50 Pixel Offset skaliert
            int platformOffset = (int) (50 * Zoom.SCALE);
            for (int i = 0; i < 7; i++) {
                tiles.add(new Tile(20 * TILE_SIZE + i * 2 * TILE_SIZE, groundY - TILE_SIZE - 2 * PLAYER_HEIGHT - (i % 2) * platformOffset, TILE_SIZE, TILE_SIZE, blockTexture));
            }

            // finale Rampe hoch zur Flagge: TILE_SIZE ist bereits skaliert, hier keine weitere Skalierung nötig
            for (int i = 0; i < 6; i++) {
                tiles.add(new Tile(37 * TILE_SIZE + i * TILE_SIZE, (int) (groundY - TILE_SIZE - i * 0.5 * TILE_SIZE), TILE_SIZE, TILE_SIZE, blockTexture));
            }

            level.enemyPositions = new int[][]{{13 * TILE_SIZE, spawnY}, {22 * TILE_SIZE, spawnY}, {28 * TILE_SIZE, spawnY}, {34 * TILE_SIZE, spawnY}, {40 * TILE_SIZE, spawnY}};

            level.endX = 49 * TILE_SIZE;
            level.endY = groundY - 3 * PLAYER_HEIGHT;
        }

        // -------
        // LEVEL 5
        // -------
        else if (index == 4) {
            // 1) Erzeuge längeren Boden, dann große Lücke (Canyon)
            // entferne Boden von tx=36..68 (Canyon)
            tiles.removeIf(t -> {
                int tx = t.getX() / TILE_SIZE;
                return tx >= 36 && tx <= 68;
            });

            // 2) Dichte Anordnung links vor dem Canyon (Stufen/Plattformen)
            tiles.add(new Tile(10 * TILE_SIZE, groundY - TILE_SIZE, TILE_SIZE, TILE_SIZE, blockTexture));
            tiles.add(new Tile(12 * TILE_SIZE, groundY - 2 * TILE_SIZE, TILE_SIZE, TILE_SIZE, blockTexture));
            tiles.add(new Tile(14 * TILE_SIZE, groundY - 3 * TILE_SIZE, TILE_SIZE, TILE_SIZE, blockTexture));
            tiles.add(new Tile(16 * TILE_SIZE, groundY - 2 * TILE_SIZE, TILE_SIZE, TILE_SIZE, blockTexture));
            tiles.add(new Tile(18 * TILE_SIZE, groundY - TILE_SIZE, TILE_SIZE, TILE_SIZE, blockTexture));

            // 3) horizontale Movers (Kettenbrücke über Canyon)
            // Plattform A: bewegt sich kurz hin und her; Spieler springt auf sie, sie fährt zu Insel
            tiles.add(new MovingPlatform(36 * TILE_SIZE, groundY - 2 * TILE_SIZE, TILE_SIZE, TILE_SIZE, 42 * TILE_SIZE, groundY - 2 * TILE_SIZE, 1.8 * Zoom.SCALE, blockTexture));

            // Plattform B: startet auf der Insel-Seite und pendelt; chain notwendig
            tiles.add(new MovingPlatform(50 * TILE_SIZE, groundY - 2 * TILE_SIZE, TILE_SIZE, TILE_SIZE, 44 * TILE_SIZE, groundY - 2 * TILE_SIZE, 1.8 * Zoom.SCALE, blockTexture));

            // 4) Insel-Anordnung nach Canyon (mehrere Stufen, hoch zum Turm)
            tiles.add(new Tile(52 * TILE_SIZE, groundY - TILE_SIZE, TILE_SIZE, TILE_SIZE, blockTexture));
            tiles.add(new Tile(54 * TILE_SIZE, groundY - 2 * TILE_SIZE, TILE_SIZE, TILE_SIZE, blockTexture));
            tiles.add(new Tile(56 * TILE_SIZE, groundY - 3 * TILE_SIZE, TILE_SIZE, TILE_SIZE, blockTexture));
            tiles.add(new Tile(58 * TILE_SIZE, groundY - 4 * TILE_SIZE, TILE_SIZE, TILE_SIZE, blockTexture));

            // 5) Zweiter mover, um auf die hohe Turmplattform zu kommen
            tiles.add(new MovingPlatform(61 * TILE_SIZE, groundY - 4 * TILE_SIZE, TILE_SIZE, TILE_SIZE, 74 * TILE_SIZE, groundY - 4 * TILE_SIZE, 1.4 * Zoom.SCALE, blockTexture));

            // 6) Turmaufbau zur Flagge (Höhe: 4 tiles)
            for (int h = 0; h < 5; h++) {
                tiles.add(new Tile(78 * TILE_SIZE, groundY - (h + 1) * TILE_SIZE, TILE_SIZE, TILE_SIZE, blockTexture));
            }
            //tiles.add(new Tile(80 * TILE_SIZE, groundY - 6 * TILE_SIZE, TILE_SIZE, TILE_SIZE, blockTexture)); // Flag base

            level.enemyPositions = new int[][]{{13 * TILE_SIZE, spawnY}, {34 * TILE_SIZE, spawnY}};
            level.endX = 80 * TILE_SIZE;
            level.endY = groundY - TILE_SIZE;
        }

        // -------
        // LEVEL 6
        // -------
        else if (index == 5) {
            // 1) Definiere mehrere Gaps (unten durchlaufen unmöglich)
            int[][] gaps = new int[][]{{8, 14},   // early gap
                    {22, 28},  // mid gap
                    {38, 46},  // long mid gap
                    {56, 64}   // late gap before ascent
            };
            for (int[] g : gaps) {
                int s = g[0], e = g[1];
                tiles.removeIf(t -> {
                    int tx = t.getX() / TILE_SIZE;
                    return tx >= s && tx <= e;
                });
            }

            // 2) Start-Platforms (präzise)
            tiles.add(new Tile(6 * TILE_SIZE, groundY - TILE_SIZE, TILE_SIZE, TILE_SIZE, blockTexture));
            tiles.add(new Tile(7 * TILE_SIZE, groundY - 2 * TILE_SIZE, TILE_SIZE, TILE_SIZE, blockTexture));

            // 3) Kette von horizontalen moving platforms über die ersten Gaps
            tiles.add(new MovingPlatform(10 * TILE_SIZE, groundY - 2 * TILE_SIZE, TILE_SIZE, TILE_SIZE, 13 * TILE_SIZE, groundY - 2 * TILE_SIZE, 1.6 * Zoom.SCALE, blockTexture));
            tiles.add(new Tile(21 * TILE_SIZE, groundY - TILE_SIZE, TILE_SIZE, TILE_SIZE, blockTexture));
            tiles.add(new MovingPlatform(24 * TILE_SIZE, groundY - 3 * TILE_SIZE, TILE_SIZE, TILE_SIZE, 30 * TILE_SIZE, groundY - 3 * TILE_SIZE, 1.8 * Zoom.SCALE, blockTexture));
            tiles.add(new MovingPlatform(40 * TILE_SIZE, groundY - 2 * TILE_SIZE, TILE_SIZE, TILE_SIZE, 45 * TILE_SIZE, groundY - 2 * TILE_SIZE, 2.0 * Zoom.SCALE, blockTexture));

            // 4) Zwischenplattformen + kleine Türme (erhöhte Plateaus)
            tiles.add(new Tile(50 * TILE_SIZE, groundY - TILE_SIZE, TILE_SIZE, TILE_SIZE, blockTexture));
            tiles.add(new Tile(52 * TILE_SIZE, groundY - 2 * TILE_SIZE, TILE_SIZE, TILE_SIZE, blockTexture));
            tiles.add(new Tile(54 * TILE_SIZE, groundY - 4 * TILE_SIZE, TILE_SIZE, TILE_SIZE, blockTexture));

            // 5) Langer „swing“ mover zu finaler Ascent
            tiles.add(new MovingPlatform(58 * TILE_SIZE, groundY - 4 * TILE_SIZE, TILE_SIZE, TILE_SIZE, 67 * TILE_SIZE, groundY - 4 * TILE_SIZE, 1.3 * Zoom.SCALE, blockTexture));

            // 6) Finale Stufen und Flagge (hoch)
            tiles.add(new Tile(72 * TILE_SIZE, groundY - TILE_SIZE, TILE_SIZE, TILE_SIZE, blockTexture));
            tiles.add(new Tile(72 * TILE_SIZE, groundY - 2 * TILE_SIZE, TILE_SIZE, TILE_SIZE, blockTexture));
            tiles.add(new Tile(74 * TILE_SIZE, groundY - 3 * TILE_SIZE, TILE_SIZE, TILE_SIZE, blockTexture));

            level.enemyPositions = new int[][]{{15 * TILE_SIZE, spawnY}, {30 * TILE_SIZE, spawnY}, {47 * TILE_SIZE, spawnY}};
            level.endX = 76 * TILE_SIZE;
            level.endY = groundY - 5 * TILE_SIZE;
        }


        // -------
        // LEVEL 7
        // -------
        else if (index == 6) {
            // 1) Großer zentraler „Trough“: entferne Boden großflächig, aber lasse sparsely stepping tiles
            tiles.removeIf(t -> {
                int tx = t.getX() / TILE_SIZE;
                return tx >= 6 && tx <= 110 && (tx % 2 == 0); // viele Lücken, aber ein paar stepping stones
            });

            // 2) Serie horizontaler movers auf progressively higher rows
            tiles.add(new MovingPlatform(12 * TILE_SIZE, groundY - 3 * TILE_SIZE, TILE_SIZE, TILE_SIZE, 20 * TILE_SIZE, groundY - 3 * TILE_SIZE, 2.0 * Zoom.SCALE, blockTexture));
            tiles.add(new MovingPlatform(30 * TILE_SIZE, groundY - 5 * TILE_SIZE, TILE_SIZE, TILE_SIZE, 22 * TILE_SIZE, groundY - 5 * TILE_SIZE, 2.0 * Zoom.SCALE, blockTexture));
            tiles.add(new MovingPlatform(32 * TILE_SIZE, groundY - 7 * TILE_SIZE, TILE_SIZE, TILE_SIZE, 40 * TILE_SIZE, groundY - 7 * TILE_SIZE, 2.0 * Zoom.SCALE, blockTexture));

            // 3) stacked vertical movers that form the ascent to the fortress roof
            tiles.add(new MovingPlatform(44 * TILE_SIZE, groundY - 3 * TILE_SIZE, TILE_SIZE, TILE_SIZE, 44 * TILE_SIZE, groundY - 10 * TILE_SIZE, Zoom.SCALE, blockTexture));
            tiles.add(new MovingPlatform(48 * TILE_SIZE, groundY - 5 * TILE_SIZE, TILE_SIZE, TILE_SIZE, 48 * TILE_SIZE, groundY - 12 * TILE_SIZE, 1.2 * Zoom.SCALE, blockTexture));
            tiles.add(new MovingPlatform(52 * TILE_SIZE, groundY - 6 * TILE_SIZE, TILE_SIZE, TILE_SIZE, 52 * TILE_SIZE, groundY - 14 * TILE_SIZE, 1.1 * Zoom.SCALE, blockTexture));

            // 4) Dense stepping stones on the fortress slopes (increase level density)
            for (int i = 0; i < 5; i++) {
                if (i == 1) continue;
                tiles.add(new Tile((47 + i) * TILE_SIZE, groundY - ((i % 5) + 4) * TILE_SIZE, TILE_SIZE, TILE_SIZE, blockTexture));
            }

            // 5) Top of fortress and flag
            tiles.add(new Tile(55 * TILE_SIZE, groundY - 15 * TILE_SIZE, TILE_SIZE, TILE_SIZE, blockTexture));
            tiles.add(new Tile(56 * TILE_SIZE, groundY - 15 * TILE_SIZE, TILE_SIZE, TILE_SIZE, blockTexture)); // flag base

            level.enemyPositions = new int[][]{{13 * TILE_SIZE, groundY - 2 * TILE_SIZE}, {49 * TILE_SIZE, groundY - 6 * TILE_SIZE}};
            level.endX = 56 * TILE_SIZE;
            level.endY = groundY - 16 * TILE_SIZE;
        }

        // -------
        // Level 8
        // -------
        else if (index == 7) {
            // 1) Entferne Boden in einem breiten mittleren Bereich (unpassierbar)
            tiles.removeIf(t -> {
                int tx = t.getX() / TILE_SIZE;
                return tx >= 14 && tx <= 38;
            });

            // 2) Basis-Cluster links
            tiles.add(new Tile(10 * TILE_SIZE, groundY - TILE_SIZE, TILE_SIZE, TILE_SIZE, blockTexture));
            tiles.add(new Tile(11 * TILE_SIZE, groundY - 2 * TILE_SIZE, TILE_SIZE, TILE_SIZE, blockTexture));

            tiles.add(new MovingPlatform(13 * TILE_SIZE, groundY - 3 * TILE_SIZE, TILE_SIZE, TILE_SIZE, 18 * TILE_SIZE, groundY - 3 * TILE_SIZE, 1.6 * Zoom.SCALE, blockTexture));

            // 3) Vertikale Fahrstühle (essenziell) — zwei Etagen hoch
            tiles.add(new MovingPlatform(20 * TILE_SIZE, groundY - 2 * TILE_SIZE, TILE_SIZE, TILE_SIZE, 20 * TILE_SIZE, groundY - 8 * TILE_SIZE, Zoom.SCALE, blockTexture)); // elevator A
            tiles.add(new MovingPlatform(24 * TILE_SIZE, groundY - 3 * TILE_SIZE, TILE_SIZE, TILE_SIZE, 24 * TILE_SIZE, groundY - 10 * TILE_SIZE, 1.1 * Zoom.SCALE, blockTexture)); // elevator B

            // 4) Horizontal movers zwischen Etagen (Chain required)
            tiles.add(new MovingPlatform(22 * TILE_SIZE, groundY - 6 * TILE_SIZE, TILE_SIZE, TILE_SIZE, 26 * TILE_SIZE, groundY - 6 * TILE_SIZE, 1.6 * Zoom.SCALE, blockTexture));
            tiles.add(new MovingPlatform(26 * TILE_SIZE, groundY - 7 * TILE_SIZE, TILE_SIZE, TILE_SIZE, 22 * TILE_SIZE, groundY - 7 * TILE_SIZE, 1.6 * Zoom.SCALE, blockTexture));

            // 5) Obere Plattformen + finale Flagge (hoch)
            tiles.add(new Tile(27 * TILE_SIZE, groundY - 12 * TILE_SIZE, TILE_SIZE, TILE_SIZE, blockTexture));
            tiles.add(new Tile(27 * TILE_SIZE + TILE_SIZE, groundY - 12 * TILE_SIZE, TILE_SIZE, TILE_SIZE, blockTexture)); // flag base

            level.enemyPositions = new int[][]{{11 * TILE_SIZE, groundY - 4 * TILE_SIZE}, {23 * TILE_SIZE, groundY - 6 * TILE_SIZE}, {25 * TILE_SIZE, groundY - 3 * TILE_SIZE}};
            level.endX = 27 * TILE_SIZE + TILE_SIZE;
            level.endY = groundY - 13 * TILE_SIZE;
        }

        // Save tiles & return
        level.solidTiles = tiles;
        return level;
    }

    public boolean isEndReached(Player p) {
        // Flaggen-Hitbox skaliert
        Rectangle flagRect = new Rectangle(endX, (int) (endY - (150 * Zoom.SCALE)), (int) (40 * Zoom.SCALE), (int) (200 * Zoom.SCALE));
        return p.getBounds().intersects(flagRect);
    }

    public void draw(Graphics2D g, int camX) {
        // Hintergrund (Parallax-Effekt und Hintergrund-Wiederholung)
        if (background != null) {
            // Skalierte Breite und Höhe des Hintergrundbildes
            int bgWidth = (int) (background.getWidth() * Zoom.SCALE);
            int bgHeight = (int) (background.getHeight() * Zoom.SCALE);

            // 1. Berechne die Parallax-Verschiebung
            int parallaxShift = camX / 2;

            // 2. Berechne den Offset (damit die Kachelung bei 0 anfängt)
            // (Wir nutzen hier das Negativ, da die Kamera nach rechts wandert)
            int offset = -(parallaxShift % bgWidth);

            // 3. Schleife, die von links nach rechts zeichnet
            for (int x = offset; x < GamePanel.WIDTH; x += bgWidth) {

                // Zeichne das Bild an der Offset-Position (x),
                // die bereits die Kachel-Wiederholung und die Parallax-Verschiebung enthält.
                g.drawImage(background, x, 0, bgWidth, bgHeight, null);
            }
        } else {
            g.setColor(new Color(135, 206, 250));
            g.fillRect(0, 0, width, height);
        }


        // Boden
        for (Tile t : solidTiles) {
            t.draw(g, camX);
        }

        // Flagge: Skalieren aller Dimensionen
        int flagOffsetX = endX - camX;
        int poleBaseWidth = (int) (30 * Zoom.SCALE);
        int poleBaseHeight = (int) (10 * Zoom.SCALE);
        int poleMastWidth = (int) (5 * Zoom.SCALE);
        int poleMastHeight = (int) (60 * Zoom.SCALE);
        int flagWidth = (int) (20 * Zoom.SCALE);
        int flagHeight = (int) (15 * Zoom.SCALE);

        // Basis
        g.setColor(new Color(50, 50, 50));
        g.fillRect((int) (flagOffsetX - (10 * Zoom.SCALE)), endY + TILE_SIZE - poleBaseHeight, poleBaseWidth, poleBaseHeight);

        // Mast
        g.setColor(new Color(200, 200, 200));
        g.fillRect((int) (flagOffsetX + (3 * Zoom.SCALE)), endY + TILE_SIZE - poleBaseHeight - poleMastHeight, poleMastWidth, poleMastHeight);

        // Flagge
        g.setColor(Color.RED);
        g.fillRect((int) (flagOffsetX + (7 * Zoom.SCALE)), endY + TILE_SIZE - poleBaseHeight - poleMastHeight, flagWidth, flagHeight);
        g.setColor(Color.BLACK);
        g.drawRect((int) (flagOffsetX + (7 * Zoom.SCALE)), endY + TILE_SIZE - poleBaseHeight - poleMastHeight, flagWidth, flagHeight);
    }

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