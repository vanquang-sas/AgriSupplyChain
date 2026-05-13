package test;

import javax.swing.*;

import gui.panel.CuaHangPanel;

public class TestCuaHangGUI {

    public static void main(String[] args) {

        JFrame frame = new JFrame("Giao Hàng");

        String maKH = "KH000001";
        frame.setContentPane(new CuaHangPanel(maKH));

        frame.setSize(1050, 700);

        frame.setLocationRelativeTo(null);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setVisible(true);
    }
}