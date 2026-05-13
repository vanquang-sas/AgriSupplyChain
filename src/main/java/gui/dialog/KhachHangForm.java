package gui.dialog;

import bus.KhachHangBUS;
import dto.KhachHangDTO;
import util.AppColor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;

public class KhachHangForm extends JPanel {

    private ModernTextField txtMaKH, txtUsername, txtTenKH, txtSDT, txtEmail, txtDiaChi;
    private ModernPasswordField txtPassword;
    private JComboBox<String> cbLoaiKH;

    private final KhachHangBUS bus = new KhachHangBUS();
    private final String currentMaKH; 

    public KhachHangForm(String maKHToEdit) {
        this.currentMaKH = maKHToEdit;
        initComponents();
        if (maKHToEdit != null) {
            loadDataToForm(maKHToEdit);
        }
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        setBackground(AppColor.SURFACE);
        setBorder(new EmptyBorder(24, 32, 24, 32));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(12, 10, 12, 10); 

        // Khởi tạo các Component
        txtMaKH = new ModernTextField(currentMaKH == null ? "Hệ thống tự động cấp" : currentMaKH, false);
        txtSDT = new ModernTextField("Nhập số điện thoại...", true);
        txtTenKH = new ModernTextField("Nhập đầy đủ tên khách hàng...", true);
        txtEmail = new ModernTextField("Nhập địa chỉ email...", true);
        txtDiaChi = new ModernTextField("Nhập địa chỉ cụ thể...", true);
        cbLoaiKH = createModernComboBox(new String[]{"Thường", "Thân thiết", "VIP"});

        int row = 0;

        // --- DÒNG 1: Mã KH (Trái) & Số điện thoại (Phải) ---
        gbc.gridy = row++;
        gbc.gridx = 0; gbc.weightx = 0; add(createLabel("Mã KH:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.5; add(txtMaKH, gbc);
        gbc.gridx = 2; gbc.weightx = 0; add(createLabel("Số điện thoại (*):"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.5; add(txtSDT, gbc);

        // --- DÒNG TÀI KHOẢN: Chỉ hiển thị khi THÊM MỚI ---
        if (currentMaKH == null) {
            txtUsername = new ModernTextField("Nhập tên đăng nhập...", true);
            txtPassword = new ModernPasswordField("Nhập mật khẩu...", true);

            gbc.gridy = row++;
            gbc.gridx = 0; gbc.weightx = 0; add(createLabel("Username (*):"), gbc);
            gbc.gridx = 1; gbc.weightx = 0.5; add(txtUsername, gbc);
            gbc.gridx = 2; gbc.weightx = 0; add(createLabel("Mật khẩu (*):"), gbc);
            gbc.gridx = 3; gbc.weightx = 0.5; add(txtPassword, gbc);
        }

        // --- DÒNG 2: Tên KH (Chiếm hết chiều ngang) ---
        gbc.gridy = row++;
        gbc.gridx = 0; gbc.weightx = 0; gbc.gridwidth = 1; add(createLabel("Tên khách hàng (*):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.gridwidth = 3; add(txtTenKH, gbc);

        // --- DÒNG 3: Email (Trái) & Loại KH (Phải) ---
        gbc.gridy = row++; gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.weightx = 0; add(createLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.5; add(txtEmail, gbc);
        gbc.gridx = 2; gbc.weightx = 0; add(createLabel("Loại KH:"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.5; add(cbLoaiKH, gbc);

        // --- DÒNG 4: Địa chỉ (Chiếm hết chiều ngang) ---
        gbc.gridy = row++;
        gbc.gridx = 0; gbc.weightx = 0; gbc.gridwidth = 1; add(createLabel("Địa chỉ (*):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.gridwidth = 3; add(txtDiaChi, gbc);
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(AppColor.TEXT_PRIMARY);
        return lbl;
    }

    private JComboBox<String> createModernComboBox(String[] items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cb.setPreferredSize(new Dimension(200, 42)); 
        cb.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cb.putClientProperty("JComponent.roundRect", true); 
        return cb;
    }

    // --- CLASS CUSTOM UI ---
    class ModernTextField extends JTextField {
        private String placeholder;
        public ModernTextField(String placeholder, boolean enabled) {
            this.placeholder = placeholder;
            setEnabled(enabled);
            setFont(new Font("Segoe UI", Font.PLAIN, 14));
            setForeground(AppColor.TEXT_PRIMARY);
            setPreferredSize(new Dimension(200, 42)); 
            setOpaque(false); 
            setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
            addFocusListener(new java.awt.event.FocusAdapter() {
                @Override public void focusGained(java.awt.event.FocusEvent e) { repaint(); }
                @Override public void focusLost(java.awt.event.FocusEvent e) { repaint(); }
            });
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(isEnabled() ? AppColor.SURFACE : AppColor.BACKGROUND);
            g2.fillRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 8, 8);
            super.paintComponent(g);
            if (getText().isEmpty() && !isFocusOwner()) {
                g2.setColor(new Color(156, 163, 175)); 
                FontMetrics fm = g.getFontMetrics();
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(placeholder, 12, y); 
            }
            g2.dispose();
        }
        @Override protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (isFocusOwner()) {
                g2.setColor(AppColor.PRIMARY); g2.setStroke(new BasicStroke(1.5f));
            } else {
                g2.setColor(AppColor.BORDER); g2.setStroke(new BasicStroke(1.2f));
            }
            g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 8, 8);
            g2.dispose();
        }
    }

    class ModernPasswordField extends JPasswordField {
        private String placeholder;
        public ModernPasswordField(String placeholder, boolean enabled) {
            this.placeholder = placeholder;
            setEnabled(enabled);
            setFont(new Font("Segoe UI", Font.PLAIN, 14));
            setForeground(AppColor.TEXT_PRIMARY);
            setPreferredSize(new Dimension(200, 42)); 
            setOpaque(false); 
            setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
            addFocusListener(new java.awt.event.FocusAdapter() {
                @Override public void focusGained(java.awt.event.FocusEvent e) { repaint(); }
                @Override public void focusLost(java.awt.event.FocusEvent e) { repaint(); }
            });
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(isEnabled() ? AppColor.SURFACE : AppColor.BACKGROUND);
            g2.fillRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 8, 8);
            super.paintComponent(g);
            if (new String(getPassword()).isEmpty() && !isFocusOwner()) {
                g2.setColor(new Color(156, 163, 175)); 
                FontMetrics fm = g.getFontMetrics();
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(placeholder, 12, y); 
            }
            g2.dispose();
        }
        @Override protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (isFocusOwner()) {
                g2.setColor(AppColor.PRIMARY); g2.setStroke(new BasicStroke(1.5f));
            } else {
                g2.setColor(AppColor.BORDER); g2.setStroke(new BasicStroke(1.2f));
            }
            g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 8, 8);
            g2.dispose();
        }
    }

    private void loadDataToForm(String maKH) {
        KhachHangDTO kh = bus.getById(maKH);
        if (kh == null) return;

        txtTenKH.setText(kh.getTenKH());
        txtSDT.setText(kh.getSdt());
        txtEmail.setText(kh.getEmail() != null ? kh.getEmail() : "");
        txtDiaChi.setText(kh.getDiaChi() != null ? kh.getDiaChi() : "");

        String loai = kh.getLoaiKH();
        if (loai != null) cbLoaiKH.setSelectedItem(loai);
    }

    public boolean saveData() {
        try {
            if(txtTenKH.getText().trim().isEmpty() || txtSDT.getText().trim().isEmpty() || txtDiaChi.getText().trim().isEmpty()) {
                throw new IllegalArgumentException("Vui lòng điền đầy đủ Tên, SĐT và Địa chỉ!");
            }

            KhachHangDTO kh = new KhachHangDTO();
            kh.setTenKH(txtTenKH.getText().trim());
            kh.setSdt(txtSDT.getText().trim());
            kh.setEmail(txtEmail.getText().trim());
            kh.setDiaChi(txtDiaChi.getText().trim());
            kh.setLoaiKH(cbLoaiKH.getSelectedItem().toString());

            if (currentMaKH == null) {
                if(txtUsername.getText().trim().isEmpty() || new String(txtPassword.getPassword()).trim().isEmpty()) {
                    throw new IllegalArgumentException("Vui lòng nhập tài khoản và mật khẩu cho khách hàng mới!");
                }
                kh.setUsername(txtUsername.getText().trim());
                String password = new String(txtPassword.getPassword()).trim();
                bus.them(kh, password);
                JOptionPane.showMessageDialog(this, "Thêm khách hàng thành công!",
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } else {
                kh.setMaKH(currentMaKH);
                bus.capNhat(kh);
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!",
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
            }
            return true;

        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
            return false;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi hệ thống: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}