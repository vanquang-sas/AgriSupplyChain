package gui.dialog;

import bus.ThamSoBUS;
import dto.ThamSoDTO;
import util.AppColor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;

public class ThamSoForm extends JPanel {

    private ModernTextField txtGiaTri, txtMoTa;
    private final ThamSoBUS bus = new ThamSoBUS();
    private final String maTS;
    private final String tenTS;

    public ThamSoForm(String maTS) {
        this.maTS = maTS;
        ThamSoDTO ts = bus.getById(maTS);
        this.tenTS = (ts != null) ? ts.getTenTS() : maTS;
        initComponents(ts);
    }

    private void initComponents(ThamSoDTO ts) {
        setLayout(new GridBagLayout());
        setBackground(AppColor.SURFACE);
        setPreferredSize(new Dimension(500, 320));
        setBorder(new EmptyBorder(24, 32, 24, 32));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Dòng 1: Tên tham số (Chỉ đọc)
        gbc.gridy = 0;
        gbc.gridx = 0; gbc.weightx = 0; add(createLabel("Tham số hệ thống:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; 
        JLabel lblTenVal = new JLabel(tenTS);
        lblTenVal.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTenVal.setForeground(AppColor.PRIMARY);
        add(lblTenVal, gbc);

        // Dòng 2: Giá trị mới
        gbc.gridy = 1;
        gbc.gridx = 0; gbc.weightx = 0; add(createLabel("Giá trị cài đặt:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; 
        txtGiaTri = new ModernTextField("Nhập giá trị số...", true);
        txtGiaTri.setText(ts != null ? formatGiaTri(ts) : "");
        add(txtGiaTri, gbc);

        // Dòng 3: Hint (Hướng dẫn nhập)
        gbc.gridy = 2;
        gbc.gridx = 1;
        JLabel lblHint = new JLabel("<html><i>" + getHintText(tenTS) + "</i></html>");
        lblHint.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblHint.setForeground(AppColor.TEXT_SECONDARY);
        add(lblHint, gbc);

        // Dòng 4: Mô tả
        gbc.gridy = 3;
        gbc.gridx = 0; add(createLabel("Ghi chú/Mô tả:"), gbc);
        gbc.gridx = 1;
        txtMoTa = new ModernTextField("Giải thích ý nghĩa tham số...", true);
        txtMoTa.setText(ts != null && ts.getMoTa() != null ? ts.getMoTa() : "");
        add(txtMoTa, gbc);
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(AppColor.TEXT_PRIMARY);
        return lbl;
    }

    private String getHintText(String tenTS) {
        if (tenTS.contains("GG_")) return "Nhập số thập phân (Ví dụ: 0.1 tương ứng giảm 10%)";
        if (tenTS.startsWith("SHIP_")) return "Nhập phí vận chuyển cố định bằng tiền VNĐ";
        return "Nhập giá trị số nguyên phù hợp với đơn vị đo lường.";
    }

    private String formatGiaTri(ThamSoDTO ts) {
        double v = ts.getGiaTri();
        return (v == (long) v) ? String.valueOf((long) v) : String.valueOf(v);
    }

    public boolean saveData() {
        try {
            if (txtGiaTri.getText().trim().isEmpty()) throw new IllegalArgumentException("Giá trị không được để trống!");
            bus.capNhat(maTS, txtGiaTri.getText(), txtMoTa.getText().trim());
            JOptionPane.showMessageDialog(this, "Cấu hình đã được áp dụng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            return true;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    // --- Modern Component ---
    class ModernTextField extends JTextField {
        private String placeholder;
        public ModernTextField(String placeholder, boolean enabled) {
            this.placeholder = placeholder;
            setEnabled(enabled);
            setFont(new Font("Segoe UI", Font.PLAIN, 14));
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
            g2.setColor(isEnabled() ? Color.WHITE : AppColor.BACKGROUND);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
            super.paintComponent(g);
            if (getText().isEmpty() && !isFocusOwner()) {
                g2.setColor(new Color(180, 180, 180));
                g2.drawString(placeholder, 12, (getHeight() - g.getFontMetrics().getHeight()) / 2 + g.getFontMetrics().getAscent());
            }
            g2.dispose();
        }
        @Override protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(isFocusOwner() ? AppColor.PRIMARY : AppColor.BORDER);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
            g2.dispose();
        }
    }
}