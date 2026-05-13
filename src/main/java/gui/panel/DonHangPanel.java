package gui.panel;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.RoundRectangle2D;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import bus.DonHangBUS;
import dto.DonHangDTO;
import util.AppColor;

public class DonHangPanel extends JPanel {

    // ── Data ──────────────────────────────────────────────────────────────────
    private final DonHangBUS bus = new DonHangBUS();
    private List<DonHangDTO> currentDataList = new ArrayList<>();
    private String currentMaKH = "KH000017"; // Giả lập mã KH đang đăng nhập

    // ── UI ────────────────────────────────────────────────────────────────────
    private JTable table;
    private DefaultTableModel tableModel;
    private ModernSearchField searchField;

    // ── Stat labels ───────────────────────────────────────────────────────────
    private JLabel lblTongDH, lblDangXuLy, lblDaGiao, lblDaHuy;

    private static final String[] COLUMNS = {
            "MÃ ĐH", "NGÀY ĐẶT", "SẢN PHẨM", "TỔNG TIỀN", "TRẠNG THÁI", "HÀNH ĐỘNG"
    };

    public DonHangPanel() {
        setLayout(new BorderLayout());
        setBackground(AppColor.BACKGROUND);
        setBorder(new EmptyBorder(24, 24, 24, 24));
        
        setFocusable(true);
        addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) { requestFocusInWindow(); }
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
        
        JLabel title = new JLabel("Lịch sử đơn hàng của tôi");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28)); 
        title.setForeground(AppColor.TEXT_PRIMARY);
        titleCard.add(title, BorderLayout.WEST);
        
        topWrapper.add(titleCard);
        topWrapper.add(Box.createVerticalStrut(18));
        
        // 2. Thống kê
        topWrapper.add(buildStats());
        topWrapper.add(Box.createVerticalStrut(18));

        add(topWrapper, BorderLayout.NORTH);

        // 3. Khu vực Bảng
        JPanel tableCard = buildTableCard();
        add(tableCard, BorderLayout.CENTER);

        loadData(null);
    }

    private JPanel buildStats() {
        JPanel row = new JPanel(new GridLayout(1, 4, 16, 0)); // Chia làm 4 thẻ
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblTongDH   = new JLabel("0");
        lblDangXuLy = new JLabel("0");
        lblDaGiao   = new JLabel("0");
        lblDaHuy    = new JLabel("0");

        row.add(createStatCard("Tất cả đơn", lblTongDH,   new Color(0x3B82F6), "📦"));
        row.add(createStatCard("Đang xử lý", lblDangXuLy, new Color(0xF59E0B), "⏳"));
        row.add(createStatCard("Hoàn thành", lblDaGiao,   new Color(0x10B981), "✅"));
        row.add(createStatCard("Đã hủy",     lblDaHuy,    new Color(0xEF4444), "❌"));
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
                d.height += 8; d.width += 4;  
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

        JButton btnRefresh = createIconButton("icons/refresh.svg");
        btnRefresh.setPreferredSize(new Dimension(36, 36));
        btnRefresh.addActionListener(e -> loadData(null));
        btnGroup.add(btnRefresh);

        JPanel searchGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        searchGroup.setOpaque(false);

        searchField = new ModernSearchField("Tìm kiếm đơn hàng...");
        searchField.setPreferredSize(new Dimension(280, 36));
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            private void onSearch() {
                String kw = searchField.getText().trim();
                // Tạm thời filter bằng mắt trên list hiện tại (vì file cũ BUS chưa có hàm timKiem)
                filterTableLocally(kw);
            }
            @Override public void insertUpdate(DocumentEvent e)  { onSearch(); }
            @Override public void removeUpdate(DocumentEvent e)  { onSearch(); }
            @Override public void changedUpdate(DocumentEvent e) { onSearch(); }
        });
        
        searchGroup.add(searchField);

        toolBar.add(btnGroup, BorderLayout.WEST);
        toolBar.add(searchGroup, BorderLayout.EAST);

        // --- Khu vực Bảng ---
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c == 5; } // Cột 5: Hành động Hủy đơn
        };

        table = new JTable(tableModel);
        table.setRowHeight(60); // Tăng chiều cao để hiện HTML nhiều dòng
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
                if(c == 3 || c == 4 || c == 5) label.setHorizontalAlignment(SwingConstants.CENTER);
                else label.setHorizontalAlignment(SwingConstants.LEFT);
                label.setBorder(new EmptyBorder(0, 16, 0, 8)); 
                return label;
            }
        };

        for (int i = 0; i < COLUMNS.length; i++) {
            table.getColumnModel().getColumn(i).setHeaderRenderer(hdrRdr);
        }

        int[] widths = {100, 150, 250, 120, 150, 100};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        ZebraHoverRenderer zebraRdr = new ZebraHoverRenderer(table);
        for (int i = 0; i < 4; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(zebraRdr); // Cột 0,1,2,3 là text
        }
        
        table.getColumnModel().getColumn(4).setCellRenderer(new BadgeStatusRenderer(table)); // Cột trạng thái
        table.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer()); // Nút Hành động
        table.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor(new JCheckBox()));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder()); 
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.getVerticalScrollBar().setUnitIncrement(16); 
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0)); 

        JPanel tableWrapper = new JPanel(new BorderLayout()) {
            @Override protected void paintBorder(Graphics g) {
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
    // DATA LOGIC
    // ─────────────────────────────────────────────────────────────────────────
    private void loadData(String keyword) {
        try {
            currentDataList = bus.getDanhSachDonHang(currentMaKH);
            
            int cTong = 0, cDang = 0, cGiao = 0, cHuy = 0;
            for (DonHangDTO dh : currentDataList) {
                cTong++;
                String st = dh.getTrangThaiDH();
                if (st.equalsIgnoreCase("Chờ xử lý") || st.equalsIgnoreCase("Đã đặt") || st.equalsIgnoreCase("Chờ giao hàng")) cDang++;
                else if (st.equalsIgnoreCase("Hoàn thành") || st.equalsIgnoreCase("Đã giao")) cGiao++;
                else if (st.equalsIgnoreCase("Đã huỷ")) cHuy++;
            }
            lblTongDH.setText(String.valueOf(cTong));
            lblDangXuLy.setText(String.valueOf(cDang));
            lblDaGiao.setText(String.valueOf(cGiao));
            lblDaHuy.setText(String.valueOf(cHuy));

            loadTableData(currentDataList);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterTableLocally(String kw) {
        if (kw == null || kw.isBlank()) {
            loadTableData(currentDataList);
            return;
        }
        List<DonHangDTO> filtered = new ArrayList<>();
        String lowerKw = kw.toLowerCase();
        for (DonHangDTO dh : currentDataList) {
            if (dh.getMaDH().toLowerCase().contains(lowerKw) ||
                (dh.getDanhSachSP() != null && dh.getDanhSachSP().toLowerCase().contains(lowerKw))) {
                filtered.add(dh);
            }
        }
        loadTableData(filtered);
    }

    private void loadTableData(List<DonHangDTO> list) {
        tableModel.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        DecimalFormat df = new DecimalFormat("#,### VND");

        for (DonHangDTO dh : list) {
            String ngayDat = dh.getTgDat() != null ? sdf.format(dh.getTgDat()) : "";
            String tongTien = df.format(dh.getTongTien());
            
            String rawSP = dh.getDanhSachSP() != null ? dh.getDanhSachSP() : "";
            String htmlDanhSachSP = "<html><div style='text-align: left; font-family: Segoe UI;'>&#8226; " 
                                  + rawSP.replace(", ", "<br>&#8226; ") 
                                  + "</div></html>";

            tableModel.addRow(new Object[]{
                    dh.getMaDH(), ngayDat, htmlDanhSachSP, tongTien, dh.getTrangThaiDH(), "Hủy đơn"
            });
        }
        updateRowHeights();
    }

    private void xuLyHuyDon(int row) {
        String maDH = (String) tableModel.getValueAt(row, 0);
        String trangThai = (String) tableModel.getValueAt(row, 4);

        int confirm = JOptionPane.showConfirmDialog(this, 
                "Bạn có chắc chắn muốn hủy đơn hàng " + maDH + " không?", 
                "Xác nhận hủy", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                DonHangDTO dto = new DonHangDTO();
                dto.setMaDH(maDH);
                dto.setTrangThaiDH(trangThai);
                bus.huyDonHang(dto);
                JOptionPane.showMessageDialog(this, "Hủy đơn hàng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadData(null); 
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi thao tác", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void updateRowHeights() {
        for (int row = 0; row < table.getRowCount(); row++) {
            int rowHeight = 60; 
            Component comp = table.prepareRenderer(table.getCellRenderer(row, 2), row, 2);
            rowHeight = Math.max(rowHeight, comp.getPreferredSize().height);
            table.setRowHeight(row, rowHeight + 5); 
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // HELPERS & INNER CLASSES
    // ─────────────────────────────────────────────────────────────────────────
    private JButton createIconButton(String svgPath) {
        JButton btn = new JButton();
        try {
            btn.setIcon(new com.formdev.flatlaf.extras.FlatSVGIcon(svgPath, 18, 18));
        } catch (Throwable ex) {
            btn.setText("↻"); btn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        }
        btn.setContentAreaFilled(false); btn.setFocusPainted(false); btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.putClientProperty("hover", true); btn.repaint(); }
            @Override public void mouseExited(MouseEvent e) { btn.putClientProperty("hover", false); btn.repaint(); }
        });
        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean isHovered = Boolean.TRUE.equals(c.getClientProperty("hover"));
                g2.setColor(isHovered ? new Color(243, 244, 246) : Color.WHITE);
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 10, 10);
                g2.setColor(new Color(209, 213, 219)); g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(1, 1, c.getWidth() - 3, c.getHeight() - 3, 10, 10);
                g2.dispose(); super.paint(g, c);
            }
        });
        return btn;
    }

    static class ModernSearchField extends JTextField {
        private final String placeholder;
        private boolean isHovered = false;
        public ModernSearchField(String placeholder) {
            this.placeholder = placeholder; setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 36));
            setFont(new Font("Segoe UI", Font.PLAIN, 13)); setForeground(AppColor.TEXT_PRIMARY);
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
            g2.setColor(Color.WHITE); g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            if (isFocusOwner()) { g2.setColor(AppColor.PRIMARY); g2.setStroke(new BasicStroke(1.5f)); } 
            else if (isHovered) { g2.setColor(new Color(156, 163, 175)); g2.setStroke(new BasicStroke(1f)); } 
            else { g2.setColor(new Color(209, 213, 219)); g2.setStroke(new BasicStroke(1f)); }
            g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 12, 12);
            super.paintComponent(g);
            if (getText().isEmpty() && !isFocusOwner()) {
                g2.setColor(new Color(156, 163, 175));
                FontMetrics fm = g.getFontMetrics();
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(placeholder, 16, y);
            }
            g2.setColor(new Color(156, 163, 175));
            int x = getWidth() - 24; int y = (getHeight() - 14) / 2;
            g2.setStroke(new BasicStroke(2f)); g2.drawOval(x, y, 10, 10); g2.drawLine(x + 8, y + 8, x + 13, y + 13);
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
        @Override public Component getTableCellRendererComponent(JTable t, Object value, boolean isS, boolean hasF, int r, int c) {
            super.getTableCellRendererComponent(t, value, isS, hasF, r, c);
            setBorder(new EmptyBorder(0, 16, 0, 8));
            if (c == 0) { 
                setFont(new Font("Segoe UI", Font.BOLD, 13)); 
                setForeground(new Color(31, 41, 55)); 
                setHorizontalAlignment(SwingConstants.LEFT); // Ép cứng lề trái
            } 
            else if (c == 3) { 
                setFont(new Font("Segoe UI", Font.BOLD, 13)); 
                setForeground(AppColor.PRIMARY); 
                setHorizontalAlignment(SwingConstants.RIGHT); // Cột tiền lề phải
            }
            else { 
                setFont(new Font("Segoe UI", Font.PLAIN, 13)); 
                setForeground(new Color(75, 85, 99)); 
                setHorizontalAlignment(SwingConstants.LEFT); // Reset lề trái cho Ngày đặt và Sản phẩm
            }

            if (isS) setBackground(new Color(220, 252, 231));
            else if (r == hoverRow) setBackground(new Color(240, 253, 244));
            else if (r % 2 == 0) setBackground(Color.WHITE); 
            else setBackground(new Color(250, 250, 250)); 
            return this;
        }
    }

    static class BadgeStatusRenderer extends DefaultTableCellRenderer {
        private int hoverRow = -1;
        public BadgeStatusRenderer(JTable tbl) {
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
        @Override public Component getTableCellRendererComponent(JTable t, Object value, boolean isS, boolean hasF, int r, int c) {
            JPanel cell = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 12));
            cell.setOpaque(true);
            if (isS) cell.setBackground(new Color(220, 252, 231));
            else if (r == hoverRow) cell.setBackground(new Color(240, 253, 244));
            else if (r % 2 == 0) cell.setBackground(Color.WHITE);
            else cell.setBackground(new Color(250, 250, 250));

            String st = (value == null) ? "" : value.toString();
            final Color bg, fg;
            if (st.equalsIgnoreCase("Hoàn thành") || st.equalsIgnoreCase("Đã giao")) {
                bg = new Color(0xD1FAE5); fg = new Color(0x065F46); // Xanh lá
            } else if (st.equalsIgnoreCase("Đã đặt") || st.equalsIgnoreCase("Chờ xử lý") || st.equalsIgnoreCase("Chờ giao hàng")) {
                bg = new Color(0xFEF3C7); fg = new Color(0xD97706); // Vàng
            } else {
                bg = new Color(0xFEE2E2); fg = new Color(0x991B1B); // Đỏ (Hủy)
            }

            JLabel badge = new JLabel(st) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(bg); g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
                    g2.dispose(); super.paintComponent(g);
                }
            };
            badge.setFont(new Font("Segoe UI", Font.BOLD, 11)); badge.setForeground(fg);
            badge.setOpaque(false); badge.setBorder(new EmptyBorder(4, 12, 4, 12));
            cell.add(badge);
            return cell;
        }
    }

    class ButtonRenderer extends JButton implements TableCellRenderer {
        private int hoverRow = -1;
        public ButtonRenderer() {
            setOpaque(true); setBorderPainted(false); setFocusPainted(false); setCursor(new Cursor(Cursor.HAND_CURSOR));
            setFont(new Font("Segoe UI", Font.BOLD, 12));
        }
        @Override public Component getTableCellRendererComponent(JTable t, Object value, boolean isS, boolean hasF, int r, int c) {
            String status = (String) t.getValueAt(r, 4);
            JPanel cell = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
            cell.setOpaque(true);
            if (isS) cell.setBackground(new Color(220, 252, 231));
            else if (r % 2 == 0) cell.setBackground(Color.WHITE);
            else cell.setBackground(new Color(250, 250, 250));

            if (status.equalsIgnoreCase("Đã đặt") || status.equalsIgnoreCase("Chờ xử lý")) {
                setText("Huỷ đơn"); setBackground(AppColor.ERROR); setForeground(Color.WHITE); setEnabled(true);
            } else {
                setText("---"); setBackground(new Color(243,244,246)); setForeground(new Color(156,163,175)); setEnabled(false);
            }
            cell.add(this); return cell;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private int clickedRow;
        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton(); button.setOpaque(true); button.setBorderPainted(false); button.setFocusPainted(false);
            button.setCursor(new Cursor(Cursor.HAND_CURSOR)); button.setFont(new Font("Segoe UI", Font.BOLD, 12));
            button.addActionListener(e -> { fireEditingStopped(); xuLyHuyDon(clickedRow); });
        }
        @Override public Component getTableCellEditorComponent(JTable t, Object value, boolean isS, int r, int c) {
            clickedRow = r;
            String status = (String) t.getValueAt(r, 4);
            if (status.equalsIgnoreCase("Đã đặt") || status.equalsIgnoreCase("Chờ xử lý")) {
                button.setText("Huỷ đơn"); button.setBackground(AppColor.ERROR); button.setForeground(Color.WHITE); button.setEnabled(true);
            } else {
                button.setText("---"); button.setBackground(new Color(243,244,246)); button.setForeground(new Color(156,163,175)); button.setEnabled(false);
            }
            return button;
        }
    }
    
}

