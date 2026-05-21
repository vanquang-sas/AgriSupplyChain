package gui.dialog;

import bus.ThongBaoBUS;
import dto.ThongBaoDTO;
import util.AppColor;
import util.Session;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class ThongBaoForm extends JDialog {

    private JPanel contentPanel;
    private final ThongBaoBUS thongBaoBUS = new ThongBaoBUS();

    public ThongBaoForm(JFrame parent) {
        super(parent, "Thông báo hệ thống", true);
        setSize(480, 600);
        setLocationRelativeTo(parent); // Hiện ở giữa MainFrame
        setLayout(new BorderLayout());
        
        initComponents();
        loadThongBao();
    }

    private void initComponents() {
        // Tiêu đề
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(Color.WHITE);
        pnlHeader.setBorder(new EmptyBorder(15, 20, 10, 20));
        
        JLabel lblTitle = new JLabel("Hộp Thư Thông Báo");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(new Color(15, 23, 42));
        pnlHeader.add(lblTitle, BorderLayout.WEST);
        
        JLabel lblSub = new JLabel(Session.tenNguoiDung + " (" + Session.chucVu + ")");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(new Color(100, 116, 139));
        pnlHeader.add(lblSub, BorderLayout.SOUTH);
        
        add(pnlHeader, BorderLayout.NORTH);

        // Vùng nội dung
        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(241, 245, 249)); // Slate 100 nền nhẹ
        contentPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(12);
        add(scrollPane, BorderLayout.CENTER);

        // --- Ô MÀU KHÁC BIỆT CHỨA NÚT ĐÓNG ---
        JPanel pnlAction = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnlAction.setBackground(Color.WHITE);
        pnlAction.setBorder(new EmptyBorder(12, 0, 12, 0));

        JButton btnClose = new JButton("Đóng") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(15, 23, 42)); // Slate 900
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnClose.setForeground(Color.WHITE);
        btnClose.setPreferredSize(new Dimension(120, 38));
        btnClose.setContentAreaFilled(false);
        btnClose.setBorderPainted(false);
        btnClose.setFocusPainted(false);
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClose.addActionListener(e -> dispose());

        pnlAction.add(btnClose);
        add(pnlAction, BorderLayout.SOUTH);
    }

    private void loadThongBao() {
        contentPanel.removeAll();
        
        if (!Session.isLogged() || Session.currentUser == null) {
            showEmptyMessage("Vui lòng đăng nhập để xem thông báo.");
            return;
        }

        String role = Session.chucVu;
        String userId = Session.currentUser.getLoaiTK() == 2 ? Session.maKH : Session.maNV;

        // 1. Mark all notifications as read immediately when shown
        thongBaoBUS.markAllAsRead(role, userId);

        // 2. Fetch notifications
        List<ThongBaoDTO> notifications = thongBaoBUS.getNotificationsForUser(role, userId);
        
        if (notifications.isEmpty()) {
            showEmptyMessage("Hộp thư thông báo của bạn trống.");
            contentPanel.revalidate();
            contentPanel.repaint();
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        
        for (ThongBaoDTO tb : notifications) {
            // Xác định màu sắc chỉ thị bên hông dựa vào loại thông báo
            Color indicatorColor = new Color(59, 130, 246); // Mặc định xanh dương (info)
            String emojiPrefix = "ℹ️  ";
            
            String loai = tb.getLoaiTB() != null ? tb.getLoaiTB() : "";
            if (loai.contains("Hết hạn") || loai.contains("Hết hàng") || tb.getNoiDung().contains("giao thất bại") || tb.getNoiDung().contains("HẾT")) {
                indicatorColor = new Color(239, 68, 68); // Đỏ (danger)
                emojiPrefix = "⚠️  ";
            } else if (loai.contains("Sắp hết hạn") || loai.contains("Sắp hết hàng")) {
                indicatorColor = new Color(245, 158, 11); // Cam (warning)
                emojiPrefix = "⚡  ";
            } else if (tb.getNoiDung().contains("giao thành công") || tb.getNoiDung().contains("đặt thành công")) {
                indicatorColor = new Color(16, 185, 129); // Xanh lá (success)
                emojiPrefix = "✅  ";
            } else if (loai.contains("Giao hàng") || loai.contains("Yêu cầu")) {
                indicatorColor = new Color(79, 70, 229); // Tím (shipping/dispatch)
                emojiPrefix = "📦  ";
            }

            final Color finalColor = indicatorColor;
            
            // TẠO THẺ TRẮNG BO TRÒN CÓ VIỀN TRÁI MÀU CHỈ THỊ
            JPanel card = new JPanel(new BorderLayout(8, 8)) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    // Vẽ nền trắng bo tròn
                    g2.setColor(Color.WHITE);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                    
                    // Vẽ thanh dọc chỉ thị màu bên trái
                    g2.setColor(finalColor);
                    g2.fillRoundRect(0, 0, 6, getHeight(), 6, 6);
                    
                    g2.dispose();
                }
            };
            card.setOpaque(false);
            card.setBorder(new EmptyBorder(12, 18, 12, 14));
            card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

            // Tiêu đề thẻ (Loại + thời gian)
            JLabel lblHeader = new JLabel(emojiPrefix + loai + "  •  " + sdf.format(tb.getTgTao()));
            lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lblHeader.setForeground(new Color(71, 85, 105)); // Slate 600

            // Nội dung thông báo
            JTextArea txtContent = new JTextArea(tb.getNoiDung());
            txtContent.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            txtContent.setForeground(new Color(15, 23, 42)); // Slate 900
            txtContent.setLineWrap(true);
            txtContent.setWrapStyleWord(true);
            txtContent.setOpaque(false);
            txtContent.setEditable(false);
            txtContent.setFocusable(false);

            card.add(lblHeader, BorderLayout.NORTH);
            card.add(txtContent, BorderLayout.CENTER);

            contentPanel.add(card);
            contentPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Khoảng cách giữa các thẻ
        }
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showEmptyMessage(String message) {
        JPanel pnlEmpty = new JPanel(new GridBagLayout());
        pnlEmpty.setOpaque(false);
        
        JLabel lblEmpty = new JLabel(message);
        lblEmpty.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lblEmpty.setForeground(new Color(100, 116, 139));
        
        pnlEmpty.add(lblEmpty);
        contentPanel.add(pnlEmpty);
    }
}