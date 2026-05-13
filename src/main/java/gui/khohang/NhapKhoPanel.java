package gui.khohang;

import bus.NhapKhoBUS;
import dto.TonKhoDTO;
import util.AppColor;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.RoundRectangle2D;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.toedter.calendar.JDateChooser;

public class NhapKhoPanel extends JPanel {

    private static final String SEARCH_PLACEHOLDER = "Tìm kiếm lô hàng...";
    private static final Color PRIMARY_LIGHT = new Color(22, 163, 74, 28);
    private static final Color WARNING_LIGHT = new Color(245, 158, 11, 24);

    private final String[] columns = {
            "MÃ LÔ HÀNG", "SẢN PHẨM", "SỐ LƯỢNG", "KHO",
            "LOẠI KHO", "VỊ TRÍ", "NGÀY HẾT HẠN", "TRẠNG THÁI"
    };

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private JLabel lblCount;
    private JLabel lblPage;
    private JLabel lblTongLoHang;
    private JLabel lblTongSanPham;
    private JLabel lblLoHangLon;
    private JButton btnPrev;
    private JButton btnNext;
    private JButton btnSort;

    private List<Object[]> allData = new ArrayList<>();
    private List<Object[]> filteredData = new ArrayList<>();
    private Map<String, String> khoLoaiKhoMap = new LinkedHashMap<>();
    private int currentPage = 1;
    private final int rowsPerPage = 6;
    private String currentSortKey;

    public NhapKhoPanel() {
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
        header.setBorder(new CompoundBorder(
                new LineBorder(AppColor.BORDER),
                new EmptyBorder(16, 20, 16, 20)));
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        header.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = new JLabel("Danh sách lô hàng nhập kho");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(AppColor.TEXT_PRIMARY);
        header.add(title, BorderLayout.NORTH);

        JPanel actions = new JPanel(new BorderLayout());
        actions.setOpaque(false);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
        left.setOpaque(false);
        JButton btnConfirm = buildPrimaryButton("Xác nhận nhập kho", 0, 36);
        btnConfirm.addActionListener(e -> onConfirmNhapKho());
        
        JButton btnRefresh = buildOutlineButton("Làm mới", 0, 36);
        btnRefresh.addActionListener(e -> loadDataToTable());
        
        left.add(btnConfirm);
        left.add(btnRefresh);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 4));
        right.setOpaque(false);
        right.add(buildSearchBox());
        
        btnSort = buildOutlineButton("Sắp xếp ▼", 0, 36);
        btnSort.addActionListener(e -> showSortMenu());
        right.add(btnSort);

        actions.add(left, BorderLayout.WEST);
        actions.add(right, BorderLayout.EAST);
        header.add(actions, BorderLayout.CENTER);
        return header;
    }

    private JPanel buildCards() {
        JPanel cards = new JPanel(new GridLayout(1, 3, 20, 0));
        cards.setOpaque(false);
        cards.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));
        cards.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblTongLoHang = new JLabel("0");
        lblTongSanPham = new JLabel("0");
        lblLoHangLon = new JLabel("0");

        cards.add(buildSummaryCard("Tổng lô hàng", lblTongLoHang, new Color(37, 99, 235), new Color(219, 234, 254),
                "box"));
        cards.add(buildSummaryCard("Tổng sản phẩm", lblTongSanPham, new Color(249, 115, 22), new Color(255, 237, 213),
                "list"));
        cards.add(buildSummaryCard("Lô hàng lớn", lblLoHangLon, new Color(239, 68, 68), new Color(254, 226, 226),
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
                return column == 3 || column == 5 || column == 6;
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
        table.getColumnModel().getColumn(0).setCellRenderer(new BoldCellRenderer(AppColor.TEXT_PRIMARY));
        table.getColumnModel().getColumn(1).setCellRenderer(new BoldCellRenderer(AppColor.TEXT_PRIMARY));
        table.getColumnModel().getColumn(4).setCellRenderer(new LoaiKhoBadgeRenderer());
        table.getColumnModel().getColumn(7).setCellRenderer(new StatusBadgeRenderer());

        loadKhoEditor();
        
        JComboBox<String> cbViTri = new JComboBox<>(new String[] { "", "A", "B", "C" });
        cbViTri.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbViTri.setBackground(Color.WHITE);
        DefaultCellEditor viTriEditor = new DefaultCellEditor(cbViTri);
        viTriEditor.setClickCountToStart(1);
        
        table.getColumnModel().getColumn(5).setCellEditor(viTriEditor);
        table.getColumnModel().getColumn(5).setCellRenderer(new PlaceholderRenderer("Chọn vị trí..."));

        table.getColumnModel().getColumn(6).setCellEditor(new DateChooserEditor());
        table.getColumnModel().getColumn(6).setCellRenderer(new DateCellRenderer());

        int[] widths = { 115, 180, 80, 150, 100, 110, 130, 125 };
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
    }

    private void loadKhoEditor() {
        try {
            khoLoaiKhoMap = new NhapKhoBUS().getKhoLoaiKhoMap();
        } catch (Exception e) {
            khoLoaiKhoMap = new LinkedHashMap<>();
        }

        JComboBox<String> cbKho = new JComboBox<>();
        cbKho.addItem("");
        khoLoaiKhoMap.keySet().forEach(cbKho::addItem);
        cbKho.setRenderer(new KhoComboRenderer(khoLoaiKhoMap));
        cbKho.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbKho.setBackground(Color.WHITE);

        DefaultCellEditor khoEditor = new DefaultCellEditor(cbKho);
        khoEditor.setClickCountToStart(1);
        
        table.getColumnModel().getColumn(3).setCellEditor(khoEditor);
        table.getColumnModel().getColumn(3).setCellRenderer(new KhoTableRenderer(khoLoaiKhoMap));
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(AppColor.SURFACE);
        footer.setBorder(new CompoundBorder(
                new MatteBorder(1, 0, 0, 0, AppColor.BORDER),
                new EmptyBorder(12, 22, 14, 22)));

        lblCount = new JLabel("Hiển thị 0 - 0 của 0 lô hàng");
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
        allData = new NhapKhoBUS().getDanhSachNhapKho();
        allData.removeIf(row -> row[7] != null && row[7].toString().toLowerCase().contains("đã"));
        if (txtSearch != null) {
            txtSearch.setText(SEARCH_PLACEHOLDER);
            txtSearch.setForeground(AppColor.TEXT_SECONDARY);
        }
        currentSortKey = null;
        if (btnSort != null) {
            btnSort.setText("Sắp xếp ▼");
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
                    .filter(row -> java.util.Arrays.stream(row)
                            .anyMatch(cell -> cell != null && cell.toString().toLowerCase().contains(keyword)))
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
            case "sl_asc" ->
                filteredData.sort(java.util.Comparator.comparingInt(r -> Integer.parseInt(r[2].toString())));
            case "sl_desc" -> filteredData.sort(
                    java.util.Comparator.<Object[]>comparingInt(r -> Integer.parseInt(r[2].toString())).reversed());
            case "ma_asc" -> filteredData.sort(java.util.Comparator.comparing(r -> r[0].toString()));
            case "ma_desc" ->
                filteredData.sort(java.util.Comparator.<Object[], String>comparing(r -> r[0].toString()).reversed());
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
            tableModel.addRow(filteredData.get(i));
        }

        lblPage.setText(total == 0 ? "0" : String.valueOf(currentPage));
        lblCount.setText(total == 0
                ? "Không tìm thấy lô hàng nào"
                : String.format("Hiển thị %d - %d của %d lô hàng", start + 1, end, total));
        btnPrev.setEnabled(currentPage > 1);
        btnNext.setEnabled(currentPage < totalPages);
        updateSummary();
    }

    private void updateSummary() {
        lblTongLoHang.setText(String.valueOf(allData.size()));
        long totalQty = allData.stream().mapToLong(row -> Long.parseLong(row[2].toString())).sum();
        lblTongSanPham.setText(String.format("%,d", totalQty));
        long bigLots = allData.stream().filter(row -> Long.parseLong(row[2].toString()) >= 100).count();
        lblLoHangLon.setText(String.valueOf(bigLots));
    }

    private void changePage(int delta) {
        currentPage += delta;
        renderTablePage();
    }

    private void showSortMenu() {
        JPopupMenu menu = new JPopupMenu();
        addSort(menu, "Số lượng tăng dần", "sl_asc");
        addSort(menu, "Số lượng giảm dần", "sl_desc");
        addSort(menu, "Mã lô hàng tăng dần", "ma_asc");
        addSort(menu, "Mã lô hàng giảm dần", "ma_desc");
        menu.show(btnSort, 0, btnSort.getHeight());
    }

    private void addSort(JPopupMenu menu, String label, String key) {
        JMenuItem item = new JMenuItem(label);
        item.addActionListener(e -> {
            currentSortKey = key;
            btnSort.setText(label);
            filterTable();
        });
        menu.add(item);
    }

    private void onConfirmNhapKho() {
        if (table.isEditing()) {
            table.getCellEditor().stopCellEditing();
        }

        int row = table.getSelectedRow();
        if (row < 0) {
            warn("Vui lòng chọn một lô hàng cần nhập kho!");
            return;
        }

        String maCTLH = getCell(row, 0);
        String tenSP = getCell(row, 1);
        String maKho = getCell(row, 3);
        String viTri = getCell(row, 5);
        Date ngayHetHan = parseDate(table.getValueAt(row, 6));

        if (maKho.isEmpty()) {
            warn("Vui lòng chọn kho cho lô hàng " + maCTLH + "!");
            return;
        }
        if (viTri.isEmpty()) {
            warn("Vui lòng chọn vị trí cho lô hàng " + maCTLH + "!");
            return;
        }
        if (ngayHetHan == null) {
            warn("Ngày hết hạn không hợp lệ! Định dạng: d/M/yyyy");
            return;
        }

        TonKhoDTO dto = new TonKhoDTO();
        dto.setMaCTLH(maCTLH);
        dto.setMaKho(maKho);
        dto.setViTri(viTri);
        dto.setTgHetHan(ngayHetHan);

        String result = new NhapKhoBUS().xacNhanNhapKho(dto, tenSP);
        if ("SUCCESS".equals(result)) {
            JOptionPane.showMessageDialog(this, "Nhập kho thành công!\nLô: " + maCTLH,
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadDataToTable();
        } else {
            JOptionPane.showMessageDialog(this, result, "Không thể nhập kho", JOptionPane.WARNING_MESSAGE);
        }
    }

    private String getCell(int row, int col) {
        Object value = table.getValueAt(row, col);
        return value == null ? "" : value.toString().trim();
    }

    private Date parseDate(Object value) {
        if (value instanceof Date date)
            return date;
        if (value == null || value.toString().trim().isEmpty())
            return null;
        try {
            return new SimpleDateFormat("dd/MM/yyyy").parse(value.toString().trim());
        } catch (Exception e) {
            return null;
        }
    }

    private void warn(String message) {
        JOptionPane.showMessageDialog(this, message, "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
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
                // Do nothing here, painted in paintComponent
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
                
                // Vẽ viền hơi đậm hơn một chút để tạo độ sắc nét
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

    private static String formatKho(String maKho, Map<String, String> map) {
        String loaiKho = map.get(maKho);
        return loaiKho == null || loaiKho.isBlank() ? maKho : maKho + " - " + loaiKho;
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

    private static class RefreshIcon implements Icon {
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(AppColor.TEXT_SECONDARY);
            g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawArc(x + 2, y + 2, 12, 12, 45, 270);
            g2.drawLine(x + 11, y + 2, x + 14, y + 5);
            g2.drawLine(x + 11, y + 8, x + 14, y + 5);
            g2.dispose();
        }

        @Override
        public int getIconWidth() { return 18; }
        @Override
        public int getIconHeight() { return 18; }
    }

    private static class TextCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focus,
                int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, selected, focus, row, column);
            label.setBorder(new EmptyBorder(0, 14, 0, 8));
            label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            return label;
        }
    }

    private static class BoldCellRenderer extends TextCellRenderer {
        private final Color color;

        BoldCellRenderer(Color color) {
            this.color = color;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focus,
                int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, selected, focus, row, column);
            label.setFont(new Font("Segoe UI", Font.BOLD, 13));
            label.setForeground(selected ? AppColor.PRIMARY_ACTIVE : color);
            return label;
        }
    }

    private static class PlaceholderRenderer extends TextCellRenderer {
        private final String placeholder;

        PlaceholderRenderer(String placeholder) {
            this.placeholder = placeholder;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focus,
                int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, selected, focus, row, column);
            if (value == null || value.toString().trim().isEmpty()) {
                label.setText(placeholder);
                label.setForeground(AppColor.TEXT_SECONDARY);
            } else {
                label.setForeground(selected ? AppColor.PRIMARY_ACTIVE : AppColor.TEXT_PRIMARY);
            }
            return label;
        }
    }

    private static class KhoComboRenderer extends DefaultListCellRenderer {
        private final Map<String, String> map;

        KhoComboRenderer(Map<String, String> map) {
            this.map = map;
        }

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean selected,
                boolean focus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, selected, focus);
            String maKho = value == null ? "" : value.toString();
            label.setText(formatKho(maKho, map));
            return label;
        }
    }

    private static class KhoTableRenderer extends PlaceholderRenderer {
        private final Map<String, String> map;

        KhoTableRenderer(Map<String, String> map) {
            super("Chọn kho...");
            this.map = map;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focus,
                int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, selected, focus, row, column);
            String maKho = value == null ? "" : value.toString().trim();
            if (!maKho.isEmpty()) {
                label.setText(formatKho(maKho, map));
            }
            return label;
        }
    }

    private static class DateCellRenderer extends PlaceholderRenderer {
        private final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

        DateCellRenderer() {
            super("Chọn ngày...");
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focus,
                int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, selected, focus, row, column);
            if (value instanceof Date date) {
                label.setText(format.format(date));
            }
            return label;
        }
    }

    private static class LoaiKhoBadgeRenderer extends JPanel implements TableCellRenderer {
        private String text = "";

        LoaiKhoBadgeRenderer() {
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
            Color bg;
            Color fg;
            switch (text.toLowerCase()) {
                case "đông" -> {
                    bg = new Color(219, 234, 254);
                    fg = new Color(29, 78, 216);
                }
                case "lạnh" -> {
                    bg = new Color(204, 251, 241);
                    fg = new Color(15, 118, 110);
                }
                default -> {
                    bg = new Color(236, 252, 203);
                    fg = new Color(77, 124, 15);
                }
            }
            paintBadge(g, text, bg, fg, getWidth(), getHeight());
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
            boolean done = text.toLowerCase().contains("đã");
            paintBadge(g, text, done ? PRIMARY_LIGHT : WARNING_LIGHT,
                    done ? AppColor.SUCCESS_ACTIVE : AppColor.WARNING_ACTIVE, getWidth(), getHeight());
        }
    }

    private static void paintBadge(Graphics g, String text, Color bg, Color fg, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        FontMetrics fm = g2.getFontMetrics();
        int w = fm.stringWidth(text) + 22;
        int h = fm.getHeight() + 6;
        int x = Math.max(6, (width - w) / 2);
        int y = (height - h) / 2;
        g2.setColor(bg);
        g2.fillRoundRect(x, y, w, h, h, h);
        g2.setColor(fg);
        g2.drawString(text, x + 11, y + fm.getAscent() + 3);
        g2.dispose();
    }

    private static class DateChooserEditor extends AbstractCellEditor implements TableCellEditor {
        private final JDateChooser dateChooser = new JDateChooser();

        DateChooserEditor() {
            dateChooser.setDateFormatString("dd/MM/yyyy");
            dateChooser.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            dateChooser.getCalendarButton().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            dateChooser.getCalendarButton().setBackground(Color.WHITE);
            dateChooser.getCalendarButton().setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 4));
            dateChooser.setBorder(BorderFactory.createEmptyBorder());
            dateChooser.setBackground(Color.WHITE);
            
            // Ngăn người dùng nhập tay vào ô văn bản, chỉ cho chọn từ lịch
            if (dateChooser.getDateEditor() instanceof com.toedter.calendar.JTextFieldDateEditor editor) {
                editor.setEditable(false);
                editor.setBackground(Color.WHITE);
            }
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean selected, int row, int column) {
            if (value instanceof Date date) {
                dateChooser.setDate(date);
            } else if (value != null && !value.toString().isBlank()) {
                try {
                    dateChooser.setDate(new SimpleDateFormat("dd/MM/yyyy").parse(value.toString()));
                } catch (Exception e) {
                    dateChooser.setDate(null);
                }
            } else {
                dateChooser.setDate(null);
            }
            return dateChooser;
        }

        @Override
        public Object getCellEditorValue() {
            return dateChooser.getDate();
        }
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
