package test;

import gui.LoginGUI;
import javax.swing.SwingUtilities;

public class TestMainLogin {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginGUI().setVisible(true);
            }
        });
    }
}
