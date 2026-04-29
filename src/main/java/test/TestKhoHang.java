package test;

import gui.khohang.Component.NhapKhoMain; 

public class TestKhoHang {
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            NhapKhoMain frame = new NhapKhoMain();
            frame.setLocationRelativeTo(null); 
            frame.setVisible(true);
        });
    }
}