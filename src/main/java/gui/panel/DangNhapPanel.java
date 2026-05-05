package gui.panel;

import util.AppColor;
import bus.TaiKhoanBUS;
import gui.AuthFrame;
import gui.MainFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DangNhapPanel extends JPanel {
    private AuthFrame parentFrame;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private TaiKhoanBUS taiKhoanBUS;

    public DangNhapPanel() {}

    public DangNhapPanel(AuthFrame parentFrame) {
        this.parentFrame = parentFrame;
        this.taiKhoanBUS = new TaiKhoanBUS();
        
        setLayout(new GridBagLayout());
        setOpaque(false);

        JPanel formWrapper = new JPanel();
        formWrapper.setOpaque(false);
        formWrapper.setLayout(new BoxLayout(formWrapper, BoxLayout.Y_AXIS));
        formWrapper.setBorder(new EmptyBorder(0, 50, 0, 50));

        JLabel lblTitle = new JLabel("ĐĂNG NHẬP");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(AppColor.PRIMARY);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitle.setBorder(new EmptyBorder(0, 0, 20, 0));

        JPanel gridForm = new JPanel(new GridBagLayout());
        gridForm.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Tài khoản
        gbc.gridy = 0; gbc.insets = new Insets(0, 0, 5, 0);
        JLabel lblUser = new JLabel("Tên đăng nhập");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblUser.setForeground(AppColor.TEXT_SECONDARY);
        gridForm.add(lblUser, gbc);

        gbc.gridy = 1; gbc.insets = new Insets(0, 0, 15, 0);
        txtUsername = createStyledTextField();
        gridForm.add(txtUsername, gbc);

        // Mật khẩu và Nút Quên mật khẩu nằm trên cùng 1 hàng
        gbc.gridy = 2; gbc.insets = new Insets(0, 0, 5, 0);
        JPanel passLabelRow = new JPanel(new BorderLayout());
        passLabelRow.setOpaque(false);
        
        JLabel lblPass = new JLabel("Mật khẩu");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblPass.setForeground(AppColor.TEXT_SECONDARY);
        passLabelRow.add(lblPass, BorderLayout.WEST);

        // CHUYỂN SANG TRANG QUÊN MẬT KHẨU
        JLabel lblForgot = new JLabel("Bạn quên mật khẩu?");
        lblForgot.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblForgot.setForeground(AppColor.PRIMARY);
        lblForgot.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblForgot.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                parentFrame.switchPanel("FORGOT_PASS");
            }
        });
        passLabelRow.add(lblForgot, BorderLayout.EAST);
        gridForm.add(passLabelRow, gbc);

        gbc.gridy = 3; gbc.insets = new Insets(0, 0, 20, 0);
        txtPassword = new JPasswordField();
        styleTextField(txtPassword);
        gridForm.add(txtPassword, gbc);

        // Nút đăng nhập
        JButton btnLogin = new JButton("ĐĂNG NHẬP");
        btnLogin.setBackground(AppColor.PRIMARY);
        btnLogin.setForeground(AppColor.SURFACE);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.setMaximumSize(new Dimension(350, 45));
        btnLogin.addActionListener(e -> handleLogin());

        // Thêm 2 dòng này để ép Java Swing hiển thị màu nền
        btnLogin.setOpaque(true);
        btnLogin.setBorderPainted(false);

        // Nút chuyển hướng Đăng ký
        JLabel lblRegister = new JLabel("Bạn chưa có tài khoản? Đăng ký mới");
        lblRegister.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblRegister.setForeground(AppColor.PRIMARY);
        lblRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblRegister.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblRegister.setBorder(new EmptyBorder(15, 0, 0, 0));
        lblRegister.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                parentFrame.switchPanel("REGISTER");
            }
        });

        formWrapper.add(lblTitle);
        formWrapper.add(gridForm);
        formWrapper.add(btnLogin);
        formWrapper.add(lblRegister);

        add(formWrapper, new GridBagConstraints() {{
            gridx = 0; gridy = 0; weightx = 1.0; fill = GridBagConstraints.HORIZONTAL;
        }});
    }

    private void handleLogin() {
        String user = txtUsername.getText();
        String pass = new String(txtPassword.getPassword());
        String result = taiKhoanBUS.login(user, pass);

        if (result.equals("SUCCESS")) {
            // 1. Lấy thông tin tài khoản hiện tại
            int loaiTK = util.Session.currentUser.getLoaiTK();
            String username = util.Session.currentUser.getUsername();
            
            // 2. Phân loại và lấy thêm dữ liệu ghi vào Session
            if (loaiTK == 0) {
                util.Session.chucVu = "Quản lý (Admin)";
                util.Session.tenNguoiDung = "Quản trị viên"; 
            } 
            else if (loaiTK == 1) {
                bus.NhanVienBUS nvBus = new bus.NhanVienBUS();
                dto.NhanVienDTO nv = nvBus.getByUsername(username);
                if (nv != null) {
                    util.Session.chucVu = nv.getChucVu(); // NV Kho, NV Thu mua,...
                    util.Session.tenNguoiDung = nv.getTenNV();
                }
            } 
            else if (loaiTK == 2) {
                bus.KhachHangBUS khBus = new bus.KhachHangBUS();
                dto.KhachHangDTO kh = khBus.getByUsername(username);
                if (kh != null) {
                    util.Session.chucVu = "Khách hàng";
                    util.Session.tenNguoiDung = kh.getTenKH();
                }
            }

            // 3. Chuyển sang MainFrame
            parentFrame.dispose();
            new gui.MainFrame().setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, result, "Lỗi đăng nhập", JOptionPane.ERROR_MESSAGE);
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
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));
    }
}