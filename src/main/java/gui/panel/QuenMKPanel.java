package gui.panel;

import util.AppColor;
import bus.TaiKhoanBUS;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import gui.AuthFrame;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class QuenMKPanel extends JPanel {
    private AuthFrame parentFrame;
    private TaiKhoanBUS taiKhoanBUS;
    
    private JPanel cardsPanel;
    private CardLayout cardLayout;
    
    // State variables
    private String currentUsername = "";
    private final String FIXED_OTP = "123456";

    // UI Components for Step 1
    private JTextField txtUsername;
    private JTextField txtEmailPhone;

    // UI Components for Step 2
    private JTextField txtOTP;

    // UI Components for Step 3
    private JPasswordField txtNewPassword;
    private JPasswordField txtConfirmPassword;

    public QuenMKPanel(AuthFrame parentFrame) {
        this.parentFrame = parentFrame;
        this.taiKhoanBUS = new TaiKhoanBUS();
        setLayout(new BorderLayout());
        setOpaque(false);

        cardLayout = new CardLayout();
        cardsPanel = new JPanel(cardLayout);
        cardsPanel.setOpaque(false);

        // Add steps
        cardsPanel.add(createStep1Panel(), "STEP1");
        cardsPanel.add(createStep2Panel(), "STEP2");
        cardsPanel.add(createStep3Panel(), "STEP3");

        add(cardsPanel, BorderLayout.CENTER);
    }
    
    // Custom JTextField creation
    private JTextField createCustomTextField() {
        JTextField textField = new JTextField();
        textField.setPreferredSize(new Dimension(300, 40));
        textField.setMaximumSize(new Dimension(300, 40));
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return textField;
    }

    private JPasswordField createCustomPasswordField() {
        JPasswordField passField = new JPasswordField();
        passField.setPreferredSize(new Dimension(300, 40));
        passField.setMaximumSize(new Dimension(300, 40));
        passField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return passField;
    }

    private JButton createCustomButton(String text) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(300, 40));
        btn.setMaximumSize(new Dimension(300, 40));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(AppColor.PRIMARY);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JLabel createBackLink() {
        JLabel lblBack = new JLabel("Quay lại đăng nhập");
        lblBack.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblBack.setForeground(AppColor.PRIMARY);
        lblBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblBack.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblBack.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                resetForm();
                parentFrame.switchPanel("LOGIN");
            }
        });
        return lblBack;
    }
    
    private JPanel createInputRow(String labelText, JComponent inputField) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(300, 70));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(new Color(100, 100, 100));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        inputField.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(lbl);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(inputField);

        return panel;
    }

    private JPanel createStep1Panel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(30, 50, 30, 50));

        JLabel lblTitle = new JLabel("KHÔI PHỤC MẬT KHẨU");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(AppColor.TEXT_PRIMARY);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblDesc = new JLabel("Vui lòng nhập thông tin để xác thực");
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblDesc.setAlignmentX(Component.CENTER_ALIGNMENT);

        txtUsername = createCustomTextField();
        txtEmailPhone = createCustomTextField();

        JButton btnNext = createCustomButton("Kiểm tra");
        btnNext.addActionListener(e -> processStep1());

        panel.add(Box.createVerticalGlue());
        panel.add(lblTitle);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(lblDesc);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));
        
        panel.add(createInputRow("Tên đăng nhập *", txtUsername));
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        panel.add(createInputRow("Email hoặc Số điện thoại *", txtEmailPhone));
        panel.add(Box.createRigidArea(new Dimension(0, 30)));
        
        btnNext.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(btnNext);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        panel.add(createBackLink());
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JPanel createStep2Panel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(30, 50, 30, 50));

        JLabel lblTitle = new JLabel("XÁC NHẬN MÃ OTP");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(AppColor.TEXT_PRIMARY);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblDesc = new JLabel("<html><center>Mã xác nhận đã được gửi.<br>Vui lòng kiểm tra và nhập mã bên dưới.</center></html>");
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblDesc.setAlignmentX(Component.CENTER_ALIGNMENT);

        txtOTP = createCustomTextField();

        JButton btnConfirm = createCustomButton("Xác nhận");
        btnConfirm.addActionListener(e -> processStep2());

        panel.add(Box.createVerticalGlue());
        panel.add(lblTitle);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(lblDesc);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));
        
        panel.add(createInputRow("Mã OTP (VD: 123456) *", txtOTP));
        panel.add(Box.createRigidArea(new Dimension(0, 30)));
        
        btnConfirm.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(btnConfirm);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        panel.add(createBackLink());
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JPanel createStep3Panel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(30, 50, 30, 50));

        JLabel lblTitle = new JLabel("ĐẶT MẬT KHẨU MỚI");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(AppColor.TEXT_PRIMARY);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        txtNewPassword = createCustomPasswordField();
        txtConfirmPassword = createCustomPasswordField();

        JButton btnReset = createCustomButton("Đổi mật khẩu");
        btnReset.addActionListener(e -> processStep3());

        panel.add(Box.createVerticalGlue());
        panel.add(lblTitle);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));
        
        panel.add(createInputRow("Mật khẩu mới *", txtNewPassword));
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        panel.add(createInputRow("Nhập lại mật khẩu mới *", txtConfirmPassword));
        panel.add(Box.createRigidArea(new Dimension(0, 30)));
        
        btnReset.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(btnReset);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        panel.add(createBackLink());
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private void processStep1() {
        String username = txtUsername.getText().trim();
        String info = txtEmailPhone.getText().trim();

        String result = taiKhoanBUS.xacThucQuenMatKhau(username, info);
        if ("SUCCESS".equals(result)) {
            currentUsername = username;
            JOptionPane.showMessageDialog(this, "Xác thực thành công! Mã OTP giả lập cho demo là: " + FIXED_OTP, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            cardLayout.show(cardsPanel, "STEP2");
        } else {
            JOptionPane.showMessageDialog(this, result, "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void processStep2() {
        String otp = txtOTP.getText().trim();
        if (FIXED_OTP.equals(otp)) {
            cardLayout.show(cardsPanel, "STEP3");
        } else {
            JOptionPane.showMessageDialog(this, "Mã OTP không chính xác!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void processStep3() {
        String newPass = new String(txtNewPassword.getPassword());
        String confirmPass = new String(txtConfirmPassword.getPassword());

        if (!newPass.equals(confirmPass)) {
            JOptionPane.showMessageDialog(this, "Mật khẩu xác nhận không khớp!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String result = taiKhoanBUS.datLaiMatKhau(currentUsername, newPass);
        if ("SUCCESS".equals(result)) {
            JOptionPane.showMessageDialog(this, "Đổi mật khẩu thành công! Vui lòng đăng nhập lại.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            resetForm();
            parentFrame.switchPanel("LOGIN");
        } else {
            JOptionPane.showMessageDialog(this, result, "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetForm() {
        txtUsername.setText("");
        txtEmailPhone.setText("");
        txtOTP.setText("");
        txtNewPassword.setText("");
        txtConfirmPassword.setText("");
        currentUsername = "";
        cardLayout.show(cardsPanel, "STEP1");
    }
}