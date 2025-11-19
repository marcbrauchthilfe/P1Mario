import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class GamePanel extends JPanel implements ActionListener {

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

    private MenuManager menuManager;

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.CYAN);
        setFocusable(true);

        timer = new Timer(16, this);

        // Menümanager initialisieren
        menuManager = new MenuManager(this);

        setupKeyBindings();
        setupMouseListener();

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                menuManager.handleMouseMove(e);
                repaint();
            }
        });
    }

    private void setupMouseListener() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int mx = e.getX();
                int my = e.getY();

                // Wenn wir im Menü sind, weiterleiten
                if (state == GameState.MENU) {
                    menuManager.handleClick(mx, my);
                    repaint();
                    return;
                }

                // LevelComplete Buttons
                if (state == GameState.LEVEL_COMPLETE) {
                    menuManager.handleClick(mx, my);
                    return;
                }
            }
        });
    }

    private void setupKeyBindings() {
        InputMap im = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getActionMap();

        // ENTER
        im.put(KeyStroke.getKeyStroke("pressed ENTER"), "enter_pressed");
        am.put("enter_pressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (state == GameState.MENU) {
                    lives = 3;
                    score = 0;
                    currentLevelIndex = 0;
                    loadLevel(currentLevelIndex);
                } else if (state == GameState.START_LEVEL) {
                    state = GameState.RUNNING;
                } else if (state == GameState.LEVEL_COMPLETE) {
                    nextLevel();
                }
            }
        });

        // ESCAPE
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "esc_pressed");
        am.put("esc_pressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (state == GameState.RUNNING || state == GameState.START_LEVEL
                        || state == GameState.LEVEL_COMPLETE || state == GameState.GAME_OVER) {
                    goToMenu();
                } else if (state == GameState.MENU) {
                    menuManager.closeCurrentMenu();
                }
            }
        });

        // RESTART
        im.put(KeyStroke.getKeyStroke("pressed R"), "r_pressed");
        am.put("r_pressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (state == GameState.GAME_OVER) {
                    lives = 3;
                    score = 0;
                    currentLevelIndex = 0;
                    state = GameState.MENU;
                    menuManager.resetMenus();
                    repaint();
                }
            }
        });

        // Bewegung
        bindMovement(im, am);
    }

    private void bindMovement(InputMap im, ActionMap am) {
        // Links
        im.put(KeyStroke.getKeyStroke("pressed LEFT"), "left_pressed");
        im.put(KeyStroke.getKeyStroke("released LEFT"), "left_released");
        im.put(KeyStroke.getKeyStroke("pressed A"), "left_pressed");
        im.put(KeyStroke.getKeyStroke("released A"), "left_released");
        am.put("left_pressed", new AbstractAction() { public void actionPerformed(ActionEvent e) { left = true; } });
        am.put("left_released", new AbstractAction() { public void actionPerformed(ActionEvent e) { left = false; } });

        // Rechts
        im.put(KeyStroke.getKeyStroke("pressed RIGHT"), "right_pressed");
        im.put(KeyStroke.getKeyStroke("released RIGHT"), "right_released");
        im.put(KeyStroke.getKeyStroke("pressed D"), "right_pressed");
        im.put(KeyStroke.getKeyStroke("released D"), "right_released");
        am.put("right_pressed", new AbstractAction() { public void actionPerformed(ActionEvent e) { right = true; } });
        am.put("right_released", new AbstractAction() { public void actionPerformed(ActionEvent e) { right = false; } });

        // Springen
        im.put(KeyStroke.getKeyStroke("pressed SPACE"), "jump_pressed");
        im.put(KeyStroke.getKeyStroke("released SPACE"), "jump_released");
        im.put(KeyStroke.getKeyStroke("pressed W"), "jump_pressed");
        im.put(KeyStroke.getKeyStroke("released W"), "jump_released");
        am.put("jump_pressed", new AbstractAction() { public void actionPerformed(ActionEvent e) { if (player != null) player.pressJump(); } });
        am.put("jump_released", new AbstractAction() { public void actionPerformed(ActionEvent e) { if (player != null) player.releaseJump(); } });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        double dt = timer.getDelay() / 1000.0;

        if (state == GameState.RUNNING) {
            if (left && !right) player.moveLeft();
            else if (right && !left) player.moveRight();
            else player.stopHorizontal();

            // Moving Platforms
            for (Tile t : level.getSolidTiles()) {
                if (t instanceof MovingPlatform mp) mp.update(dt);
            }

            player.update(dt);

            for (Enemy en : new ArrayList<>(enemies)) en.update();

            // Kollision Spieler <-> Gegner
            for (Enemy en : new ArrayList<>(enemies)) {
                if (player.getBounds().intersects(en.getBounds())) {
                    if (player.isFalling() && player.getY() < en.getY()) {
                        enemies.remove(en);
                        score += 100;
                        player.bounceAfterStomp();
                    } else {
                        lives--;
                        if (lives <= 0) state = GameState.GAME_OVER;
                        else player.respawn();
                    }
                }
            }

            if (level.isEndReached(player)) state = GameState.LEVEL_COMPLETE;
        }

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        if (state == GameState.MENU) {
            menuManager.draw(g2);
            return;
        }

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

        if (state == GameState.START_LEVEL) drawCenteredString(g2, "PRESS ENTER TO START LEVEL");
        else if (state == GameState.LEVEL_COMPLETE) drawCenteredString(g2, "LEVEL COMPLETE! Press ENTER");
        else if (state == GameState.GAME_OVER) drawCenteredString(g2, "GAME OVER - Press R to restart");
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
        int x = (WIDTH - fm.stringWidth(text)) / 2;
        int y = (HEIGHT - fm.getHeight()) / 2 + fm.getAscent();
        g.setColor(Color.WHITE);
        g.fillRect(x - 10, y - fm.getAscent() - 5, fm.stringWidth(text) + 20, fm.getHeight() + 10);
        g.setColor(Color.BLACK);
        g.drawString(text, x, y);
    }

    private void loadLevel(int index) {
        level = Level.createSampleLevel(index);
        int startX = Level.TILE_SIZE;
        int startY = 550 - Player.PLAYER_HEIGHT;

        player = new Player(startX, startY, level);
        player.loadSprite("res/player.png");

        enemies = new ArrayList<>();
        for (int[] p : level.getEnemyPositions()) enemies.add(new Enemy(p[0], p[1], level));

        state = GameState.START_LEVEL;
    }

    private void nextLevel() {
        currentLevelIndex++;
        if (currentLevelIndex >= Level.NUM_LEVELS) state = GameState.GAME_OVER;
        else loadLevel(currentLevelIndex);
    }

    private void goToMenu() {
        state = GameState.MENU;
        player = null;
        level = null;
        enemies = new ArrayList<>();
        menuManager.resetMenus();
        timer.stop();
        repaint();
    }

    public void startGame() {
        timer.start();
    }

    public void setGameState(GameState newState) {
        this.state = newState;
        if (newState == GameState.START_LEVEL && level == null) loadLevel(currentLevelIndex);
        if (newState == GameState.MENU) timer.stop();
    }

    public int getCurrentLevelIndex() {
        return currentLevelIndex;
    }

    public void setCurrentLevelIndex(int index) {
        this.currentLevelIndex = index;
    }

    public void startLevel(int index) {
        this.currentLevelIndex = index;
        loadLevel(index);
        state = GameState.START_LEVEL;
    }

    public static void subLive() {
        lives--;
    }

}
