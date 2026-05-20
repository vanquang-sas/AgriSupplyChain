package gui.dialog;

import com.formdev.flatlaf.FlatClientProperties;
import gui.MainFrame;
import util.Session;
import dto.DonHangDTO;
import dto.ChiTietDonHangDTO;
import dao.DonHangDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.List;

public class HienThiQRForm extends JDialog {

    private final MainFrame parentFrame; 
    private final DonHangDTO donHang;
    private final List<ChiTietDonHangDTO> chiTietList;
    private static final NumberFormat FMT = NumberFormat.getCurrencyInstance(Locale.of("vi", "VN"));

    public HienThiQRForm(MainFrame parent, DonHangDTO donHang, List<ChiTietDonHangDTO> chiTietList) {
        super(parent, "Quét mã thanh toán", true);
        this.parentFrame = parent;
        this.donHang = donHang;
        this.chiTietList = chiTietList;

        setSize(480, 580);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        getRootPane().putClientProperty(FlatClientProperties.STYLE, "arc: 16");

        initComponents();
    }

    private void initComponents() {
        String maDH = donHang.getMaDH();
        double tongThanhToan = donHang.getTongTien();
        String phuongThuc = donHang.getPhuongThucTT();

        // --- HEADER ---
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(Color.WHITE);
        pnlHeader.setBorder(new EmptyBorder(15, 20, 15, 20));
        pnlHeader.putClientProperty(FlatClientProperties.STYLE, "border: 0,0,1,0,#E2E8F0");
        
        JLabel lblTitle = new JLabel("Xác nhận thanh toán");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        pnlHeader.add(lblTitle, BorderLayout.WEST);

        JLabel lblMaDH = new JLabel("Mã Đơn: " + maDH);
        lblMaDH.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblMaDH.setForeground(new Color(100, 116, 139));
        pnlHeader.add(lblMaDH, BorderLayout.EAST);
        
        add(pnlHeader, BorderLayout.NORTH);

        // --- BODY ---
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(245, 247, 250)); 
        contentPanel.setBorder(new EmptyBorder(25, 30, 25, 30));

        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.putClientProperty(FlatClientProperties.STYLE, "arc: 20; border: 1,1,1,1,#E2E8F0");
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(30, 25, 30, 25));

        JLabel lblAmountText = new JLabel("Số tiền cần chuyển khoản");
        lblAmountText.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblAmountText.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblAmountText.setForeground(new Color(100, 116, 139));
        
        JLabel lblAmount = new JLabel(FMT.format(tongThanhToan));
        lblAmount.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblAmount.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblAmount.setForeground(new Color(15, 23, 42));

        card.add(lblAmountText);
        card.add(Box.createVerticalStrut(5));
        card.add(lblAmount);
        card.add(Box.createVerticalStrut(25));

        // HỘP QR
        JPanel pnlQRBox = new JPanel(new BorderLayout());
        pnlQRBox.setBackground(Color.WHITE);
        pnlQRBox.putClientProperty(FlatClientProperties.STYLE, "arc: 15");
        pnlQRBox.setBorder(new EmptyBorder(10, 10, 10, 10));
        pnlQRBox.setMaximumSize(new Dimension(240, 240));
        pnlQRBox.setAlignmentX(Component.CENTER_ALIGNMENT);

        String colorScheme = phuongThuc.equals("Ví điện tử") ? "#D82D8B" : "#2563EB"; 
        JLabel lblQRCode = new JLabel(
            "<html><div style='text-align: center; color: " + colorScheme + ";'>"
            + "<h2 style='margin:0;'>[ MÃ QR " + (phuongThuc.equals("Ví điện tử") ? "MOMO" : "CHUYỂN KHOẢN") + " ]</h2><br>"
            + "<span style='color: #475569;'>Quét mã bằng ứng dụng của bạn</span>"
            + "</div></html>", SwingConstants.CENTER);
        
        lblQRCode.setPreferredSize(new Dimension(220, 220));
        lblQRCode.setOpaque(true);
        lblQRCode.setBackground(new Color(248, 250, 252));
        lblQRCode.putClientProperty(FlatClientProperties.STYLE, "border: 2,2,2,2," + colorScheme + "; arc: 15");

        pnlQRBox.add(lblQRCode, BorderLayout.CENTER);
        card.add(pnlQRBox);

        card.add(Box.createVerticalStrut(25));
        JLabel lblNote = new JLabel("Hệ thống sẽ tự động xác nhận sau khi nhận được tiền.");
        lblNote.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblNote.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lblNote.setForeground(new Color(100, 116, 139));
        card.add(lblNote);

        contentPanel.add(card);
        add(contentPanel, BorderLayout.CENTER);

        // --- BOTTOM ACTION ---
        JPanel pnlAction = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        pnlAction.setBackground(Color.WHITE);
        pnlAction.setBorder(new EmptyBorder(10, 0, 10, 0));
        pnlAction.putClientProperty(FlatClientProperties.STYLE, "border: 1,0,0,0,#E2E8F0");

        // NÚT HỦY GIAO DỊCH (Giữ nguyên giỏ hàng, KHÔNG lưu đơn hàng)
        JButton btnHuy = new JButton("Hủy giao dịch");
        btnHuy.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnHuy.setPreferredSize(new Dimension(150, 45));
        btnHuy.putClientProperty(FlatClientProperties.STYLE, 
                "arc: 12; background: #FEF2F2; foreground: #DC2626; hoverBackground: #FEE2E2; borderWidth: 0;");
        btnHuy.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnHuy.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, 
                    "Bạn có chắc chắn muốn hủy giao dịch cho đơn hàng này?\n(Giỏ hàng của bạn vẫn sẽ được giữ nguyên)", 
                    "Xác nhận hủy", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
            if (confirm == JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(this, "Đã hủy giao dịch. Giỏ hàng được giữ nguyên!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            }
        });

        // NÚT ĐÃ THANH TOÁN (Chỉ tạo đơn hàng & xóa giỏ hàng khi thực sự thành công)
        JButton btnDone = new JButton("Đã thanh toán");
        btnDone.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnDone.setPreferredSize(new Dimension(150, 45));
        btnDone.putClientProperty(FlatClientProperties.STYLE, 
                "arc: 12; background: #0F172A; foreground: #FFFFFF; hoverBackground: #334155; borderWidth: 0;");
        btnDone.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDone.addActionListener(e -> {
            DonHangDAO dao = new DonHangDAO();
            boolean isSaved = dao.insertDonHang(donHang, chiTietList);
            
            if (isSaved) {
                new bus.GioHangBUS().clearCart(Session.maKH);
                Session.clearCart();
                if (parentFrame != null) {
                    parentFrame.updateCartBadge();
                    parentFrame.navigateToGioHang();
                }
                JOptionPane.showMessageDialog(this, "Hệ thống ghi nhận đặt hàng & thanh toán thành công!\nMã đơn hàng: " + donHang.getMaDH(), "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Có lỗi xảy ra khi tạo đơn hàng trên hệ thống. Vui lòng thanh toán lại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        pnlAction.add(btnHuy);
        pnlAction.add(btnDone);
        add(pnlAction, BorderLayout.SOUTH);
    }
}