package gui.khohang;

import bus.XuatKhoBUS;
import dto.XuatKhoDTO;
import util.AppColor;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class XuatKhoPanel extends JPanel {

    private static final java.util.logging.Logger logger = java.util.logging.Logger
            .getLogger(XuatKhoPanel.class.getName());

    private static final Color PRIMARY_LIGHT = new Color(
            AppColor.PRIMARY.getRed(), AppColor.PRIMARY.getGreen(), AppColor.PRIMARY.getBlue(), 30);
    private static final Color WARNING_LIGHT = new Color(
            AppColor.WARNING.getRed(), AppColor.WARNING.getGreen(), AppColor.WARNING.getBlue(), 30);

    private JTable tbPending;
    private JButton btnCreateRequest;
    private JTextField txtSearch;
    private JLabel lblCount;
    private DefaultTableModel tableModel;

    private List<Object[]> allData = new ArrayList<>();
    private List<Object[]> filteredData = new ArrayList<>();
    private int currentPage = 1;
    private int rowsPerPage = 6;
    private JLabel lblPageIndicator;
    private JButton btnPrev, btnNext;
    private String currentSortKey = null;
    private JButton btnSort;

    // Summary Card Labels
    private JLabel lblTongDonHang;
    private JLabel lblTongSanPham;
    private JLabel lblDonHangGiam;

    private static final String[] COL_NAMES = {
            "MÃ ĐƠN HÀNG", "KHÁCH HÀNG", "SỐ SP", "TỔNG SL", "TRẠNG THÁI"
    };

    public XuatKhoPanel() {
        initUI();
        setupTable();
        loadDataToTable();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(AppColor.BACKGROUND);
        setBorder(new EmptyBorder(24, 28, 24, 28));

        // Group chứa Top Bar và Summary Cards
        JPanel topWrap = new JPanel();
        topWrap.setLayout(new BoxLayout(topWrap, BoxLayout.Y_AXIS));
        topWrap.setOpaque(false);

        // 1. Top Bar
        JPanel topBar = new RoundedPanel(14, AppColor.SURFACE);
        topBar.setLayout(new BorderLayout());
        topBar.setBorder(new CompoundBorder(
                new LineBorder(AppColor.BORDER, 1, true),
                new EmptyBorder(16, 22, 16, 22)));
        topBar.add(buildHeader(), BorderLayout.CENTER);
        topBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        topBar.setAlignmentX(Component.LEFT_ALIGNMENT);
        topWrap.add(topBar);

        topWrap.add(Box.createVerticalStrut(20));

        // 2. Summary Cards
        JPanel cardsPanel = buildSummaryCards();
        cardsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        cardsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        topWrap.add(cardsPanel);

        topWrap.add(Box.createVerticalStrut(20));
        add(topWrap, BorderLayout.NORTH);

        // 3. Table Area
        JPanel tablePanel = new RoundedPanel(14, AppColor.SURFACE);
        tablePanel.setLayout(new BorderLayout());
        tablePanel.setBorder(new LineBorder(AppColor.BORDER, 1, true));
        tablePanel.add(buildTableArea(), BorderLayout.CENTER);
        tablePanel.add(buildFooter(), BorderLayout.SOUTH);

        add(tablePanel, BorderLayout.CENTER);
    }

    private JPanel buildSummaryCards() {
        JPanel p = new JPanel(new GridLayout(1, 3, 20, 0));
        p.setOpaque(false);

        lblTongDonHang = new JLabel("0");
        lblTongSanPham = new JLabel("0");
        lblDonHangGiam = new JLabel("0");

        p.add(buildSingleCard("Đơn hàng chờ", lblTongDonHang,
                new Color(33, 102, 240), new Color(230, 240, 255), "box"));

        p.add(buildSingleCard("Tổng sản phẩm", lblTongSanPham,
                new Color(245, 115, 30), new Color(255, 240, 230), "list"));

        p.add(buildSingleCard("Cần xử lý ngay", lblDonHangGiam,
                new Color(235, 50, 50), new Color(255, 235, 235), "urgent"));

        return p;
    }

    private JPanel buildSingleCard(String title, JLabel lblValue, Color iconColor, Color bgColor, String iconType) {
        JPanel card = new RoundedPanel(14, AppColor.SURFACE);
        card.setLayout(new BorderLayout());
        card.setBorder(new CompoundBorder(
                new LineBorder(AppColor.BORDER, 1, true),
                new EmptyBorder(16, 20, 16, 20)));

        JPanel left = new JPanel(new GridLayout(2, 1, 0, 5));
        left.setOpaque(false);
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblTitle.setForeground(AppColor.TEXT_SECONDARY);

        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblValue.setForeground(AppColor.TEXT_PRIMARY);

        left.add(lblTitle);
        left.add(lblValue);

        JPanel right = new JPanel(new GridBagLayout());
        right.setOpaque(false);

        JPanel iconWrap = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bgColor);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));

                g2.setColor(iconColor);
                g2.setStroke(new java.awt.BasicStroke(2f, java.awt.BasicStroke.CAP_ROUND, java.awt.BasicStroke.JOIN_ROUND));

                int w = getWidth(), h = getHeight();
                if ("box".equals(iconType)) {
                    int cx = w / 2, cy = h / 2 + 2;
                    int size = 11;
                    g2.drawPolygon(new int[] { cx, cx + size, cx, cx - size }, new int[] { cy - size, cy - size / 2, cy, cy - size / 2 }, 4);
                    g2.drawPolygon(new int[] { cx - size, cx, cx, cx - size }, new int[] { cy - size / 2, cy, cy + size, cy + size / 2 }, 4);
                    g2.drawPolygon(new int[] { cx, cx + size, cx + size, cx }, new int[] { cy, cy - size / 2, cy + size / 2, cy + size }, 4);
                } else if ("list".equals(iconType)) {
                    int cx = w / 2, cy = h / 2;
                    g2.drawLine(cx - 2, cy - 6, cx + 8, cy - 6);
                    g2.drawLine(cx - 2, cy, cx + 8, cy);
                    g2.drawLine(cx - 2, cy + 6, cx + 8, cy + 6);
                    g2.fillOval(cx - 8, cy - 7, 3, 3);
                    g2.fillOval(cx - 8, cy - 1, 3, 3);
                    g2.fillOval(cx - 8, cy + 5, 3, 3);
                } else {
                    int cx = w / 2, cy = h / 2 + 2;
                    int size = 10;
                    g2.drawPolygon(new int[] { cx, cx - size - 2, cx + size + 2 }, new int[] { cy - size - 4, cy + size, cy + size }, 3);
                    g2.drawLine(cx, cy - 5, cx, cy + 2);
                    g2.drawLine(cx, cy + 6, cx, cy + 6);
                }
                g2.dispose();
            }
        };
        iconWrap.setOpaque(false);
        iconWrap.setPreferredSize(new Dimension(52, 52));
        right.add(iconWrap);

        card.add(left, BorderLayout.CENTER);
        card.add(right, BorderLayout.EAST);
        return card;
    }

    private void updateSummaryCards() {
        if (lblTongDonHang == null) return;
        lblTongDonHang.setText(String.valueOf(allData.size()));
        long totalQty = allData.stream().mapToLong(r -> (long) Double.parseDouble(r[3].toString())).sum();
        lblTongSanPham.setText(String.format("%,d", totalQty));
        lblDonHangGiam.setText(String.valueOf(allData.size())); // Giả định là tất cả đều cần xử lý
    }

    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setOpaque(false);

        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titleRow.setOpaque(false);
        JLabel title = new JLabel("Quản lý đơn hàng chờ xuất kho");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(AppColor.TEXT_PRIMARY);
        titleRow.add(title);

        JPanel ctrlsRow = new JPanel(new BorderLayout());
        ctrlsRow.setOpaque(false);

        JPanel leftGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        leftGroup.setOpaque(false);
        btnCreateRequest = buildFillBtn("Tiến hành xuất kho", 180);
        btnCreateRequest.addActionListener(this::onCreateRequest);
        leftGroup.add(btnCreateRequest);

        JButton btnRefresh = buildOutlineBtn("Làm mới", 90);
        btnRefresh.addActionListener(e -> loadDataToTable());
        leftGroup.add(btnRefresh);
        ctrlsRow.add(leftGroup, BorderLayout.WEST);

        JPanel rightGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightGroup.setOpaque(false);
        rightGroup.add(buildSearchWrap());

        btnSort = buildSortDropdownBtn();
        btnSort.addActionListener(e -> showSortMenu(btnSort));
        rightGroup.add(btnSort);
        ctrlsRow.add(rightGroup, BorderLayout.EAST);

        p.add(titleRow, BorderLayout.NORTH);
        p.add(ctrlsRow, BorderLayout.CENTER);
        return p;
    }

    private JScrollPane buildTableArea() {
        tbPending = new JTable() {
            @Override
            public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? AppColor.BACKGROUND : AppColor.SECONDARY_HOVER);
                    c.setForeground(AppColor.TEXT_PRIMARY);
                } else {
                    c.setBackground(PRIMARY_LIGHT);
                    c.setForeground(AppColor.PRIMARY_ACTIVE);
                }
                return c;
            }
        };
        tbPending.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tbPending.setRowHeight(55);
        tbPending.setShowVerticalLines(false);
        tbPending.setShowHorizontalLines(true);
        tbPending.setGridColor(AppColor.BORDER);
        tbPending.setSelectionBackground(PRIMARY_LIGHT);
        tbPending.setSelectionForeground(AppColor.PRIMARY_ACTIVE);
        tbPending.setIntercellSpacing(new Dimension(0, 0));
        tbPending.setFocusable(false);

        JTableHeader th = tbPending.getTableHeader();
        th.setFont(new Font("Segoe UI", Font.BOLD, 11));
        th.setBackground(AppColor.BACKGROUND);
        th.setForeground(AppColor.TEXT_SECONDARY);
        th.setPreferredSize(new Dimension(0, 45));
        th.setBorder(new MatteBorder(0, 0, 1, 0, AppColor.BORDER));
        th.setReorderingAllowed(false);

        JScrollPane sp = new JScrollPane(tbPending);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(AppColor.SURFACE);
        return sp;
    }

    private JPanel buildFooter() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(AppColor.SURFACE);
        p.setBorder(new CompoundBorder(new MatteBorder(1, 0, 0, 0, AppColor.BORDER), new EmptyBorder(12, 22, 14, 22)));

        lblCount = new JLabel("Hiển thị 0 - 0 của 0 đơn hàng");
        lblCount.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblCount.setForeground(AppColor.TEXT_SECONDARY);
        p.add(lblCount, BorderLayout.WEST);

        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        paginationPanel.setOpaque(false);

        btnPrev = buildPageBtn("<");
        btnPrev.addActionListener(e -> { if (currentPage > 1) { currentPage--; renderTablePage(); } });

        lblPageIndicator = new JLabel(" 1 ", SwingConstants.CENTER);
        lblPageIndicator.setOpaque(true);
        lblPageIndicator.setBackground(AppColor.PRIMARY);
        lblPageIndicator.setForeground(Color.WHITE);
        lblPageIndicator.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPageIndicator.setPreferredSize(new Dimension(30, 30));

        btnNext = buildPageBtn(">");
        btnNext.addActionListener(e -> {
            int totalPages = (int) Math.ceil((double) filteredData.size() / rowsPerPage);
            if (currentPage < totalPages) { currentPage++; renderTablePage(); }
        });

        paginationPanel.add(btnPrev);
        paginationPanel.add(lblPageIndicator);
        paginationPanel.add(btnNext);
        p.add(paginationPanel, BorderLayout.EAST);

        return p;
    }

    private JButton buildPageBtn(String text) {
        JButton b = new JButton(text);
        b.setPreferredSize(new Dimension(30, 30));
        b.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        b.setBackground(Color.WHITE);
        b.setForeground(AppColor.TEXT_SECONDARY);
        b.setFocusPainted(false);
        b.setBorder(new LineBorder(AppColor.BORDER));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    private void setupTable() {
        tableModel = new DefaultTableModel(COL_NAMES, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tbPending.setModel(tableModel);

        DefaultTableCellRenderer hr = new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                l.setFont(new Font("Segoe UI", Font.BOLD, 11));
                l.setForeground(AppColor.TEXT_SECONDARY);
                l.setBackground(AppColor.BACKGROUND);
                l.setBorder(new CompoundBorder(new MatteBorder(0, 0, 1, 0, AppColor.BORDER), new EmptyBorder(0, c == 0 ? 14 : 10, 0, 6)));
                return l;
            }
        };
        for (int i = 0; i < tbPending.getColumnCount(); i++) tbPending.getColumnModel().getColumn(i).setHeaderRenderer(hr);

        tbPending.getColumnModel().getColumn(4).setCellRenderer(new BadgeLabel());
        int[] widths = { 120, 180, 80, 100, 140 };
        for (int i = 0; i < widths.length; i++) tbPending.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
    }

    public void loadDataToTable() {
        try {
            allData = new XuatKhoBUS().getDanhSachDonHangChoXuat();
            if (txtSearch != null) { txtSearch.setText("Tìm kiếm đơn hàng..."); txtSearch.setForeground(AppColor.TEXT_SECONDARY); }
            currentSortKey = null;
            filterTable();
        } catch (Exception e) {
            logger.warning("Lỗi load data: " + e.getMessage());
        }
    }

    private void renderTablePage() {
        tableModel.setRowCount(0);
        int totalItems = filteredData.size();
        int totalPages = (int) Math.ceil((double) totalItems / rowsPerPage);
        if (currentPage < 1) currentPage = 1;
        if (currentPage > totalPages && totalPages > 0) currentPage = totalPages;
        int start = (currentPage - 1) * rowsPerPage;
        int end = Math.min(start + rowsPerPage, totalItems);
        for (int i = start; i < end; i++) tableModel.addRow(filteredData.get(i));
        lblPageIndicator.setText(" " + (totalItems == 0 ? 0 : currentPage) + " ");
        btnPrev.setEnabled(currentPage > 1);
        btnNext.setEnabled(currentPage < totalPages);
        if (totalItems == 0) lblCount.setText("Không tìm thấy đơn hàng nào");
        else lblCount.setText(String.format("Hiển thị %d - %d của %d đơn hàng", (start + 1), end, totalItems));
        updateSummaryCards();
    }

    private void onCreateRequest(ActionEvent e) {
        int row = tbPending.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Vui lòng chọn đơn hàng!", "Thông báo", JOptionPane.WARNING_MESSAGE); return; }
        String maDH = tbPending.getValueAt(row, 0).toString();
        SoanHangDialog d = new SoanHangDialog((Frame) SwingUtilities.getWindowAncestor(this), maDH);
        d.setVisible(true);
        loadDataToTable();
    }

    private JPanel buildSearchWrap() {
        boolean[] focused = { false };
        JPanel w = new JPanel(new BorderLayout(6, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.dispose();
            }
            @Override protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(focused[0] ? AppColor.PRIMARY : AppColor.BORDER);
                g2.setStroke(new java.awt.BasicStroke(focused[0] ? 1.5f : 1f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, 8, 8));
                g2.dispose();
            }
        };
        w.setBackground(AppColor.SURFACE);
        w.setBorder(new EmptyBorder(0, 10, 0, 10));
        w.setOpaque(false);
        w.setPreferredSize(new Dimension(280, 36));

        txtSearch = new JTextField("Tìm kiếm đơn hàng...");
        txtSearch.setBorder(BorderFactory.createEmptyBorder());
        txtSearch.setOpaque(false);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtSearch.setForeground(AppColor.TEXT_SECONDARY);
        txtSearch.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                focused[0] = true; w.repaint();
                if (txtSearch.getText().equals("Tìm kiếm đơn hàng...")) { txtSearch.setText(""); txtSearch.setForeground(AppColor.TEXT_PRIMARY); }
            }
            public void focusLost(FocusEvent e) {
                focused[0] = false; w.repaint();
                if (txtSearch.getText().isEmpty()) { txtSearch.setText("Tìm kiếm đơn hàng..."); txtSearch.setForeground(AppColor.TEXT_SECONDARY); }
            }
        });
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
        });
        w.add(txtSearch, BorderLayout.CENTER);
        return w;
    }

    private void filterTable() {
        String k = txtSearch.getText().trim().toLowerCase();
        if (k.equals("tìm kiếm đơn hàng...") || k.isEmpty()) filteredData = new ArrayList<>(allData);
        else filteredData = allData.stream().filter(r -> r[0].toString().toLowerCase().contains(k) || r[1].toString().toLowerCase().contains(k)).collect(Collectors.toList());
        sortData();
        currentPage = 1;
        renderTablePage();
    }

    private void sortData() {
        if (currentSortKey == null) return;
        java.util.Comparator<Object[]> comp = switch (currentSortKey) {
            case "madh_asc" -> java.util.Comparator.comparing(r -> r[0].toString());
            case "madh_desc" -> java.util.Comparator.<Object[], String>comparing(r -> r[0].toString()).reversed();
            case "sl_asc" -> java.util.Comparator.comparingDouble(r -> Double.parseDouble(r[3].toString()));
            case "sl_desc" -> java.util.Comparator.<Object[], Double>comparing(r -> Double.parseDouble(r[3].toString())).reversed();
            default -> null;
        };
        if (comp != null) filteredData.sort(comp);
    }

    private void showSortMenu(JButton anchor) {
        JPopupMenu menu = new JPopupMenu();
        String[][] opts = { {"madh_asc", "Mã ĐH tăng dần"}, {"madh_desc", "Mã ĐH giảm dần"}, {"sl_asc", "Tổng SL tăng dần"}, {"sl_desc", "Tổng SL giảm dần"} };
        for (String[] o : opts) {
            JMenuItem item = new JMenuItem(o[1]);
            item.addActionListener(e -> { currentSortKey = o[0]; filterTable(); });
            menu.add(item);
        }
        menu.show(anchor, 0, anchor.getHeight());
    }

    private JButton buildSortDropdownBtn() {
        JButton b = new JButton("Sắp xếp") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AppColor.SURFACE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.setColor(AppColor.BORDER);
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setPreferredSize(new Dimension(140, 36));
        b.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        b.setOpaque(false); b.setContentAreaFilled(false);
        return b;
    }

    private JButton buildFillBtn(String text, int width) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? AppColor.PRIMARY_HOVER : AppColor.PRIMARY);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setForeground(Color.WHITE); b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setPreferredSize(new Dimension(width, 36)); b.setOpaque(false); b.setContentAreaFilled(false); b.setBorderPainted(false);
        return b;
    }

    private JButton buildOutlineBtn(String text, int width) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? AppColor.SECONDARY_HOVER : AppColor.SURFACE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.setColor(AppColor.BORDER);
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setForeground(AppColor.TEXT_SECONDARY); b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setPreferredSize(new Dimension(width, 36)); b.setOpaque(false); b.setContentAreaFilled(false); b.setBorderPainted(false);
        return b;
    }

    private static class BadgeLabel extends JPanel implements TableCellRenderer {
        private String txt = "";
        @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
            this.txt = v == null ? "" : v.toString();
            setBackground(sel ? t.getSelectionBackground() : (r % 2 == 0 ? AppColor.BACKGROUND : AppColor.SECONDARY_HOVER));
            return this;
        }
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g); if (txt.isEmpty()) return;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            boolean done = txt.toLowerCase().contains("đã") || txt.toLowerCase().contains("hoàn");
            g2.setColor(done ? PRIMARY_LIGHT : WARNING_LIGHT);
            FontMetrics fm = g2.getFontMetrics(new Font("Segoe UI", Font.BOLD, 11));
            int w = fm.stringWidth(txt) + 20, h = fm.getHeight() + 8;
            g2.fill(new RoundRectangle2D.Float((getWidth() - w) / 2, (getHeight() - h) / 2, w, h, h, h));
            g2.setColor(done ? AppColor.SUCCESS_ACTIVE : AppColor.WARNING_ACTIVE);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
            g2.drawString(txt, (getWidth() - w) / 2 + 10, (getHeight() - h) / 2 + fm.getAscent() + 4);
            g2.dispose();
        }
    }

    private static class RoundedPanel extends JPanel {
        private final int r;
        RoundedPanel(int r, Color bg) { this.r = r; setBackground(bg); setOpaque(false); }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), r, r));
            g2.dispose();
        }
    }
}