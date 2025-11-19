import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class GamePanel extends JPanel implements java.awt.event.ActionListener {

    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;

    public static int lives = 3;
    public static GameState state = GameState.MENU;
    private final Timer timer;

    private boolean left, right;

    private Player player;
    private ArrayList<Enemy> enemies;
    private Level level;

    private int currentLevelIndex = 0;
    private int score = 0;

    private MainMenu mainMenu;
    private boolean showLevelSelection,showControlsMenu;

    private Rectangle continueBtn, menuBtn;
    private ArrayList<Rectangle> levelSelectButtons = new ArrayList<>();

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.CYAN);
        setFocusable(true);

        timer = new Timer(16, this);
        setupKeyBindings();

        mainMenu = new MainMenu(this);

        // Level Complete Buttons
        continueBtn = new Rectangle(200, 400, 150, 40);
        menuBtn = new Rectangle(450, 400, 150, 40);

        // Level-Auswahl Buttons
        for (int i = 0; i < Level.NUM_LEVELS; i++) {
            levelSelectButtons.add(new Rectangle(300, 180 + i * 80, 200, 50));
        }

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int mx = e.getX();
                int my = e.getY();

                // --- LEVEL COMPLETE ---
                if (state == GameState.LEVEL_COMPLETE) {
                    if (continueBtn.contains(mx, my)) {
                        currentLevelIndex++;
                        if (currentLevelIndex >= Level.NUM_LEVELS) {
                            state = GameState.GAME_OVER;
                        } else {
                            loadLevel(currentLevelIndex);
                            state = GameState.START_LEVEL;
                        }
                    } else if (menuBtn.contains(mx, my)) {
                        state = GameState.MENU;
                    }
                    return; // nur LevelComplete Buttons abfangen
                }

                // --- MENU ---
                if (state == GameState.MENU) {
                    if (showLevelSelection) {
                        // Level-Auswahl Buttons
                        for (int i = 0; i < levelSelectButtons.size(); i++) {
                            if (levelSelectButtons.get(i).contains(mx, my)) {
                                loadLevel(i);
                                state = GameState.START_LEVEL;
                                showLevelSelection = false;
                                return;
                            }
                        }
                    } else if (showControlsMenu) {
                        // Klick irgendwo → zurück zum Menü
                        showControlsMenu = false;
                        return;
                    } else {
                        // MainMenu Buttons
                        Rectangle startBtn = mainMenu.getStartButton();
                        Rectangle controlsBtn = mainMenu.getControlsButton();
                        Rectangle levelSelectBtn = mainMenu.getLevelSelectButton();
                        Rectangle quitBtn = mainMenu.getQuitButton();

                        if (startBtn.contains(mx, my)) {
                            loadLevel(currentLevelIndex);
                            state = GameState.START_LEVEL;
                        } else if (controlsBtn.contains(mx, my)) {
                            showControlsMenu = true;
                        } else if (levelSelectBtn.contains(mx, my)) {
                            showLevelSelection = true;
                        } else if (quitBtn.contains(mx, my)) {
                            System.exit(0);
                        }
                        return;
                    }
                }
            }
        });
    }

    public static void subLive() {
        lives--;
        if (lives <= 0) {
            // Game over -> Menü nicht erlaubt
            // GamePanel kennt static nicht, also nichts ändern
        }
    }

    public void startGame() {
        timer.start();
    }

    private void loadLevel(int index) {
        level = Level.createSampleLevel(index);

        // Spieler mittig auf dem Boden spawnen
        int startX = Level.TILE_SIZE;
        int startY = 550 - Player.PLAYER_HEIGHT;

        player = new Player(startX, startY, level);
        player.loadSprite("res/player.png");

        enemies = new ArrayList<>();
        for (int[] p : level.getEnemyPositions()) {
            enemies.add(new Enemy(p[0], p[1], level));
        }

        state = GameState.START_LEVEL;
    }

    private void setupKeyBindings() {
        InputMap im = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getActionMap();

        // MENU TASTE
        im.put(KeyStroke.getKeyStroke("pressed ENTER"), "enter_pressed");
        am.put("enter_pressed", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {

                if (state == GameState.MENU) {
                    lives = 3;
                    score = 0;
                    currentLevelIndex = 0;
                    loadLevel(0);
                } else if (state == GameState.START_LEVEL) {
                    state = GameState.RUNNING;
                } else if (state == GameState.LEVEL_COMPLETE) {

                    currentLevelIndex++;

                    if (currentLevelIndex >= Level.NUM_LEVELS) {
                        state = GameState.GAME_OVER;
                    } else {
                        loadLevel(currentLevelIndex);
                        state = GameState.START_LEVEL;
                    }
                }
            }
        });

        // RESTART
        im.put(KeyStroke.getKeyStroke("pressed R"), "r_pressed");
        am.put("r_pressed", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (state == GameState.GAME_OVER) {
                    lives = 3;
                    score = 0;
                    currentLevelIndex = 0;
                    state = GameState.MENU;
                }
            }
        });

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "esc_pressed");
        am.put("esc_pressed", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                // Wenn wir gerade spielen oder LevelComplete/GameOver
                if (state == GameState.RUNNING || state == GameState.START_LEVEL
                        || state == GameState.LEVEL_COMPLETE || state == GameState.GAME_OVER) {

                    // Zurück ins Menü
                    state = GameState.MENU;

                    // Timer stoppen, damit das Spiel nicht weiterläuft
                    if (timer != null && timer.isRunning()) {
                        timer.stop();
                    }

                    // Level und Player-Objekte zurücksetzen (optional)
                    player = null;
                    level = null;
                    enemies = new ArrayList<>();
                }

                // Wenn Levelauswahl oder Steuerungsmenü offen → schließen
                if (showLevelSelection) {
                    showLevelSelection = false;
                    repaint();
                    return;
                }
                if (showControlsMenu) {
                    showControlsMenu = false;
                    repaint();
                    return;
                }

                // ESC während Spiel
                if (state == GameState.RUNNING || state == GameState.START_LEVEL
                        || state == GameState.LEVEL_COMPLETE || state == GameState.GAME_OVER) {

                    state = GameState.MENU;
                    showLevelSelection = false;
                    showControlsMenu = false;

                    if (timer != null && timer.isRunning()) timer.stop();
                    player = null;
                    level = null;
                    enemies = new ArrayList<>();

                    repaint(); // Menü sofort anzeigen
                }
            }
        });

        // LINKS
        im.put(KeyStroke.getKeyStroke("pressed LEFT"), "left_pressed");
        im.put(KeyStroke.getKeyStroke("released LEFT"), "left_released");
        im.put(KeyStroke.getKeyStroke("pressed A"), "left_pressed");
        im.put(KeyStroke.getKeyStroke("released A"), "left_released");

        am.put("left_pressed", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                left = true;
            }
        });
        am.put("left_released", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                left = false;
            }
        });

        // RECHTS
        im.put(KeyStroke.getKeyStroke("pressed RIGHT"), "right_pressed");
        im.put(KeyStroke.getKeyStroke("released RIGHT"), "right_released");
        im.put(KeyStroke.getKeyStroke("pressed D"), "right_pressed");
        im.put(KeyStroke.getKeyStroke("released D"), "right_released");

        am.put("right_pressed", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                right = true;
            }
        });
        am.put("right_released", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                right = false;
            }
        });

        // SPRUNG
        im.put(KeyStroke.getKeyStroke("pressed SPACE"), "jump_pressed");
        im.put(KeyStroke.getKeyStroke("released SPACE"), "jump_released");
        im.put(KeyStroke.getKeyStroke("pressed W"), "jump_pressed");
        im.put(KeyStroke.getKeyStroke("released W"), "jump_released");

        am.put("jump_pressed", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (player != null) player.pressJump();
            }
        });
        am.put("jump_released", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (player != null) player.releaseJump();
            }
        });
    }

    public void showLevelSelection(){
        showLevelSelection = true;
    }
    public void showControlsMenu(){
        showControlsMenu = true;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        double dt = timer.getDelay() / 1000.0;

        if (state == GameState.RUNNING) {

            if (left && !right) player.moveLeft();
            else if (right && !left) player.moveRight();
            else player.stopHorizontal();

            // --- Moving Platforms updaten ---
            for (Tile t : level.getSolidTiles()) {
                if (t instanceof MovingPlatform mp) {
                    mp.update(dt);
                }
            }

            player.update(dt);

            for (Enemy en : new ArrayList<>(enemies)) en.update();

            // Kollisionsprüfung
            for (Enemy en : new ArrayList<>(enemies)) {

                if (player.getBounds().intersects(en.getBounds())) {

                    if (player.isFalling() && player.getY() < en.getY()) {
                        enemies.remove(en);
                        score += 100;
                        player.bounceAfterStomp();
                    } else {
                        lives--;

                        if (lives <= 0) {
                            state = GameState.GAME_OVER;
                        } else {
                            player.respawn();
                        }
                    }
                }
            }

            if (level.isEndReached(player)) {
                state = GameState.LEVEL_COMPLETE;
            }
        }

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        int panelWidth = getWidth();
        int panelHeight = getHeight();

        if (state == GameState.MENU) {
            g2.setColor(Color.DARK_GRAY);
            g2.fillRect(0, 0, panelWidth, panelHeight);

            g2.setFont(new Font("Arial", Font.BOLD, 48));
            String title = "SUPER JUMP GAME";
            FontMetrics fm = g2.getFontMetrics();
            int titleX = (panelWidth - fm.stringWidth(title)) / 2;
            g2.setColor(Color.WHITE);
            g2.drawString(title, titleX, 100);

            if (showLevelSelection) {
                drawLevelSelection(g2);
            } else if (showControlsMenu) {
                drawControlsMenu(g2);
            } else {
                mainMenu.draw(g2); // Standard-Buttons zentral
            }

            return;
        }

        // ----------------
        // Level / Spiel
        // ----------------
        int camX = 0;
        if (player != null) {
            int px = player.getBounds().x;
            camX = px - WIDTH / 2;
            if (camX < 0) camX = 0;
            if (level != null && camX > level.getWidth() - WIDTH) camX = level.getWidth() - WIDTH;
        }

        if (level != null) level.draw(g2, camX);
        if (player != null) player.draw(g2, camX);
        if (enemies != null) for (Enemy en : enemies) en.draw(g2, camX);

        drawHUD(g2);

        if (state == GameState.START_LEVEL) {
            drawCenteredString(g2, "PRESS ENTER TO START LEVEL");
        } else if (state == GameState.LEVEL_COMPLETE) {
            drawMainMenu(g2);
        } else if (state == GameState.GAME_OVER) {
            drawCenteredString(g2, "GAME OVER - Press R to restart");
        }
    }

    private void drawMainMenu(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 48));
        g.drawString("SUPER JUMP GAME", 200, 150);

        g.setFont(new Font("Arial", Font.PLAIN, 32));
        g.drawString("Press ENTER to Start", 250, 300);
        g.drawString("Press ESC to Quit", 250, 360);
    }

    private void drawHUD(Graphics2D g2) {
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("SansSerif", Font.BOLD, 18));
        g2.drawString("Score: " + score, 10, 20);
        g2.drawString("Lives: " + lives, 10, 40);
        g2.drawString("Level: " + (currentLevelIndex + 1), 700, 20);
    }

    private void drawCenteredString(Graphics2D g, String text) {
        FontMetrics fm = g.getFontMetrics();
        int x = (GamePanel.WIDTH - fm.stringWidth(text)) / 2;
        int y = (GamePanel.HEIGHT - fm.getHeight()) / 2 + fm.getAscent();
        g.setColor(Color.WHITE);
        g.fillRect(x - 10, y - fm.getAscent() - 5, fm.stringWidth(text) + 20, fm.getHeight() + 10);
        g.setColor(Color.BLACK);
        g.drawString(text, x, y);
    }

    public void setGameState(GameState newState) {
        this.state = newState;

        // Verhalten beim Wechsel in bestimmte States:
        if (newState == GameState.START_LEVEL) {
            // Level laden, falls noch kein Level geladen ist
            if (level == null) {
                loadLevel(currentLevelIndex);
            }
            // Spiel-Timer starten (falls du das möchtest)
            if (timer != null && !timer.isRunning()) {
                timer.start();
            }
        } else if (newState == GameState.MENU) {
            // Zurück ins Menü: Timer anhalten
            if (timer != null && timer.isRunning()) {
                timer.stop();
            }
        } else if (newState == GameState.GAME_OVER) {
            // Bei Game Over den Timer stoppen
            if (timer != null && timer.isRunning()) {
                timer.stop();
            }
        }
    }

    private void drawControlsMenu(Graphics2D g2) {
        g2.setFont(new Font("Arial", Font.PLAIN, 28));
        g2.setColor(Color.WHITE);

        String[] lines = {
                "Steuerung:",
                "Links/Rechts: Pfeiltasten oder A/D",
                "Springen: Leertaste oder W",
                "Menü zurück: Escape",
                "Level starten/weiter: Enter",
                "Spiel neu starten: R"
        };

        int totalHeight = lines.length * 40; // Zeilenhöhe
        int startY = (getHeight() - totalHeight) / 2;

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            int x = (getWidth() - g2.getFontMetrics().stringWidth(line)) / 2;
            int y = startY + i * 40;
            g2.drawString(line, x, y);
        }

        // Hinweis zum Zurückgehen
        String back = "Klicke oder drücke ESC, um zurückzugehen";
        int backX = (getWidth() - g2.getFontMetrics().stringWidth(back)) / 2;
        g2.drawString(back, backX, startY + lines.length * 40 + 20);
    }

    private void drawLevelSelection(Graphics2D g) {
        g.setColor(Color.DARK_GRAY);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 36));
        g.drawString("Level auswählen", 250, 100);

        for (int i = 0; i < Level.NUM_LEVELS; i++) {
            Rectangle levelButton = new Rectangle(300, 180 + i*80, 200, 50);
            g.setColor(Color.LIGHT_GRAY);
            g.fill(levelButton);
            g.setColor(Color.BLACK);
            g.draw(levelButton);
            g.drawString("Level " + (i+1), levelButton.x + 60, levelButton.y + 35);
        }
    }
    public void setshowLevelSelection(boolean show) {
        this.showLevelSelection =  show;
    }
}
