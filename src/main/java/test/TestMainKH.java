package test;

import bus.KhachHangBUS;

public class TestMainKH {
    public static void main(String[] args) {
        System.out.println("--- BẮT ĐẦU TEST MODULE KHÁCH HÀNG ---");

        // 1. Giả lập việc người dùng nhập liệu trên giao diện DangKyGUI
        String username = "AGRIAPP";
        String password = "123456";
        String confirmPassword = "123456";
        String ten = "Nguyễn Văn A";
        String sdt = "0901234567";
        String email = "nva@gmail.com";
        String diaChi = "123 Đường ABC, Quận X, TP Y";

        // 2. Khởi tạo BUS và truyền dữ liệu xuống để xử lý (Validate, Hashing, gọi DAO)
        KhachHangBUS bus = new KhachHangBUS();
        
        System.out.println("Đang gửi yêu cầu đăng ký...");
        String ketQua = bus.dangKyKhachHang(username, password, confirmPassword, ten, sdt, email, diaChi);

        // 3. In kết quả phản hồi (Giả lập việc hiển thị JOptionPane lên màn hình trong DangKyGUI)
        System.out.println("Kết quả trả về từ hệ thống: " + ketQua);
        
        // 4. Test thêm một trường hợp cố tình nhập sai xác nhận mật khẩu để kiểm tra Validate
        System.out.println("\n--- Đang gửi yêu cầu đăng ký (Sai mật khẩu xác nhận) ---");
        String ketQuaLoi = bus.dangKyKhachHang("khachhang_test2", "pass123", "pass456", "Trần Thị B", "0911222333", "ttb@gmail.com", "456 Đường DEF");
        System.out.println("Kết quả trả về từ hệ thống: " + ketQuaLoi);

        System.out.println("--- KẾT THÚC TEST ---");
    }
}
/*
đọc code ở nhánh feature/dang-ky, nếu bạn không thể hãy nói không thể (nhánh feature/dang-ky có file KhachHangDAO.java hãy kiểm tra xem bạn có nhìn thấy file này hay không, nếu không thấy tức là bạn đang ở nhánh dev). viết lại hàm main test , sử dụng những cái đã được cài đặt trong KhachHangDAO.java KhachHangBUS.java DangKyGUI.java
*/