package gui.dialog;

import bus.TonKhoBUS;
import com.formdev.flatlaf.FlatClientProperties;
import util.AppColor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.ArrayList;

public class ChiTietTonKhoDialog extends JDialog {
    private String tenSP;
    private String maSP;
    private double tongSoLuong;
    private TonKhoBUS tonKhoBUS;
    private JTable table;
    private DefaultTableModel tableModel;

    public ChiTietTonKhoDialog(Frame parent, String tenSP, String maSP, double tongSoLuong, TonKhoBUS bus) {
        super(parent, "Chi Tiết Tồn Kho", true);
        this.tenSP = tenSP;
        this.maSP = maSP;
        this.tongSoLuong = tongSoLuong;
        this.tonKhoBUS = bus;
        initComponents();
        loadData();
    }

    private void initComponents() {
        this.setSize(950, 500);
        this.setLocationRelativeTo(getParent());
        this.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(AppColor.BACKGROUND);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Format số lượng (loại bỏ .0 nếu là số nguyên)
        String slStr = (tongSoLuong == (long) tongSoLuong) ? String.valueOf((long) tongSoLuong)
                : String.valueOf(tongSoLuong);

        // Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        headerPanel.setBackground(AppColor.BACKGROUND);
        JLabel lblTitle = new JLabel("Chi tiết Tồn kho: " + tenSP + " (" + maSP + ") - Tổng: " + slStr);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(AppColor.TEXT_PRIMARY);
        headerPanel.add(lblTitle);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Table
        String[] columns = { "STT", "Vị trí Kho", "Mã vị trí", "Số lượng tại vị trí", "Thời gian nhập",
                "Thời gian hết hạn" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(40);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(AppColor.BORDER);
        table.setSelectionBackground(
                new Color(AppColor.PRIMARY.getRed(), AppColor.PRIMARY.getGreen(), AppColor.PRIMARY.getBlue(), 30));
        table.setSelectionForeground(AppColor.PRIMARY_ACTIVE);
        table.setFocusable(false);

        JTableHeader th = table.getTableHeader();
        th.setFont(new Font("Segoe UI", Font.BOLD, 13));
        th.setPreferredSize(new Dimension(0, 40));
        th.setReorderingAllowed(false);

        // Canh lề và style Header (Giống TonKhoPanel)
        DefaultTableCellRenderer hr = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                l.setFont(new Font("Segoe UI", Font.BOLD, 13));
                l.setForeground(AppColor.TEXT_SECONDARY);
                l.setBackground(AppColor.BACKGROUND);
                if (c == 0 || c == 4 || c == 5)
                    l.setHorizontalAlignment(SwingConstants.CENTER);
                else
                    l.setHorizontalAlignment(SwingConstants.LEFT);
                l.setBorder(new EmptyBorder(0, 10, 0, 10)); // Giảm padding lề header xuống 10
                return l;
            }
        };
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setHeaderRenderer(hr);
        }

        class CustomCellRenderer extends DefaultTableCellRenderer {
            private int align;

            public CustomCellRenderer(int align) {
                this.align = align;
            }

            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                l.setHorizontalAlignment(align);
                l.setBorder(new EmptyBorder(0, 15, 0, 15));
                if (c == 1) { // Vị trí Kho
                    l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                    l.setForeground(sel ? AppColor.PRIMARY_ACTIVE : AppColor.TEXT_PRIMARY);
                }
                return l;
            }
        }

        CustomCellRenderer left = new CustomCellRenderer(SwingConstants.LEFT);
        CustomCellRenderer center = new CustomCellRenderer(SwingConstants.CENTER);

        table.getColumnModel().getColumn(0).setCellRenderer(center); // STT
        table.getColumnModel().getColumn(0).setPreferredWidth(60);

        table.getColumnModel().getColumn(1).setCellRenderer(left); // Vị trí kho (Bold/Primary)
        table.getColumnModel().getColumn(1).setPreferredWidth(260);

        table.getColumnModel().getColumn(2).setCellRenderer(left); // Mã vị trí
        table.getColumnModel().getColumn(2).setPreferredWidth(100);

        table.getColumnModel().getColumn(3).setCellRenderer(left); // Số lượng
        table.getColumnModel().getColumn(3).setPreferredWidth(140);

        table.getColumnModel().getColumn(4).setCellRenderer(center); // Cập nhật
        table.getColumnModel().getColumn(4).setPreferredWidth(170);

        table.getColumnModel().getColumn(5).setCellRenderer(center); // Hết hạn
        table.getColumnModel().getColumn(5).setPreferredWidth(170);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(AppColor.SURFACE);

        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBackground(AppColor.SURFACE);
        tableContainer.setBorder(new LineBorder(AppColor.BORDER, 1, true));
        tableContainer.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(tableContainer, BorderLayout.CENTER);

        // Bottom
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        bottomPanel.setBackground(AppColor.BACKGROUND);

        JButton btnDeleteExpired = new JButton("Xóa SP Hết Hạn");
        btnDeleteExpired.setPreferredSize(new Dimension(140, 38));
        btnDeleteExpired.setBackground(AppColor.ERROR);
        btnDeleteExpired.setForeground(Color.WHITE);
        btnDeleteExpired.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnDeleteExpired.setFocusPainted(false);
        btnDeleteExpired.setBorderPainted(false);
        btnDeleteExpired.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDeleteExpired.putClientProperty(FlatClientProperties.STYLE, "arc: 10");
        btnDeleteExpired.addActionListener(e -> deleteExpiredProducts());

        JButton btnClose = new JButton("Đóng");
        btnClose.setPreferredSize(new Dimension(100, 38));
        btnClose.setBackground(AppColor.PRIMARY);
        btnClose.setForeground(Color.WHITE);
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnClose.setFocusPainted(false);
        btnClose.setBorderPainted(false);
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClose.putClientProperty(FlatClientProperties.STYLE, "arc: 10");
        btnClose.addActionListener(e -> this.dispose());

        bottomPanel.add(btnDeleteExpired);
        bottomPanel.add(btnClose);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        this.add(mainPanel);
    }

    private void loadData() {
        tableModel.setRowCount(0);
        ArrayList<Object[]> chiTietList = tonKhoBUS.getChiTietTonKhoByMaSP(maSP);
        int stt = 1;
        for (Object[] row : chiTietList) {
            String viTriKho = row[0] != null ? row[0].toString() : "";
            String maViTri = row[1] != null ? row[1].toString() : "";
            double sl = row[2] != null ? (double) row[2] : 0;
            String soLuongStr = (sl == (long) sl) ? String.valueOf((long) sl) : String.valueOf(sl);
            String tgCapNhat = row[3] != null ? row[3].toString() : "";
            String tgHetHan = row[4] != null ? row[4].toString() : "";
            String trangThai = row.length > 5 && row[5] != null ? row[5].toString() : "Còn hạn";

            // Thêm dấu hiệu nhận biết đỏ/vàng bằng HTML
            if (trangThai.equals("Hết hạn")) {
                tgHetHan = "<html><font color='red'><b>" + tgHetHan + " (Hết hạn)</b></font></html>";
            } else if (trangThai.equals("Sắp hết hạn")) {
                tgHetHan = "<html><font color='#d97706'><b>" + tgHetHan + " (Sắp hết)</b></font></html>"; // Màu cam
                                                                                                          // vàng
            }

            tableModel.addRow(new Object[] { stt++, viTriKho, maViTri, soLuongStr, tgCapNhat, tgHetHan });
        }
    }

    private void deleteExpiredProducts() {
        ArrayList<Object[]> chiTietList = tonKhoBUS.getChiTietTonKhoByMaSP(maSP);
        boolean hasExpired = false;
        boolean allSuccess = true;

        for (Object[] row : chiTietList) {
            String trangThai = row.length > 5 && row[5] != null ? row[5].toString() : "";
            if ("Hết hạn".equals(trangThai)) {
                hasExpired = true;
                String maTonKho = row.length > 6 && row[6] != null ? row[6].toString() : "";
                if (!maTonKho.isEmpty()) {
                    boolean ok = tonKhoBUS.xoaTonKho(maTonKho);
                    if (!ok)
                        allSuccess = false;
                }
            }
        }

        if (!hasExpired) {
            JOptionPane.showMessageDialog(this, "Không có sản phẩm nào hết hạn để xóa!", "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if (allSuccess) {
            JOptionPane.showMessageDialog(this, "Đã xóa (cập nhật số lượng về 0) các sản phẩm hết hạn thành công!",
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadData();
        } else {
            JOptionPane.showMessageDialog(this, "Có lỗi xảy ra khi xóa một số sản phẩm hết hạn.", "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            loadData();
        }
    }
}
