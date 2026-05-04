package test;

import gui.panel.DangNhapPanel;
import javax.swing.SwingUtilities;

public class TestMainLogin {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new DangNhapPanel().setVisible(true);
            }
        });
    }
}
