package gui.panel;

import bus.LoHangBUS;
import dto.ChiTietLoHangDTO;
import dto.LoHangDTO;
import gui.dialog.LoHangForm;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;

public class LoHangPanel extends JPanel {

    private final LoHangBUS loHangBUS = new LoHangBUS();

    // Components
    private JTable tblLoHang;
    private JTable tblChiTiet;
    private DefaultTableModel modelLoHang;
    private DefaultTableModel modelChiTiet;
    private JButton btnThem, btnYeuCauNhapKho, btnLamMoi;
    private JLabel lblTongTien;

    private String maLoHangDangChon = null;

    public LoHangPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initComponents();
        loadData();
    }

    private void initComponents() {
        // ===== PANEL TIÊU ĐỀ =====
        JLabel lblTitle = new JLabel("QUẢN LÝ LÔ HÀNG NHẬP");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblTitle, BorderLayout.NORTH);

        // ===== PANEL CHÍNH (chia đôi) =====
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(250);
        splitPane.setResizeWeight(0.5);

        // --- Bảng trên: Danh sách lô hàng ---
        String[] colLoHang = {"Mã lô hàng", "Nhà cung cấp", "Ngày lập", "Trạng thái", "Tổng tiền"};
        modelLoHang = new DefaultTableModel(colLoHang, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblLoHang = new JTable(modelLoHang);
        tblLoHang.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblLoHang.setRowHeight(28);
        // Lắng nghe chọn dòng
        tblLoHang.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) onSelectLoHang();
        });

        JPanel panelTop = new JPanel(new BorderLayout());
        panelTop.add(new JLabel("  Danh sách lô hàng:"), BorderLayout.NORTH);
        panelTop.add(new JScrollPane(tblLoHang), BorderLayout.CENTER);
        splitPane.setTopComponent(panelTop);

        // --- Bảng dưới: Chi tiết lô hàng đang chọn ---
        String[] colCT = {"Mã NS", "Tên nông sản", "Số lượng", "Đơn giá", "Thành tiền"};
        modelChiTiet = new DefaultTableModel(colCT, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblChiTiet = new JTable(modelChiTiet);
        tblChiTiet.setRowHeight(28);

        lblTongTien = new JLabel("Tổng tiền lô hàng: 0 đ");
        lblTongTien.setFont(new Font("Arial", Font.BOLD, 14));
        lblTongTien.setForeground(Color.RED);

        JPanel panelBot = new JPanel(new BorderLayout());
        panelBot.add(new JLabel("  Chi tiết lô hàng đang chọn:"), BorderLayout.NORTH);
        panelBot.add(new JScrollPane(tblChiTiet), BorderLayout.CENTER);
        panelBot.add(lblTongTien, BorderLayout.SOUTH);
        splitPane.setBottomComponent(panelBot);

        add(splitPane, BorderLayout.CENTER);

        // ===== PANEL NÚT =====
        JPanel panelBtn = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        btnThem = new JButton("➕ Tạo lô hàng mới");
        btnYeuCauNhapKho = new JButton("📦 Yêu cầu nhập kho");
        btnLamMoi = new JButton("🔄 Làm mới");

        btnYeuCauNhapKho.setEnabled(false); // chỉ bật khi chọn lô hàng DANG_MUA

        panelBtn.add(btnThem);
        panelBtn.add(btnYeuCauNhapKho);
        panelBtn.add(btnLamMoi);
        add(panelBtn, BorderLayout.SOUTH);

        // ===== SỰ KIỆN =====
        btnThem.addActionListener(e -> onThemLoHang());
        btnYeuCauNhapKho.addActionListener(e -> onYeuCauNhapKho());
        btnLamMoi.addActionListener(e -> loadData());
    }

    // Load danh sách lô hàng lên bảng
    private void loadData() {
        try {
            modelLoHang.setRowCount(0);
            List<LoHangDTO> list = loHangBUS.getAllLoHang();
            DecimalFormat df = new DecimalFormat("#,###");
            for (LoHangDTO lh : list) {
                modelLoHang.addRow(new Object[]{
                        lh.getMaLoHang(),
                        lh.getTenNCC(),
                        lh.getNgayLap(),
                        lh.getTrangThai(),
                        df.format(lh.getTongTien()) + " đ"
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi tải dữ liệu: " + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Khi chọn 1 dòng trên bảng lô hàng
    private void onSelectLoHang() {
        int row = tblLoHang.getSelectedRow();
        if (row < 0) return;

        maLoHangDangChon = (String) modelLoHang.getValueAt(row, 0);
        String trangThai = (String) modelLoHang.getValueAt(row, 3);

        // Chỉ cho phép "Yêu cầu nhập kho" khi trạng thái là DANG_MUA
        btnYeuCauNhapKho.setEnabled("DANG_MUA".equals(trangThai));

        // Load chi tiết
        loadChiTiet(maLoHangDangChon);
    }

    private void loadChiTiet(String maLoHang) {
        try {
            modelChiTiet.setRowCount(0);
            List<ChiTietLoHangDTO> list = loHangBUS.getChiTietLoHang(maLoHang);
            DecimalFormat df = new DecimalFormat("#,###");
            double tong = 0;
            for (ChiTietLoHangDTO ct : list) {
                modelChiTiet.addRow(new Object[]{
                        ct.getMaNS(),
                        ct.getTenNS(),
                        ct.getSoLuong(),
                        df.format(ct.getDonGia()) + " đ",
                        df.format(ct.getThanhTien()) + " đ"
                });
                tong += ct.getThanhTien();
            }
            lblTongTien.setText("Tổng tiền lô hàng: " + new DecimalFormat("#,###").format(tong) + " đ");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi tải chi tiết: " + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onThemLoHang() {
        // Mở dialog tạo lô hàng mới
        LoHangForm form = new LoHangForm(null, loHangBUS);
        form.setVisible(true);
        if (form.isSuccess()) loadData(); // reload sau khi thêm thành công
    }

    private void onYeuCauNhapKho() {
        if (maLoHangDangChon == null) return;
        int confirm = JOptionPane.showConfirmDialog(this,
                "Xác nhận yêu cầu nhập kho cho lô hàng: " + maLoHangDangChon + "?",
                "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            loHangBUS.yeuCauNhapKho(maLoHangDangChon);
            JOptionPane.showMessageDialog(this, "Đã gửi yêu cầu nhập kho thành công!");
            loadData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}