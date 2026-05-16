package gui.panel;

import bus.XuatKhoBUS;
import gui.dialog.SoanHangDialog;
import util.AppColor;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class XuatKhoPanel extends JPanel {

    private static final String SEARCH_PLACEHOLDER = "Tìm kiếm đơn hàng...";
    private static final Color PRIMARY_LIGHT = new Color(22, 163, 74, 28);
    private static final Color WARNING_LIGHT = new Color(245, 158, 11, 24);

    private final String[] columns = {
            "MÃ ĐƠN HÀNG", "KHÁCH HÀNG", "SỐ SP", "TỔNG SL", "TRẠNG THÁI", "Hành động"
    };

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private JLabel lblCount;
    private JLabel lblPage;
    private JLabel lblTongDon;
    private JLabel lblTongSanPham;
    private JLabel lblThieuTon;
    private JButton btnPrev;
    private JButton btnNext;
    private JButton btnSort;

    private List<Object[]> allData = new ArrayList<>();
    private List<Object[]> filteredData = new ArrayList<>();
    private int currentPage = 1;
    private final int rowsPerPage = 6;
    private String currentSortKey;
    private int soDonThieuTonKho;

    public XuatKhoPanel() {
        setLayout(new BorderLayout());
        setBackground(AppColor.BACKGROUND);
        setBorder(new EmptyBorder(24, 28, 24, 28));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        content.add(buildHeader());
        content.add(Box.createVerticalStrut(16));
        content.add(buildCards());
        content.add(Box.createVerticalStrut(22));
        content.add(buildTablePanel());

        add(content, BorderLayout.CENTER);
        loadDataToTable();
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout(0, 10));
        header.setBackground(AppColor.SURFACE);
        header.setBorder(new CompoundBorder(new LineBorder(AppColor.BORDER), new EmptyBorder(16, 20, 16, 20)));
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        header.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = new JLabel("Quản lý đơn hàng chờ xuất kho");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(AppColor.TEXT_PRIMARY);
        header.add(title, BorderLayout.NORTH);

        JPanel actions = new JPanel(new BorderLayout());
        actions.setOpaque(false);

        // Chuyển "Làm mới" sang bên phải cùng nhóm với tìm kiếm/sắp xếp
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 4));
        right.setOpaque(false);
        right.add(buildSearchBox());
        
        JButton btnRefresh = buildOutlineButton("Làm mới", 0, 36);
        btnRefresh.addActionListener(e -> loadDataToTable());
        right.add(btnRefresh);

        btnSort = buildOutlineButton("Sắp xếp ▼", 0, 36);
        btnSort.addActionListener(e -> showSortMenu());
        right.add(btnSort);

        actions.add(right, BorderLayout.EAST);
        header.add(actions, BorderLayout.CENTER);
        return header;
    }

    private JPanel buildCards() {
        JPanel cards = new JPanel(new GridLayout(1, 3, 20, 0));
        cards.setOpaque(false);
        cards.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));
        cards.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblTongDon = new JLabel("0");
        lblTongSanPham = new JLabel("0");
        lblThieuTon = new JLabel("0");

        cards.add(
                buildSummaryCard("Đơn hàng chờ", lblTongDon, new Color(37, 99, 235), new Color(219, 234, 254), "box"));
        cards.add(buildSummaryCard("Tổng sản phẩm", lblTongSanPham, new Color(249, 115, 22), new Color(255, 237, 213),
                "list"));
        cards.add(buildSummaryCard("Đơn thiếu tồn kho", lblThieuTon, new Color(239, 68, 68), new Color(254, 226, 226),
                "warn"));
        return cards;
    }

    private JPanel buildSummaryCard(String title, JLabel value, Color iconColor, Color iconBg, String iconType) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(AppColor.SURFACE);
        card.setBorder(new CompoundBorder(new LineBorder(AppColor.BORDER), new EmptyBorder(10, 16, 10, 16)));

        JPanel text = new JPanel(new GridLayout(2, 1, 0, 2));
        text.setOpaque(false);
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTitle.setForeground(AppColor.TEXT_SECONDARY);
        value.setFont(new Font("Segoe UI", Font.BOLD, 20));
        value.setForeground(AppColor.TEXT_PRIMARY);
        text.add(lblTitle);
        text.add(value);

        JPanel icon = new IconBox(iconColor, iconBg, iconType);
        icon.setPreferredSize(new Dimension(36, 36));
        card.add(text, BorderLayout.CENTER);
        card.add(icon, BorderLayout.EAST);
        return card;
    }

    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(AppColor.SURFACE);
        panel.setBorder(new LineBorder(AppColor.BORDER));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5;
            }
        };

        table = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? AppColor.BACKGROUND : AppColor.SECONDARY_HOVER);
                    c.setForeground(AppColor.TEXT_PRIMARY);
                }
                return c;
            }
        };
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(55);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(false);
        table.setGridColor(AppColor.BORDER);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFocusable(false);
        table.setSelectionBackground(PRIMARY_LIGHT);
        table.setSelectionForeground(AppColor.PRIMARY_ACTIVE);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        setupTableColumns();

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(AppColor.SURFACE);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buildFooter(), BorderLayout.SOUTH);
        return panel;
    }

    private void setupTableColumns() {
        JTableHeader header = table.getTableHeader();
        header.setPreferredSize(new Dimension(0, 48));
        header.setFont(new Font("Segoe UI", Font.BOLD, 11));
        header.setBackground(AppColor.BACKGROUND);
        header.setForeground(AppColor.TEXT_SECONDARY);
        header.setReorderingAllowed(false);
        header.setBorder(new MatteBorder(0, 0, 1, 0, AppColor.BORDER));

        DefaultTableCellRenderer textRenderer = new TextCellRenderer();
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(textRenderer);
        }
        table.getColumnModel().getColumn(4).setCellRenderer(new StatusBadgeRenderer());
        table.getColumnModel().getColumn(5).setCellRenderer(new DetailButtonRenderer());
        table.getColumnModel().getColumn(5).setCellEditor(new DetailButtonEditor());

        int[] widths = { 130, 220, 75, 100, 135, 120 };
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(AppColor.SURFACE);
        footer.setBorder(
                new CompoundBorder(new MatteBorder(1, 0, 0, 0, AppColor.BORDER), new EmptyBorder(12, 22, 14, 22)));

        lblCount = new JLabel("Hiển thị 0 - 0 của 0 đơn hàng");
        lblCount.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblCount.setForeground(AppColor.TEXT_SECONDARY);
        footer.add(lblCount, BorderLayout.WEST);

        JPanel pages = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        pages.setOpaque(false);
        btnPrev = buildPageButton("Trước");
        btnNext = buildPageButton("Sau");
        lblPage = new JLabel("1", SwingConstants.CENTER);
        lblPage.setPreferredSize(new Dimension(32, 32));
        lblPage.setOpaque(true);
        lblPage.setBackground(AppColor.PRIMARY);
        lblPage.setForeground(Color.WHITE);
        lblPage.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnPrev.addActionListener(e -> changePage(-1));
        btnNext.addActionListener(e -> changePage(1));
        pages.add(btnPrev);
        pages.add(lblPage);
        pages.add(btnNext);
        footer.add(pages, BorderLayout.EAST);
        return footer;
    }

    public void loadDataToTable() {
        XuatKhoBUS bus = new XuatKhoBUS();
        allData = bus.getDanhSachDonHangChoXuat();
        soDonThieuTonKho = bus.getSoDonHangThieuTonKho();
        if (txtSearch != null) {
            txtSearch.setText(SEARCH_PLACEHOLDER);
            txtSearch.setForeground(AppColor.TEXT_SECONDARY);
        }
        currentSortKey = null;
        if (btnSort != null) {
            btnSort.setText("Sắp xếp");
        }
        filterTable();
    }

    private void filterTable() {
        if (txtSearch == null)
            return;
        String keyword = txtSearch.getText().trim().toLowerCase();
        if (keyword.isEmpty() || SEARCH_PLACEHOLDER.toLowerCase().equals(keyword)) {
            filteredData = new ArrayList<>(allData);
        } else {
            filteredData = allData.stream()
                    .filter(row -> row[0].toString().toLowerCase().contains(keyword)
                            || row[1].toString().toLowerCase().contains(keyword))
                    .collect(Collectors.toList());
        }
        sortData();
        currentPage = 1;
        renderTablePage();
    }

    private void sortData() {
        if (currentSortKey == null)
            return;
        switch (currentSortKey) {
            case "ma_asc" -> filteredData.sort(java.util.Comparator.comparing(r -> r[0].toString()));
            case "ma_desc" ->
                filteredData.sort(java.util.Comparator.<Object[], String>comparing(r -> r[0].toString()).reversed());
            case "sl_asc" ->
                filteredData.sort(java.util.Comparator.comparingDouble(r -> Double.parseDouble(r[3].toString())));
            case "sl_desc" -> filteredData.sort(java.util.Comparator
                    .<Object[]>comparingDouble(r -> Double.parseDouble(r[3].toString())).reversed());
            default -> {
            }
        }
    }

    private void renderTablePage() {
        if (table.isEditing()) {
            table.getCellEditor().stopCellEditing();
        }
        tableModel.setRowCount(0);
        int total = filteredData.size();
        int totalPages = Math.max(1, (int) Math.ceil(total / (double) rowsPerPage));
        currentPage = Math.min(Math.max(1, currentPage), totalPages);
        int start = (currentPage - 1) * rowsPerPage;
        int end = Math.min(start + rowsPerPage, total);

        for (int i = start; i < end; i++) {
            Object[] row = filteredData.get(i);
            tableModel.addRow(new Object[] { row[0], row[1], row[2], row[3], row[4], "Chi tiết" });
        }

        lblPage.setText(total == 0 ? "0" : String.valueOf(currentPage));
        lblCount.setText(total == 0
                ? "Không tìm thấy đơn hàng nào"
                : String.format("Hiển thị %d - %d của %d đơn hàng", start + 1, end, total));
        btnPrev.setEnabled(currentPage > 1);
        btnNext.setEnabled(currentPage < totalPages);
        updateSummary();
    }

    private void updateSummary() {
        lblTongDon.setText(String.valueOf(allData.size()));
        long totalQty = allData.stream().mapToLong(row -> Math.round(Double.parseDouble(row[3].toString()))).sum();
        lblTongSanPham.setText(String.format("%,d", totalQty));
        lblThieuTon.setText(String.valueOf(soDonThieuTonKho));
    }

    private void changePage(int delta) {
        currentPage += delta;
        renderTablePage();
    }

    private void showSortMenu() {
        JPopupMenu menu = new JPopupMenu();
        addSort(menu, "Mã ĐH tăng dần", "ma_asc");
        addSort(menu, "Mã ĐH giảm dần", "ma_desc");
        addSort(menu, "Tổng SL tăng dần", "sl_asc");
        addSort(menu, "Tổng SL giảm dần", "sl_desc");
        menu.show(btnSort, 0, btnSort.getHeight());
    }

    private void addSort(JPopupMenu menu, String label, String key) {
        JMenuItem item = new JMenuItem(label);
        item.addActionListener(e -> {
            currentSortKey = key;
            btnSort.setText(label + " ▼");
            filterTable();
        });
        menu.add(item);
    }

    private void openDetailDialog(String maDH) {
        Window window = SwingUtilities.getWindowAncestor(this);
        Frame frame = window instanceof Frame ? (Frame) window : null;
        SoanHangDialog dialog = new SoanHangDialog(frame, maDH);
        dialog.setVisible(true);
        loadDataToTable();
    }

    private JPanel buildSearchBox() {
        JPanel wrap = new JPanel(new BorderLayout(8, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(AppColor.BORDER);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
            }

            @Override
            protected void paintBorder(Graphics g) {
                // Do nothing here
            }
        };
        wrap.setBackground(AppColor.BACKGROUND);
        wrap.setBorder(new EmptyBorder(0, 12, 0, 12));
        wrap.setOpaque(false);
        wrap.setPreferredSize(new Dimension(300, 36));

        JLabel lblIcon = new JLabel(new SearchIconV2());
        lblIcon.setBorder(new EmptyBorder(0, 0, 0, 4));
        wrap.add(lblIcon, BorderLayout.WEST);

        txtSearch = new JTextField(SEARCH_PLACEHOLDER);
        txtSearch.setBorder(BorderFactory.createEmptyBorder());
        txtSearch.setOpaque(false);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtSearch.setForeground(AppColor.TEXT_SECONDARY);
        txtSearch.getDocument().addDocumentListener((SimpleDocumentListener) e -> filterTable());
        txtSearch.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (SEARCH_PLACEHOLDER.equals(txtSearch.getText())) {
                    txtSearch.setText("");
                    txtSearch.setForeground(AppColor.TEXT_PRIMARY);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (txtSearch.getText().trim().isEmpty()) {
                    txtSearch.setText(SEARCH_PLACEHOLDER);
                    txtSearch.setForeground(AppColor.TEXT_SECONDARY);
                }
            }
        });
        wrap.add(txtSearch, BorderLayout.CENTER);
        return wrap;
    }

    private JButton buildPrimaryButton(String text, int width, int height) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
                Color bg = getModel().isPressed() ? AppColor.PRIMARY_ACTIVE : (getModel().isRollover() ? AppColor.PRIMARY_HOVER : AppColor.PRIMARY);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(bg.darker());
                g2.setStroke(new BasicStroke(1.1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        if (width > 0) btn.setPreferredSize(new Dimension(width, height));
        else btn.setPreferredSize(new Dimension(btn.getPreferredSize().width + 40, height));
        
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton buildOutlineButton(String text, int width, int height) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
                g2.setColor(getModel().isRollover() ? AppColor.SECONDARY_HOVER : AppColor.SURFACE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(AppColor.BORDER);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        if (width > 0) btn.setPreferredSize(new Dimension(width, height));
        else btn.setPreferredSize(new Dimension(btn.getPreferredSize().width + 36, height));

        btn.setForeground(AppColor.TEXT_SECONDARY);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton buildPageButton(String text) {
        JButton btn = buildOutlineButton(text, 0, 32);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        return btn;
    }

    private static class IconBox extends JPanel {
        private final Color iconColor;
        private final Color bgColor;
        private final String type;

        IconBox(Color iconColor, Color bgColor, String type) {
            this.iconColor = iconColor;
            this.bgColor = bgColor;
            this.type = type;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bgColor);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
            g2.setColor(iconColor);
            g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            int cx = getWidth() / 2;
            int cy = getHeight() / 2;
            if ("box".equals(type)) {
                int s = 11;
                g2.drawPolygon(new int[] { cx, cx + s, cx, cx - s }, new int[] { cy - s, cy - s / 2, cy, cy - s / 2 },
                        4);
                g2.drawPolygon(new int[] { cx - s, cx, cx, cx - s }, new int[] { cy - s / 2, cy, cy + s, cy + s / 2 },
                        4);
                g2.drawPolygon(new int[] { cx, cx + s, cx + s, cx }, new int[] { cy, cy - s / 2, cy + s / 2, cy + s },
                        4);
            } else if ("list".equals(type)) {
                for (int i = -8; i <= 8; i += 8) {
                    g2.fillOval(cx - 11, cy + i - 2, 4, 4);
                    g2.drawLine(cx - 3, cy + i, cx + 12, cy + i);
                }
            } else {
                g2.drawPolygon(new int[] { cx, cx - 13, cx + 13 }, new int[] { cy - 15, cy + 13, cy + 13 }, 3);
                g2.drawLine(cx, cy - 6, cx, cy + 4);
                g2.fillOval(cx - 1, cy + 8, 3, 3);
            }
            g2.dispose();
        }
    }

    private static class TextCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focus,
                int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, selected, focus, row, column);
            label.setBorder(new EmptyBorder(0, 14, 0, 8));
            label.setFont(new Font("Segoe UI", column == 0 || column == 1 ? Font.PLAIN : Font.PLAIN, 13));
            return label;
        }
    }

    private class DetailButtonRenderer extends JPanel implements TableCellRenderer {
        private final JButton button = buildPrimaryButton("Chi tiết", 86, 30);

        DetailButtonRenderer() {
            setLayout(new GridBagLayout());
            setOpaque(true);
            add(button);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focus,
                int row, int column) {
            setBackground(selected ? table.getSelectionBackground()
                    : (row % 2 == 0 ? AppColor.BACKGROUND : AppColor.SECONDARY_HOVER));
            return this;
        }
    }

    private class DetailButtonEditor extends AbstractCellEditor implements TableCellEditor {
        private final JPanel panel = new JPanel(new GridBagLayout());
        private final JButton button = buildPrimaryButton("Chi tiết", 86, 30);
        private String maDH;

        DetailButtonEditor() {
            panel.setOpaque(true);
            panel.add(button);
            button.addActionListener(e -> {
                fireEditingStopped();
                if (maDH != null && !maDH.isBlank()) {
                    openDetailDialog(maDH);
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean selected, int row,
                int column) {
            maDH = table.getValueAt(row, 0).toString();
            panel.setBackground(table.getSelectionBackground());
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "Chi tiết";
        }
    }

    private static class StatusBadgeRenderer extends JPanel implements TableCellRenderer {
        private String text = "";

        StatusBadgeRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focus,
                int row, int column) {
            text = value == null ? "" : value.toString();
            setBackground(selected ? table.getSelectionBackground()
                    : (row % 2 == 0 ? AppColor.BACKGROUND : AppColor.SECONDARY_HOVER));
            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (text.isBlank())
                return;
            boolean done = text.toLowerCase().contains("đã") || text.toLowerCase().contains("hoàn");
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
            FontMetrics fm = g2.getFontMetrics();
            int w = fm.stringWidth(text) + 22;
            int h = fm.getHeight() + 6;
            
            // Fix ghosting by using fixed width/height
            int x = Math.max(6, (getWidth() - w) / 2);
            int y = (getHeight() - h) / 2;
            g2.setColor(done ? PRIMARY_LIGHT : WARNING_LIGHT);
            g2.fillRoundRect(x, y, w, h, h, h);
            g2.setColor(done ? AppColor.SUCCESS_ACTIVE : AppColor.WARNING_ACTIVE);
            g2.drawString(text, x + 11, y + fm.getAscent() + 3);
            g2.dispose();
        }
    }

    private static class SearchIconV2 implements Icon {
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(AppColor.TEXT_SECONDARY);
            g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawOval(x + 2, y + 2, 11, 11);
            g2.drawLine(x + 12, y + 12, x + 17, y + 17);
            g2.dispose();
        }
        @Override public int getIconWidth() { return 20; }
        @Override public int getIconHeight() { return 20; }
    }

    @FunctionalInterface
    private interface SimpleDocumentListener extends javax.swing.event.DocumentListener {
        void update(javax.swing.event.DocumentEvent e);

        @Override
        default void insertUpdate(javax.swing.event.DocumentEvent e) {
            update(e);
        }

        @Override
        default void removeUpdate(javax.swing.event.DocumentEvent e) {
            update(e);
        }

        @Override
        default void changedUpdate(javax.swing.event.DocumentEvent e) {
            update(e);
        }
    }
}
