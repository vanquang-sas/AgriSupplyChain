package gui;

import bus.KhachHangBUS;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DangKyGUI extends JFrame {
    private JTextField txtUsername, txtTen, txtSDT, txtEmail, txtDiaChi;
    private JPasswordField txtPassword, txtConfirmPassword;
    private JButton btnDangKy;

    public DangKyGUI() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Đăng ký tài khoản Khách hàng (Chế độ Test)");
        setSize(450, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new GridLayout(8, 2, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Khởi tạo và ĐIỀN SẴN dữ liệu test
        mainPanel.add(new JLabel("Tên đăng nhập:"));
        txtUsername = new JTextField("khachhang_test");
        mainPanel.add(txtUsername);

        mainPanel.add(new JLabel("Mật khẩu:"));
        txtPassword = new JPasswordField("password123");
        mainPanel.add(txtPassword);

        mainPanel.add(new JLabel("Xác nhận mật khẩu:"));
        txtConfirmPassword = new JPasswordField("password123");
        mainPanel.add(txtConfirmPassword);

        mainPanel.add(new JLabel("Họ tên:"));
        txtTen = new JTextField("Nguyễn Văn A");
        mainPanel.add(txtTen);

        mainPanel.add(new JLabel("Số điện thoại:"));
        txtSDT = new JTextField("0901234567");
        mainPanel.add(txtSDT);

        mainPanel.add(new JLabel("Email:"));
        txtEmail = new JTextField("nva@gmail.com");
        mainPanel.add(txtEmail);

        mainPanel.add(new JLabel("Địa chỉ:"));
        txtDiaChi = new JTextField("123 Đường ABC, Quận X, TP Y");
        mainPanel.add(txtDiaChi);

        btnDangKy = new JButton("Đăng ký");
        mainPanel.add(new JLabel("")); // Khoảng trống
        mainPanel.add(btnDangKy);

        // Xử lý sự kiện nút bấm
        btnDangKy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleRegistration();
            }
        });

        add(mainPanel);
    }

    private void handleRegistration() {
        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());
        String confirmPassword = new String(txtConfirmPassword.getPassword());
        String ten = txtTen.getText();
        String sdt = txtSDT.getText();
        String email = txtEmail.getText();
        String diaChi = txtDiaChi.getText();

        KhachHangBUS bus = new KhachHangBUS();
        String ketQua = bus.dangKyKhachHang(username, password, confirmPassword, ten, sdt, email, diaChi);

        JOptionPane.showMessageDialog(this, ketQua, "Thông báo", 
            ketQua.contains("thành công") ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
        
        // Cố tình không xóa trắng các ô (clearFields) sau khi test để bạn có thể bấm test lại nhiều lần 
        // hoặc sửa nhẹ dữ liệu (ví dụ đổi khachhang_test thành khachhang_test2) rồi bấm tiếp.
    }
}