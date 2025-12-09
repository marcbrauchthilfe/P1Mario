package main;

import javax.swing.*;

public class Game {

    public static void createAndShowGUI() {
        JFrame frame = new JFrame("Der Ritter der verlorenen Seelen");
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
