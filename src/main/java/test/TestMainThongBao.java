/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package test;

import com.formdev.flatlaf.FlatLightLaf;
import gui.panel.ThongBaoWidget;
import util.AppColor;

import javax.swing.*;
import java.awt.*;

public class TestMainThongBao {
    public static void main(String[] args) {
        // Set giao diện FlatLaf
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // Tạo khung giả lập
        JFrame frame = new JFrame("Test Tính Năng Thông Báo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 700);
        frame.getContentPane().setBackground(AppColor.BACKGROUND);

        // 1. Tạo thanh Header giả lập (giống Header của MainFrame thực tế)
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 30, 10));
        headerPanel.setBackground(AppColor.SURFACE); // Nền trắng
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, AppColor.BORDER)); // Viền mờ bên dưới

        // 2. Gắn cái chuông vào góc phải của Header
        ThongBaoWidget thongBaoWidget = new ThongBaoWidget();
        headerPanel.add(thongBaoWidget);

        frame.add(headerPanel, BorderLayout.NORTH);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
