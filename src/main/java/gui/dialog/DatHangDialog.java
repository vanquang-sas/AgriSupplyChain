package gui.dialog;

import bus.DonHangBUS;
import com.formdev.flatlaf.FlatClientProperties;
import gui.ThemeColor;
import raven.modal.Toast;
import raven.modal.component.SimpleModalBorder.Option;
import raven.modal.listener.ModalController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;

public class DatHangDialog extends JPanel {

    private final DonHangBUS bus;
    private final String maKH;
    private final Runnable onSuccess; 

    private JComboBox<String> cboTinhThanh, cboQuanHuyen, cboPhuongXa, cboPhuongThuc;
    private JTextField txtSoNha;
    private JSpinner spnNgayGiao;
    private JLabel lblPhiShip, lblTongTienHang, lblTongCong;

    private List<String> listTinhThanh;
    private Map<String, List<String>> mapQuanHuyen;
    private Map<String, List<String>> mapPhuongXa;

    private static final NumberFormat FMT = NumberFormat.getCurrencyInstance(Locale.of("vi", "VN"));

    public DatHangDialog(DonHangBUS bus, String maKH, Runnable onSuccess) {
        this.bus = bus;
        this.maKH = maKH;
        this.onSuccess = onSuccess;
        initDummyData();
        initUI();
    }

    // ================================================================== //
    //  1. KHỞI TẠO DỮ LIỆU ĐỊA CHỈ (DUMMY DATA)
    // ================================================================== //
    private void initDummyData() {
        listTinhThanh = Arrays.asList("Hồ Chí Minh", "Cần Thơ", "Đồng Tháp");
        
        mapQuanHuyen = new HashMap<>();
        mapQuanHuyen.put("Hồ Chí Minh", Arrays.asList("TP Thủ Đức", "Quận Bình Thạnh", "Quận Gò Vấp", "Quận 1"));
        mapQuanHuyen.put("Cần Thơ", Arrays.asList("Quận Ninh Kiều", "Quận Cái Răng", "Quận Bình Thủy"));
        mapQuanHuyen.put("Đồng Tháp", Arrays.asList("TP Sa Đéc", "TP Cao Lãnh", "Huyện Lai Vung"));

        mapPhuongXa = new HashMap<>();
        mapPhuongXa.put("TP Thủ Đức", Arrays.asList("Phường Linh Trung", "Phường Linh Xuân", "Phường Hiệp Phú"));
        mapPhuongXa.put("Quận Bình Thạnh", Arrays.asList("Phường 25", "Phường 26", "Phường 27"));
        mapPhuongXa.put("Quận Ninh Kiều", Arrays.asList("Phường Xuân Khánh", "Phường Hưng Lợi", "Phường An Khánh"));
        mapPhuongXa.put("TP Sa Đéc", Arrays.asList("Phường 1", "Phường 2", "Phường 3"));
    }

    // ================================================================== //
    //  2. KHỞI TẠO GIAO DIỆN (UI)
    // ================================================================== //
    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(ThemeColor.SURFACE);
        setPreferredSize(new Dimension(500, 600)); 

        // --- NỘI DUNG CHÍNH (DÙNG ĐỂ CUỘN) ---
        JPanel pnlContent = new JPanel();
        pnlContent.setLayout(new BoxLayout(pnlContent, BoxLayout.Y_AXIS));
        pnlContent.setBackground(ThemeColor.SURFACE);

        // Form nhập liệu
        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setBackground(ThemeColor.SURFACE);
        pnlForm.setBorder(new EmptyBorder(20, 25, 10, 25));
        setupFormFields(pnlForm);
        pnlContent.add(pnlForm);

        // Phần Tổng kết tiền
        JPanel pnlSummary = buildSummaryPanel();
        pnlContent.add(pnlSummary);

        // --- BỌC TRONG JSCROLLPANE CHỐNG CHE NÚT ---
        JScrollPane scrollPane = new JScrollPane(pnlContent);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().putClientProperty(FlatClientProperties.STYLE, "width:8; thumbArc:999; trackArc:999");
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); 

        add(scrollPane, BorderLayout.CENTER);
        
        addAddressEvents(); 
    }

    private void setupFormFields(JPanel pnl) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.gridx = 0; gbc.weightx = 1.0;
        int row = 0;

        pnl.add(createTitleLabel("Địa chỉ giao hàng (Theo chi nhánh) *"), gbc);
        
        gbc.gridy = ++row;
        cboTinhThanh = new JComboBox<>();
        setupAutoComplete(cboTinhThanh, listTinhThanh, "Nhập Tỉnh/Thành phố...");
        pnl.add(cboTinhThanh, gbc);

        gbc.gridy = ++row;
        JPanel pnlRow2 = new JPanel(new GridLayout(1, 2, 10, 0));
        pnlRow2.setOpaque(false);
        cboQuanHuyen = new JComboBox<>(); cboQuanHuyen.setEnabled(false);
        setupAutoComplete(cboQuanHuyen, new ArrayList<>(), "Quận/Huyện...");
        cboPhuongXa = new JComboBox<>(); cboPhuongXa.setEnabled(false);
        setupAutoComplete(cboPhuongXa, new ArrayList<>(), "Phường/Xã...");
        pnlRow2.add(cboQuanHuyen); pnlRow2.add(cboPhuongXa);
        pnl.add(pnlRow2, gbc);

        gbc.gridy = ++row;
        txtSoNha = new JTextField();
        setupInputStyle(txtSoNha, "Số nhà, tên đường...");
        pnl.add(txtSoNha, gbc);

        gbc.gridy = ++row; pnl.add(createTitleLabel("Ngày giao mong muốn *"), gbc);
        gbc.gridy = ++row;
        spnNgayGiao = new JSpinner(new SpinnerDateModel());
        spnNgayGiao.setEditor(new JSpinner.DateEditor(spnNgayGiao, "dd/MM/yyyy"));
        setupInputStyle((JComponent) spnNgayGiao.getEditor(), "");
        pnl.add(spnNgayGiao, gbc);

        gbc.gridy = ++row; pnl.add(createTitleLabel("Phương thức thanh toán *"), gbc);
        gbc.gridy = ++row;
        cboPhuongThuc = new JComboBox<>(new String[]{"COD (Thanh toán khi nhận)", "Chuyển khoản ngân hàng", "Ví điện tử MoMo"});
        setupInputStyle(cboPhuongThuc, "");
        pnl.add(cboPhuongThuc, gbc);
    }

    private JPanel buildSummaryPanel() {
        JPanel pnl = new JPanel(new GridBagLayout());
        pnl.setBackground(ThemeColor.BACKGROUND);
        pnl.putClientProperty(FlatClientProperties.STYLE, "arc: 12; border: 1,1,1,1," + toHex(ThemeColor.BORDER));
        pnl.setBorder(new EmptyBorder(15, 20, 15, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 0);

        // --- 1. DÒNG TIỀN HÀNG ---
        gbc.gridy = 0; gbc.gridx = 0; gbc.weightx = 0.5;
        pnl.add(new JLabel("Tiền hàng:"), gbc);
        
        gbc.gridx = 1;
        lblTongTienHang = new JLabel(FMT.format(bus.tinhTongTienHang()));
        lblTongTienHang.setHorizontalAlignment(SwingConstants.RIGHT);
        lblTongTienHang.setFont(new Font("Segoe UI", Font.BOLD, 14));
        pnl.add(lblTongTienHang, gbc);

        // --- 2. DÒNG PHÍ VẬN CHUYỂN ---
        gbc.gridy = 1; gbc.gridx = 0;
        pnl.add(new JLabel("Phí vận chuyển:"), gbc);
        
        gbc.gridx = 1;
        lblPhiShip = new JLabel("--- (chọn địa chỉ)");
        lblPhiShip.setHorizontalAlignment(SwingConstants.RIGHT);
        lblPhiShip.setForeground(ThemeColor.TEXT_SECONDARY);
        pnl.add(lblPhiShip, gbc);

        // --- 3. KẺ NGANG ---
        gbc.gridy = 2; gbc.gridx = 0; gbc.gridwidth = 2;
        JSeparator sep = new JSeparator();
        sep.setForeground(ThemeColor.BORDER);
        pnl.add(sep, gbc);

        // --- 4. TỔNG CỘNG ---
        gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 1;
        JLabel lblTotalText = new JLabel("TỔNG CỘNG:");
        lblTotalText.setFont(new Font("Segoe UI", Font.BOLD, 16));
        pnl.add(lblTotalText, gbc);
        
        gbc.gridx = 1;
        lblTongCong = new JLabel("-");
        lblTongCong.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTongCong.setForeground(ThemeColor.PRIMARY);
        lblTongCong.setHorizontalAlignment(SwingConstants.RIGHT);
        pnl.add(lblTongCong, gbc);

        return pnl;
    }

    // ================================================================== //
    //  3. LOGIC XỬ LÝ SỰ KIỆN AUTO-COMPLETE & TÍNH PHÍ SHIP
    // ================================================================== //
    private void addAddressEvents() {
        cboTinhThanh.addActionListener(e -> {
            Object selected = cboTinhThanh.getSelectedItem();
            if (selected != null && listTinhThanh.contains(selected.toString())) {
                String tinh = selected.toString();
                cboQuanHuyen.setEnabled(true);
                updateAutoCompleteData(cboQuanHuyen, mapQuanHuyen.get(tinh));
            } else {
                cboQuanHuyen.setEnabled(false);
                cboPhuongXa.setEnabled(false);
            }
            capNhatPhiShip();
        });

        cboQuanHuyen.addActionListener(e -> {
            Object selected = cboQuanHuyen.getSelectedItem();
            if (selected != null) {
                List<String> phuongs = mapPhuongXa.get(selected.toString());
                if (phuongs != null) {
                    cboPhuongXa.setEnabled(true);
                    updateAutoCompleteData(cboPhuongXa, phuongs);
                } else {
                    cboPhuongXa.setEnabled(false);
                }
            } else {
                cboPhuongXa.setEnabled(false);
            }
            capNhatPhiShip();
        });

        cboPhuongXa.addActionListener(e -> capNhatPhiShip());
    }

    private void setupAutoComplete(JComboBox<String> cbo, List<String> data, String placeholder) {
        cbo.setEditable(true);
        updateAutoCompleteData(cbo, data);
        setupInputStyle(cbo, placeholder);
        
        JTextField editor = (JTextField) cbo.getEditor().getEditorComponent();
        editor.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_ESCAPE || 
                    e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN) return;
                
                String text = editor.getText();
                DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
                for (String s : data) if (s.toLowerCase().contains(text.toLowerCase())) model.addElement(s);
                cbo.setModel(model);
                editor.setText(text);
                if(model.getSize() > 0) cbo.showPopup();
            }
        });
    }

    private void updateAutoCompleteData(JComboBox<String> cbo, List<String> data) {
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        if (data != null) for (String s : data) model.addElement(s);
        cbo.setModel(model);
        cbo.setSelectedItem(null); 
    }

    private void capNhatPhiShip() {
        String tinh = getComboText(cboTinhThanh);
        if (listTinhThanh.contains(tinh)) {
            try {
                double phi = bus.tinhPhiVanChuyen(tinh); 
                double hang = bus.tinhTongTienHang();
                lblPhiShip.setText(FMT.format(phi));
                lblPhiShip.setForeground(ThemeColor.TEXT_PRIMARY);
                lblTongCong.setText(FMT.format(hang + phi));
            } catch (Exception ex) {
                lblPhiShip.setText("Không tính được");
            }
        } else {
            lblPhiShip.setText("--- (chọn địa chỉ hợp lệ)");
            lblTongCong.setText("-");
        }
    }

    private String getComboText(JComboBox<String> cbo) {
        return ((JTextField) cbo.getEditor().getEditorComponent()).getText().trim();
    }

    private String getFullAddress() {
        String tinh = getComboText(cboTinhThanh);
        String quan = getComboText(cboQuanHuyen);
        String phuong = getComboText(cboPhuongXa);
        String soNha = txtSoNha.getText().trim();
        
        StringBuilder sb = new StringBuilder();
        if (!soNha.isEmpty()) sb.append(soNha).append(", ");
        if (!phuong.isEmpty()) sb.append(phuong).append(", ");
        if (!quan.isEmpty()) sb.append(quan).append(", ");
        sb.append(tinh);
        
        return sb.toString();
    }

    // ================================================================== //
    //  4. LUỒNG XÁC NHẬN ĐẶT HÀNG (ĐÃ FIX LỖI HIỂN THỊ THÔNG BÁO)
    // ================================================================== //
    public void xacNhanDatHang(ModalController controller) {
        String tinh = getComboText(cboTinhThanh);
        if (!listTinhThanh.contains(tinh)) {
            Toast.show(this, Toast.Type.ERROR, "Vui lòng chọn địa chỉ hợp lệ!");
            return;
        }
        if (txtSoNha.getText().trim().isEmpty()) {
            Toast.show(this, Toast.Type.ERROR, "Vui lòng nhập Số nhà/Tên đường!");
            return;
        }

        Date ngayGiao = (Date) spnNgayGiao.getValue();
        String diaChi = getFullAddress();
        String phuongThuc = cboPhuongThuc.getSelectedItem().toString(); 

        double tongTien = 0;
        try { tongTien = bus.tinhTongTienHang() + bus.tinhPhiVanChuyen(tinh); } catch (Exception ignored) {}

        // Lấy Frame gốc để hiển thị Toast sau khi đóng các Popup
        Component rootWindow = SwingUtilities.getWindowAncestor(this);
        controller.close();

        if (phuongThuc.contains("COD")) {
            // Luồng COD: Chốt luôn
            thucHienLuuDonHang(rootWindow, diaChi, ngayGiao, phuongThuc, "Chưa thanh toán");
        } else {
            // Luồng Online: Hiện QR
            ThanhToanDialog qrPanel = new ThanhToanDialog(tongTien, phuongThuc);
            Object[] options = {"Hủy giao dịch", "Xác nhận Demo"};
            
            int result = JOptionPane.showOptionDialog(
                    rootWindow, 
                    qrPanel, 
                    "Cổng thanh toán",
                    JOptionPane.YES_NO_OPTION, 
                    JOptionPane.PLAIN_MESSAGE, 
                    null, 
                    options, 
                    options[1]
            );

            if (result == 1) { // Bấm "Xác nhận Demo"
                thucHienLuuDonHang(rootWindow, diaChi, ngayGiao, phuongThuc, "Đã thanh toán");
            } else {
                Toast.show(rootWindow, Toast.Type.WARNING, "Hủy giao dịch hoàn tất");
            }
        }
    }

    private void thucHienLuuDonHang(Component rootWindow, String diaChi, Date ngayGiao, String phuongThuc, String trangThaiThanhToan) {
        try {
            // 1. Gọi BUS lưu vào Oracle
            String maDH = bus.thanhToan(maKH, diaChi, ngayGiao, phuongThuc, trangThaiThanhToan);
            
            if (maDH != null) {
                // 2. Xóa sạch giỏ hàng
                bus.xoaToanBoGio();
                
                // 3. Cập nhật lại giao diện (Gọi loadGioHang ở GioHangPanel)
                onSuccess.run(); 
                
                // 4. HIỆN THÔNG BÁO (Gắn trực tiếp vào Frame chính để không bị mất)
                Toast.show(rootWindow, Toast.Type.SUCCESS, "Đặt hàng thành công!");
            } else {
                Toast.show(rootWindow, Toast.Type.ERROR, "Đặt hàng thất bại!");
            }
        } catch (Exception ex) {
            Toast.show(rootWindow, Toast.Type.ERROR, "Lỗi hệ thống: " + ex.getMessage());
        }
    }
    // ================================================================== //
    //  5. CÁC HÀM TIỆN ÍCH DÙNG CHUNG (HELPER)
    // ================================================================== //
    private JLabel createTitleLabel(String t) { 
        JLabel l = new JLabel(t); 
        l.setFont(new Font("Segoe UI", Font.BOLD, 14)); 
        l.setForeground(ThemeColor.TEXT_PRIMARY);
        return l; 
    }
    
    private void setupInputStyle(JComponent c, String p) {
        c.setPreferredSize(new Dimension(0, 38));
        c.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        if (c instanceof JTextField) ((JTextField)c).putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, p);
        if (c instanceof JComboBox) ((JComboBox<?>)c).putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, p);
        c.putClientProperty(FlatClientProperties.STYLE, "arc:8; borderColor:" + toHex(ThemeColor.BORDER) + "; focusColor:" + toHex(ThemeColor.PRIMARY));
    }

    private String toHex(Color c) {
        return String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
    }
}