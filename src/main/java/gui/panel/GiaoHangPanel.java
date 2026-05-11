package gui.panel;

import bus.GiaoHangBUS;
import dto.DonHangDTO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class GiaoHangPanel extends JPanel {

    // =========================
    // TABLE
    // =========================
    private JTable tblChoGiao;
    private JTable tblLichSu;

    // =========================
    // MODEL
    // =========================
    private DefaultTableModel modelChoGiao;
    private DefaultTableModel modelLichSu;

    // =========================
    // BUTTON
    // =========================
    private JButton btnNhanDon;
    private JButton btnThanhCong;
    private JButton btnThatBai;
    private JButton btnReload;

    // =========================
    // BUS
    // =========================
     private GiaoHangBUS bus = new GiaoHangBUS();

    // =========================
    // MÀU SẮC
    // =========================
    private final Color PRIMARY = new Color(46, 125, 50);
    private final Color SUCCESS = new Color(56, 142, 60);
    private final Color DANGER = new Color(211, 47, 47);
    private final Color INFO = new Color(2, 136, 209);
    private final Color BG = new Color(245, 247, 250);
    private final Color TABLE_HEADER = new Color(76, 175, 80);

    public GiaoHangPanel() {

        setLayout(new BorderLayout());
        setBackground(BG);

        // =====================================================
        // TITLE
        // =====================================================
        JLabel lblTitle = new JLabel("PHÂN HỆ GIAO HÀNG");

        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setForeground(PRIMARY);

        JPanel pnlTitle = new JPanel(new BorderLayout());
        pnlTitle.setBackground(BG);
        pnlTitle.setBorder(new EmptyBorder(20, 10, 20, 10));

        pnlTitle.add(lblTitle, BorderLayout.CENTER);

        add(pnlTitle, BorderLayout.NORTH);
         // =====================================================
        // TABPANE
        // =====================================================
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 15));

        // =====================================================
        // PANEL ĐƠN CHỜ GIAO
        // =====================================================
        JPanel pnlChoGiao = new JPanel(new BorderLayout(10, 10));
        pnlChoGiao.setBackground(BG);
        pnlChoGiao.setBorder(new EmptyBorder(15, 15, 15, 15));

        modelChoGiao = new DefaultTableModel(
                new String[]{
                        "Mã ĐH",
                        "Mã KH",
                        "Mã NV",
                        "Địa chỉ giao",
                        "Ngày đặt",
                        "Tổng tiền",
                        "Trạng thái"
                },
                0
        );

        tblChoGiao = new JTable(modelChoGiao);
          customTable(tblChoGiao);

        JScrollPane scrollChoGiao = new JScrollPane(tblChoGiao);
        scrollChoGiao.setBorder(BorderFactory.createLineBorder(new Color(220,220,220)));

        pnlChoGiao.add(scrollChoGiao, BorderLayout.CENTER);

        // =====================================================
        // BUTTON PANEL
        // =====================================================
        JPanel pnlButton = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        pnlButton.setBackground(BG);

        btnNhanDon = createButton("Nhận đơn", INFO);
        btnThanhCong = createButton("Giao thành công", SUCCESS);
        btnThatBai = createButton("Giao thất bại", DANGER);
        btnReload = createButton("Reload", PRIMARY);

        pnlButton.add(btnNhanDon);
        pnlButton.add(btnThanhCong);
        pnlButton.add(btnThatBai);
        pnlButton.add(btnReload);

        pnlChoGiao.add(pnlButton, BorderLayout.SOUTH);
         // =====================================================
        // PANEL LỊCH SỬ
        // =====================================================
        JPanel pnlLichSu = new JPanel(new BorderLayout(10, 10));
        pnlLichSu.setBackground(BG);
        pnlLichSu.setBorder(new EmptyBorder(15, 15, 15, 15));

        modelLichSu = new DefaultTableModel(
                new String[]{
                        "Mã ĐH",
                        "Mã KH",
                        "Mã NV",
                        "Ngày đặt",
                        "Tổng tiền",
                        "Trạng thái"
                },
                0
        );

        tblLichSu = new JTable(modelLichSu);

        customTable(tblLichSu);

        JScrollPane scrollLichSu = new JScrollPane(tblLichSu);
        scrollLichSu.setBorder(BorderFactory.createLineBorder(new Color(220,220,220)));
                pnlLichSu.add(scrollLichSu, BorderLayout.CENTER);

        // =====================================================
        // ADD TAB
        // =====================================================
        tabbedPane.addTab("Đơn chờ giao", pnlChoGiao);
        tabbedPane.addTab("Lịch sử giao hàng", pnlLichSu);

        add(tabbedPane, BorderLayout.CENTER);

        // =====================================================
        // LOAD DATA
        // =====================================================
        loadDanhSachChoGiao();
        loadLichSu();

        // =====================================================
        // EVENT NHẬN ĐƠN
        // =====================================================
        btnNhanDon.addActionListener(e -> {

            int row = tblChoGiao.getSelectedRow();

            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn đơn hàng");
                        return;
            }

            String maDH = modelChoGiao.getValueAt(row, 0).toString();

            String maNV = JOptionPane.showInputDialog(this, "Nhập mã nhân viên:");

            if (maNV == null || maNV.isEmpty()) {
                return;
            }

            boolean result = bus.nhanDonGiao(maDH, maNV);

            if (result) {

                JOptionPane.showMessageDialog(this, "Nhận đơn thành công");
                loadDanhSachChoGiao();

            } else {

                JOptionPane.showMessageDialog(this, "Nhận đơn thất bại");
            }
        });

        // =====================================================
        // EVENT GIAO THÀNH CÔNG
        // =====================================================
        btnThanhCong.addActionListener(e -> {

            int row = tblChoGiao.getSelectedRow();
  if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn đơn hàng");
                return;
            }

            String maDH = modelChoGiao.getValueAt(row, 0).toString();

            String maNV = JOptionPane.showInputDialog(this, "Nhập mã nhân viên:");

            if (maNV == null || maNV.isEmpty()) {
                return;
            }

            boolean result = bus.giaoThanhCong(maDH, maNV);

            if (result) {

                JOptionPane.showMessageDialog(this, "Giao hàng thành công");

                loadDanhSachChoGiao();
                loadLichSu();

            } else {

                JOptionPane.showMessageDialog(this, "Giao hàng thất bại");
            }
        });

        // =====================================================
        // EVENT GIAO THẤT BẠI
        // =====================================================
        btnThatBai.addActionListener(e -> {

            int row = tblChoGiao.getSelectedRow();

            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn đơn hàng");
                return;
            }

            String maDH = modelChoGiao.getValueAt(row, 0).toString();

            boolean result = bus.giaoThatBai(maDH);

            if (result) {

                JOptionPane.showMessageDialog(this, "Cập nhật thất bại thành công");

                loadDanhSachChoGiao();
                loadLichSu();

            } else {

                JOptionPane.showMessageDialog(this, "Cập nhật thất bại thất bại");
            }
        });
// =====================================================
        // EVENT RELOAD
        // =====================================================
        btnReload.addActionListener(e -> {
            loadDanhSachChoGiao();
            loadLichSu();
        });
    }

    // =====================================================
    // STYLE TABLE
    // =====================================================
    private void customTable(JTable table) {

        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setSelectionBackground(new Color(200, 230, 201));
        table.setGridColor(new Color(230,230,230));
        table.setShowGrid(true);

        JTableHeader header = table.getTableHeader();

        header.setBackground(TABLE_HEADER);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setPreferredSize(new Dimension(header.getWidth(), 35));
    }
 // =====================================================
    // STYLE BUTTON
    // =====================================================
    private JButton createButton(String text, Color color) {

        JButton btn = new JButton(text);

        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(170, 40));

        return btn;
    }

    // =====================================================
    // LOAD ĐƠN CHỜ GIAO
    // =====================================================
    private void loadDanhSachChoGiao() {

        modelChoGiao.setRowCount(0);

        List<DonHangDTO> list = bus.layDonChoGiao();

        for (DonHangDTO dh : list) {

            modelChoGiao.addRow(new Object[]{
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

    // =====================================================
    // LOAD LỊCH SỬ
    // =====================================================
    private void loadLichSu() {

        modelLichSu.setRowCount(0);

        List<DonHangDTO> list = bus.lichSuGiaoHang();

        for (DonHangDTO dh : list) {

            modelLichSu.addRow(new Object[]{
                    dh.getMaDH(),
                    dh.getMaKH(),
                                 dh.getMaNV(),
                    dh.getTgDat(),
                    dh.getTongTien(),
                    dh.getTrangThaiDH()
            });
        }
    }
}