package gui.panel.BaoCaoThongKe;

import bus.SanPhamBUS;
import bus.ThongKeBUS;
import dto.SanPhamDTO;
import dto.ThongKeDTO;
import util.AppColor;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
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
import java.awt.event.ActionListener;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LichSuGiaPanel extends JPanel {
    private final ThongKeBUS thongKeBUS = new ThongKeBUS();
    private final SanPhamBUS sanPhamBUS = new SanPhamBUS();

    private JComboBox<String> cbSanPham;
    private JComboBox<String> cbDisplayType;
    private JSpinner spinFromDate;
    private JSpinner spinToDate;

    private JPanel chartContainer;
    private JTable table;
    private DefaultTableModel tableModel;
    private JFreeChart currentChart;
    private List<ThongKeDTO.LichSuGia> currentData = new ArrayList<>();
    private List<SanPhamDTO> products = new ArrayList<>();

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private final DecimalFormat moneyFormat = new DecimalFormat("#,###");
    private final DecimalFormat percentFormat = new DecimalFormat("0.00");

    public LichSuGiaPanel() {
        setLayout(new BorderLayout(0, 15));
        setBackground(AppColor.BACKGROUND);
        setBorder(new EmptyBorder(15, 15, 15, 15));
        initComponents();
        loadProducts();
        refreshData(false);
    }

    private void initComponents() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(AppColor.SURFACE);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppColor.BORDER, 1, true),
                new EmptyBorder(18, 20, 18, 20)));

        JLabel lblTitle = new JLabel("BÁO CÁO BIẾN ĐỘNG GIÁ SẢN PHẨM");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(AppColor.TEXT_PRIMARY);
        header.add(lblTitle, BorderLayout.WEST);

        add(header, BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(0, 15));
        content.setBackground(AppColor.BACKGROUND);

        JPanel pnlFilter = new JPanel(new GridBagLayout());
        pnlFilter.setBackground(AppColor.SURFACE);
        pnlFilter.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppColor.BORDER, 1, true),
                new EmptyBorder(12, 12, 12, 12)));

        cbSanPham = new JComboBox<>();
        cbSanPham.setPreferredSize(new Dimension(220, 32));
        cbSanPham.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbSanPham.addActionListener(e -> refreshData(false));

        spinFromDate = createDateSpinner(true);
        spinToDate = createDateSpinner(false);

        cbDisplayType = new JComboBox<>(new String[]{"Cả hai giá", "Giá mua", "Giá bán"});
        cbDisplayType.setPreferredSize(new Dimension(170, 32));
        cbDisplayType.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbDisplayType.addActionListener(e -> refreshData(false));

        ActionListener filterAction = e -> refreshData(true);
        JButton btnApply = new JButton("Thống kê");
        styleButton(btnApply, AppColor.PRIMARY);
        btnApply.addActionListener(filterAction);

        JButton btnExportPdf = new JButton("Xuất PDF");
        styleButton(btnExportPdf, AppColor.ERROR);
        btnExportPdf.addActionListener(e -> exportToPDF());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        pnlFilter.add(new JLabel("Sản phẩm:"), gbc);
        gbc.gridx = 1;
        pnlFilter.add(cbSanPham, gbc);

        gbc.gridx = 2;
        pnlFilter.add(new JLabel("Từ ngày:"), gbc);
        gbc.gridx = 3;
        pnlFilter.add(spinFromDate, gbc);

        gbc.gridx = 4;
        pnlFilter.add(new JLabel("Đến ngày:"), gbc);
        gbc.gridx = 5;
        pnlFilter.add(spinToDate, gbc);

        gbc.gridx = 6;
        gbc.weightx = 0.1;
        pnlFilter.add(new JLabel("Hiển thị:"), gbc);
        pnlFilter.add(Box.createHorizontalStrut(2)); 
        pnlFilter.add(cbDisplayType);

        gbc.gridx = 7;
        gbc.weightx = 0;
        pnlFilter.add(cbDisplayType, gbc);

        gbc.gridx = 8;
        pnlFilter.add(btnApply, gbc);
        gbc.gridx = 9;
        pnlFilter.add(btnExportPdf, gbc);

        chartContainer = new JPanel(new BorderLayout());
        chartContainer.setBackground(AppColor.SURFACE);
        chartContainer.setBorder(BorderFactory.createLineBorder(AppColor.BORDER, 1, true));
        chartContainer.setPreferredSize(new Dimension(0, 360));
        chartContainer.add(createEmptyState("Chọn sản phẩm và thời gian để xem biểu đồ lịch sử giá."), BorderLayout.CENTER);

        String[] columnNames = {"Ngày cập nhật", "Tên sản phẩm", "Giá mua (VND)", "Giá bán (VND)", "Biên độ lợi nhuận (%)"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        styleModernTable(table);

        JScrollPane scrollTable = new JScrollPane(table);
        scrollTable.setPreferredSize(new Dimension(0, 220));
        scrollTable.getViewport().setBackground(AppColor.SURFACE);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, chartContainer, scrollTable);
        splitPane.setResizeWeight(0.65);
        splitPane.setBorder(null);
        splitPane.setDividerSize(8);

        content.add(pnlFilter, BorderLayout.NORTH);
        content.add(splitPane, BorderLayout.CENTER);

        add(content, BorderLayout.CENTER);
    }

    private void loadProducts() {
        products = sanPhamBUS.getAll();
        cbSanPham.removeAllItems();
        if (products == null || products.isEmpty()) {
            cbSanPham.addItem("Không có sản phẩm");
            cbSanPham.setEnabled(false);
            showEmptyState("Không tìm thấy sản phẩm nào. Vui lòng cập nhật danh sách sản phẩm trước.");
            return;
        }

        for (SanPhamDTO product : products) {
            cbSanPham.addItem(product.getTenSP() + " (" + product.getMaSP() + ")");
        }
        cbSanPham.setSelectedIndex(0);
    }

    private void refreshData(boolean showNotification) {
        if (products == null || products.isEmpty()) {
            return;
        }

        Date from = (Date) spinFromDate.getValue();
        Date to = (Date) spinToDate.getValue();

        if (from.after(to)) {
            if (showNotification) {
                JOptionPane.showMessageDialog(this, "Ngày bắt đầu phải nhỏ hơn hoặc bằng ngày kết thúc.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            }
            return;
        }

        int index = cbSanPham.getSelectedIndex();
        if (index < 0 || index >= products.size()) {
            if (showNotification) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            }
            return;
        }

        String maSP = products.get(index).getMaSP();
        currentData = thongKeBUS.getLichSuGiaTheoSanPham(maSP, from, to);

        if (currentData == null || currentData.isEmpty()) {
            showEmptyState("Không có dữ liệu lịch sử giá cho khoảng thời gian và sản phẩm đã chọn.");
            return;
        }

        updateTable();
        updateChartOnly();
    }

    private void updateChartOnly() {
        if (currentData == null || currentData.isEmpty()) {
            showEmptyState("Không có dữ liệu để hiển thị biểu đồ.");
            return;
        }

        String displayType = cbDisplayType.getSelectedItem() != null ? cbDisplayType.getSelectedItem().toString() : "Cả hai giá";
        boolean showMua = displayType.equals("Giá mua") || displayType.equals("Cả hai giá");
        boolean showBan = displayType.equals("Giá bán") || displayType.equals("Cả hai giá");

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (ThongKeDTO.LichSuGia row : currentData) {
            String label = dateFormat.format(row.tgApDung);
            if (showMua) {
                dataset.addValue(row.giaMua, "Giá mua", label);
            }
            if (showBan) {
                dataset.addValue(row.giaBan, "Giá bán", label);
            }
        }

        if (dataset.getRowCount() == 0) {
            showEmptyState("Chưa có lựa chọn biểu đồ phù hợp.");
            return;
        }

        currentChart = ChartFactory.createLineChart(
                "Lịch sử biến động giá", "Thời gian", "Giá (VND)",
                dataset, PlotOrientation.VERTICAL, true, true, false);

        currentChart.setBackgroundPaint(Color.WHITE);
        CategoryPlot plot = currentChart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(new Color(230, 230, 230));
        plot.setOutlineVisible(false);

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setNumberFormatOverride(new DecimalFormat("#,###"));

        LineAndShapeRenderer renderer = new LineAndShapeRenderer();
        for (int i = 0; i < dataset.getRowCount(); i++) {
            String seriesKey = dataset.getRowKey(i).toString();
            if (seriesKey.equals("Giá mua")) {
                renderer.setSeriesPaint(i, Color.RED);
            } else if (seriesKey.equals("Giá bán")) {
                renderer.setSeriesPaint(i, new Color(34, 197, 94));
            }
            renderer.setSeriesStroke(i, new BasicStroke(3.0f));
            renderer.setSeriesShapesVisible(i, true);
            renderer.setSeriesShapesFilled(i, true);
        }

        renderer.setDefaultToolTipGenerator(new StandardCategoryToolTipGenerator(
                "{0} - {1}: {2} VNĐ", new DecimalFormat("#,###.##")));
        plot.setRenderer(renderer);

        chartContainer.removeAll();
        chartContainer.add(new ChartPanel(currentChart), BorderLayout.CENTER);
        chartContainer.revalidate();
        chartContainer.repaint();
    }

    private void updateTable() {
        tableModel.setRowCount(0);
        for (ThongKeDTO.LichSuGia row : currentData) {
            tableModel.addRow(new Object[]{
                    dateFormat.format(row.tgApDung),
                    row.tenSP,
                    moneyFormat.format(row.giaMua),
                    moneyFormat.format(row.giaBan),
                    percentFormat.format(row.loiNhuan) + "%"
            });
        }
    }

    private void showEmptyState(String message) {
        chartContainer.removeAll();
        chartContainer.add(createEmptyState(message), BorderLayout.CENTER);
        chartContainer.revalidate();
        chartContainer.repaint();
        tableModel.setRowCount(0);
        currentChart = null;
    }

    private JLabel createEmptyState(String message) {
        JLabel lbl = new JLabel("<html><div style='text-align:center;color:#6B7280;'>" + message + "</div></html>", SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return lbl;
    }

    private void exportToPDF() {
        if (currentChart == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng bấm 'Thống kê' để tải biểu đồ trước khi xuất!");
            return;
        }
        
        if (currentData == null || currentData.isEmpty() || currentChart == null) {
            JOptionPane.showMessageDialog(this, "Không có dữ liệu để xuất báo cáo!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String path = fileChooser.getSelectedFile().getAbsolutePath() + ".pdf";
            try (FileOutputStream fos = new FileOutputStream(path)) {
                Document document = new Document();
                PdfWriter.getInstance(document, fos);
                document.open();

                BaseFont bf = BaseFont.createFont("c:/windows/fonts/arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                com.itextpdf.text.Font fBold = new com.itextpdf.text.Font(bf, 14, com.itextpdf.text.Font.BOLD);
                com.itextpdf.text.Font fNormal = new com.itextpdf.text.Font(bf, 12, com.itextpdf.text.Font.NORMAL);

                Paragraph title = new Paragraph("BÁO CÁO BIẾN ĐỘNG GIÁ SẢN PHẨM", fBold);
                title.setAlignment(Element.ALIGN_CENTER);
                document.add(title);

                Paragraph sub = new Paragraph("Sản phẩm: " + cbSanPham.getSelectedItem() + " | " + dateFormat.format((Date) spinFromDate.getValue()) + " - " + dateFormat.format((Date) spinToDate.getValue()), fNormal);
                sub.setAlignment(Element.ALIGN_CENTER);
                sub.setSpacingAfter(14f);
                document.add(sub);

                java.awt.image.BufferedImage img = currentChart.createBufferedImage(640, 320);
                com.itextpdf.text.Image chartImage = com.itextpdf.text.Image.getInstance(img, null);
                chartImage.setAlignment(Element.ALIGN_CENTER);
                document.add(chartImage);

                PdfPTable pdfTable = new PdfPTable(tableModel.getColumnCount());
                pdfTable.setWidthPercentage(100);
                pdfTable.setSpacingBefore(16f);
                pdfTable.setWidths(new float[]{2.5f, 3.5f, 2.5f, 2.5f, 2.5f});

                for (int i = 0; i < tableModel.getColumnCount(); i++) {
                    PdfPCell cell = new PdfPCell(new Phrase(tableModel.getColumnName(i), fBold));
                    cell.setBackgroundColor(new com.itextpdf.text.BaseColor(AppColor.PRIMARY.getRGB()));
                    cell.setPadding(8f);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    pdfTable.addCell(cell);
                }

                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    for (int j = 0; j < tableModel.getColumnCount(); j++) {
                        PdfPCell cell = new PdfPCell(new Phrase(tableModel.getValueAt(i, j).toString(), fNormal));
                        cell.setPadding(6f);
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        pdfTable.addCell(cell);
                    }
                }

                document.add(pdfTable);
                document.close();
                Desktop.getDesktop().open(new File(path));
                JOptionPane.showMessageDialog(this, "Xuất PDF thành công!");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi khi xuất PDF.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private String escapeCsv(String value) {
        String escaped = value.replace("\"", "\"\"");
        if (escaped.contains(";") || escaped.contains("\n") || escaped.contains("\r")) {
            escaped = "\"" + escaped + "\"";
        }
        return escaped;
    }

    private JSpinner createDateSpinner(boolean isFrom) {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        if (isFrom) {
            calendar.add(java.util.Calendar.MONTH, -1);
        }
        Date value = calendar.getTime();
        JSpinner spinner = new JSpinner(new SpinnerDateModel(value, null, null, java.util.Calendar.DAY_OF_MONTH));
        spinner.setEditor(new JSpinner.DateEditor(spinner, "dd/MM/yyyy"));
        spinner.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        spinner.setPreferredSize(new Dimension(110, 30));
        return spinner;
    }

    private void styleModernTable(JTable tb) {
        tb.setRowHeight(36);
        tb.setGridColor(new Color(240, 240, 240));
        tb.setShowGrid(true);

        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setBackground(AppColor.PRIMARY);
        headerRenderer.setForeground(Color.WHITE);
        headerRenderer.setFont(new Font("Segoe UI", Font.BOLD, 13));
        headerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tb.getTableHeader().setDefaultRenderer(headerRenderer);

        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
        cellRenderer.setHorizontalAlignment(JLabel.CENTER);
        cellRenderer.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        for (int i = 0; i < tb.getColumnCount(); i++) {
            tb.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }
    }

    private void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(140, 34));
    }
}
