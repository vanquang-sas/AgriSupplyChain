package gui.dialog;

import com.formdev.flatlaf.FlatClientProperties;
import dao.DonHangDAO;
import dao.KhachHangDAO;
import dto.ChiTietDonHangDTO;
import dto.DonHangDTO;
import dto.GioHangDTO;
import dto.KhachHangDTO;
import gui.MainFrame;
import util.Session;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatHangForm extends JDialog {

    private final MainFrame parentFrame;
    private final BigDecimal tongTienHang;
    private BigDecimal phiVanChuyen = BigDecimal.ZERO;
    private BigDecimal giamGia = BigDecimal.ZERO;
    private BigDecimal tongThanhToan = BigDecimal.ZERO;

    private static final NumberFormat FMT = NumberFormat.getCurrencyInstance(Locale.of("vi", "VN"));

    private JComboBox<String> cbxTinhTP;
    private JComboBox<String> cbxQuanPhuong;
    private JTextField txtSoNha;
    private JSpinner spnNgayGiao;
    private JComboBox<String> cbxPhuongThuc;

    private JLabel lblPhiVC;
    private JLabel lblGiamGiaVal;
    private JLabel lblTongCong;
    private JLabel lblDiscountInfo; // Hiển thị thông tin giảm giá ship

    public DatHangForm(MainFrame parentFrame, BigDecimal tongTienHang) {
        super(parentFrame, "Xác nhận thông tin giao hàng", true);
        this.parentFrame = parentFrame;
        this.tongTienHang = tongTienHang;
        this.tongThanhToan = tongTienHang; 

        setSize(600, 800);
        setLocationRelativeTo(parentFrame);
        setLayout(new BorderLayout());
        setBackground(new Color(248, 250, 252));
        getRootPane().putClientProperty(FlatClientProperties.STYLE, "arc: 20");

        initComponents();
        tinhToanPhiShip();
    }

    private void initComponents() {
        // --- HEADER ---
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(Color.WHITE);
        pnlHeader.setBorder(new EmptyBorder(20, 30, 15, 30));
        pnlHeader.putClientProperty(FlatClientProperties.STYLE, "border: 0,0,1,0,#E2E8F0");

        JLabel lblTitle = new JLabel("Xác nhận thông tin đặt hàng");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(15, 23, 42));
        pnlHeader.add(lblTitle, BorderLayout.WEST);

        add(pnlHeader, BorderLayout.NORTH);

        // --- BODY ---
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(new Color(248, 250, 252));
        body.setBorder(new EmptyBorder(20, 30, 20, 30));

        // 1. Địa chỉ
        body.add(makeSectionLabel("Địa chỉ nhận hàng *"));
        body.add(Box.createVerticalStrut(8));

        cbxTinhTP = new JComboBox<>(new String[]{
            "Nhập Tỉnh/Thành phố...", "Hà Nội", "TP. Hồ Chí Minh", "Đà Nẵng",
            "Cần Thơ", "Bình Dương", "Đồng Nai", "Khu vực khác"
        });
        styleComponent(cbxTinhTP);
        body.add(wrapFull(cbxTinhTP));
        body.add(Box.createVerticalStrut(12)); 

        cbxQuanPhuong = new JComboBox<>(new String[]{"Quận/Huyện, Phường/Xã..."});
        styleComponent(cbxQuanPhuong);
        cbxQuanPhuong.addActionListener(e -> onQuanPhuongChanged());
        cbxTinhTP.addActionListener(e -> onTinhTPChanged());
        body.add(wrapFull(cbxQuanPhuong));
        body.add(Box.createVerticalStrut(12));

        txtSoNha = new JTextField();
        styleComponent(txtSoNha);
        txtSoNha.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Số nhà, tên đường, khu công nghiệp...");
        txtSoNha.putClientProperty("JTextField.placeholderText", "Số nhà, tên đường, khu công nghiệp...");
        
        txtSoNha.addActionListener(e -> tinhToanPhiShip());
        
        txtSoNha.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                tinhToanPhiShip();
            }
        });
        
        body.add(wrapFull(txtSoNha));
        body.add(Box.createVerticalStrut(20)); 

        // 2. Ngày giao
        body.add(makeSectionLabel("Thời gian nhận mong muốn *"));
        body.add(Box.createVerticalStrut(8));

        SpinnerDateModel dateModel = new SpinnerDateModel();
        spnNgayGiao = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(spnNgayGiao, "dd/MM/yyyy HH:mm");
        spnNgayGiao.setEditor(dateEditor);
        spnNgayGiao.setValue(new Date());
        styleComponent(spnNgayGiao);
        body.add(wrapFull(spnNgayGiao));
        body.add(Box.createVerticalStrut(20));

        // 3. Phương thức thanh toán
        body.add(makeSectionLabel("Phương thức thanh toán *"));
        body.add(Box.createVerticalStrut(8));

        cbxPhuongThuc = new JComboBox<>(new String[]{
            "Tiền mặt (COD)", "Chuyển khoản Ngân hàng", "Ví MoMo"
        });
        styleComponent(cbxPhuongThuc);
        body.add(wrapFull(cbxPhuongThuc));
        body.add(Box.createVerticalStrut(25));

        // --- TỔNG KẾT CHI PHÍ ---
        JPanel pnlSummary = new JPanel();
        pnlSummary.setLayout(new BoxLayout(pnlSummary, BoxLayout.Y_AXIS));
        pnlSummary.setBackground(Color.WHITE);
        pnlSummary.putClientProperty(FlatClientProperties.STYLE, "arc: 15; border: 1,1,1,1,#E2E8F0");
        pnlSummary.setBorder(new EmptyBorder(15, 20, 15, 20));
        pnlSummary.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        pnlSummary.add(makeAmountRow("Tổng tiền hàng:", FMT.format(tongTienHang), false));
        pnlSummary.add(Box.createVerticalStrut(8));
        
        lblPhiVC = new JLabel("--- (Vui lòng chọn địa chỉ)");
        lblPhiVC.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblPhiVC.setForeground(new Color(15, 23, 42));
        pnlSummary.add(makeAmountRowWithComp("Phí vận chuyển:", lblPhiVC));
        
        lblDiscountInfo = new JLabel();
        lblDiscountInfo.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblDiscountInfo.setForeground(new Color(16, 185, 129)); // Màu xanh lá nhẹ
        pnlSummary.add(makeAmountRowWithComp("", lblDiscountInfo));
        pnlSummary.add(Box.createVerticalStrut(8));

        lblGiamGiaVal = new JLabel("0 đ (0%)");
        lblGiamGiaVal.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblGiamGiaVal.setForeground(new Color(239, 68, 68)); // Màu đỏ nhạt / deep error shade
        pnlSummary.add(makeAmountRowWithComp("Giảm giá sản phẩm:", lblGiamGiaVal));
        
        pnlSummary.add(Box.createVerticalStrut(12));
        JPanel divider = new JPanel();
        divider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        divider.setBackground(new Color(226, 232, 240));
        pnlSummary.add(divider);
        pnlSummary.add(Box.createVerticalStrut(12));

        JPanel rowTC = new JPanel(new BorderLayout());
        rowTC.setOpaque(false);
        rowTC.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lblTCLabel = new JLabel("TỔNG THANH TOÁN:");
        lblTCLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTCLabel.setForeground(new Color(15, 23, 42));
        lblTongCong = new JLabel(FMT.format(tongThanhToan));
        lblTongCong.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTongCong.setForeground(new Color(22, 163, 74));
        rowTC.add(lblTCLabel, BorderLayout.WEST);
        rowTC.add(lblTongCong, BorderLayout.EAST);
        pnlSummary.add(rowTC);

        body.add(pnlSummary);
        body.add(Box.createVerticalGlue());

        add(body, BorderLayout.CENTER);

        // --- BOTTOM ACTION ---
        JPanel pnlAction = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        pnlAction.setBackground(Color.WHITE);
        pnlAction.putClientProperty(FlatClientProperties.STYLE, "border: 1,0,0,0,#E2E8F0");

        JButton btnNo = new JButton("Hủy bỏ");
        btnNo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnNo.setPreferredSize(new Dimension(120, 45));
        btnNo.putClientProperty(FlatClientProperties.STYLE, 
                "arc: 12; background: #F1F5F9; foreground: #475569; hoverBackground: #E2E8F0; borderWidth: 0;");
        btnNo.addActionListener(e -> dispose());
        btnNo.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton btnYes = new JButton("Xác nhận");
        btnYes.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnYes.setPreferredSize(new Dimension(220, 45));
        btnYes.putClientProperty(FlatClientProperties.STYLE, 
                "arc: 12; background: #0F172A; foreground: #FFFFFF; hoverBackground: #334155; borderWidth: 0;");
        btnYes.addActionListener(e -> hanhDongDatHang());
        btnYes.setCursor(new Cursor(Cursor.HAND_CURSOR));

        pnlAction.add(btnNo);
        pnlAction.add(btnYes);
        add(pnlAction, BorderLayout.SOUTH);

        // Đảm bảo không chiếm focus tự động của ô địa chỉ, để lộ placeholder ban đầu
        SwingUtilities.invokeLater(() -> btnYes.requestFocusInWindow());
    }

    // --- Helpers ---
    private JLabel makeSectionLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(new Color(51, 65, 85));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private void styleComponent(JComponent comp) {
        comp.putClientProperty(FlatClientProperties.STYLE, 
            "arc: 10; margin: 4 12 4 12; focusColor: #94A3B8; borderColor: #CBD5E1;");
        comp.setPreferredSize(new Dimension(0, 44)); 
        comp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        comp.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        comp.setAlignmentX(Component.LEFT_ALIGNMENT); 
        if (comp instanceof JComboBox) {
            comp.setBackground(Color.WHITE);
            comp.setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
    }

    private JComponent wrapFull(JComponent comp) {
        comp.setAlignmentX(Component.LEFT_ALIGNMENT);
        comp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        return comp;
    }

    private JPanel makeAmountRow(String label, String value, boolean bold) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26)); 
        JLabel lblL = new JLabel(label);
        lblL.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblL.setForeground(new Color(71, 85, 105));
        JLabel lblR = new JLabel(value);
        lblR.setFont(new Font("Segoe UI", bold ? Font.BOLD : Font.PLAIN, 15));
        lblR.setForeground(new Color(15, 23, 42));
        row.add(lblL, BorderLayout.WEST);
        row.add(lblR, BorderLayout.EAST);
        return row;
    }

    private JPanel makeAmountRowWithComp(String label, JComponent right) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26)); 
        JLabel lblL = new JLabel(label);
        lblL.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblL.setForeground(new Color(71, 85, 105));
        row.add(lblL, BorderLayout.WEST);
        row.add(right, BorderLayout.EAST);
        return row;
    }

    private void onTinhTPChanged() {
        String tinh = (String) cbxTinhTP.getSelectedItem();
        cbxQuanPhuong.removeAllItems();
        cbxQuanPhuong.addItem("Quận/Huyện, Phường/Xã...");

        if ("Hà Nội".equals(tinh)) {
            cbxQuanPhuong.addItem("Q. Cầu Giấy"); cbxQuanPhuong.addItem("Q. Đống Đa");
        } else if ("TP. Hồ Chí Minh".equals(tinh)) {
            cbxQuanPhuong.addItem("Quận 1"); cbxQuanPhuong.addItem("Q. Bình Thạnh");
        } else if ("Đà Nẵng".equals(tinh)) {
            cbxQuanPhuong.addItem("Q. Hải Châu"); cbxQuanPhuong.addItem("Q. Thanh Khê");
        } else {
            cbxQuanPhuong.addItem("Trung tâm TP/Tx"); cbxQuanPhuong.addItem("Ngoại ô/Huyện");
        }
    }

    private void onQuanPhuongChanged() {
    }

 
    // HÀM MỚI: Chỉ tính tiền khi gọi (phí ship cố định theo thẻ khách hàng)
    private void tinhToanPhiShip() {
        // Đã nhập đủ -> Bắt đầu tính giá dựa trên loại khách hàng từ bảng tham số
        String loaiKH = getLoaiKhachHang();
        String tsName = "SHIP_THUONG";
        double defaultVal = 500000;
        
        if ("VIP".equalsIgnoreCase(loaiKH)) {
            tsName = "SHIP_VIP";
            defaultVal = 100000;
            lblDiscountInfo.setText("(Áp dụng phí ship đặc quyền thẻ VIP)");
        } else if ("Thân thiết".equalsIgnoreCase(loaiKH)) {
            tsName = "SHIP_THANTHIET";
            defaultVal = 300000;
            lblDiscountInfo.setText("(Áp dụng phí ship ưu đãi thẻ Thân thiết)");
        } else {
            lblDiscountInfo.setText("(Áp dụng phí ship thẻ Thường)");
        }
        
        double totalFee = new dao.ThamSoDAO().getValueByName(tsName, defaultVal);
        phiVanChuyen = new BigDecimal(totalFee);
        
        lblPhiVC.setText(FMT.format(phiVanChuyen));
        capNhatTongTien();
    }                           

    private void capNhatTongTien() {
        String loaiKH = getLoaiKhachHang();
        String ggName = "GG_THUONG";
        double defaultGG = 0.0;
        if ("VIP".equalsIgnoreCase(loaiKH)) {
            ggName = "GG_VIP";
            defaultGG = 0.05;
        } else if ("Thân thiết".equalsIgnoreCase(loaiKH)) {
            ggName = "GG_THANTHIET";
            defaultGG = 0.02;
        }
        
        double tyLeGiamGia = new dao.ThamSoDAO().getValueByName(ggName, defaultGG);
        giamGia = tongTienHang.multiply(BigDecimal.valueOf(tyLeGiamGia));
        
        int phanTram = (int) Math.round(tyLeGiamGia * 100);
        if (lblGiamGiaVal != null) {
            if (giamGia.compareTo(BigDecimal.ZERO) > 0) {
                lblGiamGiaVal.setText("- " + FMT.format(giamGia) + " (" + phanTram + "%)");
            } else {
                lblGiamGiaVal.setText("0 đ (" + phanTram + "%)");
            }
        }
        
        tongThanhToan = tongTienHang.add(phiVanChuyen).subtract(giamGia);
        if (tongThanhToan.compareTo(BigDecimal.ZERO) < 0) {
            tongThanhToan = BigDecimal.ZERO;
        }
        lblTongCong.setText(FMT.format(tongThanhToan));
    }
    
    // Giả lập lấy loại KH từ Session (hoặc gọi qua DAO)
    private String getLoaiKhachHang() {
    if (Session.isLogged() && Session.currentUser != null) {
        try {
            // Dùng username đang đăng nhập để query DB
            String username = Session.currentUser.getUsername();
            KhachHangDAO khDao = new KhachHangDAO();
            KhachHangDTO kh = khDao.getByUsername(username);
            
            if (kh != null && kh.getLoaiKH() != null) {
                return kh.getLoaiKH();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    return "Thường"; 
}

    // --- Hành động Đặt hàng ---
    private void hanhDongDatHang() {
        String tinh = cbxTinhTP.getSelectedItem().toString();
        String qp = cbxQuanPhuong.getSelectedItem().toString();
        String soNha = txtSoNha.getText().trim();
        String phuongThuc = cbxPhuongThuc.getSelectedItem().toString();

        if (tinh.contains("...") || qp.contains("...") || soNha.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin địa chỉ giao hàng!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Kiểm tra thời gian giao mong muốn (TGGIAOYC)
        Date ngayGiao = (Date) spnNgayGiao.getValue();
        if (ngayGiao.getTime() < System.currentTimeMillis() - 60000) { // cho phép lệch tối đa 1 phút
            JOptionPane.showMessageDialog(this, "Thời gian giao mong muốn không được ở quá khứ!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String diaChiGop = soNha + ", " + qp + ", " + tinh;

        // Vẫn giữ kiểm tra đăng nhập cơ bản
        if (!Session.isLogged() || Session.currentUser == null) {
            JOptionPane.showMessageDialog(this, 
                "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại để tiếp tục!", 
                "Lỗi xác thực", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String username = Session.currentUser.getUsername();
            String maKH = "";

            // LINH HOẠT TÌM KIẾM: Cố gắng lấy từ DB, nếu lỗi hoặc ko có thì dùng cách cũ của bạn
            try {
                dao.KhachHangDAO khDao = new dao.KhachHangDAO(); 
                dto.KhachHangDTO kh = khDao.getByUsername(username);
                if (kh != null) {
                    maKH = kh.getMaKH(); // Dùng mã thật nếu tìm thấy
                } else {
                    // Quay lại cách cũ: Cắt chuỗi username làm mã tạm để test không bị lỗi
                    maKH = username.length() > 10 ? username.substring(0, 10) : username;
                }
            } catch (Exception e) {
                // Nếu bạn chưa thèm viết class KhachHangDAO thì nó nhảy vào đây, vẫn lấy cách cũ
                maKH = username.length() > 10 ? username.substring(0, 10) : username;
            }

            // Tạo mã đơn hàng
            String maDH = "DH" + System.currentTimeMillis();
            if (maDH.length() > 10) maDH = maDH.substring(maDH.length() - 10);

            // Gán dữ liệu vào DTO
            DonHangDTO donHang = new DonHangDTO();
            donHang.setMaDH(maDH);
            donHang.setMaKH(maKH);
            donHang.setTgGiaoYC(new Timestamp(ngayGiao.getTime())); // Ghi nhận thời gian giao mong muốn của khách
            donHang.setDiaChiGiaoHang(diaChiGop);
            
            // Map phuongThuc từ giao diện khớp với check constraint của bảng DONHANG trong Oracle
            String phuongThucDB = "COD";
            if (phuongThuc.contains("Chuyển khoản")) {
                phuongThucDB = "Chuyển khoản";
            } else if (phuongThuc.contains("MoMo")) {
                phuongThucDB = "Ví điện tử";
            }
            donHang.setPhuongThucTT(phuongThucDB);
            
            donHang.setPhiVanChuyen(phiVanChuyen.doubleValue());
            donHang.setTongTienHang(tongTienHang.doubleValue());
            donHang.setGiamGia(giamGia.doubleValue());
            donHang.setTongTien(tongThanhToan.doubleValue());
            donHang.setTrangThaiDH("Đã đặt");

            List<ChiTietDonHangDTO> chiTietList = new ArrayList<>();
            for (GioHangDTO item : Session.cartCache) {
                ChiTietDonHangDTO ct = new ChiTietDonHangDTO();
                ct.setMaDH(maDH);
                ct.setMaSP(item.getMaSP());
                ct.setSoLuong((int) item.getSoLuong());
                ct.setGiaBan(item.getDonGia().doubleValue());
                chiTietList.add(ct);
            }

            if (phuongThuc.equals("Tiền mặt (COD)")) {
                // Nếu là COD thì tạo đơn hàng trực tiếp trong CSDL và xóa giỏ hàng
                DonHangDAO dao = new DonHangDAO();
                boolean isSaved = dao.insertDonHang(donHang, chiTietList);
                if (isSaved) {
                    new bus.GioHangBUS().clearCart(Session.maKH);
                    Session.clearCart();
                    if (parentFrame != null) {
                        parentFrame.updateCartBadge();
                        parentFrame.navigateToGioHang();
                    }
                    this.dispose(); 
                    JOptionPane.showMessageDialog(parentFrame, "Đặt hàng thành công!\nMã đơn hàng: " + donHang.getMaDH() + "\nĐơn hàng sẽ được giao theo hình thức thanh toán khi nhận hàng (COD).", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Lỗi khi tạo đơn hàng COD trên hệ thống.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // Đối với chuyển khoản hoặc Ví điện tử: Đóng form đi, mở QR hiển thị thanh toán
                // Chỉ lưu vào CSDL khi người dùng thực hiện thanh toán thành công trên HienThiQRForm
                this.dispose(); 
                HienThiQRForm qrForm = new HienThiQRForm(parentFrame, donHang, chiTietList);
                qrForm.setVisible(true);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Hệ thống gặp lỗi: " + ex.getMessage(), "Lỗi Nghiêm Trọng", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}