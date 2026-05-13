package test;

import javax.swing.*;

import gui.panel.GiaoHangPanel;



public class TestGiaoHangGUI {

    public static void main(String[] args) {

        JFrame frame = new JFrame("Giao Hàng");

        // giả lập nhân viên đăng nhập
        String maNV = "NV000012";
        frame.setContentPane(new GiaoHangPanel(maNV));
        // frame.setContentPane(new GiaoHangPanel());

        frame.setSize(1000, 500);

        frame.setLocationRelativeTo(null);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setVisible(true);
    }
}