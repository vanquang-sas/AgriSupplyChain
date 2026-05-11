package gui;

import bus.TonKhoBUS;
import com.formdev.flatlaf.FlatClientProperties;
import util.AppColor;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

public class TonKhoPanel extends JPanel {

    // Màu overlay cho hàng được chọn (giống NhapKhoPanel)
    private static final Color PRIMARY_LIGHT = new Color(
            AppColor.PRIMARY.getRed(), AppColor.PRIMARY.getGreen(), AppColor.PRIMARY.getBlue(), 30);
    private static final Color WARNING_LIGHT = new Color(
            AppColor.WARNING.getRed(), AppColor.WARNING.getGreen(), AppColor.WARNING.getBlue(), 30);
    private static final Color ERROR_LIGHT = new Color(
            AppColor.ERROR.getRed(), AppColor.ERROR.getGreen(), AppColor.ERROR.getBlue(), 30);

    private JTable table;
    private DefaultTableModel tableModel;
    private TonKhoBUS tonKhoBUS = new TonKhoBUS();
    private JTextField txtSearch;
    private JComboBox<String> cbSort;

    // ================== CÁC BIẾN CHO PHÂN TRANG ==================
    private List<Object[]> originalData = new ArrayList<>();
    private List<Object[]> currentData = new ArrayList<>();
    private int currentPage = 1;
    private final int rowsPerPage = 8;
    private int totalPages = 1;
    private JPanel paginationPanel;
    private SummaryCard cardTotal;
    private SummaryCard cardLowStock;
    private SummaryCard cardOutOfStock;
    // =============================================================

    public TonKhoPanel() {
        initComponents();
        loadDataToTable(false);
    }

    private void initComponents() {
        this.setLayout(new BorderLayout(15, 15));
        this.setBackground(AppColor.BACKGROUND);
        this.setBorder(new EmptyBorder(24, 28, 24, 28));

        // ================= HEADER CONTAINER =================
        JPanel headerContainer = new JPanel(new BorderLayout(0, 15));
        headerContainer.setBackground(AppColor.BACKGROUND);

        // Khung tách biệt cho Tiêu đề
        JPanel titlePanel = new RoundedPanel(12, AppColor.SURFACE);
        titlePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 12));
        titlePanel.setBorder(new LineBorder(AppColor.BORDER, 1, true));

        JLabel lblTitle = new JLabel("Danh Sách Tồn Kho");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(AppColor.PRIMARY);
        titlePanel.add(lblTitle);

        // ================= TOP PANEL =================
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(AppColor.BACKGROUND);

        txtSearch = new JTextField();
        txtSearch.setPreferredSize(new Dimension(300, 38));
        txtSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Tìm kiếm sản phẩm kho...");
        txtSearch.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);
        txtSearch.putClientProperty(FlatClientProperties.STYLE, "arc: 15");

        try {
            java.net.URL searchUrl = getClass().getResource("/icons/search.png");
            if (searchUrl != null) {
                ImageIcon searchIcon = new ImageIcon(searchUrl);
                Image imgSearch = searchIcon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
                txtSearch.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, new ImageIcon(imgSearch));
            }
        } catch (Exception e) {
        }

        String[] sortOptions = { "Sắp xếp: Mới nhất", "Sắp xếp: Số lượng tăng dần", "Sắp xếp: Số lượng giảm dần" };
        cbSort = new JComboBox<>(sortOptions);
        cbSort.setPreferredSize(new Dimension(190, 38));
        cbSort.putClientProperty(FlatClientProperties.STYLE, "arc: 15");

        JButton btnEdit = buildButton("Sửa", AppColor.INFO, "/icons/edit.png");
        JButton btnDelete = buildButton("Xóa", AppColor.ERROR, "/icons/delete.png");

        JButton btnRefresh = new JButton("Làm mới");
        btnRefresh.setPreferredSize(new Dimension(100, 38));
        btnRefresh.putClientProperty(FlatClientProperties.STYLE, "arc: 15");
        btnRefresh.setBackground(AppColor.BACKGROUND);
        btnRefresh.setForeground(AppColor.TEXT_SECONDARY);
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnRefresh.setFocusPainted(false);
        btnRefresh.setCursor(new Cursor(Cursor.HAND_CURSOR));

        try {
            java.net.URL url = getClass().getResource("/icons/refresh.png");
            if (url != null) {
                ImageIcon icon = new ImageIcon(url);
                Image img = icon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
                btnRefresh.setIcon(new ImageIcon(img));
            }
        } catch (Exception e) {
        }

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftPanel.setBackground(AppColor.BACKGROUND);
        leftPanel.add(btnDelete); // Xóa trái
        leftPanel.add(btnEdit); // Sửa trái

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setBackground(AppColor.BACKGROUND);
        rightPanel.add(txtSearch); // Tìm kiếm phải
        rightPanel.add(cbSort); // Sắp xếp phải (width: 150px)
        rightPanel.add(btnRefresh); // Làm mới phải

        topPanel.add(leftPanel, BorderLayout.WEST);
        topPanel.add(rightPanel, BorderLayout.EAST);

        // ================= SUMMARY PANEL =================
        JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        summaryPanel.setBackground(AppColor.BACKGROUND);
        summaryPanel.setPreferredSize(new Dimension(0, 85));

        cardTotal = new SummaryCard("Tổng mặt hàng", AppColor.TEXT_PRIMARY, AppColor.PRIMARY, PRIMARY_LIGHT, "box");
        cardLowStock = new SummaryCard("Tồn kho thấp", AppColor.WARNING, AppColor.WARNING, WARNING_LIGHT, "warning");
        cardOutOfStock = new SummaryCard("Cần nhập gấp", AppColor.ERROR, AppColor.ERROR, ERROR_LIGHT, "warning");

        summaryPanel.add(cardTotal);
        summaryPanel.add(cardLowStock);
        summaryPanel.add(cardOutOfStock);

        JPanel titleAndTop = new JPanel(new BorderLayout(0, 15));
        titleAndTop.setBackground(AppColor.BACKGROUND);
        titleAndTop.add(titlePanel, BorderLayout.NORTH);
        titleAndTop.add(topPanel, BorderLayout.CENTER);

        headerContainer.add(titleAndTop, BorderLayout.NORTH);
        headerContainer.add(summaryPanel, BorderLayout.CENTER);

        this.add(headerContainer, BorderLayout.NORTH);

        // ================= SỰ KIỆN NÚT =================
        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            cbSort.setSelectedIndex(0);
            loadDataToTable(false);
        });

        btnEdit.addActionListener(e -> handleEdit());
        btnDelete.addActionListener(e -> handleDelete());

        // ================= SETUP TABLE (STYLE NHAPKHO) =================
        String[] columnNames = {
                "Mã Kho", "Tên Sản Phẩm", "Loại", "Nhà Cung Cấp",
                "Số Lượng", "ĐVT", "Vị Trí", "Cập Nhật", "TG Hết Hạn", "Trạng Thái"
        };

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 4)
                    return Double.class;
                return Object.class;
            }
        };

        table = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (!isRowSelected(row)) {
                    // nền xen kẻ
                    c.setBackground(row % 2 == 0 ? AppColor.BACKGROUND : AppColor.SECONDARY_HOVER);
                    c.setForeground(AppColor.TEXT_PRIMARY);
                } else {
                    c.setBackground(PRIMARY_LIGHT);
                    c.setForeground(AppColor.PRIMARY_ACTIVE);
                }
                return c;
            }
        };

        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(55);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(AppColor.BORDER);
        table.setSelectionBackground(PRIMARY_LIGHT);
        table.setSelectionForeground(AppColor.PRIMARY_ACTIVE);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFocusable(false);

        JTableHeader th = table.getTableHeader();
        th.setFont(new Font("Segoe UI", Font.BOLD, 11));
        th.setBackground(AppColor.BACKGROUND);
        th.setForeground(AppColor.TEXT_SECONDARY);
        th.setPreferredSize(new Dimension(0, 45));
        th.setBorder(new MatteBorder(0, 0, 1, 0, AppColor.BORDER));
        th.setReorderingAllowed(false);

        // Canh lề và style Header
        DefaultTableCellRenderer hr = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, r, c);

                l.setFont(new Font("Segoe UI", Font.BOLD, 12));
                l.setForeground(AppColor.TEXT_SECONDARY);
                l.setBackground(AppColor.BACKGROUND);

                // align giống body
                if (c == 4)
                    l.setHorizontalAlignment(SwingConstants.RIGHT);
                else if (c == 7 || c == 8 || c == 9) 
                    l.setHorizontalAlignment(SwingConstants.CENTER);
                else
                    l.setHorizontalAlignment(SwingConstants.LEFT);

                // padding đồng nhất
                l.setBorder(new EmptyBorder(0, 12, 0, 12));

                return l;
            }
        };

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setHeaderRenderer(hr);
        }

        class CustomCellRenderer extends DefaultTableCellRenderer {
            private int align;

            public CustomCellRenderer(int align) {
                this.align = align;
            }

            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, r, c);

                l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                l.setHorizontalAlignment(align);

                // padding đồng nhất toàn bảng
                l.setBorder(new EmptyBorder(0, 12, 0, 12));

                return l;
            }
        }

        CustomCellRenderer left = new CustomCellRenderer(SwingConstants.LEFT);
        CustomCellRenderer right = new CustomCellRenderer(SwingConstants.RIGHT);

        for (int i = 0; i < table.getColumnCount(); i++) {
            if (i == 4) {
                table.getColumnModel().getColumn(i).setCellRenderer(right); // Số lượng
            } else if (i == 9) {
                table.getColumnModel().getColumn(i).setCellRenderer(new BadgeStatusRenderer());
            } else {
                table.getColumnModel().getColumn(i).setCellRenderer(left);
            }
        }

        // Định dạng cột cụ thể
        table.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                l.setForeground(sel ? AppColor.PRIMARY_ACTIVE : AppColor.PRIMARY);
                l.setFont(new Font("Segoe UI", Font.BOLD, 13));
                l.setBorder(new EmptyBorder(0, 14, 0, 6));
                return l;
            }
        });

        table.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                l.setFont(new Font("Segoe UI", Font.BOLD, 13));
                l.setBorder(new EmptyBorder(0, 10, 0, 6));
                return l;
            }
        });

        DefaultTableCellRenderer readonlyRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                l.setBorder(new EmptyBorder(0, 10, 0, 6));
                return l;
            }
        };
        for (int i = 2; i <= 6; i++) {
            if (i != 4) table.getColumnModel().getColumn(i).setCellRenderer(readonlyRenderer);
        }

        // Renderer canh giữa cho cột Ngày (index 7, 8)
        DefaultTableCellRenderer centerReadonlyRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                l.setHorizontalAlignment(SwingConstants.CENTER);
                l.setBorder(new EmptyBorder(0, 10, 0, 6));
                return l;
            }
        };
        table.getColumnModel().getColumn(7).setCellRenderer(centerReadonlyRenderer);
        table.getColumnModel().getColumn(8).setCellRenderer(centerReadonlyRenderer);

        // Renderer canh phải cho cột Số Lượng (index 4)
        DefaultTableCellRenderer numberRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                l.setFont(new Font("Segoe UI", Font.BOLD, 13));
                l.setHorizontalAlignment(SwingConstants.RIGHT);
                l.setBorder(new EmptyBorder(0, 6, 0, 14));
                return l;
            }
        };
        table.getColumnModel().getColumn(4).setCellRenderer(numberRenderer);

        // Thay đổi thứ tự render
        table.getColumnModel().getColumn(0).setPreferredWidth(85); // Mã Kho
        table.getColumnModel().getColumn(1).setPreferredWidth(145); // Tên Sản Phẩm
        table.getColumnModel().getColumn(2).setPreferredWidth(100); // Loại 
        table.getColumnModel().getColumn(3).setPreferredWidth(130); // Nhà Cung Cấp
        table.getColumnModel().getColumn(4).setPreferredWidth(80); // Số Lượng
        table.getColumnModel().getColumn(5).setPreferredWidth(55); // ĐVT
        table.getColumnModel().getColumn(6).setPreferredWidth(90); // Vị Trí
        table.getColumnModel().getColumn(7).setPreferredWidth(105); // Cập Nhật
        table.getColumnModel().getColumn(8).setPreferredWidth(105); // TG Hết Hạn
        table.getColumnModel().getColumn(9).setPreferredWidth(95); // Trạng Thái

        table.getColumnModel().getColumn(9).setCellRenderer(new BadgeStatusRenderer());

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(AppColor.SURFACE);

        // Bọc trong RoundedPanel giống NhapKhoPanel
        JPanel tableCard = new RoundedPanel(14, AppColor.SURFACE);
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(new LineBorder(AppColor.BORDER, 1, true));
        tableCard.add(scrollPane, BorderLayout.CENTER);

        this.add(tableCard, BorderLayout.CENTER);

        // ================= BOTTOM PANEL =================
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(AppColor.BACKGROUND);
        bottomPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        paginationPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        paginationPanel.setBackground(AppColor.BACKGROUND);
        bottomPanel.add(paginationPanel, BorderLayout.EAST);

        this.add(bottomPanel, BorderLayout.SOUTH);

        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                applyFilterAndSort(false);
            }
        });
        cbSort.addActionListener(e -> applyFilterAndSort(false));
    }

    private JButton buildButton(String text, Color bg, String iconPath) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(90, 38));
        btn.putClientProperty(FlatClientProperties.STYLE, "arc: 15");
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setContentAreaFilled(true); // ✅ Giữ background màu
        btn.setOpaque(true);
        btn.setBorderPainted(false); // ✅ Loại bỏ border
        btn.setFocusPainted(false); // ✅ Loại bỏ focus border
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        try {
            java.net.URL url = getClass().getResource(iconPath);
            if (url != null) {
                ImageIcon icon = new ImageIcon(url);
                Image img = icon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
                btn.setIcon(new ImageIcon(img));
                btn.setIconTextGap(8); // ✅ Khoảng cách icon-text
            }
        } catch (Exception e) {
        }
        return btn;
    }

    // ================= XỬ LÝ SỰ KIỆN =================
    private void handleEdit() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một sản phẩm để sửa!", "Thông báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = table.convertRowIndexToModel(selectedRow);
        int dataIndex = (currentPage - 1) * rowsPerPage + modelRow;
        String maTonKho = currentData.get(dataIndex)[10].toString(); // Dùng để thao tác CSDL
        String maKho = table.getModel().getValueAt(modelRow, 0).toString(); // Hiển thị
        String tenSP = table.getModel().getValueAt(modelRow, 1).toString();
        String loai = table.getModel().getValueAt(modelRow, 2) != null ? table.getModel().getValueAt(modelRow, 2).toString() : "";
        String ncc = table.getModel().getValueAt(modelRow, 3) != null ? table.getModel().getValueAt(modelRow, 3).toString() : "";
        String soLuongStr = table.getModel().getValueAt(modelRow, 4).toString();
        String dvt = table.getModel().getValueAt(modelRow, 5) != null ? table.getModel().getValueAt(modelRow, 5).toString() : "";
        String viTri = table.getModel().getValueAt(modelRow, 6) != null ? table.getModel().getValueAt(modelRow, 6).toString() : "";
        String capNhat = table.getModel().getValueAt(modelRow, 7) != null ? table.getModel().getValueAt(modelRow, 7).toString() : "";
        String tgHetHan = table.getModel().getValueAt(modelRow, 8) != null ? table.getModel().getValueAt(modelRow, 8).toString() : "";

        // ✅ Tạo dialog chỉnh sửa
        JDialog editDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Sửa Tồn Kho", true);
        editDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        editDialog.setSize(700, 520);
        editDialog.setLocationRelativeTo(this);

        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(Color.WHITE);

        // --- Header ---
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 15));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(new MatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));
        JLabel headerLabel = new JLabel("Thông Tin Tồn Kho");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerLabel.setForeground(AppColor.PRIMARY);
        headerPanel.add(headerLabel);
        mainContainer.add(headerPanel, BorderLayout.NORTH);

        // --- Form Content ---
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(15, 25, 15, 25));
        panel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- CỘT 1 ---
        // Mã Kho
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.15;
        JLabel lblMa = new JLabel("Mã Kho:");
        lblMa.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblMa.setForeground(AppColor.TEXT_PRIMARY);
        panel.add(lblMa, gbc);

        gbc.gridx = 1; gbc.weightx = 0.35;
        JTextField txtMa = new JTextField(maKho);
        txtMa.setEditable(false);
        txtMa.setPreferredSize(new Dimension(0, 38));
        txtMa.putClientProperty(FlatClientProperties.STYLE, "arc: 10; margin: 0,10,0,10");
        txtMa.setBackground(new Color(245, 245, 245));
        txtMa.setForeground(new Color(100, 100, 100));
        panel.add(txtMa, gbc);

        // Loại
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.15;
        JLabel lblLoai = new JLabel("Loại:");
        lblLoai.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblLoai.setForeground(AppColor.TEXT_PRIMARY);
        panel.add(lblLoai, gbc);

        gbc.gridx = 1; gbc.weightx = 0.35;
        JTextField txtLoai = new JTextField(loai);
        txtLoai.setEditable(false);
        txtLoai.setPreferredSize(new Dimension(0, 38));
        txtLoai.putClientProperty(FlatClientProperties.STYLE, "arc: 10; margin: 0,10,0,10");
        txtLoai.setBackground(new Color(245, 245, 245));
        txtLoai.setForeground(new Color(100, 100, 100));
        panel.add(txtLoai, gbc);

        // Số Lượng
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.15;
        JLabel lblSoLuong = new JLabel("Số Lượng (*):");
        lblSoLuong.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblSoLuong.setForeground(AppColor.PRIMARY);
        panel.add(lblSoLuong, gbc);

        gbc.gridx = 1; gbc.weightx = 0.35;
        JTextField txtSoLuong = new JTextField(soLuongStr);
        txtSoLuong.setPreferredSize(new Dimension(0, 38));
        txtSoLuong.putClientProperty(FlatClientProperties.STYLE, "arc: 10; margin: 0,10,0,10");
        panel.add(txtSoLuong, gbc);

        // Vị Trí
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.15;
        JLabel lblViTri = new JLabel("Vị Trí (*):");
        lblViTri.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblViTri.setForeground(AppColor.PRIMARY);
        panel.add(lblViTri, gbc);

        gbc.gridx = 1; gbc.weightx = 0.35;
        JTextField txtViTri = new JTextField(viTri);
        txtViTri.setPreferredSize(new Dimension(0, 38));
        txtViTri.putClientProperty(FlatClientProperties.STYLE, "arc: 10; margin: 0,10,0,10");
        panel.add(txtViTri, gbc);

        // --- CỘT 2 ---
        // Tên Sản Phẩm
        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0.15;
        JLabel lblTen = new JLabel("Tên Sản Phẩm:");
        lblTen.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTen.setForeground(AppColor.TEXT_PRIMARY);
        panel.add(lblTen, gbc);

        gbc.gridx = 3; gbc.weightx = 0.35;
        JTextField txtTen = new JTextField(tenSP);
        txtTen.setEditable(false);
        txtTen.setPreferredSize(new Dimension(0, 38));
        txtTen.putClientProperty(FlatClientProperties.STYLE, "arc: 10; margin: 0,10,0,10");
        txtTen.setBackground(new Color(245, 245, 245));
        txtTen.setForeground(new Color(100, 100, 100));
        panel.add(txtTen, gbc);

        // Nhà Cung Cấp
        gbc.gridx = 2; gbc.gridy = 1; gbc.weightx = 0.15;
        JLabel lblNCC = new JLabel("Nhà Cung Cấp:");
        lblNCC.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblNCC.setForeground(AppColor.TEXT_PRIMARY);
        panel.add(lblNCC, gbc);

        gbc.gridx = 3; gbc.weightx = 0.35;
        JTextField txtNCC = new JTextField(ncc);
        txtNCC.setEditable(false);
        txtNCC.setPreferredSize(new Dimension(0, 38));
        txtNCC.putClientProperty(FlatClientProperties.STYLE, "arc: 10; margin: 0,10,0,10");
        txtNCC.setBackground(new Color(245, 245, 245));
        txtNCC.setForeground(new Color(100, 100, 100));
        panel.add(txtNCC, gbc);

        // ĐVT
        gbc.gridx = 2; gbc.gridy = 2; gbc.weightx = 0.15;
        JLabel lblDVT = new JLabel("ĐVT:");
        lblDVT.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblDVT.setForeground(AppColor.TEXT_PRIMARY);
        panel.add(lblDVT, gbc);

        gbc.gridx = 3; gbc.weightx = 0.35;
        JTextField txtDVT = new JTextField(dvt);
        txtDVT.setEditable(false);
        txtDVT.setPreferredSize(new Dimension(0, 38));
        txtDVT.putClientProperty(FlatClientProperties.STYLE, "arc: 10; margin: 0,10,0,10");
        txtDVT.setBackground(new Color(245, 245, 245));
        txtDVT.setForeground(new Color(100, 100, 100));
        panel.add(txtDVT, gbc);

        // Cập Nhật
        gbc.gridx = 2; gbc.gridy = 3; gbc.weightx = 0.15;
        JLabel lblCapNhat = new JLabel("Cập Nhật:");
        lblCapNhat.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblCapNhat.setForeground(AppColor.TEXT_PRIMARY);
        panel.add(lblCapNhat, gbc);

        gbc.gridx = 3; gbc.weightx = 0.35;
        JTextField txtCapNhat = new JTextField(capNhat);
        txtCapNhat.setEditable(false);
        txtCapNhat.setPreferredSize(new Dimension(0, 38));
        txtCapNhat.putClientProperty(FlatClientProperties.STYLE, "arc: 10; margin: 0,10,0,10");
        txtCapNhat.setBackground(new Color(245, 245, 245));
        txtCapNhat.setForeground(new Color(100, 100, 100));
        panel.add(txtCapNhat, gbc);

        // TG Hết Hạn
        gbc.gridx = 2; gbc.gridy = 4; gbc.weightx = 0.15;
        JLabel lblHan = new JLabel("TG Hết Hạn:");
        lblHan.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblHan.setForeground(AppColor.TEXT_PRIMARY);
        panel.add(lblHan, gbc);

        gbc.gridx = 3; gbc.weightx = 0.35;
        JTextField txtHan = new JTextField(tgHetHan);
        txtHan.setEditable(false);
        txtHan.setPreferredSize(new Dimension(0, 38));
        txtHan.putClientProperty(FlatClientProperties.STYLE, "arc: 10; margin: 0,10,0,10");
        txtHan.setBackground(new Color(245, 245, 245));
        txtHan.setForeground(new Color(100, 100, 100));
        panel.add(txtHan, gbc);

        mainContainer.add(panel, BorderLayout.CENTER);

        // --- Panel nút ---
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 12));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.setBorder(new MatteBorder(1, 0, 0, 0, new Color(230, 230, 230)));

        JButton btnCancel = new JButton("Hủy");
        btnCancel.setPreferredSize(new Dimension(100, 40));
        btnCancel.setBackground(new Color(240, 240, 240)); 
        btnCancel.setForeground(new Color(100, 100, 100));
        btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCancel.setFocusPainted(false);
        btnCancel.setBorderPainted(false);
        btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancel.putClientProperty(FlatClientProperties.STYLE, "arc: 10");

        JButton btnSave = new JButton("Lưu Thay Đổi");
        btnSave.setPreferredSize(new Dimension(140, 40));
        btnSave.setBackground(AppColor.PRIMARY);
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSave.setFocusPainted(false);
        btnSave.setBorderPainted(false);
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSave.putClientProperty(FlatClientProperties.STYLE, "arc: 10");

        btnPanel.add(btnCancel);
        btnPanel.add(btnSave);

        mainContainer.add(btnPanel, BorderLayout.SOUTH);
        editDialog.add(mainContainer);

        // ✅ Xử lý nút Lưu
        btnSave.addActionListener(e -> {
            try {
                String soLuongMoi = txtSoLuong.getText().trim();
                String viTriMoi = txtViTri.getText().trim();

                if (soLuongMoi.isEmpty() || viTriMoi.isEmpty()) {
                    JOptionPane.showMessageDialog(editDialog, "Vui lòng điền đầy đủ thông tin (*).", "Cảnh báo",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                double sl = Double.parseDouble(soLuongMoi);
                if (sl < 0) {
                    JOptionPane.showMessageDialog(editDialog, "Số lượng phải >= 0!", "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // ✅ Gọi method cập nhật từ BUS
                if (tonKhoBUS.capNhatTonKho(maTonKho, sl, viTriMoi)) {
                    JOptionPane.showMessageDialog(editDialog, "Cập nhật thành công!", "Thành công",
                            JOptionPane.INFORMATION_MESSAGE);
                    editDialog.dispose();
                    loadDataToTable(true);
                } else {
                    JOptionPane.showMessageDialog(editDialog, "Cập nhật thất bại!", "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(editDialog, "Số lượng phải là số!", "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        // ✅ Xử lý nút Hủy
        btnCancel.addActionListener(e -> editDialog.dispose());

        editDialog.setVisible(true);
    }

    private void handleDelete() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một sản phẩm để xóa!", "Thông báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = table.convertRowIndexToModel(selectedRow);
        int dataIndex = (currentPage - 1) * rowsPerPage + modelRow;
        String maTonKho = currentData.get(dataIndex)[10].toString();
        String maKho = table.getModel().getValueAt(modelRow, 0).toString();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa dữ liệu tồn kho tại kho " + maKho + "?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (tonKhoBUS.xoaTonKho(maTonKho)) {
                JOptionPane.showMessageDialog(this, "Đã xóa thành công!");
                loadDataToTable(true);
            } else {
                JOptionPane.showMessageDialog(this, "Xóa thất bại! Có thể mã này đang bị ràng buộc dữ liệu.",
                        "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ================= LOGIC DỮ LIỆU & PHÂN TRANG =================
    private void updateSummaryCards() {
        int total = originalData.size();
        int lowStock = 0;
        int outOfStock = 0;

        for (Object[] row : originalData) {
            double slConLai = row[4] != null ? (double) row[4] : 0;
            if (slConLai == 0) {
                outOfStock++;
            } else if (slConLai < 50) {
                lowStock++;
            }
        }

        if (cardTotal != null)
            cardTotal.setCount(total);
        if (cardLowStock != null)
            cardLowStock.setCount(lowStock);
        if (cardOutOfStock != null)
            cardOutOfStock.setCount(outOfStock);
    }

    private void loadDataToTable(boolean keepCurrentPage) {
        ArrayList<Object[]> list = tonKhoBUS.getDanhSachTonKho();
        originalData = (list != null) ? list : new ArrayList<>();
        updateSummaryCards();
        applyFilterAndSort(keepCurrentPage);
    }

    private void applyFilterAndSort(boolean keepCurrentPage) {
        String keyword = txtSearch.getText().trim().toLowerCase();
        currentData = new ArrayList<>();

        for (Object[] row : originalData) {
            boolean match = false;
            for (Object cell : row) {
                if (cell != null && cell.toString().toLowerCase().contains(keyword)) {
                    match = true;
                    break;
                }
            }
            if (match)
                currentData.add(row);
        }

        String sortOpt = cbSort.getSelectedItem().toString();
        currentData.sort((row1, row2) -> {
            Double s1 = row1[4] != null ? (Double) row1[4] : 0.0;
            Double s2 = row2[4] != null ? (Double) row2[4] : 0.0;
            if (sortOpt.contains("giảm dần"))
                return s2.compareTo(s1);
            if (sortOpt.contains("tăng dần"))
                return s1.compareTo(s2);
            String m1 = row1[0] != null ? row1[0].toString() : "";
            String m2 = row2[0] != null ? row2[0].toString() : "";
            return m2.compareTo(m1);
        });

        totalPages = (int) Math.ceil((double) currentData.size() / rowsPerPage);
        if (totalPages == 0)
            totalPages = 1;
        if (!keepCurrentPage)
            currentPage = 1;
        else if (currentPage > totalPages)
            currentPage = totalPages;

        renderTablePage();
        renderPaginationButtons();
    }

    // Sửa trong renderTablePage()
    private void renderTablePage() {
        tableModel.setRowCount(0);
        int start = (currentPage - 1) * rowsPerPage;
        int end = Math.min(start + rowsPerPage, currentData.size());

        for (int i = start; i < end; i++) {
            Object[] row = currentData.get(i);
            double slConLai = row[4] != null ? (double) row[4] : 0;

            // ✅ Xóa .0 nếu là số nguyên
            String soLuong = (slConLai == (long) slConLai)
                    ? String.valueOf((long) slConLai)
                    : String.valueOf(slConLai);

            String trangThai = slConLai >= 50 ? "Còn hàng" : (slConLai > 0 ? "Tồn kho thấp" : "Cần nhập gấp");
            tableModel.addRow(new Object[] {
                    row[0], row[1], row[2], row[3], row[4], soLuong, row[6], row[7],
                    row[8], trangThai
            });
        }
    }

    private void renderPaginationButtons() {
        paginationPanel.removeAll();

        JButton btnPrev = new JButton("Trang trước");
        btnPrev.setEnabled(currentPage > 1);
        btnPrev.putClientProperty(FlatClientProperties.STYLE, "arc: 10");
        btnPrev.addActionListener(e -> {
            currentPage--;
            renderTablePage();
            renderPaginationButtons();
        });
        paginationPanel.add(btnPrev);

        int maxVisible = 3;
        int startPage = Math.max(1, currentPage - 1);
        int endPage = Math.min(totalPages, startPage + maxVisible - 1);
        if (endPage - startPage < maxVisible - 1)
            startPage = Math.max(1, endPage - maxVisible + 1);

        for (int i = startPage; i <= endPage; i++) {
            int pageNum = i;
            JButton btnPage = new JButton(String.valueOf(pageNum));
            btnPage.putClientProperty(FlatClientProperties.STYLE, "arc: 10");

            if (pageNum == currentPage) {
                btnPage.setBackground(AppColor.PRIMARY);
                btnPage.setForeground(Color.WHITE);
            }
            btnPage.addActionListener(e -> {
                currentPage = pageNum;
                renderTablePage();
                renderPaginationButtons();
            });
            paginationPanel.add(btnPage);
        }

        if (endPage < totalPages) {
            JButton btnDots = new JButton("...");
            btnDots.putClientProperty(FlatClientProperties.STYLE, "arc: 10");
            btnDots.addActionListener(e -> {
                currentPage = endPage + 1;
                renderTablePage();
                renderPaginationButtons();
            });
            paginationPanel.add(btnDots);
        }

        JButton btnNext = new JButton("Trang sau");
        btnNext.setEnabled(currentPage < totalPages);
        btnNext.putClientProperty(FlatClientProperties.STYLE, "arc: 10");
        btnNext.addActionListener(e -> {
            currentPage++;
            renderTablePage();
            renderPaginationButtons();
        });
        paginationPanel.add(btnNext);

        paginationPanel.revalidate();
        paginationPanel.repaint();
    }

    // ================= RENDERER CHO BẢNG =================
    class ImageRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
            JLabel label = new JLabel();
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setVerticalAlignment(SwingConstants.CENTER);
            label.setBorder(new EmptyBorder(0, 12, 0, 12));
            label.setOpaque(true);
            label.setBackground(
                    sel ? t.getSelectionBackground() : (r % 2 == 0 ? AppColor.BACKGROUND : AppColor.SECONDARY_HOVER));

            if (v != null && !v.toString().trim().isEmpty()) {
                try {
                    java.net.URL imgUrl = getClass().getResource("/images/" + v.toString() + ".jpg");
                    if (imgUrl != null) {
                        Image img = new ImageIcon(imgUrl).getImage().getScaledInstance(45, 35, Image.SCALE_SMOOTH);
                        label.setIcon(new ImageIcon(img));
                    } else {
                        label.setText("No Image");
                        label.setFont(new Font("Segoe UI", Font.ITALIC, 11));
                    }
                } catch (Exception e) {
                    label.setText("Error");
                }
            }
            return label;
        }
    }

    private static class BadgeStatusRenderer extends JPanel implements TableCellRenderer {
        private String txt = "";

        BadgeStatusRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
            this.txt = v == null ? "" : v.toString();
            setBackground(
                    sel ? t.getSelectionBackground() : (r % 2 == 0 ? AppColor.BACKGROUND : AppColor.SECONDARY_HOVER));
            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (txt.isEmpty())
                return;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color bg, fg;
            if (txt.equals("Còn hàng")) {
                bg = PRIMARY_LIGHT;
                fg = AppColor.SUCCESS_ACTIVE;
            } else if (txt.equals("Tồn kho thấp")) {
                bg = WARNING_LIGHT;
                fg = AppColor.WARNING_ACTIVE;
            } else { // Hết hàng
                bg = ERROR_LIGHT;
                fg = AppColor.ERROR;
            }

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

    private static class IconPanel extends RoundedPanel {
        private String type;
        private Color color;

        IconPanel(int r, Color bg, Color color, String type) {
            super(r, bg);
            this.color = color;
            this.type = type;
            setPreferredSize(new Dimension(45, 45));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            int w = getWidth();
            int h = getHeight();

            if ("box".equals(type)) {
                int cx = w / 2;
                int cy = h / 2 + 1;
                int s = 10;

                int[] xTop = { cx, cx + s, cx, cx - s };
                int[] yTop = { cy - s, cy - s / 2, cy, cy - s / 2 };
                g2.drawPolygon(xTop, yTop, 4);

                g2.drawLine(cx - s, cy - s / 2, cx - s, cy + s / 2);
                g2.drawLine(cx - s, cy + s / 2, cx, cy + s);

                g2.drawLine(cx + s, cy - s / 2, cx + s, cy + s / 2);
                g2.drawLine(cx + s, cy + s / 2, cx, cy + s);

                g2.drawLine(cx, cy, cx, cy + s);
            } else if ("warning".equals(type)) {
                int cx = w / 2;
                int cy = h / 2;
                int s = 11;

                g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                int[] xPoints = { cx, cx - s, cx + s };
                int[] yPoints = { cy - s + 2, cy + s - 1, cy + s - 1 };
                g2.drawPolygon(xPoints, yPoints, 3);

                g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(cx, cy - 3, cx, cy + 2);
                g2.fillOval(cx - 1, cy + 5, 2, 2);
            }
            g2.dispose();
        }
    }

    private class SummaryCard extends JPanel {
        private JLabel lblCount;

        public SummaryCard(String title, Color countColor, Color iconColor, Color iconBgColor, String iconType) {
            this.setLayout(new BorderLayout(10, 10));
            this.setBackground(AppColor.SURFACE);
            this.setOpaque(false);
            this.setBorder(new EmptyBorder(15, 20, 15, 20));

            JPanel leftPanel = new JPanel();
            leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
            leftPanel.setOpaque(false);

            JLabel lblTitle = new JLabel(title);
            lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            lblTitle.setForeground(AppColor.TEXT_SECONDARY);

            lblCount = new JLabel("0");
            lblCount.setFont(new Font("Segoe UI", Font.BOLD, 24));
            lblCount.setForeground(countColor);

            leftPanel.add(lblTitle);
            leftPanel.add(Box.createVerticalStrut(5));
            leftPanel.add(lblCount);

            JPanel rightContainer = new JPanel(new GridBagLayout());
            rightContainer.setOpaque(false);
            rightContainer.add(new IconPanel(12, iconBgColor, iconColor, iconType));

            this.add(leftPanel, BorderLayout.CENTER);
            this.add(rightContainer, BorderLayout.EAST);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Vẽ nền bo góc 15px (nhẹ)
            g2.setColor(AppColor.SURFACE);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 15, 15));

            // Vẽ border bo góc
            g2.setColor(AppColor.BORDER);
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
            g2.dispose();
        }

        public void setCount(int count) {
            lblCount.setText(String.valueOf(count));
        }
    }
}