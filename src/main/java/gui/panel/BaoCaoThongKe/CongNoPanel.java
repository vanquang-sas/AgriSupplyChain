package gui.panel.BaoCaoThongKe;

import bus.ThongKeBUS;
import dto.ThongKeDTO;
import util.AppColor;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CongNoPanel extends JPanel {
    private ThongKeBUS thongKeBUS = new ThongKeBUS();
    private List<ThongKeDTO.CongNo> allData = new ArrayList<>();
    private List<ThongKeDTO.CongNo> filteredData = new ArrayList<>();

    // Components UI
    private JTextField txtSearch;
    private JLabel lblTotalDebt;
    private JLabel lblCustomerCount;
    private JLabel lblPaymentRate;
    private JPanel chartContainer;
    private JTable table;
    private DefaultTableModel tableModel;

    // Charts JFreeChart
    private JFreeChart pieChart;
    private JFreeChart barChart;

    public CongNoPanel() {
        setLayout(new BorderLayout(0, 15));
        setBackground(AppColor.BACKGROUND);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        initComponents();
        loadData();
    }

    private void initComponents() {
        // --- 1. THANH BỘ LỌC VÀ Ô TÌM KIẾM ---
        JPanel pnlFilter = new JPanel(new BorderLayout(15, 0));
        pnlFilter.setBackground(Color.WHITE);
        pnlFilter.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
                new EmptyBorder(10, 15, 10, 15)
        ));

        // Khung tìm kiếm ở bên trái
        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlSearch.setBackground(Color.WHITE);
        
        JLabel lblSearch = new JLabel("Tìm kiếm:");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        txtSearch = new JTextField(20);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtSearch.putClientProperty("JTextField.placeholderText", "Nhập tên hoặc mã khách hàng...");
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { filterData(); }
            @Override
            public void removeUpdate(DocumentEvent e) { filterData(); }
            @Override
            public void changedUpdate(DocumentEvent e) { filterData(); }
        });

        pnlSearch.add(lblSearch);
        pnlSearch.add(txtSearch);

        // Các nút thao tác ở bên phải
        JPanel pnlActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlActions.setBackground(Color.WHITE);

        JButton btnRefresh = new JButton("Làm mới");
        styleButton(btnRefresh, AppColor.PRIMARY);
        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            loadData();
        });

        JButton btnExport = new JButton("Xuất PDF");
        styleButton(btnExport, new Color(220, 38, 38)); // Màu đỏ cao cấp cho PDF
        btnExport.addActionListener(e -> exportToPDF());

        pnlActions.add(btnRefresh);
        pnlActions.add(btnExport);

        pnlFilter.add(pnlSearch, BorderLayout.WEST);
        pnlFilter.add(pnlActions, BorderLayout.EAST);

        // --- 2. HỆ THỐNG KPI CARDS ---
        lblTotalDebt = new JLabel("0đ");
        lblCustomerCount = new JLabel("0");
        lblPaymentRate = new JLabel("0.0%");

        JPanel pnlCards = new JPanel(new GridLayout(1, 3, 15, 0));
        pnlCards.setBackground(AppColor.BACKGROUND);
        pnlCards.add(createCardPanel("TỔNG CÔNG NỢ HIỆN TẠI", lblTotalDebt, AppColor.ERROR));
        pnlCards.add(createCardPanel("SỐ KHÁCH HÀNG CÒN NỢ", lblCustomerCount, AppColor.PRIMARY));
        pnlCards.add(createCardPanel("TỶ LỆ ĐÃ THANH TOÁN (LŨY KẾ)", lblPaymentRate, AppColor.INFO));

        // --- 3. KHU VỰC BIỂU ĐỒ (SPLIT SIDE-BY-SIDE) ---
        chartContainer = new JPanel(new GridLayout(1, 2, 15, 0));
        chartContainer.setBackground(AppColor.BACKGROUND);
        chartContainer.setPreferredSize(new Dimension(800, 320));

        // --- 4. BẢNG DỮ LIỆU CHI TIẾT ---
        String[] cols = {"Mã KH", "Tên KH", "Số đơn ghi nợ", "Tổng tiền nợ lũy kế (VND)", "Đã thanh toán (VND)", "Còn nợ (VND)"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        styleModernTable(table);

        JScrollPane scrollTable = new JScrollPane(table);
        scrollTable.setPreferredSize(new Dimension(800, 220));

        // Gom nhóm Cards + Charts
        JPanel pnlTop = new JPanel(new BorderLayout(0, 15));
        pnlTop.setBackground(AppColor.BACKGROUND);
        pnlTop.add(pnlCards, BorderLayout.NORTH);
        pnlTop.add(chartContainer, BorderLayout.CENTER);

        // Chia đôi theo chiều dọc bằng JSplitPane
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, pnlTop, scrollTable);
        split.setResizeWeight(0.65);
        split.setBorder(null);

        add(pnlFilter, BorderLayout.NORTH);
        add(split, BorderLayout.CENTER);
    }

    private void loadData() {
        allData = thongKeBUS.getThongKeCongNo();
        filterData();
    }

    private void filterData() {
        String query = txtSearch.getText().trim().toLowerCase();
        filteredData = new ArrayList<>();

        for (ThongKeDTO.CongNo d : allData) {
            if (d.maKH.toLowerCase().contains(query) || d.tenKH.toLowerCase().contains(query)) {
                filteredData.add(d);
            }
        }

        updateUIComponents();
    }

    private void updateUIComponents() {
        // Cập nhật bảng dữ liệu
        tableModel.setRowCount(0);
        DecimalFormat df = new DecimalFormat("#,###");

        double totalTongTien = 0;
        double totalPaid = 0;
        double totalRemaining = 0;
        int totalOrders = 0;

        for (ThongKeDTO.CongNo d : filteredData) {
            tableModel.addRow(new Object[]{
                    d.maKH,
                    d.tenKH,
                    d.soDonGhiNo,
                    df.format(d.tongTien),
                    df.format(d.daThanhToan),
                    df.format(d.conNo)
            });

            totalTongTien += d.tongTien;
            totalPaid += d.daThanhToan;
            totalRemaining += d.conNo;
            totalOrders += d.soDonGhiNo;
        }

        // Thêm dòng TỔNG CỘNG ở cuối bảng nếu có dữ liệu
        if (!filteredData.isEmpty()) {
            tableModel.addRow(new Object[]{
                    "TỔNG CỘNG",
                    "",
                    totalOrders,
                    df.format(totalTongTien),
                    df.format(totalPaid),
                    df.format(totalRemaining)
            });
        }

        // Cập nhật KPI Cards
        lblTotalDebt.setText(df.format(totalRemaining) + " VNĐ");
        lblCustomerCount.setText(String.valueOf(filteredData.size()));
        
        double paymentRate = 0.0;
        if (totalTongTien > 0) {
            paymentRate = (totalPaid / totalTongTien) * 100;
        }
        lblPaymentRate.setText(new DecimalFormat("0.0").format(paymentRate) + "%");

        // Cập nhật Biểu đồ
        updateCharts(totalPaid, totalRemaining);
    }

    private void updateCharts(double totalPaid, double totalRemaining) {
        chartContainer.removeAll();

        if (filteredData.isEmpty()) {
            // Xử lý khi không có dữ liệu
            JPanel pnlEmpty = new JPanel(new GridBagLayout());
            pnlEmpty.setBackground(Color.WHITE);
            pnlEmpty.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
            JLabel lblMsg = new JLabel("Không tìm thấy dữ liệu thống kê công nợ phù hợp.");
            lblMsg.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            lblMsg.setForeground(Color.GRAY);
            pnlEmpty.add(lblMsg);
            chartContainer.add(pnlEmpty);
            chartContainer.add(new JPanel()); // Giữ layout cân đối
            chartContainer.revalidate();
            chartContainer.repaint();
            return;
        }

        // --- BIỂU ĐỒ TRÒN: TỶ LỆ THANH TOÁN ---
        DefaultPieDataset pieDataset = new DefaultPieDataset();
        pieDataset.setValue("Đã thanh toán", totalPaid);
        pieDataset.setValue("Còn nợ", totalRemaining);

        pieChart = ChartFactory.createPieChart(
                "Tỷ lệ Thanh toán / Công nợ",
                pieDataset,
                true,
                true,
                false
        );
        pieChart.setBackgroundPaint(Color.WHITE);
        pieChart.getTitle().setFont(new Font("Segoe UI", Font.BOLD, 15));
        
        PiePlot plotPie = (PiePlot) pieChart.getPlot();
        plotPie.setBackgroundPaint(Color.WHITE);
        plotPie.setOutlineVisible(false);
        plotPie.setSectionPaint("Đã thanh toán", new Color(40, 167, 69)); // Xanh lá
        plotPie.setSectionPaint("Còn nợ", new Color(220, 53, 69));      // Đỏ

        ChartPanel pnlPie = new ChartPanel(pieChart);
        pnlPie.setBackground(Color.WHITE);
        pnlPie.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));

        // --- BIỂU ĐỒ CỘT: TOP 5 KHÁCH NỢ ---
        DefaultCategoryDataset barDataset = new DefaultCategoryDataset();
        
        // Sắp xếp và lấy Top 5 khách nợ nhiều nhất từ danh sách hiện tại
        List<ThongKeDTO.CongNo> sortedList = new ArrayList<>(filteredData);
        sortedList.sort((o1, o2) -> Double.compare(o2.conNo, o1.conNo));

        for (int i = 0; i < Math.min(5, sortedList.size()); i++) {
            ThongKeDTO.CongNo item = sortedList.get(i);
            // Cắt ngắn tên khách hàng nếu quá dài để hiển thị biểu đồ đẹp hơn
            String shortName = item.tenKH;
            if (shortName.length() > 12) {
                shortName = shortName.substring(0, 10) + "..";
            }
            barDataset.addValue(item.conNo, "Còn nợ (VNĐ)", shortName);
        }

        barChart = ChartFactory.createBarChart(
                "Top 5 khách nợ nhiều nhất",
                "Khách hàng",
                "Số tiền nợ (VNĐ)",
                barDataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false
        );
        barChart.setBackgroundPaint(Color.WHITE);
        barChart.getTitle().setFont(new Font("Segoe UI", Font.BOLD, 15));

        CategoryPlot plotBar = barChart.getCategoryPlot();
        plotBar.setBackgroundPaint(Color.WHITE);
        plotBar.setRangeGridlinePaint(new Color(220, 220, 220));
        plotBar.setOutlineVisible(false);

        // Định dạng trục Y hiển thị số thu gọn
        org.jfree.chart.axis.NumberAxis rangeAxis = (org.jfree.chart.axis.NumberAxis) plotBar.getRangeAxis();
        rangeAxis.setNumberFormatOverride(new java.text.DecimalFormat("#,###") {
            @Override
            public StringBuffer format(double number, StringBuffer result, java.text.FieldPosition fieldPosition) {
                if (number >= 1000000) {
                    return super.format(number / 1000000.0, result, fieldPosition).append(" Tr");
                }
                return super.format(number, result, fieldPosition);
            }
        });

        // Thiết lập màu sắc cột bar
        BarRenderer renderer = (BarRenderer) plotBar.getRenderer();
        renderer.setSeriesPaint(0, new Color(220, 53, 69)); // Màu đỏ thống nhất với công nợ
        renderer.setMaximumBarWidth(0.15);

        ChartPanel pnlBar = new ChartPanel(barChart);
        pnlBar.setBackground(Color.WHITE);
        pnlBar.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));

        // Add 2 biểu đồ vào Panel Container xếp ngang
        chartContainer.add(pnlPie);
        chartContainer.add(pnlBar);
        chartContainer.revalidate();
        chartContainer.repaint();
    }

    private void exportToPDF() {
        if (filteredData.isEmpty() || pieChart == null || barChart == null) {
            JOptionPane.showMessageDialog(this, "Không có dữ liệu để xuất!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn vị trí lưu tệp PDF");
        fileChooser.setSelectedFile(new File("BaoCaoCongNoKhachHang.pdf"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".pdf")) {
                filePath += ".pdf";
            }

            try {
                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(filePath));
                document.open();

                // Nạp font Arial hỗ trợ tiếng Việt
                BaseFont bf = BaseFont.createFont("c:/windows/fonts/arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                com.itextpdf.text.Font fBoldHeader = new com.itextpdf.text.Font(bf, 16, com.itextpdf.text.Font.BOLD, new com.itextpdf.text.BaseColor(AppColor.PRIMARY.getRGB()));
                com.itextpdf.text.Font fBold = new com.itextpdf.text.Font(bf, 11, com.itextpdf.text.Font.BOLD);
                com.itextpdf.text.Font fNormal = new com.itextpdf.text.Font(bf, 10, com.itextpdf.text.Font.NORMAL);
                com.itextpdf.text.Font fWhiteBold = new com.itextpdf.text.Font(bf, 10, com.itextpdf.text.Font.BOLD, com.itextpdf.text.BaseColor.WHITE);

                // --- 1. TIÊU ĐỀ BÁO CÁO ---
                Paragraph pTitle = new Paragraph("BÁO CÁO THỐNG KÊ CÔNG NỢ KHÁCH HÀNG", fBoldHeader);
                pTitle.setAlignment(Element.ALIGN_CENTER);
                pTitle.setSpacingAfter(5f);
                document.add(pTitle);

                Paragraph pSub = new Paragraph("Ngày xuất báo cáo: " + new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new java.util.Date()), fNormal);
                pSub.setAlignment(Element.ALIGN_CENTER);
                pSub.setSpacingAfter(20f);
                document.add(pSub);

                // --- 2. KPI SUMMARY TEXT ---
                Paragraph pSummary = new Paragraph();
                pSummary.setFont(fBold);
                pSummary.add("Tổng công nợ hiện tại: " + lblTotalDebt.getText() + "   |   " +
                             "Số khách nợ: " + lblCustomerCount.getText() + "   |   " +
                             "Tỷ lệ đã thanh toán: " + lblPaymentRate.getText() + "\n");
                pSummary.setSpacingAfter(15f);
                pSummary.setAlignment(Element.ALIGN_CENTER);
                document.add(pSummary);

                // --- 3. BIỂU ĐỒ (NHÚNG 2 BIỂU ĐỒ VÀO PDF) ---
                // Biểu đồ tròn
                java.awt.image.BufferedImage imgPie = pieChart.createBufferedImage(500, 260);
                com.itextpdf.text.Image pdfImgPie = com.itextpdf.text.Image.getInstance(imgPie, null);
                pdfImgPie.setAlignment(Element.ALIGN_CENTER);
                pdfImgPie.setSpacingAfter(15f);
                document.add(pdfImgPie);

                // Biểu đồ cột
                java.awt.image.BufferedImage imgBar = barChart.createBufferedImage(500, 260);
                com.itextpdf.text.Image pdfImgBar = com.itextpdf.text.Image.getInstance(imgBar, null);
                pdfImgBar.setAlignment(Element.ALIGN_CENTER);
                pdfImgBar.setSpacingAfter(20f);
                document.add(pdfImgBar);

                // --- 4. BẢNG DỮ LIỆU CHI TIẾT ---
                PdfPTable pdfTable = new PdfPTable(tableModel.getColumnCount());
                pdfTable.setWidthPercentage(100);
                pdfTable.setSpacingBefore(10f);
                
                // Thiết lập tỷ lệ độ rộng các cột (Mã KH, Tên KH, Số đơn, Tổng tiền, Đã TT, Còn nợ)
                pdfTable.setWidths(new float[]{1.5f, 2.5f, 1.5f, 2.2f, 2.2f, 2.2f});

                // Header Table
                for (int i = 0; i < tableModel.getColumnCount(); i++) {
                    PdfPCell cell = new PdfPCell(new Phrase(tableModel.getColumnName(i), fWhiteBold));
                    cell.setBackgroundColor(new com.itextpdf.text.BaseColor(AppColor.PRIMARY.getRGB()));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setPadding(8f);
                    pdfTable.addCell(cell);
                }

                // Data rows (bao gồm cả dòng tổng kết)
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    boolean isTotalRow = i == tableModel.getRowCount() - 1;
                    com.itextpdf.text.Font rowFont = isTotalRow ? fBold : fNormal;
                    com.itextpdf.text.BaseColor bgRowColor = isTotalRow ? new com.itextpdf.text.BaseColor(240, 245, 255) : com.itextpdf.text.BaseColor.WHITE;

                    for (int j = 0; j < tableModel.getColumnCount(); j++) {
                        PdfPCell cell = new PdfPCell(new Phrase(tableModel.getValueAt(i, j).toString(), rowFont));
                        cell.setBackgroundColor(bgRowColor);
                        
                        // Cột text căn trái, cột số căn phải, cột mã căn center
                        if (j == 0 || j == 1) {
                            cell.setHorizontalAlignment(isTotalRow && j == 0 ? Element.ALIGN_CENTER : Element.ALIGN_LEFT);
                        } else {
                            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        }
                        
                        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        cell.setPadding(6f);
                        pdfTable.addCell(cell);
                    }
                }

                document.add(pdfTable);
                document.close();

                JOptionPane.showMessageDialog(this, "Xuất báo cáo PDF thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                Desktop.getDesktop().open(new File(filePath));
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi xảy ra khi xuất PDF: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void styleModernTable(JTable tb) {
        tb.setRowHeight(35);
        tb.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                // Dòng tổng cộng chữ đậm, nền nhạt màu xanh
                if (row == table.getRowCount() - 1) {
                    c.setFont(new Font("Segoe UI", Font.BOLD, 13));
                    c.setBackground(new Color(235, 243, 250));
                    c.setForeground(Color.BLACK);
                } else {
                    c.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                    if (isSelected) {
                        c.setBackground(new Color(184, 207, 229));
                        c.setForeground(Color.BLACK);
                    } else {
                        c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 250, 250));
                        c.setForeground(Color.BLACK);
                    }
                }
                
                // Căn lề
                if (column == 0 || column == 2) {
                    setHorizontalAlignment(JLabel.CENTER);
                } else if (column == 1) {
                    setHorizontalAlignment(JLabel.LEFT);
                } else {
                    setHorizontalAlignment(JLabel.RIGHT);
                }
                
                return c;
            }
        };

        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setBackground(AppColor.PRIMARY);
        headerRenderer.setForeground(Color.WHITE);
        headerRenderer.setHorizontalAlignment(JLabel.CENTER);
        headerRenderer.setFont(new Font("Segoe UI", Font.BOLD, 13));

        for (int i = 0; i < tb.getColumnCount(); i++) {
            tb.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
            tb.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
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
        
        // Hiệu ứng hover nhạt/đậm màu
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(bg.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(bg);
            }
        });
    }

    private JPanel createCardPanel(String title, JLabel lblValue, Color valueColor) {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.setBackground(AppColor.SURFACE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitle.setForeground(AppColor.TEXT_SECONDARY);

        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblValue.setForeground(valueColor);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.CENTER);

        return card;
    }
}
