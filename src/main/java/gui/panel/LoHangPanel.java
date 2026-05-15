package gui.panel;

import bus.LoHangBUS;
import dto.LoHangDTO;
import gui.dialog.LoHangForm;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class LoHangPanel extends JPanel {

    private JTextField txtMaNCC, txtMaLH, txtMaSP, txtSoLuong;
    private JTable table;
    private LoHangBUS loHangBUS = new LoHangBUS();

    public LoHangPanel() {
        setLayout(new BorderLayout());

        JPanel panel = new JPanel(new BorderLayout());

        // TOP INPUT
        JPanel top = new JPanel(new GridLayout(2, 4));

        txtMaNCC = new JTextField();
        txtMaLH = new JTextField();
        txtMaSP = new JTextField();
        txtSoLuong = new JTextField();

        top.add(new JLabel("Mã NCC"));
        top.add(txtMaNCC);
        top.add(new JLabel("Mã Lô"));
        top.add(txtMaLH);

        top.add(new JLabel("Mã SP"));
        top.add(txtMaSP);
        top.add(new JLabel("Số lượng"));
        top.add(txtSoLuong);

        panel.add(top, BorderLayout.NORTH);

        // TABLE
        table = new JTable(new DefaultTableModel(
                new Object[]{"Mã LH", "Mã NCC", "Trạng thái"}, 0
        ));
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        // BUTTON
        JPanel bottom = new JPanel();

        JButton btnThemMoi = new JButton("Thêm mới");
        JButton btnThemLH = new JButton("Tạo lô");
        JButton btnThemCT = new JButton("Thêm chi tiết");
        JButton btnNhapKho = new JButton("Yêu cầu nhập kho");
        JButton btnLoad = new JButton("Load");

        bottom.add(btnThemMoi);
        bottom.add(btnThemLH);
        bottom.add(btnThemCT);
        bottom.add(btnNhapKho);
        bottom.add(btnLoad);

        panel.add(bottom, BorderLayout.SOUTH);

        add(panel);

        // EVENT

        btnThemMoi.addActionListener(e -> {
            LoHangForm dialog = new LoHangForm(SwingUtilities.getWindowAncestor(this));
            dialog.setVisible(true);
            loadTable();
        });

        btnThemLH.addActionListener(e -> {
            try {
                int maNCC = Integer.parseInt(txtMaNCC.getText());
                if (loHangBUS.themLoHang(maNCC)) {
                    JOptionPane.showMessageDialog(this, "Tạo lô thành công");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Mã NCC phải là số nguyên.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnThemCT.addActionListener(e -> {
            try {
                int maLH = Integer.parseInt(txtMaLH.getText());
                int maSP = Integer.parseInt(txtMaSP.getText());
                int soLuong = Integer.parseInt(txtSoLuong.getText());

                if (loHangBUS.themChiTiet(maLH, maSP, soLuong)) {
                    JOptionPane.showMessageDialog(this, "Thêm chi tiết thành công");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Mã LH, Mã SP và Số lượng phải là số nguyên.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnNhapKho.addActionListener(e -> {
            try {
                int maLH = Integer.parseInt(txtMaLH.getText());
                if (loHangBUS.yeuCauNhapKho(maLH)) {
                    JOptionPane.showMessageDialog(this, "Đã gửi yêu cầu nhập kho");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Mã LH phải là số nguyên.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnLoad.addActionListener(e -> loadTable());
    }

    private void loadTable() {
        List<LoHangDTO> list = loHangBUS.getAll();

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        for (LoHangDTO lh : list) {
            model.addRow(new Object[]{
                    lh.getMaLH(),
                    lh.getMaNCC(),
                    lh.getTrangThaiLH()
            });
        }
    }
}