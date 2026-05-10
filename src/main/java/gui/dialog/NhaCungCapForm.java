package gui.dialog;

import bus.NhaCungCapBUS;
import dto.NhaCungCapDTO;
import util.AppColor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;

public class NhaCungCapForm extends JPanel {

    private ModernTextField txtMaNCC, txtTenNCC, txtSDT, txtEmail, txtDiaChi, txtChungNhan;
    private final NhaCungCapBUS bus = new NhaCungCapBUS();
    private final String currentMaNCC;

    public NhaCungCapForm(String maNCC) {
        this.currentMaNCC = maNCC;
        initComponents();
        if (maNCC != null) {
            loadDataToForm(maNCC);
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
        txtMaNCC = new ModernTextField(currentMaNCC == null ? "Hệ thống tự động cấp" : currentMaNCC, false);
        txtSDT = new ModernTextField("Nhập số điện thoại...", true);
        txtTenNCC = new ModernTextField("Nhập tên nhà cung cấp...", true);
        txtEmail = new ModernTextField("Nhập email liên hệ...", true);
        txtChungNhan = new ModernTextField("VD: ISO 9001...", true);
        txtDiaChi = new ModernTextField("Nhập địa chỉ cụ thể...", true);

        int row = 0;

        // --- DÒNG 1: Mã NCC (Trái) & Số điện thoại (Phải) ---
        gbc.gridy = row++;
        gbc.gridx = 0; gbc.weightx = 0; add(createLabel("Mã NCC:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.5; add(txtMaNCC, gbc);
        gbc.gridx = 2; gbc.weightx = 0; add(createLabel("Số điện thoại (*):"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.5; add(txtSDT, gbc);

        // --- DÒNG 2: Tên NCC (Chiếm hết chiều ngang) ---
        gbc.gridy = row++;
        gbc.gridx = 0; gbc.weightx = 0; gbc.gridwidth = 1; add(createLabel("Tên nhà CC (*):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.gridwidth = 3; add(txtTenNCC, gbc);

        // --- DÒNG 3: Email (Trái) & Chứng nhận (Phải) ---
        gbc.gridy = row++; gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.weightx = 0; add(createLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.5; add(txtEmail, gbc);
        gbc.gridx = 2; gbc.weightx = 0; add(createLabel("Chứng nhận:"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.5; add(txtChungNhan, gbc);

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

    private void loadDataToForm(String maNCC) {
        NhaCungCapDTO ncc = bus.getById(maNCC);
        if (ncc == null) return;

        txtTenNCC.setText(ncc.getTenNCC());
        txtSDT.setText(ncc.getSdt());
        txtEmail.setText(ncc.getEmail() != null ? ncc.getEmail() : "");
        txtDiaChi.setText(ncc.getDiaChi() != null ? ncc.getDiaChi() : "");
        txtChungNhan.setText(ncc.getChungNhanCL() != null ? ncc.getChungNhanCL() : "");
    }

    public boolean saveData() {
        try {
            if(txtTenNCC.getText().trim().isEmpty() || txtSDT.getText().trim().isEmpty() || txtDiaChi.getText().trim().isEmpty()) {
                throw new IllegalArgumentException("Vui lòng điền đầy đủ Tên, SĐT và Địa chỉ!");
            }

            NhaCungCapDTO ncc = new NhaCungCapDTO();
            ncc.setTenNCC(txtTenNCC.getText().trim());
            ncc.setSdt(txtSDT.getText().trim());
            ncc.setEmail(txtEmail.getText().trim());
            ncc.setDiaChi(txtDiaChi.getText().trim());
            ncc.setChungNhanCL(txtChungNhan.getText().trim());

            if (currentMaNCC == null) {
                bus.them(ncc);
                JOptionPane.showMessageDialog(this, "Thêm nhà cung cấp thành công!",
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } else {
                ncc.setMaNCC(currentMaNCC);
                bus.capNhat(ncc);
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