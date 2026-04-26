package test;

import gui.DangKyGUI;
import javax.swing.SwingUtilities;

public class TestMainKH {
    public static void main(String[] args) {
        // Sử dụng SwingUtilities.invokeLater để đảm bảo an toàn cho luồng giao diện (Thread-safe)
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Khởi tạo và hiển thị cửa sổ đăng ký
                DangKyGUI frame = new DangKyGUI();
                frame.setVisible(true);
                
                // Bạn có thể thêm tiêu đề riêng cho bản test nếu muốn
                frame.setTitle("Test Hệ Thống: Đăng ký Khách hàng");
            }
        });
    }
}