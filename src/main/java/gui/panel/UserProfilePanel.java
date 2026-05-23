package gui.panel;
import dto.TaiKhoanDTO;
import dto.KhachHangDTO;
import dto.NhanVienDTO;
import bus.KhachHangBUS;
import bus.NhanVienBUS;
import bus.DonHangBUS;
import util.AppColor;
import util.Session;
import util.HashPass;
import util.DBConnection;
import gui.component.RoundedButton;
import gui.component.RoundedPanel;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
public class UserProfilePanel extends JPanel {
    private CardLayout cardLayout;
    private JPanel pnlCards;
    private GradientAvatarPanel gradientAvatar;
    private JLabel lblFullName;
    private JLabel lblBadge;
    private RoundedBadgePanel pnlBadgeBg;
    private JLabel lblStat1;
    private JLabel lblStat2;
    private JButton btnTabProfile;
    private JButton btnTabSecurity;
    private JLabel lblUsernameVal;
    private JLabel lblJoinDateVal;
    private JLabel lblIdVal;
    private JPanel pnlSpecDetail;
    private JLabel lblSpecLabel;
    private JLabel lblSpecVal;
    private JTextField txtEmail;
    private JTextField txtPhone;
    private JTextField txtAddress;
    private JPasswordField txtOldPass;
    private JPasswordField txtNewPass;
    private JPasswordField txtConfirmPass;
    public UserProfilePanel() {
        setLayout(new BorderLayout(24, 24));
        setBackground(AppColor.BACKGROUND);
        setBorder(new EmptyBorder(24, 24, 24, 24));
        // Bố cục 2 cột (Cột trái: Profile Card, Cột phải: Form)
        JPanel pnlLeft = buildLeftSidebar();
        add(pnlLeft, BorderLayout.WEST);
        cardLayout = new CardLayout();
        pnlCards = new JPanel(cardLayout);
        pnlCards.setOpaque(false);
        pnlCards.add(buildProfileView(), "PROFILE_VIEW");
        pnlCards.add(buildSecurityView(), "PASSWORD_VIEW");
        add(pnlCards, BorderLayout.CENTER);
        loadProfile();
    }
    private JPanel buildLeftSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setOpaque(false);
        sidebar.setPreferredSize(new Dimension(280, 0));
        RoundedPanel cardLeft = new RoundedPanel(16);
        cardLeft.setLayout(new BoxLayout(cardLeft, BoxLayout.Y_AXIS));
        cardLeft.setBackground(Color.WHITE);
        cardLeft.setBorder(new EmptyBorder(32, 20, 32, 20));
        // 1. Avatar
        gradientAvatar = new GradientAvatarPanel("?");
        gradientAvatar.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardLeft.add(gradientAvatar);
        cardLeft.add(Box.createVerticalStrut(16));
        // 2. Full Name
        lblFullName = new JLabel("Người dùng");
        lblFullName.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblFullName.setForeground(AppColor.TEXT_PRIMARY);
        lblFullName.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardLeft.add(lblFullName);
        cardLeft.add(Box.createVerticalStrut(8));
        // 3. Role Badge
        pnlBadgeBg = new RoundedBadgePanel(12);
        pnlBadgeBg.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 4));
        pnlBadgeBg.setBackground(Color.LIGHT_GRAY);
        pnlBadgeBg.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnlBadgeBg.setMaximumSize(new Dimension(180, 24));
        lblBadge = new JLabel("Khách hàng");
        lblBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        pnlBadgeBg.add(lblBadge);
        cardLeft.add(pnlBadgeBg);
        cardLeft.add(Box.createVerticalStrut(20));
        // 4. Quick stats
        JPanel pnlStats = new JPanel();
        pnlStats.setOpaque(false);
        pnlStats.setLayout(new BoxLayout(pnlStats, BoxLayout.Y_AXIS));
        pnlStats.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode("#F3F4F6"), 1, true),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));
        pnlStats.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnlStats.setMaximumSize(new Dimension(240, 70));
        lblStat1 = new JLabel("");
        lblStat1.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblStat1.setForeground(AppColor.TEXT_PRIMARY);
        lblStat1.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblStat2 = new JLabel("");
        lblStat2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblStat2.setForeground(AppColor.TEXT_SECONDARY);
        lblStat2.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnlStats.add(lblStat1);
        pnlStats.add(Box.createVerticalStrut(6));
        pnlStats.add(lblStat2);
        cardLeft.add(pnlStats);
        cardLeft.add(Box.createVerticalStrut(24));
        // 5. Divider
        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(Short.MAX_VALUE, 1));
        separator.setForeground(AppColor.BORDER);
        separator.setBackground(AppColor.BORDER);
        cardLeft.add(separator);
        cardLeft.add(Box.createVerticalStrut(24));
        // 6. Navigation Tabs
        JPanel pnlTabs = new JPanel();
        pnlTabs.setOpaque(false);
        pnlTabs.setLayout(new BoxLayout(pnlTabs, BoxLayout.Y_AXIS));
        pnlTabs.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnTabProfile = createTabButton("Thông tin cá nhân", true);
        btnTabSecurity = createTabButton("Đổi mật khẩu", false);
        btnTabProfile.addActionListener(e -> {
            updateTabButtonStyle(btnTabProfile, true);
            updateTabButtonStyle(btnTabSecurity, false);
            cardLayout.show(pnlCards, "PROFILE_VIEW");
        });
        btnTabSecurity.addActionListener(e -> {
            updateTabButtonStyle(btnTabProfile, false);
            updateTabButtonStyle(btnTabSecurity, true);
            cardLayout.show(pnlCards, "PASSWORD_VIEW");
        });
        pnlTabs.add(btnTabProfile);
        pnlTabs.add(Box.createVerticalStrut(10));
        pnlTabs.add(btnTabSecurity);
        cardLeft.add(pnlTabs);
        cardLeft.add(Box.createVerticalGlue());
        sidebar.add(cardLeft, BorderLayout.CENTER);
        return sidebar;
    }
    private JButton createTabButton(String text, boolean isActive) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(240, 44));
        btn.setPreferredSize(new Dimension(240, 44));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        updateTabButtonStyle(btn, isActive);
        return btn;
    }
    private void updateTabButtonStyle(JButton btn, boolean isActive) {
        if (isActive) {
            btn.setBackground(Color.decode("#F0FDF4")); // Soft green background
            btn.setForeground(AppColor.PRIMARY);       // Deep green text
            btn.setOpaque(true);
            btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 4, 0, 0, AppColor.PRIMARY), // Left indicator border
                    BorderFactory.createEmptyBorder(10, 16, 10, 16)
            ));
        } else {
            btn.setBackground(Color.WHITE);
            btn.setForeground(AppColor.TEXT_SECONDARY);
            btn.setOpaque(false);
            btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        }
        btn.repaint();
    }
    private JPanel buildProfileView() {
        RoundedPanel card = new RoundedPanel(16);
        card.setLayout(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(30, 40, 30, 40));
        // Header
        JPanel pnlHeader = new JPanel(new GridLayout(2, 1, 0, 4));
        pnlHeader.setOpaque(false);
        pnlHeader.setBorder(new EmptyBorder(0, 0, 24, 0));
        JLabel lblTitle = new JLabel("HỒ SƠ CỦA TÔI");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(AppColor.TEXT_PRIMARY);
        JLabel lblSub = new JLabel("Xem và cập nhật thông tin cá nhân của tài khoản");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(AppColor.TEXT_SECONDARY);
        pnlHeader.add(lblTitle);
        pnlHeader.add(lblSub);
        card.add(pnlHeader, BorderLayout.NORTH);
        // Center Content
        JPanel pnlContent = new JPanel();
        pnlContent.setOpaque(false);
        pnlContent.setLayout(new BoxLayout(pnlContent, BoxLayout.Y_AXIS));
        // SECTION 1: Thông tin tài khoản (Read-only)
        JPanel pnlAccSection = new JPanel(new BorderLayout());
        pnlAccSection.setOpaque(false);
        JLabel lblSecTitle1 = new JLabel("Thông tin tài khoản");
        lblSecTitle1.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblSecTitle1.setForeground(AppColor.PRIMARY);
        lblSecTitle1.setBorder(new EmptyBorder(0, 0, 12, 0));
        pnlAccSection.add(lblSecTitle1, BorderLayout.NORTH);
        JPanel pnlAccGrid = new JPanel(new GridLayout(2, 2, 24, 16));
        pnlAccGrid.setOpaque(false);
        // Username
        JPanel pnlUser = new JPanel(new GridLayout(2, 1, 0, 4));
        pnlUser.setOpaque(false);
        pnlUser.add(createMiniLabel("Tên đăng nhập"));
        lblUsernameVal = new JLabel("");
        lblUsernameVal.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblUsernameVal.setForeground(AppColor.TEXT_PRIMARY);
        pnlUser.add(lblUsernameVal);
        // Join Date
        JPanel pnlJoinDate = new JPanel(new GridLayout(2, 1, 0, 4));
        pnlJoinDate.setOpaque(false);
        pnlJoinDate.add(createMiniLabel("Ngày tạo tài khoản"));
        lblJoinDateVal = new JLabel("");
        lblJoinDateVal.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblJoinDateVal.setForeground(AppColor.TEXT_PRIMARY);
        pnlJoinDate.add(lblJoinDateVal);
        // Unique ID (MaKH or MaNV)
        JPanel pnlId = new JPanel(new GridLayout(2, 1, 0, 4));
        pnlId.setOpaque(false);
        pnlId.add(createMiniLabel("Mã định danh hệ thống"));
        lblIdVal = new JLabel("");
        lblIdVal.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblIdVal.setForeground(AppColor.TEXT_PRIMARY);
        pnlId.add(lblIdVal);
        // Specific detail (Salary or Customer Type)
        pnlSpecDetail = new JPanel(new GridLayout(2, 1, 0, 4));
        pnlSpecDetail.setOpaque(false);
        lblSpecLabel = createMiniLabel("Chi tiết tài khoản");
        pnlSpecDetail.add(lblSpecLabel);
        lblSpecVal = new JLabel("");
        lblSpecVal.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSpecVal.setForeground(AppColor.TEXT_PRIMARY);
        pnlSpecDetail.add(lblSpecVal);
        pnlAccGrid.add(pnlUser);
        pnlAccGrid.add(pnlId);
        pnlAccGrid.add(pnlJoinDate);
        pnlAccGrid.add(pnlSpecDetail);
        pnlAccSection.add(pnlAccGrid, BorderLayout.CENTER);
        pnlContent.add(pnlAccSection);
        pnlContent.add(Box.createVerticalStrut(24));
        pnlContent.add(createSeparator());
        pnlContent.add(Box.createVerticalStrut(20));
        // SECTION 2: Thông tin liên hệ (Editable)
        JPanel pnlContactSection = new JPanel(new BorderLayout());
        pnlContactSection.setOpaque(false);
        JLabel lblSecTitle2 = new JLabel("Thông tin cá nhân & Liên hệ");
        lblSecTitle2.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblSecTitle2.setForeground(AppColor.PRIMARY);
        lblSecTitle2.setBorder(new EmptyBorder(0, 0, 12, 0));
        pnlContactSection.add(lblSecTitle2, BorderLayout.NORTH);
        JPanel pnlContactGrid = new JPanel(new GridBagLayout());
        pnlContactGrid.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 16, 0);
        // Phone & Email on the same row (split into 2 columns)
        JPanel pnlRow1 = new JPanel(new GridLayout(1, 2, 24, 0));
        pnlRow1.setOpaque(false);
        JPanel pnlPhone = new JPanel(new BorderLayout(0, 4));
        pnlPhone.setOpaque(false);
        pnlPhone.add(createMiniLabel("Số điện thoại *"), BorderLayout.NORTH);
        txtPhone = createStyledTextField();
        pnlPhone.add(txtPhone, BorderLayout.CENTER);
        JPanel pnlEmail = new JPanel(new BorderLayout(0, 4));
        pnlEmail.setOpaque(false);
        pnlEmail.add(createMiniLabel("Email liên hệ *"), BorderLayout.NORTH);
        txtEmail = createStyledTextField();
        pnlEmail.add(txtEmail, BorderLayout.CENTER);
        pnlRow1.add(pnlPhone);
        pnlRow1.add(pnlEmail);
        gbc.gridy = 0;
        pnlContactGrid.add(pnlRow1, gbc);
        // Address on a single full-width row
        JPanel pnlAddress = new JPanel(new BorderLayout(0, 4));
        pnlAddress.setOpaque(false);
        pnlAddress.add(createMiniLabel("Địa chỉ cư trú *"), BorderLayout.NORTH);
        txtAddress = createStyledTextField();
        pnlAddress.add(txtAddress, BorderLayout.CENTER);
        gbc.gridy = 1;
        pnlContactGrid.add(pnlAddress, gbc);
        pnlContactSection.add(pnlContactGrid, BorderLayout.CENTER);
        pnlContent.add(pnlContactSection);
        card.add(pnlContent, BorderLayout.CENTER);
        // Bottom Actions
        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        pnlBottom.setOpaque(false);
        pnlBottom.setBorder(new EmptyBorder(24, 0, 0, 0));
        RoundedButton btnSave = new RoundedButton("Lưu Thay Đổi", AppColor.PRIMARY);
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSave.setPreferredSize(new Dimension(160, 40));
        btnSave.addActionListener(e -> updateUserInfo());
        pnlBottom.add(btnSave);
        card.add(pnlBottom, BorderLayout.SOUTH);
        return card;
    }
    private JPanel buildSecurityView() {
        RoundedPanel card = new RoundedPanel(16);
        card.setLayout(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(30, 40, 30, 40));
        // Header
        JPanel pnlHeader = new JPanel(new GridLayout(2, 1, 0, 4));
        pnlHeader.setOpaque(false);
        pnlHeader.setBorder(new EmptyBorder(0, 0, 24, 0));
        JLabel lblTitle = new JLabel("ĐỔI MẬT KHẨU");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(AppColor.TEXT_PRIMARY);
        JLabel lblSub = new JLabel("Cập nhật mật khẩu mới để tăng cường bảo mật cho tài khoản");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(AppColor.TEXT_SECONDARY);
        pnlHeader.add(lblTitle);
        pnlHeader.add(lblSub);
        card.add(pnlHeader, BorderLayout.NORTH);
        // Content fields
        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 20, 0);
        // Old Pass
        JPanel pnlOldPass = new JPanel(new BorderLayout(0, 4));
        pnlOldPass.setOpaque(false);
        pnlOldPass.add(createMiniLabel("Mật khẩu hiện tại *"), BorderLayout.NORTH);
        txtOldPass = new JPasswordField();
        pnlOldPass.add(createStyledPasswordFieldWrapper(txtOldPass), BorderLayout.CENTER);
        gbc.gridy = 0;
        pnlForm.add(pnlOldPass, gbc);
        // New Pass
        JPanel pnlNewPass = new JPanel(new BorderLayout(0, 4));
        pnlNewPass.setOpaque(false);
        pnlNewPass.add(createMiniLabel("Mật khẩu mới *"), BorderLayout.NORTH);
        txtNewPass = new JPasswordField();
        pnlNewPass.add(createStyledPasswordFieldWrapper(txtNewPass), BorderLayout.CENTER);
        gbc.gridy = 1;
        pnlForm.add(pnlNewPass, gbc);
        // Confirm Pass
        JPanel pnlConfirmPass = new JPanel(new BorderLayout(0, 4));
        pnlConfirmPass.setOpaque(false);
        pnlConfirmPass.add(createMiniLabel("Xác nhận mật khẩu mới *"), BorderLayout.NORTH);
        txtConfirmPass = new JPasswordField();
        pnlConfirmPass.add(createStyledPasswordFieldWrapper(txtConfirmPass), BorderLayout.CENTER);
        gbc.gridy = 2;
        pnlForm.add(pnlConfirmPass, gbc);
        JPanel pnlCenter = new JPanel(new BorderLayout());
        pnlCenter.setOpaque(false);
        pnlCenter.add(pnlForm, BorderLayout.NORTH);
        card.add(pnlCenter, BorderLayout.CENTER);
        // Bottom Actions
        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        pnlBottom.setOpaque(false);
        pnlBottom.setBorder(new EmptyBorder(24, 0, 0, 0));
        JButton btnCancel = new JButton("Hủy Bỏ");
        btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCancel.setForeground(AppColor.TEXT_SECONDARY);
        btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancel.setFocusPainted(false);
        btnCancel.setContentAreaFilled(false);
        btnCancel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppColor.BORDER, 1, true),
                BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));
        btnCancel.addActionListener(e -> {
            txtOldPass.setText("");
            txtNewPass.setText("");
            txtConfirmPass.setText("");
            // Switch to profile tab
            updateTabButtonStyle(btnTabProfile, true);
            updateTabButtonStyle(btnTabSecurity, false);
            cardLayout.show(pnlCards, "PROFILE_VIEW");
        });
        pnlBottom.add(btnCancel);
        RoundedButton btnChangePass = new RoundedButton("Xác Nhận Đổi", AppColor.PRIMARY);
        btnChangePass.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnChangePass.setPreferredSize(new Dimension(160, 40));
        btnChangePass.addActionListener(e -> changePassword());
        pnlBottom.add(btnChangePass);
        card.add(pnlBottom, BorderLayout.SOUTH);
        return card;
    }
    private JLabel createMiniLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(AppColor.TEXT_SECONDARY);
        return lbl;
    }
    private JSeparator createSeparator() {
        JSeparator sep = new JSeparator();
        sep.setForeground(AppColor.BORDER);
        sep.setBackground(AppColor.BORDER);
        return sep;
    }
    private JTextField createStyledTextField() {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf.setBackground(Color.WHITE);
        tf.setForeground(AppColor.TEXT_PRIMARY);
        tf.setCaretColor(AppColor.TEXT_PRIMARY);
        // Default border: light gray
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppColor.BORDER, 1, true),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        // Focus effect
        tf.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                tf.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(AppColor.PRIMARY, 1, true),
                        BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
            }
            @Override
            public void focusLost(FocusEvent e) {
                tf.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(AppColor.BORDER, 1, true),
                        BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
            }
        });
        return tf;
    }
    private JPanel createStyledPasswordFieldWrapper(JPasswordField txtPassword) {
        JPanel passWrapper = new JPanel(new BorderLayout());
        passWrapper.setBackground(Color.WHITE);
        passWrapper.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppColor.BORDER, 1, true),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.setBackground(Color.WHITE);
        txtPassword.setForeground(AppColor.TEXT_PRIMARY);
        txtPassword.setCaretColor(AppColor.TEXT_PRIMARY);
        txtPassword.setBorder(null); // Bỏ viền mặc định
        JLabel lblEye = new JLabel("👁");
        lblEye.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        lblEye.setForeground(AppColor.TEXT_SECONDARY);
        lblEye.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblEye.setBorder(new EmptyBorder(0, 10, 0, 0));
        char defaultEchoChar = txtPassword.getEchoChar();
        lblEye.addMouseListener(new MouseAdapter() {
            boolean isVisible = false;
            @Override
            public void mouseClicked(MouseEvent e) {
                isVisible = !isVisible;
                if (isVisible) {
                    txtPassword.setEchoChar((char) 0);
                    lblEye.setText("🙈");
                } else {
                    txtPassword.setEchoChar(defaultEchoChar);
                    lblEye.setText("👁");
                }
            }
        });
        // Focus listeners to wrapper border
        txtPassword.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                passWrapper.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(AppColor.PRIMARY, 1, true),
                        BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
            }
            @Override
            public void focusLost(FocusEvent e) {
                passWrapper.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(AppColor.BORDER, 1, true),
                        BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
            }
        });
        passWrapper.add(txtPassword, BorderLayout.CENTER);
        passWrapper.add(lblEye, BorderLayout.EAST);
        return passWrapper;
    }
    public void loadProfile() {
        if (Session.currentUser == null) return;
        // Set basic info initially
        String username = Session.currentUser.getUsername();
        lblUsernameVal.setText(username);
        String dateStr = "";
        if (Session.currentUser.getTgTao() != null) {
            dateStr = new SimpleDateFormat("dd/MM/yyyy").format(Session.currentUser.getTgTao());
        }
        lblJoinDateVal.setText(dateStr);
        txtEmail.setText(Session.currentUser.getEmail() != null ? Session.currentUser.getEmail() : "");
        txtPhone.setText(Session.currentUser.getSdt() != null ? Session.currentUser.getSdt() : "");
        txtAddress.setText(Session.currentUser.getDiaChi() != null ? Session.currentUser.getDiaChi() : "");
        // Set name letter for gradient avatar
        String fullName = Session.tenNguoiDung != null ? Session.tenNguoiDung : "User";
        lblFullName.setText(fullName);
        String letter = "?";
        if (!fullName.isEmpty()) {
            letter = fullName.substring(0, 1).toUpperCase();
        }
        gradientAvatar.setNameLetter(letter);
        // Customize role badge styling
        String roleStr = Session.chucVu != null ? Session.chucVu : "Khách hàng";
        lblBadge.setText(roleStr);
        int loaiTK = Session.currentUser.getLoaiTK();
        Color bgBadge, fgBadge;
        if (loaiTK == 0) {
            bgBadge = Color.decode("#FEE2E2"); // light red
            fgBadge = Color.decode("#991B1B"); // dark red
        } else if (loaiTK == 1) {
            bgBadge = Color.decode("#E0F2FE"); // light blue
            fgBadge = Color.decode("#075985"); // dark blue
        } else {
            bgBadge = Color.decode("#DCFCE7"); // light green
            fgBadge = Color.decode("#166534"); // dark green
        }
        pnlBadgeBg.setBackground(bgBadge);
        lblBadge.setForeground(fgBadge);
        // Async loading for database-intensive details (MaKH/MaNV, Salary/CustomerRank, total orders)
        lblIdVal.setText("Đang tải...");
        lblSpecVal.setText("Đang tải...");
        lblStat1.setText("Đang kết nối...");
        lblStat2.setText("Đang tính toán...");
        SwingWorker<ProfileDetails, Void> worker = new SwingWorker<ProfileDetails, Void>() {
            @Override
            protected ProfileDetails doInBackground() throws Exception {
                ProfileDetails details = new ProfileDetails();
                if (loaiTK == 0 || loaiTK == 1) {
                    NhanVienBUS nvBus = new NhanVienBUS();
                    NhanVienDTO nv = nvBus.getByUsername(username);
                    if (nv != null) {
                        details.id = nv.getMaNV();
                        details.specLabel = "Mức lương hiện tại";
                        details.specValue = String.format("%,.0f VND", nv.getLuong());
                        details.stat1 = "Mã NV: " + nv.getMaNV();
                        details.stat2 = "Lương: " + String.format("%,.0f VND", nv.getLuong());
                    }
                } else if (loaiTK == 2) {
                    KhachHangBUS khBus = new KhachHangBUS();
                    KhachHangDTO kh = khBus.getByUsername(username);
                    if (kh != null) {
                        details.id = kh.getMaKH();
                        details.specLabel = "Hạng thành viên";
                        details.specValue = kh.getLoaiKH() != null ? kh.getLoaiKH() : "Thành viên thường";
                        details.stat1 = "Mã KH: " + kh.getMaKH();
                        // Query total orders count
                        try {
                            DonHangBUS dhBus = new DonHangBUS();
                            int orderCount = dhBus.getDanhSachDonHang(kh.getMaKH()).size();
                            details.stat2 = "Đã đặt: " + orderCount + " đơn hàng";
                        } catch (Exception ex) {
                            details.stat2 = "Đã đặt: 0 đơn hàng";
                        }
                    }
                }
                return details;
            }
            @Override
            protected void done() {
                try {
                    ProfileDetails res = get();
                    if (res != null) {
                        lblIdVal.setText(res.id != null ? res.id : "Chưa cập nhật");
                        lblSpecLabel.setText(res.specLabel != null ? res.specLabel : "Chi tiết khác");
                        lblSpecVal.setText(res.specValue != null ? res.specValue : "N/A");
                        lblStat1.setText(res.stat1 != null ? res.stat1 : "");
                        lblStat2.setText(res.stat2 != null ? res.stat2 : "");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    lblIdVal.setText("Lỗi tải");
                    lblSpecVal.setText("Lỗi tải");
                    lblStat1.setText("Mã: N/A");
                    lblStat2.setText("Lỗi kết nối");
                }
            }
        };
        worker.execute();
    }
    private void updateUserInfo() {
        if (Session.currentUser == null) return;
        String email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim();
        String address = txtAddress.getText().trim();
        if (email.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE TAIKHOAN SET Email = ?, SDT = ?, DiaChi = ? WHERE Username = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, email);
            ps.setString(2, phone);
            ps.setString(3, address);
            ps.setString(4, Session.currentUser.getUsername());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                Session.currentUser.setEmail(email);
                Session.currentUser.setSdt(phone);
                Session.currentUser.setDiaChi(address);
                JOptionPane.showMessageDialog(this, "Cập nhật thông tin thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Có lỗi xảy ra: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void changePassword() {
        if (Session.currentUser == null) return;
        String oldPass = new String(txtOldPass.getPassword());
        String newPass = new String(txtNewPass.getPassword());
        String confirmPass = new String(txtConfirmPass.getPassword());
        if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ các trường mật khẩu!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!newPass.equals(confirmPass)) {
            JOptionPane.showMessageDialog(this, "Mật khẩu xác nhận không trùng khớp!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String hashedOld = HashPass.hashPassword(oldPass);
        if (!hashedOld.equals(Session.currentUser.getPassword())) {
            JOptionPane.showMessageDialog(this, "Mật khẩu hiện tại không chính xác!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String hashedNew = HashPass.hashPassword(newPass);
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE TAIKHOAN SET Password = ? WHERE Username = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, hashedNew);
            ps.setString(2, Session.currentUser.getUsername());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                Session.currentUser.setPassword(hashedNew);
                txtOldPass.setText("");
                txtNewPass.setText("");
                txtConfirmPass.setText("");
                JOptionPane.showMessageDialog(this, "Đổi mật khẩu thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                // Tự động quay lại tab thông tin cá nhân
                updateTabButtonStyle(btnTabProfile, true);
                updateTabButtonStyle(btnTabSecurity, false);
                cardLayout.show(pnlCards, "PROFILE_VIEW");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi đổi mật khẩu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    private static class ProfileDetails {
        String id;
        String specLabel;
        String specValue;
        String stat1;
        String stat2;
    }
    class RoundedBadgePanel extends JPanel {
        private int radius;
        public RoundedBadgePanel(int radius) {
            super();
            this.radius = radius;
            setOpaque(false);
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.dispose();
        }
    }
    class GradientAvatarPanel extends JPanel {
        private String nameLetter;
        public GradientAvatarPanel(String nameLetter) {
            this.nameLetter = nameLetter;
            setPreferredSize(new Dimension(100, 100));
            setMinimumSize(new Dimension(100, 100));
            setMaximumSize(new Dimension(100, 100));
            setOpaque(false);
        }
        public void setNameLetter(String letter) {
            this.nameLetter = letter;
            repaint();
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // Dải màu gradient từ Xanh lá (AppColor.PRIMARY) đến Xanh lá nhạt (AppColor.PRIMARY_HOVER)
            GradientPaint gp = new GradientPaint(0, 0, AppColor.PRIMARY, getWidth(), getHeight(), AppColor.PRIMARY_HOVER);
            g2.setPaint(gp);
            g2.fillOval(0, 0, getWidth(), getHeight());
            if (nameLetter != null && !nameLetter.isEmpty()) {
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 36));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(nameLetter)) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(nameLetter, x, y);
            }
            g2.dispose();
        }
    }
}