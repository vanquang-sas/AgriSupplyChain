package gui.dialog;

import bus.KhachHangBUS;
import dto.KhachHangDTO;
import util.AppColor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;

public class KhachHangForm extends JPanel {

    private JTextField txtUsername, txtPassword, txtTenKH, txtSDT, txtEmail, txtDiaChi;
    private JComboBox<String> cbLoaiKH;
    private JLabel lblMaKH;

    private final KhachHangBUS bus = new KhachHangBUS();
    private final String currentMaKH; // null = thêm mới, có giá trị = sửa

    public KhachHangForm(String maKHToEdit) {
        this.currentMaKH = maKHToEdit;
        initComponents();
        if (maKHToEdit != null) {
            loadDataToForm(maKHToEdit);
        }
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(AppColor.SURFACE);
        setPreferredSize(new Dimension(480, currentMaKH == null ? 420 : 370));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(AppColor.SURFACE);
        form.setBorder(new EmptyBorder(10, 20, 10, 20));

        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(6, 4, 6, 4);

        int row = 0;

        // Mã KH (chỉ hiện khi sửa)
        if (currentMaKH != null) {
            addLabel(form, gc, row, "Mã khách hàng:");
            lblMaKH = new JLabel();
            lblMaKH.setFont(new Font("Segoe UI", Font.BOLD, 13));
            lblMaKH.setForeground(AppColor.PRIMARY);
            addField(form, gc, row++, lblMaKH);
        }

        // Username (chỉ hiện khi thêm mới)
        if (currentMaKH == null) {
            addLabel(form, gc, row, "Username (*):");
            txtUsername = new JTextField();
            styleTextField(txtUsername);
            addField(form, gc, row++, txtUsername);

            addLabel(form, gc, row, "Mật khẩu (*):");
            txtPassword = new JPasswordField();
            styleTextField((JTextField) txtPassword);
            addField(form, gc, row++, (JTextField) txtPassword);
        }

        // Tên KH
        addLabel(form, gc, row, "Tên khách hàng (*):");
        txtTenKH = new JTextField();
        styleTextField(txtTenKH);
        addField(form, gc, row++, txtTenKH);

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

        // Loại KH
        addLabel(form, gc, row, "Loại khách hàng:");
        cbLoaiKH = new JComboBox<>(new String[]{"Thường", "Thân thiết", "VIP"});
        cbLoaiKH.setBackground(AppColor.SURFACE);
        addField(form, gc, row, cbLoaiKH);

        add(form, BorderLayout.CENTER);
    }

    // Load dữ liệu lên form khi sửa
    private void loadDataToForm(String maKH) {
        KhachHangDTO kh = bus.getById(maKH);
        if (kh == null) return;

        lblMaKH.setText(kh.getMaKH());
        txtTenKH.setText(kh.getTenKH());
        txtSDT.setText(kh.getSdt());
        txtEmail.setText(kh.getEmail() != null ? kh.getEmail() : "");
        txtDiaChi.setText(kh.getDiaChi() != null ? kh.getDiaChi() : "");

        // Chọn đúng loại KH trong combobox
        String loai = kh.getLoaiKH();
        if (loai != null) {
            for (int i = 0; i < cbLoaiKH.getItemCount(); i++) {
                if (cbLoaiKH.getItemAt(i).equals(loai)) {
                    cbLoaiKH.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    // Được gọi từ ModalDialog khi người dùng bấm "Lưu"
    public boolean saveData() {
        try {
            KhachHangDTO kh = new KhachHangDTO();
            kh.setTenKH(txtTenKH.getText().trim());
            kh.setSdt(txtSDT.getText().trim());
            kh.setEmail(txtEmail.getText().trim());
            kh.setDiaChi(txtDiaChi.getText().trim());
            kh.setLoaiKH(cbLoaiKH.getSelectedItem().toString());

            if (currentMaKH == null) {
                // THÊM MỚI
                kh.setUsername(txtUsername.getText().trim());
                String password = ((JPasswordField) txtPassword).getText().trim();
                bus.them(kh, password);
                JOptionPane.showMessageDialog(this, "Thêm khách hàng thành công!",
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } else {
                // CẬP NHẬT
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

    // ===================== HELPER =====================
    private void addLabel(JPanel panel, GridBagConstraints gc, int row, String text) {
        gc.gridx = 0; gc.gridy = row; gc.weightx = 0.35;
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(AppColor.TEXT_SECONDARY);
        panel.add(lbl, gc);
    }

    private void addField(JPanel panel, GridBagConstraints gc, int row, JComponent field) {
        gc.gridx = 1; gc.gridy = row; gc.weightx = 0.65;
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