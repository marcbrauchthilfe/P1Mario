package ui;

import main.GamePanel;

import java.awt.*;

public class LoadingScreens {

    private long startTime;
    private final long duration = 1200; // 1.2 Sekunden Loading

    public void start() {
        startTime = System.currentTimeMillis();
    }

    public boolean isFinished() {
        return System.currentTimeMillis() - startTime >= duration;
    }

    public void draw(Graphics2D g) {

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 40));

        String text = "Loading...";
        int w = g.getFontMetrics().stringWidth(text);
        g.drawString(text, (GamePanel.WIDTH - w) / 2, 250);

        // Optional: kleiner Balken
        int barWidth = 300;
        int barHeight = 20;
        int x = (GamePanel.WIDTH - barWidth) / 2;
        int y = 300;

        // Fortschritt berechnen
        double progress = (System.currentTimeMillis() - startTime) / (double) duration;
        if (progress > 1) progress = 1;

        g.setColor(Color.GRAY);
        g.fillRect(x, y, barWidth, barHeight);

        g.setColor(Color.GREEN);
        g.fillRect(x, y, (int) (barWidth * progress), barHeight);
    }
}
