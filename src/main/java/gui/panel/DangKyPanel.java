package gui.panel;

import util.AppColor;
import bus.KhachHangBUS;
import gui.AuthFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DangKyPanel extends JPanel {
    private AuthFrame parentFrame;
    private JTextField txtUsername, txtTen, txtSDT, txtEmail, txtDiaChi;
    private JPasswordField txtPassword, txtConfirmPassword;

    public DangKyPanel() {}

    public DangKyPanel(AuthFrame parentFrame) {
        this.parentFrame = parentFrame;
        setLayout(new GridBagLayout());
        setOpaque(false);

        JPanel formWrapper = new JPanel();
        formWrapper.setOpaque(false);
        formWrapper.setLayout(new BoxLayout(formWrapper, BoxLayout.Y_AXIS));
        formWrapper.setBorder(new EmptyBorder(0, 50, 0, 50));

        JLabel lblTitle = new JLabel("ĐĂNG KÝ TÀI KHOẢN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(AppColor.PRIMARY);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitle.setBorder(new EmptyBorder(0, 0, 15, 0));

        JPanel gridForm = new JPanel(new GridBagLayout());
        gridForm.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        int row = 0;
        txtUsername = createStyledTextField(); addFormField(gridForm, "Tên đăng nhập", txtUsername, gbc, row); row += 2;
        txtPassword = new JPasswordField(); styleTextField(txtPassword); addFormField(gridForm, "Mật khẩu", txtPassword, gbc, row); row += 2;
        txtConfirmPassword = new JPasswordField(); styleTextField(txtConfirmPassword); addFormField(gridForm, "Xác nhận mật khẩu", txtConfirmPassword, gbc, row); row += 2;
        txtTen = createStyledTextField(); addFormField(gridForm, "Họ tên", txtTen, gbc, row); row += 2;
        txtSDT = createStyledTextField(); addFormField(gridForm, "Số điện thoại", txtSDT, gbc, row); row += 2;
        txtEmail = createStyledTextField(); addFormField(gridForm, "Email", txtEmail, gbc, row); row += 2;
        txtDiaChi = createStyledTextField(); addFormField(gridForm, "Địa chỉ", txtDiaChi, gbc, row);

        JButton btnDangKy = new JButton("ĐĂNG KÝ");
        btnDangKy.setBackground(AppColor.PRIMARY);
        btnDangKy.setForeground(AppColor.SURFACE);
        btnDangKy.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnDangKy.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDangKy.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnDangKy.setMaximumSize(new Dimension(350, 45));
        btnDangKy.addActionListener(e -> handleRegistration());

        // Thêm 2 dòng này để ép Java Swing hiển thị màu nền
        btnDangKy.setOpaque(true);
        btnDangKy.setBorderPainted(false);

        // QUAY LẠI ĐĂNG NHẬP 
        JLabel lblBackLogin = new JLabel("Đã có tài khoản? Quay lại đăng nhập");
        lblBackLogin.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblBackLogin.setForeground(AppColor.PRIMARY);
        lblBackLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblBackLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblBackLogin.setBorder(new EmptyBorder(15, 0, 0, 0));
        lblBackLogin.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                parentFrame.switchPanel("LOGIN");
            }
        });

        formWrapper.add(lblTitle);
        formWrapper.add(gridForm);
        formWrapper.add(Box.createRigidArea(new Dimension(0, 15)));
        formWrapper.add(btnDangKy);
        formWrapper.add(lblBackLogin);

        add(formWrapper, new GridBagConstraints() {{
            gridx = 0; gridy = 0; weightx = 1.0; fill = GridBagConstraints.HORIZONTAL;
        }});
    }

    private void addFormField(JPanel panel, String labelStr, JComponent field, GridBagConstraints gbc, int row) {
        gbc.gridy = row; gbc.insets = new Insets(0, 0, 2, 0);
        JLabel label = new JLabel(labelStr);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(AppColor.TEXT_SECONDARY);
        panel.add(label, gbc);

        gbc.gridy = row + 1; gbc.insets = new Insets(0, 0, 8, 0);
        panel.add(field, gbc);
    }

    private void handleRegistration() {
        KhachHangBUS bus = new KhachHangBUS();
        String result = bus.dangKyKhachHang(txtUsername.getText(), new String(txtPassword.getPassword()), 
                        new String(txtConfirmPassword.getPassword()), txtTen.getText(), 
                        txtSDT.getText(), txtEmail.getText(), txtDiaChi.getText());

        if (result.contains("thành công")) {
            JOptionPane.showMessageDialog(this, result, "Thành công", JOptionPane.INFORMATION_MESSAGE);
            parentFrame.switchPanel("LOGIN"); // Chuyển về panel Đăng nhập
        } else {
            JOptionPane.showMessageDialog(this, result, "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JTextField createStyledTextField() {
        JTextField tf = new JTextField();
        styleTextField(tf);
        return tf;
    }

    private void styleTextField(JTextField tf) {
        tf.setBackground(AppColor.BACKGROUND);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppColor.BORDER),
            BorderFactory.createEmptyBorder(7, 15, 7, 15)
        ));
    }
}