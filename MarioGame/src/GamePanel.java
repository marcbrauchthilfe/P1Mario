import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
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

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.CYAN);
        setFocusable(true);

        timer = new Timer(16, this);
        setupKeyBindings();

        // Starte im MENU
        state = GameState.MENU;
    }

    public static void subLive() {
        lives--;
    }

    public void startGame() {
        timer.start();
    }

    private void loadLevel(int index) {
        level = Level.createSampleLevel(index + 2);

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

        // ESC → EXIT
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "esc_pressed");
        am.put("esc_pressed", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
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

        // KAMERA
        int camX = 0;
        if (player != null) {
            int px = player.getBounds().x;
            camX = px - WIDTH / 2;
            if (camX < 0) camX = 0;
            if (camX > level.getWidth() - WIDTH) camX = level.getWidth() - WIDTH;
        }

        // RENDER MENÜ
        if (state == GameState.MENU) {
            drawMainMenu(g2);
            return;
        }

        // LEVEL / SPIEL
        level.draw(g2, camX);

        if (player != null) player.draw(g2, camX);
        for (Enemy en : enemies) en.draw(g2, camX);

        drawHUD(g2);

        if (state == GameState.START_LEVEL) {
            drawCenteredString(g2, "PRESS ENTER TO START LEVEL");
        } else if (state == GameState.LEVEL_COMPLETE) {
            drawCenteredString(g2, "LEVEL COMPLETE! PRESS ENTER");
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
        state = newState;

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
}
