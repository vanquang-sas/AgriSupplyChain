package gui;

import bus.KhachHangBUS;
import javax.swing.*;
import java.awt.event.ActionEvent;

public class DangKyGUI extends JFrame {
    // Các Component nháp
    private JTextField txtUsername, txtTen, txtSDT, txtEmail, txtDiaChi;
    private JPasswordField txtPassword, txtConfirmPass;
    private JButton btnDangKy;
    
    private KhachHangBUS khBUS = new KhachHangBUS();

    public DangKyGUI() {
        setTitle("Đăng Ký Khách Hàng");
        // ... Code setup Form (Netbeans sinh ra) ...
        btnDangKy = new JButton("Đăng ký");
        btnDangKy.addActionListener(this::btnDangKyActionPerformed);
    }

    private void btnDangKyActionPerformed(ActionEvent e) {
        String user = txtUsername.getText();
        String pass = new String(txtPassword.getPassword());
        String confirm = new String(txtConfirmPass.getPassword());
        String ten = txtTen.getText();
        String sdt = txtSDT.getText();
        String email = txtEmail.getText();
        String diaChi = txtDiaChi.getText();

        String message = khBUS.dangKyKhachHang(user, pass, confirm, ten, sdt, email, diaChi);
        
        if (message.equals("Thành công")) {
            JOptionPane.showMessageDialog(this, "Đăng ký thành công! Vui lòng đăng nhập.");
            this.dispose(); // Đóng form mở form đăng nhập
        } else {
            JOptionPane.showMessageDialog(this, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}