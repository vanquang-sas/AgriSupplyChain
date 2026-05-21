package gui.panel.BaoCaoThongKe;

import bus.ThongKeBUS;
import dto.ThongKeDTO;
import util.AppColor;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
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
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.List;

public class NhanVienPanel extends JPanel {
    private ThongKeBUS thongKeBUS = new ThongKeBUS();
    
    private JComboBox<String> cbBoPhan;
    private JComboBox<String> cbThoiGian;
    
    private JPanel summaryPanel, chartTopPanel, chartDetailPanel;
    private JLabel lblCard1Title, lblCard1Value, lblCard2Title, lblCard2Value, lblCard3Title, lblCard3Value;
    
    private JTable table;
    private DefaultTableModel tableModel;
    private JFreeChart currentTopChart, currentDetailChart;
    
    private List<ThongKeDTO.NhanVienThongKe> lastData;
    private DecimalFormat dfMoney = new DecimalFormat("#,###");

    public NhanVienPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(AppColor.BACKGROUND);
        setBorder(new EmptyBorder(15, 15, 15, 15));
        initComponents();
        refreshData();
    }

    private void initComponents() {
        // --- BỘ LỌC ---
        JPanel pnlFilter = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        pnlFilter.setBackground(Color.WHITE);
        pnlFilter.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true));

        cbBoPhan = new JComboBox<>(new String[]{"Tất cả", "NV thu mua", "NV kho", "NV giao hàng"});
        cbThoiGian = new JComboBox<>(new String[]{"3 tháng gần đây", "6 tháng gần đây", "12 tháng gần đây"});
        
        JButton btnFilter = new JButton("Thống kê");
        styleButton(btnFilter, AppColor.PRIMARY);
        btnFilter.addActionListener(e -> refreshData());

        JButton btnExport = new JButton("Xuất PDF");
        styleButton(btnExport, new Color(220, 38, 38));
        btnExport.addActionListener(e -> exportToPDF());

        pnlFilter.add(new JLabel("Bộ phận:")); pnlFilter.add(cbBoPhan);
        pnlFilter.add(new JLabel("Thời gian:")); pnlFilter.add(cbThoiGian);
        pnlFilter.add(btnFilter); pnlFilter.add(btnExport);

        // --- SUMMARY CARDS ---
        summaryPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        summaryPanel.setBackground(AppColor.BACKGROUND);
        summaryPanel.setPreferredSize(new Dimension(0, 95));
        
        summaryPanel.add(createSummaryCard(1));
        summaryPanel.add(createSummaryCard(2));
        summaryPanel.add(createSummaryCard(3));

        // --- BIỂU ĐỒ ---
        JPanel chartsContainer = new JPanel(new GridLayout(1, 2, 15, 0));
        chartsContainer.setBackground(AppColor.BACKGROUND);
        chartsContainer.setPreferredSize(new Dimension(0, 320));
        
        chartTopPanel = new JPanel(new BorderLayout());
        chartTopPanel.setBackground(Color.WHITE);
        chartTopPanel.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true));
        
        chartDetailPanel = new JPanel(new BorderLayout());
        chartDetailPanel.setBackground(Color.WHITE);
        chartDetailPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true), 
            "Chi tiết hiệu suất nhân viên", 0, 0, new Font("Segoe UI", Font.BOLD, 13), AppColor.PRIMARY
        ));

        chartsContainer.add(chartTopPanel);
        chartsContainer.add(chartDetailPanel);

        // --- BẢNG SỐ LIỆU ---
        String[] cols = {"Mã NV", "Tên NV", "Chức vụ", "Tổng công việc", "Lương", "Hành động"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c == 5; }
        };
        table = new JTable(tableModel);
        styleModernTable(table);
        JScrollPane scrollTable = new JScrollPane(table);
        scrollTable.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        scrollTable.setBackground(Color.WHITE);
        scrollTable.getViewport().setBackground(Color.WHITE);
        
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, chartsContainer, scrollTable);
        split.setResizeWeight(0.55);
        split.setBorder(null);
        split.setDividerSize(8);
        
        JPanel mainWrapper = new JPanel(new BorderLayout(0, 15));
        mainWrapper.setBackground(AppColor.BACKGROUND);
        mainWrapper.add(summaryPanel, BorderLayout.NORTH);
        mainWrapper.add(split, BorderLayout.CENTER);

        add(pnlFilter, BorderLayout.NORTH);
        add(mainWrapper, BorderLayout.CENTER);
    }

    private JPanel createSummaryCard(int index) {
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(225, 225, 225), 1, true),
            new EmptyBorder(15, 20, 15, 20)
        ));
        
        JLabel lblTitle = new JLabel("Title " + index);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitle.setForeground(Color.GRAY);
        
        JLabel lblValue = new JLabel("0");
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblValue.setForeground(AppColor.PRIMARY);
        
        if (index == 1) { lblCard1Title = lblTitle; lblCard1Value = lblValue; }
        else if (index == 2) { lblCard2Title = lblTitle; lblCard2Value = lblValue; }
        else { lblCard3Title = lblTitle; lblCard3Value = lblValue; }
        
        pnl.add(lblTitle, BorderLayout.NORTH);
        pnl.add(lblValue, BorderLayout.CENTER);
        return pnl;
    }

    private void refreshData() {
        String boPhanRaw = cbBoPhan.getSelectedItem().toString();
        int months = cbThoiGian.getSelectedIndex() == 0 ? 3 : (cbThoiGian.getSelectedIndex() == 1 ? 6 : 12);

        String dbChucVu = boPhanRaw;

        lastData = thongKeBUS.getDanhSachNhanVienThongKe(dbChucVu, months);
        
        updateSummaryCards(boPhanRaw, months);
        updateTable();
        updateTopChart(boPhanRaw);
        
        chartDetailPanel.removeAll();
        chartDetailPanel.revalidate();
        chartDetailPanel.repaint();
    }

    private void updateSummaryCards(String boPhan, int months) {
        int countNV = 0, countQL = 0, sumTasks = 0;
        double sumSalary = 0;
        
        for (ThongKeDTO.NhanVienThongKe nv : lastData) {
            if (nv.chucVu.equals("Quản lý")) countQL++;
            else countNV++;
            sumTasks += nv.tongCongViec;
            sumSalary += nv.luong;
        }
        int totalStaff = countNV + countQL;
        double avgSalary = totalStaff > 0 ? (sumSalary / totalStaff) : 0;

        if (boPhan.equals("Tất cả")) {
            lblCard1Title.setText("Quản lý"); lblCard1Value.setText(String.valueOf(countQL));
            lblCard2Title.setText("Nhân viên"); lblCard2Value.setText(String.valueOf(countNV));
        } else {
            lblCard1Title.setText("Tổng nhân viên " + boPhan); lblCard1Value.setText(String.valueOf(totalStaff));
            double perf = totalStaff > 0 ? (double) sumTasks / totalStaff / months : 0;
            lblCard2Title.setText("Hiệu suất (Lần/tháng)"); lblCard2Value.setText(String.format("%.1f", perf));
        }
        lblCard3Title.setText("Lương trung bình");
        lblCard3Value.setText(dfMoney.format(avgSalary) + " VNĐ");
    }

    private void updateTable() {
        tableModel.setRowCount(0);
        for (ThongKeDTO.NhanVienThongKe nv : lastData) {
            tableModel.addRow(new Object[]{
                nv.maNV, nv.tenNV, nv.chucVu, nv.tongCongViec, dfMoney.format(nv.luong), "Xem"
            });
        }
    }

    private void updateTopChart(String boPhan) {
        DefaultCategoryDataset ds = new DefaultCategoryDataset();
        List<ThongKeDTO.NhanVienThongKe> sorted = new java.util.ArrayList<>(lastData);
        sorted.sort((a, b) -> Double.compare(b.luong, a.luong));
        
        int count = 0;
        for (ThongKeDTO.NhanVienThongKe nv : sorted) {
            if (count >= 10) break;
            ds.addValue(nv.luong, "Lương", nv.tenNV);
            count++;
        }

        currentTopChart = ChartFactory.createBarChart("Top 10 Lương (" + boPhan + ")", "Nhân viên", "Lương (Triệu VNĐ)", ds, PlotOrientation.VERTICAL, false, true, false);
        currentTopChart.setBackgroundPaint(Color.WHITE);
        CategoryPlot plot = currentTopChart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(new Color(220, 220, 220));
        plot.setOutlineVisible(false);
        
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setNumberFormatOverride(new DecimalFormat("#,### M") {
            @Override public StringBuffer format(double number, StringBuffer res, java.text.FieldPosition pos) {
                return super.format(number / 1000000.0, res, pos);
            }
        });
        
        plot.getDomainAxis().setCategoryLabelPositions(org.jfree.chart.axis.CategoryLabelPositions.UP_45);
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, AppColor.PRIMARY);

        chartTopPanel.removeAll();
        chartTopPanel.add(new ChartPanel(currentTopChart), BorderLayout.CENTER);
        chartTopPanel.revalidate();
        chartTopPanel.repaint();
    }

    private void viewDetailChart(String maNV, String tenNV, String chucVu) {
        int months = cbThoiGian.getSelectedIndex() == 0 ? 3 : (cbThoiGian.getSelectedIndex() == 1 ? 6 : 12);
        List<ThongKeDTO.HieuSuatChiTiet> detailData = thongKeBUS.getHieuSuatNhanVien(maNV, months);
        
        CategoryPlot plot = new CategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(new Color(220, 220, 220));
        plot.setOutlineVisible(false);

        CategoryAxis domainAxis = new CategoryAxis("Tháng/Năm");
        plot.setDomainAxis(domainAxis);

        if (chucVu != null && chucVu.contains("kho")) {
            DefaultCategoryDataset dsQuantity = new DefaultCategoryDataset();
            for (ThongKeDTO.HieuSuatChiTiet d : detailData) {
                dsQuantity.addValue(d.soLuong, "Số lần xuất", d.thangNam);
            }
            NumberAxis rangeAxis = new NumberAxis("Số lần xuất");
            plot.setDataset(0, dsQuantity);
            plot.setRangeAxis(0, rangeAxis);
            BarRenderer barRenderer = new BarRenderer();
            barRenderer.setSeriesPaint(0, new Color(40, 167, 69));
            plot.setRenderer(0, barRenderer);
        } else {
            DefaultCategoryDataset dsMoney = new DefaultCategoryDataset();
            DefaultCategoryDataset dsQuantity = new DefaultCategoryDataset();
            for (ThongKeDTO.HieuSuatChiTiet d : detailData) {
                dsMoney.addValue(d.tongGiaTri, "Tổng Tiền", d.thangNam);
                dsQuantity.addValue(d.soLuong, "Số lượng CV", d.thangNam);
            }

            NumberAxis moneyAxis = new NumberAxis("Tổng Tiền (Triệu VNĐ)");
            moneyAxis.setNumberFormatOverride(new DecimalFormat("#,###") {
                @Override public StringBuffer format(double num, StringBuffer res, java.text.FieldPosition pos) {
                    return super.format(num / 1000000.0, res, pos);
                }
            });
            NumberAxis quantityAxis = new NumberAxis("Số lượng (Lần)");

            plot.setDataset(0, dsMoney);
            plot.setRangeAxis(0, moneyAxis);
            BarRenderer barRenderer = new BarRenderer();
            barRenderer.setSeriesPaint(0, new Color(0, 123, 255, 180));
            plot.setRenderer(0, barRenderer);
            plot.mapDatasetToRangeAxis(0, 0);

            plot.setDataset(1, dsQuantity);
            plot.setRangeAxis(1, quantityAxis);
            LineAndShapeRenderer lineRenderer = new LineAndShapeRenderer();
            lineRenderer.setSeriesPaint(0, new Color(220, 53, 69));
            lineRenderer.setSeriesStroke(0, new BasicStroke(3.0f));
            plot.setRenderer(1, lineRenderer);
            plot.mapDatasetToRangeAxis(1, 1);

            plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
        }

        currentDetailChart = new JFreeChart("Hiệu suất: " + tenNV, currentTopChart.getTitle().getFont(), plot, true);
        currentDetailChart.setBackgroundPaint(Color.WHITE);
        
        chartDetailPanel.removeAll();
        chartDetailPanel.add(new ChartPanel(currentDetailChart), BorderLayout.CENTER);
        chartDetailPanel.revalidate();
        chartDetailPanel.repaint();
    }

    private void styleModernTable(JTable tb) {
        tb.setRowHeight(40); // Tăng khoảng trống dòng lên 40px thoải mái hơn
        tb.setShowGrid(true);
        tb.setGridColor(new Color(240, 240, 240)); // Màu đường lưới mờ tinh tế
        tb.setSelectionBackground(new Color(232, 240, 254)); // Trùng màu highlight của KhachHangPanel
        tb.setSelectionForeground(Color.BLACK);

        // Custom Header phẳng đẹp thanh lịch
        JTableHeader header = tb.getTableHeader();
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBackground(AppColor.PRIMARY);
                setForeground(Color.WHITE);
                setFont(new Font("Segoe UI", Font.BOLD, 13));
                setHorizontalAlignment(JLabel.CENTER);
                setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, new Color(255, 255, 255, 50)));
                return this;
            }
        };
        header.setDefaultRenderer(headerRenderer);

        // Custom Zebra striping xen kẽ dòng trắng / xám nhạt
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.CENTER);
                setFont(new Font("Segoe UI", Font.PLAIN, 13));
                
                if (!isSelected) {
                    if (row % 2 == 0) {
                        setBackground(Color.WHITE);
                    } else {
                        setBackground(new Color(250, 250, 250)); // Màu xen kẽ đồng bộ KhachHangPanel
                    }
                }
                return this;
            }
        };

        for (int i = 0; i < tb.getColumnCount(); i++) {
            if (i != 5) tb.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }
        
        // Gán Render & Editor cho nút Hành động mới hiện đại hơn
        tb.getColumnModel().getColumn(5).setCellRenderer(new ModernButtonRenderer());
        tb.getColumnModel().getColumn(5).setCellEditor(new ModernButtonEditor(new JCheckBox()));
    }

    private void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg); btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false); btn.setBorderPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(110, 34));
    }

    private void exportToPDF() {
        if (tableModel.getRowCount() == 0) {
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

                Paragraph pTitle = new Paragraph("BÁO CÁO HIỆU QUẢ LÀM VIỆC NHÂN VIÊN", fBold);
                pTitle.setAlignment(Element.ALIGN_CENTER); pTitle.setSpacingAfter(20f);
                document.add(pTitle);

                if (currentTopChart != null) {
                    java.awt.image.BufferedImage imgTop = currentTopChart.createBufferedImage(500, 280);
                    com.itextpdf.text.Image pdfImgTop = com.itextpdf.text.Image.getInstance(imgTop, null);
                    pdfImgTop.setAlignment(Element.ALIGN_CENTER);
                    document.add(pdfImgTop);
                }
                
                if (currentDetailChart != null) {
                    java.awt.image.BufferedImage imgDet = currentDetailChart.createBufferedImage(500, 280);
                    com.itextpdf.text.Image pdfImgDet = com.itextpdf.text.Image.getInstance(imgDet, null);
                    pdfImgDet.setAlignment(Element.ALIGN_CENTER);
                    document.add(pdfImgDet);
                }

                PdfPTable pdfTable = new PdfPTable(5);
                pdfTable.setSpacingBefore(20f); pdfTable.setWidthPercentage(100);
                String[] pdfCols = {"Mã NV", "Tên NV", "Chức vụ", "Tổng công việc", "Lương"};
                
                for (String c : pdfCols) {
                    PdfPCell cell = new PdfPCell(new Phrase(c, fBold));
                    cell.setBackgroundColor(new com.itextpdf.text.BaseColor(AppColor.PRIMARY.getRGB()));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER); cell.setPadding(8f);
                    pdfTable.addCell(cell);
                }

                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    for (int j = 0; j < 5; j++) {
                        PdfPCell cell = new PdfPCell(new Phrase(tableModel.getValueAt(i, j).toString(), fNormal));
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER); cell.setPadding(6f);
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

    // --- RENDERER NÚT BẤM PHẲNG HIỆN ĐẠI (FLAT/ROUNDED STYLE) ---
    class ModernButtonRenderer extends JPanel implements TableCellRenderer {
        private final JButton button;

        public ModernButtonRenderer() {
            setLayout(new GridBagLayout());
            setOpaque(true);
            
            // Tạo nút bo góc tinh tế bằng kế thừa hoặc custom border padding
            button = new JButton("Xem") {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(getBackground());
                    g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            button.setFont(new Font("Segoe UI", Font.BOLD, 12));
            button.setForeground(Color.WHITE);
            button.setBackground(new Color(23, 162, 184)); // Màu lam Cyan sáng hiện đại
            button.setBorderPainted(false);
            button.setContentAreaFilled(false);
            button.setFocusPainted(false);
            button.setPreferredSize(new Dimension(75, 26));
            
            add(button);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setBackground(table.getSelectionBackground());
            } else {
                setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 250, 250));
            }
            return this;
        }
    }

    // --- EDITOR NÚT BẤM KHI TƯƠNG TÁC CLICK ---
    class ModernButtonEditor extends DefaultCellEditor {
        protected JPanel panel;
        protected JButton button;
        private String label;
        private boolean isPushed;
        private int clickedRow;

        public ModernButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            panel = new JPanel(new GridBagLayout());
            panel.setOpaque(true);

            button = new JButton("Xem") {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(getBackground());
                    g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            button.setFont(new Font("Segoe UI", Font.BOLD, 12));
            button.setForeground(Color.WHITE);
            button.setBackground(new Color(19, 134, 153)); // Màu tối hơn chút khi bấm xuống
            button.setBorderPainted(false);
            button.setContentAreaFilled(false);
            button.setFocusPainted(false);
            button.setPreferredSize(new Dimension(75, 26));
            button.addActionListener(e -> fireEditingStopped());
            
            panel.add(button);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            label = (value == null) ? "Xem" : value.toString();
            isPushed = true;
            clickedRow = row;
            panel.setBackground(table.getSelectionBackground());
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                String maNV = table.getValueAt(clickedRow, 0).toString();
                String tenNV = table.getValueAt(clickedRow, 1).toString();
                String chucVu = table.getValueAt(clickedRow, 2).toString();
                viewDetailChart(maNV, tenNV, chucVu);
            }
            isPushed = false;
            return label;
        }

        @Override
        public boolean stopCellEditing() { isPushed = false; return super.stopCellEditing(); }
    }
}