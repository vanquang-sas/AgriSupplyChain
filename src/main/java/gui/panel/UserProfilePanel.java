package gui.panel;

import dto.TaiKhoanDTO;
import util.AppColor;
import util.Session;
import util.HashPass;
import util.DBConnection;
import gui.component.RoundedButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;

public class UserProfilePanel extends JPanel {

    private CardLayout cardLayout;
    private JPanel pnlCards;

    private JTextField txtEmail;
    private JTextField txtPhone;
    private JTextField txtAddress;

    private JPasswordField txtOldPass;
    private JPasswordField txtNewPass;
    private JPasswordField txtConfirmPass;

    public UserProfilePanel() {
        setLayout(new BorderLayout());
        setBackground(AppColor.BACKGROUND);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        cardLayout = new CardLayout();
        pnlCards = new JPanel(cardLayout);
        pnlCards.setOpaque(false);

        // Khởi tạo và thêm 2 phân hệ màn hình vào bộ điều hướng CardLayout
        pnlCards.add(buildProfileView(), "PROFILE_VIEW");
        pnlCards.add(buildSecurityView(), "PASSWORD_VIEW");

        add(pnlCards, BorderLayout.CENTER);

        loadProfile();
    }

    private JPanel buildProfileView() {
        JPanel view = new JPanel(new BorderLayout());
        view.setOpaque(false);

        // Tiêu đề phân hệ chính
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 16, 0));

        JLabel title = new JLabel("Hồ Sơ Của Tôi");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(AppColor.TEXT_PRIMARY);
        header.add(title, BorderLayout.WEST);
        view.add(header, BorderLayout.NORTH);

        // Căn giữa thẻ thông tin bằng GridBagLayout giúp UI cân đối, sang trọng
        JPanel pnlCenter = new JPanel(new GridBagLayout());
        pnlCenter.setOpaque(false);
        
        JPanel card = buildProfileCard();
        card.setPreferredSize(new Dimension(600, 460));
        pnlCenter.add(card);

        view.add(pnlCenter, BorderLayout.CENTER);
        return view;
    }

    private JPanel buildSecurityView() {
        JPanel view = new JPanel(new BorderLayout());
        view.setOpaque(false);

        // Tiêu đề phân hệ bảo mật
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 16, 0));

        JLabel title = new JLabel("Đổi mật khẩu");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(AppColor.TEXT_PRIMARY);
        header.add(title, BorderLayout.WEST);
        view.add(header, BorderLayout.NORTH);

        // Căn giữa thẻ đổi mật khẩu
        JPanel pnlCenter = new JPanel(new GridBagLayout());
        pnlCenter.setOpaque(false);
        
        JPanel card = buildSecurityCard();
        card.setPreferredSize(new Dimension(600, 320));
        pnlCenter.add(card);

        view.add(pnlCenter, BorderLayout.CENTER);
        return view;
    }

    private JPanel buildProfileCard() {
        RoundedPanel card = new RoundedPanel(16);
        card.setLayout(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(28, 32, 28, 32));

        JLabel lblTitle = new JLabel("Thông Tin Cá Nhân");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(AppColor.TEXT_PRIMARY);
        lblTitle.setBorder(new EmptyBorder(0, 0, 24, 0));

        JPanel pnlForm = new JPanel(new GridLayout(6, 2, 16, 20));
        pnlForm.setOpaque(false);

        pnlForm.add(createLabel("Tên đăng nhập:"));
        JLabel lblUsername = new JLabel(Session.currentUser != null ? Session.currentUser.getUsername() : "");
        lblUsername.setFont(new Font("Segoe UI", Font.BOLD, 14));
        pnlForm.add(lblUsername);

        pnlForm.add(createLabel("Chức vụ:"));
        JLabel lblRole = new JLabel(Session.chucVu);
        lblRole.setFont(new Font("Segoe UI", Font.BOLD, 14));
        pnlForm.add(lblRole);

        pnlForm.add(createLabel("Ngày tạo tài khoản:"));
        String dateStr = "";
        if (Session.currentUser != null && Session.currentUser.getTgTao() != null) {
            dateStr = new SimpleDateFormat("dd/MM/yyyy").format(Session.currentUser.getTgTao());
        }
        JLabel lblDate = new JLabel(dateStr);
        lblDate.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pnlForm.add(lblDate);

        pnlForm.add(createLabel("Email liên hệ:"));
        txtEmail = createTextField();
        pnlForm.add(txtEmail);

        pnlForm.add(createLabel("Số điện thoại:"));
        txtPhone = createTextField();
        pnlForm.add(txtPhone);

        pnlForm.add(createLabel("Địa chỉ cư trú:"));
        txtAddress = createTextField();
        pnlForm.add(txtAddress);

        JPanel pnlWrap = new JPanel(new BorderLayout());
        pnlWrap.setOpaque(false);
        pnlWrap.add(pnlForm, BorderLayout.NORTH);

        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        pnlBottom.setOpaque(false);
        pnlBottom.setBorder(new EmptyBorder(24, 0, 0, 0));

        // Nút chuyển trang sang màn hình đổi mật khẩu
        RoundedButton btnToChangePass = new RoundedButton("Đổi Mật Khẩu", AppColor.ERROR_ACTIVE);
        btnToChangePass.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnToChangePass.setPreferredSize(new Dimension(140, 40));
        btnToChangePass.addActionListener(e -> cardLayout.show(pnlCards, "PASSWORD_VIEW"));
        pnlBottom.add(btnToChangePass);

        RoundedButton btnSave = new RoundedButton("Lưu Thay Đổi", AppColor.INFO_ACTIVE);
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSave.setPreferredSize(new Dimension(150, 40));
        btnSave.addActionListener(e -> updateUserInfo());
        pnlBottom.add(btnSave);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(pnlWrap, BorderLayout.CENTER);
        card.add(pnlBottom, BorderLayout.SOUTH);

        return card;
    }

    private JPanel buildSecurityCard() {
        RoundedPanel card = new RoundedPanel(16);
        card.setLayout(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(28, 32, 28, 32));

        JLabel lblTitle = new JLabel("Thông Tin Bảo Mật");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(AppColor.TEXT_PRIMARY);
        lblTitle.setBorder(new EmptyBorder(0, 0, 24, 0));

        JPanel pnlForm = new JPanel(new GridLayout(3, 2, 16, 24));
        pnlForm.setOpaque(false);

        pnlForm.add(createLabel("Mật khẩu hiện tại:"));
        txtOldPass = createPasswordField();
        pnlForm.add(txtOldPass);

        pnlForm.add(createLabel("Mật khẩu mới:"));
        txtNewPass = createPasswordField();
        pnlForm.add(txtNewPass);

        pnlForm.add(createLabel("Xác nhận mật khẩu mới:"));
        txtConfirmPass = createPasswordField();
        pnlForm.add(txtConfirmPass);

        JPanel pnlWrap = new JPanel(new BorderLayout());
        pnlWrap.setOpaque(false);
        pnlWrap.add(pnlForm, BorderLayout.NORTH);

        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        pnlBottom.setOpaque(false);
        pnlBottom.setBorder(new EmptyBorder(24, 0, 0, 0));

        // Nút quay lại màn hình thông tin cá nhân
        RoundedButton btnBack = new RoundedButton("Quay Lại", AppColor.PRIMARY);
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnBack.setPreferredSize(new Dimension(120, 40));
        btnBack.addActionListener(e -> {
            txtOldPass.setText("");
            txtNewPass.setText("");
            txtConfirmPass.setText("");
            cardLayout.show(pnlCards, "PROFILE_VIEW");
        });
        pnlBottom.add(btnBack);

        RoundedButton btnChangePass = new RoundedButton("Xác Nhận Đổi", AppColor.INFO_ACTIVE);
        btnChangePass.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnChangePass.setPreferredSize(new Dimension(150, 40));
        btnChangePass.addActionListener(e -> changePassword());
        pnlBottom.add(btnChangePass);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(pnlWrap, BorderLayout.CENTER);
        card.add(pnlBottom, BorderLayout.SOUTH);

        return card;
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(new Color(120, 120, 120));
        return lbl;
    }

    private JTextField createTextField() {
        JTextField txt = new JTextField();
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(215, 215, 215), 1, true),
                new EmptyBorder(8, 12, 8, 12)
        ));
        return txt;
    }

    private JPasswordField createPasswordField() {
        JPasswordField txt = new JPasswordField();
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(215, 215, 215), 1, true),
                new EmptyBorder(8, 12, 8, 12)
        ));
        return txt;
    }

    public void loadProfile() {
        if (Session.currentUser != null) {
            txtEmail.setText(Session.currentUser.getEmail());
            txtPhone.setText(Session.currentUser.getSdt());
            txtAddress.setText(Session.currentUser.getDiaChi());
        }
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
                cardLayout.show(pnlCards, "PROFILE_VIEW"); // Tự động quay lại trang cá nhân sau khi đổi thành công
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi đổi mật khẩu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    class RoundedPanel extends JPanel {
        private int cornerRadius;

        public RoundedPanel(int radius) {
            super();
            this.cornerRadius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
            g2.dispose();
        }
    }
}