package gui.dialog;

import bus.LoHangBUS;
import bus.NhaCungCapBUS; // dùng để load danh sách NCC
import dto.NhaCungCapDTO;

import javax.swing.*;
import java.awt.*;
import java.util.Date;
import java.util.List;

public class LoHangForm extends JDialog {

    private final LoHangBUS loHangBUS;
    private boolean success = false;

    private JComboBox<String> cboNCC;
    private com.toedter.calendar.JDateChooser dateNgayLap;
    private JButton btnTao, btnHuy;

    public LoHangForm(Frame parent, LoHangBUS loHangBUS) {
        super(parent, "Tạo lô hàng mới", true);
        this.loHangBUS = loHangBUS;
        initComponents();
        loadNhaCungCap();
        setSize(400, 220);
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Nhà cung cấp
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Nhà cung cấp: *"), gbc);
        cboNCC = new JComboBox<>();
        gbc.gridx = 1;
        add(cboNCC, gbc);

        // Ngày lập (dùng JDateChooser từ jcalendar)
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Ngày lập: *"), gbc);
        dateNgayLap = new com.toedter.calendar.JDateChooser();
        dateNgayLap.setDate(new Date());
        gbc.gridx = 1;
        add(dateNgayLap, gbc);

        // Nút
        JPanel panelBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnTao = new JButton("Tạo lô hàng");
        btnHuy = new JButton("Hủy");
        panelBtn.add(btnTao);
        panelBtn.add(btnHuy);

        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        add(panelBtn, gbc);

        // Sự kiện
        btnTao.addActionListener(e -> onTaoLoHang());
        btnHuy.addActionListener(e -> dispose());
    }

    private void loadNhaCungCap() {
        try {
            NhaCungCapBUS nccBUS = new NhaCungCapBUS();
            List<NhaCungCapDTO> list = nccBUS.getAllNhaCungCap();
            for (NhaCungCapDTO ncc : list) {
                cboNCC.addItem(ncc.getMaNCC() + " - " + ncc.getTenNCC());
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Không tải được danh sách nhà cung cấp: " + ex.getMessage());
        }
    }

    private void onTaoLoHang() {
        try {
            // Lấy mã NCC từ ComboBox (cắt lấy phần trước " - ")
            String selected = (String) cboNCC.getSelectedItem();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn nhà cung cấp!");
                return;
            }
            String maNCC = selected.split(" - ")[0];
            java.util.Date ngayLap = dateNgayLap.getDate();

            String maLoHang = loHangBUS.themLoHang(maNCC, ngayLap);
            JOptionPane.showMessageDialog(this,
                    "Tạo lô hàng thành công!\nMã lô hàng: " + maLoHang);
            success = true;
            dispose();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(), "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi hệ thống: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSuccess() { return success; }
}