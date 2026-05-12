package gui.panel;

import bus.LoaiSanPhamBUS;
import dto.LoaiSanPhamDTO;
import gui.dialog.LoaiSanPhamForm;
import util.AppColor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LoaiSanPhamPanel extends JPanel {

    private final LoaiSanPhamBUS bus = new LoaiSanPhamBUS();
    private List<LoaiSanPhamDTO> currentDataList = new ArrayList<>();

    private JTable table;
    private DefaultTableModel tableModel;
    private ModernSearchField searchField;
    private JLabel lblTongLSP;

    private static final String[] COLUMNS = { "", "MÃ LOẠI SP", "TÊN LOẠI SẢN PHẨM", "MÔ TẢ" };

    public LoaiSanPhamPanel() {
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
        
        // 1. Tiêu đề
        RoundedPanel titleCard = new RoundedPanel(16);
        titleCard.setBackground(AppColor.SURFACE);
        titleCard.setLayout(new BorderLayout());
        titleCard.setBorder(new EmptyBorder(24, 24, 24, 24)); 
        titleCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100)); 
        titleCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel title = new JLabel("Danh mục Loại Sản Phẩm");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28)); 
        title.setForeground(AppColor.TEXT_PRIMARY);
        titleCard.add(title, BorderLayout.WEST);
        
        topWrapper.add(titleCard);
        topWrapper.add(Box.createVerticalStrut(18));
        
        // 2. Thống kê (1 Cột duy nhất cho loại sản phẩm)
        topWrapper.add(buildStats());
        topWrapper.add(Box.createVerticalStrut(18));

        add(topWrapper, BorderLayout.NORTH);

        // 3. Bảng dữ liệu
        JPanel tableCard = buildTableCard();
        add(tableCard, BorderLayout.CENTER);

        loadData(null);
    }

    private JPanel buildStats() {
        JPanel row = new JPanel(new GridLayout(1, 1, 16, 0)); // Chỉ dùng 1 Card để nhấn mạnh
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(300, 90)); // Khống chế chiều dài card
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblTongLSP = new JLabel("0");
        row.add(createStatCard("Tổng số danh mục", lblTongLSP, new Color(0x8B5CF6), "📂"));
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
        iconWrap.setOpaque(false); iconWrap.setPreferredSize(new Dimension(52, 52));
        iconWrap.setLayout(new GridBagLayout());
        JLabel iconLbl = new JLabel(icon); iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        iconWrap.add(iconLbl);

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 2)); textPanel.setOpaque(false);
        JLabel lTitle = new JLabel(cardTitle); lTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lTitle.setForeground(AppColor.TEXT_SECONDARY);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24)); valueLabel.setForeground(AppColor.TEXT_PRIMARY);

        textPanel.add(lTitle); textPanel.add(valueLabel);
        card.add(iconWrap, BorderLayout.WEST); card.add(textPanel, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildTableCard() {
        RoundedPanel card = new RoundedPanel(16);
        card.setBackground(AppColor.SURFACE); card.setLayout(new BorderLayout());

        JPanel toolBar = new JPanel(new BorderLayout()); toolBar.setOpaque(false);
        toolBar.setBorder(new EmptyBorder(16, 16, 16, 16));

        JPanel btnGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0)); btnGroup.setOpaque(false);
        JButton btnThem = createActionButton("Thêm", AppColor.SUCCESS, AppColor.SUCCESS_HOVER, AppColor.SUCCESS_ACTIVE);
        JButton btnSua  = createActionButton("Sửa",  AppColor.INFO,    AppColor.INFO_HOVER,    AppColor.INFO_ACTIVE);
        JButton btnXoa  = createActionButton("Xóa",  AppColor.ERROR,   AppColor.ERROR_HOVER,   AppColor.ERROR_ACTIVE);
        
        // CẬP NHẬT NÚT REFRESH THEO CHUẨN MỚI
        JButton btnRefresh = createIconButton("icons/refresh.svg"); 

        btnThem.addActionListener(e -> showForm(null));
        btnSua.addActionListener(e -> showFormForEdit());
        btnXoa.addActionListener(e -> xoaDanhMucNhieu());
        btnRefresh.addActionListener(e -> loadData(null));

        btnGroup.add(btnThem); btnGroup.add(btnSua); btnGroup.add(btnXoa); 
        btnGroup.add(Box.createHorizontalStrut(4)); btnGroup.add(btnRefresh);

        JPanel searchGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0)); searchGroup.setOpaque(false);
        searchField = new ModernSearchField("Tìm danh mục...");
        searchField.setPreferredSize(new Dimension(280, 36));
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            private void onSearch() { loadData(searchField.getText().trim().isEmpty() ? null : searchField.getText().trim()); }
            @Override public void insertUpdate(DocumentEvent e) { onSearch(); }
            @Override public void removeUpdate(DocumentEvent e) { onSearch(); }
            @Override public void changedUpdate(DocumentEvent e) { onSearch(); }
        });
        searchGroup.add(searchField);
        toolBar.add(btnGroup, BorderLayout.WEST); toolBar.add(searchGroup, BorderLayout.EAST);

        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c == 0; }
            @Override public Class<?> getColumnClass(int columnIndex) { return (columnIndex == 0) ? Boolean.class : String.class; }
        };

        table = new JTable(tableModel);
        table.setRowHeight(48); table.setShowVerticalLines(false); table.setShowHorizontalLines(true);
        table.setGridColor(new Color(229, 231, 235)); table.setBackground(Color.WHITE);
        table.setSelectionBackground(new Color(220, 252, 231));
        table.setSelectionForeground(AppColor.TEXT_PRIMARY); // Giữ màu chữ đen khi chọn
        table.setFocusable(false); table.setIntercellSpacing(new Dimension(0, 0));

        JTableHeader header = table.getTableHeader(); header.setPreferredSize(new Dimension(0, 52));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(209, 213, 219)));
        
        DefaultTableCellRenderer hdrRdr = new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean isS, boolean hasF, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, isS, hasF, r, c);
                comp.setFont(new Font("Segoe UI", Font.BOLD, 14)); comp.setBackground(new Color(243, 244, 246));
                ((JLabel) comp).setBorder(new EmptyBorder(0, 16, 0, 8)); return comp;
            }
        };
        for (int i = 0; i < COLUMNS.length; i++) table.getColumnModel().getColumn(i).setHeaderRenderer(hdrRdr);

        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(120);
        table.getColumnModel().getColumn(2).setPreferredWidth(300);
        table.getColumnModel().getColumn(3).setPreferredWidth(400);

        ZebraHoverRenderer zebraRdr = new ZebraHoverRenderer(table);
        for (int i = 1; i < COLUMNS.length; i++) table.getColumnModel().getColumn(i).setCellRenderer(zebraRdr);
        table.getColumnModel().getColumn(0).setCellRenderer(new ZebraCheckBoxRenderer(table));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder()); scroll.getViewport().setBackground(Color.WHITE);

        JPanel tableWrapper = new JPanel(new BorderLayout());
        tableWrapper.setBorder(new EmptyBorder(0, 16, 16, 16)); tableWrapper.setOpaque(false);
        tableWrapper.add(scroll, BorderLayout.CENTER);

        card.add(toolBar, BorderLayout.NORTH); card.add(tableWrapper, BorderLayout.CENTER);
        return card;
    }

    private void showForm(String maLSP) {
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parentWindow, maLSP == null ? "Thêm Loại Sản Phẩm" : "Sửa Loại Sản Phẩm", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setLayout(new BorderLayout()); dialog.setResizable(false);

        LoaiSanPhamForm form = new LoaiSanPhamForm(maLSP);
        dialog.add(form, BorderLayout.CENTER);

        JPanel bottomWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        bottomWrapper.setBackground(AppColor.SURFACE);
        
        JButton btnCancel = createActionButton("Hủy", AppColor.ERROR, AppColor.ERROR_HOVER, AppColor.ERROR_ACTIVE);
        JButton btnSave   = createActionButton("Lưu", AppColor.SUCCESS, AppColor.SUCCESS_HOVER, AppColor.SUCCESS_ACTIVE);

        btnSave.addActionListener(e -> { if (form.saveData()) { dialog.dispose(); loadData(null); } });
        btnCancel.addActionListener(e -> dialog.dispose());

        bottomWrapper.add(btnCancel); bottomWrapper.add(btnSave);
        dialog.add(bottomWrapper, BorderLayout.SOUTH);

        dialog.pack(); dialog.setLocationRelativeTo(parentWindow); dialog.setVisible(true);
    }

    private void showFormForEdit() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một danh mục để sửa.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        showForm(tableModel.getValueAt(row, 1).toString());
    }

    private void xoaDanhMucNhieu() {
        List<String> listMa = new ArrayList<>();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if (Boolean.TRUE.equals(tableModel.getValueAt(i, 0))) {
                listMa.add(tableModel.getValueAt(i, 1).toString());
            }
        }
        if (listMa.isEmpty() && table.getSelectedRow() >= 0) {
            listMa.add(tableModel.getValueAt(table.getSelectedRow(), 1).toString());
        }
        if (listMa.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn danh mục cần xóa.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Bạn chắc chắn muốn xóa " + listMa.size() + " danh mục?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int countSuccess = 0;
            StringBuilder errors = new StringBuilder();

            for (String ma : listMa) {
                try {
                    // Gọi lệnh xóa từ BUS (BUS sẽ gọi DAO)
                    if (bus.delete(ma)) countSuccess++;
                } catch (SQLException ex) {
                    // Bắt chính xác lỗi từ Database
                    String errorMsg = ex.getMessage();
                    
                    if (errorMsg.contains("ORA-20040")) {
                        // Đây là mã lỗi tùy chỉnh do lệnh RAISE_APPLICATION_ERROR(-20040) tạo ra
                        errors.append("- Mã [").append(ma).append("]: Đang chứa sản phẩm.\n");
                    } else if (errorMsg.contains("ORA-02292")) {
                        // Ràng buộc khóa ngoại dự phòng
                        errors.append("- Mã [").append(ma).append("]: Vướng ràng buộc dữ liệu.\n");
                    } else {
                        errors.append("- Mã [").append(ma).append("]: Lỗi không xác định.\n");
                    }
                }
            }

            if (errors.length() > 0) {
                JOptionPane.showMessageDialog(this, 
                    "Đã xóa thành công: " + countSuccess + " danh mục.\n\nKhông thể xóa các danh mục sau:\n" + errors.toString(), 
                    "Kết quả xóa", JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Đã xóa thành công toàn bộ " + countSuccess + " danh mục!", 
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
            loadData(null); 
        }
    }

    private void loadData(String keyword) {
        currentDataList = (keyword == null || keyword.isBlank()) ? bus.getAll() : bus.timKiem(keyword);
        lblTongLSP.setText(String.valueOf(currentDataList.size()));
        loadTableData();
    }

    private void loadTableData() {
        tableModel.setRowCount(0);
        for (LoaiSanPhamDTO lsp : currentDataList) {
            tableModel.addRow(new Object[]{ false, lsp.getMaLSP(), lsp.getTenLSP(), lsp.getMoTa() });
        }
    }

    private JButton createActionButton(String text, Color bg, Color hoverColor, Color activeColor) {
        JButton btn = new JButton(text) {
            private Color current = bg;
            {
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e)  { current = hoverColor; repaint(); }
                    @Override public void mouseExited(MouseEvent e)   { current = bg; repaint(); }
                    @Override public void mousePressed(MouseEvent e)  { current = activeColor; repaint(); }
                });
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(current); g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.dispose(); super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13)); btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false); btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(80, 36)); btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // THÊM HÀM TẠO NÚT ICON REFRESH THEO CHUẨN
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

    static class ModernSearchField extends JTextField {
        private final String placeholder;
        public ModernSearchField(String placeholder) {
            this.placeholder = placeholder; setOpaque(false); setBorder(new EmptyBorder(0, 16, 0, 36));
            setFont(new Font("Segoe UI", Font.PLAIN, 13));
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE); g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            g2.setColor(isFocusOwner() ? AppColor.PRIMARY : new Color(209, 213, 219));
            g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 12, 12); super.paintComponent(g);
            if (getText().isEmpty() && !isFocusOwner()) {
                g2.setColor(new Color(156, 163, 175));
                g2.drawString(placeholder, 16, (getHeight() - g.getFontMetrics().getHeight()) / 2 + g.getFontMetrics().getAscent());
            }
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
            g2.dispose(); super.paintComponent(g);
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
            setForeground(AppColor.TEXT_PRIMARY); // Cố định màu đen
            setBackground(s ? new Color(220, 252, 231) : (r == hoverRow ? new Color(240, 253, 244) : (r % 2 == 0 ? Color.WHITE : new Color(250, 250, 250))));
            return this;
        }
    }
}