package gui.khohang;

import bus.XuatKhoBUS;
import dto.XuatKhoDTO;
import util.AppColor;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class XuatKhoDialog extends JDialog {

    private String maDH;
    private JTable tbDialog;
    private JComboBox<String> cbNV;
    private JButton btnXacNhanDialog;

    public XuatKhoDialog(Frame parent, String maDH) {
        super(parent, "Xác nhận xuất hàng", true);
        this.maDH = maDH;
        initUI();
        loadData();
    }

    private void initUI() {
        setSize(950, 600);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        getContentPane().setBackground(AppColor.SURFACE);

        // --- TOP ---
        JPanel pnlTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        pnlTop.setBackground(AppColor.SURFACE);
        JLabel lblDH = new JLabel("Xác nhận xuất kho Đơn hàng: " + maDH);
        lblDH.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblDH.setForeground(AppColor.TEXT_PRIMARY);
        pnlTop.add(lblDH);
        add(pnlTop, BorderLayout.NORTH);

        // --- CENTER (TABLE) ---
        String[] dialogCols = { "STT", "Mã SP", "Tên Sản phẩm", "Số lượng yêu cầu", "Số lượng xuất", "Vị trí lấy hàng", "Ngày hết hạn" };
        DefaultTableModel dialogModel = new DefaultTableModel(dialogCols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        tbDialog = new JTable(dialogModel);
        tbDialog.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tbDialog.setRowHeight(35);
        tbDialog.setShowVerticalLines(false);
        tbDialog.setShowHorizontalLines(true);
        tbDialog.setGridColor(AppColor.BORDER);
        tbDialog.setSelectionBackground(
                new Color(AppColor.PRIMARY.getRed(), AppColor.PRIMARY.getGreen(), AppColor.PRIMARY.getBlue(), 30));
        tbDialog.setSelectionForeground(AppColor.PRIMARY_ACTIVE);
        tbDialog.setFocusable(false);

        // Cấu hình độ rộng cột
        int[] columnWidths = { 50, 100, 250, 110, 110, 150, 120 };
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        
        for (int i = 0; i < columnWidths.length; i++) {
            if (i < tbDialog.getColumnCount()) {
                TableColumn col = tbDialog.getColumnModel().getColumn(i);
                col.setPreferredWidth(columnWidths[i]);
                // Căn giữa cho STT, Mã SP, Số lượng yêu cầu, Số lượng xuất
                if (i == 0 || i == 1 || i == 3 || i == 4) {
                    col.setCellRenderer(centerRenderer);
                }
            }
        }

        JTableHeader th = tbDialog.getTableHeader();
        th.setFont(new Font("Segoe UI", Font.BOLD, 12));
        th.setBackground(AppColor.BACKGROUND);
        th.setForeground(AppColor.TEXT_SECONDARY);
        th.setPreferredSize(new Dimension(0, 40));
        th.setBorder(new MatteBorder(0, 0, 1, 0, AppColor.BORDER));

        JScrollPane sp = new JScrollPane(tbDialog);
        sp.setBorder(new LineBorder(AppColor.BORDER));
        sp.getViewport().setBackground(AppColor.SURFACE);

        JPanel pnlCenter = new JPanel(new BorderLayout());
        pnlCenter.setBackground(AppColor.SURFACE);
        pnlCenter.setBorder(new EmptyBorder(0, 20, 10, 20));
        pnlCenter.add(sp, BorderLayout.CENTER);

        add(pnlCenter, BorderLayout.CENTER);

        // --- BOTTOM (COMBOBOX + BUTTONS) ---
        JPanel pnlBottomWrap = new JPanel(new BorderLayout());
        pnlBottomWrap.setBackground(AppColor.SURFACE);
        pnlBottomWrap.setBorder(new EmptyBorder(0, 20, 0, 20));

        JPanel pnlNV = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 10));
        pnlNV.setOpaque(false);
        JLabel lblNV = new JLabel("Nhân viên xác nhận: ");
        lblNV.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        java.util.Vector<String> nvList = new java.util.Vector<>();
        nvList.add("");
        try {
            nvList.addAll(new XuatKhoBUS().getAllMaNhanVien());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        cbNV = new JComboBox<>(nvList);
        styleCombo(cbNV);
        cbNV.setPreferredSize(new Dimension(250, 35));

        pnlNV.add(lblNV);
        pnlNV.add(cbNV);
        pnlBottomWrap.add(pnlNV, BorderLayout.NORTH);

        JPanel pnlActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        pnlActions.setBackground(AppColor.SURFACE);
        pnlActions.setBorder(new MatteBorder(1, 0, 0, 0, AppColor.BORDER));

        JButton btnHuy = buildOutlineBtn("Quay lại", 90);
        btnHuy.addActionListener(evt -> dispose());

        btnXacNhanDialog = buildFillBtn("Xác nhận xuất hàng", 170);
        btnXacNhanDialog.addActionListener(this::onXacNhan);

        pnlActions.add(btnXacNhanDialog);
        pnlActions.add(btnHuy);
        pnlBottomWrap.add(pnlActions, BorderLayout.SOUTH);

        add(pnlBottomWrap, BorderLayout.SOUTH);
    }

    private void loadData() {
        try {
            // Load chi tiết đơn hàng trực tiếp từ CHITIETDONHANG — không cần gọi
            // SP_YEUCAU_XUATKHO trước
            List<Object[]> items = new XuatKhoBUS().getChiTietDonHang(maDH);
            DefaultTableModel model = (DefaultTableModel) tbDialog.getModel();
            model.setRowCount(0);

            if (items.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Không tìm thấy sản phẩm nào trong đơn hàng: " + maDH,
                        "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int stt = 1;
            for (Object[] r : items) {
                model.addRow(new Object[] {
                        stt++,
                        r[0], // Mã SP
                        r[1], // Tên sản phẩm
                        r[2], // Số lượng yêu cầu
                        r[3], // Số lượng xuất
                        r[4], // Vị trí kho (FEFO preview)
                        r[5]  // Ngày hết hạn (FEFO preview)
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onXacNhan(ActionEvent evt) {
        String maNV = cbNV.getSelectedItem() == null ? "" : cbNV.getSelectedItem().toString();
        if (maNV.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên thực hiện!", "Thiếu thông tin",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        btnXacNhanDialog.setEnabled(false);
        btnXacNhanDialog.setText("Đang xử lý...");

        try {
            // Bước 1: Phân bổ hàng theo FEFO (tạo bản ghi Tạm giữ trong XUATKHO)
            String yeuCauResult = new XuatKhoBUS().yeuCauXuatKho(maDH);
            if (!"SUCCESS".equals(yeuCauResult)) {
                JOptionPane.showMessageDialog(this, yeuCauResult, "Không thể phân bổ hàng",
                        JOptionPane.WARNING_MESSAGE);
                btnXacNhanDialog.setEnabled(true);
                btnXacNhanDialog.setText("Xác nhận xuất hàng");
                return;
            }

            // Bước 2: Xác nhận xuất kho (chuyển Tạm giữ → Đã xuất, cập nhật TồnKho)
            XuatKhoDTO dto = new XuatKhoDTO();
            dto.setMaXK(maDH);
            dto.setMaNV(maNV);

            String xacNhanResult = new XuatKhoBUS().xacNhanSoanHang(dto);

            if ("SUCCESS".equals(xacNhanResult)) {
                JOptionPane.showMessageDialog(this,
                        "✅ Xuất kho thành công Đơn hàng: " + maDH,
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        xacNhanResult, "Không thể xác nhận", JOptionPane.WARNING_MESSAGE);
                btnXacNhanDialog.setEnabled(true);
                btnXacNhanDialog.setText("Xác nhận xuất hàng");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi hệ thống: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            btnXacNhanDialog.setEnabled(true);
            btnXacNhanDialog.setText("Xác nhận xuất hàng");
        }
    }

    private void styleCombo(JComboBox<?> cb) {
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cb.setBackground(AppColor.SURFACE);
        cb.setForeground(AppColor.TEXT_PRIMARY);
        cb.setFocusable(false);
        cb.setCursor(new Cursor(Cursor.HAND_CURSOR));
        ((JLabel) cb.getRenderer()).setBorder(new EmptyBorder(0, 8, 0, 0));
    }

    private JButton buildFillBtn(String text, int width) {
        JButton b = new JButton(text);
        b.setPreferredSize(new Dimension(width, 36));
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setBackground(AppColor.PRIMARY);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JButton buildOutlineBtn(String text, int width) {
        JButton b = new JButton(text);
        b.setPreferredSize(new Dimension(width, 36));
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setBackground(AppColor.SURFACE);
        b.setForeground(AppColor.PRIMARY);
        b.setFocusPainted(false);
        b.setBorder(new LineBorder(AppColor.PRIMARY, 1, true));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }
}
