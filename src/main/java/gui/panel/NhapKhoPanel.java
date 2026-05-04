package gui.panel;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import util.AppColor;

public class NhapKhoPanel extends JPanel {

    private static final java.util.logging.Logger logger =
            java.util.logging.Logger.getLogger(NhapKhoPanel.class.getName());

    private static final Color PRIMARY_LIGHT = new Color(
            AppColor.PRIMARY.getRed(), AppColor.PRIMARY.getGreen(), AppColor.PRIMARY.getBlue(), 30);
    private static final Color WARNING_LIGHT = new Color(
            AppColor.WARNING.getRed(), AppColor.WARNING.getGreen(), AppColor.WARNING.getBlue(), 30);

    private JTable tbNhap;
    private JButton btnXacNhan;
    private JTextField txtSearch;
    private JLabel lblCount;
    private DefaultTableModel tableModel;

    private List<Object[]> allData = new ArrayList<>();
    private List<Object[]> filteredData = new ArrayList<>();
    private int currentPage = 1;
    private int rowsPerPage = 9; 
    private JLabel lblPageIndicator;
    private JButton btnPrev, btnNext;

    private static final String[] COL_NAMES = {
        "MÃ LÔ HÀNG", "SẢN PHẨM", "SỐ LƯỢNG",
        "KHO", "LOẠI KHO", "VỊ TRÍ", "NGÀY HẾT HẠN", "TRẠNG THÁI"
    };

    public NhapKhoPanel() {
        initUI();
        setupTable();
        loadDataToTable();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(AppColor.BACKGROUND); 
        setBorder(new EmptyBorder(24, 28, 24, 28));

        JPanel card = new RoundedPanel(14, AppColor.SURFACE);
        card.setLayout(new BorderLayout());
        card.setBorder(new LineBorder(AppColor.BORDER, 1, true));

        card.add(buildHeader(), BorderLayout.NORTH);
        card.add(buildTableArea(), BorderLayout.CENTER);
        card.add(buildFooter(), BorderLayout.SOUTH);
        add(card, BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout(12, 0));
        p.setBackground(AppColor.SURFACE);
        p.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, AppColor.BORDER),
                new EmptyBorder(18, 22, 16, 22)));

        JPanel titleBlock = new JPanel();
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        titleBlock.setOpaque(false);

        JLabel title = new JLabel("Danh sách lô hàng nhập kho");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(AppColor.TEXT_PRIMARY);

        titleBlock.add(title);
        p.add(titleBlock, BorderLayout.WEST);

        JPanel ctrls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        ctrls.setOpaque(false);
        
        ctrls.add(buildSearchWrap());
        
        JButton btnRefresh = buildOutlineBtn("⟳", 40);
        btnRefresh.addActionListener(e -> loadDataToTable());
        ctrls.add(btnRefresh);

        btnXacNhan = buildFillBtn("✓ Xác nhận nhập kho", 180);
        btnXacNhan.addActionListener(this::onXacNhan);
        ctrls.add(btnXacNhan);
        
        p.add(ctrls, BorderLayout.EAST);
        return p;
    }

    private JScrollPane buildTableArea() {
        tbNhap = new JTable() {
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
        tbNhap.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tbNhap.setRowHeight(55); 
        tbNhap.setShowVerticalLines(false);
        tbNhap.setShowHorizontalLines(true);
        tbNhap.setGridColor(AppColor.BORDER);
        tbNhap.setSelectionBackground(PRIMARY_LIGHT);
        tbNhap.setSelectionForeground(AppColor.PRIMARY_ACTIVE);
        tbNhap.setIntercellSpacing(new Dimension(0, 0));
        tbNhap.setFocusable(false);

        JTableHeader th = tbNhap.getTableHeader();
        th.setFont(new Font("Segoe UI", Font.BOLD, 11));
        th.setBackground(AppColor.BACKGROUND);
        th.setForeground(AppColor.TEXT_SECONDARY);
        th.setPreferredSize(new Dimension(0, 45));
        th.setBorder(new MatteBorder(0, 0, 1, 0, AppColor.BORDER));
        th.setReorderingAllowed(false);

        JScrollPane sp = new JScrollPane(tbNhap);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(AppColor.SURFACE);
        return sp;
    }

    private JPanel buildFooter() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(AppColor.SURFACE);
        p.setBorder(new CompoundBorder(
                new MatteBorder(1, 0, 0, 0, AppColor.BORDER),
                new EmptyBorder(12, 22, 14, 22)));

        JPanel leftInfo = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftInfo.setOpaque(false);

        lblCount = new JLabel("Hiển thị 0 - 0 của 0 lô hàng");
        lblCount.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblCount.setForeground(AppColor.TEXT_SECONDARY);
        leftInfo.add(lblCount);
        p.add(leftInfo, BorderLayout.WEST);

        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        paginationPanel.setOpaque(false);

        btnPrev = buildPageBtn("<");
        btnPrev.addActionListener(e -> {
            if (currentPage > 1) {
                currentPage--;
                renderTablePage();
            }
        });

        lblPageIndicator = new JLabel(" 1 ", SwingConstants.CENTER);
        lblPageIndicator.setOpaque(true);
        lblPageIndicator.setBackground(AppColor.PRIMARY);
        lblPageIndicator.setForeground(Color.WHITE);
        lblPageIndicator.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPageIndicator.setPreferredSize(new Dimension(30, 30));

        btnNext = buildPageBtn(">");
        btnNext.addActionListener(e -> {
            int totalPages = (int) Math.ceil((double) filteredData.size() / rowsPerPage);
            if (currentPage < totalPages) {
                currentPage++;
                renderTablePage();
            }
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
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 3 || col == 5 || col == 6;
            }
        };
        tbNhap.setModel(tableModel);

        DefaultTableCellRenderer readonlyRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                l.setBorder(new EmptyBorder(0, 10, 0, 6));
                return l;
            }
        };

        tbNhap.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                l.setForeground(sel ? AppColor.PRIMARY_ACTIVE : AppColor.PRIMARY);
                l.setFont(new Font("Segoe UI", Font.BOLD, 13));
                l.setBorder(new EmptyBorder(0, 14, 0, 6));
                return l;
            }
        });

        tbNhap.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                l.setFont(new Font("Segoe UI", Font.BOLD, 13));
                l.setBorder(new EmptyBorder(0, 6, 0, 6));
                return l;
            }
        });

        tbNhap.getColumnModel().getColumn(2).setCellRenderer(readonlyRenderer);

        java.util.Vector<String> khoList = new java.util.Vector<>();
        khoList.add("");
        try {
            khoList.addAll(new bus.NhapKhoBUS().getAllMaKho());
        } catch (Exception e) {
            logger.warning("Lỗi tải mã kho: " + e.getMessage());
        }

        JComboBox<String> cbKho = new JComboBox<>(khoList);
        styleCombo(cbKho);
        tbNhap.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(cbKho));
        tbNhap.getColumnModel().getColumn(3).setCellRenderer(new EditableComboRenderer("Chọn kho..."));

        tbNhap.getColumnModel().getColumn(4).setCellRenderer(new LoaiKhoBadgeRenderer());

        JComboBox<String> cbViTri = new JComboBox<>(new String[]{"", "A", "B", "C"});
        styleCombo(cbViTri);
        tbNhap.getColumnModel().getColumn(5).setCellEditor(new DefaultCellEditor(cbViTri));
        tbNhap.getColumnModel().getColumn(5).setCellRenderer(new EditableComboRenderer("Chọn vị trí..."));

        tbNhap.getColumnModel().getColumn(6).setCellEditor(new com.toedter.calendar.JDateChooserCellEditor());
        tbNhap.getColumnModel().getColumn(6).setCellRenderer(new EditableDateRenderer("Chọn ngày..."));

        tbNhap.getColumnModel().getColumn(7).setCellRenderer(new BadgeLabel());

        int[] widths = {115, 175, 80, 80, 95, 80, 125, 105};
        for (int i = 0; i < widths.length; i++)
            tbNhap.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        DefaultTableCellRenderer hr = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                l.setFont(new Font("Segoe UI", Font.BOLD, 11));
                l.setForeground(AppColor.TEXT_SECONDARY);
                l.setBackground(AppColor.BACKGROUND);
                l.setBorder(new CompoundBorder(
                        new MatteBorder(0, 0, 1, 0, AppColor.BORDER),
                        new EmptyBorder(0, c == 0 ? 14 : 10, 0, 6)));
                return l;
            }
        };
        for (int i = 0; i < tbNhap.getColumnCount(); i++)
            tbNhap.getColumnModel().getColumn(i).setHeaderRenderer(hr);
    }

    public void loadDataToTable() {
        try {
            bus.NhapKhoBUS bus = new bus.NhapKhoBUS();
            allData = bus.getDanhSachNhapKho();
            
            // Xóa ngay các đơn đã nhập ra khỏi danh sách nếu DB vô tình load lên
            allData.removeIf(row -> row[7] != null && row[7].toString().toLowerCase().contains("đã"));
            
            if(txtSearch != null) {
                txtSearch.setText("Tìm kiếm lô hàng...");
                txtSearch.setForeground(AppColor.TEXT_SECONDARY);
            }
            
            filterTable(); 
        } catch (Exception e) {
            logger.warning("Lỗi load data: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                    "Không thể tải dữ liệu từ database:\n" + e.getMessage(),
                    "Lỗi kết nối", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void renderTablePage() {
        if (tbNhap.isEditing()) tbNhap.getCellEditor().stopCellEditing();
        tableModel.setRowCount(0);

        int totalItems = filteredData.size();
        int totalPages = (int) Math.ceil((double) totalItems / rowsPerPage);
        
        if (currentPage < 1) currentPage = 1;
        if (currentPage > totalPages && totalPages > 0) currentPage = totalPages;

        int startIndex = (currentPage - 1) * rowsPerPage;
        int endIndex = Math.min(startIndex + rowsPerPage, totalItems);

        for (int i = startIndex; i < endIndex; i++) {
            tableModel.addRow(filteredData.get(i));
        }

        lblPageIndicator.setText(" " + currentPage + " ");
        btnPrev.setEnabled(currentPage > 1);
        btnNext.setEnabled(currentPage < totalPages);

        if (totalItems == 0) {
            lblCount.setText("Không tìm thấy lô hàng nào");
            lblPageIndicator.setText(" 0 ");
        } else {
            lblCount.setText(String.format("Hiển thị %d - %d của %d lô hàng", 
                    (startIndex + 1), endIndex, totalItems));
        }
    }

    private void onXacNhan(ActionEvent e) {
        if (tbNhap.isEditing()) tbNhap.getCellEditor().stopCellEditing();

        int row = tbNhap.getSelectedRow();
        if (row == -1) {
            warn("Vui lòng chọn một lô hàng cần nhập kho!");
            return;
        }

        String maCTLH = cell(row, 0);
        String tenSP  = cell(row, 1);
        String maKho  = cell(row, 3);

        String viTri = cell(row, 5);
        if (viTri.isEmpty()) {
            warn("Vui lòng chọn Vị trí cho lô hàng " + maCTLH + "!");
            tbNhap.editCellAt(row, 5);
            return;
        }

        Object objHH = tbNhap.getValueAt(row, 6);
        if (objHH == null || objHH.toString().trim().isEmpty() || objHH.toString().equals("Chọn ngày...")) {
            warn("Vui lòng chọn Ngày hết hạn cho lô hàng " + maCTLH + "!");
            tbNhap.editCellAt(row, 6);
            return;
        }

        java.util.Date ngayHH;
        try {
            if (objHH instanceof java.util.Date) {
                ngayHH = (java.util.Date) objHH;
            } else {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
                sdf.setLenient(false);
                ngayHH = sdf.parse(objHH.toString().trim());
            }
        } catch (Exception ex) {
            warn("Ngày hết hạn không hợp lệ! Định dạng: yyyy-MM-dd");
            return;
        }

        dto.TonKhoDTO dto = new dto.TonKhoDTO();
        dto.setMaCTLH(maCTLH);
        dto.setMaKho(maKho);
        dto.setViTri(viTri);
        dto.setTgHetHan(ngayHH);

        try {
            btnXacNhan.setEnabled(false);
            btnXacNhan.setText("Đang xử lý...");

            String result = new bus.NhapKhoBUS().xacNhanNhapKho(dto, tenSP);

            if ("SUCCESS".equals(result)) {
                JOptionPane.showMessageDialog(this,
                        "Nhập kho thành công!\nLô: " + maCTLH + "  |  SP: " + tenSP,
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
                
                // Tự động xóa dòng vừa nhập thành công khỏi danh sách hiển thị
                allData.removeIf(item -> item[0] != null && item[0].toString().equals(maCTLH));
                filterTable(); 
            } else {
                JOptionPane.showMessageDialog(this,
                        result, "Không thể nhập kho", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi hệ thống: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        } finally {
            btnXacNhan.setEnabled(true);
            btnXacNhan.setText("✓ Xác nhận nhập kho");
        }
    }

    private String cell(int row, int col) {
        Object o = tbNhap.getValueAt(row, col);
        return o == null ? "" : o.toString().trim();
    }

    private void warn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
    }

    private JPanel buildSearchWrap() {
        JPanel w = new JPanel(new BorderLayout(6, 0));
        w.setBackground(AppColor.BACKGROUND);
        w.setBorder(new CompoundBorder(
                new LineBorder(AppColor.BORDER, 1, true),
                new EmptyBorder(0, 10, 0, 10)));
        w.setPreferredSize(new Dimension(280, 36));

        JLabel icon = new JLabel("🔍");
        icon.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        w.add(icon, BorderLayout.WEST);

        txtSearch = new JTextField("Tìm kiếm lô hàng...");
        txtSearch.setBorder(BorderFactory.createEmptyBorder());
        txtSearch.setBackground(AppColor.BACKGROUND);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtSearch.setForeground(AppColor.TEXT_SECONDARY);

        txtSearch.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (txtSearch.getText().equals("Tìm kiếm lô hàng...")) {
                    txtSearch.setText("");
                    txtSearch.setForeground(AppColor.TEXT_PRIMARY);
                }
            }
            public void focusLost(FocusEvent e) {
                if (txtSearch.getText().isEmpty()) {
                    txtSearch.setText("Tìm kiếm lô hàng...");
                    txtSearch.setForeground(AppColor.TEXT_SECONDARY);
                }
            }
        });

        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { filterTable(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { filterTable(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
        });

        w.add(txtSearch, BorderLayout.CENTER);
        return w;
    }

    private void filterTable() {
        String keyword = txtSearch.getText().trim().toLowerCase();
        
        if (keyword.equals("tìm kiếm lô hàng...") || keyword.isEmpty()) {
            filteredData = new ArrayList<>(allData);
        } else {
            filteredData = allData.stream().filter(row -> {
                for (Object cell : row) {
                    if (cell != null && cell.toString().toLowerCase().contains(keyword)) {
                        return true;
                    }
                }
                return false;
            }).collect(Collectors.toList());
        }
        
        currentPage = 1;
        renderTablePage();
    }

    private JButton buildFillBtn(String text, int width) {
        JButton b = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? AppColor.PRIMARY_HOVER : AppColor.PRIMARY);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setPreferredSize(new Dimension(width, 36));
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JButton buildOutlineBtn(String text, int width) {
        JButton b = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
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
        b.setForeground(AppColor.TEXT_SECONDARY);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setPreferredSize(new Dimension(width, 36));
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private static void styleCombo(JComboBox<String> cb) {
        cb.setBackground(AppColor.SURFACE);
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cb.setBorder(new LineBorder(AppColor.BORDER, 1, true));
    }

    private static class LoaiKhoBadgeRenderer extends JPanel implements TableCellRenderer {
        private String txt = "";
        LoaiKhoBadgeRenderer() { setOpaque(true); }

        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
            txt = v == null ? "" : v.toString();
            setBackground(sel ? t.getSelectionBackground() : (r % 2 == 0 ? AppColor.BACKGROUND : AppColor.SECONDARY_HOVER));
            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (txt.isEmpty()) return;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color bg, fg;
            switch (txt.toLowerCase()) {
                case "đông" -> { bg = new Color(0xE3F2FD); fg = new Color(0x0D47A1); }
                case "lạnh" -> { bg = new Color(0xE0F7FA); fg = new Color(0x00695C); }
                default      -> { bg = new Color(0xF9FBE7); fg = new Color(0x558B2F); }
            }

            g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
            FontMetrics fm = g2.getFontMetrics();
            int tw = fm.stringWidth(txt);
            int px = 10, py = 3;
            int w = tw + px * 2, h = fm.getHeight() + py * 2;
            int x = (getWidth() - w) / 2, y = (getHeight() - h) / 2;

            g2.setColor(bg);
            g2.fill(new RoundRectangle2D.Float(x, y, w, h, h, h));
            g2.setColor(fg);
            g2.drawString(txt, x + px, y + py + fm.getAscent());
            g2.dispose();
        }
    }

    private static class EditableComboRenderer extends DefaultTableCellRenderer {
        private final String placeholder;
        EditableComboRenderer(String ph) { this.placeholder = ph; setOpaque(true); }

        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
            JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, r, c);
            l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            l.setBorder(new EmptyBorder(0, 8, 0, 0));
            String val = v == null ? "" : v.toString().trim();
            if (val.isEmpty()) {
                l.setText(placeholder);
                l.setForeground(AppColor.TEXT_SECONDARY);
            } else {
                l.setForeground(AppColor.TEXT_PRIMARY);
            }
            return l;
        }
    }

    private static class EditableDateRenderer extends DefaultTableCellRenderer {
        private final String placeholder;
        EditableDateRenderer(String ph) { this.placeholder = ph; setOpaque(true); }

        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
            JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, r, c);
            l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            l.setBorder(new EmptyBorder(0, 8, 0, 0));
            String val = v == null ? "" : v.toString().trim();
            if (val.isEmpty() || val.equals(" ")) {
                l.setText(placeholder);
                l.setForeground(AppColor.TEXT_SECONDARY);
            } else {
                l.setText(val);
                l.setForeground(AppColor.TEXT_PRIMARY);
            }
            return l;
        }
    }

    private static class BadgeLabel extends JPanel implements TableCellRenderer {
        private String txt = "";
        BadgeLabel() { setOpaque(true); }

        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
            this.txt = v == null ? "" : v.toString();
            setBackground(sel ? t.getSelectionBackground() : (r % 2 == 0 ? AppColor.BACKGROUND : AppColor.SECONDARY_HOVER));
            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g); 
            if (txt.isEmpty()) return;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            boolean done = txt.toLowerCase().contains("đã");
            Color bg = done ? PRIMARY_LIGHT : WARNING_LIGHT;
            Color fg = done ? AppColor.SUCCESS_ACTIVE  : AppColor.WARNING_ACTIVE;

            g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
            FontMetrics fm = g2.getFontMetrics();
            int tw = fm.stringWidth(txt), px = 10, py = 4;
            int w = tw + px * 2, h = fm.getHeight() + py * 2;
            int x = (getWidth() - w) / 2, y = (getHeight() - h) / 2;

            g2.setColor(bg);
            g2.fill(new RoundRectangle2D.Float(x, y, w, h, h, h));
            g2.setColor(fg);
            g2.drawString(txt, x + px, y + py + fm.getAscent());
            g2.dispose();
        }
    }

    private static class RoundedPanel extends JPanel {
        private final int radius;
        RoundedPanel(int r, Color bg) {
            this.radius = r;
            setBackground(bg);
            setOpaque(false);
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius, radius));
            g2.dispose();
        }
    }
}