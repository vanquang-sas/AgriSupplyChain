package gui.dialog;

import com.formdev.flatlaf.FlatClientProperties;
import gui.MainFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class HienThiQRForm extends JDialog {

    private final BigDecimal tongTien;
    private final String phuongThuc;
    private final String maDH;
    private static final NumberFormat FMT = NumberFormat.getCurrencyInstance(Locale.of("vi", "VN"));

    public HienThiQRForm(MainFrame parent, BigDecimal tongTien, String phuongThuc, String maDH) {
        super(parent, "Xác nhận thanh toán", true);
        this.tongTien = tongTien;
        this.phuongThuc = phuongThuc;
        this.maDH = maDH;

        setSize(450, phuongThuc.equals("Tiền mặt (COD)") ? 350 : 550);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        
        initComponents();
    }

    private void initComponents() {
        // --- HEADER ---
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(Color.WHITE);
        JLabel lblTitle = new JLabel("MÃ ĐƠN: " + maDH);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setBorder(new EmptyBorder(20, 20, 10, 20));
        pnlHeader.add(lblTitle, BorderLayout.WEST);
        add(pnlHeader, BorderLayout.NORTH);

        // --- BODY ---
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(245, 246, 250)); 
        contentPanel.setBorder(new EmptyBorder(10, 15, 10, 15));

        // THẺ TRẮNG BO TRÒN CHỨA QR (Chuẩn theo code mẫu của bạn)
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblAmountText = new JLabel(phuongThuc.equals("Tiền mặt (COD)") ? "Số tiền cần thanh toán:" : "Số tiền cần chuyển khoản:");
        lblAmountText.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblAmountText.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblAmountText.setForeground(new Color(100, 116, 139));
        
        JLabel lblAmount = new JLabel(FMT.format(tongTien));
        lblAmount.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblAmount.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblAmount.setForeground(new Color(16, 185, 129)); // Màu Primary xanh lanh

        card.add(lblAmountText);
        card.add(Box.createVerticalStrut(5));
        card.add(lblAmount);
        card.add(Box.createVerticalStrut(20));

        if (!phuongThuc.equals("Tiền mặt (COD)")) {
            // HỘP QR BO GÓC DÙNG FlatClientProperties
            JPanel pnlQRBox = new JPanel(new BorderLayout(0, 10));
            pnlQRBox.setBackground(Color.WHITE);
            pnlQRBox.putClientProperty(FlatClientProperties.STYLE, "arc: 15; border: 1,1,1,1,#E2E8F0");
            pnlQRBox.setBorder(new EmptyBorder(15, 15, 15, 15));
            pnlQRBox.setMaximumSize(new Dimension(280, 280));
            pnlQRBox.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel lblQRCode = new JLabel("<html><div style='text-align: center;'><b>[ HÌNH ẢNH MÃ QR ]</b><br><br>Quét mã bằng ứng dụng<br>" + phuongThuc + "</div></html>", SwingConstants.CENTER);
            lblQRCode.setPreferredSize(new Dimension(200, 200));
            lblQRCode.setOpaque(true);
            lblQRCode.setBackground(Color.WHITE);
            lblQRCode.putClientProperty(FlatClientProperties.STYLE, "border: 2,2,2,2,#10B981"); // Border màu xanh Primary

            pnlQRBox.add(lblQRCode, BorderLayout.CENTER);
            card.add(pnlQRBox);

            card.add(Box.createVerticalStrut(20));
            JLabel lblNote = new JLabel("Hệ thống sẽ tự động xác nhận sau khi nhận được tiền.");
            lblNote.setAlignmentX(Component.CENTER_ALIGNMENT);
            lblNote.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            lblNote.setForeground(new Color(100, 116, 139));
            card.add(lblNote);
        } else {
            JLabel lblNoteCOD = new JLabel("<html><div style='text-align: center;'>Đặt hàng thành công!<br>Vui lòng giữ điện thoại để nhân viên giao hàng liên hệ.</div></html>", SwingConstants.CENTER);
            lblNoteCOD.setAlignmentX(Component.CENTER_ALIGNMENT);
            lblNoteCOD.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            lblNoteCOD.setForeground(new Color(100, 116, 139));
            card.add(lblNoteCOD);
        }

        contentPanel.add(card);
        
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        // --- BOTTOM ACTION ---
        JPanel pnlAction = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnlAction.setBackground(new Color(236, 239, 241));
        pnlAction.setBorder(new EmptyBorder(12, 0, 12, 0));

        JButton btnDone = new JButton("Hoàn tất") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(71, 85, 105));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnDone.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnDone.setForeground(Color.WHITE);
        btnDone.setPreferredSize(new Dimension(120, 38));
        btnDone.setContentAreaFilled(false);
        btnDone.setBorderPainted(false);
        btnDone.setFocusPainted(false);
        btnDone.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDone.addActionListener(e -> dispose());

        pnlAction.add(btnDone);
        add(pnlAction, BorderLayout.SOUTH);
    }
}