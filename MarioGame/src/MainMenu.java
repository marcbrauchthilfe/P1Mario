import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainMenu {

    private Rectangle startButton = new Rectangle(320, 200, 180, 60);
    private Rectangle controlsButton = new Rectangle(310, 280, 200, 60);
    private Rectangle levelSelectButton = new Rectangle(300, 360, 250, 60);
    private Rectangle quitButton = new Rectangle(320, 440, 200, 60);

    private GamePanel gamePanel;

    private boolean showControls = false;

    public MainMenu(GamePanel panel) {
        this.gamePanel = panel;

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int mx = e.getX();
                int my = e.getY();

                if (!showControls) {
                    if (startButton.contains(mx, my)) {
                        gamePanel.setGameState(GameState.START_LEVEL);
                    } else if (controlsButton.contains(mx, my)) {
                        showControls = true;
                    } else if (levelSelectButton.contains(mx, my)) {
                        gamePanel.setshowLevelSelection(true);
                    } else if (quitButton.contains(mx, my)) {
                        System.exit(0);
                    }
                } else {
                    // Klick irgendwo → zurück zum Menü
                    showControls = false;
                }
            }
        });
    }

    public void draw(Graphics2D g) {
        // Hintergrund
        g.setColor(Color.DARK_GRAY);
        g.fillRect(0, 0, gamePanel.getWidth(), gamePanel.getHeight());

        int panelWidth = gamePanel.getWidth();
        int panelHeight = gamePanel.getHeight();

        // Titel
        g.setFont(new Font("Arial", Font.BOLD, 48));
        String title = "Game Menü";
        FontMetrics fm = g.getFontMetrics();
        int titleX = (panelWidth - fm.stringWidth(title)) / 2;
        g.setColor(Color.WHITE);
        g.drawString(title, titleX, 150);

        // Buttons
        g.setFont(new Font("Arial", Font.PLAIN, 32));

        // Button-Liste
        Rectangle[] buttons = {startButton, levelSelectButton, controlsButton, quitButton};
        String[] labels = {"Start", "Level auswählen", "Steuerung", "Beenden"};

        int buttonWidth = 250;
        int buttonHeight = 50;
        int spacing = 20; // Abstand zwischen Buttons

        // Gesamthöhe der Button-Gruppe
        int totalHeight = buttons.length * buttonHeight + (buttons.length - 1) * spacing;

        // Start-Y für die erste Button-Reihe
        int startY = (panelHeight - totalHeight) / 2 + 50; // +50 nach unten verschoben, unter Titel

        for (int i = 0; i < buttons.length; i++) {
            int x = (panelWidth - buttonWidth) / 2;
            int y = startY + i * (buttonHeight + spacing);

            buttons[i].setBounds(x, y, buttonWidth, buttonHeight);

            // Button Hintergrund
            g.setColor(Color.LIGHT_GRAY);
            g.fill(buttons[i]);

            // Button Rahmen
            g.setColor(Color.BLACK);
            g.draw(buttons[i]);

            // Button-Text zentrieren
            String text = labels[i];
            FontMetrics fmBtn = g.getFontMetrics();
            int textX = x + (buttonWidth - fmBtn.stringWidth(text)) / 2;
            int textY = y + ((buttonHeight - fmBtn.getHeight()) / 2) + fmBtn.getAscent();
            g.drawString(text, textX, textY);
        }
    }


    private void drawButtons(Graphics2D g) {
        g.setColor(Color.LIGHT_GRAY);
        g.fill(startButton);
        g.fill(controlsButton);
        g.fill(levelSelectButton);
        g.fill(quitButton);

        g.setColor(Color.BLACK);
        g.draw(startButton);
        g.draw(controlsButton);
        g.draw(levelSelectButton);
        g.draw(quitButton);

        g.setFont(new Font("Arial", Font.PLAIN, 28));
        g.drawString("Start", startButton.x + 60, startButton.y + 40);
        g.drawString("Steuerung", controlsButton.x + 30, controlsButton.y + 40);
        g.drawString("Level auswählen", levelSelectButton.x + 10, levelSelectButton.y + 40);
        g.drawString("Beenden", quitButton.x + 50, quitButton.y + 40);
    }

    private void drawControls(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 24));
        g.drawString("Steuerung:", 50, 100);
        g.drawString("Links: Pfeil LINKS / A", 50, 150);
        g.drawString("Rechts: Pfeil RECHTS / D", 50, 180);
        g.drawString("Springen: LEERTASTE / W", 50, 210);
        g.drawString("Level starten / weiter: ENTER", 50, 240);
        g.drawString("Zurück ins Menü: ESC", 50, 270);
        g.drawString("Klicken, um zurückzugehen", 50, 330);
    }

    // -------- Getter für GamePanel-MausListener --------
    public Rectangle getStartButton() { return startButton; }
    public Rectangle getQuitButton() { return quitButton; }
    public Rectangle getControlsButton() { return controlsButton; }
    public Rectangle getLevelSelectButton() { return levelSelectButton; }
}
