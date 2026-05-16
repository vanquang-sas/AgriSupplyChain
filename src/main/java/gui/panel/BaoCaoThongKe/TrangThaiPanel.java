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
import javax.swing.table.JTableHeader;
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
    
    private JPanel chartPanelDH, chartPanelLH;
    private JFreeChart pieChartDH, pieChartLH;
    
    private JTable tableDH, tableLH;
    private DefaultTableModel modelDH, modelLH;
    
    private JSpinner spinTuNgay, spinDenNgay;
    private List<ThongKeDTO.TrangThai> dataDH, dataLH;
    private DecimalFormat dfPercent = new DecimalFormat("0.0");

    public TrangThaiPanel() {
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

        JButton btnFilter = new JButton("Thống kê");
        styleButton(btnFilter, AppColor.PRIMARY);
        btnFilter.addActionListener(e -> refreshData());

        JButton btnExport = new JButton("Xuất PDF");
        styleButton(btnExport, new Color(220, 38, 38));
        btnExport.addActionListener(e -> exportToPDF());

        pnlFilter.add(new JLabel("Từ ngày:")); pnlFilter.add(spinTuNgay);
        pnlFilter.add(new JLabel("Đến ngày:")); pnlFilter.add(spinDenNgay);
        pnlFilter.add(btnFilter); pnlFilter.add(btnExport);

        // --- 2. CHARTS PANEL (Nửa trên) ---
        JPanel chartsContainer = new JPanel(new GridLayout(1, 2, 15, 0));
        chartsContainer.setBackground(AppColor.BACKGROUND);
        chartsContainer.setPreferredSize(new Dimension(0, 300));
        
        chartPanelDH = new JPanel(new BorderLayout());
        chartPanelDH.setBackground(Color.WHITE);
        chartPanelDH.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        
        chartPanelLH = new JPanel(new BorderLayout());
        chartPanelLH.setBackground(Color.WHITE);
        chartPanelLH.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        
        chartsContainer.add(chartPanelDH);
        chartsContainer.add(chartPanelLH);

        // --- 3. TABLES PANEL (Nửa dưới) ---
        JPanel tablesContainer = new JPanel(new GridLayout(1, 2, 15, 0));
        tablesContainer.setBackground(AppColor.BACKGROUND);
        
        // Table Đơn Hàng
        String[] cols = {"Trạng thái", "Số lượng", "Tỷ lệ (%)"};
        modelDH = new DefaultTableModel(cols, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        tableDH = new JTable(modelDH);
        styleModernTable(tableDH);
        JScrollPane scrollDH = new JScrollPane(tableDH);
        scrollDH.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(230,230,230)), "Bảng số liệu: Đơn Hàng", 0, 0, new Font("Segoe UI", Font.BOLD, 13), AppColor.PRIMARY
        ));
        scrollDH.getViewport().setBackground(Color.WHITE);

        // Table Lô Hàng
        modelLH = new DefaultTableModel(cols, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        tableLH = new JTable(modelLH);
        styleModernTable(tableLH);
        JScrollPane scrollLH = new JScrollPane(tableLH);
        scrollLH.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(230,230,230)), "Bảng số liệu: Lô Hàng", 0, 0, new Font("Segoe UI", Font.BOLD, 13), AppColor.PRIMARY
        ));
        scrollLH.getViewport().setBackground(Color.WHITE);

        tablesContainer.add(scrollDH);
        tablesContainer.add(scrollLH);

        // --- GỘP BỐ CỤC BẰNG SPLITPANE ---
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, chartsContainer, tablesContainer);
        split.setResizeWeight(0.55);
        split.setBorder(null);
        split.setDividerSize(10);
        split.setBackground(AppColor.BACKGROUND);

        add(pnlFilter, BorderLayout.NORTH);
        add(split, BorderLayout.CENTER);
    }

    private void refreshData() {
        Date tuNgay = (Date) spinTuNgay.getValue();
        Date denNgay = (Date) spinDenNgay.getValue();
        
        if (tuNgay.after(denNgay)) {
            JOptionPane.showMessageDialog(this, "Ngày bắt đầu không được lớn hơn ngày kết thúc!");
            return;
        }

        // 1. Tải dữ liệu Đơn hàng
        dataDH = thongKeBUS.getThongKeTrangThaiDonHang(tuNgay, denNgay);
        modelDH.setRowCount(0);
        DefaultPieDataset<String> dsDH = new DefaultPieDataset<>();
        for (ThongKeDTO.TrangThai d : dataDH) {
            modelDH.addRow(new Object[]{d.trangThai, d.soLuong, dfPercent.format(d.tyLe) + "%"});
            dsDH.setValue(d.trangThai + " (" + d.soLuong + ")", d.soLuong);
        }
        drawPieChart(chartPanelDH, dsDH, "Tỷ lệ trạng thái Đơn hàng", pieChartDH, true);

        // 2. Tải dữ liệu Lô hàng
        dataLH = thongKeBUS.getThongKeTrangThaiLoHang(tuNgay, denNgay); // Yêu cầu DAO/BUS đã thêm hàm này
        modelLH.setRowCount(0);
        DefaultPieDataset<String> dsLH = new DefaultPieDataset<>();
        for (ThongKeDTO.TrangThai d : dataLH) {
            modelLH.addRow(new Object[]{d.trangThai, d.soLuong, dfPercent.format(d.tyLe) + "%"});
            dsLH.setValue(d.trangThai + " (" + d.soLuong + ")", d.soLuong);
        }
        drawPieChart(chartPanelLH, dsLH, "Tỷ lệ trạng thái Lô hàng", pieChartLH, false);
    }

    private void drawPieChart(JPanel container, DefaultPieDataset<String> dataset, String title, JFreeChart refChart, boolean isDonHang) {
        JFreeChart chart = ChartFactory.createPieChart(title, dataset, true, true, false);
        chart.setBackgroundPaint(Color.WHITE);
        chart.getTitle().setFont(new Font("Segoe UI", Font.BOLD, 16));

        PiePlot<String> plot = (PiePlot<String>) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setLabelFont(new Font("Segoe UI", Font.PLAIN, 12));
        plot.setLabelBackgroundPaint(Color.WHITE);
        
        // Gán lại reference để Export PDF
        if (isDonHang) pieChartDH = chart;
        else pieChartLH = chart;

        container.removeAll();
        container.add(new ChartPanel(chart), BorderLayout.CENTER);
        container.revalidate();
        container.repaint();
    }

    private JSpinner createDateSpinner(boolean isFrom) {
        Calendar cal = Calendar.getInstance();
        if (isFrom) cal.add(Calendar.MONTH, -1);
        JSpinner s = new JSpinner(new SpinnerDateModel(cal.getTime(), null, null, Calendar.DAY_OF_MONTH));
        JSpinner.DateEditor editor = new JSpinner.DateEditor(s, "dd/MM/yyyy");
        s.setEditor(editor);
        s.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        s.setPreferredSize(new Dimension(130, 30));
        return s;
    }

    private void styleModernTable(JTable tb) {
        tb.setRowHeight(35);
        tb.setShowGrid(true);
        tb.setGridColor(new Color(240, 240, 240));

        JTableHeader header = tb.getTableHeader();
        header.setPreferredSize(new Dimension(header.getWidth(), 35));
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBackground(AppColor.PRIMARY);
                setForeground(Color.WHITE);
                setFont(new Font("Segoe UI", Font.BOLD, 13));
                setHorizontalAlignment(JLabel.CENTER);
                return this;
            }
        });

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.CENTER);
                setFont(new Font("Segoe UI", Font.PLAIN, 13));
                if (!isSelected) {
                    setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 250, 250));
                }
                return this;
            }
        };

        for (int i = 0; i < tb.getColumnCount(); i++) {
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

    private void exportToPDF() {
        if (modelDH.getRowCount() == 0 && modelLH.getRowCount() == 0) {
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
                com.itextpdf.text.Font fBold = new com.itextpdf.text.Font(bf, 15, com.itextpdf.text.Font.BOLD);
                com.itextpdf.text.Font fNormal = new com.itextpdf.text.Font(bf, 12, com.itextpdf.text.Font.NORMAL);

                Paragraph pTitle = new Paragraph("BÁO CÁO TRẠNG THÁI ĐƠN HÀNG VÀ LÔ HÀNG", fBold);
                pTitle.setAlignment(Element.ALIGN_CENTER);
                document.add(pTitle);

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Paragraph pSub = new Paragraph("Từ: " + sdf.format(spinTuNgay.getValue()) + " - Đến: " + sdf.format(spinDenNgay.getValue()), fNormal);
                pSub.setAlignment(Element.ALIGN_CENTER);
                pSub.setSpacingAfter(20f);
                document.add(pSub);

                // --- Xuất phần ĐƠN HÀNG ---
                Paragraph pDH = new Paragraph("1. THỐNG KÊ ĐƠN HÀNG", fBold);
                pDH.setSpacingAfter(10f);
                document.add(pDH);

                if (pieChartDH != null) {
                    java.awt.image.BufferedImage imgDH = pieChartDH.createBufferedImage(500, 300);
                    com.itextpdf.text.Image pdfImgDH = com.itextpdf.text.Image.getInstance(imgDH, null);
                    pdfImgDH.setAlignment(Element.ALIGN_CENTER);
                    document.add(pdfImgDH);
                }
                document.add(createPdfTable(modelDH, fBold, fNormal));

                // --- Xuất phần LÔ HÀNG ---
                document.newPage(); // Sang trang mới cho lô hàng
                Paragraph pLH = new Paragraph("2. THỐNG KÊ LÔ HÀNG", fBold);
                pLH.setSpacingAfter(10f);
                document.add(pLH);

                if (pieChartLH != null) {
                    java.awt.image.BufferedImage imgLH = pieChartLH.createBufferedImage(500, 300);
                    com.itextpdf.text.Image pdfImgLH = com.itextpdf.text.Image.getInstance(imgLH, null);
                    pdfImgLH.setAlignment(Element.ALIGN_CENTER);
                    document.add(pdfImgLH);
                }
                document.add(createPdfTable(modelLH, fBold, fNormal));

                document.close();
                JOptionPane.showMessageDialog(this, "Xuất PDF thành công!");
                Desktop.getDesktop().open(new File(fileChooser.getSelectedFile().getAbsolutePath() + ".pdf"));
            } catch (Exception ex) { ex.printStackTrace(); }
        }
    }

    private PdfPTable createPdfTable(DefaultTableModel model, com.itextpdf.text.Font fBold, com.itextpdf.text.Font fNormal) throws Exception {
        PdfPTable pdfTable = new PdfPTable(model.getColumnCount());
        pdfTable.setSpacingBefore(15f);
        pdfTable.setWidthPercentage(100);

        for (int i = 0; i < model.getColumnCount(); i++) {
            PdfPCell cell = new PdfPCell(new Phrase(model.getColumnName(i), fBold));
            cell.setBackgroundColor(new com.itextpdf.text.BaseColor(AppColor.PRIMARY.getRGB()));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(8f);
            pdfTable.addCell(cell);
        }

        for (int i = 0; i < model.getRowCount(); i++) {
            for (int j = 0; j < model.getColumnCount(); j++) {
                PdfPCell cell = new PdfPCell(new Phrase(model.getValueAt(i, j).toString(), fNormal));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(6f);
                pdfTable.addCell(cell);
            }
        }
        return pdfTable;
    }
}