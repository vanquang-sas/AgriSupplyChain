package gui.panel.BaoCaoThongKe;

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
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TopSanPhamPanel extends JPanel {
    private ThongKeBUS thongKeBUS = new ThongKeBUS();
    private JPanel chartContainer;
    private JTable table;
    private DefaultTableModel tableModel;
    private JFreeChart currentChart;
    
    private JComboBox<String> cbLimit;
    private JComboBox<String> cbType;
    private JSpinner spinFromDate;
    private JSpinner spinToDate;

    private final Color[] CHART_PALETTE = {
        new Color(79, 129, 189), new Color(155, 187, 89), 
        new Color(128, 100, 162), new Color(75, 172, 198), 
        new Color(247, 150, 70),  new Color(192, 80, 77),
        new Color(146, 208, 80),  new Color(0, 176, 240),
        new Color(255, 192, 0),   new Color(112, 48, 160)
    };

    public TopSanPhamPanel() {
        setLayout(new BorderLayout(0, 15));
        setBackground(AppColor.BACKGROUND);
        setBorder(new EmptyBorder(15, 15, 15, 15));
        initComponents();
        refreshData(); 
    }

    private void initComponents() {
        JPanel pnlFilter = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 12));
        pnlFilter.setBackground(Color.WHITE);
        pnlFilter.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
                new EmptyBorder(5, 5, 5, 5)
        ));

        cbLimit = new JComboBox<>(new String[]{"Top 10", "Top 5", "Tất cả"});
        cbType = new JComboBox<>(new String[]{"Bán chạy nhất", "Bán ít nhất"});
        spinFromDate = createDateSpinner(true); 
        spinToDate = createDateSpinner(false); 

        JButton btnFilter = new JButton("Thống kê");
        styleButton(btnFilter, AppColor.PRIMARY);
        btnFilter.addActionListener(e -> refreshData());

        JButton btnExport = new JButton("Xuất PDF");
        styleButton(btnExport, new Color(220, 38, 38));
        btnExport.addActionListener(e -> exportToPDF());

        pnlFilter.add(new JLabel("Hiển thị:")); pnlFilter.add(cbLimit);
        pnlFilter.add(new JLabel("Tiêu chí:")); pnlFilter.add(cbType);
        pnlFilter.add(new JLabel("Từ:")); pnlFilter.add(spinFromDate);
        pnlFilter.add(new JLabel("Đến:")); pnlFilter.add(spinToDate);
        pnlFilter.add(btnFilter);
        pnlFilter.add(btnExport);

        chartContainer = new JPanel(new BorderLayout());
        chartContainer.setBackground(Color.WHITE);
        chartContainer.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true));

        String[] columnNames = {"STT", "Tên Sản Phẩm", "Số Lượng (Đã Bán)"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; } 
        };
        table = new JTable(tableModel);
        styleModernTable(table);

        JScrollPane scrollTable = new JScrollPane(table);
        scrollTable.setPreferredSize(new Dimension(800, 200));

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, chartContainer, scrollTable);
        splitPane.setResizeWeight(0.6);
        splitPane.setBorder(null);

        add(pnlFilter, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
    }

    private void refreshData() {
        int limit = cbLimit.getSelectedIndex() == 0 ? 10 : (cbLimit.getSelectedIndex() == 1 ? 5 : 0);
        String type = cbType.getSelectedIndex() == 0 ? "BEST" : "WORST";
        Date from = (Date) spinFromDate.getValue();
        Date to = (Date) spinToDate.getValue();

        List<ThongKeDTO.TopSanPham> data = thongKeBUS.getThongKeSanPham(limit, type, from, to);

        // KIỂM TRA DỮ LIỆU RỖNG THÔNG MINH (Tổng bán = 0)
        long totalSales = 0;
        if (data != null) {
            for (ThongKeDTO.TopSanPham sp : data) {
                totalSales += sp.soLuongBan;
            }
        }

        if (data == null || data.isEmpty() || totalSales == 0) {
            chartContainer.removeAll();
            chartContainer.repaint();
            tableModel.setRowCount(0);
            currentChart = null; // Reset chart để không xuất PDF rỗng
            JOptionPane.showMessageDialog(this, 
                "Không có sản phẩm nào được bán ra trong khoảng thời gian được chọn!", 
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        tableModel.setRowCount(0);
        int stt = 1;
        for (ThongKeDTO.TopSanPham sp : data) {
            dataset.addValue(sp.soLuongBan, "Số lượng", sp.tenSP);
            tableModel.addRow(new Object[]{stt++, sp.tenSP, sp.soLuongBan});
        }
        updateChartUI(dataset, cbType.getSelectedItem().toString());
    }

    private void updateChartUI(DefaultCategoryDataset dataset, String typeName) {
        currentChart = ChartFactory.createBarChart(
                "Thống kê " + typeName, "Sản phẩm", "Số lượng", 
                dataset, PlotOrientation.VERTICAL, false, true, false);

        CategoryPlot plot = currentChart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);

        // --- XOAY XIÊN 45 ĐỘ CHO DỄ ĐỌC ---
        org.jfree.chart.axis.CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(org.jfree.chart.axis.CategoryLabelPositions.UP_45);
        // ----------------------------------
        
        BarRenderer renderer = new BarRenderer() {
            @Override
            public Paint getItemPaint(int row, int column) {
                return CHART_PALETTE[column % CHART_PALETTE.length];
            }
        };
        renderer.setShadowVisible(false);
        renderer.setBarPainter(new org.jfree.chart.renderer.category.StandardBarPainter());
        renderer.setItemMargin(0.1);
        plot.setRenderer(renderer);

        chartContainer.removeAll();
        chartContainer.add(new ChartPanel(currentChart), BorderLayout.CENTER);
        chartContainer.revalidate();
    }

    private void exportToPDF() {
        if (tableModel.getRowCount() == 0 || currentChart == null) {
            JOptionPane.showMessageDialog(this, "Không có dữ liệu để xuất báo cáo!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
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

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                String tieuChi = cbType.getSelectedItem().toString().toLowerCase();
                String gioiHan = cbLimit.getSelectedItem().toString().toLowerCase();
                String tuNgay = sdf.format((Date) spinFromDate.getValue());
                String denNgay = sdf.format((Date) spinToDate.getValue());
                
                String reportTitle = String.format("THỐNG KÊ %s SẢN PHẨM %s", gioiHan.toUpperCase(), tieuChi.toUpperCase());
                String reportSub = String.format("Giai đoạn: Từ ngày %s đến ngày %s", tuNgay, denNgay);

                Paragraph pTitle = new Paragraph(reportTitle, fBold);
                pTitle.setAlignment(Element.ALIGN_CENTER);
                document.add(pTitle);

                Paragraph pSub = new Paragraph(reportSub, fNormal);
                pSub.setAlignment(Element.ALIGN_CENTER);
                pSub.setSpacingAfter(20f);
                document.add(pSub);

                java.awt.image.BufferedImage img = currentChart.createBufferedImage(500, 300);
                com.itextpdf.text.Image pdfImg = com.itextpdf.text.Image.getInstance(img, null);
                pdfImg.setAlignment(Element.ALIGN_CENTER);
                document.add(pdfImg);

                PdfPTable pdfTable = new PdfPTable(3);
                pdfTable.setSpacingBefore(20f);
                pdfTable.setWidthPercentage(100);
                
                String[] headers = {"STT", "Tên sản phẩm", "Số lượng bán"};
                for (String h : headers) {
                    PdfPCell cell = new PdfPCell(new Phrase(h, fBold));
                    cell.setBackgroundColor(new com.itextpdf.text.BaseColor(AppColor.PRIMARY.getRGB()));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setPadding(8f);
                    pdfTable.addCell(cell);
                }

                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    for (int j = 0; j < 3; j++) {
                        PdfPCell cell = new PdfPCell(new Phrase(tableModel.getValueAt(i, j).toString(), fNormal));
                        cell.setHorizontalAlignment(j == 1 ? Element.ALIGN_LEFT : Element.ALIGN_CENTER);
                        cell.setPadding(5f);
                        pdfTable.addCell(cell);
                    }
                }
                document.add(pdfTable);
                document.close();
                
                JOptionPane.showMessageDialog(this, "Xuất PDF thành công!");
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
            if (i != 1) tb.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    private void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private JSpinner createDateSpinner(boolean isFrom) {
        Calendar cal = Calendar.getInstance();
        if (isFrom) cal.add(Calendar.MONTH, -1);
        JSpinner s = new JSpinner(new SpinnerDateModel(cal.getTime(), null, null, Calendar.DAY_OF_MONTH));
        s.setEditor(new JSpinner.DateEditor(s, "dd/MM/yyyy"));
        return s;
    }
}