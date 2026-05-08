package test;

import gui.CuaHangGUI;

import javax.swing.*;

public class TestCuaHangGUI {

    public static void main(String[] args) {

        JFrame frame = new JFrame("Giao Hàng");

        frame.setContentPane(new CuaHangGUI());

        frame.setSize(1050, 700);

        frame.setLocationRelativeTo(null);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setVisible(true);
    }
}