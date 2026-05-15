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
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class LoHangPanel extends JPanel {

    private final LoHangBUS loHangBUS = new LoHangBUS();
    private final SanPhamBUS sanPhamBUS = new SanPhamBUS();
    private JTable table;
    private DefaultTableModel tableModel;
    private ModernSearchField searchField;
    private JLabel lblTongLH, lblChoNhap, lblDaNhap;
    private JTable detailTable;
    private DefaultTableModel detailModel;
    private JLabel lblDetailTitle;
    private String selectedMaLH;

    private static final String[] COLUMNS = {"MÃ LH", "NHÀ CUNG CẤP", "TRẠNG THÁI"};

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
        JButton btnRefresh = createIconButton("↻");

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

        JPanel centerPanel = new JPanel(new BorderLayout(0, 16));
        centerPanel.setOpaque(false);
        centerPanel.add(buildTableWrapper(), BorderLayout.CENTER);
        centerPanel.add(buildDetailCard(), BorderLayout.SOUTH);

        card.add(toolBar, BorderLayout.NORTH);
        card.add(centerPanel, BorderLayout.CENTER);

        btnTaoLoHang.addActionListener(e -> {
            LoHangForm dialog = new LoHangForm(SwingUtilities.getWindowAncestor(this));
            dialog.setVisible(true);
            loadData(null);
        });

        btnNhapKho.addActionListener(e -> {
            if (selectedMaLH == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn lô hàng trước khi yêu cầu nhập kho.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (loHangBUS.yeuCauNhapKho(selectedMaLH)) {
                JOptionPane.showMessageDialog(this, "Đã gửi yêu cầu nhập kho", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadData(null);
            } else {
                JOptionPane.showMessageDialog(this, "Gửi yêu cầu nhập kho thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnXoaLoHang.addActionListener(e -> {
            if (selectedMaLH == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn lô hàng cần xóa.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int option = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa lô hàng " + selectedMaLH + " không?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                if (loHangBUS.xoaLoHang(selectedMaLH)) {
                    JOptionPane.showMessageDialog(this, "Đã xóa lô hàng.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    loadData(null);
                } else {
                    JOptionPane.showMessageDialog(this, "Xóa lô hàng thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnRefresh.addActionListener(e -> loadData(null));
        return card;
    }

    private JPanel buildTableWrapper() {
        JPanel tableWrapper = new JPanel(new BorderLayout());
        tableWrapper.setOpaque(false);
        tableWrapper.setBorder(new EmptyBorder(0, 16, 16, 16));

        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
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

        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        headerRenderer.setFont(new Font("Segoe UI", Font.BOLD, 14));
        headerRenderer.setBackground(new Color(243, 244, 246));
        headerRenderer.setBorder(new EmptyBorder(0, 16, 0, 0));
        for (int i = 0; i < COLUMNS.length; i++) {
            table.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);

        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                    selectedMaLH = tableModel.getValueAt(table.getSelectedRow(), 0).toString();
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

        detailModel = new DefaultTableModel(new Object[]{"Mã SP", "Tên SP", "Giá mua", "Số lượng", "Thành tiền"}, 0) {
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

        DefaultTableCellRenderer detailHeaderRenderer = new DefaultTableCellRenderer();
        detailHeaderRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        detailHeaderRenderer.setFont(new Font("Segoe UI", Font.BOLD, 14));
        detailHeaderRenderer.setBackground(new Color(243, 244, 246));
        detailHeaderRenderer.setBorder(new EmptyBorder(0, 16, 0, 0));
        for (int i = 0; i < detailTable.getColumnCount(); i++) {
            detailTable.getColumnModel().getColumn(i).setHeaderRenderer(detailHeaderRenderer);
        }

        JScrollPane detailScroll = new JScrollPane(detailTable);
        detailScroll.setBorder(BorderFactory.createEmptyBorder());
        detailScroll.getViewport().setBackground(Color.WHITE);
        detailCard.add(detailScroll, BorderLayout.CENTER);

        return detailCard;
    }

    private JPanel createInputGroup(String labelText, JComponent inputComp) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(AppColor.TEXT_SECONDARY);
        label.setBorder(new EmptyBorder(0, 0, 8, 0));

        inputComp.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        inputComp.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(AppColor.BORDER), new EmptyBorder(8, 12, 8, 12)));
        inputComp.setPreferredSize(new Dimension(0, 40));

        panel.add(label);
        panel.add(inputComp);
        return panel;
    }

    private JButton createActionButton(String text, Color bg, Color hoverColor, Color activeColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bg);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(hoverColor); }
            @Override public void mouseExited(MouseEvent e) { btn.setBackground(bg); }
            @Override public void mousePressed(MouseEvent e) { btn.setBackground(activeColor); }
            @Override public void mouseReleased(MouseEvent e) { btn.setBackground(bg); }
        });
        return btn;
    }

    private JButton createIconButton(String symbol) {
        JButton btn = new JButton(symbol);
        btn.setFont(new Font("Segoe UI Symbol", Font.BOLD, 16));
        btn.setBackground(Color.WHITE);
        btn.setForeground(AppColor.TEXT_PRIMARY);
        btn.setBorder(BorderFactory.createLineBorder(AppColor.BORDER));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(40, 40));
        return btn;
    }

    private void loadData(String keyword) {
        List<LoHangDTO> list = loHangBUS.getAll();
        List<LoHangDTO> filtered = new ArrayList<>();

        for (LoHangDTO lh : list) {
            if (keyword == null || keyword.isEmpty()) {
                filtered.add(lh);
            } else {
                String lower = keyword.toLowerCase();
                if (lh.getMaLH().toLowerCase().contains(lower)
                        || (lh.getTenNCC() != null && lh.getTenNCC().toLowerCase().contains(lower))
                        || (lh.getMaNCC() != null && lh.getMaNCC().toLowerCase().contains(lower))
                        || (lh.getTrangThaiLH() != null && lh.getTrangThaiLH().toLowerCase().contains(lower))) {
                    filtered.add(lh);
                }
            }
        }

        loadTableData(filtered);
        updateStats(list);
        clearSelection();
    }

    private void loadTableData(List<LoHangDTO> list) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        for (LoHangDTO lh : list) {
            model.addRow(new Object[]{
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
                if (status.equalsIgnoreCase("Đã nhập kho")) {
                    completed++;
                } else {
                    pending++;
                }
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

        if (maLH == null || maLH.trim().isEmpty()) {
            return;
        }

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
        if (maSP == null) {
            return "";
        }
        for (SanPhamDTO sp : sanPhamBUS.getAll()) {
            if (maSP.equals(sp.getMaSP())) {
                return sp.getTenSP();
            }
        }
        return "";
    }

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
            int iconSize = 14;
            int x = getWidth() - 24;
            int y = (getHeight() - iconSize) / 2;
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
}