package gui.dialog;

import bus.XuatKhoBUS;
import dto.XuatKhoDTO;
import util.AppColor;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class SoanHangDialog extends JDialog {

    private final String maDH;
    private JTable table;
    private JComboBox<String> cbNhanVien;
    private JButton btnXacNhan;

    public SoanHangDialog(Frame parent, String maDH) {
        super(parent, "Xác nhận xuất hàng", true);
        this.maDH = maDH;
        initUI();
        loadData();
    }

    private void initUI() {
        setSize(1000, 620);
        setMinimumSize(new Dimension(880, 540));
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        getContentPane().setBackground(AppColor.SURFACE);

        add(buildHeader(), BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);
    }

    private JPanel buildHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(AppColor.SURFACE);
        panel.setBorder(new EmptyBorder(20, 24, 14, 24));

        JLabel title = new JLabel("Chi tiết xuất kho - Đơn hàng: " + maDH);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(AppColor.TEXT_PRIMARY);
        panel.add(title, BorderLayout.WEST);
        return panel;
    }

    private JPanel buildTablePanel() {
        String[] columns = {
                "STT", "Mã SP", "Tên sản phẩm", "SL yêu cầu",
                "SL xuất", "Vị trí lấy hàng", "Ngày hết hạn"
        };

        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? AppColor.BACKGROUND : AppColor.SECONDARY_HOVER);
                }
                return c;
            }
        };
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(38);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(false);
        table.setGridColor(AppColor.BORDER);
        table.setSelectionBackground(new Color(22, 163, 74, 28));
        table.setSelectionForeground(AppColor.PRIMARY_ACTIVE);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFocusable(false);

        JTableHeader header = table.getTableHeader();
        header.setPreferredSize(new Dimension(0, 42));
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(AppColor.BACKGROUND);
        header.setForeground(AppColor.TEXT_SECONDARY);
        header.setReorderingAllowed(false);
        header.setBorder(new MatteBorder(0, 0, 1, 0, AppColor.BORDER));

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        int[] widths = { 50, 95, 270, 110, 90, 170, 130 };
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
            if (i == 0 || i == 1 || i == 3 || i == 4) {
                table.getColumnModel().getColumn(i).setCellRenderer(center);
            }
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(AppColor.SURFACE);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(AppColor.SURFACE);
        panel.setBorder(new EmptyBorder(0, 24, 12, 24));
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildFooter() {
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBackground(AppColor.SURFACE);
        wrap.setBorder(new EmptyBorder(0, 24, 18, 24));

        JPanel employee = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 10));
        employee.setOpaque(false);
        JLabel label = new JLabel("Nhân viên xác nhận:");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(AppColor.TEXT_PRIMARY);

        cbNhanVien = new JComboBox<>();
        cbNhanVien.addItem("");
        try {
            for (String maNV : new XuatKhoBUS().getAllMaNhanVien()) {
                cbNhanVien.addItem(maNV);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Không thể tải danh sách nhân viên: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
        cbNhanVien.setPreferredSize(new Dimension(240, 34));

        employee.add(label);
        employee.add(cbNhanVien);
        wrap.add(employee, BorderLayout.NORTH);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        actions.setBackground(AppColor.SURFACE);
        actions.setBorder(new MatteBorder(1, 0, 0, 0, AppColor.BORDER));

        btnXacNhan = primaryButton("Xác nhận xuất hàng", 170);
        btnXacNhan.addActionListener(e -> onXacNhan());

        JButton btnBack = outlineButton("Quay lại", 95);
        btnBack.addActionListener(e -> dispose());

        actions.add(btnXacNhan);
        actions.add(btnBack);
        wrap.add(actions, BorderLayout.SOUTH);
        return wrap;
    }

    private void loadData() {
        try {
            List<Object[]> rows = new XuatKhoBUS().getChiTietDonHang(maDH);
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setRowCount(0);

            int stt = 1;
            for (Object[] row : rows) {
                model.addRow(new Object[] { stt++, row[0], row[1], row[2], row[3], row[4], row[5] });
            }

            if (rows.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Không tìm thấy sản phẩm nào trong đơn hàng: " + maDH,
                        "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi tải dữ liệu: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onXacNhan() {
        String selected = cbNhanVien.getSelectedItem() == null ? "" : cbNhanVien.getSelectedItem().toString().trim();
        if (selected.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn nhân viên thực hiện!",
                    "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String maNV = selected;
        if (selected.contains(" - ")) {
            maNV = selected.substring(selected.lastIndexOf(" - ") + 3).trim();
        }

        btnXacNhan.setEnabled(false);
        btnXacNhan.setText("Đang xử lý...");

        try {
            XuatKhoBUS bus = new XuatKhoBUS();
            String yeuCau = bus.yeuCauXuatKho(maDH);
            if (!"SUCCESS".equals(yeuCau)) {
                JOptionPane.showMessageDialog(this, yeuCau,
                        "Không thể phân bổ hàng", JOptionPane.WARNING_MESSAGE);
                resetButton();
                return;
            }

            XuatKhoDTO dto = new XuatKhoDTO();
            dto.setMaXK(maDH);
            dto.setMaNV(maNV);

            String result = bus.xacNhanSoanHang(dto);
            if ("SUCCESS".equals(result)) {
                JOptionPane.showMessageDialog(this,
                        "Xuất kho thành công đơn hàng: " + maDH,
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, result,
                        "Không thể xác nhận", JOptionPane.WARNING_MESSAGE);
                resetButton();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi hệ thống: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            resetButton();
        }
    }

    private void resetButton() {
        btnXacNhan.setEnabled(true);
        btnXacNhan.setText("Xác nhận xuất hàng");
    }

    private JButton primaryButton(String text, int width) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(width, 36));
        btn.setBackground(AppColor.PRIMARY);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton outlineButton(String text, int width) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(width, 36));
        btn.setBackground(AppColor.SURFACE);
        btn.setForeground(AppColor.TEXT_PRIMARY);
        btn.setFocusPainted(false);
        btn.setBorder(new LineBorder(AppColor.BORDER));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
