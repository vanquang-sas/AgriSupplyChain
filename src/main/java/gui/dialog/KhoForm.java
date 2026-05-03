package gui.dialog;

import bus.KhoBUS;
import dto.KhoDTO;
import util.AppColor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;

public class KhoForm extends JPanel {

    private JTextField txtTenKho, txtDiaChi;
    private JTextArea txtMoTa;
    private JComboBox<String> cbLoaiKho;
    private JLabel lblMaKho;

    private final KhoBUS bus = new KhoBUS();
    private final String currentMaKho; // null = thêm mới

    public KhoForm(String maKhoToEdit) {
        this.currentMaKho = maKhoToEdit;
        initComponents();
        if (maKhoToEdit != null) loadDataToForm(maKhoToEdit);
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(AppColor.SURFACE);
        setPreferredSize(new Dimension(460, currentMaKho == null ? 340 : 370));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(AppColor.SURFACE);
        form.setBorder(new EmptyBorder(12, 24, 12, 24));

        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(7, 4, 7, 4);

        int row = 0;

        // Mã kho (chỉ hiện khi sửa)
        if (currentMaKho != null) {
            addLabel(form, gc, row, "Mã kho:");
            lblMaKho = new JLabel();
            lblMaKho.setFont(new Font("Segoe UI", Font.BOLD, 13));
            lblMaKho.setForeground(AppColor.PRIMARY);
            addField(form, gc, row++, lblMaKho);
        }

        // Tên kho
        addLabel(form, gc, row, "Tên kho (*):");
        txtTenKho = new JTextField();
        styleTextField(txtTenKho);
        addField(form, gc, row++, txtTenKho);

        // Loại kho
        addLabel(form, gc, row, "Loại kho (*):");
        cbLoaiKho = new JComboBox<>(new String[]{"Mát", "Lạnh", "Đông"});
        cbLoaiKho.setBackground(AppColor.SURFACE);
        cbLoaiKho.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        addField(form, gc, row++, cbLoaiKho);

        // Địa chỉ
        addLabel(form, gc, row, "Địa chỉ (*):");
        txtDiaChi = new JTextField();
        styleTextField(txtDiaChi);
        addField(form, gc, row++, txtDiaChi);

        // Mô tả (textarea)
        addLabel(form, gc, row, "Mô tả:");
        txtMoTa = new JTextArea(3, 20);
        txtMoTa.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtMoTa.setLineWrap(true);
        txtMoTa.setWrapStyleWord(true);
        txtMoTa.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppColor.BORDER, 1, true),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        JScrollPane spMoTa = new JScrollPane(txtMoTa);
        spMoTa.setBorder(BorderFactory.createEmptyBorder());
        gc.gridx = 1; gc.gridy = row; gc.weightx = 0.65;
        form.add(spMoTa, gc);

        add(form, BorderLayout.CENTER);
    }

    private void loadDataToForm(String maKho) {
        KhoDTO kho = bus.getById(maKho);
        if (kho == null) return;
        lblMaKho.setText(kho.getMaKho());
        txtTenKho.setText(kho.getTenKho());
        txtDiaChi.setText(kho.getDiaChi() != null ? kho.getDiaChi() : "");
        txtMoTa.setText(kho.getMoTa() != null ? kho.getMoTa() : "");
        // Chọn đúng loại kho
        for (int i = 0; i < cbLoaiKho.getItemCount(); i++) {
            if (cbLoaiKho.getItemAt(i).equals(kho.getLoaiKho())) {
                cbLoaiKho.setSelectedIndex(i);
                break;
            }
        }
    }

    public boolean saveData() {
        try {
            KhoDTO kho = new KhoDTO();
            kho.setTenKho(txtTenKho.getText().trim());
            kho.setLoaiKho(cbLoaiKho.getSelectedItem().toString());
            kho.setDiaChi(txtDiaChi.getText().trim());
            kho.setMoTa(txtMoTa.getText().trim());

            if (currentMaKho == null) {
                bus.them(kho);
                JOptionPane.showMessageDialog(this, "Thêm kho thành công!",
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } else {
                kho.setMaKho(currentMaKho);
                bus.capNhat(kho);
                JOptionPane.showMessageDialog(this, "Cập nhật kho thành công!",
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
            }
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
    private void addLabel(JPanel p, GridBagConstraints gc, int row, String text) {
        gc.gridx = 0; gc.gridy = row; gc.weightx = 0.35;
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(AppColor.TEXT_SECONDARY);
        p.add(lbl, gc);
    }

    private void addField(JPanel p, GridBagConstraints gc, int row, JComponent field) {
        gc.gridx = 1; gc.gridy = row; gc.weightx = 0.65;
        p.add(field, gc);
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