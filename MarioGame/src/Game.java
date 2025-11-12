import javax.swing.JFrame;

public class Game {
    private static JFrame frame;

    public static void createAndShowGUI() {
        frame = new JFrame("Platformer — Mario-Style (Simple)");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        GamePanel panel = new GamePanel();
        frame.setContentPane(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        panel.startGame();
    }
}
