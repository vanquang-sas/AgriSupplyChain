package gui.dialog;

import com.formdev.flatlaf.FlatClientProperties;
import gui.ThemeColor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.NumberFormat;
import java.util.Locale;

public class ThanhToanDialog extends JPanel {

    private final double tongTien;
    private final String phuongThuc;
    private static final NumberFormat FMT = NumberFormat.getCurrencyInstance(Locale.of("vi", "VN"));

    public ThanhToanDialog(double tongTien, String phuongThuc) {
        this.tongTien = tongTien;
        this.phuongThuc = phuongThuc;
        initUI();
    }

    private void initUI() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(ThemeColor.SURFACE);
        setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel lblAmountText = new JLabel("Số tiền cần chuyển:");
        lblAmountText.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblAmountText.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblAmountText.setForeground(ThemeColor.TEXT_SECONDARY);
        
        JLabel lblAmount = new JLabel(FMT.format(tongTien));
        lblAmount.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblAmount.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblAmount.setForeground(ThemeColor.PRIMARY);

        JPanel pnlQRBox = new JPanel(new BorderLayout(0, 10));
        pnlQRBox.setBackground(ThemeColor.BACKGROUND);
        pnlQRBox.putClientProperty(FlatClientProperties.STYLE, "arc: 15; border: 1,1,1,1," + toHex(ThemeColor.BORDER));
        pnlQRBox.setBorder(new EmptyBorder(15, 15, 15, 15));
        pnlQRBox.setMaximumSize(new Dimension(280, 280));

        JLabel lblQRCode = new JLabel("<html><div style='text-align: center;'><b>[ HÌNH ẢNH MÃ QR ]</b><br><br>Quét mã bằng ứng dụng<br>Ngân hàng / Ví điện tử</div></html>", SwingConstants.CENTER);
        lblQRCode.setPreferredSize(new Dimension(200, 200));
        lblQRCode.setOpaque(true);
        lblQRCode.setBackground(Color.WHITE);
        lblQRCode.putClientProperty(FlatClientProperties.STYLE, "border: 2,2,2,2," + toHex(ThemeColor.PRIMARY));

        pnlQRBox.add(lblQRCode, BorderLayout.CENTER);

        JLabel lblNote = new JLabel("Hệ thống sẽ tự động xác nhận sau khi nhận được tiền.");
        lblNote.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblNote.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblNote.setForeground(ThemeColor.TEXT_SECONDARY);

        add(lblAmountText);
        add(Box.createVerticalStrut(5));
        add(lblAmount);
        add(Box.createVerticalStrut(20));
        add(pnlQRBox);
        add(Box.createVerticalStrut(20));
        add(lblNote);
    }

    private String toHex(Color c) {
        return String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
    }
}