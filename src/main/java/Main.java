import gui.AuthFrame;
import gui.MainFrame;
import util.Session;

import javax.swing.UIManager;

import dto.TaiKhoanDTO;

public class Main {
    public static void main(String[] args) {
        // Áp dụng LookAndFeel của hệ điều hành cho giao diện đẹp hơn
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // ==========================================
        // CHẾ ĐỘ TEST: TỰ ĐỘNG ĐĂNG NHẬP
        // ==========================================
        TaiKhoanDTO mockUser = new TaiKhoanDTO();
        mockUser.setUsername("nvtm01");

        mockUser.setLoaiTK(0); // 0: Quản lý, 1: Nhân viên, 2: Khách hàng
        
        Session.currentUser = mockUser;
        Session.chucVu = "giao hàng";
        Session.tenNguoiDung = "Quản trị viên Test";

        // Khởi chạy màn hình Đăng nhập tổng
        java.awt.EventQueue.invokeLater(() -> {
            // Mở thẳng MainFrame với quyền Admin
            new MainFrame().setVisible(true);

            // Khi nào muốn deploy thật, hãy comment dòng trên và uncomment dòng dưới:
            // new AuthFrame().setVisible(true);
        });
    }
}