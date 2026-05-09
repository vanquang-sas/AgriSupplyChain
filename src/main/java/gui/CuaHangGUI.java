
package gui;

import bus.CuaHangBUS;
import dto.SanPhamDTO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class CuaHangGUI extends JPanel {

    // =========================================
    // BUS
    // =========================================locTheoLoai
    private CuaHangBUS bus = new CuaHangBUS();

    // =========================================
    // TABLE
    // =========================================
    private JTable tblSanPham;
    private DefaultTableModel model;

    // =========================================
    // COMPONENT
    // =========================================
    private JTextField txtTimKiem;
    private JComboBox<String> cboLoai;

    // private JButton btnTimKiem;
    private JButton btnReload;
    private JButton btnThemGio;

    // =========================================
    // COLOR
    // =========================================
    private final Color PRIMARY = new Color(46, 125, 50);
    private final Color BG = new Color(245, 247, 250);
    private final Color INFO = new Color(2, 136, 209);
    private final Color HEADER = new Color(76, 175, 80);

    public CuaHangGUI() {

        setLayout(new BorderLayout());
        setBackground(BG);

        // =========================================
        // TITLE
        // =========================================
        JLabel lblTitle = new JLabel("CỬA HÀNG SẢN PHẨM");

        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(PRIMARY);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel pnlTitle = new JPanel(new BorderLayout());

        pnlTitle.setBackground(BG);
        pnlTitle.setBorder(new EmptyBorder(20, 10, 20, 10));

        pnlTitle.add(lblTitle, BorderLayout.CENTER);

        add(pnlTitle, BorderLayout.NORTH);

        // =========================================
        // CENTER PANEL
        // =========================================
        JPanel pnlCenter = new JPanel(new BorderLayout(10, 10));

        pnlCenter.setBackground(BG);
        pnlCenter.setBorder(new EmptyBorder(10, 20, 20, 20));

        add(pnlCenter, BorderLayout.CENTER);

        // =========================================
        // SEARCH PANEL
        // =========================================
        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));

        pnlSearch.setBackground(BG);

        JLabel lblTim = new JLabel("Tìm kiếm:");

        lblTim.setFont(new Font("Segoe UI", Font.BOLD, 14));

        txtTimKiem = new JTextField();

        txtTimKiem.setPreferredSize(new Dimension(220, 35));
        txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JLabel lblLoai = new JLabel("Loại sản phẩm:");

        lblLoai.setFont(new Font("Segoe UI", Font.BOLD, 14));

        cboLoai = new JComboBox<>();

        cboLoai.setPreferredSize(new Dimension(180, 35));
        cboLoai.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        cboLoai.addItem("Tất cả");
        cboLoai.addItem("LSP00001");
        cboLoai.addItem("LSP00002");
        cboLoai.addItem("LSP00003");
        cboLoai.addItem("LSP00004");
        cboLoai.addItem("LSP00005");

        // btnTimKiem = createButton("Tìm kiếm", INFO);

        btnReload = createButton("Reload", PRIMARY);

        pnlSearch.add(lblTim);
        pnlSearch.add(txtTimKiem);

        pnlSearch.add(lblLoai);
        pnlSearch.add(cboLoai);

        // pnlSearch.add(btnTimKiem);
        pnlSearch.add(btnReload);

        pnlCenter.add(pnlSearch, BorderLayout.NORTH);

        // =========================================
        // TABLE
        // =========================================
        model = new DefaultTableModel(
                new String[]{
                        "Mã SP",
                        "Tên sản phẩm",
                        "Loại SP",
                        "Giá bán",
                },
                0
        );

        tblSanPham = new JTable(model);

        customTable(tblSanPham);

        JScrollPane scrollPane = new JScrollPane(tblSanPham);

        scrollPane.setBorder(
                BorderFactory.createLineBorder(new Color(220,220,220))
        );

        pnlCenter.add(scrollPane, BorderLayout.CENTER);

        // =========================================
        // BUTTON PANEL
        // =========================================
        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        pnlBottom.setBackground(BG);

        btnThemGio = createButton("Thêm vào giỏ hàng", PRIMARY);

        pnlBottom.add(btnThemGio);

        pnlCenter.add(pnlBottom, BorderLayout.SOUTH);

        // =========================================
        // LOAD DATA
        // =========================================
        loadData(bus.getAllSanPham());

        // =========================================
        // REALTIME SEARCH
        // =========================================
        txtTimKiem.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                filterSanPham();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filterSanPham();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filterSanPham();
            }
        });
        cboLoai.addActionListener(e -> {
            filterSanPham();
        });

        // =========================================
        // EVENT RELOAD
        // =========================================
        btnReload.addActionListener(e -> {

            txtTimKiem.setText("");

            cboLoai.setSelectedIndex(0);

            loadData(bus.getAllSanPham());
        });

        // =========================================
        // EVENT THÊM GIỎ HÀNG
        // =========================================
        btnThemGio.addActionListener(e -> {

            int row = tblSanPham.getSelectedRow();

            if (row == -1) {

                JOptionPane.showMessageDialog(
                        this,"Vui lòng chọn sản phẩm"
                );

                return;
            }

            String maSP = model.getValueAt(row, 0).toString();

            String tenSP = model.getValueAt(row, 1).toString();

            JOptionPane.showMessageDialog(
                    this,
                    "Đã thêm vào giỏ hàng:\n" + maSP + " - " + tenSP
            );
        });


    tblSanPham.addMouseListener(new java.awt.event.MouseAdapter() {

        @Override
        public void mouseClicked(java.awt.event.MouseEvent e) {

            if (e.getClickCount() == 2) {

                int row = tblSanPham.getSelectedRow();

                String maSP = model.getValueAt(row,0).toString();

                SanPhamDTO sp = bus.getById(maSP);
                ChiTietSanPhamGUI dialog = new ChiTietSanPhamGUI(null, sp);

                dialog.setVisible(true);
            }
        }
    });
    }
        // =========================================
    // LOAD DATA
    // =========================================
    private void loadData(List<SanPhamDTO> list) {

        model.setRowCount(0);

        for (SanPhamDTO sp : list) {

            model.addRow(new Object[]{
                    sp.getMaSP(),
                    sp.getTenSP(),
                    sp.getMaLSP(),
                    sp.getGiaBan()
            });
        }
    }

    // =========================================
    // CUSTOM TABLE
    // =========================================
    private void customTable(JTable table) {

        table.setRowHeight(32);

        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        table.setSelectionBackground(new Color(200, 230, 201));

        table.setGridColor(new Color(230,230,230));

        JTableHeader header = table.getTableHeader();

        header.setBackground(HEADER);

        header.setForeground(Color.WHITE);

        header.setFont(new Font("Segoe UI", Font.BOLD, 14));

        header.setPreferredSize(new Dimension(header.getWidth(), 35));
    }

    // =========================================
    // CUSTOM BUTTON
    // =========================================
    private JButton createButton(String text, Color color) {

        JButton btn = new JButton(text);

        btn.setBackground(color);

        btn.setForeground(Color.WHITE);

        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));

        btn.setFocusPainted(false);

        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.setPreferredSize(new Dimension(170, 40));

        return btn;
    }
    // =========================================
    // FILTER REALTIME
    // =========================================
    private void filterSanPham() {

        String keyword = txtTimKiem.getText().trim();

        String loai = cboLoai.getSelectedItem().toString();

        // ===============================
        // TÌM KIẾM + LỌC
        // ===============================
        if (!keyword.isEmpty()) {

            List<SanPhamDTO> list = bus.timKiem(keyword);

            // nếu có chọn loại
            if (!loai.equals("Tất cả")) {

                list.removeIf(sp ->
                        !sp.getMaLSP().equals(loai)
                );
            }

            loadData(list);
        }

        // ===============================
        // CHỈ LỌC LOẠI
        // ===============================
        else if (!loai.equals("Tất cả")) {

            loadData(bus.locTheoLoai(loai));
        }

        // ===============================
        // HIỂN THỊ TẤT CẢ
        // ===============================
        else {

            loadData(bus.getAllSanPham());
        }
    }

}