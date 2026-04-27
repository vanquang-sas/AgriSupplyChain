package gui;

import bus.TaiKhoanBUS;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginGUI extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private TaiKhoanBUS taiKhoanBUS;

    public LoginGUI() {
        taiKhoanBUS = new TaiKhoanBUS();
        initComponents();
    }

    private void initComponents() {
        setTitle("Đăng nhập - AgriSupplyChain");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Tiêu đề
        JLabel lblTitle = new JLabel("ĐĂNG NHẬP HỆ THỐNG", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(lblTitle, BorderLayout.NORTH);

        // Panel chứa Form
        JPanel panelForm = new JPanel(new GridLayout(2, 2, 10, 20));
        panelForm.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));
        
        panelForm.add(new JLabel("Tài khoản:"));
        txtUsername = new JTextField();
        panelForm.add(txtUsername);

        panelForm.add(new JLabel("Mật khẩu:"));
        txtPassword = new JPasswordField();
        panelForm.add(txtPassword);

        add(panelForm, BorderLayout.CENTER);

        // Panel chứa nút Bấm
        JPanel panelButton = new JPanel();
        panelButton.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        btnLogin = new JButton("Đăng nhập");
        btnLogin.setPreferredSize(new Dimension(150, 40));
        btnLogin.setFont(new Font("Arial", Font.BOLD, 14));
        panelButton.add(btnLogin);
        add(panelButton, BorderLayout.SOUTH);

        // Sự kiện Đăng nhập
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = txtUsername.getText();
                String password = new String(txtPassword.getPassword());

                String result = taiKhoanBUS.login(username, password);

                if (result.equals("Thành công")) {
                    JOptionPane.showMessageDialog(LoginGUI.this, "Đăng nhập thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    new MainGUI().setVisible(true);
                    LoginGUI.this.dispose(); // Đóng form đăng nhập
                } else {
                    JOptionPane.showMessageDialog(LoginGUI.this, result, "Lỗi đăng nhập", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}
