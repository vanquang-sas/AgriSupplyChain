package gui.panel;

import bus.NhapKhoBUS;
import bus.LoHangBUS;
import bus.SanPhamBUS;
import dto.TonKhoDTO;
import dto.LoHangDTO;
import dto.ChiTietLoHangDTO;
import dto.SanPhamDTO;
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
import com.toedter.calendar.JDateChooser;

public class NhapKhoPanel extends JPanel {

    private static final String SEARCH_PLACEHOLDER = "Tìm kiếm lô hàng...";
    private static final Color PRIMARY_LIGHT = new Color(22, 163, 74, 28);
    private static final Color WARNING_LIGHT = new Color(245, 158, 11, 24);

    private final String[] shipmentColumns = {
            "MÃ LÔ", "NGÀY NHẬP", "TỔNG TIỀN", "TRẠNG THÁI"
    };

    private final String[] detailColumns = {
            "MÃ SP", "SẢN PHẨM", "SỐ LƯỢNG", "KHO", "LOẠI KHO", "VỊ TRÍ", "NGÀY HẾT HẠN"
    };

    private JTable shipmentTable;
    private DefaultTableModel shipmentModel;
    private JTable detailTable;
    private DefaultTableModel detailModel;
    
    private JTextField txtSearch;
    private JLabel lblCount;
    private JLabel lblTongLoHang;
    private JLabel lblTongSanPham;
    private JLabel lblLoHangLon;
    private JButton btnSort;

    private List<LoHangDTO> allShipments = new ArrayList<>();
    private List<LoHangDTO> filteredShipments = new ArrayList<>();
    private Map<String, String> khoLoaiKhoMap = new LinkedHashMap<>();
    private String currentSortKey;
    private String selectedMaLH;

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
        content.add(buildMainSplitPanel());

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
        
        JButton btnReject = buildDangerButton("Từ chối nhập", 0, 36);
        btnReject.addActionListener(e -> onRejectNhapKho());
        
        JButton btnRefresh = buildOutlineButton("Làm mới", 0, 36);
        btnRefresh.addActionListener(e -> loadDataToTable());
        
        left.add(btnConfirm);
        left.add(btnReject);
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

    private JPanel buildMainSplitPanel() {
        JPanel splitWrapper = new JPanel(new BorderLayout(16, 0));
        splitWrapper.setOpaque(false);
        splitWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel leftPanel = buildShipmentsPanel();
        JPanel rightPanel = buildDetailCard();

        leftPanel.setPreferredSize(new Dimension(450, 0));

        splitWrapper.add(leftPanel, BorderLayout.WEST);
        splitWrapper.add(rightPanel, BorderLayout.CENTER);

        return splitWrapper;
    }

    private JPanel buildShipmentsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(AppColor.SURFACE);
        panel.setBorder(new LineBorder(AppColor.BORDER));

        shipmentModel = new DefaultTableModel(shipmentColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        shipmentTable = new JTable(shipmentModel) {
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
        shipmentTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        shipmentTable.setRowHeight(50);
        shipmentTable.setShowVerticalLines(false);
        shipmentTable.setShowHorizontalLines(false);
        shipmentTable.setGridColor(AppColor.BORDER);
        shipmentTable.setIntercellSpacing(new Dimension(0, 0));
        shipmentTable.setFocusable(false);
        shipmentTable.setSelectionBackground(PRIMARY_LIGHT);
        shipmentTable.setSelectionForeground(AppColor.PRIMARY_ACTIVE);

        JTableHeader header = shipmentTable.getTableHeader();
        header.setPreferredSize(new Dimension(0, 48));
        header.setFont(new Font("Segoe UI", Font.BOLD, 11));
        header.setBackground(AppColor.BACKGROUND);
        header.setForeground(AppColor.TEXT_SECONDARY);
        header.setReorderingAllowed(false);
        header.setBorder(new MatteBorder(0, 0, 1, 0, AppColor.BORDER));

        DefaultTableCellRenderer textRenderer = new TextCellRenderer();
        for (int i = 0; i < shipmentTable.getColumnCount(); i++) {
            shipmentTable.getColumnModel().getColumn(i).setCellRenderer(textRenderer);
        }
        shipmentTable.getColumnModel().getColumn(0).setCellRenderer(new BoldCellRenderer(AppColor.TEXT_PRIMARY));
        shipmentTable.getColumnModel().getColumn(3).setCellRenderer(new StatusBadgeRenderer());

        int[] widths = { 90, 110, 110, 120 };
        for (int i = 0; i < widths.length; i++) {
            shipmentTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        shipmentTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && shipmentTable.getSelectedRow() >= 0) {
                selectedMaLH = shipmentTable.getValueAt(shipmentTable.getSelectedRow(), 0).toString();
                showShipmentDetails(selectedMaLH);
            }
        });

        JScrollPane scrollPane = new JScrollPane(shipmentTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(AppColor.SURFACE);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buildFooter(), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildDetailCard() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(AppColor.SURFACE);
        panel.setBorder(new LineBorder(AppColor.BORDER));

        detailModel = new DefaultTableModel(detailColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3 || column == 5 || column == 6;
            }
        };

        detailTable = new JTable(detailModel) {
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
        detailTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        detailTable.setRowHeight(50);
        detailTable.setShowVerticalLines(false);
        detailTable.setShowHorizontalLines(false);
        detailTable.setGridColor(AppColor.BORDER);
        detailTable.setIntercellSpacing(new Dimension(0, 0));
        detailTable.setFocusable(false);
        detailTable.setSelectionBackground(PRIMARY_LIGHT);
        detailTable.setSelectionForeground(AppColor.PRIMARY_ACTIVE);

        JTableHeader header = detailTable.getTableHeader();
        header.setPreferredSize(new Dimension(0, 48));
        header.setFont(new Font("Segoe UI", Font.BOLD, 11));
        header.setBackground(AppColor.BACKGROUND);
        header.setForeground(AppColor.TEXT_SECONDARY);
        header.setReorderingAllowed(false);
        header.setBorder(new MatteBorder(0, 0, 1, 0, AppColor.BORDER));

        DefaultTableCellRenderer textRenderer = new TextCellRenderer();
        for (int i = 0; i < detailTable.getColumnCount(); i++) {
            detailTable.getColumnModel().getColumn(i).setCellRenderer(textRenderer);
        }
        detailTable.getColumnModel().getColumn(0).setCellRenderer(new BoldCellRenderer(AppColor.TEXT_PRIMARY));
        detailTable.getColumnModel().getColumn(4).setCellRenderer(new LoaiKhoBadgeRenderer());

        loadKhoEditor();
        
        JComboBox<String> cbViTri = new JComboBox<>(new String[] { "", "A", "B", "C" });
        cbViTri.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbViTri.setBackground(Color.WHITE);
        DefaultCellEditor viTriEditor = new DefaultCellEditor(cbViTri);
        viTriEditor.setClickCountToStart(1);
        
        detailTable.getColumnModel().getColumn(5).setCellEditor(viTriEditor);
        detailTable.getColumnModel().getColumn(5).setCellRenderer(new PlaceholderRenderer("Chọn vị trí..."));

        detailTable.getColumnModel().getColumn(6).setCellEditor(new DateChooserEditor());
        detailTable.getColumnModel().getColumn(6).setCellRenderer(new DateCellRenderer());

        int[] widths = { 80, 160, 80, 120, 90, 80, 120 };
        for (int i = 0; i < widths.length; i++) {
            detailTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        JScrollPane scrollPane = new JScrollPane(detailTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(AppColor.SURFACE);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
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
        
        detailTable.getColumnModel().getColumn(3).setCellEditor(khoEditor);
        detailTable.getColumnModel().getColumn(3).setCellRenderer(new KhoTableRenderer(khoLoaiKhoMap));
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(AppColor.SURFACE);
        footer.setBorder(new CompoundBorder(
                new MatteBorder(1, 0, 0, 0, AppColor.BORDER),
                new EmptyBorder(12, 22, 14, 22)));

        lblCount = new JLabel("Hiển thị 0 của 0 lô hàng");
        lblCount.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblCount.setForeground(AppColor.TEXT_SECONDARY);
        footer.add(lblCount, BorderLayout.WEST);
        return footer;
    }

    public void loadDataToTable() {
        try {
            List<LoHangDTO> list = new bus.LoHangBUS().getAll();
            allShipments = new ArrayList<>();
            for (LoHangDTO lh : list) {
                if (lh.getTrangThaiLH() != null && 
                    lh.getTrangThaiLH().equalsIgnoreCase("Chờ nhập kho")) {
                    allShipments.add(lh);
                }
            }
        } catch (Exception e) {
            allShipments = new ArrayList<>();
        }

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
        if (txtSearch == null) {
            filteredShipments = new ArrayList<>(allShipments);
            sortData();
            renderShipments();
            return;
        }
        String keyword = txtSearch.getText().trim().toLowerCase();
        if (keyword.isEmpty() || SEARCH_PLACEHOLDER.toLowerCase().equals(keyword)) {
            filteredShipments = new ArrayList<>(allShipments);
        } else {
            filteredShipments = new ArrayList<>();
            for (LoHangDTO lh : allShipments) {
                if (lh.getMaLH().toLowerCase().contains(keyword) || 
                    (lh.getTenNCC() != null && lh.getTenNCC().toLowerCase().contains(keyword)) ||
                    (lh.getTrangThaiLH() != null && lh.getTrangThaiLH().toLowerCase().contains(keyword))) {
                    filteredShipments.add(lh);
                }
            }
        }
        sortData();
        renderShipments();
    }

    private void sortData() {
        if (currentSortKey == null)
            return;
        switch (currentSortKey) {
            case "ma_asc" -> filteredShipments.sort(java.util.Comparator.comparing(r -> r.getMaLH()));
            case "ma_desc" -> filteredShipments.sort(java.util.Comparator.<LoHangDTO, String>comparing(r -> r.getMaLH()).reversed());
            case "tien_asc" -> filteredShipments.sort(java.util.Comparator.comparingDouble(r -> r.getTongTien()));
            case "tien_desc" -> filteredShipments.sort(java.util.Comparator.<LoHangDTO>comparingDouble(r -> r.getTongTien()).reversed());
            default -> {
            }
        }
    }

    private void renderShipments() {
        if (shipmentTable.isEditing()) {
            shipmentTable.getCellEditor().stopCellEditing();
        }
        shipmentModel.setRowCount(0);
        int total = filteredShipments.size();

        for (LoHangDTO lh : filteredShipments) {
            shipmentModel.addRow(new Object[] {
                lh.getMaLH(),
                new SimpleDateFormat("dd/MM/yyyy").format(lh.getTgNhap()),
                new java.text.DecimalFormat("#,###").format(lh.getTongTien()),
                lh.getTrangThaiLH()
            });
        }

        lblCount.setText(total == 0
                ? "Không tìm thấy lô hàng nào"
                : String.format("Hiển thị %d lô hàng", total));
        
        clearSelection();
        updateSummary();
    }

    private void clearSelection() {
        shipmentTable.clearSelection();
        detailModel.setRowCount(0);
        selectedMaLH = null;
    }

    private void updateSummary() {
        lblTongLoHang.setText(String.valueOf(allShipments.size()));
        long totalQty = 0;
        long bigLots = 0;
        LoHangBUS bus = new LoHangBUS();
        for (LoHangDTO lh : allShipments) {
            List<ChiTietLoHangDTO> details = bus.getChiTietLoHang(lh.getMaLH());
            long lhQty = 0;
            for (ChiTietLoHangDTO dt : details) {
                lhQty += dt.getSoLuong();
            }
            totalQty += lhQty;
            if (lhQty >= 100) {
                bigLots++;
            }
        }
        lblTongSanPham.setText(String.format("%,d", totalQty));
        lblLoHangLon.setText(String.valueOf(bigLots));
    }

    private void showSortMenu() {
        JPopupMenu menu = new JPopupMenu();
        addSort(menu, "Mã lô hàng tăng dần", "ma_asc");
        addSort(menu, "Mã lô hàng giảm dần", "ma_desc");
        addSort(menu, "Tổng tiền tăng dần", "tien_asc");
        addSort(menu, "Tổng tiền giảm dần", "tien_desc");
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

    private void showShipmentDetails(String maLH) {
        if (detailTable.isEditing()) {
            detailTable.getCellEditor().stopCellEditing();
        }
        detailModel.setRowCount(0);
        if (maLH == null || maLH.trim().isEmpty()) return;

        List<ChiTietLoHangDTO> details = new LoHangBUS().getChiTietLoHang(maLH);
        for (ChiTietLoHangDTO dt : details) {
            detailModel.addRow(new Object[] {
                dt.getMaSP(),
                findProductNameByMaSP(dt.getMaSP()),
                dt.getSoLuong(),
                "",
                findProductBaoQuanByMaSP(dt.getMaSP()),
                "",
                null
            });
        }
    }

    private String findProductNameByMaSP(String maSP) {
        if (maSP == null) return "";
        for (SanPhamDTO sp : new SanPhamBUS().getAll()) {
            if (maSP.equals(sp.getMaSP())) return sp.getTenSP();
        }
        return "";
    }

    private String findProductBaoQuanByMaSP(String maSP) {
        if (maSP == null) return "";
        for (SanPhamDTO sp : new SanPhamBUS().getAll()) {
            if (maSP.equals(sp.getMaSP())) return sp.getBaoQuan();
        }
        return "";
    }

    private void onConfirmNhapKho() {
        if (detailTable.isEditing()) {
            detailTable.getCellEditor().stopCellEditing();
        }

        int selectedRow = shipmentTable.getSelectedRow();
        if (selectedRow < 0) {
            warn("Vui lòng chọn một lô hàng cần nhập kho!");
            return;
        }

        String maLH = shipmentTable.getValueAt(selectedRow, 0).toString();
        List<TonKhoDTO> listTonKho = new ArrayList<>();
        List<String> listTenSP = new ArrayList<>();

        for (int i = 0; i < detailTable.getRowCount(); i++) {
            String maSP = getCell(detailTable, i, 0);
            String tenSP = getCell(detailTable, i, 1);
            String maKho = getCell(detailTable, i, 3);
            String viTri = getCell(detailTable, i, 5);
            Date ngayHetHan = parseDate(detailTable.getValueAt(i, 6));

            if (maKho.isEmpty()) {
                warn("Vui lòng chọn kho cho sản phẩm " + tenSP + "!");
                return;
            }
            if (viTri.isEmpty()) {
                warn("Vui lòng chọn vị trí cho sản phẩm " + tenSP + "!");
                return;
            }
            if (ngayHetHan == null) {
                warn("Vui lòng chọn ngày hết hạn cho sản phẩm " + tenSP + "!");
                return;
            }

            TonKhoDTO dto = new TonKhoDTO();
            dto.setMaLH(maLH);
            dto.setMaSP(maSP);
            dto.setMaKho(maKho);
            dto.setViTri(viTri);
            dto.setTgHetHan(ngayHetHan);

            listTonKho.add(dto);
            listTenSP.add(tenSP);
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn tiến hành nhập kho cho toàn bộ sản phẩm thuộc lô hàng " + maLH + " không?",
                "Xác nhận nhập kho lô hàng",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            NhapKhoBUS bus = new NhapKhoBUS();
            for (int i = 0; i < listTonKho.size(); i++) {
                String result = bus.xacNhanNhapKho(listTonKho.get(i), listTenSP.get(i));
                if (!"SUCCESS".equals(result)) {
                    JOptionPane.showMessageDialog(this, "Lỗi khi nhập kho sản phẩm " + listTenSP.get(i) + ":\n" + result,
                            "Lỗi nhập kho", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            JOptionPane.showMessageDialog(this, "Đã nhập kho toàn bộ sản phẩm của lô hàng " + maLH + " thành công!",
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadDataToTable();
        }
    }

    private void onRejectNhapKho() {
        if (shipmentTable.isEditing()) {
            shipmentTable.getCellEditor().stopCellEditing();
        }

        int selectedRow = shipmentTable.getSelectedRow();
        if (selectedRow < 0) {
            warn("Vui lòng chọn một lô hàng cần từ chối!");
            return;
        }

        String maLH = shipmentTable.getValueAt(selectedRow, 0).toString();

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn từ chối nhập kho cho toàn bộ lô hàng " + maLH + " không?",
                "Xác nhận từ chối lô hàng",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.OK_OPTION) {
            String result = new bus.NhapKhoBUS().tuChoiNhapKho(maLH);
            if ("SUCCESS".equals(result)) {
                JOptionPane.showMessageDialog(this, "Đã từ chối nhập kho cho lô hàng " + maLH + " thành công!",
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadDataToTable();
            } else {
                JOptionPane.showMessageDialog(this, result, "Lỗi khi từ chối nhập", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private String getCell(JTable tbl, int row, int col) {
        Object value = tbl.getValueAt(row, col);
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

    private JButton buildDangerButton(String text, int width, int height) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
                
                Color bg = getModel().isPressed() ? AppColor.ERROR_ACTIVE : (getModel().isRollover() ? AppColor.ERROR_HOVER : AppColor.ERROR);
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
            Color bg;
            Color fg;
            if (text.equalsIgnoreCase("Chờ nhập kho")) {
                bg = new Color(0xFEF3C7);
                fg = new Color(0xD97706);
            } else if (text.equalsIgnoreCase("Chờ kiểm duyệt")) {
                bg = new Color(219, 234, 254);
                fg = new Color(29, 78, 216);
            } else {
                bg = WARNING_LIGHT;
                fg = AppColor.WARNING_ACTIVE;
            }
            paintBadge(g, text, bg, fg, getWidth(), getHeight());
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
