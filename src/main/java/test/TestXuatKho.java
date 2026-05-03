package test;

import gui.khohang.XuatKhoPanel;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.Color;

public class TestXuatKho extends JFrame {

    public TestXuatKho() {
        // Thiết lập tiêu đề cho cửa sổ test
        setTitle("Hệ thống quản lý kho - Kiểm thử Module Xuất kho");
        setSize(1100, 700); // Tăng kích thước để hiển thị đủ bảng và phân trang
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(245, 245, 245));

        // Thêm Panel Xuất kho đã thiết kế vào khung hình
        // Đảm bảo bạn đã import đúng package gui.khohang
        XuatKhoPanel xuatKhoPanel = new XuatKhoPanel();
        add(xuatKhoPanel);
    }

    public static void main(String[] args) {
        // Sử dụng Look and Feel của hệ thống để đồng bộ với NhapKhoPanel
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        // Chạy ứng dụng
        SwingUtilities.invokeLater(() -> {
            new TestXuatKho().setVisible(true);
        });
    }
}