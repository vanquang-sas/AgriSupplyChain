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
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.net.URL;
import java.io.File;

/**
 * MainFrame - Giao diện chính của hệ thống
 * Đã được tối ưu hóa hiển thị tiếng Việt và Sidebar hiện đại
 */
public class MainFrame extends JFrame {

    // --- Thành phần giao diện chính ---
    private JPanel sidebarPanel;
    private JPanel contentPanel;
    private CardLayout cardLayout;

    // --- Danh sách nút menu để quản lý trạng thái Active ---
    private final List<JButton> menuButtons = new ArrayList<>();

    // --- Khai báo tất cả các Panel chức năng ---
    private KhachHangPanel pnlKhachHang;
    private NhanVienPanel pnlNhanVien;
    private NhaCungCapPanel pnlNhaCungCap;
    private KhoPanel pnlKho;
    private ThamSoPanel pnlThamSo;
    private DonHangPanel pnlDonHang;
    private GiaoHangPanel pnlGiaoHang;
    private LoaiSanPhamPanel pnlLoaiSanPham;
    private SanPhamPanel pnlSanPham;
    private NhapKhoPanel pnlNhapKho;
    private XuatKhoPanel pnlXuatKho;
    private TonKhoPanel pnlTonKho;
    private ThongKePanel pnlThongKe;
    private LoHangPanel pnlLoHang;

    public MainFrame() {
        initComponents();
    }

    private void initComponents() {
        // Thiết lập thông số cơ bản cho Frame
        setTitle("AgriSupplyChain – Hệ thống Quản lý Chuỗi cung ứng Nông sản");
        setSize(1366, 768);
        setMinimumSize(new Dimension(1100, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Khởi tạo vùng nội dung trước để Menu có thể link tới
        buildContentArea();
        
        // Khởi tạo Sidebar
        buildSidebar();

        // Thêm vào Frame chính
        add(sidebarPanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
    }

    // ================================================================
    // SIDEBAR & PHÂN QUYỀN
    // ================================================================
    private void buildSidebar() {
        sidebarPanel = new JPanel(new BorderLayout());
        sidebarPanel.setBackground(AppColor.SIDEBAR_BG);
        sidebarPanel.setPreferredSize(new Dimension(260, 0));

        // ---------- 1. LOGO / HEADER SIDEBAR ----------
        JPanel logoPanel = new JPanel(new BorderLayout());
        logoPanel.setBackground(AppColor.SIDEBAR_ACTIVE);
        logoPanel.setBorder(new EmptyBorder(0, 0, 1, 0));

        JPanel logoInner = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 16));
        logoInner.setBackground(AppColor.SIDEBAR_ACTIVE);

        // Load ảnh Logo từ resources/images/logo.jpg
        JLabel iconLogo = new JLabel();
        ImageIcon logoImg = getImageIcon("images/logo.jpg", 36, 36);
        if (logoImg != null) {
            iconLogo.setIcon(logoImg);
        } else {
            iconLogo.setText("⬡");
            iconLogo.setFont(new Font("Segoe UI", Font.BOLD, 30));
            iconLogo.setForeground(Color.WHITE);
        }

        JPanel logoText = new JPanel(new GridLayout(2, 1, 0, 1));
        logoText.setBackground(AppColor.SIDEBAR_ACTIVE);

        JLabel lblAppName = new JLabel("Agri Supply");
        lblAppName.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblAppName.setForeground(Color.WHITE);

        // Hiển thị chức vụ người dùng
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

        // ---------- 2. MENU ITEMS ----------
        JPanel menuPanel = new JPanel();
        menuPanel.setBackground(AppColor.SIDEBAR_BG);
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBorder(new EmptyBorder(16, 0, 12, 0));

        // Khởi tạo các nút menu với icon Emoji
        JButton btnTrangChu = createMenuButton("🏠", "Trang chủ", "TrangChu");
        JButton btnKhachHang = createMenuButton("👥", "Quản lý Khách hàng", "KhachHang");
        JButton btnNhanVien = createMenuButton("👤", "Quản lý Nhân viên", "NhanVien");
        JButton btnNhaCungCap = createMenuButton("🏢", "Quản lý Nhà cung cấp", "NhaCungCap");
        JButton btnKho = createMenuButton("🏭", "Quản lý Kho", "Kho");
        JButton btnThamSo = createMenuButton("⚙", "Cấu hình Tham số", "ThamSo");
        JButton btnSanPham = createMenuButton("📦", "Quản lý Sản phẩm", "SanPham");
        JButton btnLoaiSanPham = createMenuButton("🏷", "Loại sản phẩm", "LoaiSanPham");
        JButton btnThongKe = createMenuButton("📊", "Báo cáo Thống kê", "ThongKe");
        JButton btnNhapKho = createMenuButton("📥", "Nhập kho", "NhapKho");
        JButton btnXuatKho = createMenuButton("📤", "Xuất kho", "XuatKho");
        JButton btnTonKho = createMenuButton("📋", "Tồn kho", "TonKho");
        JButton btnDonHang = createMenuButton("📜", "Đơn hàng", "DonHang");
        JButton btnGiaoHang = createMenuButton("🚚", "Giao hàng", "GiaoHang");
        JButton btnLoHang = createMenuButton("🧾", "Lô hàng nhập", "LoHang");
        
        JButton btnCuaHang = createMenuButton("🛒", "Cửa hàng Nông sản", "TrangChu");

        JButton defaultActiveBtn = null;

        // Logic phân quyền (Hiển thị các nút tương ứng với từng Role)
        if (roleId == 0) { // ADMIN - QUẢN LÝ
            menuPanel.add(buildSectionLabel("TỔNG QUAN"));
            menuPanel.add(btnTrangChu);
            menuPanel.add(Box.createRigidArea(new Dimension(0, 4)));

            menuPanel.add(buildSectionLabel("NHÂN SỰ & ĐỐI TÁC"));
            menuPanel.add(btnKhachHang);
            menuPanel.add(Box.createRigidArea(new Dimension(0, 4)));
            menuPanel.add(btnNhanVien);
            menuPanel.add(Box.createRigidArea(new Dimension(0, 4)));
            menuPanel.add(btnNhaCungCap);
            menuPanel.add(Box.createRigidArea(new Dimension(0, 4)));

            menuPanel.add(buildSectionLabel("KHO BÃI & HỆ THỐNG"));
            menuPanel.add(btnKho);
            menuPanel.add(Box.createRigidArea(new Dimension(0, 4)));
            menuPanel.add(btnThamSo);
            menuPanel.add(Box.createRigidArea(new Dimension(0, 4)));

            menuPanel.add(buildSectionLabel("SẢN PHẨM & BÁO CÁO"));
            menuPanel.add(btnSanPham);
            menuPanel.add(Box.createRigidArea(new Dimension(0, 4)));
            menuPanel.add(btnLoaiSanPham);
            menuPanel.add(Box.createRigidArea(new Dimension(0, 4)));
            menuPanel.add(btnThongKe);
            menuPanel.add(Box.createRigidArea(new Dimension(0, 4)));

            defaultActiveBtn = btnTrangChu;

        } else if (roleId == 1) { // NHÂN VIÊN
            String cv = util.Session.chucVu != null ? util.Session.chucVu.toLowerCase() : "";
            
            if (cv.contains("kho")) { // NHÂN VIÊN KHO
                menuPanel.add(buildSectionLabel("QUẢN LÝ KHO BÃI"));
                menuPanel.add(btnTonKho);
                menuPanel.add(Box.createRigidArea(new Dimension(0, 4)));
                menuPanel.add(btnNhapKho);
                menuPanel.add(Box.createRigidArea(new Dimension(0, 4)));
                menuPanel.add(btnXuatKho);
                menuPanel.add(Box.createRigidArea(new Dimension(0, 4)));
                defaultActiveBtn = btnTonKho;
                
            } else if (cv.contains("thu mua")) { // NV THU MUA
                menuPanel.add(buildSectionLabel("ĐỐI TÁC & SẢN PHẨM"));
                menuPanel.add(btnNhaCungCap); menuPanel.add(Box.createRigidArea(new Dimension(0, 4)));
                menuPanel.add(btnSanPham); menuPanel.add(Box.createRigidArea(new Dimension(0, 4)));
                menuPanel.add(btnLoHang); menuPanel.add(Box.createRigidArea(new Dimension(0, 4)));
                defaultActiveBtn = btnNhaCungCap;
            } else if (cv.contains("giao hàng")) { // NV GIAO HÀNG
                menuPanel.add(buildSectionLabel("VẬN CHUYỂN"));
                menuPanel.add(btnGiaoHang); menuPanel.add(Box.createRigidArea(new Dimension(0, 4)));
                defaultActiveBtn = btnGiaoHang;
            }
        } else { // KHÁCH HÀNG
            menuPanel.add(buildSectionLabel("MUA SẮM"));
            menuPanel.add(btnCuaHang);
            menuPanel.add(Box.createRigidArea(new Dimension(0, 4)));
            menuPanel.add(btnDonHang);
            menuPanel.add(Box.createRigidArea(new Dimension(0, 4)));
            defaultActiveBtn = btnCuaHang;
        }

        // Thanh cuộn cho sidebar nếu menu quá dài
        JScrollPane scrollMenu = new JScrollPane(menuPanel);
        scrollMenu.setBorder(null);
        scrollMenu.getViewport().setBackground(AppColor.SIDEBAR_BG);
        scrollMenu.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0)); // Ẩn thanh cuộn

        // ---------- 3. FOOTER (THÔNG TIN USER & ĐĂNG XUẤT) ----------
        JPanel footerPanel = buildFooter();

        sidebarPanel.add(logoPanel, BorderLayout.NORTH);
        sidebarPanel.add(scrollMenu, BorderLayout.CENTER);
        sidebarPanel.add(footerPanel, BorderLayout.SOUTH);

        // Kích hoạt nút menu mặc định
        if (defaultActiveBtn != null) {
            final JButton finalDefaultBtn = defaultActiveBtn;
            setActiveMenu(finalDefaultBtn);
            // Kích hoạt hành động của nút ngay khi mở ứng dụng
            SwingUtilities.invokeLater(() -> {
                for (ActionListener a : finalDefaultBtn.getActionListeners()) {
                    a.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
                }
            });
        }
    }

    private JLabel buildSectionLabel(String text) {
        JLabel lbl = new JLabel("   " + text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(new Color(255, 255, 255, 120));
        lbl.setBorder(new EmptyBorder(12, 16, 6, 0));
        lbl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        return lbl;
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new BorderLayout(10, 0));
        footer.setBackground(AppColor.SIDEBAR_ACTIVE);
        footer.setBorder(new EmptyBorder(16, 16, 16, 16));

        String username = Session.isLogged() ? Session.currentUser.getUsername() : "Guest";
        String firstChar = username.substring(0, 1).toUpperCase();

        JLabel avatar = new JLabel(firstChar);
        avatar.setFont(new Font("Segoe UI", Font.BOLD, 18));
        avatar.setForeground(AppColor.SIDEBAR_BG);
        avatar.setOpaque(true);
        avatar.setBackground(Color.WHITE);
        avatar.setHorizontalAlignment(SwingConstants.CENTER);
        avatar.setPreferredSize(new Dimension(42, 42));
        avatar.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 80), 2, true));

        JPanel userText = new JPanel(new GridLayout(2, 1, 0, 2));
        userText.setBackground(AppColor.SIDEBAR_ACTIVE);

        JLabel lblName = new JLabel(username);
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblName.setForeground(Color.WHITE);

        JLabel lblStatus = new JLabel("Đang trực tuyến");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblStatus.setForeground(new Color(187, 247, 208));

        userText.add(lblName);
        userText.add(lblStatus);

        // Nút Đăng xuất: Nền trắng, Chữ đỏ
        JButton btnLogout = new JButton("Đăng xuất") {
            private boolean isHovered = false;
            {
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        isHovered = true;
                        repaint();
                    }
                    @Override
                    public void mouseExited(MouseEvent e) {
                        isHovered = false;
                        repaint();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Thay đổi màu sắc dựa trên trạng thái Hover
                if (isHovered) {
                    g2.setColor(new Color(220, 38, 38)); // Màu đỏ đậm
                    setForeground(Color.WHITE);
                } else {
                    g2.setColor(Color.GRAY); // Màu trắng
                    setForeground(Color.WHITE);
                }
                
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };

        btnLogout.setIcon(createPowerIcon(14));
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLogout.setFocusPainted(false);
        btnLogout.setBorderPainted(false);
        btnLogout.setContentAreaFilled(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.setBorder(new EmptyBorder(8, 0, 8, 0));

        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn đăng xuất?", "Đăng xuất", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                Session.currentUser = null;
                dispose();
                new AuthFrame().setVisible(true);
            }
        });

        footer.add(avatar, BorderLayout.WEST);
        footer.add(userText, BorderLayout.CENTER);
        footer.add(btnLogout, BorderLayout.SOUTH);
        return footer;
    }

    // TỰ VẼ ICON NÚT NGUỒN (POWER) CỰC NÉT BẰNG CODE
    private Icon createPowerIcon(int size) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(c.getForeground()); // Tự động đổi màu theo chữ
                g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                
                int cx = x + size / 2;
                int cy = y + size / 2;
                
                // Vẽ cung tròn khuyết phía trên
                g2.drawArc(x + 2, y + 3, size - 4, size - 4, -60, 300);
                // Vẽ thanh gạt dọc ở giữa
                g2.drawLine(cx, y + 1, cx, cy);
                
                g2.dispose();
            }
            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }
    // ================================================================
    // CONTENT AREA (LẤY LẠI CÁC PANEL CỐT LÕI)
    // ================================================================
    private void buildContentArea() {
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(AppColor.BACKGROUND);

        // Khởi tạo các panel thực tế
        pnlKhachHang = new KhachHangPanel();
        pnlNhanVien = new NhanVienPanel();
        pnlNhaCungCap = new NhaCungCapPanel();
        pnlKho = new KhoPanel();
        pnlThamSo = new ThamSoPanel();
        pnlDonHang = new DonHangPanel();
        pnlGiaoHang = new GiaoHangPanel();
        pnlLoaiSanPham = new LoaiSanPhamPanel();
        pnlSanPham = new SanPhamPanel();
        pnlNhapKho = new NhapKhoPanel();
        pnlXuatKho = new XuatKhoPanel();
        pnlTonKho = new TonKhoPanel();
        pnlThongKe = new ThongKePanel();
        pnlLoHang = new LoHangPanel();

        // Thêm vào CardLayout với tên gọi tương ứng
        contentPanel.add(createPlaceholder("🏠", "Trang chủ", "Dashboard tổng quan"), "TrangChu");
        contentPanel.add(pnlSanPham, "SanPham");
        contentPanel.add(pnlKhachHang, "KhachHang");
        contentPanel.add(pnlNhanVien, "NhanVien");
        contentPanel.add(pnlNhaCungCap, "NhaCungCap");
        contentPanel.add(pnlKho, "Kho");
        contentPanel.add(pnlThamSo, "ThamSo");
        contentPanel.add(pnlThongKe, "ThongKe");
        contentPanel.add(pnlDonHang, "DonHang");
        contentPanel.add(pnlGiaoHang, "GiaoHang");
        contentPanel.add(pnlLoaiSanPham, "LoaiSanPham");
        contentPanel.add(pnlNhapKho, "NhapKho");
        contentPanel.add(pnlXuatKho, "XuatKho");
        contentPanel.add(pnlTonKho, "TonKho");
        contentPanel.add(pnlLoHang, "LoHang");
    }

    // Hàm tạo nút menu với giao diện Capsule (Viên thuốc) hiện đại
    private JButton createMenuButton(String emojiStr, String label, String cardName) {
        JButton btn = new JButton(label) {
            private boolean isHovered = false;
            {
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        isHovered = true;
                        repaint();
                    }
                    @Override
                    public void mouseExited(MouseEvent e) {
                        isHovered = false;
                        repaint();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int marginX = 12;
                int marginY = 2;
                int w = getWidth() - marginX * 2;
                int h = getHeight() - marginY * 2;
                int arc = 12;

                if (getBackground().equals(AppColor.SIDEBAR_ACTIVE)) {
                    // Trạng thái nút đang được chọn
                    g2.setColor(AppColor.SIDEBAR_ACTIVE);
                    g2.fillRoundRect(marginX, marginY, w, h, arc, arc);

                    // Vẽ vạch trắng báo hiệu ở bên trái
                    g2.setColor(Color.WHITE);
                    g2.fillRoundRect(marginX, marginY + 8, 4, h - 16, 4, 4);
                } else if (isHovered) {
                    // Trạng thái khi di chuyển chuột qua
                    g2.setColor(new Color(255, 255, 255, 20));
                    g2.fillRoundRect(marginX, marginY, w, h, arc, arc);
                }

                g2.dispose();
                super.paintComponent(g);
            }
        };

        // Gắn Icon Emoji bằng hàm tự vẽ để fix lỗi font
        btn.setIcon(createEmojiIcon(emojiStr, 16));
        btn.setIconTextGap(16);

        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setBackground(AppColor.SIDEBAR_BG);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(10, 28, 10, 10));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addActionListener(e -> {
            cardLayout.show(contentPanel, cardName);
            setActiveMenu(btn);
        });

        menuButtons.add(btn);
        return btn;
    }

    private void setActiveMenu(JButton activeBtn) {
        for (JButton b : menuButtons) {
            b.setBackground(AppColor.SIDEBAR_BG);
        }
        activeBtn.setBackground(AppColor.SIDEBAR_ACTIVE);
    }

    // Hàm tạo Icon từ Emoji bằng Graphics2D (Giải pháp tốt nhất cho lỗi font)
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

            @Override
            public int getIconWidth() {
                return size + 4;
            }

            @Override
            public int getIconHeight() {
                return size + 4;
            }
        };
    }

    // Panel dự phòng cho những trang đang phát triển
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

    // Hàm load ảnh từ file hệ thống hoặc tài nguyên
    private ImageIcon getImageIcon(String path, int width, int height) {
        try {
            URL imgURL = getClass().getClassLoader().getResource(path);
            if (imgURL != null) {
                Image img = new ImageIcon(imgURL).getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                return new ImageIcon(img);
            } else {
                File file = new File("src/main/resources/" + path);
                if (file.exists()) {
                    Image img = new ImageIcon(file.getAbsolutePath()).getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                    return new ImageIcon(img);
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi load ảnh: " + e.getMessage());
        }
        return null;
    }
}