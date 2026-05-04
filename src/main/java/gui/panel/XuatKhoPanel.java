package gui.panel;

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

    private static final java.util.logging.Logger logger =
            java.util.logging.Logger.getLogger(XuatKhoPanel.class.getName());

    private static final Color PRIMARY_LIGHT = new Color(
            AppColor.PRIMARY.getRed(), AppColor.PRIMARY.getGreen(), AppColor.PRIMARY.getBlue(), 30);
    private static final Color WARNING_LIGHT = new Color(
            AppColor.WARNING.getRed(), AppColor.WARNING.getGreen(), AppColor.WARNING.getBlue(), 30);

    private JTable tbXuat;
    private JTable tbPending;
    private JButton btnXacNhan;
    private JButton btnCreateRequest;
    private JButton btnRefreshPending;
    private JTextField txtSearch;
    private JLabel lblCount;
    private JLabel lblPendingCount;
    private DefaultTableModel tableModel;
    private DefaultTableModel pendingTableModel;

    private List<Object[]> allData = new ArrayList<>();
    private List<Object[]> filteredData = new ArrayList<>();
    private int currentPage = 1;
    private int rowsPerPage = 9; 
    private JLabel lblPageIndicator;
    private JButton btnPrev, btnNext;

    // Cột 0 đã được đổi thành MÃ ĐƠN HÀNG
    private static final String[] COL_NAMES = {
        "MÃ ĐƠN HÀNG", "SẢN PHẨM", "SỐ LƯỢNG",
        "NGÀY HẾT HẠN", "VỊ TRÍ", "NHÂN VIÊN", "TRẠNG THÁI"
    };

    private static final String[] PENDING_COL_NAMES = {
        "MÃ ĐƠN HÀNG", "KHÁCH HÀNG", "SỐ SP", "TỔNG SL", "TRẠNG THÁI"
    };

    public XuatKhoPanel() {
        initUI();
        setupTable();
        loadDataToPendingTable();
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
        card.add(buildTabArea(), BorderLayout.CENTER);
        add(card, BorderLayout.CENTER);
    }

    private JTabbedPane buildTabArea() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabs.setBackground(AppColor.SURFACE);
        tabs.setForeground(AppColor.TEXT_PRIMARY);
        tabs.setBorder(BorderFactory.createEmptyBorder(0, 16, 16, 16));

        tabs.addTab("Đơn hàng cần xuất", buildPendingTab());
        tabs.addTab("Soạn hàng xuất kho", buildProcessTab());
        return tabs;
    }

    private JPanel buildPendingTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.add(buildPendingHeader(), BorderLayout.NORTH);
        panel.add(buildPendingTableArea(), BorderLayout.CENTER);
        panel.add(buildPendingFooter(), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildProcessTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.add(buildProcessHeader(), BorderLayout.NORTH);
        panel.add(buildTableArea(), BorderLayout.CENTER);
        panel.add(buildFooter(), BorderLayout.SOUTH);
        return panel;
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

        JLabel title = new JLabel("Quản lý xuất kho");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(AppColor.TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Quản lý cả đơn hàng cần tạo yêu cầu và danh sách soạn hàng.");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(AppColor.TEXT_SECONDARY);

        titleBlock.add(title);
        titleBlock.add(Box.createVerticalStrut(4));
        titleBlock.add(subtitle);
        p.add(titleBlock, BorderLayout.WEST);

        return p;
    }

    private JPanel buildPendingHeader() {
        JPanel p = new JPanel(new BorderLayout(12, 0));
        p.setOpaque(false);
        p.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, AppColor.BORDER),
                new EmptyBorder(18, 22, 16, 22)));

        JLabel title = new JLabel("Đơn hàng cần tạo yêu cầu xuất kho");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(AppColor.TEXT_PRIMARY);

        JPanel ctrls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        ctrls.setOpaque(false);

        btnRefreshPending = buildOutlineBtn("⟳", 40);
        btnRefreshPending.addActionListener(e -> loadDataToPendingTable());
        ctrls.add(btnRefreshPending);

        btnCreateRequest = buildFillBtn("Tạo yêu cầu xuất kho", 180);
        btnCreateRequest.addActionListener(this::onCreateRequest);
        ctrls.add(btnCreateRequest);

        p.add(title, BorderLayout.WEST);
        p.add(ctrls, BorderLayout.EAST);
        return p;
    }

    private JPanel buildProcessHeader() {
        JPanel p = new JPanel(new BorderLayout(12, 0));
        p.setOpaque(false);
        p.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, AppColor.BORDER),
                new EmptyBorder(18, 22, 16, 22)));

        JLabel title = new JLabel("Danh sách đơn hàng xuất kho (Soạn hàng)");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(AppColor.TEXT_PRIMARY);

        JPanel ctrls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        ctrls.setOpaque(false);
        ctrls.add(buildSearchWrap());

        JButton btnRefresh = buildOutlineBtn("⟳", 40);
        btnRefresh.addActionListener(e -> loadDataToTable());
        ctrls.add(btnRefresh);

        btnXacNhan = buildFillBtn("✓ Xác nhận hoàn tất", 180);
        btnXacNhan.addActionListener(this::onXacNhan);
        ctrls.add(btnXacNhan);

        p.add(title, BorderLayout.WEST);
        p.add(ctrls, BorderLayout.EAST);
        return p;
    }

    private JScrollPane buildPendingTableArea() {
        tbPending = new JTable();
        styleStandardTable(tbPending);

        pendingTableModel = new DefaultTableModel(PENDING_COL_NAMES, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        tbPending.setModel(pendingTableModel);

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
        for (int i = 0; i < tbPending.getColumnCount(); i++) {
            tbPending.getColumnModel().getColumn(i).setHeaderRenderer(hr);
        }

        int[] widths = {120, 180, 80, 100, 140};
        for (int i = 0; i < widths.length; i++) {
            tbPending.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        JScrollPane sp = new JScrollPane(tbPending);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(AppColor.SURFACE);
        return sp;
    }

    private JPanel buildPendingFooter() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(AppColor.SURFACE);
        p.setBorder(new CompoundBorder(
                new MatteBorder(1, 0, 0, 0, AppColor.BORDER),
                new EmptyBorder(12, 22, 14, 22)));

        lblPendingCount = new JLabel("Hiển thị 0 đơn hàng cần tạo yêu cầu");
        lblPendingCount.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblPendingCount.setForeground(AppColor.TEXT_SECONDARY);
        p.add(lblPendingCount, BorderLayout.WEST);
        return p;
    }

    private void styleStandardTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(55);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(AppColor.BORDER);
        table.setSelectionBackground(PRIMARY_LIGHT);
        table.setSelectionForeground(AppColor.PRIMARY_ACTIVE);
        table.setIntercellSpacing(new Dimension(0, 0));

        JTableHeader th = table.getTableHeader();
        th.setFont(new Font("Segoe UI", Font.BOLD, 11));
        th.setBackground(AppColor.BACKGROUND);
        th.setForeground(AppColor.TEXT_SECONDARY);
        th.setPreferredSize(new Dimension(0, 45));
        th.setBorder(new MatteBorder(0, 0, 1, 0, AppColor.BORDER));
        th.setReorderingAllowed(false);
    }

    private void loadDataToPendingTable() {
        try {
            ArrayList<Object[]> data = new XuatKhoBUS().getDanhSachDonHangChoXuat();
            pendingTableModel.setRowCount(0);
            for (Object[] row : data) {
                pendingTableModel.addRow(row);
            }
            lblPendingCount.setText(String.format("Hiển thị %d đơn hàng cần tạo yêu cầu", data.size()));
        } catch (Exception e) {
            logger.warning("Lỗi tải danh sách đơn hàng cần xuất: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                    "Không thể tải danh sách đơn hàng cần xuất:\n" + e.getMessage(),
                    "Lỗi kết nối", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onCreateRequest(ActionEvent e) {
        int row = tbPending.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn một đơn hàng trước khi tạo yêu cầu xuất kho.",
                    "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String maDH = tbPending.getValueAt(row, 0).toString();
        String result = new XuatKhoBUS().yeuCauXuatKho(maDH);

        if ("SUCCESS".equals(result)) {
            JOptionPane.showMessageDialog(this,
                    "Đã tạo yêu cầu xuất kho cho đơn hàng: " + maDH,
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadDataToPendingTable();
            loadDataToTable();
        } else {
            JOptionPane.showMessageDialog(this,
                    result,
                    "Không thể tạo yêu cầu", JOptionPane.WARNING_MESSAGE);
        }
    }

    private JScrollPane buildTableArea() {
        tbXuat = new JTable() {
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
        tbXuat.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tbXuat.setRowHeight(55); 
        tbXuat.setShowVerticalLines(false);
        tbXuat.setShowHorizontalLines(true);
        tbXuat.setGridColor(AppColor.BORDER);
        tbXuat.setSelectionBackground(PRIMARY_LIGHT);
        tbXuat.setSelectionForeground(AppColor.PRIMARY_ACTIVE);
        tbXuat.setIntercellSpacing(new Dimension(0, 0));
        tbXuat.setFocusable(false);

        JTableHeader th = tbXuat.getTableHeader();
        th.setFont(new Font("Segoe UI", Font.BOLD, 11));
        th.setBackground(AppColor.BACKGROUND);
        th.setForeground(AppColor.TEXT_SECONDARY);
        th.setPreferredSize(new Dimension(0, 45));
        th.setBorder(new MatteBorder(0, 0, 1, 0, AppColor.BORDER));
        th.setReorderingAllowed(false);

        JScrollPane sp = new JScrollPane(tbXuat);
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

        lblCount = new JLabel("Hiển thị 0 - 0 của 0 chi tiết xuất kho");
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
                return col == 5; // Chỉ cho phép sửa cột Nhân viên soạn hàng (Index 5)
            }
        };
        tbXuat.setModel(tableModel);

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

        tbXuat.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                l.setForeground(sel ? AppColor.PRIMARY_ACTIVE : AppColor.PRIMARY);
                l.setFont(new Font("Segoe UI", Font.BOLD, 13));
                l.setBorder(new EmptyBorder(0, 14, 0, 6));
                return l;
            }
        });

        tbXuat.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                l.setFont(new Font("Segoe UI", Font.BOLD, 13));
                l.setBorder(new EmptyBorder(0, 6, 0, 6));
                return l;
            }
        });

        tbXuat.getColumnModel().getColumn(2).setCellRenderer(readonlyRenderer); // Số lượng
        tbXuat.getColumnModel().getColumn(3).setCellRenderer(readonlyRenderer); // Ngày hết hạn
        tbXuat.getColumnModel().getColumn(4).setCellRenderer(readonlyRenderer); // Vị trí

        java.util.Vector<String> nvList = new java.util.Vector<>();
        nvList.add("");
        try {
            nvList.addAll(new XuatKhoBUS().getAllMaNhanVien());
        } catch (Exception e) {
            logger.warning("Lỗi tải danh sách nhân viên: " + e.getMessage());
        }

        JComboBox<String> cbNV = new JComboBox<>(nvList);
        styleCombo(cbNV);
        tbXuat.getColumnModel().getColumn(5).setCellEditor(new DefaultCellEditor(cbNV));
        tbXuat.getColumnModel().getColumn(5).setCellRenderer(new EditableComboRenderer("Chọn nhân viên..."));

        tbXuat.getColumnModel().getColumn(6).setCellRenderer(new BadgeLabel());

        int[] widths = {120, 200, 90, 100, 120, 150, 120};
        for (int i = 0; i < widths.length; i++)
            tbXuat.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

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
        for (int i = 0; i < tbXuat.getColumnCount(); i++)
            tbXuat.getColumnModel().getColumn(i).setHeaderRenderer(hr);
    }

    public void loadDataToTable() {
        try {
            XuatKhoBUS bus = new XuatKhoBUS();
            allData = bus.getDanhSachSoanHang();
            
            allData.removeIf(row -> row[6] != null && row[6].toString().toLowerCase().contains("đã"));
            
            if(txtSearch != null) {
                txtSearch.setText("Tìm kiếm đơn hàng...");
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
        if (tbXuat.isEditing()) tbXuat.getCellEditor().stopCellEditing();
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
            lblCount.setText("Không tìm thấy dòng chi tiết nào");
            lblPageIndicator.setText(" 0 ");
        } else {
            lblCount.setText(String.format("Hiển thị %d - %d của %d dòng", 
                    (startIndex + 1), endIndex, totalItems));
        }
    }

    private void onXacNhan(ActionEvent e) {
        if (tbXuat.isEditing()) tbXuat.getCellEditor().stopCellEditing();

        int row = tbXuat.getSelectedRow();
        if (row == -1) {
            warn("Vui lòng chọn một dòng để xác nhận xuất đơn hàng tương ứng!");
            return;
        }

        String maDH = cell(row, 0); // Lấy MaDH
        String maNV = cell(row, 5); 

        if (maNV.isEmpty() || maNV.equals("Chọn nhân viên...")) {
            warn("Vui lòng chọn Nhân viên thực hiện cho Đơn hàng " + maDH + "!");
            tbXuat.editCellAt(row, 5);
            return;
        }

        XuatKhoDTO dto = new XuatKhoDTO();
        dto.setMaXK(maDH); // Truyền MaDH vào biến MaXK
        dto.setMaNV(maNV);

        try {
            btnXacNhan.setEnabled(false);
            btnXacNhan.setText("Đang xử lý...");

            String result = new XuatKhoBUS().xacNhanSoanHang(dto);

            if ("SUCCESS".equals(result)) {
                JOptionPane.showMessageDialog(this,
                        "Đã xác nhận xuất kho thành công toàn bộ Đơn hàng: " + maDH,
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
                
                // Tự động xóa TẤT CẢ các dòng có chung Mã đơn hàng (Gom đơn)
                allData.removeIf(item -> item[0] != null && item[0].toString().equals(maDH));
                filterTable(); 
            } else {
                JOptionPane.showMessageDialog(this,
                        result, "Không thể xác nhận", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi hệ thống: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        } finally {
            btnXacNhan.setEnabled(true);
            btnXacNhan.setText("✓ Xác nhận hoàn tất");
        }
    }

    private String cell(int row, int col) {
        Object o = tbXuat.getValueAt(row, col);
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

        txtSearch = new JTextField("Tìm kiếm đơn hàng...");
        txtSearch.setBorder(BorderFactory.createEmptyBorder());
        txtSearch.setBackground(AppColor.BACKGROUND);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtSearch.setForeground(AppColor.TEXT_SECONDARY);

        txtSearch.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (txtSearch.getText().equals("Tìm kiếm đơn hàng...")) {
                    txtSearch.setText("");
                    txtSearch.setForeground(AppColor.TEXT_PRIMARY);
                }
            }
            public void focusLost(FocusEvent e) {
                if (txtSearch.getText().isEmpty()) {
                    txtSearch.setText("Tìm kiếm đơn hàng...");
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
        
        if (keyword.equals("tìm kiếm đơn hàng...") || keyword.isEmpty()) {
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