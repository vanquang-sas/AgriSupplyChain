package gui.panel;

import bus.ThongKeBUS;
import dto.ThongKeDTO;
import util.AppColor;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TopSanPhamPanel extends JPanel {
    private ThongKeBUS thongKeBUS = new ThongKeBUS();
    
    // Components Giao diện
    private JPanel chartContainer;
    private JTable table;
    private DefaultTableModel tableModel;
    
    // Components bộ lọc
    private JComboBox<String> cbLimit;
    private JComboBox<String> cbType;
    private JSpinner spinFromDate;
    private JSpinner spinToDate;

    public TopSanPhamPanel() {
        setLayout(new BorderLayout(0, 15));
        setBackground(AppColor.BACKGROUND);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        initComponents();
        refreshData(); // Load mặc định lần đầu
    }

    private void initComponents() {
        // ==========================================
        // 1. KHU VỰC BỘ LỌC (NORTH) - FLAT DESIGN
        // ==========================================
        JPanel pnlFilter = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 12));
        pnlFilter.setBackground(Color.WHITE);
        pnlFilter.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
                new EmptyBorder(5, 5, 5, 5)
        ));

        // Styling chung cho font chữ
        Font fontLabel = new Font("Segoe UI", Font.BOLD, 13);
        Font fontInput = new Font("Segoe UI", Font.PLAIN, 13);

        JLabel lblLimit = new JLabel("Hiển thị:"); lblLimit.setFont(fontLabel);
        cbLimit = new JComboBox<>(new String[]{"Top 10", "Top 5", "Tất cả"});
        cbLimit.setFont(fontInput);
        cbLimit.setBackground(Color.WHITE);

        JLabel lblType = new JLabel("Tiêu chí:"); lblType.setFont(fontLabel);
        cbType = new JComboBox<>(new String[]{"Bán chạy nhất", "Bán ít nhất"});
        cbType.setFont(fontInput);
        cbType.setBackground(Color.WHITE);
        
        JLabel lblFrom = new JLabel("Từ ngày:"); lblFrom.setFont(fontLabel);
        spinFromDate = createDateSpinner(true); 
        
        JLabel lblTo = new JLabel("Đến ngày:"); lblTo.setFont(fontLabel);
        spinToDate = createDateSpinner(false); 

        // Nút Thống kê Flat Design
        JButton btnFilter = new JButton("Thống kê");
        btnFilter.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnFilter.setBackground(AppColor.PRIMARY);
        btnFilter.setForeground(Color.WHITE);
        btnFilter.setFocusPainted(false);
        btnFilter.setBorderPainted(false);
        btnFilter.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnFilter.setPreferredSize(new Dimension(100, 32));
        
        // Hiệu ứng Hover cho nút
        btnFilter.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnFilter.setBackground(AppColor.PRIMARY_HOVER); // Tùy chỉnh màu hover nếu có trong AppColor
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnFilter.setBackground(AppColor.PRIMARY);
            }
        });

        // Sự kiện click
        btnFilter.addActionListener(e -> refreshData());

        pnlFilter.add(lblLimit); pnlFilter.add(cbLimit);
        pnlFilter.add(lblType);  pnlFilter.add(cbType);
        pnlFilter.add(lblFrom);  pnlFilter.add(spinFromDate);
        pnlFilter.add(lblTo);    pnlFilter.add(spinToDate);
        pnlFilter.add(btnFilter);

        // ==========================================
        // 2. KHU VỰC TRUNG TÂM (CENTER) CHỨA CHART VÀ TABLE
        // ==========================================
        JPanel pnlCenter = new JPanel(new BorderLayout(0, 15));
        pnlCenter.setBackground(AppColor.BACKGROUND);

        // -- BIỂU ĐỒ --
        chartContainer = new JPanel(new BorderLayout());
        chartContainer.setBackground(Color.WHITE);
        chartContainer.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true));
        chartContainer.setPreferredSize(new Dimension(800, 350));

        // -- BẢNG DỮ LIỆU --
        String[] columnNames = {"STT", "Tên Sản Phẩm", "Phân Loại", "Số Lượng (Đã Bán)"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; } // Không cho edit trực tiếp trên bảng
        };
        table = new JTable(tableModel);
        styleModernTable(table);

        JScrollPane scrollTable = new JScrollPane(table);
        scrollTable.getViewport().setBackground(Color.WHITE);
        scrollTable.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true));
        scrollTable.setPreferredSize(new Dimension(800, 200));

        // Add Chart ở trên, Table ở dưới bằng JSplitPane hoặc BoderLayout
        // Ở đây dùng BorderLayout (Chart ở Center, Table ở South) để chia tỷ lệ đẹp
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, chartContainer, scrollTable);
        splitPane.setResizeWeight(0.65); // Chart chiếm 65% không gian
        splitPane.setDividerSize(5);
        splitPane.setBorder(null);

        pnlCenter.add(splitPane, BorderLayout.CENTER);

        // Add tất cả vào Panel chính
        add(pnlFilter, BorderLayout.NORTH);
        add(pnlCenter, BorderLayout.CENTER);
    }

    // --- XỬ LÝ ĐỒNG BỘ DỮ LIỆU (CHART & TABLE) ---
    private void refreshData() {
        // 1. Lấy dữ liệu từ bộ lọc
        int limit = 10;
        if (cbLimit.getSelectedIndex() == 1) limit = 5;
        else if (cbLimit.getSelectedIndex() == 2) limit = 0; // Tất cả

        String type = cbType.getSelectedIndex() == 0 ? "BEST" : "WORST";
        String displayType = cbType.getSelectedIndex() == 0 ? "Bán chạy" : "Bán chậm";
        Date from = (Date) spinFromDate.getValue();
        Date to = (Date) spinToDate.getValue();

        // 2. Lấy Data từ Database
        List<ThongKeDTO.TopSanPham> data = thongKeBUS.getThongKeSanPham(limit, type, from, to);

        // 3. Cập nhật Biểu đồ
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (ThongKeDTO.TopSanPham sp : data) {
            dataset.addValue(sp.soLuongBan, "Số lượng", sp.tenSP);
        }
        updateChartUI(dataset, displayType);

        // 4. Cập nhật Bảng (Table)
        tableModel.setRowCount(0); // Xóa data cũ
        int stt = 1;
        for (ThongKeDTO.TopSanPham sp : data) {
            tableModel.addRow(new Object[]{
                stt++, 
                sp.tenSP, 
                displayType, 
                sp.soLuongBan
            });
        }
    }

    // --- TÙY CHỈNH BIỂU ĐỒ HIỆN ĐẠI (GIỐNG D3.JS) ---
    private void updateChartUI(DefaultCategoryDataset dataset, String typeName) {
        JFreeChart chart = ChartFactory.createBarChart(
                "Thống kê Top Sản phẩm (" + typeName + ")", 
                "Tên Sản phẩm", 
                "Số lượng", 
                dataset, PlotOrientation.VERTICAL, false, true, false);

        // Flat Design cho Chart
        chart.setBackgroundPaint(Color.WHITE);
        chart.getTitle().setFont(new Font("Segoe UI", Font.BOLD, 18));
        
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);       // Nền trong trắng
        plot.setRangeGridlinePaint(new Color(220, 220, 220)); // Màu lưới xám nhạt
        plot.setOutlineVisible(false);              // Bỏ viền plot

        // Tùy chỉnh cột (Bar)
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, AppColor.PRIMARY); // Dùng màu chủ đạo của App
        renderer.setDrawBarOutline(false);
        renderer.setItemMargin(0.1); // Khoảng cách giữa các cột
        // renderer.setMaximumBarWidth(0.15); // Chiều rộng tối đa của cột (giúp nó không bị quá to nếu ít SP)

        // Custom Font cho Trục (Axis)
        plot.getDomainAxis().setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 12));
        plot.getRangeAxis().setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 12));
        plot.getDomainAxis().setLabelFont(new Font("Segoe UI", Font.BOLD, 13));
        plot.getRangeAxis().setLabelFont(new Font("Segoe UI", Font.BOLD, 13));

        // Cập nhật lên UI
        chartContainer.removeAll();
        ChartPanel cp = new ChartPanel(chart);
        cp.setMouseWheelEnabled(true); // Cho phép zoom
        chartContainer.add(cp, BorderLayout.CENTER);
        chartContainer.revalidate();
        chartContainer.repaint();
    }

    // --- TÙY CHỈNH JTABLE HIỆN ĐẠI ---
    private void styleModernTable(JTable tb) {
        tb.setRowHeight(35);
        tb.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tb.setGridColor(new Color(240, 240, 240));
        tb.setSelectionBackground(new Color(220, 235, 255)); // Màu xanh nhạt khi chọn dòng
        tb.setSelectionForeground(Color.BLACK);
        tb.setShowVerticalLines(false); // Ẩn viền dọc để trông thoáng hơn

        JTableHeader header = tb.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(AppColor.PRIMARY);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(100, 40));
        
        // Căn giữa cột STT và Số lượng
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tb.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // STT
        tb.getColumnModel().getColumn(3).setCellRenderer(centerRenderer); // Số Lượng
        
        // Căn độ rộng cột
        tb.getColumnModel().getColumn(0).setPreferredWidth(50);
        tb.getColumnModel().getColumn(0).setMaxWidth(50);
        tb.getColumnModel().getColumn(2).setPreferredWidth(150);
        tb.getColumnModel().getColumn(3).setPreferredWidth(150);
    }

    // --- TIỆN ÍCH TẠO BỘ CHỌN NGÀY ---
    private JSpinner createDateSpinner(boolean isFromDate) {
        Calendar cal = Calendar.getInstance();
        if (isFromDate) cal.add(Calendar.MONTH, -1); // Mặc định Từ: 1 tháng trước, Đến: Hôm nay
        
        SpinnerDateModel model = new SpinnerDateModel(cal.getTime(), null, null, Calendar.DAY_OF_MONTH);
        JSpinner spinner = new JSpinner(model);
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "dd/MM/yyyy");
        editor.getTextField().setFont(new Font("Segoe UI", Font.PLAIN, 13));
        spinner.setEditor(editor);
        spinner.setPreferredSize(new Dimension(120, 30));
        return spinner;
    }
}