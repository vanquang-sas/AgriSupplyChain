package gui.panel.BaoCaoThongKe;

import bus.ThongKeBUS;
import dto.ThongKeDTO;
import util.AppColor;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
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
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TopSanPhamPanel extends JPanel {
    private ThongKeBUS thongKeBUS = new ThongKeBUS();
    
    private JSpinner spinTuNgay, spinDenNgay;
    private JComboBox<String> cbLimit, cbSortOrder, cbSortTableBy;
    
    private JPanel chartPanelDoanhThu, chartPanelSoLuong, chartPanelBienDo;
    private JFreeChart chartDT, chartSL, chartBD;
    private JTable table;
    private DefaultTableModel tableModel;
    
    private List<ThongKeDTO.SanPham> rawData = new ArrayList<>();
    private DecimalFormat dfMoney = new DecimalFormat("#,###");
    private DecimalFormat dfPercent = new DecimalFormat("0.00");

    public TopSanPhamPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(AppColor.BACKGROUND);
        setBorder(new EmptyBorder(15, 15, 15, 15));
        initComponents();
        refreshData();
    }

    private void initComponents() {
        // --- 1. FILTER PANEL ---
        JPanel pnlFilter = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        pnlFilter.setBackground(Color.WHITE);
        pnlFilter.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true));

        spinTuNgay = createDateSpinner(true);
        spinDenNgay = createDateSpinner(false);
        cbLimit = new JComboBox<>(new String[]{"Top 5", "Top 10", "Tất cả"});
        cbSortOrder = new JComboBox<>(new String[]{"Cao nhất", "Thấp nhất"});
        cbSortTableBy = new JComboBox<>(new String[]{"Theo Doanh thu", "Theo Số lượng", "Theo Biên độ LN"});

        JButton btnFilter = new JButton("Thống kê");
        styleButton(btnFilter, AppColor.PRIMARY);
        btnFilter.addActionListener(e -> refreshData());

        JButton btnExport = new JButton("Xuất PDF");
        styleButton(btnExport, new Color(220, 38, 38));
        btnExport.addActionListener(e -> exportToPDF());

        pnlFilter.add(new JLabel("Từ ngày:")); pnlFilter.add(spinTuNgay);
        pnlFilter.add(new JLabel("Đến ngày:")); pnlFilter.add(spinDenNgay);
        pnlFilter.add(new JLabel("Hiển thị:")); pnlFilter.add(cbLimit);
        pnlFilter.add(new JLabel("Thứ tự:")); pnlFilter.add(cbSortOrder);
        pnlFilter.add(new JLabel("Sắp xếp:")); pnlFilter.add(cbSortTableBy);
        pnlFilter.add(btnFilter); pnlFilter.add(btnExport);

        // --- 2. MAIN CONTENT (CHỨA CẢ CHART & TABLE) ---
        JPanel contentPanel = new JPanel(new BorderLayout(0, 20));
        contentPanel.setBackground(AppColor.BACKGROUND);
        contentPanel.setBorder(new EmptyBorder(10, 5, 10, 5));

        // KHU VỰC 3 CHART
        JPanel chartsContainer = new JPanel(new GridLayout(3, 1, 0, 20));
        chartsContainer.setBackground(AppColor.BACKGROUND);
        // Cố định chiều cao tổng cho 3 chart (khoảng 350px mỗi chart)
        chartsContainer.setPreferredSize(new Dimension(0, 1050)); 

        chartPanelDoanhThu = createEmptyChartPanel("Biểu đồ Doanh Thu");
        chartPanelSoLuong = createEmptyChartPanel("Biểu đồ Số Lượng Bán");
        chartPanelBienDo = createEmptyChartPanel("Biểu đồ Biên Độ Lợi Nhuận");

        chartsContainer.add(chartPanelDoanhThu);
        chartsContainer.add(chartPanelSoLuong);
        chartsContainer.add(chartPanelBienDo);

        // KHU VỰC BẢNG DỮ LIỆU
        String[] cols = {"Mã SP", "Tên SP", "Số lượng", "Doanh thu", "Lợi nhuận", "Biên độ (%)"};
        tableModel = new DefaultTableModel(cols, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        table = new JTable(tableModel);
        styleModernTable(table);
        
        JScrollPane scrollTable = new JScrollPane(table);
        scrollTable.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(230,230,230)), 
                "Bảng Chi Tiết Thống Kê", 0, 0, new Font("Segoe UI", Font.BOLD, 13), AppColor.PRIMARY));
        scrollTable.getViewport().setBackground(Color.WHITE);
        // Cố định chiều cao cho vùng chứa bảng
        scrollTable.setPreferredSize(new Dimension(0, 450)); 

        // Gắn 2 khu vực vào contentPanel
        contentPanel.add(chartsContainer, BorderLayout.NORTH);
        contentPanel.add(scrollTable, BorderLayout.CENTER);

        // --- 3. BỌC TOÀN BỘ TRANG VÀO 1 SCROLL CHÍNH ---
        JScrollPane mainScroll = new JScrollPane(contentPanel);
        mainScroll.setBorder(null);
        mainScroll.getVerticalScrollBar().setUnitIncrement(25); // Chỉnh tốc độ cuộn mượt mà
        mainScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER); // Tắt cuộn ngang
        mainScroll.getViewport().setBackground(AppColor.BACKGROUND);

        add(pnlFilter, BorderLayout.NORTH);
        add(mainScroll, BorderLayout.CENTER);
    }

    private JPanel createEmptyChartPanel(String title) {
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(230,230,230)), 
                title, 0, 0, new Font("Segoe UI", Font.BOLD, 13), Color.GRAY));
        return pnl;
    }

    private void refreshData() {
        final Date tuNgay = (Date) spinTuNgay.getValue();
        final Date denNgay = (Date) spinDenNgay.getValue();

        if (tuNgay.after(denNgay)) {
            JOptionPane.showMessageDialog(this, "Ngày bắt đầu không được lớn hơn ngày kết thúc!");
            return;
        }

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        SwingWorker<List<ThongKeDTO.SanPham>, Void> worker = new SwingWorker<List<ThongKeDTO.SanPham>, Void>() {
            @Override
            protected List<ThongKeDTO.SanPham> doInBackground() throws Exception {
                return thongKeBUS.getThongKeSanPham(tuNgay, denNgay);
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                try {
                    rawData = get();
                    if (rawData == null || rawData.isEmpty()) {
                        showEmptyState();
                        return;
                    }

                    boolean isDesc = cbSortOrder.getSelectedIndex() == 0; 
                    int limit = cbLimit.getSelectedIndex() == 0 ? 5 : (cbLimit.getSelectedIndex() == 1 ? 10 : rawData.size());

                    drawChartDoanhThu(limit, isDesc);
                    drawChartSoLuong(limit, isDesc);
                    drawChartBienDo(limit, isDesc);
                    fillTable(isDesc);

                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Lỗi tải dữ liệu SP: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void showEmptyState() {
        tableModel.setRowCount(0);
        showEmptyChart(chartPanelDoanhThu, "Chưa có dữ liệu Doanh thu");
        showEmptyChart(chartPanelSoLuong, "Chưa có dữ liệu Số lượng");
        showEmptyChart(chartPanelBienDo, "Chưa có dữ liệu Biên độ LN");
    }

    private void showEmptyChart(JPanel pnl, String message) {
        pnl.removeAll();
        JLabel lblEmpty = new JLabel(message, SwingConstants.CENTER);
        lblEmpty.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lblEmpty.setForeground(Color.GRAY);
        pnl.add(lblEmpty, BorderLayout.CENTER);
        pnl.revalidate(); pnl.repaint();
    }

    private void drawChartDoanhThu(int limit, boolean isDesc) {
        List<ThongKeDTO.SanPham> list = new ArrayList<>(rawData);
        list.sort((a, b) -> isDesc ? Double.compare(b.doanhThu, a.doanhThu) : Double.compare(a.doanhThu, b.doanhThu));
        
        DefaultCategoryDataset ds = new DefaultCategoryDataset();
        for (int i = 0; i < Math.min(limit, list.size()); i++) {
            ds.addValue(list.get(i).doanhThu, "Doanh Thu", list.get(i).tenSP);
        }

        chartDT = ChartFactory.createBarChart("Xếp hạng Doanh Thu", "Sản phẩm", "VNĐ", ds, PlotOrientation.VERTICAL, false, true, false);
        customizeChart(chartDT, AppColor.PRIMARY, true);
        
        NumberAxis rangeAxis = (NumberAxis) chartDT.getCategoryPlot().getRangeAxis();
        rangeAxis.setNumberFormatOverride(new DecimalFormat("#,### M") {
            @Override public StringBuffer format(double num, StringBuffer res, java.text.FieldPosition pos) {
                return super.format(num / 1000000.0, res, pos);
            }
        });

        chartPanelDoanhThu.removeAll();
        chartPanelDoanhThu.add(new ChartPanel(chartDT), BorderLayout.CENTER);
        chartPanelDoanhThu.revalidate(); chartPanelDoanhThu.repaint();
    }

    private void drawChartSoLuong(int limit, boolean isDesc) {
        List<ThongKeDTO.SanPham> list = new ArrayList<>(rawData);
        list.sort((a, b) -> isDesc ? Integer.compare(b.soLuong, a.soLuong) : Integer.compare(a.soLuong, b.soLuong));
        
        DefaultCategoryDataset ds = new DefaultCategoryDataset();
        for (int i = 0; i < Math.min(limit, list.size()); i++) {
            ds.addValue(list.get(i).soLuong, "Số Lượng", list.get(i).tenSP);
        }

        chartSL = ChartFactory.createBarChart("Xếp hạng Số Lượng Bán", "Sản phẩm", "Số lượng", ds, PlotOrientation.VERTICAL, false, true, false);
        customizeChart(chartSL, new Color(40, 167, 69), true);

        chartPanelSoLuong.removeAll();
        chartPanelSoLuong.add(new ChartPanel(chartSL), BorderLayout.CENTER);
        chartPanelSoLuong.revalidate(); chartPanelSoLuong.repaint();
    }

    private void drawChartBienDo(int limit, boolean isDesc) {
        List<ThongKeDTO.SanPham> list = new ArrayList<>(rawData);
        list.sort((a, b) -> isDesc ? Double.compare(b.bienDoLN, a.bienDoLN) : Double.compare(a.bienDoLN, b.bienDoLN));
        
        DefaultCategoryDataset ds = new DefaultCategoryDataset();
        for (int i = 0; i < Math.min(limit, list.size()); i++) {
            ds.addValue(list.get(i).bienDoLN, "Biên Độ", list.get(i).tenSP);
        }

        chartBD = ChartFactory.createBarChart("Xếp hạng Biên Độ Lợi Nhuận", "Sản phẩm", "Phần trăm (%)", ds, PlotOrientation.HORIZONTAL, false, true, false);
        customizeChart(chartBD, new Color(255, 193, 7), false);

        chartPanelBienDo.removeAll();
        chartPanelBienDo.add(new ChartPanel(chartBD), BorderLayout.CENTER);
        chartPanelBienDo.revalidate(); chartPanelBienDo.repaint();
    }

    private void customizeChart(JFreeChart chart, Color barColor, boolean isVertical) {
        chart.setBackgroundPaint(Color.WHITE);
        chart.getTitle().setFont(new Font("Segoe UI", Font.BOLD, 14));
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(new Color(220, 220, 220));
        plot.setOutlineVisible(false);
        
        if(isVertical) {
            plot.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        }
        
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, barColor);
        renderer.setMaximumBarWidth(0.15);
    }

    private void fillTable(boolean isDesc) {
        List<ThongKeDTO.SanPham> list = new ArrayList<>(rawData);
        int sortType = cbSortTableBy.getSelectedIndex();
        
        list.sort((a, b) -> {
            if (sortType == 0) return isDesc ? Double.compare(b.doanhThu, a.doanhThu) : Double.compare(a.doanhThu, b.doanhThu);
            if (sortType == 1) return isDesc ? Integer.compare(b.soLuong, a.soLuong) : Integer.compare(a.soLuong, b.soLuong);
            return isDesc ? Double.compare(b.bienDoLN, a.bienDoLN) : Double.compare(a.bienDoLN, b.bienDoLN);
        });

        tableModel.setRowCount(0);
        for (ThongKeDTO.SanPham sp : list) {
            tableModel.addRow(new Object[]{
                sp.maSP, sp.tenSP, sp.soLuong, 
                dfMoney.format(sp.doanhThu), dfMoney.format(sp.loiNhuan), 
                dfPercent.format(sp.bienDoLN) + "%"
            });
        }
    }

    private JSpinner createDateSpinner(boolean isFrom) {
        Calendar cal = Calendar.getInstance();
        if (isFrom) cal.add(Calendar.MONTH, -1);
        JSpinner s = new JSpinner(new SpinnerDateModel(cal.getTime(), null, null, Calendar.DAY_OF_MONTH));
        JSpinner.DateEditor editor = new JSpinner.DateEditor(s, "dd/MM/yyyy");
        s.setEditor(editor);
        s.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        s.setPreferredSize(new Dimension(110, 30));
        return s;
    }

    private void styleModernTable(JTable tb) {
        tb.setRowHeight(35); tb.setShowGrid(true); tb.setGridColor(new Color(240, 240, 240));
        JTableHeader header = tb.getTableHeader();
        header.setPreferredSize(new Dimension(header.getWidth(), 35));
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBackground(AppColor.PRIMARY); setForeground(Color.WHITE);
                setFont(new Font("Segoe UI", Font.BOLD, 13)); setHorizontalAlignment(JLabel.CENTER); return this;
            }
        });
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.CENTER); setFont(new Font("Segoe UI", Font.PLAIN, 13));
                if (!isSelected) setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 250, 250));
                return this;
            }
        };
        for (int i = 0; i < tb.getColumnCount(); i++) tb.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
    }

    private void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg); btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false); btn.setBorderPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR)); btn.setPreferredSize(new Dimension(100, 32));
    }

    private void exportToPDF() {
        if (rawData == null || rawData.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có dữ liệu để xuất PDF!"); return;
        }
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(fileChooser.getSelectedFile().getAbsolutePath() + ".pdf"));
                document.open();

                BaseFont bf = BaseFont.createFont("c:/windows/fonts/arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                com.itextpdf.text.Font fBold = new com.itextpdf.text.Font(bf, 14, com.itextpdf.text.Font.BOLD);
                com.itextpdf.text.Font fNormal = new com.itextpdf.text.Font(bf, 11, com.itextpdf.text.Font.NORMAL);

                Paragraph pTitle = new Paragraph("BÁO CÁO THỐNG KÊ SẢN PHẨM", fBold);
                pTitle.setAlignment(Element.ALIGN_CENTER); 
                pTitle.setSpacingAfter(15f); 
                document.add(pTitle);

                if (chartDT != null) {
                    com.itextpdf.text.Image img1 = com.itextpdf.text.Image.getInstance(chartDT.createBufferedImage(450, 220), null);
                    img1.setAlignment(Element.ALIGN_CENTER);
                    document.add(img1);
                    document.add(new Paragraph(" "));
                }
                if (chartSL != null) {
                    com.itextpdf.text.Image img2 = com.itextpdf.text.Image.getInstance(chartSL.createBufferedImage(450, 220), null);
                    img2.setAlignment(Element.ALIGN_CENTER);
                    document.add(img2);
                    document.add(new Paragraph(" "));
                }
                if (chartBD != null) {
                    com.itextpdf.text.Image img3 = com.itextpdf.text.Image.getInstance(chartBD.createBufferedImage(450, 220), null);
                    img3.setAlignment(Element.ALIGN_CENTER);
                    document.add(img3);
                }

                document.add(new Paragraph(" ", fNormal)); 

                PdfPTable pdfTable = new PdfPTable(table.getColumnCount());
                pdfTable.setSpacingBefore(15f); 
                pdfTable.setWidthPercentage(100);
                for (int i = 0; i < table.getColumnCount(); i++) {
                    PdfPCell cell = new PdfPCell(new Phrase(table.getColumnName(i), fBold));
                    cell.setBackgroundColor(new com.itextpdf.text.BaseColor(AppColor.PRIMARY.getRGB()));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER); cell.setPadding(6f); pdfTable.addCell(cell);
                }
                for (int i = 0; i < table.getRowCount(); i++) {
                    for (int j = 0; j < table.getColumnCount(); j++) {
                        PdfPCell cell = new PdfPCell(new Phrase(table.getValueAt(i, j).toString(), fNormal));
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER); cell.setPadding(5f); pdfTable.addCell(cell);
                    }
                }
                document.add(pdfTable); 
                document.close();
                
                JOptionPane.showMessageDialog(this, "Xuất file PDF thành công!");
                Desktop.getDesktop().open(new File(fileChooser.getSelectedFile().getAbsolutePath() + ".pdf"));
            } catch (Exception ex) { 
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi xuất PDF: " + ex.getMessage());
            }
        }
    }
}