package gui.panel;

import bus.GiaoHangBUS;
import dto.DonHangDTO;
import util.AppColor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class GiaoHangPanel extends JPanel {

    private String maNV;
    private final GiaoHangBUS bus = new GiaoHangBUS();

    // Models
    private DefaultTableModel modelChoGiao, modelDaNhan, modelLichSu;
    private JTable tblChoGiao, tblDaNhan, tblLichSu;

    private static final String[] COLUMNS = {"MÃ ĐH", "MÃ KH", "ĐỊA CHỈ GIAO", "NGÀY ĐẶT", "TỔNG TIỀN", "PHƯƠNG THỨC", "TRẠNG THÁI"};

    public GiaoHangPanel(String maNV) {
        this.maNV = maNV;
        setLayout(new BorderLayout());
        setBackground(AppColor.BACKGROUND);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        // 1. Tiêu đề
        RoundedPanel titleCard = new RoundedPanel(16);
        titleCard.setBackground(AppColor.SURFACE);
        titleCard.setLayout(new BorderLayout());
        titleCard.setBorder(new EmptyBorder(24, 24, 24, 24));
        
        JLabel title = new JLabel("Phân hệ Giao Hàng - Nhân viên: " + maNV);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(AppColor.TEXT_PRIMARY);
        titleCard.add(title, BorderLayout.WEST);

        JPanel topWrapper = new JPanel();
        topWrapper.setLayout(new BoxLayout(topWrapper, BoxLayout.Y_AXIS));
        topWrapper.setOpaque(false);
        topWrapper.add(titleCard);
        topWrapper.add(Box.createVerticalStrut(18));
        add(topWrapper, BorderLayout.NORTH);

        // 2. TabbedPane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 15));
        tabbedPane.setBackground(Color.WHITE);
        tabbedPane.setFocusable(false);

        // Tạo 3 tab
        tabbedPane.addTab("ĐƠN CHỜ GIAO", buildTabChoGiao());
        tabbedPane.addTab("ĐƠN ĐÃ NHẬN", buildTabDaNhan());
        tabbedPane.addTab("LỊCH SỬ GIAO", buildTabLichSu());

        RoundedPanel mainCard = new RoundedPanel(16);
        mainCard.setBackground(AppColor.SURFACE);
        mainCard.setLayout(new BorderLayout());
        mainCard.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainCard.add(tabbedPane, BorderLayout.CENTER);

        add(mainCard, BorderLayout.CENTER);

        // Load dữ liệu lần đầu
        loadData();
    }

    // ================== TAB 1: ĐƠN CHỜ GIAO ==================
    private JPanel buildTabChoGiao() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(16, 0, 0, 0));

        JPanel toolBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        toolBar.setOpaque(false);

        JButton btnNhanDon = createActionButton("Xác nhận giao", AppColor.INFO, AppColor.INFO_HOVER, AppColor.INFO_ACTIVE);
        JButton btnRefresh = createActionButton("Làm mới", AppColor.SUCCESS, AppColor.SUCCESS_HOVER, AppColor.SUCCESS_ACTIVE);

        btnNhanDon.addActionListener(e -> nhanDonAction());
        btnRefresh.addActionListener(e -> loadData());

        toolBar.add(btnRefresh);
        toolBar.add(btnNhanDon);
        panel.add(toolBar, BorderLayout.NORTH);

        modelChoGiao = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblChoGiao = new JTable(modelChoGiao);
        setupTable(tblChoGiao);
        panel.add(createTableScroll(tblChoGiao), BorderLayout.CENTER);

        return panel;
    }

    // ================== TAB 2: ĐƠN ĐÃ NHẬN ==================
    private JPanel buildTabDaNhan() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(16, 0, 0, 0));

        JPanel toolBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        toolBar.setOpaque(false);

        JButton btnThanhCong = createActionButton("Giao thành công", AppColor.SUCCESS, AppColor.SUCCESS_HOVER, AppColor.SUCCESS_ACTIVE);
        JButton btnThatBai = createActionButton("Giao thất bại", AppColor.ERROR, AppColor.ERROR_HOVER, AppColor.ERROR_ACTIVE);

        btnThanhCong.addActionListener(e -> giaoThanhCongAction());
        btnThatBai.addActionListener(e -> giaoThatBaiAction());

        toolBar.add(btnThatBai);
        toolBar.add(btnThanhCong);
        panel.add(toolBar, BorderLayout.NORTH);

        modelDaNhan = new DefaultTableModel(COLUMNS, 0){
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblDaNhan = new JTable(modelDaNhan);
        setupTable(tblDaNhan);
        panel.add(createTableScroll(tblDaNhan), BorderLayout.CENTER);

        return panel;
    }

    // ================== TAB 3: LỊCH SỬ GIAO ==================
    private JPanel buildTabLichSu() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(16, 0, 0, 0));

        modelLichSu = new DefaultTableModel(COLUMNS, 0){
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblLichSu = new JTable(modelLichSu);
        setupTable(tblLichSu);
        panel.add(createTableScroll(tblLichSu), BorderLayout.CENTER);

        return panel;
    }

    // ================== ACTIONS ==================
    private void nhanDonAction() {
        int row = tblChoGiao.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đơn hàng cần nhận!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String maDH = modelChoGiao.getValueAt(row, 0).toString();
        if (bus.nhanDonGiao(maDH, maNV)) {
            JOptionPane.showMessageDialog(this, "Nhận đơn thành công!");
            loadData();
        } else {
            JOptionPane.showMessageDialog(this, "Nhận đơn thất bại. Có thể đơn đã bị người khác nhận!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            loadData();
        }
    }

    private void giaoThanhCongAction() {
        int row = tblDaNhan.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đơn hàng để cập nhật!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String maDH = modelDaNhan.getValueAt(row, 0).toString();
        if (JOptionPane.showConfirmDialog(this, "Xác nhận đã giao đơn hàng " + maDH + " thành công?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            if (bus.giaoThanhCong(maDH, maNV)) {
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void giaoThatBaiAction() {
        int row = tblDaNhan.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đơn hàng báo thất bại!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String maDH = modelDaNhan.getValueAt(row, 0).toString();
        String lyDo = JOptionPane.showInputDialog(this, "Nhập lý do giao thất bại (Bắt buộc):", "Giao thất bại", JOptionPane.QUESTION_MESSAGE);
        
        if (lyDo != null) {
            if (lyDo.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Lý do không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (bus.giaoThatBai(maDH, maNV, lyDo)) {
                JOptionPane.showMessageDialog(this, "Đã cập nhật trạng thái thất bại.");
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ================== LOAD DATA ==================
    private void loadData() {
        // Tab 1
        modelChoGiao.setRowCount(0);
        for (DonHangDTO dh : bus.layDonChoGiao()) { // Lưu ý: Hàm này đã không cần truyền MaNV
            modelChoGiao.addRow(new Object[]{dh.getMaDH(), dh.getMaKH(), dh.getDiaChiGiaoHang(), dh.getTgDat(), dh.getTongTien(), dh.getPhuongThucTT(), dh.getTrangThaiDH()});
        }
        // Tab 2
        modelDaNhan.setRowCount(0);
        for (DonHangDTO dh : bus.layDonDaNhan(maNV)) {
            modelDaNhan.addRow(new Object[]{dh.getMaDH(), dh.getMaKH(), dh.getDiaChiGiaoHang(), dh.getTgDat(), dh.getTongTien(), dh.getPhuongThucTT(), dh.getTrangThaiDH()});
        }
        // Tab 3
        modelLichSu.setRowCount(0);
        for (DonHangDTO dh : bus.lichSuGiaoHang(maNV)) {
            modelLichSu.addRow(new Object[]{dh.getMaDH(), dh.getMaKH(), dh.getDiaChiGiaoHang(), dh.getTgDat(), dh.getTongTien(), dh.getPhuongThucTT(), dh.getTrangThaiDH()});
        }
    }

    // ================== UI HELPERS ==================
    private JScrollPane createTableScroll(JTable tbl) {
        JScrollPane scroll = new JScrollPane(tbl);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        scroll.getViewport().setBackground(Color.WHITE);
        return scroll;
    }

    private void setupTable(JTable table) {
        table.setRowHeight(48);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(229, 231, 235));
        table.setBackground(Color.WHITE);
        table.setSelectionBackground(new Color(220, 252, 231));
        table.setSelectionForeground(AppColor.TEXT_PRIMARY);
        table.setFocusable(false);

        JTableHeader header = table.getTableHeader();
        header.setPreferredSize(new Dimension(0, 52));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(209, 213, 219)));
        header.setReorderingAllowed(false);

        DefaultTableCellRenderer hdrRdr = new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean isS, boolean hasF, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, isS, hasF, r, c);
                comp.setFont(new Font("Segoe UI", Font.BOLD, 14));
                comp.setForeground(new Color(17, 24, 39));
                comp.setBackground(new Color(243, 244, 246));
                ((JLabel) comp).setBorder(new EmptyBorder(0, 16, 0, 8));
                return comp;
            }
        };
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setHeaderRenderer(hdrRdr);
        }

        ZebraHoverRenderer zebraRdr = new ZebraHoverRenderer(table);
        for (int i = 0; i < table.getColumnCount(); i++) {
            if (i == 6) { // Cột trạng thái
                table.getColumnModel().getColumn(i).setCellRenderer(new BadgeStatusRenderer(table));
            } else {
                table.getColumnModel().getColumn(i).setCellRenderer(zebraRdr);
            }
        }
    }

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
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
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
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ================= INNER CLASSES =================
    static class RoundedPanel extends JPanel {
        private final int arc;
        RoundedPanel(int arc) { this.arc = arc; setOpaque(false); }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
            g2.setColor(new Color(229, 231, 235)); // Border mỏng
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
            g2.dispose();
            super.paintComponent(g);
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
        @Override public Component getTableCellRendererComponent(JTable t, Object value, boolean selected, boolean focused, int row, int col) {
            super.getTableCellRendererComponent(t, value, selected, focused, row, col);
            setBorder(new EmptyBorder(0, 16, 0, 8));
            setFont(new Font("Segoe UI", col == 0 ? Font.BOLD : Font.PLAIN, 13));
            setForeground(col == 0 ? new Color(31, 41, 55) : new Color(75, 85, 99));

            if (selected) setBackground(new Color(220, 252, 231));
            else if (row == hoverRow) setBackground(new Color(240, 253, 244));
            else if (row % 2 == 0) setBackground(Color.WHITE);
            else setBackground(new Color(250, 250, 250));
            return this;
        }
    }

    static class BadgeStatusRenderer extends DefaultTableCellRenderer {
        private int hoverRow = -1;
        BadgeStatusRenderer(JTable tbl) {
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
        @Override public Component getTableCellRendererComponent(JTable t, Object value, boolean selected, boolean focused, int row, int col) {
            JPanel cell = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 8));
            cell.setOpaque(true);

            if (selected) cell.setBackground(new Color(220, 252, 231));
            else if (row == hoverRow) cell.setBackground(new Color(240, 253, 244));
            else if (row % 2 == 0) cell.setBackground(Color.WHITE);
            else cell.setBackground(new Color(250, 250, 250));

            String status = value != null ? value.toString() : "";
            Color badgeBg, badgeFg;
            
            switch (status) {
                case "Chờ giao hàng": badgeBg = new Color(0xDBEAFE); badgeFg = new Color(0x1E3A8A); break; // Xanh dương
                case "Đang giao":     badgeBg = new Color(0xFEF3C7); badgeFg = new Color(0xD97706); break; // Vàng
                case "Hoàn thành":    badgeBg = new Color(0xD1FAE5); badgeFg = new Color(0x065F46); break; // Xanh lá
                case "Đã huỷ":        badgeBg = new Color(0xFEE2E2); badgeFg = new Color(0x991B1B); break; // Đỏ
                default:              badgeBg = new Color(0xF3F4F6); badgeFg = new Color(0x374151); break; // Xám
            }

            JLabel badge = new JLabel(status) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(badgeBg);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            badge.setFont(new Font("Segoe UI", Font.BOLD, 11));
            badge.setForeground(badgeFg);
            badge.setOpaque(false);
            badge.setBorder(new EmptyBorder(4, 12, 4, 12));

            cell.add(badge);
            return cell;
        }
    }
}