package gui.dialog;

// import bus.NhanVienBUS;
import dto.NhanVienDTO;

import javax.swing.*;
import java.awt.*;

public class NhanVienForm extends JPanel {
    private JTextField txtMaNV, txtTenNV, txtSDT, txtLuong;
    private JComboBox<String> cbChucVu;
    private JComboBox<String> cbTrangThai;
    // private NhanVienBUS bus;
    private String currentMaNV;

    public NhanVienForm(String maNVToEdit) {
        this.currentMaNV = maNVToEdit;
        // this.bus = new NhanVienBUS();
        initComponents();
        if (maNVToEdit != null) {
            loadDataToForm(maNVToEdit);
        }
    }

    private void initComponents() {
        setLayout(new GridLayout(6, 2, 15, 10));
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(500, 350));

        add(new JLabel("Mã NV (Tự động):"));
        txtMaNV = new JTextField();
        txtMaNV.setEnabled(false);
        add(txtMaNV);

        add(new JLabel("Tên nhân viên (*):"));
        txtTenNV = new JTextField();
        add(txtTenNV);

        add(new JLabel("Số điện thoại (*):"));
        txtSDT = new JTextField();
        add(txtSDT);

        // Theo mô tả CSDL, chỉ có 3 chức vụ này[cite: 2]
        add(new JLabel("Chức vụ (*):"));
        cbChucVu = new JComboBox<>(new String[]{"NV thu mua", "NV kho", "NV giao hàng"});
        add(cbChucVu);

        add(new JLabel("Mức lương (VNĐ):"));
        txtLuong = new JTextField();
        add(txtLuong);

        add(new JLabel("Trạng thái tài khoản:"));
        cbTrangThai = new JComboBox<>(new String[]{"Bị khóa", "Hoạt động"});
        cbTrangThai.setSelectedIndex(1); 
        add(cbTrangThai);
    }

    private void loadDataToForm(String maNV) {
        // TODO: Lấy dữ liệu NhanVienDTO và đưa lên form
    }

    public boolean saveData() {
        try {
            // Validate sơ bộ
            if(txtTenNV.getText().trim().isEmpty() || txtSDT.getText().trim().isEmpty()) {
                throw new IllegalArgumentException("Vui lòng điền đầy đủ Tên và Số điện thoại!");
            }
            double luong = 0;
            try {
                if(!txtLuong.getText().trim().isEmpty()) {
                    luong = Double.parseDouble(txtLuong.getText().trim());
                }
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException("Lương phải là kiểu số nguyên/thực!");
            }

            NhanVienDTO nv = new NhanVienDTO();
            nv.setTenNV(txtTenNV.getText().trim());
            nv.setSdt(txtSDT.getText().trim());
            nv.setChucVu(cbChucVu.getSelectedItem().toString());
            nv.setLuong(luong);
            // nv.setTrangThaiTK(cbTrangThai.getSelectedIndex());

            if (currentMaNV == null) {
                // return bus.addNhanVien(nv);
                JOptionPane.showMessageDialog(this, "Đã gọi hàm Thêm (Mock)!");
                return true;
            } else {
                nv.setMaNV(currentMaNV);
                // return bus.updateNhanVien(nv);
                JOptionPane.showMessageDialog(this, "Đã gọi hàm Sửa (Mock)!");
                return true;
            }
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}