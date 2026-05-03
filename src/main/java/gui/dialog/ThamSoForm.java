package gui.dialog;

import bus.ThamSoBUS;
import dto.ThamSoDTO;
import util.AppColor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;

public class ThamSoForm extends JPanel {

    private JTextField txtGiaTri;
    private JTextArea txtMoTa;

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
        setLayout(new BorderLayout());
        setBackground(AppColor.SURFACE);
        setPreferredSize(new Dimension(440, 280));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(AppColor.SURFACE);
        form.setBorder(new EmptyBorder(12, 24, 12, 24));

        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(8, 4, 8, 4);

        // Tên tham số (readonly - chỉ hiển thị)
        gc.gridx = 0; gc.gridy = 0; gc.weightx = 0.35;
        JLabel lblTen = new JLabel("Tham số:");
        lblTen.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblTen.setForeground(AppColor.TEXT_SECONDARY);
        form.add(lblTen, gc);

        gc.gridx = 1; gc.gridy = 0; gc.weightx = 0.65;
        JLabel lblTenVal = new JLabel(tenTS);
        lblTenVal.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTenVal.setForeground(AppColor.TEXT_PRIMARY);
        form.add(lblTenVal, gc);

        // Hint đơn vị tuỳ theo tên tham số
        gc.gridx = 0; gc.gridy = 1; gc.weightx = 0.35;
        String labelGiaTri = buildLabelGiaTri(tenTS);
        JLabel lblGiaTri = new JLabel(labelGiaTri);
        lblGiaTri.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblGiaTri.setForeground(AppColor.TEXT_SECONDARY);
        form.add(lblGiaTri, gc);

        gc.gridx = 1; gc.gridy = 1; gc.weightx = 0.65;
        txtGiaTri = new JTextField(ts != null ? formatGiaTri(ts) : "");
        styleTextField(txtGiaTri);
        form.add(txtGiaTri, gc);

        // Hint nhỏ dưới ô giá trị
        gc.gridx = 1; gc.gridy = 2; gc.weightx = 0.65;
        JLabel lblHint = new JLabel(buildHint(tenTS));
        lblHint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblHint.setForeground(AppColor.TEXT_SECONDARY);
        form.add(lblHint, gc);

        // Mô tả
        gc.gridx = 0; gc.gridy = 3; gc.weightx = 0.35;
        JLabel lblMoTa = new JLabel("Mô tả:");
        lblMoTa.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblMoTa.setForeground(AppColor.TEXT_SECONDARY);
        form.add(lblMoTa, gc);

        gc.gridx = 1; gc.gridy = 3; gc.weightx = 0.65;
        txtMoTa = new JTextArea(3, 20);
        txtMoTa.setText(ts != null && ts.getMoTa() != null ? ts.getMoTa() : "");
        txtMoTa.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtMoTa.setLineWrap(true);
        txtMoTa.setWrapStyleWord(true);
        txtMoTa.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppColor.BORDER, 1, true),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        form.add(new JScrollPane(txtMoTa) {{ setBorder(BorderFactory.createEmptyBorder()); }}, gc);

        add(form, BorderLayout.CENTER);
    }

    public boolean saveData() {
        try {
            bus.capNhat(maTS, txtGiaTri.getText(), txtMoTa.getText().trim());
            JOptionPane.showMessageDialog(this, "Cập nhật tham số thành công!",
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
            return true;
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
            return false;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi hệ thống: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    // ===================== HELPER =====================
    private String buildLabelGiaTri(String tenTS) {
        switch (tenTS) {
            case "DON_GIA_VANCHUYEN":   return "Đơn giá (VNĐ/km) (*):";
            case "MIN_TONKHO":          return "Số lượng tối thiểu (*):";
            case "CANHBAO_HETHAN":      return "Số ngày cảnh báo (*):";
            case "MAX_TG_THANHTOAN":    return "Thời gian (giờ) (*):";
            case "GG_THUONG":
            case "GG_THANTHIET":
            case "GG_VIP":              return "Tỷ lệ giảm giá (*):";
            default:                    return "Giá trị (*):";
        }
    }

    private String buildHint(String tenTS) {
        switch (tenTS) {
            case "DON_GIA_VANCHUYEN":   return "Ví dụ: 20000 (= 20,000 VNĐ mỗi km)";
            case "MIN_TONKHO":          return "Ví dụ: 10 (cảnh báo khi còn < 10 đơn vị)";
            case "CANHBAO_HETHAN":      return "Ví dụ: 7 (cảnh báo trước 7 ngày hết hạn)";
            case "MAX_TG_THANHTOAN":    return "Ví dụ: 24 (huỷ đơn sau 24 giờ chưa thanh toán)";
            case "GG_THUONG":
            case "GG_THANTHIET":
            case "GG_VIP":              return "Ví dụ: 0.05 (= 5% giảm giá)";
            default:                    return "";
        }
    }

    private String formatGiaTri(ThamSoDTO ts) {
        double v = ts.getGiaTri();
        // Nếu là số nguyên thì không hiển thị phần thập phân
        if (v == Math.floor(v) && !Double.isInfinite(v)) {
            return String.valueOf((long) v);
        }
        return String.valueOf(v);
    }

    private void styleTextField(JTextField tf) {
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppColor.BORDER, 1, true),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        tf.setBackground(AppColor.SURFACE);
        tf.setForeground(AppColor.TEXT_PRIMARY);
    }
}