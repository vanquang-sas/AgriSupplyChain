package gui;

import bus.KhachHangBUS;
import util.AppColor;   
import raven.modal.ModalDialog;
import raven.modal.component.SimpleModalBorder;

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
        setSize(1200, 800);
        setMinimumSize(new Dimension(1200, 800));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        // --- PANEL TRÁI (Logo và hình ảnh) ---
        JPanel leftPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                
                java.net.URL imgURL = getClass().getResource("/images/Background_DangKy.jpg");
                if (imgURL != null) {
                    Image bgImage = new ImageIcon(imgURL).getImage();
                    int imgW = bgImage.getWidth(null);
                    int imgH = bgImage.getHeight(null);
                    int panelW = getWidth();
                    int panelH = getHeight();

                    // Logic Center-Crop: Giữ tỷ lệ, lấp đầy panel và overlap phần thừa
                    double scale = Math.max((double) panelW / imgW, (double) panelH / imgH);
                    int w = (int) (imgW * scale);
                    int h = (int) (imgH * scale);
                    int x = (panelW - w) / 2;
                    int y = (panelH - h) / 2;

                    g2d.drawImage(bgImage, x, y, w, h, this);
                } else {
                    g2d.setColor(AppColor.PRIMARY);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        // Ép Layout tuân thủ tỷ lệ 6:4 bằng cách xóa bỏ kích thước ưu tiên mặc định
        leftPanel.setPreferredSize(new Dimension(0, 0));
        leftPanel.setMinimumSize(new Dimension(0, 0));

        // Logo Container mờ (Translucent)
        JPanel logoContainer = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 160)); // Độ mờ 160/255
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        logoContainer.setOpaque(false);
        logoContainer.setLayout(new BoxLayout(logoContainer, BoxLayout.Y_AXIS));
        logoContainer.setBorder(new EmptyBorder(30, 40, 30, 40));

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
        gbc.weightx = 0.6;
        add(leftPanel, gbc);

        // --- PANEL PHẢI (Form Đăng ký) ---
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(AppColor.SURFACE);
        rightPanel.setPreferredSize(new Dimension(0, 0));
        rightPanel.setMinimumSize(new Dimension(0, 0));

        JPanel formWrapper = new JPanel();
        formWrapper.setOpaque(false);
        formWrapper.setLayout(new BoxLayout(formWrapper, BoxLayout.Y_AXIS));
        formWrapper.setBorder(new EmptyBorder(0, 60, 0, 60)); // Lề hai bên để form cân đối

        JLabel lblTitle = new JLabel("ĐĂNG KÝ TÀI KHOẢN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(AppColor.TEXT_PRIMARY);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitle.setBorder(new EmptyBorder(0, 0, 40, 0));

        JPanel gridForm = new JPanel(new GridBagLayout());
        gridForm.setBackground(AppColor.SURFACE);
        GridBagConstraints fGbc = new GridBagConstraints();
        fGbc.fill = GridBagConstraints.HORIZONTAL;
        fGbc.weightx = 1.0;

        // Sửa lỗi: Quản lý hàng (row) chặt chẽ để không mất tên Label
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
        btnDangKy.setPreferredSize(new Dimension(0, 50));
        btnDangKy.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDangKy.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnDangKy.setMaximumSize(new Dimension(300, 50)); 
        btnDangKy.setPreferredSize(new Dimension(300, 50));
        btnDangKy.addActionListener(e -> handleRegistration());

        formWrapper.add(lblTitle);
        formWrapper.add(gridForm);
        formWrapper.add(Box.createRigidArea(new Dimension(0, 20)));
        formWrapper.add(btnDangKy);

        GridBagConstraints wrapperGbc = new GridBagConstraints();
        wrapperGbc.fill = GridBagConstraints.HORIZONTAL;
        wrapperGbc.weightx = 1.0;
        rightPanel.add(formWrapper, wrapperGbc);

        gbc.gridx = 1;
        gbc.weightx = 0.4; 
        add(rightPanel, gbc);
    }

    private void addFormField(JPanel panel, String labelStr, JComponent field, GridBagConstraints gbc, int row) {
        // Vẽ Label
        gbc.gridy = row;
        gbc.insets = new Insets(0, 0, 5, 0); // Khoảng cách nhỏ dưới nhãn
        JLabel label = new JLabel(labelStr);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(AppColor.TEXT_SECONDARY);
        panel.add(label, gbc);

        // Vẽ Input Field
        gbc.gridy = row + 1;
        gbc.insets = new Insets(0, 0, 10, 0); // Khoảng cách giữa các nhóm form
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
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
    }

    private void handleRegistration() {
        KhachHangBUS bus = new KhachHangBUS();
        String ketQua = bus.dangKyKhachHang(txtUsername.getText(), new String(txtPassword.getPassword()), 
                        new String(txtConfirmPassword.getPassword()), txtTen.getText(), 
                        txtSDT.getText(), txtEmail.getText(), txtDiaChi.getText());

        boolean isOk = ketQua.contains("thành công");
        ModalDialog.showModal(this, new SimpleModalBorder(new JLabel(ketQua), isOk ? "Thành công" : "Thông báo", 
                SimpleModalBorder.YES_OPTION, (controller, action) -> {
                    if (isOk && action == SimpleModalBorder.YES_OPTION) this.dispose();
                }));
    }
}