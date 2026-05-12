
package gui.panel;

import bus.CuaHangBUS;
import dto.SanPhamDTO;
import gui.component.RoundedButton;
import gui.dialog.ChiTietSanPhamForm;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import util.AppColor;

public class CuaHangPanel extends JPanel {

    private String maKH;

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

    public CuaHangPanel(String maKH) {

        this.maKH = maKH;

        setLayout(new BorderLayout());
        setBackground(AppColor.BACKGROUND);

        // =========================================
        // TITLE
        // =========================================
        JLabel lblTitle = new JLabel("CỬA HÀNG SẢN PHẨM");

        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(AppColor.TEXT_PRIMARY);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel pnlTitle = new JPanel(new BorderLayout());
        pnlTitle.setBackground(AppColor.BACKGROUND);
        pnlTitle.setBorder(new EmptyBorder(20, 10, 20, 10));
        pnlTitle.add(lblTitle, BorderLayout.CENTER);
        add(pnlTitle, BorderLayout.NORTH);

        // =========================================
        // CENTER PANEL
        // =========================================
        JPanel pnlCenter = new JPanel(new BorderLayout(10, 10));
        pnlCenter.setBackground(AppColor.BACKGROUND);
        pnlCenter.setBorder(new EmptyBorder(10, 20, 20, 20));
        add(pnlCenter, BorderLayout.CENTER);

        // =========================================
        // SEARCH PANEL
        // =========================================
        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        pnlSearch.setBackground(AppColor.BACKGROUND);
        JLabel lblTim = new JLabel("Tìm kiếm:");
        lblTim.setFont(new Font("Segoe UI", Font.BOLD, 14));

        txtTimKiem = new JTextField();
        txtTimKiem.setPreferredSize(new Dimension(220, 35));
        txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        txtTimKiem.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
        BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        txtTimKiem.setBackground(Color.WHITE);
        txtTimKiem.setForeground(Color.DARK_GRAY);
        txtTimKiem.setCaretColor(AppColor.PRIMARY);
        


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

        

        // btnReload = createButton("Reload", AppColor.PRIMARY);
        btnReload = new RoundedButton("Reload", AppColor.PRIMARY);

        pnlSearch.add(lblTim);
        pnlSearch.add(txtTimKiem);

        pnlSearch.add(lblLoai);
        pnlSearch.add(cboLoai);

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

        pnlBottom.setBackground(AppColor.BACKGROUND);

        // btnThemGio = createButton("Thêm vào giỏ hàng", AppColor.PRIMARY);
        btnThemGio = new RoundedButton("Thêm vào giỏ hàng", AppColor.PRIMARY);

        pnlBottom.add(btnThemGio);

        pnlCenter.add(pnlBottom, BorderLayout.SOUTH);

        // =========================================
        // LOAD DATA
        // =========================================
        loadData(bus.getAllSanPham(maKH));

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

            loadData(bus.getAllSanPham(maKH));
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

                SanPhamDTO sp = bus.getById(maSP,maKH);
                ChiTietSanPhamForm dialog = new ChiTietSanPhamForm(null, sp);

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

        // ======================
        // BASIC STYLE
        // ======================
        table.setRowHeight(38);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));

        table.setSelectionBackground(new Color(46, 204, 113));
        table.setSelectionForeground(Color.WHITE);

        table.setFocusable(false);

        // ======================
        // HEADER STYLE + IN HOA TIÊU ĐỀ
        // ======================
        JTableHeader header = table.getTableHeader();

        header.setDefaultRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(
                    JTable table, Object value,
                    boolean isSelected, boolean hasFocus,
                    int row, int column) {

                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                setBackground(new Color(245, 245, 245));
                setForeground(AppColor.TEXT_PRIMARY);
                setFont(new Font("Segoe UI", Font.BOLD, 14));
                setHorizontalAlignment(CENTER);

                // IN HOA TIÊU ĐỀ
                if (value != null) {
                    setText(value.toString().toUpperCase());
                }

                return this;
            }
        });

        header.setPreferredSize(new Dimension(header.getWidth(), 40));

        // ======================
        // CELL RENDERER (DỮ LIỆU KHÔNG IN HOA)
        // ======================
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {

            @Override
            public java.awt.Component getTableCellRendererComponent(
                    JTable table, Object value,
                    boolean isSelected, boolean hasFocus,
                    int row, int column) {

                super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                // ======================
                // ZEBRA ROW
                // ======================
                if (isSelected) {
                    setBackground(AppColor.PRIMARY);
                    setForeground(Color.WHITE);
                } else {
                    setBackground(row % 2 == 0
                            ? Color.WHITE
                            : new Color(245, 245, 245));
                    setForeground(Color.BLACK);
                }

                setFont(new Font("Segoe UI", Font.PLAIN, 14));
                // ======================
                // CHỈ MÃ SP (CỘT 0) -> IN ĐẬM
                // ======================
                if (column == 0) {
                    setFont(new Font("Segoe UI", Font.BOLD, 14));
                }

                setBorder(noFocusBorder);

                return this;
            }
        });
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

            List<SanPhamDTO> list = bus.timKiem(keyword,maKH);

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

            loadData(bus.locTheoLoai(loai,maKH));
        }

        // ===============================
        // HIỂN THỊ TẤT CẢ
        // ===============================
        else {

            loadData(bus.getAllSanPham(maKH));
        }
    }

}