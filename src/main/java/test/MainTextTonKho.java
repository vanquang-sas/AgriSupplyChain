/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package test;

import com.formdev.flatlaf.FlatLightLaf;

import gui.panel.TonKhoPanel;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.BorderLayout;

public class MainTextTonKho extends JFrame {

    public MainTextTonKho() {
        setTitle("Quản Lý Kho - Agri App");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        TonKhoPanel tonKhoPanel = new TonKhoPanel();
        add(tonKhoPanel, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            UIManager.put("defaultFont", new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 13));
        } catch (Exception ex) {
            System.err.println("Không thể khởi tạo FlatLaf");
        }

        SwingUtilities.invokeLater(() -> {
            new MainTextTonKho().setVisible(true);
        });
    }
}
