import java.awt.*;

public class ControlsMenu {

    private final GamePanel gamePanel;

    public ControlsMenu(GamePanel panel) {
        this.gamePanel = panel;
    }

    public void draw(Graphics2D g) {
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
}
