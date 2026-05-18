package test;

import dto.GioHangDTO;
import dto.TaiKhoanDTO;
import gui.MainFrame;
import util.Session;

import javax.swing.*;
import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Lớp Test độc lập để kiểm tra tính năng Giỏ Hàng và Thanh Toán
 * Không cần đăng nhập, tự động mock (giả lập) dữ liệu giỏ hàng.
 */
public class TestGioHangThanhToan {
    public static void main(String[] args) {
        // 1. Giả lập Look & Feel hệ thống cho đẹp
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 2. Giả lập phiên đăng nhập (Session) của Khách hàng
        TaiKhoanDTO dummyUser = new TaiKhoanDTO();
        dummyUser.setUsername("KhachHang_Test");
        dummyUser.setLoaiTK(2); // Role 2: Khách hàng (Sẽ hiển thị menu Mua sắm & Giỏ hàng)
        Session.currentUser = dummyUser;
        Session.chucVu = "Khách Hàng";

        // 3. Giả lập dữ liệu trong Giỏ hàng (cartCache)
        Session.cartCache = new ArrayList<>();

        GioHangDTO sp1 = new GioHangDTO();
        sp1.setMaSP("SPTEST01");
        sp1.setTenSP("Cà chua Đà Lạt (Mock Data)");
        sp1.setDonGia(new BigDecimal("25000"));
        sp1.setSoLuong(2);
        // XÓA HOẶC COMMENT DÒNG setThanhTien: 
        // sp1.setThanhTien(new BigDecimal("50000"));

        GioHangDTO sp2 = new GioHangDTO();
        sp2.setMaSP("SPTEST02");
        sp2.setTenSP("Xà lách thủy canh (Mock Data)");
        sp2.setDonGia(new BigDecimal("30000"));
        sp2.setSoLuong(1);
        // XÓA HOẶC COMMENT DÒNG setThanhTien:
        // sp2.setThanhTien(new BigDecimal("30000"));

        Session.cartCache.add(sp1);
        Session.cartCache.add(sp2);

        // 4. Khởi chạy Giao diện chính (MainFrame)
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);

            // 5. Tự động chuyển thẳng đến tab Giỏ Hàng để test ngay
            try {
                // Gọi hàm navigateToGioHang() mà chúng ta đã thêm ở MainFrame
                frame.navigateToGioHang();
            } catch (Exception e) {
                System.err.println("Lỗi điều hướng. Nếu bạn chưa thêm hàm navigateToGioHang() vào MainFrame, hãy tự click chuột vào Sidebar để sang trang Giỏ Hàng.");
            }
        });
    }
}