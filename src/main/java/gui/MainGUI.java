package gui;

import util.Session;
import javax.swing.*;
import java.awt.*;

public class MainGUI extends JFrame {
    
    private JButton btnQLDonHang;
    private JButton btnQLKho;
    private JButton btnQLNhanVien;
    
    public MainGUI() {
        initComponents();
        applyAuthorization(); // Áp dụng phân quyền sau khi khởi tạo form
    }

    private void initComponents() {
        setTitle("Hệ thống AgriSupplyChain - " + (Session.isLogged() ? Session.currentUser.getUsername() : "Chưa đăng nhập"));
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new FlowLayout(FlowLayout.CENTER, 20, 50));

        btnQLDonHang = new JButton("Quản lý Đơn hàng");
        btnQLKho = new JButton("Quản lý Kho");
        btnQLNhanVien = new JButton("Quản lý Nhân viên (Admin)");

        btnQLDonHang.setPreferredSize(new Dimension(200, 50));
        btnQLKho.setPreferredSize(new Dimension(200, 50));
        btnQLNhanVien.setPreferredSize(new Dimension(250, 50));

        add(btnQLDonHang);
        add(btnQLKho);
        add(btnQLNhanVien);
    }

    /**
     * Phương thức phân quyền hiển thị dựa trên Role (loaiTK)
     * Giả định:
     * 1 = Admin (Toàn quyền)
     * 2 = Nhân viên Kho (Chỉ thấy Quản lý Kho)
     * 3 = Nhân viên Bán hàng (Chỉ thấy Quản lý Đơn hàng)
     */
    private void applyAuthorization() {
        if (!Session.isLogged()) return;

        int role = Session.currentUser.getLoaiTK();

        // Ẩn tất cả theo mặc định (hoặc vô hiệu hóa setEnabled(false))
        btnQLDonHang.setVisible(false);
        btnQLKho.setVisible(false);
        btnQLNhanVien.setVisible(false);

        // Phân quyền bật lên
        if (role == 1) { // Admin
            btnQLDonHang.setVisible(true);
            btnQLKho.setVisible(true);
            btnQLNhanVien.setVisible(true);
        } else if (role == 2) { // Nhân viên Kho
            btnQLKho.setVisible(true);
        } else if (role == 3) { // Nhân viên Bán hàng
            btnQLDonHang.setVisible(true);
        }
    }
}
