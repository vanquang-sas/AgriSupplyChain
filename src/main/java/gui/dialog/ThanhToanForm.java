package gui.dialog;

import com.formdev.flatlaf.FlatClientProperties;
import dto.GioHangDTO;
import gui.MainFrame;
import util.Session;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class ThanhToanForm extends JDialog {

    private final GioHangForm parentCartForm;
    private final MainFrame parentFrame;
    private final BigDecimal tongTien;
    private static final NumberFormat FMT = NumberFormat.getCurrencyInstance(Locale.of("vi", "VN"));
    private int pX, pY; // Dùng để kéo thả JDialog

    public ThanhToanForm(GioHangForm parentCartForm, MainFrame parentFrame, BigDecimal tongTien) {
        super(parentCartForm, true); // Modal Dialog, chặn thao tác form dưới
        this.parentCartForm = parentCartForm;
        this.parentFrame = parentFrame;
        this.tongTien = tongTien;
        
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0)); // JDialog trong suốt
        initUI();
    }

    private void initUI() {
        setSize(500, 550); // Kích thước popup thanh toán
        setLocationRelativeTo(parentCartForm);

        // --- Panel nền bo góc (Tương tự GioHangForm) ---
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255)); // Nền trắng
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(1.0f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        mainPanel.setOpaque(false);
        mainPanel.setBorder(new EmptyBorder(15, 20, 25, 20));

        // Event Kéo thả form
        mainPanel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent me) { pX = me.getX(); pY = me.getY(); }
        });
        mainPanel.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent me) {
                setLocation(getLocation().x + me.getX() - pX, getLocation().y + me.getY() - pY);
            }
        });

        // --- HEADER (Tiêu đề + Nút X) ---
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setOpaque(false);
        
        JLabel lblTitle = new JLabel("THANH TOÁN ĐƠN HÀNG");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(new Color(15, 23, 42));
        
        JButton btnClose = new JButton("X");
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnClose.setForeground(new Color(220, 38, 38));
        btnClose.setContentAreaFilled(false);
        btnClose.setBorderPainted(false);
        btnClose.setFocusPainted(false);
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClose.addActionListener(e -> dispose());
        
        pnlHeader.add(lblTitle, BorderLayout.WEST);
        pnlHeader.add(btnClose, BorderLayout.EAST);

        // --- MAIN CONTENT (Số tiền + QR) ---
        JPanel pnlContent = new JPanel();
        pnlContent.setLayout(new BoxLayout(pnlContent, BoxLayout.Y_AXIS));
        pnlContent.setOpaque(false);
        pnlContent.setBorder(new EmptyBorder(20, 0, 20, 0));

        JLabel lblAmountText = new JLabel("SỐ TIỀN CẦN CHUYỂN KHOẢN");
        lblAmountText.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblAmountText.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblAmountText.setForeground(new Color(107, 114, 128));

        JLabel lblAmount = new JLabel(FMT.format(tongTien));
        lblAmount.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblAmount.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblAmount.setForeground(new Color(16, 185, 129));

        JPanel pnlQRBox = new JPanel(new BorderLayout());
        pnlQRBox.setBackground(Color.WHITE);
        pnlQRBox.putClientProperty(FlatClientProperties.STYLE, "arc: 15; border: 1,1,1,1,#e2e8f0");
        pnlQRBox.setBorder(new EmptyBorder(15, 15, 15, 15));
        pnlQRBox.setMaximumSize(new Dimension(250, 250));
        pnlQRBox.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblQRCode = new JLabel("<html><div style='text-align: center;'><b>[ HÌNH ẢNH MÃ QR ]</b><br><br>Quét mã bằng ứng dụng<br>Ngân hàng / Ví điện tử</div></html>", SwingConstants.CENTER);
        lblQRCode.setPreferredSize(new Dimension(200, 200));
        lblQRCode.setOpaque(true);
        lblQRCode.setBackground(new Color(248, 250, 252));
        lblQRCode.putClientProperty(FlatClientProperties.STYLE, "arc: 10");

        pnlQRBox.add(lblQRCode, BorderLayout.CENTER);

        JLabel lblNote = new JLabel("Vui lòng chụp lại biên lai sau khi chuyển tiền.");
        lblNote.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblNote.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblNote.setForeground(Color.GRAY);

        pnlContent.add(lblAmountText);
        pnlContent.add(Box.createVerticalStrut(5));
        pnlContent.add(lblAmount);
        pnlContent.add(Box.createVerticalStrut(20));
        pnlContent.add(pnlQRBox);
        pnlContent.add(Box.createVerticalStrut(20));
        pnlContent.add(lblNote);

        // --- BOTTOM (Nút xác nhận hoàn tất) ---
        JButton btnConfirm = new JButton("XÁC NHẬN ĐÃ CHUYỂN KHOẢN") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(16, 185, 129));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnConfirm.setPreferredSize(new Dimension(0, 45));
        btnConfirm.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnConfirm.setForeground(Color.WHITE);
        btnConfirm.setContentAreaFilled(false);
        btnConfirm.setBorderPainted(false);
        btnConfirm.setFocusPainted(false);
        btnConfirm.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Sự kiện ấn Xác Nhận: Lưu Database
        btnConfirm.addActionListener(e -> xacNhanVaLuuDatabase());

        // Lắp ráp
        mainPanel.add(pnlHeader, BorderLayout.NORTH);
        mainPanel.add(pnlContent, BorderLayout.CENTER);
        mainPanel.add(btnConfirm, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void xacNhanVaLuuDatabase() {
        try {
            // ========================================================
            // CHỖ NÀY BẠN GỌI LỆNH LƯU VÀO DATABASE (DAO/BUS)
            // Ví dụ: HoaDonDAO.insert(hd); ChiTietHoaDonDAO.insert(cthd);
            // ========================================================
            
            // Giả lập lưu thành công
            boolean isSaved = true; 

            if (isSaved) {
                Session.clearCart(); // Dọn giỏ hàng
                if (parentFrame != null) {
                    parentFrame.updateCartBadge(); // Cập nhật lại số lượng trên MainFrame
                }
                JOptionPane.showMessageDialog(this, "Thanh toán thành công! Đơn hàng đã được lưu.");
                
                // Đóng cả form thanh toán và form giỏ hàng
                this.dispose();
                if (parentCartForm != null) {
                    parentCartForm.dispose();
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu Database: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}