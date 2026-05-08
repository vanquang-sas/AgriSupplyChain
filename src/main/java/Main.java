import gui.panel.LoHangPanel;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            JFrame frame = new JFrame("Test LoHangPanel");

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            frame.setSize(1200, 700);

            frame.setLocationRelativeTo(null);

            // Gắn JPanel vào JFrame
            frame.setContentPane(new LoHangPanel());

            frame.setVisible(true);
        });
    }
}