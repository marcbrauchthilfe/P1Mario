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
    private final MenuManager menuManager;
    private int score = 0;
    private int currentLevelIndex = 0;
    private boolean left, right;
    private Player player;
    private ArrayList<Enemy> enemies;
    private Level level;
    private final int[] levelScores;  // Score pro Level
    private int totalScore;     // Gesamt-Score


    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.CYAN);
        setFocusable(true);

        levelScores = new int[Level.NUM_LEVELS];
        totalScore = 0;


        timer = new Timer(16, this);

        menuManager = new MenuManager(this);

        setupKeyBindings();
        setupMouse();
    }

    public static void subLive() {
        lives--;
    }

    private void setupMouse() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                menuManager.handleMousePressed(e.getX(), e.getY());
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                menuManager.setMousePosition(e.getX(), e.getY());
                repaint();
            }
        });
    }

    private void setupKeyBindings() {
        InputMap im = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getActionMap();

        im.put(KeyStroke.getKeyStroke("pressed ENTER"), "enter_pressed");
        am.put("enter_pressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                menuManager.handleEnter();
            }
        });

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "esc_pressed");
        am.put("esc_pressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                menuManager.handleEscape();
            }
        });

        // Links/Rechts Bewegung
        im.put(KeyStroke.getKeyStroke("pressed LEFT"), "left_pressed");
        im.put(KeyStroke.getKeyStroke("released LEFT"), "left_released");
        im.put(KeyStroke.getKeyStroke("pressed A"), "left_pressed");
        im.put(KeyStroke.getKeyStroke("released A"), "left_released");

        im.put(KeyStroke.getKeyStroke("pressed RIGHT"), "right_pressed");
        im.put(KeyStroke.getKeyStroke("released RIGHT"), "right_released");
        im.put(KeyStroke.getKeyStroke("pressed D"), "right_pressed");
        im.put(KeyStroke.getKeyStroke("released D"), "right_released");

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

        // Springen
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

    public void startGame() {
        timer.start();
    }

    public void loadSelectedLevel(int index) {
        currentLevelIndex = index;
        level = Level.createSampleLevel(index);

        // Spieler mittig auf Boden spawnen
        int startX = Level.TILE_SIZE;
        int startY = 550 - Player.PLAYER_HEIGHT;

        player = new Player(startX, startY, level);
        player.loadSprite("res/player.png");

        enemies = new ArrayList<>();
        for (int[] p : level.getEnemyPositions()) {
            enemies.add(new Enemy(p[0], p[1], level));
        }

        state = GameState.START_LEVEL;
        if (!timer.isRunning()) timer.start();
    }

    public int getCurrentLevelIndex() {
        return currentLevelIndex;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        double dt = timer.getDelay() / 1000.0;

        if (state == GameState.RUNNING && player != null) {
            if (left && !right) player.moveLeft();
            else if (right && !left) player.moveRight();
            else player.stopHorizontal();

            for (Tile t : level.getSolidTiles()) {
                if (t instanceof MovingPlatform mp) mp.update(dt);
            }

            player.update(dt);

            for (Enemy en : new ArrayList<>(enemies)) en.update();

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
            levelScores[currentLevelIndex] = score;  // Score für das aktuelle Level sichern
            totalScore = 0;
            for (int s : levelScores) totalScore += s;
        }

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        if (state == GameState.RUNNING || state == GameState.START_LEVEL || state == GameState.LEVEL_COMPLETE || state == GameState.GAME_OVER) {

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
        }

        // Menü zeichnen
        menuManager.draw(g2);
    }

    private void drawHUD(Graphics2D g2) {
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("SansSerif", Font.BOLD, 18));
        g2.drawString("Score: " + score, 10, 20);
        g2.drawString("Lives: " + lives, 10, 40);
        g2.drawString("Level: " + (currentLevelIndex + 1), 700, 20);
    }

    public void setGameState(GameState newState) {
        state = newState;

        if (state == GameState.MENU || state == GameState.LEVEL_SELECTION || state == GameState.CONTROLS_MENU) {
            if (timer.isRunning()) timer.stop();
        } else if (state == GameState.START_LEVEL || state == GameState.RUNNING) {
            if (!timer.isRunning()) timer.start();
        }
    }

    public void restartLevel() {
        lives = 3;
        score = 0;
        loadSelectedLevel(currentLevelIndex);
        state = GameState.START_LEVEL;
    }

    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_R -> restartLevel();
            case KeyEvent.VK_ESCAPE -> menuManager.handleEscape();
            case KeyEvent.VK_ENTER -> menuManager.handleEnter();
        }
    }

    public int getTotalScore() {
        return totalScore;
    }
}
