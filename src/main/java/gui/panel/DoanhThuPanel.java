package gui.panel;

import bus.ThongKeBUS;
import dto.ThongKeDTO;
import util.AppColor;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.List;

public class DoanhThuPanel extends JPanel {
    private ThongKeBUS thongKeBUS = new ThongKeBUS();
    private JPanel chartContainer;
    private JTable table;
    private DefaultTableModel tableModel;
    private JFreeChart currentChart;
    
    private JComboBox<String> cbLoaiThongKe;
    private JComboBox<String> cbPeriod;
    private JCheckBox chkRevenue, chkCost;
    private List<ThongKeDTO.TaiChinh> lastData;

    public DoanhThuPanel() {
        setLayout(new BorderLayout(0, 15));
        setBackground(AppColor.BACKGROUND);
        setBorder(new EmptyBorder(15, 15, 15, 15));
        initComponents();
        
        // CỜ HIỆU: Chỉ load data ngầm, KHÔNG HIỆN POPUP LỖI
        refreshData(false); 
    }

    private void initComponents() {
        JPanel pnlFilter = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 12));
        pnlFilter.setBackground(Color.WHITE);
        pnlFilter.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
                new EmptyBorder(5, 5, 5, 5)
        ));

        // Khởi tạo ComboBox loại thống kê
        cbLoaiThongKe = new JComboBox<>(new String[]{"Theo tháng", "Theo ngày"});
        cbLoaiThongKe.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbLoaiThongKe.setBackground(Color.WHITE);

        // Khởi tạo ComboBox chu kỳ
        cbPeriod = new JComboBox<>();
        cbPeriod.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbPeriod.setBackground(Color.WHITE);
        
        // Sự kiện đổi loại thống kê sẽ cập nhật lại các Option thời gian
        updatePeriodOptions();
        cbLoaiThongKe.addActionListener(e -> updatePeriodOptions());

        chkRevenue = new JCheckBox("Hiển thị Doanh thu", true);
        chkCost = new JCheckBox("Hiển thị Chi phí", true);
        chkRevenue.setBackground(Color.WHITE); chkRevenue.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        chkCost.setBackground(Color.WHITE); chkCost.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        chkRevenue.addActionListener(e -> updateChartOnly());
        chkCost.addActionListener(e -> updateChartOnly());

        JButton btnFilter = new JButton("Thống kê");
        styleButton(btnFilter, AppColor.PRIMARY);
        
        // CỜ HIỆU: Khi người dùng bấm nút, CHO PHÉP HIỆN POPUP
        btnFilter.addActionListener(e -> refreshData(true));

        JButton btnExport = new JButton("Xuất PDF");
        styleButton(btnExport, new Color(220, 38, 38)); 
        btnExport.addActionListener(e -> exportToPDF());

        // Thêm nhãn "Chế độ" và cbLoaiThongKe vào trước
        pnlFilter.add(new JLabel("Chế độ:")); 
        pnlFilter.add(cbLoaiThongKe);

        pnlFilter.add(new JLabel("   Thời gian:")); 
        pnlFilter.add(cbPeriod);

        pnlFilter.add(new JLabel("   Tùy chọn:")); 
        pnlFilter.add(chkRevenue); 
        pnlFilter.add(chkCost);
        
        pnlFilter.add(Box.createRigidArea(new Dimension(10, 0)));
        pnlFilter.add(btnFilter);
        pnlFilter.add(btnExport);

        chartContainer = new JPanel(new BorderLayout());
        chartContainer.setBackground(Color.WHITE);
        chartContainer.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));

        String[] cols = {"Tháng", "Doanh thu (VND)", "Chi phí (VND)", "Lợi nhuận (VND)", "Tỷ lệ Lãi/Vốn"};
        tableModel = new DefaultTableModel(cols, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        table = new JTable(tableModel);
        styleModernTable(table);

        JScrollPane scrollTable = new JScrollPane(table);
        scrollTable.setPreferredSize(new Dimension(800, 200));

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, chartContainer, scrollTable);
        split.setResizeWeight(0.65);
        split.setBorder(null);

        add(pnlFilter, BorderLayout.NORTH);
        add(split, BorderLayout.CENTER);
    }

    // Nhận tham số showNotification để quyết định có báo lỗi hay không
    private void refreshData(boolean showNotification) {
        String type = cbLoaiThongKe.getSelectedIndex() == 0 ? "MONTH" : "DAY";
        int period = 0;
        
        if (type.equals("MONTH")) {
            switch(cbPeriod.getSelectedIndex()) {
                case 0: period = 3; break;
                case 1: period = 6; break;
                case 2: period = 12; break;
                case 3: period = 36; break;
                default: period = 3;
            }
        } else {
            switch(cbPeriod.getSelectedIndex()) {
                case 0: period = 7; break;
                case 1: period = 15; break;
                case 2: period = 30; break;
                default: period = 7;
            }
        }

        lastData = thongKeBUS.getThongKeTaiChinh(type, period);

        tableModel.setRowCount(0);
        DecimalFormat df = new DecimalFormat("#,###");
        DecimalFormat pf = new DecimalFormat("0.0");

        for (ThongKeDTO.TaiChinh d : lastData) {
            double profit = d.doanhThu - d.chiPhi;
            String percentStr = "N/A";
            
            if (d.chiPhi > 0) {
                percentStr = pf.format((profit / d.chiPhi) * 100) + "%";
            } else if (d.doanhThu > 0) {
                percentStr = "100.0%"; 
            }

            tableModel.addRow(new Object[]{
                d.thangNam, df.format(d.doanhThu), df.format(d.chiPhi), 
                df.format(profit), percentStr
            });
        }
        updateChartOnly();
    }

    private void updateChartOnly() {
        if (lastData == null || lastData.isEmpty()) return;
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        for (ThongKeDTO.TaiChinh d : lastData) {
            if (chkRevenue.isSelected()) dataset.addValue(d.doanhThu, "Doanh thu", d.thangNam);
            if (chkCost.isSelected()) dataset.addValue(d.chiPhi, "Chi phí", d.thangNam);
        }

        currentChart = ChartFactory.createLineChart(
                "Biểu đồ Doanh thu & Chi phí", "Thời gian", "Số tiền (Triệu VND)", 
                dataset, PlotOrientation.VERTICAL, true, true, false);

        currentChart.setBackgroundPaint(Color.WHITE);
        currentChart.getTitle().setFont(new Font("Segoe UI", Font.BOLD, 18));
        
        CategoryPlot plot = currentChart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(new Color(220, 220, 220));
        plot.setOutlineVisible(false);
        
        // --- 1. RÚT GỌN TRỤC Y XUỐNG TRIỆU VND BẰNG FORMATTER ---
        org.jfree.chart.axis.NumberAxis rangeAxis = (org.jfree.chart.axis.NumberAxis) plot.getRangeAxis();
        rangeAxis.setNumberFormatOverride(new java.text.DecimalFormat("#,###") {
            @Override
            public StringBuffer format(double number, StringBuffer result, java.text.FieldPosition fieldPosition) {
                // Tự động chia 1 triệu chỉ ở phần hiển thị nhãn trục Y
                return super.format(number / 1000000.0, result, fieldPosition);
            }
        });
        
        LineAndShapeRenderer renderer = new LineAndShapeRenderer();
        for (int i = 0; i < dataset.getRowCount(); i++) {
            Comparable<?> rowKey = dataset.getRowKey(i);
            if (rowKey.equals("Doanh thu")) renderer.setSeriesPaint(i, new Color(40, 167, 69)); 
            else if (rowKey.equals("Chi phí")) renderer.setSeriesPaint(i, new Color(220, 53, 69)); 
            
            renderer.setSeriesStroke(i, new BasicStroke(3.0f)); 
            renderer.setSeriesShapesVisible(i, true); 
        }
        
        // --- 2. GẮN TOOLTIP HIỂN THỊ SỐ ĐẦY ĐỦ KHI RÊ CHUỘT ---
        org.jfree.chart.labels.StandardCategoryToolTipGenerator tooltip = 
            new org.jfree.chart.labels.StandardCategoryToolTipGenerator(
                "{0} - {1}: {2} VNĐ", new java.text.DecimalFormat("#,###")
            );
            
        renderer.setDefaultToolTipGenerator(tooltip);
        
        plot.setRenderer(renderer);
        chartContainer.removeAll();
        chartContainer.add(new ChartPanel(currentChart));
        chartContainer.revalidate();
        chartContainer.repaint();
    }

    private void updatePeriodOptions() {
        cbPeriod.removeAllItems();
        if (cbLoaiThongKe.getSelectedIndex() == 0) { // Đang chọn Tháng
            cbPeriod.addItem("3 tháng gần đây");
            cbPeriod.addItem("6 tháng gần đây");
            cbPeriod.addItem("1 năm gần đây");
            cbPeriod.addItem("3 năm gần đây");
        } else { // Đang chọn Ngày
            cbPeriod.addItem("7 ngày gần đây");
            cbPeriod.addItem("15 ngày gần đây");
            cbPeriod.addItem("30 ngày gần đây");
        }
    }

    private void exportToPDF() {
        if (tableModel.getRowCount() == 0 || currentChart == null) {
            JOptionPane.showMessageDialog(this, "Không có dữ liệu để xuất!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(fileChooser.getSelectedFile().getAbsolutePath() + ".pdf"));
                document.open();

                BaseFont bf = BaseFont.createFont("c:/windows/fonts/arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                com.itextpdf.text.Font fBold = new com.itextpdf.text.Font(bf, 14, com.itextpdf.text.Font.BOLD);
                com.itextpdf.text.Font fNormal = new com.itextpdf.text.Font(bf, 12, com.itextpdf.text.Font.NORMAL);

                Paragraph pTitle = new Paragraph("BÁO CÁO DOANH THU & CHI PHÍ", fBold);
                pTitle.setAlignment(Element.ALIGN_CENTER);
                document.add(pTitle);

                Paragraph pSub = new Paragraph("Giai đoạn: " + cbPeriod.getSelectedItem().toString(), fNormal);
                pSub.setAlignment(Element.ALIGN_CENTER);
                pSub.setSpacingAfter(20f);
                document.add(pSub);

                java.awt.image.BufferedImage img = currentChart.createBufferedImage(600, 350);
                com.itextpdf.text.Image pdfImg = com.itextpdf.text.Image.getInstance(img, null);
                pdfImg.setAlignment(Element.ALIGN_CENTER);
                document.add(pdfImg);

                PdfPTable pdfTable = new PdfPTable(tableModel.getColumnCount());
                pdfTable.setSpacingBefore(20f);
                pdfTable.setWidthPercentage(100);
                pdfTable.setWidths(new float[]{1.5f, 2.5f, 2.5f, 2.5f, 1.5f});
                
                for (int i = 0; i < tableModel.getColumnCount(); i++) {
                    PdfPCell cell = new PdfPCell(new Phrase(tableModel.getColumnName(i), fBold));
                    cell.setBackgroundColor(new com.itextpdf.text.BaseColor(AppColor.PRIMARY.getRGB()));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setPadding(8f);
                    pdfTable.addCell(cell);
                }

                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    for (int j = 0; j < tableModel.getColumnCount(); j++) {
                        PdfPCell cell = new PdfPCell(new Phrase(tableModel.getValueAt(i, j).toString(), fNormal));
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setPadding(6f);
                        pdfTable.addCell(cell);
                    }
                }
                document.add(pdfTable);
                document.close();
                
                JOptionPane.showMessageDialog(this, "Xuất PDF thành công!");
                Desktop.getDesktop().open(new File(fileChooser.getSelectedFile().getAbsolutePath() + ".pdf"));
            } catch (Exception ex) { ex.printStackTrace(); }
        }
    }

    private void styleModernTable(JTable tb) {
        tb.setRowHeight(35);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setBackground(AppColor.PRIMARY);
        headerRenderer.setForeground(Color.WHITE);
        headerRenderer.setHorizontalAlignment(JLabel.CENTER);
        headerRenderer.setFont(new Font("Segoe UI", Font.BOLD, 14));

        for (int i = 0; i < tb.getColumnCount(); i++) {
            tb.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
            tb.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    private void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(110, 32));
    }
}