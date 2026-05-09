package gui.dialog;

import bus.KhoBUS;
import dto.KhoDTO;
import util.AppColor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;

public class KhoForm extends JPanel {

    private ModernTextField txtMaKho, txtTenKho, txtDiaChi, txtMoTa;
    private JComboBox<String> cbLoaiKho;

    private final KhoBUS bus = new KhoBUS();
    private final String currentMaKho;

    public KhoForm(String maKhoToEdit) {
        this.currentMaKho = maKhoToEdit;
        initComponents();
        if (maKhoToEdit != null) {
            loadDataToForm(maKhoToEdit);
        }
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        setBackground(AppColor.SURFACE);
        setBorder(new EmptyBorder(24, 32, 24, 32));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(12, 10, 12, 10);

        // Khởi tạo các Component theo chuẩn Modern
        txtMaKho = new ModernTextField(currentMaKho == null ? "Hệ thống tự động cấp" : currentMaKho, false);
        txtTenKho = new ModernTextField("Nhập tên kho...", true);
        txtDiaChi = new ModernTextField("Nhập địa chỉ cụ thể...", true);
        txtMoTa = new ModernTextField("Nhập ghi chú hoặc mô tả...", true);
        cbLoaiKho = createModernComboBox(new String[]{"Mát", "Lạnh", "Đông"});

        int row = 0;

        // --- DÒNG 1: Mã Kho (Trái) & Loại Kho (Phải) ---
        gbc.gridy = row++;
        gbc.gridx = 0; gbc.weightx = 0; add(createLabel("Mã kho:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.5; add(txtMaKho, gbc);
        gbc.gridx = 2; gbc.weightx = 0; add(createLabel("Loại kho (*):"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.5; add(cbLoaiKho, gbc);

        // --- DÒNG 2: Tên Kho (Chiếm hết chiều ngang) ---
        gbc.gridy = row++;
        gbc.gridx = 0; gbc.weightx = 0; gbc.gridwidth = 1; add(createLabel("Tên kho (*):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.gridwidth = 3; add(txtTenKho, gbc);

        // --- DÒNG 3: Địa chỉ (Chiếm hết chiều ngang) ---
        gbc.gridy = row++;
        gbc.gridx = 0; gbc.weightx = 0; gbc.gridwidth = 1; add(createLabel("Địa chỉ (*):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.gridwidth = 3; add(txtDiaChi, gbc);

        // --- DÒNG 4: Mô tả (Chiếm hết chiều ngang) ---
        gbc.gridy = row++;
        gbc.gridx = 0; gbc.weightx = 0; gbc.gridwidth = 1; add(createLabel("Mô tả:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.gridwidth = 3; add(txtMoTa, gbc);
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

    private void loadDataToForm(String maKho) {
        KhoDTO kho = bus.getById(maKho);
        if (kho == null) return;

        txtTenKho.setText(kho.getTenKho());
        txtDiaChi.setText(kho.getDiaChi() != null ? kho.getDiaChi() : "");
        txtMoTa.setText(kho.getMoTa() != null ? kho.getMoTa() : "");

        String loai = kho.getLoaiKho();
        if (loai != null) cbLoaiKho.setSelectedItem(loai);
    }

    public boolean saveData() {
        try {
            if(txtTenKho.getText().trim().isEmpty() || txtDiaChi.getText().trim().isEmpty()) {
                throw new IllegalArgumentException("Vui lòng điền đầy đủ Tên kho và Địa chỉ!");
            }

            KhoDTO kho = new KhoDTO();
            kho.setTenKho(txtTenKho.getText().trim());
            kho.setLoaiKho(cbLoaiKho.getSelectedItem().toString());
            kho.setDiaChi(txtDiaChi.getText().trim());
            kho.setMoTa(txtMoTa.getText().trim());

            if (currentMaKho == null) {
                bus.them(kho);
                JOptionPane.showMessageDialog(this, "Thêm kho bãi thành công!",
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } else {
                kho.setMaKho(currentMaKho);
                bus.capNhat(kho);
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