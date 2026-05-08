package test;

import gui.GiaoHangGUI;

import javax.swing.*;

public class TestGiaoHangGUI {

    public static void main(String[] args) {

        JFrame frame = new JFrame("Giao Hàng");

        frame.setContentPane(new GiaoHangGUI());

        frame.setSize(1000, 500);

        frame.setLocationRelativeTo(null);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setVisible(true);
    }
}