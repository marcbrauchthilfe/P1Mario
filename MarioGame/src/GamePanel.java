import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class GamePanel extends JPanel implements java.awt.event.ActionListener {
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;
    private static int lives = 3;
    private static State state = State.START;
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

        timer = new Timer(16, this); // ~60 FPS
        setupKeyBindings();

        loadLevel(currentLevelIndex);
    }

    public static void subLive() {
        lives--;
        if (lives <= 0) state = State.GAME_OVER;
    }

    public void startGame() {
        timer.start();
    }

    private void loadLevel(int index) {
        level = Level.createSampleLevel(index);
        player = new Player(Level.TILE_SIZE, Level.groundY - Level.TILE_SIZE - Player.PLAYER_HEIGHT, level); // start slightly above ground
        player.loadSprite("res/player.png");
        enemies = new ArrayList<>();
        for (int[] p : level.getEnemyPositions()) {
            enemies.add(new Enemy(p[0], p[1], level));
        }
        state = State.START;
    }

    private void setupKeyBindings() {
        InputMap im = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getActionMap();

        // LEFT / A
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

        // RIGHT / D
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

        // JUMP: SPACE / UP / W
        im.put(KeyStroke.getKeyStroke("pressed SPACE"), "jump_pressed");
        im.put(KeyStroke.getKeyStroke("pressed UP"), "jump_pressed");
        im.put(KeyStroke.getKeyStroke("pressed W"), "jump_pressed");
        im.put(KeyStroke.getKeyStroke("released SPACE"), "jump_released");
        im.put(KeyStroke.getKeyStroke("released UP"), "jump_released");
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

        // ENTER: start / next
        im.put(KeyStroke.getKeyStroke("pressed ENTER"), "enter_pressed");
        am.put("enter_pressed", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (state == State.START) state = State.RUNNING;
                else if (state == State.LEVEL_COMPLETE) {
                    currentLevelIndex++;
                    if (currentLevelIndex >= Level.NUM_LEVELS) {
                        state = State.GAME_OVER;
                    } else {
                        loadLevel(currentLevelIndex);
                        state = State.START;
                    }
                }
            }
        });

        // R: restart after game over
        im.put(KeyStroke.getKeyStroke("pressed R"), "r_pressed");
        am.put("r_pressed", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (state == State.GAME_OVER) {
                    score = 0;
                    lives = 3;
                    currentLevelIndex = 0;
                    loadLevel(currentLevelIndex);
                    state = State.START;
                }
            }
        });

        // ESC: exit game
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "ESC_pressed");
        am.put("ESC_pressed", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        double dt = timer.getDelay() / 1000.0;

        if (state == State.RUNNING) {
            if (left && !right) player.moveLeft();
            else if (right && !left) player.moveRight();
            else player.stopHorizontal();

            player.update(dt);
            for (Enemy en : new ArrayList<>(enemies)) en.update();

            // enemy-player collisions
            for (Enemy en : new ArrayList<>(enemies)) {
                if (player.getBounds().intersects(en.getBounds())) {
                    if (player.isFalling() && player.getY() < en.getY()) {
                        enemies.remove(en);
                        score += 100;
                        player.bounceAfterStomp();
                    } else {
                        lives--;
                        if (lives <= 0) state = State.GAME_OVER;
                        else player.respawn();
                    }
                }
            }

            if (level.isEndReached(player)) {
                state = State.LEVEL_COMPLETE;
            }
        }

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        int camX = 0;
        if (player != null) {
            int px = player.getBounds().x;
            camX = px - WIDTH / 2;
            if (camX < 0) camX = 0;
            if (camX > level.getWidth() - WIDTH) camX = level.getWidth() - WIDTH;
        }

        level.draw(g2, camX);

        if (player != null) player.draw(g2, camX);
        for (Enemy en : new ArrayList<>(enemies)) en.draw(g2, camX);

        // HUD
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("SansSerif", Font.BOLD, 18));
        g2.drawString("Score: " + score, 10, 20);
        g2.drawString("Lives: " + lives, 10, 40);
        g2.drawString("Level: " + (currentLevelIndex + 1), 700, 20);

        if (state == State.START) {
            drawCenteredString(g2, "PRESS ENTER TO START", getWidth(), getHeight());
            drawCenteredString(g2, "Controls: Arrow keys or WASD to move, SPACE/W to jump", getWidth(), getHeight() - 50);
        } else if (state == State.GAME_OVER) {
            drawCenteredString(g2, "GAME OVER - Press R to restart", getWidth(), getHeight());
        } else if (state == State.LEVEL_COMPLETE) {
            drawCenteredString(g2, "LEVEL COMPLETE - Press ENTER to continue", getWidth(), getHeight());
        }
    }

    private void drawCenteredString(Graphics2D g, String text, int w, int h) {
        FontMetrics fm = g.getFontMetrics();
        int x = (w - fm.stringWidth(text)) / 2;
        int y = (h - fm.getHeight()) / 2 + fm.getAscent();
        g.setColor(Color.WHITE);
        g.fillRect(x - 10, y - fm.getAscent() - 5, fm.stringWidth(text) + 20, fm.getHeight() + 10);
        g.setColor(Color.BLACK);
        g.drawString(text, x, y);
    }

    private enum State {START, RUNNING, GAME_OVER, LEVEL_COMPLETE}
}
