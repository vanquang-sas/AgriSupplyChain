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

        buildContentArea(); // Nên khởi tạo Content trước để Menu có đích đến
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

        JLabel iconLogo = new JLabel("⬡");
        iconLogo.setFont(new Font("Segoe UI Emoji", Font.BOLD, 26));
        iconLogo.setForeground(Color.WHITE);

        JPanel logoText = new JPanel(new GridLayout(2, 1, 0, 1));
        logoText.setBackground(AppColor.PRIMARY_ACTIVE);

        JLabel lblAppName = new JLabel("Agri Supply");
        lblAppName.setFont(new Font("Segoe UI Emoji", Font.BOLD, 15));
        lblAppName.setForeground(Color.WHITE);

        // Lấy Role Name để hiển thị dưới Logo
        int roleId = util.Session.isLogged() ? util.Session.currentUser.getLoaiTK() : 2;
        String roleName = util.Session.chucVu != null ? util.Session.chucVu : "Khách Hàng";        

        JLabel lblSubName = new JLabel(roleName);
        lblSubName.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 11));
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
        menuPanel.setBorder(new EmptyBorder(12, 0, 12, 0));

        // KHỞI TẠO CÁC NÚT (Nhưng chưa Add vào Menu)
        JButton btnTrangChu   = createMenuButton("🏠", "Trang chủ", "TrangChu");
        JButton btnKhachHang  = createMenuButton("👥", "Quản lý Khách hàng", "KhachHang");
        JButton btnNhanVien   = createMenuButton("👤", "Quản lý Nhân viên", "NhanVien");
        JButton btnNhaCungCap = createMenuButton("🏢", "Quản lý Nhà cung cấp", "NhaCungCap");
        JButton btnKho        = createMenuButton("🏭", "Quản lý Kho", "Kho");
        JButton btnThamSo     = createMenuButton("⚙",  "Cấu hình Tham số", "ThamSo");
        JButton btnSanPham    = createMenuButton("📦", "Quản lý Sản phẩm", "SanPham");
        JButton btnThongKe    = createMenuButton("📊", "Báo cáo Thống kê", "ThongKe");

        JButton btnCuaHang    = createMenuButton("🛒", "Cửa hàng Nông sản", "TrangChu"); // Map tạm
        JButton btnDonCuaToi  = createMenuButton("📜", "Đơn hàng của tôi", "TrangChu");  // Map tạm

        JButton defaultActiveBtn = null;

        // ============================================
        // LOGIC PHÂN QUYỀN ADD NÚT THEO ROLE
        // ============================================
        if (roleId == 0) { 
            // 0. ADMIN - QUẢN LÝ
            menuPanel.add(buildSectionLabel("TỔNG QUAN"));
            menuPanel.add(btnTrangChu); menuPanel.add(Box.createRigidArea(new Dimension(0, 2)));
            
            menuPanel.add(buildSectionLabel("NHÂN SỰ & ĐỐI TÁC"));
            menuPanel.add(btnKhachHang); menuPanel.add(Box.createRigidArea(new Dimension(0, 2)));
            menuPanel.add(btnNhanVien); menuPanel.add(Box.createRigidArea(new Dimension(0, 2)));
            menuPanel.add(btnNhaCungCap); menuPanel.add(Box.createRigidArea(new Dimension(0, 2)));
            
            menuPanel.add(buildSectionLabel("KHO BÃI & HỆ THỐNG"));
            menuPanel.add(btnKho); menuPanel.add(Box.createRigidArea(new Dimension(0, 2)));
            menuPanel.add(btnThamSo); menuPanel.add(Box.createRigidArea(new Dimension(0, 2)));
            
            menuPanel.add(buildSectionLabel("SẢN PHẨM & BÁO CÁO"));
            menuPanel.add(btnSanPham); menuPanel.add(Box.createRigidArea(new Dimension(0, 2)));
            menuPanel.add(btnThongKe); menuPanel.add(Box.createRigidArea(new Dimension(0, 2)));
            
            defaultActiveBtn = btnTrangChu;

        } else if (roleId == 1) { 
            // 1. NHÂN VIÊN (Kiểm tra thêm theo tên chức vụ để chia quyền chi tiết)
            String cv = util.Session.chucVu != null ? util.Session.chucVu.toLowerCase() : "";
            
            if (cv.contains("kho")) { // NHÂN VIÊN KHO
                menuPanel.add(buildSectionLabel("QUẢN LÝ KHO BÃI"));
                menuPanel.add(btnKho); menuPanel.add(Box.createRigidArea(new Dimension(0, 2)));
                // Thêm nút Nhập/Xuất kho nếu có
                defaultActiveBtn = btnKho;
                
            } else if (cv.contains("thu mua") || cv.contains("giao hàng")) { // NV THU MUA / GIAO HÀNG
                menuPanel.add(buildSectionLabel("ĐỐI TÁC & SẢN PHẨM"));
                menuPanel.add(btnNhaCungCap); menuPanel.add(Box.createRigidArea(new Dimension(0, 2)));
                menuPanel.add(btnSanPham); menuPanel.add(Box.createRigidArea(new Dimension(0, 2)));
                defaultActiveBtn = btnNhaCungCap;
            }

        } else { 
            // 2. KHÁCH HÀNG (Hoặc người chưa đăng nhập)
            menuPanel.add(buildSectionLabel("MUA SẮM"));
            menuPanel.add(btnCuaHang); menuPanel.add(Box.createRigidArea(new Dimension(0, 2)));
            menuPanel.add(btnDonCuaToi); menuPanel.add(Box.createRigidArea(new Dimension(0, 2)));
            
            defaultActiveBtn = btnCuaHang;
        }

        // ---------- FOOTER ----------
        JPanel footerPanel = buildFooter();

        sidebarPanel.add(logoPanel,  BorderLayout.NORTH);
        sidebarPanel.add(menuPanel,  BorderLayout.CENTER);
        sidebarPanel.add(footerPanel, BorderLayout.SOUTH);

        // Mặc định active nút đầu tiên tùy vào role
        if (defaultActiveBtn != null) {
            setActiveMenu(defaultActiveBtn);
            // Click ảo để render màn hình đầu tiên
            for(ActionListener a : defaultActiveBtn.getActionListeners()) {
                a.actionPerformed(new java.awt.event.ActionEvent(this, java.awt.event.ActionEvent.ACTION_PERFORMED, null));
            }
        }
    }

    private JLabel buildSectionLabel(String text) {
        JLabel lbl = new JLabel("  " + text);
        lbl.setFont(new Font("Segoe UI Emoji", Font.BOLD, 10));
        lbl.setForeground(new Color(187, 247, 208, 160));
        lbl.setBorder(new EmptyBorder(12, 14, 4, 0));
        lbl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        return lbl;
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new BorderLayout(10, 0));
        footer.setBackground(AppColor.PRIMARY_ACTIVE);
        footer.setBorder(new EmptyBorder(12, 16, 16, 16));

        // Lấy tên thật từ Session
        String username = Session.isLogged() ? Session.currentUser.getUsername() : "Guest";
        String firstChar = username.substring(0, 1).toUpperCase();

        JLabel avatar = new JLabel(firstChar);
        avatar.setFont(new Font("Segoe UI Emoji", Font.BOLD, 16));
        avatar.setForeground(AppColor.PRIMARY);
        avatar.setOpaque(true);
        avatar.setBackground(Color.WHITE);
        avatar.setHorizontalAlignment(SwingConstants.CENTER);
        avatar.setPreferredSize(new Dimension(38, 38));
        avatar.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 80), 2, true));

        JPanel userText = new JPanel(new GridLayout(2, 1, 0, 2));
        userText.setBackground(AppColor.PRIMARY_ACTIVE);

        JLabel lblName = new JLabel(username);
        lblName.setFont(new Font("Segoe UI Emoji", Font.BOLD, 13));
        lblName.setForeground(Color.WHITE);

        JLabel lblStatus = new JLabel("Đang trực tuyến");
        lblStatus.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 11));
        lblStatus.setForeground(new Color(187, 247, 208));

        userText.add(lblName);
        userText.add(lblStatus);

        JButton btnLogout = new JButton("⏻ Đăng xuất");
        btnLogout.setBackground(new Color(255, 255, 255, 30));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFont(new Font("Segoe UI Emoji", Font.BOLD, 12));
        btnLogout.setFocusPainted(false);
        btnLogout.setBorderPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 60), 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        
        btnLogout.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btnLogout.setBackground(new Color(220, 38, 38)); }
            public void mouseExited(MouseEvent e) { btnLogout.setBackground(new Color(255, 255, 255, 30)); }
        });
        
        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn đăng xuất?", "Đăng xuất", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                // Clear session
                Session.currentUser = null; 
                dispose();
                new AuthFrame().setVisible(true); // Trở về trang đăng nhập
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

    private JButton createMenuButton(String icon, String label, String cardName) {
        JButton btn = new JButton(icon + "  " + label);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setBackground(AppColor.PRIMARY);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));
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

    private void setActiveMenu(JButton activeBtn) {
        for (JButton b : menuButtons) b.setBackground(AppColor.PRIMARY);
        activeBtn.setBackground(AppColor.PRIMARY_ACTIVE);
        activeBtn.setFont(new Font("Segoe UI Emoji", Font.BOLD, 13)); 
        for (JButton b : menuButtons) {
            if (b != activeBtn) b.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));
        }
    }

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
        lblTitle.setFont(new Font("Segoe UI Emoji", Font.BOLD, 22));
        lblTitle.setForeground(AppColor.TEXT_PRIMARY);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel(subtitle);
        lblSub.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
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
}