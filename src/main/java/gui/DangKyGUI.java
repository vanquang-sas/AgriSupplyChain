package gui;

import bus.KhachHangBUS;
import util.AppColor;   

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DangKyGUI extends JFrame {
    private JTextField txtUsername, txtTen, txtSDT, txtEmail, txtDiaChi;
    private JPasswordField txtPassword, txtConfirmPassword;
    private JButton btnDangKy;

    public DangKyGUI() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Đăng ký tài khoản - Agri-Supply-Chain");
        setSize(1200, 750);
        setMinimumSize(new Dimension(1100, 650));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        // --- PANEL TRÁI (Logo và hình ảnh - chiếm 60%) ---
        JPanel leftPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                
                java.net.URL imgURL = getClass().getResource("/images/Background_DangKy.jpg");
                if (imgURL != null) {
                    Image bgImage = new ImageIcon(imgURL).getImage();
                    int panelW = getWidth();
                    int panelH = getHeight();
                    double scale = Math.max((double) panelW / bgImage.getWidth(null), (double) panelH / bgImage.getHeight(null));
                    int w = (int) (bgImage.getWidth(null) * scale);
                    int h = (int) (bgImage.getHeight(null) * scale);
                    g2d.drawImage(bgImage, (panelW - w) / 2, (panelH - h) / 2, w, h, this);
                } else {
                    g2d.setColor(AppColor.PRIMARY);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        // FIX: Ép kích thước tự nhiên về 0 để ăn chia tỷ lệ 100% theo weightx
        leftPanel.setPreferredSize(new Dimension(0, 0)); 

        JPanel logoContainer = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 160)); 
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        logoContainer.setOpaque(false);
        logoContainer.setLayout(new BoxLayout(logoContainer, BoxLayout.Y_AXIS));
        logoContainer.setBorder(new EmptyBorder(20, 30, 20, 30));

        JLabel lblLogo = new JLabel();
        java.net.URL logoURL = getClass().getResource("/images/TestLogo.png");
        if (logoURL != null) {
            lblLogo.setIcon(new ImageIcon(new ImageIcon(logoURL).getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH)));
        }
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblBrand = new JLabel("AGRI-SUPPLY-CHAIN");
        lblBrand.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblBrand.setForeground(AppColor.PRIMARY_ACTIVE);
        lblBrand.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblBrand.setBorder(new EmptyBorder(10, 0, 0, 0));

        logoContainer.add(lblLogo);
        logoContainer.add(lblBrand);
        leftPanel.add(logoContainer);

        gbc.gridx = 0;
        gbc.weightx = 0.6; // 60%
        add(leftPanel, gbc);

        // --- PANEL PHẢI (Form Đăng ký - chiếm 40%) ---
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(AppColor.SURFACE);
        // FIX: Ép kích thước tự nhiên về 0 để ăn chia tỷ lệ 100% theo weightx
        rightPanel.setPreferredSize(new Dimension(0, 0));

        JPanel formWrapper = new JPanel();
        formWrapper.setOpaque(false);
        formWrapper.setLayout(new BoxLayout(formWrapper, BoxLayout.Y_AXIS));
        formWrapper.setBorder(new EmptyBorder(0, 50, 0, 50)); 

        JLabel lblTitle = new JLabel("ĐĂNG KÝ TÀI KHOẢN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26)); 
        lblTitle.setForeground(AppColor.TEXT_PRIMARY);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitle.setBorder(new EmptyBorder(0, 0, 20, 0)); 

        JPanel gridForm = new JPanel(new GridBagLayout());
        gridForm.setBackground(AppColor.SURFACE);
        GridBagConstraints fGbc = new GridBagConstraints();
        fGbc.fill = GridBagConstraints.HORIZONTAL;
        fGbc.weightx = 1.0;

        int currentRow = 0;
        addFormField(gridForm, "Tên đăng nhập", txtUsername = createStyledTextField(), fGbc, currentRow); currentRow += 2;
        addFormField(gridForm, "Mật khẩu", txtPassword = new JPasswordField(), fGbc, currentRow); currentRow += 2;
        addFormField(gridForm, "Xác nhận mật khẩu", txtConfirmPassword = new JPasswordField(), fGbc, currentRow); currentRow += 2;
        addFormField(gridForm, "Họ tên", txtTen = createStyledTextField(), fGbc, currentRow); currentRow += 2;
        addFormField(gridForm, "Số điện thoại", txtSDT = createStyledTextField(), fGbc, currentRow); currentRow += 2;
        addFormField(gridForm, "Email", txtEmail = createStyledTextField(), fGbc, currentRow); currentRow += 2;
        addFormField(gridForm, "Địa chỉ", txtDiaChi = createStyledTextField(), fGbc, currentRow);

        styleTextField(txtPassword);
        styleTextField(txtConfirmPassword);

        btnDangKy = new JButton("ĐĂNG KÝ");
        btnDangKy.setBackground(AppColor.PRIMARY);
        btnDangKy.setForeground(AppColor.SURFACE);
        btnDangKy.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnDangKy.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDangKy.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnDangKy.setMaximumSize(new Dimension(350, 45)); 
        btnDangKy.setPreferredSize(new Dimension(350, 45));
        btnDangKy.addActionListener(e -> handleRegistration());

        formWrapper.add(lblTitle);
        formWrapper.add(gridForm);
        formWrapper.add(Box.createRigidArea(new Dimension(0, 15)));
        formWrapper.add(btnDangKy);

        GridBagConstraints wrapperGbc = new GridBagConstraints();
        wrapperGbc.gridx = 0;
        wrapperGbc.gridy = 0;
        wrapperGbc.weightx = 1.0;
        wrapperGbc.fill = GridBagConstraints.HORIZONTAL;
        rightPanel.add(formWrapper, wrapperGbc);

        gbc.gridx = 1;
        gbc.weightx = 0.4; // 40%
        add(rightPanel, gbc);
    }

    private void addFormField(JPanel panel, String labelStr, JComponent field, GridBagConstraints gbc, int row) {
        gbc.gridy = row;
        gbc.insets = new Insets(0, 0, 2, 0); 
        JLabel label = new JLabel(labelStr);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(AppColor.TEXT_SECONDARY);
        panel.add(label, gbc);

        gbc.gridy = row + 1;
        gbc.insets = new Insets(0, 0, 8, 0); 
        panel.add(field, gbc);
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

    private void handleRegistration() {
        KhachHangBUS bus = new KhachHangBUS();
        String ketQua = bus.dangKyKhachHang(txtUsername.getText(), new String(txtPassword.getPassword()), 
                        new String(txtConfirmPassword.getPassword()), txtTen.getText(), 
                        txtSDT.getText(), txtEmail.getText(), txtDiaChi.getText());

        showCustomAlert(ketQua, ketQua.contains("thành công"));
    }

    private void showCustomAlert(String message, boolean isSuccess) {
        JDialog dialog = new JDialog(this, true);
        dialog.setUndecorated(true);
        dialog.setSize(420, 220);
        dialog.setLocationRelativeTo(this);
        
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(AppColor.SURFACE);
        Color statusColor = isSuccess ? AppColor.PRIMARY_ACTIVE : new Color(220, 53, 69);
        contentPane.setBorder(BorderFactory.createLineBorder(statusColor, 2));

        JLabel lblTitle = new JLabel(isSuccess ? "THÀNH CÔNG" : "THÔNG BÁO");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(statusColor);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setBorder(new EmptyBorder(20, 0, 10, 0));

        JLabel lblMessage = new JLabel("<html><div style='text-align: center; padding: 0 10px;'>" + message + "</div></html>");
        lblMessage.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblMessage.setForeground(AppColor.TEXT_PRIMARY);
        lblMessage.setHorizontalAlignment(SwingConstants.CENTER);
        lblMessage.setBorder(new EmptyBorder(0, 20, 20, 20));

        JButton btnOK = new JButton("ĐÓNG");
        btnOK.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnOK.setBackground(statusColor);
        btnOK.setForeground(AppColor.SURFACE);
        btnOK.setFocusPainted(false);
        btnOK.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnOK.setPreferredSize(new Dimension(120, 40));
        btnOK.addActionListener(e -> {
            dialog.dispose();
            if (isSuccess) {
                this.dispose();
                // Mở UI đăng nhập sau khi đăng kí thành công
                // new DangNhapGUI().setVisible(true);
            }
        });
        
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(AppColor.SURFACE);
        bottomPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        bottomPanel.add(btnOK);

        contentPane.add(lblTitle, BorderLayout.NORTH);
        contentPane.add(lblMessage, BorderLayout.CENTER);
        contentPane.add(bottomPanel, BorderLayout.SOUTH);

        dialog.setContentPane(contentPane);
        dialog.setVisible(true);
    }
}