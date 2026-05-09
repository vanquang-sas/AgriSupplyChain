package gui;

import gui.panel.*;
import util.AppColor;
import util.Session;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.net.URL;
import java.io.File;

public class MainFrame extends JFrame {

    private JPanel sidebarPanel;
    private JPanel contentPanel;
    private CardLayout cardLayout;

    private final List<JButton> menuButtons = new ArrayList<>();

    // Giữ reference các Panel 
    private KhachHangPanel  pnlKhachHang;
    private NhanVienPanel   pnlNhanVien;
    private NhaCungCapPanel pnlNhaCungCap;
    private KhoPanel        pnlKho;
    private ThamSoPanel     pnlThamSo;

    public MainFrame() {
        initComponents();
    }

    private void initComponents() {
        setTitle("AgriSupplyChain – Hệ thống Quản lý Chuỗi cung ứng Nông sản");
        setSize(1366, 768);
        setMinimumSize(new Dimension(1100, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        buildContentArea(); // Khởi tạo Content trước để Menu có đích đến
        buildSidebar();

        add(sidebarPanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
    }

    // ================================================================
    //  SIDEBAR & PHÂN QUYỀN
    // ================================================================
    private void buildSidebar() {
        sidebarPanel = new JPanel(new BorderLayout());
        sidebarPanel.setBackground(AppColor.PRIMARY);
        sidebarPanel.setPreferredSize(new Dimension(260, 0));

        // ---------- LOGO / HEADER ----------
        JPanel logoPanel = new JPanel(new BorderLayout());
        logoPanel.setBackground(AppColor.PRIMARY_ACTIVE);
        logoPanel.setBorder(new EmptyBorder(0, 0, 1, 0));

        JPanel logoInner = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 16));
        logoInner.setBackground(AppColor.PRIMARY_ACTIVE);

        JLabel iconLogo = new JLabel();
        ImageIcon logoImg = getImageIcon("images/logo.jpg", 36, 36);
        if (logoImg != null) {
            iconLogo.setIcon(logoImg);
        } else {
            iconLogo.setText("⬡");
            iconLogo.setFont(new Font("Segoe UI", Font.BOLD, 30));
            iconLogo.setForeground(Color.WHITE);
        }

        JPanel logoText = new JPanel(new GridLayout(2, 1, 0, 2));
        logoText.setBackground(AppColor.PRIMARY_ACTIVE);

        JLabel lblAppName = new JLabel("Agri Supply");
        lblAppName.setFont(new Font("Segoe UI", Font.BOLD, 18)); 
        lblAppName.setForeground(Color.WHITE);

        int roleId = util.Session.isLogged() ? util.Session.currentUser.getLoaiTK() : 2;
        String roleName = util.Session.chucVu != null ? util.Session.chucVu : "Khách Hàng";        

        JLabel lblSubName = new JLabel(roleName);
        lblSubName.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubName.setForeground(new Color(187, 247, 208));

        logoText.add(lblAppName);
        logoText.add(lblSubName);
        logoInner.add(iconLogo);
        logoInner.add(logoText);
        logoPanel.add(logoInner, BorderLayout.CENTER);

        // ---------- MENU ITEMS ----------
        JPanel menuPanel = new JPanel();
        menuPanel.setBackground(AppColor.PRIMARY);
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBorder(new EmptyBorder(16, 0, 12, 0));

        // KHỞI TẠO CÁC NÚT VỚI ICON EMOJI 
        JButton btnTrangChu   = createMenuButton("🏠", "Trang chủ", "TrangChu");
        JButton btnKhachHang  = createMenuButton("👥", "Quản lý Khách hàng", "KhachHang");
        JButton btnNhanVien   = createMenuButton("👤", "Quản lý Nhân viên", "NhanVien");
        JButton btnNhaCungCap = createMenuButton("🏢", "Quản lý Nhà cung cấp", "NhaCungCap");
        JButton btnKho        = createMenuButton("🏭", "Quản lý Kho", "Kho");
        JButton btnThamSo     = createMenuButton("⚙", "Cấu hình Tham số", "ThamSo");
        JButton btnSanPham    = createMenuButton("📦", "Quản lý Sản phẩm", "SanPham");
        JButton btnThongKe    = createMenuButton("📊", "Báo cáo Thống kê", "ThongKe");

        JButton btnCuaHang    = createMenuButton("🛒", "Cửa hàng Nông sản", "TrangChu"); 
        JButton btnDonCuaToi  = createMenuButton("📜", "Đơn hàng của tôi", "TrangChu"); 

        JButton defaultActiveBtn = null;

        // ============================================
        // LOGIC PHÂN QUYỀN
        // ============================================
        if (roleId == 0) { 
            menuPanel.add(buildSectionLabel("TỔNG QUAN"));
            menuPanel.add(btnTrangChu); menuPanel.add(Box.createRigidArea(new Dimension(0, 4)));
            
            menuPanel.add(buildSectionLabel("NHÂN SỰ & ĐỐI TÁC"));
            menuPanel.add(btnKhachHang); menuPanel.add(Box.createRigidArea(new Dimension(0, 4)));
            menuPanel.add(btnNhanVien); menuPanel.add(Box.createRigidArea(new Dimension(0, 4)));
            menuPanel.add(btnNhaCungCap); menuPanel.add(Box.createRigidArea(new Dimension(0, 4)));
            
            menuPanel.add(buildSectionLabel("KHO BÃI & HỆ THỐNG"));
            menuPanel.add(btnKho); menuPanel.add(Box.createRigidArea(new Dimension(0, 4)));
            menuPanel.add(btnThamSo); menuPanel.add(Box.createRigidArea(new Dimension(0, 4)));
            
            menuPanel.add(buildSectionLabel("SẢN PHẨM & BÁO CÁO"));
            menuPanel.add(btnSanPham); menuPanel.add(Box.createRigidArea(new Dimension(0, 4)));
            menuPanel.add(btnThongKe); menuPanel.add(Box.createRigidArea(new Dimension(0, 4)));
            
            defaultActiveBtn = btnTrangChu;
        } else if (roleId == 1) { 
            String cv = util.Session.chucVu != null ? util.Session.chucVu.toLowerCase() : "";
            
            if (cv.contains("kho")) { 
                menuPanel.add(buildSectionLabel("QUẢN LÝ KHO BÃI"));
                menuPanel.add(btnKho); menuPanel.add(Box.createRigidArea(new Dimension(0, 4)));
                defaultActiveBtn = btnKho;
            } else if (cv.contains("thu mua") || cv.contains("giao hàng")) { 
                menuPanel.add(buildSectionLabel("ĐỐI TÁC & SẢN PHẨM"));
                menuPanel.add(btnNhaCungCap); menuPanel.add(Box.createRigidArea(new Dimension(0, 4)));
                menuPanel.add(btnSanPham); menuPanel.add(Box.createRigidArea(new Dimension(0, 4)));
                defaultActiveBtn = btnNhaCungCap;
            }
        } else { 
            menuPanel.add(buildSectionLabel("MUA SẮM"));
            menuPanel.add(btnCuaHang); menuPanel.add(Box.createRigidArea(new Dimension(0, 4)));
            menuPanel.add(btnDonCuaToi); menuPanel.add(Box.createRigidArea(new Dimension(0, 4)));
            defaultActiveBtn = btnCuaHang;
        }

        // ---------- FOOTER ----------
        JPanel footerPanel = buildFooter();

        sidebarPanel.add(logoPanel,   BorderLayout.NORTH);
        sidebarPanel.add(menuPanel,   BorderLayout.CENTER);
        sidebarPanel.add(footerPanel, BorderLayout.SOUTH);

        if (defaultActiveBtn != null) {
            setActiveMenu(defaultActiveBtn);
            for(ActionListener a : defaultActiveBtn.getActionListeners()) {
                a.actionPerformed(new java.awt.event.ActionEvent(this, java.awt.event.ActionEvent.ACTION_PERFORMED, null));
            }
        }
    }

    private JLabel buildSectionLabel(String text) {
        JLabel lbl = new JLabel("  " + text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11)); 
        lbl.setForeground(new Color(187, 247, 208, 160));
        lbl.setBorder(new EmptyBorder(12, 14, 6, 0));
        lbl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        return lbl;
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new BorderLayout(10, 0));
        footer.setBackground(AppColor.PRIMARY_ACTIVE);
        footer.setBorder(new EmptyBorder(16, 16, 16, 16));

        String username = Session.isLogged() ? Session.currentUser.getUsername() : "Guest";
        String firstChar = username.substring(0, 1).toUpperCase();

        JLabel avatar = new JLabel(firstChar);
        avatar.setFont(new Font("Segoe UI", Font.BOLD, 18));
        avatar.setForeground(AppColor.PRIMARY);
        avatar.setOpaque(true);
        avatar.setBackground(Color.WHITE);
        avatar.setHorizontalAlignment(SwingConstants.CENTER);
        avatar.setPreferredSize(new Dimension(42, 42));
        avatar.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 80), 2, true));

        JPanel userText = new JPanel(new GridLayout(2, 1, 0, 2));
        userText.setBackground(AppColor.PRIMARY_ACTIVE);

        JLabel lblName = new JLabel(username);
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblName.setForeground(Color.WHITE);

        JLabel lblStatus = new JLabel("Đang trực tuyến");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblStatus.setForeground(new Color(187, 247, 208));

        userText.add(lblName);
        userText.add(lblStatus);

        // Nút Đăng xuất sử dụng Icon tự vẽ
        JButton btnLogout = new JButton("Đăng xuất");
        btnLogout.setIcon(createEmojiIcon("⏻", 14)); // Tạo icon tắt nguồn
        btnLogout.setIconTextGap(8);
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLogout.setBackground(new Color(255, 255, 255, 30));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.setBorderPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 60), 1, true),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        
        btnLogout.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btnLogout.setBackground(new Color(220, 38, 38)); }
            public void mouseExited(MouseEvent e) { btnLogout.setBackground(new Color(255, 255, 255, 30)); }
        });
        
        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn đăng xuất?", "Đăng xuất", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                Session.currentUser = null; 
                dispose();
                new AuthFrame().setVisible(true);
            }
        });

        footer.add(avatar,   BorderLayout.WEST);
        footer.add(userText, BorderLayout.CENTER);
        footer.add(btnLogout, BorderLayout.SOUTH);
        return footer;
    }

    // ================================================================
    //  CONTENT AREA 
    // ================================================================
    private void buildContentArea() {
        cardLayout    = new CardLayout();
        contentPanel  = new JPanel(cardLayout);
        contentPanel.setBackground(AppColor.BACKGROUND);

        pnlKhachHang  = new KhachHangPanel();
        pnlNhanVien   = new NhanVienPanel();
        pnlNhaCungCap = new NhaCungCapPanel();
        pnlKho        = new KhoPanel();
        pnlThamSo     = new ThamSoPanel();

        JPanel pnlTrangChu = createPlaceholder("🏠", "Trang chủ", "Dashboard tổng quan");
        JPanel pnlSanPham  = createPlaceholder("📦", "Quản lý Sản phẩm", "Đang xây dựng...");
        JPanel pnlThongKe  = createPlaceholder("📊", "Báo cáo Thống kê", "Đang xây dựng...");

        contentPanel.add(pnlTrangChu,   "TrangChu");
        contentPanel.add(pnlSanPham,    "SanPham");
        contentPanel.add(pnlKhachHang,  "KhachHang");
        contentPanel.add(pnlNhanVien,   "NhanVien");
        contentPanel.add(pnlNhaCungCap, "NhaCungCap");
        contentPanel.add(pnlKho,        "Kho");
        contentPanel.add(pnlThamSo,     "ThamSo");
        contentPanel.add(pnlThongKe,    "ThongKe");
    }

    // KỸ THUẬT FIX LỖI: Dùng Icon tự vẽ bằng Graphics2D để chứa Emoji, nhường Font chuẩn lại cho Text
    private JButton createMenuButton(String emojiStr, String label, String cardName) {
        JButton btn = new JButton(label) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Vẽ màu nền
                g2.setColor(getBackground());
                g2.fillRect(0, 0, getWidth(), getHeight());
                
                // Nếu đang active, vẽ thêm vạch đánh dấu bên trái
                if (getBackground().equals(AppColor.PRIMARY_ACTIVE)) {
                    g2.setColor(Color.WHITE); 
                    g2.fillRoundRect(0, 8, 4, getHeight() - 16, 4, 4); 
                }
                
                g2.dispose();
                super.paintComponent(g);
            }
        };

        // Gắn Icon Emoji tự vẽ
        btn.setIcon(createEmojiIcon(emojiStr, 16));
        btn.setIconTextGap(14); // Khoảng cách giữa icon và chữ

        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setBackground(AppColor.PRIMARY);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14)); // FONT CHUẨN ĐỂ HIỂN THỊ TIẾNG VIỆT
        btn.setContentAreaFilled(false); 
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(10, 20, 10, 10)); 
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addActionListener(e -> {
            cardLayout.show(contentPanel, cardName);
            setActiveMenu(btn);
        });

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (!AppColor.PRIMARY_ACTIVE.equals(btn.getBackground()))
                    btn.setBackground(AppColor.PRIMARY_HOVER);
            }
            public void mouseExited(MouseEvent e) {
                if (!AppColor.PRIMARY_ACTIVE.equals(btn.getBackground()))
                    btn.setBackground(AppColor.PRIMARY);
            }
        });

        menuButtons.add(btn);
        return btn;
    }

    private void setActiveMenu(JButton activeBtn) {
        for (JButton b : menuButtons) {
            b.setBackground(AppColor.PRIMARY);
            b.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        }
        activeBtn.setBackground(AppColor.PRIMARY_ACTIVE);
        activeBtn.setFont(new Font("Segoe UI", Font.BOLD, 14)); 
    }

    // Tự động tạo Icon từ chuỗi Emoji để không ảnh hưởng đến Font chính của Nút bấm
    private Icon createEmojiIcon(String emojiText, int size) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, size));
                g2.setColor(c.getForeground());
                FontMetrics fm = g2.getFontMetrics();
                int textY = y + ((getIconHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(emojiText, x, textY);
                g2.dispose();
            }
            @Override public int getIconWidth() { return size + 4; }
            @Override public int getIconHeight() { return size + 4; }
        };
    }

    private JPanel createPlaceholder(String emojiIcon, String title, String subtitle) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(AppColor.BACKGROUND);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(AppColor.SURFACE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppColor.BORDER, 1, true),
                BorderFactory.createEmptyBorder(40, 60, 40, 60)));

        JLabel lblIcon = new JLabel(emojiIcon);
        // Ở đây Label chỉ chứa mỗi Icon nên dùng Font Emoji thoải mái
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64)); 
        lblIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22)); 
        lblTitle.setForeground(AppColor.TEXT_PRIMARY);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel(subtitle);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 14)); 
        lblSub.setForeground(AppColor.TEXT_SECONDARY);
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(lblIcon);
        card.add(Box.createRigidArea(new Dimension(0, 14)));
        card.add(lblTitle);
        card.add(Box.createRigidArea(new Dimension(0, 8)));
        card.add(lblSub);

        p.add(card);
        return p;
    }

    private ImageIcon getImageIcon(String path, int width, int height) {
        try {
            URL imgURL = getClass().getClassLoader().getResource(path);
            if (imgURL != null) {
                ImageIcon originalIcon = new ImageIcon(imgURL);
                Image img = originalIcon.getImage();
                Image scaledImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                return new ImageIcon(scaledImg);
            } else {
                File file = new File("src/main/resources/" + path);
                if (file.exists()) {
                    ImageIcon originalIcon = new ImageIcon(file.getAbsolutePath());
                    Image img = originalIcon.getImage();
                    Image scaledImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                    return new ImageIcon(scaledImg);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}