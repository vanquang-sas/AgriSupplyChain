package gui.panel;

import bus.KhoBUS;
import dto.KhoDTO;
import gui.dialog.KhoForm;
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
import java.util.ArrayList;
import java.util.List;

public class KhoPanel extends JPanel {

    // ── Data ──────────────────────────────────────────────────────────────────
    private final KhoBUS bus = new KhoBUS();
    private List<KhoDTO> currentDataList = new ArrayList<>();

    // ── UI ────────────────────────────────────────────────────────────────────
    private JTable table;
    private DefaultTableModel tableModel;
    private ModernSearchField searchField;

    // ── Stat labels ───────────────────────────────────────────────────────────
    private JLabel lblTongKho, lblKhoMat, lblKhoLanh, lblKhoDong;

    // Cột trống ở đầu cho Checkbox
    private static final String[] COLUMNS = {
            "", "MÃ KHO", "TÊN KHO", "LOẠI KHO", "ĐỊA CHỈ", "MÔ TẢ"
    };

    public KhoPanel() {
        setLayout(new BorderLayout());
        setBackground(AppColor.BACKGROUND);
        setBorder(new EmptyBorder(24, 24, 24, 24));
        
        setFocusable(true);
        addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                requestFocusInWindow();
            }
        });

        // --- Gom nhóm Title và Stats đặt lên NORTH ---
        JPanel topWrapper = new JPanel();
        topWrapper.setLayout(new BoxLayout(topWrapper, BoxLayout.Y_AXIS));
        topWrapper.setOpaque(false);
        topWrapper.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) { requestFocusInWindow(); }
        });
        
        // 1. Tiêu đề
        RoundedPanel titleCard = new RoundedPanel(16);
        titleCard.setBackground(AppColor.SURFACE);
        titleCard.setLayout(new BorderLayout());
        titleCard.setBorder(new EmptyBorder(24, 24, 24, 24)); 
        titleCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100)); 
        titleCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel title = new JLabel("Danh sách kho bãi");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(AppColor.TEXT_PRIMARY);
        titleCard.add(title, BorderLayout.WEST);
        
        topWrapper.add(titleCard);
        topWrapper.add(Box.createVerticalStrut(18));
        
        // 2. Thống kê (Dùng 4 thẻ cho 4 loại dữ liệu)
        topWrapper.add(buildStats());
        topWrapper.add(Box.createVerticalStrut(18));

        add(topWrapper, BorderLayout.NORTH);

        // 3. Khu vực Bảng
        JPanel tableCard = buildTableCard();
        add(tableCard, BorderLayout.CENTER);

        loadData(null);
    }

    private JPanel buildStats() {
        // Form kho bãi có 4 loại thẻ nên ta dùng GridLayout 1x4
        JPanel row = new JPanel(new GridLayout(1, 4, 16, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblTongKho = new JLabel("0");
        lblKhoMat  = new JLabel("0");
        lblKhoLanh = new JLabel("0");
        lblKhoDong = new JLabel("0");

        row.add(createStatCard("Tổng số kho", lblTongKho, new Color(0x3B82F6), "🏢"));
        row.add(createStatCard("Kho Mát",     lblKhoMat,  new Color(0x0369A1), "🌿"));
        row.add(createStatCard("Kho Lạnh",    lblKhoLanh, new Color(0x1D4ED8), "❄"));
        row.add(createStatCard("Kho Đông",    lblKhoDong, new Color(0x6D28D9), "🧊"));
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

        JLabel iconLbl = new JLabel(icon) {
            @Override
            public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                d.height += 8; 
                d.width += 4;  
                return d;
            }
        };
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        iconLbl.setVerticalAlignment(SwingConstants.BOTTOM); 
        iconLbl.setHorizontalAlignment(SwingConstants.CENTER);
        
        iconWrap.add(iconLbl);

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        textPanel.setOpaque(false);

        JLabel lTitle = new JLabel(cardTitle);
        lTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lTitle.setForeground(AppColor.TEXT_SECONDARY);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(AppColor.TEXT_PRIMARY);

        textPanel.add(lTitle);
        textPanel.add(valueLabel);

        card.add(iconWrap,  BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildTableCard() {
        RoundedPanel card = new RoundedPanel(16);
        card.setBackground(AppColor.SURFACE);
        card.setLayout(new BorderLayout());
        card.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) { requestFocusInWindow(); }
        });

        JPanel toolBar = new JPanel(new BorderLayout());
        toolBar.setOpaque(false);
        toolBar.setBorder(new EmptyBorder(16, 16, 16, 16));

        JPanel btnGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnGroup.setOpaque(false);

        JButton btnThem = createActionButton("Thêm", AppColor.SUCCESS, AppColor.SUCCESS_HOVER, AppColor.SUCCESS_ACTIVE);
        JButton btnSua  = createActionButton("Sửa",  AppColor.INFO,    AppColor.INFO_HOVER,    AppColor.INFO_ACTIVE);
        JButton btnXoa  = createActionButton("Xóa",  AppColor.ERROR,   AppColor.ERROR_HOVER,  AppColor.ERROR_ACTIVE);
        JButton btnRefresh = createIconButton("↻");

        btnThem.addActionListener(e -> showForm(null));
        btnSua .addActionListener(e -> showFormForEdit());
        btnXoa .addActionListener(e -> xoaKhoNieu()); // Tính năng Xóa đa nhiệm
        btnRefresh.addActionListener(e -> loadData(null));

        btnGroup.add(btnThem);
        btnGroup.add(btnSua);
        btnGroup.add(btnXoa);
        btnGroup.add(Box.createHorizontalStrut(4));
        btnGroup.add(btnRefresh);

        JPanel searchGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        searchGroup.setOpaque(false);

        searchField = new ModernSearchField("Tìm kiếm kho bãi...");
        searchField.setPreferredSize(new Dimension(280, 36));
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            private void onSearch() {
                String kw = searchField.getText().trim();
                loadData(kw.isEmpty() ? null : kw);
            }
            @Override public void insertUpdate(DocumentEvent e)  { onSearch(); }
            @Override public void removeUpdate(DocumentEvent e)  { onSearch(); }
            @Override public void changedUpdate(DocumentEvent e) { onSearch(); }
        });

        OutlineButton btnSort = new OutlineButton("Sắp xếp ▼");
        btnSort.setPreferredSize(new Dimension(110, 36));
        JPopupMenu sortMenu = new JPopupMenu();
        sortMenu.setBackground(Color.WHITE);
        sortMenu.setBorder(BorderFactory.createLineBorder(new Color(209, 213, 219), 1)); 

        JMenuItem sortNameAsc = createStyledMenuItem("Tên kho (A - Z)");
        JMenuItem sortType    = createStyledMenuItem("Loại kho");

        sortNameAsc.addActionListener(e -> {
            currentDataList.sort((k1, k2) -> k1.getTenKho().compareToIgnoreCase(k2.getTenKho()));
            loadTableData();
        });
        sortType.addActionListener(e -> {
            currentDataList.sort((k1, k2) -> k1.getLoaiKho().compareToIgnoreCase(k2.getLoaiKho()));
            loadTableData();
        });

        sortMenu.add(sortNameAsc);
        sortMenu.add(sortType);
        btnSort.addActionListener(e -> sortMenu.show(btnSort, 0, btnSort.getHeight() + 4)); 
        
        searchGroup.add(searchField);
        searchGroup.add(btnSort);

        toolBar.add(btnGroup, BorderLayout.WEST);
        toolBar.add(searchGroup, BorderLayout.EAST);

        // --- Khu vực Bảng ---
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c == 0; } // Cho phép Edit Checkbox
            @Override public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Boolean.class;
                return String.class;
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
        header.setReorderingAllowed(false);

        DefaultTableCellRenderer hdrRdr = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean isS, boolean hasF, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, isS, hasF, r, c);
                comp.setFont(new Font("Segoe UI", Font.BOLD, 14));
                comp.setForeground(new Color(17, 24, 39)); 
                comp.setBackground(new Color(243, 244, 246));
                JLabel label = (JLabel) comp;
                if (c == 0) {
                    label.setHorizontalAlignment(SwingConstants.CENTER);
                    label.setBorder(new EmptyBorder(0, 0, 0, 0));
                } else {
                    label.setHorizontalAlignment(SwingConstants.LEFT);
                    label.setBorder(new EmptyBorder(0, 16, 0, 8)); 
                }
                return label;
            }
        };

        for (int i = 0; i < COLUMNS.length; i++) {
            table.getColumnModel().getColumn(i).setHeaderRenderer(hdrRdr);
        }

        int[] widths = {50, 100, 200, 120, 250, 250};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        ZebraHoverRenderer zebraRdr = new ZebraHoverRenderer(table);
        for (int i = 1; i < COLUMNS.length; i++) {
            if (i == 3) continue; // Cột Loại kho dùng Badge riêng
            table.getColumnModel().getColumn(i).setCellRenderer(zebraRdr);
        }
        
        table.getColumnModel().getColumn(0).setCellRenderer(new ZebraCheckBoxRenderer(table));
        table.getColumnModel().getColumn(3).setCellRenderer(new BadgeRenderer(table)); // Cột Loại Kho

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder()); 
        scroll.getViewport().setBackground(Color.WHITE);

        JScrollBar vScroll = scroll.getVerticalScrollBar();
        vScroll.setUnitIncrement(16); 
        vScroll.setPreferredSize(new Dimension(0, 0)); 

        JPanel tableWrapper = new JPanel(new BorderLayout()) {
            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(229, 231, 235)); 
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
            }
        };
        tableWrapper.setOpaque(false);
        tableWrapper.setBorder(new EmptyBorder(1, 1, 1, 1)); 
        tableWrapper.add(scroll, BorderLayout.CENTER);

        JPanel layoutWrapper = new JPanel(new BorderLayout());
        layoutWrapper.setOpaque(false);
        layoutWrapper.setBorder(new EmptyBorder(0, 16, 16, 16)); 
        layoutWrapper.add(tableWrapper, BorderLayout.CENTER);

        card.add(toolBar, BorderLayout.NORTH);
        card.add(layoutWrapper, BorderLayout.CENTER); 

        return card;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ACTIONS
    // ─────────────────────────────────────────────────────────────────────────
    private void showForm(String maKho) {
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        String title = (maKho == null) ? "Thêm mới Kho bãi" : "Cập nhật Kho bãi";
        
        JDialog dialog = new JDialog(parentWindow, title, Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setLayout(new BorderLayout());
        dialog.setResizable(false);

        KhoForm form = new KhoForm(maKho);
        dialog.add(form, BorderLayout.CENTER);

        JPanel bottomWrapper = new JPanel(new BorderLayout());
        bottomWrapper.setBackground(AppColor.SURFACE);
        bottomWrapper.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, AppColor.BORDER), 
                BorderFactory.createEmptyBorder(16, 32, 16, 32)
        ));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0)); 
        buttonPanel.setBackground(AppColor.SURFACE);

        JButton btnCancel = new JButton("Hủy") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AppColor.ERROR);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCancel.setPreferredSize(new Dimension(100, 42)); 
        btnCancel.setContentAreaFilled(false); 
        btnCancel.setBorderPainted(false);
        btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton btnSave = new JButton("Xác nhận") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AppColor.SUCCESS); 
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSave.setPreferredSize(new Dimension(120, 42));
        btnSave.setContentAreaFilled(false);
        btnSave.setBorderPainted(false);
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnSave.addActionListener(e -> {
            if (form.saveData()) {
                dialog.dispose();
                loadData(null); 
            }
        });
        btnCancel.addActionListener(e -> dialog.dispose());

        buttonPanel.add(btnCancel);
        buttonPanel.add(btnSave);

        bottomWrapper.add(buttonPanel, BorderLayout.CENTER);
        dialog.add(bottomWrapper, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setLocationRelativeTo(parentWindow);
        dialog.setVisible(true);
    }

    private void showFormForEdit() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng click chọn một kho trên bảng để sửa.",
                    "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String maKho = tableModel.getValueAt(row, 1).toString(); // Cột 1 là Mã Kho
        showForm(maKho);
    }

    // TÍNH NĂNG XÓA KHO BÃI THỰC TẾ
    private void xoaKhoNieu() {
        List<String> listMa = new ArrayList<>();
        
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Boolean isChecked = (Boolean) tableModel.getValueAt(i, 0);
            if (Boolean.TRUE.equals(isChecked)) {
                listMa.add(tableModel.getValueAt(i, 1).toString());
            }
        }

        if (listMa.isEmpty()) {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                listMa.add(tableModel.getValueAt(selectedRow, 1).toString());
            }
        }

        if (listMa.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng tick vào ô vuông hoặc click bôi đen một kho để xóa.",
                    "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String msg = listMa.size() == 1 
                ? "Bạn có chắc chắn muốn xóa kho bãi (Mã: " + listMa.get(0) + ") không?\nHành động này không thể hoàn tác."
                : "Bạn có chắc chắn muốn xóa " + listMa.size() + " kho bãi đã chọn không?\nHành động này không thể hoàn tác.";

        int confirm = JOptionPane.showConfirmDialog(
                this, msg, "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            int countSuccess = 0;
            StringBuilder errors = new StringBuilder();

            for (String ma : listMa) {
                try {
                    // Gọi hàm xóa thật từ BUS
                    if (bus.delete(ma)) {
                        countSuccess++;
                    }
                } catch (Exception ex) {
                    errors.append("- Mã ").append(ma).append(": Đang có hàng hóa lưu trữ hoặc phiếu nhập/xuất liên quan.\n");
                }
            }

            if (errors.length() > 0) {
                JOptionPane.showMessageDialog(this,
                        "Đã xóa thành công: " + countSuccess + " kho bãi.\n\nKhông thể xóa các kho sau:\n" + errors.toString(),
                        "Kết quả xóa", JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Đã xóa thành công toàn bộ " + countSuccess + " kho bãi!",
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
            }
            loadData(null);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // DATA
    // ─────────────────────────────────────────────────────────────────────────
    private void loadData(String keyword) {
        currentDataList = (keyword == null || keyword.isBlank())
                ? bus.getAll()
                : bus.timKiem(keyword);

        int mat = 0, lanh = 0, dong = 0;
        for (KhoDTO k : currentDataList) {
            if ("Mát".equals(k.getLoaiKho())) mat++;
            else if ("Lạnh".equals(k.getLoaiKho())) lanh++;
            else dong++;
        }
        
        lblTongKho.setText(String.valueOf(currentDataList.size()));
        lblKhoMat.setText(String.valueOf(mat));
        lblKhoLanh.setText(String.valueOf(lanh));
        lblKhoDong.setText(String.valueOf(dong));

        loadTableData();
    }

    private void loadTableData() {
        tableModel.setRowCount(0);
        for (KhoDTO k : currentDataList) {
            tableModel.addRow(new Object[]{
                    false, // Trạng thái checkbox ban đầu
                    k.getMaKho(),
                    k.getTenKho(),
                    k.getLoaiKho(),
                    k.getDiaChi(),
                    k.getMoTa() != null ? k.getMoTa() : ""
            });
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // HELPERS
    // ─────────────────────────────────────────────────────────────────────────
    private JButton createActionButton(String text, Color bg, Color hoverColor, Color activeColor) {
        JButton btn = new JButton(text) {
            private Color current = bg;
            {
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e)  { current = hoverColor;  repaint(); }
                    @Override public void mouseExited(MouseEvent e)   { current = bg;          repaint(); }
                    @Override public void mousePressed(MouseEvent e)  { current = activeColor; repaint(); }
                    @Override public void mouseReleased(MouseEvent e) { current = hoverColor;  repaint(); }
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
        btn.setOpaque(false);
        btn.setPreferredSize(new Dimension(80, 36));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton createIconButton(String fallbackText) {
        JButton btn = new JButton();
        try {
            btn.setIcon(new com.formdev.flatlaf.extras.FlatSVGIcon("icons/refresh.svg", 18, 18));
        } catch (Throwable ex) {
            btn.setText(fallbackText);
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

    private JMenuItem createStyledMenuItem(String text) {
        JMenuItem item = new JMenuItem(text);
        item.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        item.setBackground(Color.WHITE);
        item.setForeground(AppColor.TEXT_PRIMARY);
        item.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        item.setPreferredSize(new Dimension(150, 36)); 

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
                super.paintText(g2, menuItem, textRect, text);
                g2.dispose();
            }
        });
        return item;
    }

    // ═════════════════════════════════════════════════════════════════════════
    // INNER CLASSES (GIAO DIỆN)
    // ═════════════════════════════════════════════════════════════════════════

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
                g2.fill(new RoundRectangle2D.Float(i, i + 1,
                        getWidth() - i * 2, getHeight() - i * 2, arc, arc));
            }
            g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 5, getHeight() - 5, arc, arc));
            g2.dispose();
            super.paintComponent(g);
        }
    }

    static class ZebraCheckBoxRenderer extends JCheckBox implements TableCellRenderer {
        private int hoverRow = -1;

        ZebraCheckBoxRenderer(JTable tbl) {
            setHorizontalAlignment(SwingConstants.CENTER);
            setOpaque(true);
            tbl.addMouseMotionListener(new MouseMotionAdapter() {
                @Override public void mouseMoved(MouseEvent e) {
                    int r = tbl.rowAtPoint(e.getPoint());
                    if (r != hoverRow) { hoverRow = r; tbl.repaint(); }
                }
            });
            tbl.addMouseListener(new MouseAdapter() {
                @Override public void mouseExited(MouseEvent e) { hoverRow = -1; tbl.repaint(); }
            });
        }

        @Override
        public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
            setSelected(value != null && (Boolean) value);
            
            if (isSelected)           setBackground(new Color(220, 252, 231));
            else if (row == hoverRow) setBackground(new Color(240, 253, 244));
            else if (row % 2 == 0)    setBackground(Color.WHITE); 
            else                      setBackground(new Color(250, 250, 250));
            return this;
        }
    }

    static class ZebraHoverRenderer extends DefaultTableCellRenderer {
        private int hoverRow = -1;

        ZebraHoverRenderer(JTable tbl) {
            tbl.addMouseMotionListener(new MouseMotionAdapter() {
                @Override public void mouseMoved(MouseEvent e) {
                    int r = tbl.rowAtPoint(e.getPoint());
                    if (r != hoverRow) { hoverRow = r; tbl.repaint(); }
                }
            });
            tbl.addMouseListener(new MouseAdapter() {
                @Override public void mouseExited(MouseEvent e) { hoverRow = -1; tbl.repaint(); }
            });
        }

        @Override
        public Component getTableCellRendererComponent(JTable t, Object value,
                boolean selected, boolean focused, int row, int col) {
            super.getTableCellRendererComponent(t, value, selected, focused, row, col);
            setBorder(new EmptyBorder(0, 16, 0, 8));
            
            if (col == 1) { // Mã Kho
                setFont(new Font("Segoe UI", Font.BOLD, 13));
                setForeground(new Color(31, 41, 55)); 
            } else {
                setFont(new Font("Segoe UI", Font.PLAIN, 13));
                setForeground(new Color(75, 85, 99)); 
            }

            if (selected)           setBackground(new Color(220, 252, 231));
            else if (row == hoverRow) setBackground(new Color(240, 253, 244));
            else if (row % 2 == 0)  setBackground(Color.WHITE); 
            else                    setBackground(new Color(250, 250, 250)); 
            return this;
        }
    }

    static class BadgeRenderer extends DefaultTableCellRenderer {
        private int hoverRow = -1;

        public BadgeRenderer(JTable tbl) {
            tbl.addMouseMotionListener(new MouseMotionAdapter() {
                @Override public void mouseMoved(MouseEvent e) {
                    int r = tbl.rowAtPoint(e.getPoint());
                    if (r != hoverRow) { hoverRow = r; tbl.repaint(); }
                }
            });
            tbl.addMouseListener(new MouseAdapter() {
                @Override public void mouseExited(MouseEvent e) { hoverRow = -1; tbl.repaint(); }
            });
        }

        @Override
        public Component getTableCellRendererComponent(JTable t, Object value,
                boolean selected, boolean focused, int row, int col) {

            JPanel cell = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 8));
            cell.setOpaque(true);

            if (selected)             cell.setBackground(new Color(220, 252, 231));
            else if (row == hoverRow) cell.setBackground(new Color(240, 253, 244));
            else if (row % 2 == 0)    cell.setBackground(Color.WHITE);
            else                      cell.setBackground(new Color(250, 250, 250));

            String loai = (value == null) ? "Khác" : value.toString();
            final Color badgeBg, badgeFg;
            switch (loai) {
                case "Mát":
                    badgeBg = new Color(0xE0F2FE); badgeFg = new Color(0x0369A1); break;
                case "Lạnh":
                    badgeBg = new Color(0xDBEAFE); badgeFg = new Color(0x1D4ED8); break;
                case "Đông":
                    badgeBg = new Color(0xEDE9FE); badgeFg = new Color(0x6D28D9); break;
                default:
                    badgeBg = new Color(0xF3F4F6); badgeFg = new Color(0x374151); break;
            }

            JLabel badge = new JLabel(loai) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(badgeBg);
                    g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            badge.setFont(new Font("Segoe UI", Font.BOLD, 11)); 
            badge.setForeground(badgeFg);
            badge.setOpaque(false);
            badge.setBorder(new EmptyBorder(3, 10, 3, 10));

            cell.add(badge);
            return cell;
        }
    }
}