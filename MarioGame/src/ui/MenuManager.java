package ui;

import main.GamePanel;
import main.Storage;
import utils.GameState;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MenuManager {

    private final GamePanel game;
    private final List<Rectangle> levelButtons = new ArrayList<>();
    private final Rectangle startBtn;
    private final Rectangle controlsBtn;
    private final Rectangle levelSelectBtn;
    private final Rectangle quitBtn;
    private final Rectangle continueBtn;
    private final Rectangle restartBtn;
    private final Rectangle menuBtn;
    private int mouseX, mouseY;
    Storage storage;

    public MenuManager(GamePanel game) {
        this.game = game;
        storage = new Storage();

        // MainMenu Buttons
        startBtn = new Rectangle(300, 200, 200, 50);
        controlsBtn = new Rectangle(300, 270, 200, 50);
        levelSelectBtn = new Rectangle(300, 340, 200, 50);
        quitBtn = new Rectangle(300, 410, 200, 50);

        // LevelComplete Button
        continueBtn = new Rectangle(300, 250, 200, 50);

        // GameOver Button
        restartBtn = new Rectangle(300, 250, 200, 50);

        // Menu Button
        menuBtn = new Rectangle(300, 350, 200, 50);
    }

    public void setMousePosition(int x, int y) {
        mouseX = x;
        mouseY = y;
    }

    public void handleMousePressed(int mx, int my) {

        if (GamePanel.state == GameState.MENU_SCREEN) {
            GamePanel.setlives(3);
            if (startBtn.contains(mx, my)) {

                game.loadSelectedLevel(game.getCurrentLevelIndex());
                game.showLoadingThen(GameState.RUNNING);
            } else if (controlsBtn.contains(mx, my)) {
                game.showLoadingThen(GameState.CONTROLS_MENU_SCREEN);
            } else if (levelSelectBtn.contains(mx, my)) {
                game.showLoadingThen(GameState.LEVEL_SELECTION_SCREEN);
            } else if (quitBtn.contains(mx, my)) {
                System.exit(0);
            }
        } else if (GamePanel.state == GameState.LEVEL_SELECTION_SCREEN) {
            GamePanel.setlives(3);
            for (int i = 0; i < levelButtons.size(); i++) {
                if (levelButtons.get(i).contains(mx, my)) {
                    game.loadSelectedLevel(i);
                    game.showLoadingThen(GameState.RUNNING);
                    return;
                }
            }
        } else if (GamePanel.state == GameState.LEVEL_COMPLETE_SCREEN) {
            GamePanel.setlives(3);
            if (continueBtn.contains(mx, my)) {
                int next = game.getCurrentLevelIndex() + 1;
                if (next >= storage.getTotalNumberOfLevels()) {
                    game.showLoadingThen(GameState.GAME_OVER_SCREEN);
                    game.setCurrentScore(0);
                } else {
                    game.loadSelectedLevel(next);
                    game.showLoadingThen(GameState.RUNNING);
                }
            } else if (menuBtn.contains(mx, my)) {
                game.showLoadingThen(GameState.MENU_SCREEN);
                game.setCurrentScore(0);
            }
        } else if (GamePanel.state == GameState.GAME_OVER_SCREEN) {
            GamePanel.setlives(3);
            if (restartBtn.contains(mx, my)) {
                game.restartLevel();  // definiere diese Methode im main.GamePanel
            } else if (menuBtn.contains(mx, my)) {
                game.showLoadingThen(GameState.MENU_SCREEN);
            }
        }
    }

    /*
    public void handleEnter() {
        if (GamePanel.state == GameState.START_LEVEL_SCREEN) {
            game.showLoadingThen(GameState.RUNNING);
        }
    }
     */

    public void handleEscape() {
        if (GamePanel.state == GameState.RUNNING || GamePanel.state == GameState.START_LEVEL_SCREEN || GamePanel.state == GameState.LEVEL_COMPLETE_SCREEN || GamePanel.state == GameState.GAME_OVER_SCREEN || GamePanel.state == GameState.LEVEL_SELECTION_SCREEN || GamePanel.state == GameState.CONTROLS_MENU_SCREEN) {
            game.showLoadingThen(GameState.MENU_SCREEN);
        }
    }

    public void draw(Graphics2D g) {
        if (GamePanel.state == GameState.MENU_SCREEN) {
            drawMainMenu(g);
        } else if (GamePanel.state == GameState.LEVEL_SELECTION_SCREEN) {
            drawLevelSelection(g);
        } else if (GamePanel.state == GameState.CONTROLS_MENU_SCREEN) {
            drawControlsMenu(g);
        } else if (GamePanel.state == GameState.LEVEL_COMPLETE_SCREEN) {
            drawLevelComplete(g);
        } else if (GamePanel.state == GameState.GAME_OVER_SCREEN) {
            drawGameOver(g);
        } else if (GamePanel.state == GameState.START_LEVEL_SCREEN) {
            game.showLoadingThen(GameState.RUNNING);
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
        String title = "Der Ritter der verlorenen Seelen";
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
        g.drawString("Total Score: " + storage.getTotalScore(), 10, 50);
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

        int totalLevels = storage.getTotalNumberOfLevels();
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

        String[] lines = {"Steuerung:", "Links/Rechts: Pfeiltasten oder A/D", "Springen: Leertaste oder W",};

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

        g.setColor(Color.YELLOW);
        g.setFont(new Font("Arial", Font.PLAIN, 24));
        g.drawString("YOUR SCORE WAS: " + game.getCurrentScore() + "!", 250, 225);

        g.setFont(new Font("Arial", Font.PLAIN, 32));
        drawButton(g, continueBtn, "Continue");
        drawButton(g, menuBtn, "Main Menu");
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
        g.drawString("Total Score: " + Storage.getTotalScore(), 10, 50);

        // Buttons
        g.setFont(new Font("Arial", Font.PLAIN, 32));
        drawButton(g, restartBtn, "Restart ");
        drawButton(g, menuBtn, "Main Menu ");
    }
}
