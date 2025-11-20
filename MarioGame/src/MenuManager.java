import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MenuManager {

    private final GamePanel game;
    private int mouseX, mouseY;

    private final List<Rectangle> levelButtons = new ArrayList<>();
    private final Rectangle startBtn;
    private final Rectangle controlsBtn;
    private final Rectangle levelSelectBtn;
    private final Rectangle quitBtn;
    private final Rectangle continueBtn;
    private Rectangle menuBtn;
    private final Rectangle restartBtn;

    public MenuManager(GamePanel game) {
        this.game = game;

        // MainMenu Buttons
        startBtn = new Rectangle(300, 200, 200, 50);
        controlsBtn = new Rectangle(300, 270, 200, 50);
        levelSelectBtn = new Rectangle(300, 340, 200, 50);
        quitBtn = new Rectangle(300, 410, 200, 50);

        // Buttons
        List<Rectangle> mainMenuButtons = new ArrayList<>();
        mainMenuButtons.add(startBtn);
        mainMenuButtons.add(controlsBtn);
        mainMenuButtons.add(levelSelectBtn);
        mainMenuButtons.add(quitBtn);

        // LevelComplete Buttons
        continueBtn = new Rectangle(200, 400, 150, 40);
        menuBtn = new Rectangle(450, 400, 150, 40);

        // GameOver Buttons
        restartBtn = new Rectangle(300, 250, 200, 50);
        menuBtn = new Rectangle(300, 350, 200, 50);
    }

    public void setMousePosition(int x, int y) {
        mouseX = x;
        mouseY = y;
    }

    public void handleMousePressed(int mx, int my) {

        if (GamePanel.state == GameState.MENU) {
            // Hauptmenü
            if (startBtn.contains(mx, my)) {
                game.loadSelectedLevel(game.getCurrentLevelIndex());
            } else if (controlsBtn.contains(mx, my)) {
                game.setGameState(GameState.CONTROLS_MENU);
            } else if (levelSelectBtn.contains(mx, my)) {
                game.setGameState(GameState.LEVEL_SELECTION);
            } else if (quitBtn.contains(mx, my)) {
                System.exit(0);
            }
        } else if (GamePanel.state == GameState.LEVEL_SELECTION) {
            for (int i = 0; i < levelButtons.size(); i++) {
                if (levelButtons.get(i).contains(mx, my)) {
                    game.loadSelectedLevel(i);
                    return;
                }
            }
        } else if (GamePanel.state == GameState.LEVEL_COMPLETE) {
            if (continueBtn.contains(mx, my)) {
                int next = game.getCurrentLevelIndex() + 1;
                if (next >= Level.NUM_LEVELS) {
                    game.setGameState(GameState.GAME_OVER);
                } else {
                    game.loadSelectedLevel(next);
                }
            } else if (menuBtn.contains(mx, my)) {
                game.setGameState(GameState.MENU);
            }
        } else if (GamePanel.state == GameState.GAME_OVER) {
            if (restartBtn.contains(mx, my)) {
                game.restartLevel();  // definiere diese Methode im GamePanel
            } else if (menuBtn.contains(mx, my)) {
                game.setGameState(GameState.MENU);
            }
        }
    }

    public void handleEnter() {
        if (GamePanel.state == GameState.START_LEVEL) {
            game.setGameState(GameState.RUNNING);
        }
    }

    public void handleEscape() {
        if (GamePanel.state == GameState.RUNNING || GamePanel.state == GameState.START_LEVEL || GamePanel.state == GameState.LEVEL_COMPLETE || GamePanel.state == GameState.GAME_OVER || GamePanel.state == GameState.LEVEL_SELECTION || GamePanel.state == GameState.CONTROLS_MENU) {
            game.setGameState(GameState.MENU);
        }
    }

    public void draw(Graphics2D g) {
        if (GamePanel.state == GameState.MENU) {
            drawMainMenu(g);
        } else if (GamePanel.state == GameState.LEVEL_SELECTION) {
            drawLevelSelection(g);
        } else if (GamePanel.state == GameState.CONTROLS_MENU) {
            drawControlsMenu(g);
        } else if (GamePanel.state == GameState.LEVEL_COMPLETE) {
            drawLevelComplete(g);
        } else if (GamePanel.state == GameState.GAME_OVER) {
            drawGameOver(g);
        } else if (GamePanel.state == GameState.START_LEVEL) {
            drawStartLevel(g);
        }
    }

    private void drawButton(Graphics2D g, Rectangle rect, String text) {
        if (rect.contains(mouseX, mouseY)) g.setColor(Color.ORANGE);
        else g.setColor(Color.LIGHT_GRAY);
        g.fill(rect);

        g.setColor(Color.BLACK);
        g.draw(rect);

        FontMetrics fm = g.getFontMetrics();
        int textX = rect.x + (rect.width - fm.stringWidth(text)) / 2;
        int textY = rect.y + (rect.height + fm.getAscent()) / 2 - 4;
        g.drawString(text, textX, textY);
    }

    private void drawMainMenu(Graphics2D g) {
        g.setColor(Color.DARK_GRAY);
        g.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);

        g.setFont(new Font("Arial", Font.BOLD, 48));
        String title = "SUPER JUMP GAME";
        FontMetrics fm = g.getFontMetrics();
        int titleX = (GamePanel.WIDTH - fm.stringWidth(title)) / 2;
        g.setColor(Color.WHITE);
        g.drawString(title, titleX, 100);

        g.setFont(new Font("Arial", Font.PLAIN, 32));
        drawButton(g, startBtn, "Start");
        drawButton(g, controlsBtn, "Controls");
        drawButton(g, levelSelectBtn, "Level Select");
        drawButton(g, quitBtn, "Quit");

        g.setFont(new Font("SansSerif", Font.BOLD, 20));
        g.setColor(Color.YELLOW);
        g.drawString("Total Score: " + game.getTotalScore(), 10, 50);
    }

    private void drawLevelSelection(Graphics2D g) {
        g.setColor(Color.DARK_GRAY);
        g.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 36));
        g.drawString("Level auswählen", 220, 100);

        levelButtons.clear();

        int columns = 4;
        int buttonWidth = 160;
        int buttonHeight = 60;
        int gap = 20;

        int totalLevels = Level.NUM_LEVELS;
        int rows = (int) Math.ceil(totalLevels / (double) columns);

        int gridWidth = columns * (buttonWidth + gap) - gap;
        int startX = GamePanel.WIDTH / 2 - gridWidth / 2;
        int startY = 180;

        g.setFont(new Font("Arial", Font.PLAIN, 24));

        int index = 0;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                if (index >= totalLevels) break;
                int x = startX + col * (buttonWidth + gap);
                int y = startY + row * (buttonHeight + gap);

                Rectangle btn = new Rectangle(x, y, buttonWidth, buttonHeight);
                levelButtons.add(btn);

                drawButton(g, btn, "Level " + (index + 1));
                index++;
            }
        }
    }

    private void drawControlsMenu(Graphics2D g) {
        g.setColor(Color.DARK_GRAY);
        g.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);

        g.setFont(new Font("Arial", Font.PLAIN, 28));
        g.setColor(Color.WHITE);

        String[] lines = {"Steuerung:", "Links/Rechts: Pfeiltasten oder A/D", "Springen: Leertaste oder W", "Menü zurück: Escape", "Level starten/weiter: Enter", "Spiel neu starten: R"};

        int totalHeight = lines.length * 40;
        int startY = (GamePanel.HEIGHT - totalHeight) / 2;

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            int x = (GamePanel.WIDTH - g.getFontMetrics().stringWidth(line)) / 2;
            int y = startY + i * 40;
            g.drawString(line, x, y);
        }

        String back = "Drücke ESC um zurückzugehen";
        int backX = (GamePanel.WIDTH - g.getFontMetrics().stringWidth(back)) / 2;
        g.drawString(back, backX, startY + lines.length * 40 + 20);
    }

    private void drawLevelComplete(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 48));
        g.drawString("LEVEL COMPLETE", 200, 200);

        g.setFont(new Font("Arial", Font.PLAIN, 32));
        drawButton(g, continueBtn, "Continue");
        drawButton(g, menuBtn, "Menu");
    }

    private void drawGameOver(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);

        // Titel
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 48));
        String title = "GAME OVER";
        FontMetrics fm = g.getFontMetrics();
        int titleX = (GamePanel.WIDTH - fm.stringWidth(title)) / 2;
        g.drawString(title, titleX, 100);

        // Total Score
        g.setFont(new Font("SansSerif", Font.BOLD, 24));
        g.setColor(Color.ORANGE);
        g.drawString("Total Score: " + game.getTotalScore(), 10, 50);

        // Buttons
        g.setFont(new Font("Arial", Font.PLAIN, 32));
        drawButton(g, restartBtn, "Restart ");
        drawButton(g, menuBtn, "Main Menu ");
    }

    private void drawStartLevel(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 36));
        String msg = "PRESS ENTER TO START LEVEL";
        int x = (GamePanel.WIDTH - g.getFontMetrics().stringWidth(msg)) / 2;
        int y = GamePanel.HEIGHT / 2;
        g.setColor(Color.WHITE);
        g.fillRect(x - 10, y - 30, g.getFontMetrics().stringWidth(msg) + 20, 40);
        g.setColor(Color.BLACK);
        g.drawString(msg, x, y);
    }
}
