package gui;

import bus.GiaoHangBUS;
import dto.DonHangDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class GiaoHangGUI extends JPanel {

    private JTable tblDonHang;
    private DefaultTableModel model;

    private JButton btnNhanDon;
    private JButton btnThanhCong;
    private JButton btnThatBai;
    private JButton btnReload;

    private GiaoHangBUS bus = new GiaoHangBUS();

    public GiaoHangGUI() {

        setLayout(new BorderLayout());

        // =========================================
        // TITLE
        // =========================================
        JLabel lblTitle = new JLabel("PHÂN HỆ GIAO HÀNG");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);

        add(lblTitle, BorderLayout.NORTH);

        // =========================================
        // TABLE
        // =========================================
        model = new DefaultTableModel(new String[]{"Mã ĐH", "Mã KH", "Mã NV", "Địa chỉ","Ngày đặt","Tổng tiền","Trạng thái"},0);

        tblDonHang = new JTable(model);

        JScrollPane scrollPane = new JScrollPane(tblDonHang);

        add(scrollPane, BorderLayout.CENTER);

        // =========================================
        // BUTTON PANEL
        // =========================================
        JPanel pnlButton = new JPanel();

        btnNhanDon = new JButton("Nhận đơn");
        btnThanhCong = new JButton("Giao thành công");
        btnThatBai = new JButton("Giao thất bại");
        btnReload = new JButton("Reload");

        pnlButton.add(btnNhanDon);
        pnlButton.add(btnThanhCong);
        pnlButton.add(btnThatBai);
        pnlButton.add(btnReload);

        add(pnlButton, BorderLayout.SOUTH);

        // =========================================
        // LOAD DATA
        // =========================================
        loadDanhSachChoGiao();

        // =========================================
        // EVENT NHẬN ĐƠN
        // =========================================
        btnNhanDon.addActionListener(e -> {

            int row = tblDonHang.getSelectedRow();

            if (row == -1) {
                JOptionPane.showMessageDialog(this,
                        "Vui lòng chọn đơn hàng");
                return;
            }

            String maDH = model.getValueAt(row, 0).toString();

            String maNV = JOptionPane.showInputDialog(
                    this,
                    "Nhập mã nhân viên giao hàng:"
            );

            if (maNV == null || maNV.isEmpty()) {
                return;
            }

            boolean kq = bus.nhanDonGiao(maDH, maNV);

            if (kq) {

                JOptionPane.showMessageDialog(this,
                        "Nhận đơn thành công");

                loadDanhSachChoGiao();

            } else {

                JOptionPane.showMessageDialog(this,
                        "Nhận đơn thất bại");
            }
        });

        // =========================================
        // EVENT GIAO THÀNH CÔNG
        // =========================================
        btnThanhCong.addActionListener(e -> {

            int row = tblDonHang.getSelectedRow();

            if (row == -1) {

                JOptionPane.showMessageDialog(this,
                        "Vui lòng chọn đơn hàng");

                return;
            }

            String maDH = model.getValueAt(row, 0).toString();

            String maNV = JOptionPane.showInputDialog(
                    this,
                    "Nhập mã nhân viên:"
            );

            if (maNV == null || maNV.isEmpty()) {
                return;
            }

            boolean kq = bus.giaoThanhCong(maDH, maNV);

            if (kq) {

                JOptionPane.showMessageDialog(this,
                        "Giao hàng thành công");

                loadDanhSachChoGiao();

            } else {

                JOptionPane.showMessageDialog(this,
                        "Giao hàng thất bại");
            }
        });

        // =========================================
        // EVENT GIAO THẤT BẠI
        // =========================================
        btnThatBai.addActionListener(e -> {

            int row = tblDonHang.getSelectedRow();

            if (row == -1) {

                JOptionPane.showMessageDialog(this,
                        "Vui lòng chọn đơn hàng");

                return;
            }

            String maDH = model.getValueAt(row, 0).toString();

            boolean kq = bus.giaoThatBai(maDH);

            if (kq) {

                JOptionPane.showMessageDialog(this,
                        "Đã cập nhật thất bại");

                loadDanhSachChoGiao();

            } else {

                JOptionPane.showMessageDialog(this,
                        "Cập nhật thất bại");
            }
        });

        // =========================================
        // EVENT RELOAD
        // =========================================
        btnReload.addActionListener(e -> {
            loadDanhSachChoGiao();
        });
    }

    // =====================================================
    // LOAD DANH SÁCH ĐƠN CHỜ GIAO
    // =====================================================
    private void loadDanhSachChoGiao() {

        model.setRowCount(0);

        List<DonHangDTO> list = bus.layDonChoGiao();

        for (DonHangDTO dh : list) {

            model.addRow(new Object[]{
                    dh.getMaDH(),
                    dh.getMaKH(),
                    dh.getMaNV(),
                    dh.getDiaChiGiaoHang(),
                    dh.getTgDat(),
                    dh.getTongTien(),
                    dh.getTrangThaiDH()
            });
        }
    }
}