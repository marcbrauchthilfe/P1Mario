import java.awt.*;

public class MainMenu {

    private GamePanel gamePanel;

    private Rectangle startBtn, levelSelectBtn, controlsBtn, quitBtn;

    public MainMenu(GamePanel panel) {
        this.gamePanel = panel;

        startBtn = new Rectangle(300, 200, 200, 50);
        levelSelectBtn = new Rectangle(300, 270, 200, 50);
        controlsBtn = new Rectangle(300, 340, 200, 50);
        quitBtn = new Rectangle(300, 410, 200, 50);
    }

    public void draw(Graphics2D g) {
        g.setColor(Color.DARK_GRAY);
        g.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);

        g.setFont(new Font("Arial", Font.BOLD, 48));
        g.setColor(Color.WHITE);
        g.drawString("SUPER JUMP GAME", 200, 100);

        g.setFont(new Font("Arial", Font.PLAIN, 28));
        drawButton(g, startBtn, "Start");
        drawButton(g, levelSelectBtn, "Level Auswahl");
        drawButton(g, controlsBtn, "Steuerung");
        drawButton(g, quitBtn, "Beenden");
    }

    private void drawButton(Graphics2D g, Rectangle rect, String text) {
        g.setColor(Color.LIGHT_GRAY);
        g.fill(rect);
        g.setColor(Color.BLACK);
        g.draw(rect);

        FontMetrics fm = g.getFontMetrics();
        int x = rect.x + (rect.width - fm.stringWidth(text)) / 2;
        int y = rect.y + (rect.height + fm.getAscent()) / 2 - 4;
        g.drawString(text, x, y);
    }

    public Rectangle getStartButton() { return startBtn; }
    public Rectangle getLevelSelectButton() { return levelSelectBtn; }
    public Rectangle getControlsButton() { return controlsBtn; }
    public Rectangle getQuitButton() { return quitBtn; }
}
