import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class MenuManager {

    private GamePanel gamePanel;

    private MainMenu mainMenu;
    private LevelMenu levelMenu;
    private ControlsMenu controlsMenu;

    private enum ActiveMenu { MAIN, LEVELS, CONTROLS, NONE }
    private ActiveMenu activeMenu;

    public MenuManager(GamePanel panel) {
        this.gamePanel = panel;

        mainMenu = new MainMenu(panel);
        levelMenu = new LevelMenu(panel);
        controlsMenu = new ControlsMenu(panel);

        activeMenu = ActiveMenu.MAIN;
    }

    public void draw(Graphics2D g2) {
        switch (activeMenu) {
            case MAIN -> mainMenu.draw(g2);
            case LEVELS -> levelMenu.draw(g2);
            case CONTROLS -> controlsMenu.draw(g2);
            case NONE -> {} // Nichts zeichnen
        }
    }

    public void handleClick(int mx, int my) {
        switch (activeMenu) {
            case MAIN -> {
                if (mainMenu.getStartButton().contains(mx, my)) {
                    gamePanel.startLevel(gamePanel.getCurrentLevelIndex());
                    gamePanel.state = GameState.START_LEVEL;
                } else if (mainMenu.getLevelSelectButton().contains(mx, my)) {
                    activeMenu = ActiveMenu.LEVELS;
                } else if (mainMenu.getControlsButton().contains(mx, my)) {
                    activeMenu = ActiveMenu.CONTROLS;
                } else if (mainMenu.getQuitButton().contains(mx, my)) {
                    System.exit(0);
                }
            }
            case LEVELS -> {
                List<Rectangle> buttons = levelMenu.getLevelButtons();
                for (int i = 0; i < buttons.size(); i++) {
                    if (buttons.get(i).contains(mx, my)) {
                        gamePanel.startLevel(i);
                        gamePanel.state = GameState.START_LEVEL;
                        activeMenu = ActiveMenu.MAIN;
                        return;
                    }
                }
            }
            case CONTROLS -> {
                // Klick irgendwo → zurück zum Hauptmenü
                activeMenu = ActiveMenu.MAIN;
            }
        }
    }

    public void closeCurrentMenu() {
        // ESC → zurück zum Hauptmenü
        if (activeMenu != ActiveMenu.MAIN) activeMenu = ActiveMenu.MAIN;
    }

    public void resetMenus() {
        activeMenu = ActiveMenu.MAIN;
    }

    public void handleMouseMove(MouseEvent e) {
        if (activeMenu == ActiveMenu.LEVELS) {
            levelMenu.handleMouseMove(e);
        }
    }

}
