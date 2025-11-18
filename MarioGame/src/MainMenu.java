import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainMenu {

    private Rectangle startButton = new Rectangle(350, 250, 200, 60);
    private Rectangle quitButton = new Rectangle(350, 350, 200, 60);

    private GamePanel gamePanel;

    public MainMenu(GamePanel panel) {
        this.gamePanel = panel;

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int mx = e.getX();
                int my = e.getY();

                if (startButton.contains(mx, my)) {
                    // Spiel vom Menü in den Start-Screen wechseln
                    gamePanel.setGameState(GameState.START_LEVEL);
                }

                if (quitButton.contains(mx, my)) {
                    System.exit(0);
                }
            }
        });
    }

    public void draw(Graphics2D g) {
        g.setColor(Color.DARK_GRAY);
        g.fillRect(0, 0, gamePanel.getWidth(), gamePanel.getHeight());

        g.setFont(new Font("Arial", Font.BOLD, 50));
        g.setColor(Color.WHITE);
        g.drawString("MEIN PLATFORMER", 230, 150);

        // Start Button
        g.setColor(Color.LIGHT_GRAY);
        g.fill(startButton);
        g.setColor(Color.BLACK);
        g.draw(startButton);
        g.drawString("Start", startButton.x + 50, startButton.y + 45);

        // Quit Button
        g.setColor(Color.LIGHT_GRAY);
        g.fill(quitButton);
        g.setColor(Color.BLACK);
        g.draw(quitButton);
        g.drawString("Beenden", quitButton.x + 10, quitButton.y + 45);
    }
}
