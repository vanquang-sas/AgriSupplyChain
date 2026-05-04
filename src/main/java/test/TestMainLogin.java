package test;

import gui.DangNhapGUI;
import javax.swing.SwingUtilities;

public class TestMainLogin {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new DangNhapGUI().setVisible(true);
            }
        });
    }
}
