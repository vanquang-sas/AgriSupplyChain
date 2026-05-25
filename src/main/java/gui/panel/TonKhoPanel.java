package gui.panel;

import bus.TonKhoBUS;
import bus.ThamSoBUS;
import com.formdev.flatlaf.FlatClientProperties;
import util.AppColor;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

public class TonKhoPanel extends JPanel {

    // Màu overlay cho hàng được chọn (giống NhapKhoPanel)
    private static final Color PRIMARY_LIGHT = new Color(
            AppColor.PRIMARY.getRed(), AppColor.PRIMARY.getGreen(), AppColor.PRIMARY.getBlue(), 30);
    private static final Color WARNING_LIGHT = new Color(
            AppColor.WARNING.getRed(), AppColor.WARNING.getGreen(), AppColor.WARNING.getBlue(), 30);
    private static final Color ERROR_LIGHT = new Color(
            AppColor.ERROR.getRed(), AppColor.ERROR.getGreen(), AppColor.ERROR.getBlue(), 30);

    private JTable table;
    private DefaultTableModel tableModel;
    private TonKhoBUS tonKhoBUS = new TonKhoBUS();
    private ThamSoBUS thamSoBUS = new ThamSoBUS();
    private double minTonKho = 10.0;
    private ModernSearchField txtSearch;
    private OutlineButton btnSort;
    private String currentSortOpt = "Sắp xếp: Mới nhất";

    // ================== CÁC BIẾN CHO PHÂN TRANG ==================
    private List<Object[]> originalData = new ArrayList<>();
    private List<Object[]> currentData = new ArrayList<>();
    private int currentPage = 1;
    private final int rowsPerPage = 8;
    private int totalPages = 1;
    private JPanel paginationPanel;
    private SummaryCard cardTotal;
    private SummaryCard cardLowStock;
    private SummaryCard cardOutOfStock;
    // =============================================================

    public TonKhoPanel() {
        initComponents();
        loadDataToTable(false);
    }

    private void initComponents() {
        this.setLayout(new BorderLayout(15, 15));
        this.setBackground(AppColor.BACKGROUND);
        this.setBorder(new EmptyBorder(24, 28, 24, 28));

        // ================= HEADER CONTAINER =================
        JPanel headerContainer = new JPanel(new BorderLayout(0, 15));
        headerContainer.setBackground(AppColor.BACKGROUND);

        // Khung tách biệt cho Tiêu đề
        JPanel titlePanel = new RoundedPanel(12, AppColor.SURFACE);
        titlePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 12));
        titlePanel.setBorder(new LineBorder(AppColor.BORDER, 1, true));

        JLabel lblTitle = new JLabel("Danh Sách Tồn Kho");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(AppColor.PRIMARY);
        titlePanel.add(lblTitle);

        // ================= TOP PANEL =================
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(AppColor.BACKGROUND);

        txtSearch = new ModernSearchField("Tìm kiếm sản phẩm kho...");
        txtSearch.setPreferredSize(new Dimension(300, 38));

        btnSort = new OutlineButton("Sắp xếp: Mới nhất ▼");
        btnSort.setPreferredSize(new Dimension(190, 38));
        
        JPopupMenu sortMenu = new JPopupMenu();
        sortMenu.setBackground(Color.WHITE);
        sortMenu.setBorder(BorderFactory.createLineBorder(new Color(209, 213, 219), 1)); 

        String[] sortOptions = { "Sắp xếp: Mới nhất", "Sắp xếp: Số lượng tăng dần", "Sắp xếp: Số lượng giảm dần" };
        for (String opt : sortOptions) {
            JMenuItem item = createStyledMenuItem(opt);
            item.addActionListener(e -> {
                currentSortOpt = opt;
                btnSort.setText(opt + " ▼");
                applyFilterAndSort(false);
            });
            sortMenu.add(item);
        }
        btnSort.addActionListener(e -> sortMenu.show(btnSort, 0, btnSort.getHeight() + 4));

        JButton btnRefresh = new JButton("Làm mới");
        btnRefresh.setPreferredSize(new Dimension(100, 38));
        btnRefresh.setBackground(Color.WHITE);
        btnRefresh.setForeground(new Color(75, 85, 99));
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnRefresh.setFocusPainted(false);
        btnRefresh.setContentAreaFilled(false);
        btnRefresh.setBorderPainted(false);
        btnRefresh.setCursor(new Cursor(Cursor.HAND_CURSOR));

        try {
            java.net.URL url = getClass().getResource("/icons/refresh.png");
            if (url != null) {
                Image img = new ImageIcon(url).getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
                btnRefresh.setIcon(new ImageIcon(img));
            }
        } catch (Exception e) {}

        btnRefresh.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btnRefresh.putClientProperty("hover", true); btnRefresh.repaint(); }
            @Override public void mouseExited(MouseEvent e) { btnRefresh.putClientProperty("hover", false); btnRefresh.repaint(); }
        });

        btnRefresh.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean isHovered = Boolean.TRUE.equals(c.getClientProperty("hover"));
                g2.setColor(isHovered ? new Color(243, 244, 246) : Color.WHITE);
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 10, 10);
                g2.setColor(new Color(209, 213, 219));
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(1, 1, c.getWidth() - 3, c.getHeight() - 3, 10, 10);
                g2.dispose();
                super.paint(g, c);
            }
        });

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftPanel.setBackground(AppColor.BACKGROUND);
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setBackground(AppColor.BACKGROUND);
        rightPanel.add(txtSearch); // Tìm kiếm phải
        rightPanel.add(btnSort); // Sắp xếp phải (width: 150px)
        rightPanel.add(btnRefresh); // Làm mới phải

        topPanel.add(leftPanel, BorderLayout.WEST);
        topPanel.add(rightPanel, BorderLayout.EAST);

        // ================= SUMMARY PANEL =================
        JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        summaryPanel.setBackground(AppColor.BACKGROUND);
        summaryPanel.setPreferredSize(new Dimension(0, 85));

        cardTotal = new SummaryCard("Tổng mặt hàng", AppColor.TEXT_PRIMARY, AppColor.PRIMARY, PRIMARY_LIGHT, "box");
        cardLowStock = new SummaryCard("Sắp hết hạn", AppColor.WARNING, AppColor.WARNING, WARNING_LIGHT, "warning");
        cardOutOfStock = new SummaryCard("Hết hạn", AppColor.ERROR, AppColor.ERROR, ERROR_LIGHT, "warning");

        summaryPanel.add(cardTotal);
        summaryPanel.add(cardLowStock);
        summaryPanel.add(cardOutOfStock);

        JPanel titleAndTop = new JPanel(new BorderLayout(0, 15));
        titleAndTop.setBackground(AppColor.BACKGROUND);
        titleAndTop.add(titlePanel, BorderLayout.NORTH);
        titleAndTop.add(topPanel, BorderLayout.CENTER);

        headerContainer.add(titleAndTop, BorderLayout.NORTH);
        headerContainer.add(summaryPanel, BorderLayout.CENTER);

        this.add(headerContainer, BorderLayout.NORTH);

        // ================= SỰ KIỆN NÚT =================
        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            currentSortOpt = "Sắp xếp: Mới nhất";
            btnSort.setText(currentSortOpt + " ▼");
            loadDataToTable(false);
        });

        // ================= SETUP TABLE (STYLE NHAPKHO) =================
        String[] columnNames = {
                "Sản phẩm/Loại", "Mã loại", "Đơn vị tính", "Tổng số lượng khả dụng", "Tổng số lượng tồn", "Trạng thái", "Hành động"
        };

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Chỉ cột Hành động có thể edit (click)
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return Object.class;
            }
        };

        table = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (!isRowSelected(row)) {
                    // nền xen kẻ
                    c.setBackground(row % 2 == 0 ? AppColor.BACKGROUND : AppColor.SECONDARY_HOVER);
                    c.setForeground(AppColor.TEXT_PRIMARY);
                } else {
                    c.setBackground(PRIMARY_LIGHT);
                    c.setForeground(AppColor.PRIMARY_ACTIVE);
                }
                return c;
            }
        };

        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(55);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(AppColor.BORDER);
        table.setSelectionBackground(PRIMARY_LIGHT);
        table.setSelectionForeground(AppColor.PRIMARY_ACTIVE);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFocusable(false);

        JTableHeader th = table.getTableHeader();
        th.setFont(new Font("Segoe UI", Font.BOLD, 11));
        th.setBackground(AppColor.BACKGROUND);
        th.setForeground(AppColor.TEXT_SECONDARY);
        th.setPreferredSize(new Dimension(0, 45));
        th.setBorder(new MatteBorder(0, 0, 1, 0, AppColor.BORDER));
        th.setReorderingAllowed(false);

        // Canh lề và style Header
        DefaultTableCellRenderer hr = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                l.setFont(new Font("Segoe UI", Font.BOLD, 13));
                l.setForeground(AppColor.TEXT_SECONDARY);
                l.setBackground(AppColor.BACKGROUND);

                if (c == 5 || c == 6)
                    l.setHorizontalAlignment(SwingConstants.CENTER);
                else
                    l.setHorizontalAlignment(SwingConstants.LEFT);

                l.setBorder(new EmptyBorder(0, 15, 0, 15));
                return l;
            }
        };

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setHeaderRenderer(hr);
        }

        class CustomCellRenderer extends DefaultTableCellRenderer {
            private int align;

            public CustomCellRenderer(int align) {
                this.align = align;
            }

            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                l.setHorizontalAlignment(align);
                l.setBorder(new EmptyBorder(0, 15, 0, 15));
                if (c == 0) {
                    l.setFont(new Font("Segoe UI", Font.BOLD, 13));
                    l.setForeground(sel ? AppColor.PRIMARY_ACTIVE : AppColor.TEXT_PRIMARY);
                }
                return l;
            }
        }

        CustomCellRenderer left = new CustomCellRenderer(SwingConstants.LEFT);
        // CstomCellRenderer right = new CustomCellRenderer(SwingConstants.RIGHT);

        table.getColumnModel().getColumn(0).setCellRenderer(left);
        table.getColumnModel().getColumn(1).setCellRenderer(left);
        table.getColumnModel().getColumn(2).setCellRenderer(left);
        table.getColumnModel().getColumn(3).setCellRenderer(left);
        table.getColumnModel().getColumn(4).setCellRenderer(new StockQuantityRenderer());
        table.getColumnModel().getColumn(5).setCellRenderer(new BadgeStatusRenderer());

        table.getColumnModel().getColumn(6).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(6).setCellEditor(new ButtonEditor(new JCheckBox()));

        table.getColumnModel().getColumn(0).setPreferredWidth(250);
        table.getColumnModel().getColumn(1).setPreferredWidth(120);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(150);
        table.getColumnModel().getColumn(4).setPreferredWidth(150);
        table.getColumnModel().getColumn(5).setPreferredWidth(120);
        table.getColumnModel().getColumn(6).setPreferredWidth(120);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(AppColor.SURFACE);

        // Bọc trong RoundedPanel giống NhapKhoPanel
        JPanel tableCard = new RoundedPanel(14, AppColor.SURFACE);
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(new LineBorder(AppColor.BORDER, 1, true));
        tableCard.add(scrollPane, BorderLayout.CENTER);

        this.add(tableCard, BorderLayout.CENTER);

        // ================= BOTTOM PANEL =================
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(AppColor.BACKGROUND);
        bottomPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        paginationPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        paginationPanel.setBackground(AppColor.BACKGROUND);
        bottomPanel.add(paginationPanel, BorderLayout.EAST);

        this.add(bottomPanel, BorderLayout.SOUTH);

        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                applyFilterAndSort(false);
            }
        });
        // Listener moved to menu items
    }

    // ================= CLASS CHO NÚT CHI TIẾT =================
    class ButtonRenderer extends JPanel implements TableCellRenderer {
        private JButton btn;

        public ButtonRenderer() {
            setLayout(new GridBagLayout());
            setBackground(AppColor.BACKGROUND);
            btn = new JButton("Chi tiết");
            btn.setPreferredSize(new Dimension(80, 32));
            btn.setBackground(AppColor.PRIMARY);
            btn.setForeground(Color.WHITE);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btn.putClientProperty(FlatClientProperties.STYLE, "arc: 10");
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            add(btn);
        }

        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
            setBackground(sel ? PRIMARY_LIGHT : (r % 2 == 0 ? AppColor.BACKGROUND : AppColor.SECONDARY_HOVER));
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        private JPanel panel;
        private JButton btn;
        private boolean isPushed;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            panel = new JPanel(new GridBagLayout());
            btn = new JButton("Chi tiết");
            btn.setPreferredSize(new Dimension(80, 32));
            btn.setBackground(AppColor.PRIMARY);
            btn.setForeground(Color.WHITE);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btn.putClientProperty(FlatClientProperties.STYLE, "arc: 10");
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);

            btn.addActionListener(e -> fireEditingStopped());
            panel.add(btn);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
                int column) {
            panel.setBackground(table.getSelectionBackground());
            isPushed = true;
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                int modelRow = table.convertRowIndexToModel(table.getEditingRow());
                int dataIndex = (currentPage - 1) * rowsPerPage + modelRow;
                Object[] rowData = currentData.get(dataIndex);

                String tenSP = rowData[1] != null ? rowData[1].toString() : "";
                String maSP = rowData[2] != null ? rowData[2].toString() : "";
                double sl = rowData[4] != null ? (double) rowData[4] : 0;

                SwingUtilities.invokeLater(() -> {
                    gui.dialog.ChiTietTonKhoDialog dialog = new gui.dialog.ChiTietTonKhoDialog(
                            (Frame) SwingUtilities.getWindowAncestor(TonKhoPanel.this),
                            tenSP, maSP, sl, tonKhoBUS);
                    dialog.setVisible(true);

                    // Reload data after dialog closes to reflect changes (e.g., deleted expired
                    // products)
                    loadDataToTable(true);
                });
            }
            isPushed = false;
            return "Chi tiết";
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }

    // ================= LOGIC DỮ LIỆU & PHÂN TRANG =================
    private void updateSummaryCards() {
        int total = originalData.size();
        int lowStock = 0;
        int outOfStock = 0;

        for (Object[] row : originalData) {
            String status = row.length > 6 && row[6] != null ? row[6].toString() : "";
            if (status.equals("Hết hạn")) {
                outOfStock++;
            } else if (status.equals("Sắp hết hạn")) {
                lowStock++;
            }
        }

        if (cardTotal != null)
            cardTotal.setCount(total);
        if (cardLowStock != null)
            cardLowStock.setCount(lowStock);
        if (cardOutOfStock != null)
            cardOutOfStock.setCount(outOfStock);
    }

    private void loadDataToTable(boolean keepCurrentPage) {
        try {
            this.minTonKho = thamSoBUS.getValueByName("MIN_TONKHO", 10.0);
        } catch (Exception e) {
            this.minTonKho = 10.0;
        }
        ArrayList<Object[]> list = tonKhoBUS.getDanhSachTonKhoTongHop();
        originalData = (list != null) ? list : new ArrayList<>();
        updateSummaryCards();
        applyFilterAndSort(keepCurrentPage);
    }

    private void applyFilterAndSort(boolean keepCurrentPage) {
        String keyword = txtSearch.getText().trim().toLowerCase();
        currentData = new ArrayList<>();

        for (Object[] row : originalData) {
            boolean match = false;
            for (Object cell : row) {
                if (cell != null && cell.toString().toLowerCase().contains(keyword)) {
                    match = true;
                    break;
                }
            }
            if (match)
                currentData.add(row);
        }

        String sortOpt = currentSortOpt;
        currentData.sort((row1, row2) -> {
            Double s1 = row1[4] != null ? (Double) row1[4] : 0.0;
            Double s2 = row2[4] != null ? (Double) row2[4] : 0.0;
            if (sortOpt.contains("giảm dần"))
                return s2.compareTo(s1);
            if (sortOpt.contains("tăng dần"))
                return s1.compareTo(s2);
            String m1 = row1[0] != null ? row1[0].toString() : "";
            String m2 = row2[0] != null ? row2[0].toString() : "";
            return m2.compareTo(m1);
        });

        totalPages = (int) Math.ceil((double) currentData.size() / rowsPerPage);
        if (totalPages == 0)
            totalPages = 1;
        if (!keepCurrentPage)
            currentPage = 1;
        else if (currentPage > totalPages)
            currentPage = totalPages;

        renderTablePage();
        renderPaginationButtons();
    }

    // Sửa trong renderTablePage()
    private void renderTablePage() {
        tableModel.setRowCount(0);
        int start = (currentPage - 1) * rowsPerPage;
        int end = Math.min(start + rowsPerPage, currentData.size());

        for (int i = start; i < end; i++) {
            Object[] row = currentData.get(i);
            String tenSP = row[1] != null ? row[1].toString() : "";
            String maSP = row[2] != null ? row[2].toString() : "";
            String dvt = row[3] != null ? row[3].toString() : "";
            double slConLai = row[4] != null ? (double) row[4] : 0;
            double slKhaDung = row.length > 5 && row[5] != null ? (double) row[5] : 0;

            // ✅ Xóa .0 nếu là số nguyên cho Tổng số lượng tồn
            String soLuong = (slConLai == (long) slConLai)
                    ? String.valueOf((long) slConLai)
                    : String.valueOf(slConLai);

            // ✅ Xóa .0 nếu là số nguyên cho Tổng số lượng khả dụng
            String soLuongKhaDung = (slKhaDung == (long) slKhaDung)
                    ? String.valueOf((long) slKhaDung)
                    : String.valueOf(slKhaDung);

            String trangThai = row.length > 6 && row[6] != null ? row[6].toString() : "Còn hạn";
            tableModel.addRow(new Object[] {
                    tenSP, maSP, dvt, soLuongKhaDung, soLuong, trangThai, "Chi tiết"
            });
        }
    }

    private void renderPaginationButtons() {
        paginationPanel.removeAll();

        PageButton btnPrev = new PageButton("Trang trước", false);
        btnPrev.setEnabled(currentPage > 1);
        btnPrev.setPreferredSize(new Dimension(100, 36));
        btnPrev.addActionListener(e -> {
            currentPage--;
            renderTablePage();
            renderPaginationButtons();
        });
        paginationPanel.add(btnPrev);

        int maxVisible = 3;
        int startPage = Math.max(1, currentPage - 1);
        int endPage = Math.min(totalPages, startPage + maxVisible - 1);
        if (endPage - startPage < maxVisible - 1)
            startPage = Math.max(1, endPage - maxVisible + 1);

        for (int i = startPage; i <= endPage; i++) {
            int pageNum = i;
            PageButton btnPage = new PageButton(String.valueOf(pageNum), pageNum == currentPage);
            btnPage.addActionListener(e -> {
                currentPage = pageNum;
                renderTablePage();
                renderPaginationButtons();
            });
            paginationPanel.add(btnPage);
        }

        if (endPage < totalPages) {
            PageButton btnDots = new PageButton("...", false);
            btnDots.addActionListener(e -> {
                currentPage = endPage + 1;
                renderTablePage();
                renderPaginationButtons();
            });
            paginationPanel.add(btnDots);
        }

        PageButton btnNext = new PageButton("Trang sau", false);
        btnNext.setEnabled(currentPage < totalPages);
        btnNext.setPreferredSize(new Dimension(90, 36));
        btnNext.addActionListener(e -> {
            currentPage++;
            renderTablePage();
            renderPaginationButtons();
        });
        paginationPanel.add(btnNext);

        paginationPanel.revalidate();
        paginationPanel.repaint();
    }

    // ================= RENDERER CHO BẢNG =================
    private class StockQuantityRenderer extends JPanel implements TableCellRenderer {
        private String quantityStr = "";
        private boolean isLowStock = false;

        public StockQuantityRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
            this.quantityStr = v == null ? "" : v.toString();
            this.isLowStock = false;
            if (v != null) {
                try {
                    double sl = Double.parseDouble(v.toString());
                    if (sl < minTonKho) {
                        this.isLowStock = true;
                    }
                } catch (NumberFormatException e) {
                    // ignore
                }
            }

            setBackground(
                    sel ? t.getSelectionBackground() : (r % 2 == 0 ? AppColor.BACKGROUND : AppColor.SECONDARY_HOVER));
            setForeground(sel ? AppColor.PRIMARY_ACTIVE : AppColor.TEXT_PRIMARY);
            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (quantityStr.isEmpty())
                return;

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // 1. Draw the quantity number
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            g2.setColor(getForeground());
            FontMetrics fmNum = g2.getFontMetrics();
            int xNum = 15; // standard left padding
            int yNum = (getHeight() - fmNum.getHeight()) / 2 + fmNum.getAscent();
            g2.drawString(quantityStr, xNum, yNum);

            // 2. Draw the low stock badge if needed
            if (isLowStock) {
                String badgeText = "(Sắp hết hàng)";
                g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                FontMetrics fmBadge = g2.getFontMetrics();
                
                // Color configuration: orange text, yellow background
                Color bgBadge = new Color(254, 240, 138); // Soft yellow: #FEF08A
                Color fgBadge = new Color(217, 119, 6);   // Deep orange: #D97706

                int px = 8; // badge horizontal padding
                int py = 3; // badge vertical padding
                int badgeW = fmBadge.stringWidth(badgeText) + px * 2;
                int badgeH = fmBadge.getHeight() + py * 2;

                // Position the badge after the number
                int gap = 8; // gap between number and badge
                int xBadge = xNum + fmNum.stringWidth(quantityStr) + gap;
                int yBadge = (getHeight() - badgeH) / 2;

                // Draw rounded rectangle background
                g2.setColor(bgBadge);
                g2.fill(new RoundRectangle2D.Float(xBadge, yBadge, badgeW, badgeH, 8, 8));

                // Draw text inside badge
                g2.setColor(fgBadge);
                g2.drawString(badgeText, xBadge + px, yBadge + py + fmBadge.getAscent());
            }

            g2.dispose();
        }
    }

    class ImageRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
            JLabel label = new JLabel();
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setVerticalAlignment(SwingConstants.CENTER);
            label.setBorder(new EmptyBorder(0, 12, 0, 12));
            label.setOpaque(true);
            label.setBackground(
                    sel ? t.getSelectionBackground() : (r % 2 == 0 ? AppColor.BACKGROUND : AppColor.SECONDARY_HOVER));

            if (v != null && !v.toString().trim().isEmpty()) {
                try {
                    java.net.URL imgUrl = getClass().getResource("/images/" + v.toString() + ".jpg");
                    if (imgUrl != null) {
                        Image img = new ImageIcon(imgUrl).getImage().getScaledInstance(45, 35, Image.SCALE_SMOOTH);
                        label.setIcon(new ImageIcon(img));
                    } else {
                        label.setText("No Image");
                        label.setFont(new Font("Segoe UI", Font.ITALIC, 11));
                    }
                } catch (Exception e) {
                    label.setText("Error");
                }
            }
            return label;
        }
    }

    private static class BadgeStatusRenderer extends JPanel implements TableCellRenderer {
        private String txt = "";

        BadgeStatusRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
            this.txt = v == null ? "" : v.toString();
            setBackground(
                    sel ? t.getSelectionBackground() : (r % 2 == 0 ? AppColor.BACKGROUND : AppColor.SECONDARY_HOVER));
            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (txt.isEmpty())
                return;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color bg, fg;
            if (txt.equals("Còn hạn") || txt.equals("Còn hàng")) {
                bg = PRIMARY_LIGHT;
                fg = AppColor.SUCCESS_ACTIVE;
            } else if (txt.equals("Sắp hết hạn") || txt.equals("Sắp hết") || txt.equals("Tồn kho thấp")) {
                bg = WARNING_LIGHT;
                fg = AppColor.WARNING_ACTIVE;
            } else { // Hết hạn hoặc Cần nhập gấp
                bg = ERROR_LIGHT;
                fg = AppColor.ERROR;
            }

            g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
            FontMetrics fm = g2.getFontMetrics();
            int tw = fm.stringWidth(txt), px = 10, py = 4;
            int w = tw + px * 2, h = fm.getHeight() + py * 2;
            int x = (getWidth() - w) / 2, y = (getHeight() - h) / 2;

            g2.setColor(bg);
            g2.fill(new RoundRectangle2D.Float(x, y, w, h, h, h));
            g2.setColor(fg);
            g2.drawString(txt, x + px, y + py + fm.getAscent());
            g2.dispose();
        }
    }

    private static class RoundedPanel extends JPanel {
        private final int radius;

        RoundedPanel(int r, Color bg) {
            this.radius = r;
            setBackground(bg);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius, radius));
            g2.dispose();
        }
    }

    private static class IconPanel extends RoundedPanel {
        private String type;
        private Color color;

        IconPanel(int r, Color bg, Color color, String type) {
            super(r, bg);
            this.color = color;
            this.type = type;
            setPreferredSize(new Dimension(45, 45));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            int w = getWidth();
            int h = getHeight();

            if ("box".equals(type)) {
                int cx = w / 2;
                int cy = h / 2 + 1;
                int s = 10;

                int[] xTop = { cx, cx + s, cx, cx - s };
                int[] yTop = { cy - s, cy - s / 2, cy, cy - s / 2 };
                g2.drawPolygon(xTop, yTop, 4);

                g2.drawLine(cx - s, cy - s / 2, cx - s, cy + s / 2);
                g2.drawLine(cx - s, cy + s / 2, cx, cy + s);

                g2.drawLine(cx + s, cy - s / 2, cx + s, cy + s / 2);
                g2.drawLine(cx + s, cy + s / 2, cx, cy + s);

                g2.drawLine(cx, cy, cx, cy + s);
            } else if ("warning".equals(type)) {
                int cx = w / 2;
                int cy = h / 2;
                int s = 11;

                g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                int[] xPoints = { cx, cx - s, cx + s };
                int[] yPoints = { cy - s + 2, cy + s - 1, cy + s - 1 };
                g2.drawPolygon(xPoints, yPoints, 3);

                g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(cx, cy - 3, cx, cy + 2);
                g2.fillOval(cx - 1, cy + 5, 2, 2);
            }
            g2.dispose();
        }
    }

    private class SummaryCard extends JPanel {
        private JLabel lblCount;

        public SummaryCard(String title, Color countColor, Color iconColor, Color iconBgColor, String iconType) {
            this.setLayout(new BorderLayout(10, 10));
            this.setBackground(AppColor.SURFACE);
            this.setOpaque(false);
            this.setBorder(new EmptyBorder(15, 20, 15, 20));

            JPanel leftPanel = new JPanel();
            leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
            leftPanel.setOpaque(false);

            JLabel lblTitle = new JLabel(title);
            lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            lblTitle.setForeground(AppColor.TEXT_SECONDARY);

            lblCount = new JLabel("0");
            lblCount.setFont(new Font("Segoe UI", Font.BOLD, 24));
            lblCount.setForeground(countColor);

            leftPanel.add(lblTitle);
            leftPanel.add(Box.createVerticalStrut(5));
            leftPanel.add(lblCount);

            JPanel rightContainer = new JPanel(new GridBagLayout());
            rightContainer.setOpaque(false);
            rightContainer.add(new IconPanel(12, iconBgColor, iconColor, iconType));

            this.add(leftPanel, BorderLayout.CENTER);
            this.add(rightContainer, BorderLayout.EAST);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Vẽ nền bo góc 15px (nhẹ)
            g2.setColor(AppColor.SURFACE);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 15, 15));

            // Vẽ border bo góc
            g2.setColor(AppColor.BORDER);
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
            g2.dispose();
        }

        public void setCount(int count) {
            lblCount.setText(String.valueOf(count));
        }
    }

    static class ModernSearchField extends JTextField {
        private final String placeholder;
        private boolean isHovered = false;

        private ImageIcon searchIcon;

        public ModernSearchField(String placeholder) {
            this.placeholder = placeholder;
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(0, 38, 0, 16));
            setFont(new Font("Segoe UI", Font.PLAIN, 13));
            setForeground(AppColor.TEXT_PRIMARY);

            try {
                java.net.URL searchUrl = getClass().getResource("/icons/search.png");
                if (searchUrl != null) {
                    Image imgSearch = new ImageIcon(searchUrl).getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH);
                    searchIcon = new ImageIcon(imgSearch);
                }
            } catch (Exception e) {
            }

            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { isHovered = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { isHovered = false; repaint(); }
            });

            addFocusListener(new FocusAdapter() {
                @Override public void focusGained(FocusEvent e) { repaint(); }
                @Override public void focusLost(FocusEvent e) { 
                    isHovered = false; 
                    repaint(); 
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

            if (isFocusOwner()) {
                g2.setColor(AppColor.PRIMARY);
                g2.setStroke(new BasicStroke(1.5f));
            } else if (isHovered) {
                g2.setColor(new Color(156, 163, 175));
                g2.setStroke(new BasicStroke(1f));
            } else {
                g2.setColor(new Color(209, 213, 219));
                g2.setStroke(new BasicStroke(1f));
            }
            g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 12, 12);

            super.paintComponent(g);

            if (getText().isEmpty() && !isFocusOwner()) {
                g2.setColor(new Color(156, 163, 175));
                FontMetrics fm = g.getFontMetrics();
                int yText = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(placeholder, 38, yText);
            }

            if (searchIcon != null) {
                searchIcon.paintIcon(this, g2, 12, (getHeight() - searchIcon.getIconHeight()) / 2);
            } else {
                g2.setColor(new Color(156, 163, 175));
                int iconSize = 14;
                int x = 14;
                int yIcon = (getHeight() - iconSize) / 2;
                g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawOval(x, yIcon, 10, 10);
                g2.drawLine(x + 8, yIcon + 8, x + 13, yIcon + 13);
            }

            g2.dispose();
        }
    }

    static class PageButton extends JButton {
        private boolean active;
        private boolean hovered = false;
        public PageButton(String text, boolean active) {
            super(text);
            this.active = active;
            setFont(new Font("Segoe UI", Font.BOLD, 13));
            setForeground(active ? Color.WHITE : new Color(75, 85, 99));
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setMargin(new Insets(0, 0, 0, 0));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(36, 36));

            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hovered = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { hovered = false; repaint(); }
            });
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (active) {
                g2.setColor(AppColor.PRIMARY);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
            } else {
                if (isEnabled()) {
                    g2.setColor(hovered ? new Color(243, 244, 246) : Color.WHITE);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    g2.setColor(new Color(209, 213, 219));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                } else {
                    g2.setColor(new Color(249, 250, 251));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    g2.setColor(new Color(229, 231, 235));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                }
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private JMenuItem createStyledMenuItem(String text) {
        JMenuItem item = new JMenuItem(text);
        item.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        item.setBackground(Color.WHITE);
        item.setForeground(AppColor.TEXT_PRIMARY);
        item.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        item.setPreferredSize(new Dimension(190, 36)); 

        item.setUI(new javax.swing.plaf.basic.BasicMenuItemUI() {
            @Override
            protected void paintBackground(Graphics g, JMenuItem menuItem, Color bgColor) {
                if (menuItem.isArmed() || menuItem.isSelected()) {
                    g.setColor(new Color(243, 244, 246)); 
                } else {
                    g.setColor(Color.WHITE);
                }
                g.fillRect(0, 0, menuItem.getWidth(), menuItem.getHeight());
            }

            @Override
            protected void paintText(Graphics g, JMenuItem menuItem, Rectangle textRect, String text) {
                textRect.x = 16; 
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setColor(AppColor.TEXT_PRIMARY);
                FontMetrics fm = g2.getFontMetrics();
                int y = textRect.y + fm.getAscent() + (textRect.height - fm.getHeight()) / 2;
                g2.drawString(text, textRect.x, y);
                g2.dispose();
            }
        });
        return item;
    }

    static class OutlineButton extends JButton {
        private boolean isHovered = false;

        public OutlineButton(String text) {
            super(text);
            setFont(new Font("Segoe UI", Font.PLAIN, 13));
            setForeground(new Color(75, 85, 99));
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { isHovered = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { isHovered = false; repaint(); }
            });
        }

       @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            g2.setColor(isHovered ? new Color(243, 244, 246) : Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            
            g2.setColor(new Color(209, 213, 219)); 
            g2.setStroke(new BasicStroke(1.2f)); 
            g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 10, 10);
            
            g2.dispose();
            super.paintComponent(g);
        }
    }
}