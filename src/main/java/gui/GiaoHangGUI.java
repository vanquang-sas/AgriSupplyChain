package gui;

import bus.GiaoHangBUS;
import dto.DonHangDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class GiaoHangGUI extends JFrame {

    private JTable tableChoGiao, tableLichSu;
    private DefaultTableModel modelChoGiao, modelLichSu;
    private GiaoHangBUS bus = new GiaoHangBUS();

    public GiaoHangGUI() {
        setTitle("Quản lý giao hàng");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // ===== TABLE CHỜ GIAO =====
        modelChoGiao = new DefaultTableModel(new String[]{"Mã DH", "Trạng thái"}, 0);
        tableChoGiao = new JTable(modelChoGiao);

        // ===== TABLE LỊCH SỬ =====
        modelLichSu = new DefaultTableModel(new String[]{"Mã DH", "Trạng thái"}, 0);
        tableLichSu = new JTable(modelLichSu);

        // ===== BUTTON =====
        JButton btnNhan = new JButton("Nhận đơn");
        JButton btnThanhCong = new JButton("Giao thành công");
        JButton btnThatBai = new JButton("Giao thất bại");

        // ===== PANEL BUTTON =====
        JPanel panelBtn = new JPanel();
        panelBtn.add(btnNhan);
        panelBtn.add(btnThanhCong);
        panelBtn.add(btnThatBai);

        // ===== LAYOUT =====
        setLayout(new BorderLayout());

        add(new JScrollPane(tableChoGiao), BorderLayout.NORTH);
        add(panelBtn, BorderLayout.CENTER);
        add(new JScrollPane(tableLichSu), BorderLayout.SOUTH);

        // ===== LOAD DATA =====
        loadChoGiao();
        loadLichSu();

        // ===== EVENT =====

        // Nhận đơn
        btnNhan.addActionListener(e -> {
            int row = tableChoGiao.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Chọn đơn trước!");
                return;
            }

            String maDH = modelChoGiao.getValueAt(row, 0).toString();
            boolean kq = bus.nhanDonGiao(maDH, "NV001");

            JOptionPane.showMessageDialog(this, "Kết quả: " + kq);
            loadChoGiao();
        });

        // Giao thành công
        btnThanhCong.addActionListener(e -> {
            int row = tableChoGiao.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Chọn đơn trước!");
                return;
            }

            String maDH = modelChoGiao.getValueAt(row, 0).toString();
            boolean kq = bus.giaoThanhCong(maDH, "NV001");

            JOptionPane.showMessageDialog(this, "Kết quả: " + kq);
            loadChoGiao();
            loadLichSu();
        });

        // Giao thất bại
        btnThatBai.addActionListener(e -> {
            int row = tableChoGiao.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Chọn đơn trước!");
                return;
            }

            String maDH = modelChoGiao.getValueAt(row, 0).toString();
            boolean kq = bus.giaoThatBai(maDH);

            JOptionPane.showMessageDialog(this, "Kết quả: " + kq);
            loadChoGiao();
            loadLichSu();
        });
    }

    // ===== LOAD DANH SÁCH CHỜ GIAO =====
    private void loadChoGiao() {
        modelChoGiao.setRowCount(0);
        List<DonHangDTO> list = bus.layDonChoGiao();

        for (DonHangDTO dh : list) {
            modelChoGiao.addRow(new Object[]{
                    dh.getMaDH(),
                    dh.getTrangThaiDH()
            });
        }
    }

    // ===== LOAD LỊCH SỬ =====
    private void loadLichSu() {
        modelLichSu.setRowCount(0);
        List<DonHangDTO> list = bus.lichSuGiaoHang();

        for (DonHangDTO dh : list) {
            modelLichSu.addRow(new Object[]{
                    dh.getMaDH(),
                    dh.getTrangThaiDH()
            });
        }
    }

    // ===== MAIN =====
    public static void main(String[] args) {
        new GiaoHangGUI().setVisible(true);
    }
}