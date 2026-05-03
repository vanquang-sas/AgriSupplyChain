package test;

import javax.swing.SwingUtilities;
import gui.khohang.MainFrame;

public class TestNhapKho {
    public static void main(String[] args) {
        // Chạy giao diện MainFrame
        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}