package gui.dialog;

import dto.NhanVienDTO;
import util.AppColor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class NhanVienForm extends JPanel {
    private ModernTextField txtMaNV, txtTenNV, txtSDT, txtLuong;
    private JComboBox<String> cbChucVu;
    private JComboBox<String> cbTrangThai;
    private String currentMaNV;

    public NhanVienForm(String maNVToEdit) {
        this.currentMaNV = maNVToEdit;
        initComponents();
        if (maNVToEdit != null) {
            loadDataToForm(maNVToEdit);
        }
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        setBackground(AppColor.SURFACE);
        
        // Đã xóa setPreferredSize cố định để form tự động co giãn vừa khít nội dung
        setBorder(new EmptyBorder(24, 32, 24, 32));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(12, 10, 12, 10); // Tăng khoảng cách các dòng

        // Khởi tạo các ô nhập
        txtMaNV = new ModernTextField("Hệ thống tự động cấp", false);
        txtSDT = new ModernTextField("Nhập số điện thoại (10 số)...", true);
        txtTenNV = new ModernTextField("Nhập đầy đủ họ và tên nhân viên...", true);
        txtLuong = new ModernTextField("Nhập mức lương cơ bản (VNĐ)...", true);

        // Khởi tạo ComboBox
        cbChucVu = createModernComboBox(new String[]{"NV thu mua", "NV kho", "NV giao hàng"});
        cbTrangThai = createModernComboBox(new String[]{"Bị khóa", "Hoạt động"});
        cbTrangThai.setSelectedIndex(1);

        // --- DÒNG 1: Mã NV (Trái) & Số điện thoại (Phải) ---
        gbc.gridy = 0;
        gbc.gridx = 0; gbc.weightx = 0; add(createLabel("Mã NV:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.5; add(txtMaNV, gbc); // Dùng 0.5 để chia đều chiều ngang
        gbc.gridx = 2; gbc.weightx = 0; add(createLabel("Số điện thoại (*):"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.5; add(txtSDT, gbc);

        // --- DÒNG 2: Tên nhân viên (Chiếm hết chiều ngang) ---
        gbc.gridy = 1;
        gbc.gridx = 0; gbc.weightx = 0; gbc.gridwidth = 1; add(createLabel("Tên nhân viên (*):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.gridwidth = 3; add(txtTenNV, gbc); // 1.0 là tổng của 2 ô phía trên (0.5 + 0.5)

        // --- DÒNG 3: Chức vụ (Trái) & Trạng thái (Phải) ---
        gbc.gridy = 2; gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.weightx = 0; add(createLabel("Chức vụ (*):"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.5; add(cbChucVu, gbc);
        gbc.gridx = 2; gbc.weightx = 0; add(createLabel("Trạng thái:"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.5; add(cbTrangThai, gbc);

        // --- DÒNG 4: Lương (Chiếm hết chiều ngang) ---
        gbc.gridy = 3;
        gbc.gridx = 0; gbc.weightx = 0; gbc.gridwidth = 1; add(createLabel("Mức lương (VNĐ):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.gridwidth = 3; add(txtLuong, gbc);
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
        
        // Đặt Dimension(200, 42) thay vì Dimension(0, 42) để tránh lỗi mất viền
        cb.setPreferredSize(new Dimension(200, 42)); 
        cb.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Mượn tính năng bo góc của FlatLaf
        cb.putClientProperty("JComponent.roundRect", true); 
        return cb;
    }

    // --- CLASS CUSTOM: TỰ VẼ PLACEHOLDER VÀ CHỐNG LẸM CHỮ ---
    class ModernTextField extends JTextField {
        private String placeholder;
        public ModernTextField(String placeholder, boolean enabled) {
            this.placeholder = placeholder;
            setEnabled(enabled);
            setFont(new Font("Segoe UI", Font.PLAIN, 14));
            setForeground(AppColor.TEXT_PRIMARY);
            setPreferredSize(new Dimension(200, 42)); // Sửa từ 0 thành 200
            setOpaque(false); 
            setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
            addFocusListener(new java.awt.event.FocusAdapter() {
                @Override public void focusGained(java.awt.event.FocusEvent e) { repaint(); }
                @Override public void focusLost(java.awt.event.FocusEvent e) { repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(isEnabled() ? AppColor.SURFACE : AppColor.BACKGROUND);
            // Vẽ nền lùi vào 1px
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

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (isFocusOwner()) {
                g2.setColor(AppColor.PRIMARY);
                g2.setStroke(new BasicStroke(1.5f));
            } else {
                g2.setColor(AppColor.BORDER);
                g2.setStroke(new BasicStroke(1.2f)); // Tăng độ dày nhẹ
            }
            // QUAN TRỌNG: Vẽ lùi vào 1px và trừ đi 3px kích thước để không bị khuyết cạnh
            g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 8, 8);
            g2.dispose();
        }
    }

    private void loadDataToForm(String maNV) {
        // Khởi tạo BUS để kéo dữ liệu từ CSDL
        bus.NhanVienBUS bus = new bus.NhanVienBUS();
        NhanVienDTO nv = bus.getById(maNV);

        if (nv != null) {
            // Đổ dữ liệu vào các Text Field
            txtMaNV.setText(nv.getMaNV());
            txtTenNV.setText(nv.getTenNV());
            txtSDT.setText(nv.getSdt());
            
            // Ép kiểu lương để không bị hiện chữ 'E' (ví dụ 1.5E7) hoặc có số .0 phía sau
            txtLuong.setText(String.format("%.0f", nv.getLuong()));

            // Đổ dữ liệu vào Combo Box Chức vụ
            cbChucVu.setSelectedItem(nv.getChucVu());

            // Đổ dữ liệu vào Combo Box Trạng thái (Giả sử 1 = Hoạt động, 0 = Bị khóa)
            if (nv.getTrangThaiTK() == 1) {
                cbTrangThai.setSelectedItem("Hoạt động");
            } else {
                cbTrangThai.setSelectedItem("Bị khóa");
            }
        }
    }

    public boolean saveData() {
        try {
            if(txtTenNV.getText().trim().isEmpty() || txtSDT.getText().trim().isEmpty()) {
                throw new IllegalArgumentException("Vui lòng điền đầy đủ Tên và Số điện thoại!");
            }
            JOptionPane.showMessageDialog(this, "Đã gọi hàm Thêm/Sửa (Mock)!");
            return true;
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}