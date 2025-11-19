import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class LevelMenu {

    private GamePanel gamePanel;
    private List<Rectangle> levelButtons;
    private int hoveredIndex = -1; // Index des Buttons, über dem die Maus ist

    public LevelMenu(GamePanel panel) {
        this.gamePanel = panel;
        levelButtons = new ArrayList<>();
        createButtons();
    }

    private void createButtons() {
        int columns = 4;
        int buttonWidth = 160;
        int buttonHeight = 60;
        int gap = 20;

        int totalLevels = Level.NUM_LEVELS;
        int rows = (int) Math.ceil(totalLevels / (double) columns);

        int gridWidth = columns * (buttonWidth + gap) - gap;
        int startX = GamePanel.WIDTH / 2 - gridWidth / 2;
        int startY = 150;

        int index = 0;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                if (index >= totalLevels) break;
                int x = startX + col * (buttonWidth + gap);
                int y = startY + row * (buttonHeight + gap);
                levelButtons.add(new Rectangle(x, y, buttonWidth, buttonHeight));
                index++;
            }
        }
    }

    public void draw(Graphics2D g) {
        g.setColor(Color.DARK_GRAY);
        g.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);

        g.setFont(new Font("Arial", Font.BOLD, 36));
        g.setColor(Color.WHITE);
        g.drawString("Level auswählen", GamePanel.WIDTH / 2 - 150, 100);

        g.setFont(new Font("Arial", Font.PLAIN, 24));
        for (int i = 0; i < levelButtons.size(); i++) {
            Rectangle btn = levelButtons.get(i);

            // Hover-Effekt
            if (i == hoveredIndex) {
                g.setColor(Color.ORANGE);
            } else {
                g.setColor(Color.LIGHT_GRAY);
            }
            g.fill(btn);

            g.setColor(Color.BLACK);
            g.draw(btn);

            String text = "Level " + (i + 1);
            FontMetrics fm = g.getFontMetrics();
            int textX = btn.x + (btn.width - fm.stringWidth(text)) / 2;
            int textY = btn.y + (btn.height + fm.getAscent()) / 2 - 4;
            g.drawString(text, textX, textY);
        }
    }

    public void handleMouseMove(MouseEvent e) {
        int mx = e.getX();
        int my = e.getY();

        hoveredIndex = -1; // Reset
        for (int i = 0; i < levelButtons.size(); i++) {
            if (levelButtons.get(i).contains(mx, my)) {
                hoveredIndex = i;
                break;
            }
        }
    }

    public List<Rectangle> getLevelButtons() {
        return levelButtons;
    }

    public int getHoveredIndex() {
        return hoveredIndex;
    }
}
