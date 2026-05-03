package gui;

import gui.panel.KhachHangPanel;
import gui.panel.KhoPanel;
import gui.panel.NhaCungCapPanel;
import gui.panel.NhanVienPanel;
import gui.panel.ThamSoPanel;
import util.AppColor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * MainFrame - Khung giao diện chính của hệ thống.
 *
 * Cấu trúc:
 *   ┌─────────────┬──────────────────────────────────┐
 *   │   SIDEBAR   │           CONTENT AREA           │
 *   │  (260px)    │         (CardLayout)              │
 *   │  - Logo     │  - Trang chủ                     │
 *   │  - Menu     │  - Khách hàng / NV / NCC         │
 *   │  - Footer   │  - Kho / Tham số / ...           │
 *   └─────────────┴──────────────────────────────────┘
 *
 * Cách thêm Panel mới:
 *   1. Tạo instance: XxxPanel pnlXxx = new XxxPanel();
 *   2. Thêm vào card: contentPanel.add(pnlXxx, "Xxx");
 *   3. Thêm nút menu: createMenuButton("...", "Xxx");
 */
public class MainFrame extends JFrame {

    private JPanel sidebarPanel;
    private JPanel contentPanel;
    private CardLayout cardLayout;

    // Danh sách nút menu để quản lý trạng thái active
    private final List<JButton> menuButtons = new ArrayList<>();

    // Giữ reference các Panel đã khởi tạo (lazy nếu muốn sau)
    private KhachHangPanel  pnlKhachHang;
    private NhanVienPanel   pnlNhanVien;
    private NhaCungCapPanel pnlNhaCungCap;
    private KhoPanel        pnlKho;
    private ThamSoPanel     pnlThamSo;

    public MainFrame() {
        initComponents();
    }

    // ================================================================
    //  KHỞI TẠO GIAO DIỆN
    // ================================================================
    private void initComponents() {
        setTitle("AgriSupplyChain – Hệ thống Quản lý Chuỗi cung ứng Nông sản");
        setSize(1366, 768);
        setMinimumSize(new Dimension(1100, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        buildSidebar();
        buildContentArea();

        add(sidebarPanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
    }

    // ================================================================
    //  SIDEBAR
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

        // Icon hộp
        JLabel iconLogo = new JLabel("⬡");
        iconLogo.setFont(new Font("Segoe UI Emoji", Font.BOLD, 26));
        iconLogo.setForeground(Color.WHITE);

        JPanel logoText = new JPanel(new GridLayout(2, 1, 0, 1));
        logoText.setBackground(AppColor.PRIMARY_ACTIVE);

        JLabel lblAppName = new JLabel("Admin Panel");
        lblAppName.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblAppName.setForeground(Color.WHITE);

        JLabel lblSubName = new JLabel("AgriSupplyChain");
        lblSubName.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSubName.setForeground(new Color(187, 247, 208)); // xanh nhạt

        logoText.add(lblAppName);
        logoText.add(lblSubName);

        logoInner.add(iconLogo);
        logoInner.add(logoText);
        logoPanel.add(logoInner, BorderLayout.CENTER);

        // ---------- MENU ITEMS ----------
        JPanel menuPanel = new JPanel();
        menuPanel.setBackground(AppColor.PRIMARY);
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBorder(new EmptyBorder(12, 0, 12, 0));

        // --- Nhóm: Tổng quan ---
        menuPanel.add(buildSectionLabel("TỔNG QUAN"));
        JButton btnTrangChu = createMenuButton("🏠", "Trang chủ",         "TrangChu");

        // --- Nhóm: Quản trị Nhân sự ---
        menuPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        menuPanel.add(buildSectionLabel("NHÂN SỰ & ĐỐI TÁC"));
        JButton btnKhachHang  = createMenuButton("👥", "Quản lý Khách hàng",   "KhachHang");
        JButton btnNhanVien   = createMenuButton("👤", "Quản lý Nhân viên",    "NhanVien");
        JButton btnNhaCungCap = createMenuButton("🏢", "Quản lý Nhà cung cấp", "NhaCungCap");

        // --- Nhóm: Kho bãi & Hệ thống ---
        menuPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        menuPanel.add(buildSectionLabel("KHO BÃI & HỆ THỐNG"));
        JButton btnKho    = createMenuButton("🏭", "Quản lý Kho",          "Kho");
        JButton btnThamSo = createMenuButton("⚙",  "Cấu hình Tham số",    "ThamSo");

        // --- Nhóm: Sản phẩm & Báo cáo (placeholder) ---
        menuPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        menuPanel.add(buildSectionLabel("SẢN PHẨM & BÁO CÁO"));
        JButton btnSanPham  = createMenuButton("📦", "Quản lý Sản phẩm",   "SanPham");
        JButton btnThongKe  = createMenuButton("📊", "Báo cáo Thống kê",   "ThongKe");

        // Thêm tất cả nút vào menu
        for (JButton b : new JButton[]{
                btnTrangChu,
                btnKhachHang, btnNhanVien, btnNhaCungCap,
                btnKho, btnThamSo,
                btnSanPham, btnThongKe}) {
            menuPanel.add(b);
            menuPanel.add(Box.createRigidArea(new Dimension(0, 2)));
        }

        // ---------- FOOTER (user info + đăng xuất) ----------
        JPanel footerPanel = buildFooter();

        sidebarPanel.add(logoPanel,  BorderLayout.NORTH);
        sidebarPanel.add(menuPanel,  BorderLayout.CENTER);
        sidebarPanel.add(footerPanel, BorderLayout.SOUTH);

        // Mặc định active Trang chủ
        setActiveMenu(btnTrangChu);
    }

    // Label tiêu đề nhóm menu nhỏ
    private JLabel buildSectionLabel(String text) {
        JLabel lbl = new JLabel("  " + text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(new Color(187, 247, 208, 160)); // xanh nhạt mờ
        lbl.setBorder(new EmptyBorder(6, 14, 4, 0));
        lbl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        return lbl;
    }

    // Footer dưới sidebar
    private JPanel buildFooter() {
        JPanel footer = new JPanel(new BorderLayout(10, 0));
        footer.setBackground(AppColor.PRIMARY_ACTIVE);
        footer.setBorder(new EmptyBorder(12, 16, 16, 16));

        // Avatar chữ cái
        JLabel avatar = new JLabel("Q");
        avatar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        avatar.setForeground(AppColor.PRIMARY);
        avatar.setOpaque(true);
        avatar.setBackground(Color.WHITE);
        avatar.setHorizontalAlignment(SwingConstants.CENTER);
        avatar.setPreferredSize(new Dimension(38, 38));
        avatar.setBorder(BorderFactory.createLineBorder(
                new Color(255, 255, 255, 80), 2, true));

        // Tên & email
        JPanel userText = new JPanel(new GridLayout(2, 1, 0, 2));
        userText.setBackground(AppColor.PRIMARY_ACTIVE);

        JLabel lblName = new JLabel("Quản trị viên");
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblName.setForeground(Color.WHITE);

        JLabel lblEmail = new JLabel("admin@system.vn");
        lblEmail.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblEmail.setForeground(new Color(187, 247, 208));

        userText.add(lblName);
        userText.add(lblEmail);

        // Nút đăng xuất
        JButton btnLogout = new JButton("⏻ Đăng xuất");
        btnLogout.setBackground(new Color(255, 255, 255, 30));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLogout.setFocusPainted(false);
        btnLogout.setBorderPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 60), 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        btnLogout.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btnLogout.setBackground(new Color(220, 38, 38)); // đỏ hover
            }
            public void mouseExited(MouseEvent e) {
                btnLogout.setBackground(new Color(255, 255, 255, 30));
            }
        });
        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this, "Bạn có chắc muốn đăng xuất?",
                    "Đăng xuất", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                // Nếu có LoginFrame thì mở tại đây: new LoginFrame().setVisible(true);
            }
        });

        footer.add(avatar,   BorderLayout.WEST);
        footer.add(userText, BorderLayout.CENTER);
        footer.add(btnLogout, BorderLayout.SOUTH);
        return footer;
    }

    // ================================================================
    //  CONTENT AREA (CardLayout)
    // ================================================================
    private void buildContentArea() {
        cardLayout    = new CardLayout();
        contentPanel  = new JPanel(cardLayout);
        contentPanel.setBackground(AppColor.BACKGROUND);

        // --- Khởi tạo các Panel đã hoàn thiện ---
        pnlKhachHang  = new KhachHangPanel();
        pnlNhanVien   = new NhanVienPanel();
        pnlNhaCungCap = new NhaCungCapPanel();
        pnlKho        = new KhoPanel();
        pnlThamSo     = new ThamSoPanel();

        // --- Placeholder cho các phần chưa code ---
        JPanel pnlTrangChu = createPlaceholder("🏠", "Trang chủ",
                "Dashboard tổng quan sẽ được hiển thị tại đây");
        JPanel pnlSanPham  = createPlaceholder("📦", "Quản lý Sản phẩm",
                "Module quản lý sản phẩm & lịch sử giá");
        JPanel pnlThongKe  = createPlaceholder("📊", "Báo cáo Thống kê",
                "Dashboard báo cáo doanh thu, tồn kho, top sản phẩm");

        // --- Đăng ký tất cả vào CardLayout ---
        contentPanel.add(pnlTrangChu,   "TrangChu");
        contentPanel.add(pnlSanPham,    "SanPham");
        contentPanel.add(pnlKhachHang,  "KhachHang");
        contentPanel.add(pnlNhanVien,   "NhanVien");
        contentPanel.add(pnlNhaCungCap, "NhaCungCap");
        contentPanel.add(pnlKho,        "Kho");
        contentPanel.add(pnlThamSo,     "ThamSo");
        contentPanel.add(pnlThongKe,    "ThongKe");
    }

    // ================================================================
    //  HELPER: Tạo nút menu sidebar
    // ================================================================
    private JButton createMenuButton(String icon, String label, String cardName) {
        // Ghép icon + label
        JButton btn = new JButton(icon + "  " + label);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setBackground(AppColor.PRIMARY);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(10, 18, 10, 10));
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

    // Đánh dấu nút menu đang được chọn
    private void setActiveMenu(JButton activeBtn) {
        for (JButton b : menuButtons) b.setBackground(AppColor.PRIMARY);
        activeBtn.setBackground(AppColor.PRIMARY_ACTIVE);
        activeBtn.setFont(new Font("Segoe UI", Font.BOLD, 13)); // in đậm khi active
        // Reset font các nút còn lại
        for (JButton b : menuButtons) {
            if (b != activeBtn)
                b.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        }
    }

    // ================================================================
    //  HELPER: Tạo Placeholder Panel đẹp cho trang chưa code
    // ================================================================
    private JPanel createPlaceholder(String icon, String title, String subtitle) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(AppColor.BACKGROUND);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(AppColor.SURFACE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppColor.BORDER, 1, true),
                BorderFactory.createEmptyBorder(40, 60, 40, 60)));

        JLabel lblIcon = new JLabel(icon);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 56));
        lblIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(AppColor.TEXT_PRIMARY);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel(subtitle);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSub.setForeground(AppColor.TEXT_SECONDARY);
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblComingSoon = new JLabel("🚧  Đang phát triển...");
        lblComingSoon.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblComingSoon.setForeground(AppColor.WARNING);
        lblComingSoon.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblComingSoon.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));

        card.add(lblIcon);
        card.add(Box.createRigidArea(new Dimension(0, 14)));
        card.add(lblTitle);
        card.add(Box.createRigidArea(new Dimension(0, 8)));
        card.add(lblSub);
        card.add(lblComingSoon);

        p.add(card);
        return p;
    }
}