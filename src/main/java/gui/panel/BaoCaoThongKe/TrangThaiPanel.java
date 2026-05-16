package gui.panel.BaoCaoThongKe;

import bus.ThongKeBUS;
import dto.ThongKeDTO;
import util.AppColor;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TrangThaiPanel extends JPanel {
    private ThongKeBUS thongKeBUS = new ThongKeBUS();
    private JPanel chartContainer;
    private JTable table;
    private DefaultTableModel tableModel;
    private JFreeChart currentChart;
    
    private JSpinner spinFromDate;
    private JSpinner spinToDate;

    // Bảng màu trạng thái
    private final Color[] CHART_PALETTE = {
        new Color(75, 172, 198),  new Color(155, 187, 89), 
        new Color(247, 150, 70),  new Color(192, 80, 77),
        new Color(128, 100, 162), new Color(79, 129, 189)
    };

    public TrangThaiPanel() {
        setLayout(new BorderLayout(0, 15));
        setBackground(AppColor.BACKGROUND);
        setBorder(new EmptyBorder(15, 15, 15, 15));
        initComponents();
        refreshData(); 
    }

    private void initComponents() {
        // --- BỘ LỌC ---
        JPanel pnlFilter = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 12));
        pnlFilter.setBackground(Color.WHITE);
        pnlFilter.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
                new EmptyBorder(5, 5, 5, 5)
        ));

        spinFromDate = createDateSpinner(true); 
        spinToDate = createDateSpinner(false); 

        JButton btnFilter = new JButton("Thống kê");
        styleButton(btnFilter, AppColor.PRIMARY);
        btnFilter.addActionListener(e -> refreshData());

        JButton btnExport = new JButton("Xuất PDF");
        styleButton(btnExport, new Color(220, 38, 38));
        btnExport.addActionListener(e -> exportToPDF());

        pnlFilter.add(new JLabel("Từ ngày:")); pnlFilter.add(spinFromDate);
        pnlFilter.add(new JLabel("Đến ngày:")); pnlFilter.add(spinToDate);
        pnlFilter.add(btnFilter);
        pnlFilter.add(btnExport);

        // --- KHU VỰC HIỂN THỊ ---
        chartContainer = new JPanel(new BorderLayout());
        chartContainer.setBackground(Color.WHITE);
        chartContainer.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true));

        String[] columnNames = {"STT", "Trạng Thái Đơn Hàng", "Số Lượng", "Tỷ Lệ (%)"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; } 
        };
        table = new JTable(tableModel);
        styleModernTable(table);

        JScrollPane scrollTable = new JScrollPane(table);
        scrollTable.setPreferredSize(new Dimension(800, 200));

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, chartContainer, scrollTable);
        splitPane.setResizeWeight(0.65);
        splitPane.setBorder(null);

        add(pnlFilter, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
    }

    private void refreshData() {
        Date from = (Date) spinFromDate.getValue();
        Date to = (Date) spinToDate.getValue();

        List<ThongKeDTO.TrangThai> data = thongKeBUS.getThongKeTrangThai(from, to);

        // Kiểm tra dữ liệu rỗng
        if (data == null || data.isEmpty()) {
            chartContainer.removeAll();
            chartContainer.repaint();
            tableModel.setRowCount(0);
            currentChart = null;
            JOptionPane.showMessageDialog(this, 
                "Không có đơn hàng nào được tạo trong khoảng thời gian này!", 
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Tính tổng số lượng để chia phần trăm
        int totalOrders = 0;
        for (ThongKeDTO.TrangThai tt : data) totalOrders += tt.soLuong;

        DefaultPieDataset dataset = new DefaultPieDataset();
        tableModel.setRowCount(0);
        int stt = 1;
        DecimalFormat df = new DecimalFormat("0.00");

        for (ThongKeDTO.TrangThai tt : data) {
            dataset.setValue(tt.trangThai, tt.soLuong);
            
            // Tính %
            double percent = (double) tt.soLuong / totalOrders * 100;
            tableModel.addRow(new Object[]{stt++, tt.trangThai, tt.soLuong, df.format(percent) + "%"});
        }
        
        updateChartUI(dataset);
    }

    private void updateChartUI(DefaultPieDataset dataset) {
        currentChart = ChartFactory.createPieChart(
                "Tỷ Lệ Trạng Thái Đơn Hàng", 
                dataset, true, true, false);

        currentChart.setBackgroundPaint(Color.WHITE);
        currentChart.getTitle().setFont(new Font("Segoe UI", Font.BOLD, 18));
        
        PiePlot plot = (PiePlot) currentChart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false); // Bỏ viền ngoài của khung chart
        plot.setShadowPaint(null);     // Flat design, bỏ shadow
        
        // Custom font cho nhãn (Label)
        plot.setLabelFont(new Font("Segoe UI", Font.PLAIN, 12));
        plot.setLabelBackgroundPaint(Color.WHITE);
        plot.setLabelShadowPaint(null);
        plot.setLabelOutlinePaint(null);

        // Áp dụng màu cho từng section
        List keys = dataset.getKeys();
        for (int i = 0; i < keys.size(); i++) {
            plot.setSectionPaint((Comparable) keys.get(i), CHART_PALETTE[i % CHART_PALETTE.length]);
        }

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
                String reportTitle = "BÁO CÁO TỶ LỆ TRẠNG THÁI ĐƠN HÀNG";
                String reportSub = String.format("Giai đoạn: Từ ngày %s đến ngày %s", 
                        sdf.format((Date) spinFromDate.getValue()), sdf.format((Date) spinToDate.getValue()));

                Paragraph pTitle = new Paragraph(reportTitle, fBold);
                pTitle.setAlignment(Element.ALIGN_CENTER);
                document.add(pTitle);

                Paragraph pSub = new Paragraph(reportSub, fNormal);
                pSub.setAlignment(Element.ALIGN_CENTER);
                pSub.setSpacingAfter(20f);
                document.add(pSub);

                // Add Chart
                java.awt.image.BufferedImage img = currentChart.createBufferedImage(500, 350);
                com.itextpdf.text.Image pdfImg = com.itextpdf.text.Image.getInstance(img, null);
                pdfImg.setAlignment(Element.ALIGN_CENTER);
                document.add(pdfImg);

                // Add Table
                PdfPTable pdfTable = new PdfPTable(4);
                pdfTable.setSpacingBefore(20f);
                pdfTable.setWidthPercentage(100);
                pdfTable.setWidths(new float[]{1f, 4f, 2f, 2f});
                
                String[] headers = {"STT", "Trạng thái", "Số lượng", "Tỷ lệ (%)"};
                for (String h : headers) {
                    PdfPCell cell = new PdfPCell(new Phrase(h, fBold));
                    cell.setBackgroundColor(new com.itextpdf.text.BaseColor(AppColor.PRIMARY.getRGB()));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setPadding(8f);
                    pdfTable.addCell(cell);
                }

                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    for (int j = 0; j < 4; j++) {
                        PdfPCell cell = new PdfPCell(new Phrase(tableModel.getValueAt(i, j).toString(), fNormal));
                        cell.setHorizontalAlignment(j == 1 ? Element.ALIGN_LEFT : Element.ALIGN_CENTER);
                        cell.setPadding(5f);
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
            if (i != 1) tb.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        tb.getColumnModel().getColumn(0).setMaxWidth(60);
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