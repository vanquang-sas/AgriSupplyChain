/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gui.panel;

import bus.TonKhoBUS;
import com.formdev.flatlaf.FlatClientProperties;
import util.AppColor; 

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TonKhoPanel extends JPanel {
    
    private JTable table;
    private DefaultTableModel tableModel;
    private TonKhoBUS tonKhoBUS = new TonKhoBUS();
    private JTextField txtSearch;
    private JComboBox<String> cbSort;

    // ================== CÁC BIẾN CHO PHÂN TRANG ==================
    private List<Object[]> originalData = new ArrayList<>(); // Toàn bộ dữ liệu
    private List<Object[]> currentData = new ArrayList<>();  // Dữ liệu sau khi lọc/sắp xếp
    private int currentPage = 1;
    private final int rowsPerPage = 8;
    private int totalPages = 1;
    private JPanel paginationPanel; 
    // =============================================================

    public TonKhoPanel() {
        initComponents();
        loadDataToTable(false); // false = Load từ đầu, đưa về trang 1
    }

    private void initComponents() {
        this.setLayout(new BorderLayout(15, 15));
        this.setBackground(AppColor.BACKGROUND); 
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // ================= TOP PANEL =================
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(AppColor.BACKGROUND);

        txtSearch = new JTextField();
        txtSearch.setPreferredSize(new Dimension(350, 38));
        txtSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Tìm kiếm sản phẩm kho...");
        txtSearch.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true); 
        txtSearch.putClientProperty(FlatClientProperties.STYLE, "arc: 15");

        try {
            java.net.URL searchUrl = getClass().getResource("/icons/search.png");
            if(searchUrl != null) {
                ImageIcon searchIcon = new ImageIcon(searchUrl);
                Image imgSearch = searchIcon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
                txtSearch.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, new ImageIcon(imgSearch));
            }
        } catch (Exception e) {
            System.out.println("Lỗi load icon search");
        }
        
        String[] sortOptions = {"Sắp xếp: Mới nhất", "Sắp xếp: Số lượng tăng dần", "Sắp xếp: Số lượng giảm dần"};
        cbSort = new JComboBox<>(sortOptions);
        cbSort.setPreferredSize(new Dimension(200, 38));
        cbSort.putClientProperty(FlatClientProperties.STYLE, "arc: 15");

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchPanel.setBackground(AppColor.BACKGROUND);
        searchPanel.add(txtSearch);
        searchPanel.add(cbSort);

        // ====================== CODE THÊM NÚT LÀM MỚI ======================
        JButton btnRefresh = new JButton("Làm mới");
        btnRefresh.setPreferredSize(new Dimension(100, 38));
        btnRefresh.putClientProperty(FlatClientProperties.STYLE, "arc: 15");
        btnRefresh.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnRefresh.addActionListener(e -> {
            txtSearch.setText(""); 
            cbSort.setSelectedIndex(0); 
            loadDataToTable(false); // Tải lại toàn bộ và về trang 1
        });

        searchPanel.add(btnRefresh); 
        topPanel.add(searchPanel, BorderLayout.EAST);
        this.add(topPanel, BorderLayout.NORTH);

        // ================= TABLE ===============================================================
        String[] columnNames = {
            "Mã SKU", "Tên Sản Phẩm", "Hình Ảnh", "Loại", "Nhà Cung Cấp", 
            "Số Lượng", "Đơn Vị Tính", "Vị Trí", "Trạng Thái", "Hành Động"
        };
        
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 9; // Chỉ cho phép click cột Hành Động
            }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 5) return Double.class; 
                return Object.class; 
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(55); 
        table.setSelectionBackground(new Color(232, 240, 254));
        table.setSelectionForeground(AppColor.TEXT_PRIMARY);
        table.setShowVerticalLines(false); 
        table.setShowHorizontalLines(true);
        table.setGridColor(AppColor.BORDER); 
        table.setBackground(AppColor.SURFACE);
        table.getTableHeader().setReorderingAllowed(false);
        
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 13));
        header.setBackground(AppColor.SURFACE);
        header.setForeground(AppColor.TEXT_PRIMARY);
        header.setPreferredSize(new Dimension(0, 45));
        header.putClientProperty(FlatClientProperties.STYLE, "separatorColor: #E5E7EB; bottomSeparatorColor: #D1D5DB;");

        table.getColumnModel().getColumn(0).setPreferredWidth(90);
        table.getColumnModel().getColumn(1).setPreferredWidth(160);
        table.getColumnModel().getColumn(2).setPreferredWidth(80);
        table.getColumnModel().getColumn(8).setPreferredWidth(100);
        table.getColumnModel().getColumn(9).setPreferredWidth(120);

        table.getColumnModel().getColumn(2).setCellRenderer(new ImageRenderer());
        table.getColumnModel().getColumn(8).setCellRenderer(new StatusRenderer());
        table.getColumnModel().getColumn(9).setCellRenderer(new ActionRenderer());
        table.getColumnModel().getColumn(9).setCellEditor(new ActionEditor(new JCheckBox(), table));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(AppColor.SURFACE);
        scrollPane.putClientProperty(FlatClientProperties.STYLE, "arc: 15"); 
        scrollPane.setBorder(BorderFactory.createLineBorder(AppColor.BORDER, 1, true));

        this.add(scrollPane, BorderLayout.CENTER);

        // ================= BOTTOM PANEL (CHỨA PHÂN TRANG) =================
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(AppColor.BACKGROUND);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        paginationPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        paginationPanel.setBackground(AppColor.BACKGROUND);
        bottomPanel.add(paginationPanel, BorderLayout.EAST);
        
        this.add(bottomPanel, BorderLayout.SOUTH);
        
        // ================= SỰ KIỆN TÌM KIẾM VÀ SẮP XẾP MỚI =================
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                applyFilterAndSort(false); // Gõ phím là tìm và đưa về trang 1
            }
        });

        cbSort.addActionListener(e -> applyFilterAndSort(false));
    }

    // ================= LOGIC LOAD DỮ LIỆU & PHÂN TRANG =================
    private void loadDataToTable(boolean keepCurrentPage) {
        ArrayList<Object[]> list = tonKhoBUS.getDanhSachTonKho();
        if (list != null) {
            originalData = list;
        } else {
            originalData = new ArrayList<>();
        }
        applyFilterAndSort(keepCurrentPage);
    }

    private void applyFilterAndSort(boolean keepCurrentPage) {
        String keyword = txtSearch.getText().trim().toLowerCase();
        currentData = new ArrayList<>();

        // 1. Lọc theo chữ tìm kiếm
        for (Object[] row : originalData) {
            boolean match = false;
            for (Object cell : row) {
                if (cell != null && cell.toString().toLowerCase().contains(keyword)) {
                    match = true; break;
                }
            }
            if (match) currentData.add(row);
        }

        // 2. Sắp xếp
        String sortOpt = cbSort.getSelectedItem().toString();
        currentData.sort((row1, row2) -> {
            if (sortOpt.contains("Số lượng giảm dần")) {
                Double s1 = row1[5] != null ? (Double) row1[5] : 0.0;
                Double s2 = row2[5] != null ? (Double) row2[5] : 0.0;
                return s2.compareTo(s1);
            } else if (sortOpt.contains("Số lượng tăng dần")) {
                Double s1 = row1[5] != null ? (Double) row1[5] : 0.0;
                Double s2 = row2[5] != null ? (Double) row2[5] : 0.0;
                return s1.compareTo(s2);
            } else { // Mới nhất (SKU giảm dần)
                String m1 = row1[0] != null ? row1[0].toString() : "";
                String m2 = row2[0] != null ? row2[0].toString() : "";
                return m2.compareTo(m1);
            }
        });

        // 3. Xử lý số trang
        totalPages = (int) Math.ceil((double) currentData.size() / rowsPerPage);
        if (totalPages == 0) totalPages = 1;
        
        if (!keepCurrentPage) {
            currentPage = 1;
        } else {
            if (currentPage > totalPages) currentPage = totalPages;
        }

        renderTablePage();
        renderPaginationButtons();
    }

    private void renderTablePage() {
        tableModel.setRowCount(0);
        int start = (currentPage - 1) * rowsPerPage;
        int end = Math.min(start + rowsPerPage, currentData.size());

        for (int i = start; i < end; i++) {
            Object[] row = currentData.get(i);
            double slConLai = row[5] != null ? (double) row[5] : 0; 
            String trangThai = slConLai >= 50 ? "Còn hàng" : (slConLai > 0 ? "Sắp hết" : "Hết hàng");
            
            tableModel.addRow(new Object[]{
                row[0], row[1], row[2], row[3], row[4], row[5], row[6], row[7], trangThai, ""
            });
        }
    }

    private void renderPaginationButtons() {
        paginationPanel.removeAll(); 

        JButton btnPrev = new JButton("Trang trước");
        btnPrev.setEnabled(currentPage > 1);
        btnPrev.putClientProperty(FlatClientProperties.STYLE, "arc: 10");
        btnPrev.addActionListener(e -> { currentPage--; renderTablePage(); renderPaginationButtons(); });
        paginationPanel.add(btnPrev);

        // Hiển thị tối đa 3 nút trang ở giữa
        int maxVisible = 3;
        int startPage = Math.max(1, currentPage - 1);
        int endPage = Math.min(totalPages, startPage + maxVisible - 1);
        
        if (endPage - startPage < maxVisible - 1) {
            startPage = Math.max(1, endPage - maxVisible + 1);
        }

        for (int i = startPage; i <= endPage; i++) {
            int pageNum = i;
            JButton btnPage = new JButton(String.valueOf(pageNum));
            btnPage.putClientProperty(FlatClientProperties.STYLE, "arc: 10");
            
            if (pageNum == currentPage) {
                btnPage.setBackground(AppColor.PRIMARY);
                btnPage.setForeground(Color.WHITE);
            }
            
            btnPage.addActionListener(e -> { currentPage = pageNum; renderTablePage(); renderPaginationButtons(); });
            paginationPanel.add(btnPage);
        }

        if (endPage < totalPages) {
            JButton btnDots = new JButton("...");
            btnDots.putClientProperty(FlatClientProperties.STYLE, "arc: 10");
            btnDots.addActionListener(e -> { currentPage = endPage + 1; renderTablePage(); renderPaginationButtons(); });
            paginationPanel.add(btnDots);
        }

        JButton btnNext = new JButton("Trang sau");
        btnNext.setEnabled(currentPage < totalPages);
        btnNext.putClientProperty(FlatClientProperties.STYLE, "arc: 10");
        btnNext.addActionListener(e -> { currentPage++; renderTablePage(); renderPaginationButtons(); });
        paginationPanel.add(btnNext);

        paginationPanel.revalidate();
        paginationPanel.repaint();
    }


    // ================= RENDERER HÌNH ẢNH =================
    class ImageRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = new JLabel();
            label.setHorizontalAlignment(SwingConstants.CENTER);
            if (value != null && !value.toString().trim().isEmpty()) {
                try {
                    String imagePath = "/images/" + value.toString() + ".jpg";
                    java.net.URL imgUrl = getClass().getResource(imagePath);
                    if(imgUrl != null) {
                        ImageIcon icon = new ImageIcon(imgUrl);
                        Image img = icon.getImage().getScaledInstance(45, 35, Image.SCALE_SMOOTH);
                        label.setIcon(new ImageIcon(img));
                    } else {
                        label.setText("No Image");
                        label.setFont(new Font("SansSerif", Font.ITALIC, 11));
                    }
                } catch (Exception e) { label.setText("Error"); }
            }
            label.setOpaque(true);
            label.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            // THÊM DÒNG NÀY ĐỂ VẼ LẠI ĐƯỜNG KẺ DƯỚI:
            label.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, table.getGridColor()));
            return label;
        }
    }

    // ================= RENDERER TRẠNG THÁI =================
    class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = new JLabel(value != null ? value.toString() : "", SwingConstants.CENTER);
            label.setOpaque(true);
            label.setFont(new Font("SansSerif", Font.BOLD, 12));
            
            String status = label.getText();
            if (status.equals("Còn hàng")) { 
                label.setBackground(new Color(209, 250, 229)); 
                label.setForeground(AppColor.SUCCESS); 
            } else if (status.equals("Sắp hết")) { 
                label.setBackground(new Color(254, 243, 199)); 
                label.setForeground(AppColor.WARNING); 
            } else { 
                label.setBackground(new Color(243, 244, 246)); 
                label.setForeground(AppColor.TEXT_SECONDARY); 
            }
            label.putClientProperty(FlatClientProperties.STYLE, "arc: 10");
            JPanel panel = new JPanel(new GridBagLayout());
            panel.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            label.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
            // THÊM DÒNG NÀY CHO PANEL BÊN NGOÀI:
            panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, table.getGridColor()));
            panel.add(label);
            return panel;
        }
    }

    // ================= RENDERER & EDITOR HÀNH ĐỘNG =================
    class ActionPanel extends JPanel {
        JButton btnEdit = new JButton();
        JButton btnDelete = new JButton();
        
        public ActionPanel() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 12));
            setOpaque(true);
            setupButton(btnEdit, "/icons/edit.png", "Sửa");
            setupButton(btnDelete, "/icons/delete.png", "Xóa");
            add(btnEdit);
            add(btnDelete);
        }

        private void setupButton(JButton btn, String iconPath, String fallbackText) {
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.setPreferredSize(new Dimension(28, 28));
            btn.putClientProperty(FlatClientProperties.STYLE, "buttonType: toolBarButton; arc: 10");
            try {
                java.net.URL url = getClass().getResource(iconPath);
                if (url != null) {
                    ImageIcon icon = new ImageIcon(url);
                    Image img = icon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
                    btn.setIcon(new ImageIcon(img));
                } else {
                    btn.setText(fallbackText.substring(0, 1));
                    btn.setFont(new Font("SansSerif", Font.BOLD, 10));
                }
            } catch (Exception e) { btn.setText(fallbackText.substring(0, 1)); }
        }
    }

    class ActionRenderer extends ActionPanel implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            // THÊM DÒNG NÀY ĐỂ KẺ ĐƯỜNG LINE BÊN DƯỚI:
            setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, table.getGridColor()));
            return this;
        }
    }

    class ActionEditor extends DefaultCellEditor {
        private ActionPanel panel = new ActionPanel();
        private int currentRow = -1; 
        
        public ActionEditor(JCheckBox checkBox, JTable table) {
            super(checkBox);
            
            // SỰ KIỆN NÚT SỬA
            panel.btnEdit.addActionListener(e -> {
                int viewRow = currentRow; 
                if (viewRow == -1) return;
                
                int modelRow = table.convertRowIndexToModel(viewRow);
                String maSKU = table.getModel().getValueAt(modelRow, 0).toString();
                double slHienTai = (double) table.getModel().getValueAt(modelRow, 5);

                fireEditingStopped(); 

                String input = JOptionPane.showInputDialog(null, "Nhập số lượng mới cho mã " + maSKU + ":", slHienTai);
                
                if (input != null && !input.trim().isEmpty()) {
                    try {
                        double slMoi = Double.parseDouble(input);
                        if(slMoi < 0) throw new NumberFormatException(); 
                        
                        boolean isSuccess = tonKhoBUS.capNhatSoLuong(maSKU, slMoi);
                        if (isSuccess) {
                            JOptionPane.showMessageDialog(null, "Đã cập nhật số lượng thành công!");
                            loadDataToTable(true); // Load lại bảng và giữ nguyên trang đang đứng
                        } else {
                            JOptionPane.showMessageDialog(null, "Lỗi cập nhật CSDL!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "Số lượng không hợp lệ! Vui lòng nhập số >= 0.", "Lỗi", JOptionPane.WARNING_MESSAGE);
                    }
                }
            });
            
            // SỰ KIỆN NÚT XÓA
            panel.btnDelete.addActionListener(e -> {
                int viewRow = currentRow;
                if (viewRow == -1) return;
                
                int modelRow = table.convertRowIndexToModel(viewRow);
                String maSKU = table.getModel().getValueAt(modelRow, 0).toString();
                
                fireEditingStopped(); 

                int confirm = JOptionPane.showConfirmDialog(null, 
                    "Bạn có chắc chắn muốn xóa dữ liệu tồn kho của " + maSKU + "?", 
                    "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                    
                if (confirm == JOptionPane.YES_OPTION) {
                    boolean isSuccess = tonKhoBUS.xoaTonKho(maSKU);
                    if (isSuccess) {
                        JOptionPane.showMessageDialog(null, "Đã xóa thành công!");
                        loadDataToTable(true); // Load lại bảng và giữ nguyên trang đang đứng
                    } else {
                        JOptionPane.showMessageDialog(null, "Xóa thất bại! Có thể mã này đang bị ràng buộc dữ liệu.", "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.currentRow = row; 
            panel.setBackground(table.getSelectionBackground());
            
            // THÊM DÒNG NÀY (để khi click chuột vào nó không bị mất viền):
            panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, table.getGridColor()));
            return panel;
        }
    }
}