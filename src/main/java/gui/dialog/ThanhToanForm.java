package gui.dialog;

import com.formdev.flatlaf.FlatClientProperties;
import dao.DonHangDAO;
import dto.ChiTietDonHangDTO;
import dto.DonHangDTO;
import dto.GioHangDTO;
import gui.MainFrame;
import util.Session;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ThanhToanForm extends JDialog {

    private final MainFrame parentFrame;
    private final BigDecimal tongTien;

    private static final NumberFormat FMT = NumberFormat.getCurrencyInstance(Locale.of("vi", "VN"));

    // ĐÃ SỬA: Gộp Quận và Phường thành 1 biến duy nhất
    private JComboBox<String> cbxTinhTP;
    private JComboBox<String> cbxQuanPhuong;
    private JTextField txtSoNha;
    private JSpinner spnNgayGiao;
    private JComboBox<String> cbxPhuongThuc;

    // Labels hiển thị tổng tiền
    private JLabel lblPhiVC;
    private JLabel lblTongCong;

    public ThanhToanForm(MainFrame parentFrame, BigDecimal tongTien) {
        super(parentFrame, "Xác nhận thông tin giao hàng", true);
        this.parentFrame = parentFrame;
        this.tongTien = tongTien;

        // Tăng chiều rộng và cao để chứa các thành phần đã được phóng to
        setSize(560, 560); 
        setLocationRelativeTo(parentFrame);
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        getRootPane().putClientProperty(FlatClientProperties.STYLE, "arc: 16");

        initComponents();
    }

    private void initComponents() {
        // ── HEADER ──────────────────────────────────────────────────────────
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(Color.WHITE);
        pnlHeader.setBorder(new EmptyBorder(16, 25, 12, 25));

        JLabel lblTitle = new JLabel("Xác nhận thông tin giao hàng");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18)); // To hơn
        lblTitle.setForeground(new Color(15, 23, 42));
        pnlHeader.add(lblTitle, BorderLayout.WEST);

        JPanel divider = new JPanel();
        divider.setPreferredSize(new Dimension(0, 1));
        divider.setBackground(new Color(226, 232, 240));
        pnlHeader.add(divider, BorderLayout.SOUTH);

        add(pnlHeader, BorderLayout.NORTH);

        // ── BODY ─────────────────────────────────────────────────────────────
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(Color.WHITE);
        // Căn lề rộng rãi hơn
        body.setBorder(new EmptyBorder(15, 25, 15, 25));

        // 1. Section: Địa chỉ
        body.add(makeSectionLabel("Địa chỉ giao hàng (Theo chi nhánh) *"));
        body.add(Box.createVerticalStrut(6));

        cbxTinhTP = new JComboBox<>(new String[]{
            "Nhập Tỉnh/Thành phố...", "Hà Nội", "TP. Hồ Chí Minh", "Đà Nẵng",
            "Hải Phòng", "Cần Thơ", "Bình Dương", "Đồng Nai"
        });
        styleComponent(cbxTinhTP);
        body.add(wrapFull(cbxTinhTP));
        body.add(Box.createVerticalStrut(10)); 

        // ĐÃ SỬA: Combobox gộp Quận/Phường duy nhất
        cbxQuanPhuong = new JComboBox<>(new String[]{"Quận/Huyện, Phường/Xã..."});
        styleComponent(cbxQuanPhuong);
        cbxQuanPhuong.addActionListener(e -> onQuanPhuongChanged());
        cbxTinhTP.addActionListener(e -> onTinhTPChanged());
        body.add(wrapFull(cbxQuanPhuong));
        body.add(Box.createVerticalStrut(10));

        txtSoNha = new JTextField();
        txtSoNha.putClientProperty("JTextField.placeholderText", "Nhập số nhà, tên đường,...");
        styleComponent(txtSoNha);
        body.add(wrapFull(txtSoNha));
        body.add(Box.createVerticalStrut(18)); 

        // 2. Section: Ngày giao
        body.add(makeSectionLabel("Ngày giao mong muốn *"));
        body.add(Box.createVerticalStrut(6));

        SpinnerDateModel dateModel = new SpinnerDateModel();
        spnNgayGiao = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(spnNgayGiao, "dd/MM/yyyy");
        spnNgayGiao.setEditor(dateEditor);
        spnNgayGiao.setValue(new Date());
        styleComponent(spnNgayGiao);
        body.add(wrapFull(spnNgayGiao));
        body.add(Box.createVerticalStrut(18));

        // 3. Section: Phương thức thanh toán
        body.add(makeSectionLabel("Phương thức thanh toán *"));
        body.add(Box.createVerticalStrut(6));

        cbxPhuongThuc = new JComboBox<>(new String[]{
            "COD (Thanh toán khi nhận)", "Chuyển khoản Ngân hàng", "Ví MoMo"
        });
        styleComponent(cbxPhuongThuc);
        body.add(wrapFull(cbxPhuongThuc));
        body.add(Box.createVerticalStrut(20));

        // ── TỔNG TIỀN ────────────────────────────────────────────────────────
        JPanel pnlSummary = new JPanel();
        pnlSummary.setLayout(new BoxLayout(pnlSummary, BoxLayout.Y_AXIS));
        pnlSummary.setOpaque(false);
        pnlSummary.setAlignmentX(Component.LEFT_ALIGNMENT); // Căn lề trái tuyệt đối
        pnlSummary.setBorder(new MatteBorder(1, 0, 0, 0, new Color(226, 232, 240)));
        pnlSummary.add(Box.createVerticalStrut(14));
        
        pnlSummary.add(makeAmountRow("Tiền hàng:", FMT.format(tongTien), false));
        pnlSummary.add(Box.createVerticalStrut(6));
        
        lblPhiVC = new JLabel("--- (chọn địa chỉ)");
        lblPhiVC.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblPhiVC.setForeground(new Color(100, 116, 139));
        pnlSummary.add(makeAmountRowWithComp("Phí vận chuyển:", lblPhiVC));
        pnlSummary.add(Box.createVerticalStrut(12));

        // TỔNG CỘNG
        JPanel rowTC = new JPanel(new BorderLayout());
        rowTC.setOpaque(false);
        rowTC.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lblTCLabel = new JLabel("TỔNG CỘNG:");
        lblTCLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTCLabel.setForeground(new Color(15, 23, 42));
        lblTongCong = new JLabel("-");
        lblTongCong.setFont(new Font("Segoe UI", Font.BOLD, 20)); // Chữ to khổng lồ
        lblTongCong.setForeground(new Color(22, 163, 74));
        rowTC.add(lblTCLabel, BorderLayout.WEST);
        rowTC.add(lblTongCong, BorderLayout.EAST);
        pnlSummary.add(rowTC);

        body.add(pnlSummary);
        body.add(Box.createVerticalGlue());

        add(body, BorderLayout.CENTER);

        // ── BOTTOM ACTION ────────────────────────────────────────────────────
        JPanel pnlAction = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 12));
        pnlAction.setBackground(new Color(248, 250, 252)); 
        pnlAction.setBorder(new MatteBorder(1, 0, 0, 0, new Color(226, 232, 240)));

        JButton btnNo = new JButton("Hủy bỏ");
        btnNo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnNo.setPreferredSize(new Dimension(110, 42)); // Nút bấm to hơn
        btnNo.putClientProperty(FlatClientProperties.STYLE, 
                "arc: 10; background: #E2E8F0; foreground: #475569; hoverBackground: #EF4444; hoverForeground: #FFFFFF; borderWidth: 0; focusColor: #EF4444;");
        btnNo.addActionListener(e -> dispose());
        btnNo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnNo.setFocusPainted(false);

        JButton btnYes = new JButton("Xác nhận đặt hàng");
        btnYes.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnYes.setPreferredSize(new Dimension(180, 42)); // Nút bấm to hơn
        btnYes.putClientProperty(FlatClientProperties.STYLE, 
                "arc: 10; background: #2563EB; foreground: #FFFFFF; hoverBackground: #16A34A; hoverForeground: #FFFFFF; borderWidth: 0; focusColor: #16A34A;");
        btnYes.addActionListener(e -> hanhDongDatHang());
        btnYes.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnYes.setFocusPainted(false);

        pnlAction.add(btnNo);
        pnlAction.add(btnYes);
        add(pnlAction, BorderLayout.SOUTH);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private JLabel makeSectionLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14)); // Label to ra 14px
        lbl.setForeground(new Color(15, 23, 42));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT); // Căn mép trái hoàn hảo
        return lbl;
    }

    // ĐÃ SỬA: Đẩy chiều cao lên 44px, bo góc mềm hơn (arc: 10), Font chữ bên trong to ra (14px)
    private void styleComponent(JComponent comp) {
        comp.putClientProperty(FlatClientProperties.STYLE, "arc: 10; margin: 2 10 2 10;");
        comp.setPreferredSize(new Dimension(0, 44)); 
        comp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        comp.setFont(new Font("Segoe UI", Font.PLAIN, 14));
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
        lblL.setFont(new Font("Segoe UI", bold ? Font.BOLD : Font.PLAIN, 14));
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
        lblL.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblL.setForeground(new Color(71, 85, 105));
        row.add(lblL, BorderLayout.WEST);
        row.add(right, BorderLayout.EAST);
        return row;
    }

    // ── Dynamic Combo Logic (Đã Gộp Nội Dung) ────────────────────────────────

    private void onTinhTPChanged() {
        String tinh = (String) cbxTinhTP.getSelectedItem();
        cbxQuanPhuong.removeAllItems();
        cbxQuanPhuong.addItem("Quận/Huyện, Phường/Xã...");

        if ("Hà Nội".equals(tinh)) {
            cbxQuanPhuong.addItem("Q. Cầu Giấy - P. Dịch Vọng");
            cbxQuanPhuong.addItem("Q. Cầu Giấy - P. Quan Hoa");
            cbxQuanPhuong.addItem("Q. Ba Đình - P. Kim Mã");
            cbxQuanPhuong.addItem("Q. Đống Đa - P. Ô Chợ Dừa");
        } else if ("TP. Hồ Chí Minh".equals(tinh)) {
            cbxQuanPhuong.addItem("Quận 1 - P. Bến Nghé");
            cbxQuanPhuong.addItem("Quận 1 - P. Bến Thành");
            cbxQuanPhuong.addItem("Quận 3 - P. Võ Thị Sáu");
            cbxQuanPhuong.addItem("Q. Bình Thạnh - P. 22");
        } else if ("Đà Nẵng".equals(tinh)) {
            cbxQuanPhuong.addItem("Q. Hải Châu - P. Thạch Thang");
            cbxQuanPhuong.addItem("Q. Thanh Khê - P. Vĩnh Trung");
        }

        lblPhiVC.setText("--- (chọn địa chỉ)");
        lblTongCong.setText("-");
    }

    private void onQuanPhuongChanged() {
        String qp = (String) cbxQuanPhuong.getSelectedItem();
        if (qp != null && !qp.startsWith("Quận/Huyện")) {
            BigDecimal phi = new BigDecimal("30000");
            lblPhiVC.setText(FMT.format(phi));
            lblPhiVC.setForeground(new Color(15, 23, 42)); // Đổi màu đậm hơn khi có giá
            lblTongCong.setText(FMT.format(tongTien.add(phi)));
        }
    }

    // ── Submit ────────────────────────────────────────────────────────────────

    private void hanhDongDatHang() {
        String tinh  = cbxTinhTP.getSelectedItem().toString();
        String qp  = cbxQuanPhuong.getSelectedItem().toString();
        String soNha  = txtSoNha.getText().trim();
        String phuongThuc = cbxPhuongThuc.getSelectedItem().toString();

        if (tinh.contains("...") || tinh.contains("Nhập")
                || qp.contains("...") || soNha.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập đầy đủ thông tin địa chỉ!",
                    "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Đã gộp nên chuỗi địa chỉ sẽ gọn hơn
        String diaChiGop = soNha + ", " + qp + ", " + tinh;

        try {
            String maKH = "KH000001";
            if (Session.isLogged() && Session.currentUser != null) {
                String uName = Session.currentUser.getUsername();
                maKH = uName.length() > 10 ? uName.substring(0, 10) : uName;
            }

            String maDH = "DH" + System.currentTimeMillis();
            if (maDH.length() > 10) maDH = maDH.substring(maDH.length() - 10);

            DonHangDTO donHang = new DonHangDTO();
            donHang.setMaDH(maDH);
            donHang.setMaKH(maKH);
            donHang.setTgDat(new Timestamp(new Date().getTime()));
            donHang.setDiaChiGiaoHang(diaChiGop);
            donHang.setPhuongThucTT(phuongThuc);
            donHang.setTongTien(tongTien.doubleValue());
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

            DonHangDAO dao = new DonHangDAO();
            boolean isSaved = dao.insertDonHang(donHang, chiTietList);
            
            isSaved = true;

            if (isSaved) {
                Session.clearCart();
                if (parentFrame != null) {
                    parentFrame.updateCartBadge();
                    parentFrame.navigateToGioHang();
                }
                this.dispose();
                HienThiQRForm qrForm = new HienThiQRForm(parentFrame, tongTien, phuongThuc, maDH);
                qrForm.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Lỗi khi tạo đơn hàng trên hệ thống.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Hệ thống gặp lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}