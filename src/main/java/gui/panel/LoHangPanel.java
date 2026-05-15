package gui.panel;

import bus.LoHangBUS;
import bus.SanPhamBUS;
import dto.ChiTietLoHangDTO;
import dto.LoHangDTO;
import dto.SanPhamDTO;
import gui.dialog.LoHangForm;
import util.AppColor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class LoHangPanel extends JPanel {

    private final LoHangBUS loHangBUS = new LoHangBUS();
    private final SanPhamBUS sanPhamBUS = new SanPhamBUS();
    private List<LoHangDTO> currentDataList = new ArrayList<>();

    private JTable table;
    private DefaultTableModel tableModel;
    private ModernSearchField searchField;
    private JLabel lblTongLH, lblChoNhap, lblDaNhap;
    
    private JTable detailTable;
    private DefaultTableModel detailModel;
    private JLabel lblDetailTitle;
    private String selectedMaLH;

    private static final String[] COLUMNS = {"", "MÃ LH", "NHÀ CUNG CẤP", "TRẠNG THÁI"};

    public LoHangPanel() {
        setLayout(new BorderLayout());
        setBackground(AppColor.BACKGROUND);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        setFocusable(true);
        addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) { requestFocusInWindow(); }
        });

        JPanel topWrapper = new JPanel();
        topWrapper.setLayout(new BoxLayout(topWrapper, BoxLayout.Y_AXIS));
        topWrapper.setOpaque(false);

        RoundedPanel titleCard = new RoundedPanel(16);
        titleCard.setBackground(AppColor.SURFACE);
        titleCard.setLayout(new BorderLayout());
        titleCard.setBorder(new EmptyBorder(24, 24, 24, 24));
        titleCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        titleCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = new JLabel("Quản lý Lô hàng nhập");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(AppColor.TEXT_PRIMARY);
        titleCard.add(title, BorderLayout.WEST);

        topWrapper.add(titleCard);
        topWrapper.add(Box.createVerticalStrut(18));
        topWrapper.add(buildStats());
        topWrapper.add(Box.createVerticalStrut(18));

        add(topWrapper, BorderLayout.NORTH);
        add(buildTableCard(), BorderLayout.CENTER);

        loadData(null);
    }

    private JPanel buildStats() {
        JPanel row = new JPanel(new GridLayout(1, 3, 16, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblTongLH = new JLabel("0");
        lblChoNhap = new JLabel("0");
        lblDaNhap = new JLabel("0");

        row.add(createStatCard("Tổng lô hàng", lblTongLH, new Color(0x3B82F6), "📦"));
        row.add(createStatCard("Đang chờ nhập", lblChoNhap, new Color(0xF59E0B), "⏳"));
        row.add(createStatCard("Đã nhập kho", lblDaNhap, new Color(0x10B981), "✅"));
        return row;
    }

    private JPanel createStatCard(String cardTitle, JLabel valueLabel, Color accent, String icon) {
        RoundedPanel card = new RoundedPanel(16);
        card.setBackground(AppColor.SURFACE);
        card.setLayout(new BorderLayout(14, 0));
        card.setBorder(new EmptyBorder(14, 18, 14, 18));

        JPanel iconWrap = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 35));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        iconWrap.setOpaque(false);
        iconWrap.setPreferredSize(new Dimension(52, 52));
        iconWrap.setLayout(new GridBagLayout());

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        iconWrap.add(iconLabel);

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        textPanel.setOpaque(false);

        JLabel lblTitle = new JLabel(cardTitle);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTitle.setForeground(AppColor.TEXT_SECONDARY);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(AppColor.TEXT_PRIMARY);

        textPanel.add(lblTitle);
        textPanel.add(valueLabel);

        card.add(iconWrap, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildTableCard() {
        RoundedPanel card = new RoundedPanel(16);
        card.setBackground(AppColor.SURFACE);
        card.setLayout(new BorderLayout());

        JPanel toolBar = new JPanel(new BorderLayout());
        toolBar.setOpaque(false);
        toolBar.setBorder(new EmptyBorder(16, 16, 16, 16));

        JPanel btnGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnGroup.setOpaque(false);

        JButton btnTaoLoHang = createActionButton("Tạo lô hàng", AppColor.SUCCESS, AppColor.SUCCESS_HOVER, AppColor.SUCCESS_ACTIVE);
        JButton btnNhapKho = createActionButton("Yêu cầu nhập kho", AppColor.WARNING, AppColor.WARNING_HOVER, AppColor.WARNING_ACTIVE);
        JButton btnXoaLoHang = createActionButton("Xóa lô hàng", new Color(220, 38, 38), new Color(239, 68, 68), new Color(185, 28, 28));
        JButton btnRefresh = createIconButton("icons/refresh.svg");

        btnGroup.add(btnTaoLoHang);
        btnGroup.add(btnNhapKho);
        btnGroup.add(btnXoaLoHang);
        btnGroup.add(Box.createHorizontalStrut(4));
        btnGroup.add(btnRefresh);

        JPanel searchGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        searchGroup.setOpaque(false);

        searchField = new ModernSearchField("Tìm kiếm lô hàng...");
        searchField.setPreferredSize(new Dimension(280, 36));
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            private void onSearch() {
                String keyword = searchField.getText().trim();
                loadData(keyword.isEmpty() ? null : keyword);
            }
            @Override public void insertUpdate(DocumentEvent e) { onSearch(); }
            @Override public void removeUpdate(DocumentEvent e) { onSearch(); }
            @Override public void changedUpdate(DocumentEvent e) { onSearch(); }
        });

        searchGroup.add(searchField);
        toolBar.add(btnGroup, BorderLayout.WEST);
        toolBar.add(searchGroup, BorderLayout.EAST);

        JPanel splitWrapper = new JPanel(new BorderLayout(16, 0));
        splitWrapper.setOpaque(false);
        splitWrapper.setBorder(new EmptyBorder(0, 16, 16, 16));

        JPanel leftPanel = buildTableWrapper();
        JPanel rightPanel = buildDetailCard();
        
        // [FIX] Điều chỉnh lại chiều rộng (rút ngắn bảng chi tiết xuống 480px để nhường chỗ cho bảng Lô hàng)
        rightPanel.setPreferredSize(new Dimension(480, 0)); 

        splitWrapper.add(leftPanel, BorderLayout.CENTER);
        splitWrapper.add(rightPanel, BorderLayout.EAST);

        card.add(toolBar, BorderLayout.NORTH);
        card.add(splitWrapper, BorderLayout.CENTER);

        // Action Listeners
        btnTaoLoHang.addActionListener(e -> {
            LoHangForm dialog = new LoHangForm(SwingUtilities.getWindowAncestor(this));
            dialog.setVisible(true);
            loadData(null);
        });

        btnNhapKho.addActionListener(e -> {
            List<String> listMa = getSelectedItems();
            if (listMa.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn lô hàng trước khi yêu cầu nhập kho.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!showConfirmYeuCauNhapKho(listMa)) {
                return;
            }
            int success = 0;
            StringBuilder errors = new StringBuilder();
            for (String ma : listMa) {
                try {
                    if (loHangBUS.yeuCauNhapKho(ma)) success++;
                } catch (Exception ex) {
                    errors.append("Lô hàng ").append(ma).append(": ").append(extractDbErrorMessage(ex)).append("\n");
                }
            }
            if (success > 0) {
                JOptionPane.showMessageDialog(this, "Đã gửi yêu cầu nhập kho cho " + success + " lô hàng.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            }
            if (errors.length() > 0) {
                JOptionPane.showMessageDialog(this, errors.toString().trim(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
            loadData(null);
        });

        btnXoaLoHang.addActionListener(e -> {
            List<String> listMa = getSelectedItems();
            if (listMa.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn lô hàng cần xóa.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int option = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa " + listMa.size() + " lô hàng đã chọn không?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                int success = 0;
                StringBuilder errors = new StringBuilder();
                for (String ma : listMa) {
                    try {
                        if (loHangBUS.xoaLoHang(ma)) success++;
                    } catch (Exception ex) {
                        errors.append("Lô hàng ").append(ma).append(": ").append(extractDbErrorMessage(ex)).append("\n");
                    }
                }
                if (success > 0) {
                    JOptionPane.showMessageDialog(this, "Đã xóa thành công " + success + " lô hàng.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                }
                if (errors.length() > 0) {
                    JOptionPane.showMessageDialog(this, errors.toString().trim(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
                loadData(null);
            }
        });

        btnRefresh.addActionListener(e -> loadData(null));
        return card;
    }

    private List<String> getSelectedItems() {
        List<String> listMa = new ArrayList<>();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if (Boolean.TRUE.equals(tableModel.getValueAt(i, 0))) {
                listMa.add(tableModel.getValueAt(i, 1).toString());
            }
        }
        if (listMa.isEmpty() && table.getSelectedRow() >= 0) {
            listMa.add(tableModel.getValueAt(table.getSelectedRow(), 1).toString());
        }
        return listMa;
    }

    private String extractDbErrorMessage(Throwable error) {
        if (error == null) return "Đã có lỗi xảy ra.";

        Throwable current = error;
        String message = null;
        while (current != null) {
            if (current.getMessage() != null && !current.getMessage().trim().isEmpty()) {
                message = current.getMessage().trim();
            }
            if (message != null && message.contains("ORA-")) {
                break;
            }
            current = current.getCause();
        }

        if (message == null || message.isEmpty()) {
            return "Đã có lỗi xảy ra.";
        }

        // Tìm thông báo sau ORA-xxxx:
        int idx = message.indexOf("ORA-");
        if (idx >= 0) {
            int colon = message.indexOf(':', idx);
            if (colon >= 0 && colon + 1 < message.length()) {
                message = message.substring(colon + 1).trim();
            } else {
                message = message.substring(idx).trim();
            }
        }

        // Nếu chuỗi chứa Error Message =, lấy phần sau nó
        int errIdx = message.indexOf("Error Message =");
        if (errIdx >= 0) {
            message = message.substring(errIdx + "Error Message =".length()).trim();
            int colon = message.indexOf(':');
            if (colon >= 0) {
                message = message.substring(colon + 1).trim();
            }
        }

        // Chỉ giữ dòng đầu tiên và loại bỏ các chi tiết như "at ..."
        String[] lines = message.split("(\\r\\n|\\n)");
        if (lines.length > 0) {
            message = lines[0].trim();
        }
        if (message.startsWith("ORA-")) {
            int colon = message.indexOf(':');
            if (colon >= 0 && colon + 1 < message.length()) {
                message = message.substring(colon + 1).trim();
            }
        }
        if (message.startsWith("at ")) {
            return "Đã có lỗi xảy ra.";
        }

        return message.isEmpty() ? "Đã có lỗi xảy ra." : message;
    }

    private boolean showConfirmYeuCauNhapKho(List<String> listMa) {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(AppColor.SURFACE);
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        JLabel message = new JLabel("Bạn có chắc muốn yêu cầu nhập hàng cho các lô hàng dưới đây?");
        message.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        message.setForeground(AppColor.TEXT_PRIMARY);
        panel.add(message, BorderLayout.NORTH);

        JList<String> list = new JList<>(listMa.toArray(new String[0]));
        list.setVisibleRowCount(Math.min(listMa.size(), 8));
        list.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setBackground(Color.WHITE);
        list.setFixedCellHeight(24);
        JScrollPane scroll = new JScrollPane(list);
        scroll.setPreferredSize(new Dimension(320, Math.min(listMa.size() * 24 + 6, 200)));
        panel.add(scroll, BorderLayout.CENTER);

        Object[] options = {"Tạo", "Huỷ"};
        int result = JOptionPane.showOptionDialog(
                this,
                panel,
                "Xác nhận yêu cầu nhập kho",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );
        return result == JOptionPane.YES_OPTION;
    }

    private JPanel buildTableWrapper() {
        JPanel tableWrapper = new JPanel(new BorderLayout());
        tableWrapper.setOpaque(false);

        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int row, int column) { return column == 0; }
            @Override public Class<?> getColumnClass(int columnIndex) {
                return (columnIndex == 0) ? Boolean.class : String.class;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(48);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(229, 231, 235));
        table.setBackground(Color.WHITE);
        table.setSelectionBackground(new Color(220, 252, 231));
        table.setSelectionForeground(AppColor.TEXT_PRIMARY);
        table.setFocusable(false);
        table.setIntercellSpacing(new Dimension(0, 0));

        JTableHeader header = table.getTableHeader();
        header.setPreferredSize(new Dimension(0, 52));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(209, 213, 219)));

        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean isS, boolean hasF, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, isS, hasF, r, c);
                comp.setFont(new Font("Segoe UI", Font.BOLD, 14));
                comp.setBackground(new Color(243, 244, 246));
                ((JLabel) comp).setBorder(new EmptyBorder(0, 16, 0, 8));
                return comp;
            }
        };
        for (int i = 0; i < COLUMNS.length; i++) {
            table.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }

        // [FIX] Căn chỉnh độ rộng từng cột một cách cụ thể để cột Trạng Thái luôn hiển thị đẹp
        table.getColumnModel().getColumn(0).setPreferredWidth(50);  // Checkbox
        table.getColumnModel().getColumn(1).setPreferredWidth(100); // Mã LH
        table.getColumnModel().getColumn(2).setPreferredWidth(200); // Nhà cung cấp
        table.getColumnModel().getColumn(3).setPreferredWidth(150); // Trạng thái

        ZebraHoverRenderer zebraRdr = new ZebraHoverRenderer(table);
        for (int i = 1; i < COLUMNS.length; i++) {
            if (i != 3) table.getColumnModel().getColumn(i).setCellRenderer(zebraRdr);
        }
        table.getColumnModel().getColumn(0).setCellRenderer(new ZebraCheckBoxRenderer(table));
        table.getColumnModel().getColumn(3).setCellRenderer(new BadgeRenderer(table));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);

        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                    selectedMaLH = tableModel.getValueAt(table.getSelectedRow(), 1).toString();
                    showLoHangDetails(selectedMaLH);
                }
            }
        });

        tableWrapper.add(scroll, BorderLayout.CENTER);
        return tableWrapper;
    }

    private JPanel buildDetailCard() {
        RoundedPanel detailCard = new RoundedPanel(16);
        detailCard.setBackground(AppColor.SURFACE);
        detailCard.setBorder(new EmptyBorder(16, 16, 16, 16));
        detailCard.setLayout(new BorderLayout(0, 12));

        lblDetailTitle = new JLabel("Chi tiết lô hàng");
        lblDetailTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblDetailTitle.setForeground(AppColor.TEXT_PRIMARY);
        detailCard.add(lblDetailTitle, BorderLayout.NORTH);

        detailModel = new DefaultTableModel(new Object[]{"Mã SP", "Tên SP", "Giá mua", "SL", "Thành tiền"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        detailTable = new JTable(detailModel);
        detailTable.setRowHeight(38);
        detailTable.setShowVerticalLines(false);
        detailTable.setShowHorizontalLines(true);
        detailTable.setGridColor(new Color(229, 231, 235));
        detailTable.setBackground(Color.WHITE);
        detailTable.setSelectionBackground(new Color(220, 252, 231));
        detailTable.setSelectionForeground(AppColor.TEXT_PRIMARY);
        detailTable.setFocusable(false);
        detailTable.setIntercellSpacing(new Dimension(0, 0));

        JTableHeader detailHeader = detailTable.getTableHeader();
        detailHeader.setPreferredSize(new Dimension(0, 46));
        detailHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(209, 213, 219)));

        // [FIX] Bê nguyên code style của bảng Lô Hàng xuống đây cho bảng Chi tiết đồng bộ
        DefaultTableCellRenderer detailHeaderRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean isS, boolean hasF, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, isS, hasF, r, c);
                comp.setFont(new Font("Segoe UI", Font.BOLD, 14)); // Đồng bộ font size 14
                comp.setBackground(new Color(243, 244, 246));
                ((JLabel) comp).setBorder(new EmptyBorder(0, 16, 0, 8)); // Đồng bộ padding
                return comp;
            }
        };
        for (int i = 0; i < detailTable.getColumnCount(); i++) {
            detailTable.getColumnModel().getColumn(i).setHeaderRenderer(detailHeaderRenderer);
        }

        // Căn chỉnh độ rộng các cột bảng chi tiết cho gọn
        detailTable.getColumnModel().getColumn(0).setPreferredWidth(60);
        detailTable.getColumnModel().getColumn(1).setPreferredWidth(160);
        detailTable.getColumnModel().getColumn(3).setPreferredWidth(30);

        ZebraHoverRenderer detailZebraRdr = new ZebraHoverRenderer(detailTable);
        for(int i = 0; i < detailTable.getColumnCount(); i++) {
            detailTable.getColumnModel().getColumn(i).setCellRenderer(detailZebraRdr);
        }

        JScrollPane detailScroll = new JScrollPane(detailTable);
        detailScroll.setBorder(BorderFactory.createEmptyBorder());
        detailScroll.getViewport().setBackground(Color.WHITE);
        detailCard.add(detailScroll, BorderLayout.CENTER);

        return detailCard;
    }

    private JButton createActionButton(String text, Color bg, Color hoverColor, Color activeColor) {
        JButton btn = new JButton(text) {
            private Color current = bg;
            {
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e)  { current = hoverColor; repaint(); }
                    @Override public void mouseExited(MouseEvent e)   { current = bg; repaint(); }
                    @Override public void mousePressed(MouseEvent e)  { current = activeColor; repaint(); }
                    @Override public void mouseReleased(MouseEvent e) { current = hoverColor; repaint(); }
                });
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(current);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(130, 36));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton createIconButton(String svgPath) {
        JButton btn = new JButton();
        try {
            btn.setIcon(new com.formdev.flatlaf.extras.FlatSVGIcon(svgPath, 18, 18));
        } catch (Throwable ex) {
            btn.setText("↻");
            btn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        }
        btn.setPreferredSize(new Dimension(36, 36));
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.putClientProperty("hover", true); btn.repaint(); }
            @Override public void mouseExited(MouseEvent e) { btn.putClientProperty("hover", false); btn.repaint(); }
        });
        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
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
        return btn;
    }

    private void loadData(String keyword) {
        List<LoHangDTO> list = loHangBUS.getAll();
        currentDataList.clear();
        for (LoHangDTO lh : list) {
            if (keyword == null || keyword.isEmpty()) {
                currentDataList.add(lh);
            } else {
                String lower = keyword.toLowerCase();
                if (lh.getMaLH().toLowerCase().contains(lower)
                        || (lh.getTenNCC() != null && lh.getTenNCC().toLowerCase().contains(lower))
                        || (lh.getMaNCC() != null && lh.getMaNCC().toLowerCase().contains(lower))
                        || (lh.getTrangThaiLH() != null && lh.getTrangThaiLH().toLowerCase().contains(lower))) {
                    currentDataList.add(lh);
                }
            }
        }
        loadTableData();
        updateStats(list);
        clearSelection();
    }

    private void loadTableData() {
        tableModel.setRowCount(0);
        for (LoHangDTO lh : currentDataList) {
            tableModel.addRow(new Object[]{
                    false,
                    lh.getMaLH(),
                    lh.getTenNCC() != null ? lh.getTenNCC() : lh.getMaNCC(),
                    lh.getTrangThaiLH()
            });
        }
    }

    private void updateStats(List<LoHangDTO> list) {
        int total = list.size();
        int pending = 0;
        int completed = 0;
        for (LoHangDTO lh : list) {
            String status = lh.getTrangThaiLH();
            if (status != null) {
                if (status.equalsIgnoreCase("Đã nhập kho")) completed++;
                else pending++;
            }
        }
        lblTongLH.setText(String.valueOf(total));
        lblChoNhap.setText(String.valueOf(pending));
        lblDaNhap.setText(String.valueOf(completed));
    }

    private void clearSelection() {
        selectedMaLH = null;
        table.clearSelection();
        lblDetailTitle.setText("Chi tiết lô hàng");
        detailModel.setRowCount(0);
    }

    private void showLoHangDetails(String maLH) {
        lblDetailTitle.setText("Chi tiết lô hàng " + maLH);
        detailModel.setRowCount(0);
        if (maLH == null || maLH.trim().isEmpty()) return;
        List<ChiTietLoHangDTO> details = loHangBUS.getChiTietLoHang(maLH);
        if (details == null || details.isEmpty()) {
            detailModel.addRow(new Object[]{"", "Chưa có sản phẩm", "", "", ""});
            return;
        }
        DecimalFormat df = new DecimalFormat("#,###");
        for (ChiTietLoHangDTO item : details) {
            detailModel.addRow(new Object[]{
                    item.getMaSP(),
                    findProductNameByMaSP(item.getMaSP()),
                    df.format(item.getGiaMua()),
                    item.getSoLuong(),
                    df.format(item.getThanhTien())
            });
        }
    }

    private String findProductNameByMaSP(String maSP) {
        if (maSP == null) return "";
        for (SanPhamDTO sp : sanPhamBUS.getAll()) {
            if (maSP.equals(sp.getMaSP())) return sp.getTenSP();
        }
        return "";
    }

    // --- INNER CLASSES (RENDERERS & COMPONENTS) ---
    
    static class ModernSearchField extends JTextField {
        private final String placeholder;
        private boolean isHovered = false;
        public ModernSearchField(String placeholder) {
            this.placeholder = placeholder;
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 36));
            setFont(new Font("Segoe UI", Font.PLAIN, 13));
            setForeground(AppColor.TEXT_PRIMARY);
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { isHovered = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { isHovered = false; repaint(); }
            });
            addFocusListener(new FocusAdapter() {
                @Override public void focusGained(FocusEvent e) { repaint(); }
                @Override public void focusLost(FocusEvent e) { isHovered = false; repaint(); }
            });
        }
        @Override protected void paintComponent(Graphics g) {
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
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(placeholder, 16, y);
            }
            g2.setColor(new Color(156, 163, 175));
            int x = getWidth() - 24;
            int y = (getHeight() - 14) / 2;
            g2.setStroke(new BasicStroke(2f));
            g2.drawOval(x, y, 10, 10);
            g2.drawLine(x + 8, y + 8, x + 13, y + 13);
            g2.dispose();
        }
    }

    static class RoundedPanel extends JPanel {
        private final int arc;
        RoundedPanel(int arc) { this.arc = arc; setOpaque(false); }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            for (int i = 5; i >= 1; i--) {
                g2.setColor(new Color(0, 0, 0, 6));
                g2.fill(new RoundRectangle2D.Float(i, i + 1, getWidth() - i * 2, getHeight() - i * 2, arc, arc));
            }
            g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 5, getHeight() - 5, arc, arc));
            g2.dispose();
            super.paintComponent(g);
        }
    }

    static class ZebraCheckBoxRenderer extends JCheckBox implements TableCellRenderer {
        ZebraCheckBoxRenderer(JTable tbl) { setHorizontalAlignment(SwingConstants.CENTER); setOpaque(true); }
        @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean isS, boolean hasF, int r, int c) {
            setSelected(v != null && (Boolean) v);
            setBackground(isS ? new Color(220, 252, 231) : (r % 2 == 0 ? Color.WHITE : new Color(250, 250, 250)));
            return this;
        }
    }

    static class ZebraHoverRenderer extends DefaultTableCellRenderer {
        private int hoverRow = -1;
        ZebraHoverRenderer(JTable tbl) {
            tbl.addMouseMotionListener(new MouseMotionAdapter() {
                @Override public void mouseMoved(MouseEvent e) { int r = tbl.rowAtPoint(e.getPoint()); if (r != hoverRow) { hoverRow = r; tbl.repaint(); } }
            });
            tbl.addMouseListener(new MouseAdapter() { @Override public void mouseExited(MouseEvent e) { hoverRow = -1; tbl.repaint(); } });
        }
        @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
            super.getTableCellRendererComponent(t, v, s, f, r, c);
            setBorder(new EmptyBorder(0, 16, 0, 8));
            setFont(new Font("Segoe UI", c == 1 ? Font.BOLD : Font.PLAIN, 13));
            setForeground(AppColor.TEXT_PRIMARY); 
            setBackground(s ? new Color(220, 252, 231) : (r == hoverRow ? new Color(240, 253, 244) : (r % 2 == 0 ? Color.WHITE : new Color(250, 250, 250))));
            return this;
        }
    }

    static class BadgeRenderer extends DefaultTableCellRenderer {
        private int hoverRow = -1;
        public BadgeRenderer(JTable tbl) {
            tbl.addMouseMotionListener(new MouseMotionAdapter() {
                @Override public void mouseMoved(MouseEvent e) { int r = tbl.rowAtPoint(e.getPoint()); if (r != hoverRow) { hoverRow = r; tbl.repaint(); } }
            });
            tbl.addMouseListener(new MouseAdapter() { @Override public void mouseExited(MouseEvent e) { hoverRow = -1; tbl.repaint(); } });
        }
        @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
            JPanel cell = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 8));
            cell.setOpaque(true);
            if (s)                  cell.setBackground(new Color(220, 252, 231));
            else if (r == hoverRow) cell.setBackground(new Color(240, 253, 244));
            else if (r % 2 == 0)    cell.setBackground(Color.WHITE);
            else                    cell.setBackground(new Color(250, 250, 250));
            String status = (v == null) ? "" : v.toString();
            Color badgeBg, badgeFg;
            if (status.equalsIgnoreCase("Đã nhập kho")) {
                badgeBg = new Color(0xD1FAE5); badgeFg = new Color(0x065F46);
            } else if (status.equalsIgnoreCase("Chờ nhập kho") || status.equalsIgnoreCase("Đang chờ nhập")) {
                badgeBg = new Color(0xFEF3C7); badgeFg = new Color(0xD97706);
            } else {
                badgeBg = new Color(0xF3F4F6); badgeFg = new Color(0x374151);
            }
            JLabel badge = new JLabel(status) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(badgeBg); g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
                    super.paintComponent(g2);
                }
            };
            badge.setFont(new Font("Segoe UI", Font.BOLD, 11)); badge.setForeground(badgeFg);
            badge.setOpaque(false); badge.setBorder(new EmptyBorder(3, 10, 3, 10));
            cell.add(badge); return cell;
        }
    }
}