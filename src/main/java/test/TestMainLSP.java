package test;

import javax.swing.*;

import gui.panel.LoaiSanPhamPanel;

public class TestMainLSP {
    public static void main(String[] args) {
        // Chạy GUI trên Event Dispatch Thread để đảm bảo an toàn luồng (Thread-safe)
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Test - Quản lý Loại Sản Phẩm");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1000, 600); // Kích thước mô phỏng vùng hiển thị (phần 3)
            frame.setLocationRelativeTo(null); // Căn giữa màn hình
            
            // Đưa Panel LoaiSanPhamGUI vào Frame
            frame.add(new LoaiSanPhamPanel());
            frame.setVisible(true);
        });
    }
}