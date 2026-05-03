package gui.dialog;

import bus.NhaCungCapBUS;
import dto.NhaCungCapDTO;
import util.AppColor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;

public class NhaCungCapForm extends JPanel {

    private JTextField txtTenNCC, txtSDT, txtEmail, txtDiaChi, txtChungNhan;
    private JLabel lblMaNCC;

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
        setLayout(new BorderLayout());
        setBackground(AppColor.SURFACE);
        setPreferredSize(new Dimension(480, currentMaNCC == null ? 350 : 380));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(AppColor.SURFACE);
        form.setBorder(new EmptyBorder(10, 20, 10, 20));

        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(6, 4, 6, 4);

        int row = 0;

        // Mã NCC (chỉ hiện khi sửa)
        if (currentMaNCC != null) {
            addLabel(form, gc, row, "Mã nhà cung cấp:");
            lblMaNCC = new JLabel();
            lblMaNCC.setFont(new Font("Segoe UI", Font.BOLD, 13));
            lblMaNCC.setForeground(AppColor.PRIMARY);
            addField(form, gc, row++, lblMaNCC);
        }

        // Tên NCC
        addLabel(form, gc, row, "Tên nhà cung cấp (*):");
        txtTenNCC = new JTextField();
        styleTextField(txtTenNCC);
        addField(form, gc, row++, txtTenNCC);

        // SĐT
        addLabel(form, gc, row, "Số điện thoại (*):");
        txtSDT = new JTextField();
        styleTextField(txtSDT);
        addField(form, gc, row++, txtSDT);

        // Email
        addLabel(form, gc, row, "Email:");
        txtEmail = new JTextField();
        styleTextField(txtEmail);
        addField(form, gc, row++, txtEmail);

        // Địa chỉ
        addLabel(form, gc, row, "Địa chỉ (*):");
        txtDiaChi = new JTextField();
        styleTextField(txtDiaChi);
        addField(form, gc, row++, txtDiaChi);

        // Chứng nhận CL
        addLabel(form, gc, row, "Chứng nhận chất lượng:");
        txtChungNhan = new JTextField();
        styleTextField(txtChungNhan);
        addField(form, gc, row, txtChungNhan);

        add(form, BorderLayout.CENTER);
    }

    private void loadDataToForm(String maNCC) {
        NhaCungCapDTO ncc = bus.getById(maNCC);
        if (ncc == null) return;

        lblMaNCC.setText(ncc.getMaNCC());
        txtTenNCC.setText(ncc.getTenNCC());
        txtSDT.setText(ncc.getSdt());
        txtEmail.setText(ncc.getEmail() != null ? ncc.getEmail() : "");
        txtDiaChi.setText(ncc.getDiaChi() != null ? ncc.getDiaChi() : "");
        txtChungNhan.setText(ncc.getChungNhanCL() != null ? ncc.getChungNhanCL() : "");
    }

    public boolean saveData() {
        try {
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

    // ===================== HELPER =====================
    private void addLabel(JPanel panel, GridBagConstraints gc, int row, String text) {
        gc.gridx = 0; gc.gridy = row; gc.weightx = 0.38;
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(AppColor.TEXT_SECONDARY);
        panel.add(lbl, gc);
    }

    private void addField(JPanel panel, GridBagConstraints gc, int row, JComponent field) {
        gc.gridx = 1; gc.gridy = row; gc.weightx = 0.62;
        panel.add(field, gc);
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