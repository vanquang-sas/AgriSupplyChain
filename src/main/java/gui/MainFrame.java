package gui;

import gui.panel.*;
import util.AppColor;
import util.Session;
import bus.ThongBaoBUS;

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
 * Đã tích hợp Header chứa Giỏ hàng & Cập nhật Badge
 */
public class MainFrame extends JFrame {

    // --- Thành phần giao diện chính ---
    private JPanel sidebarPanel;
    private JPanel topHeader;     
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private GioHangPanel pnlGioHang;
    private JButton btnGioHangMenu;
    private JButton btnBell;
    private int unreadNotificationCount = 0;
    private final ThongBaoBUS thongBaoBUS = new ThongBaoBUS();
    private Timer notificationTimer;

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
    private CuaHangPanel pnlCuaHang;
    private LoaiSanPhamPanel pnlLoaiSanPham;
    private UserProfilePanel pnlUserProfile;
    private SanPhamPanel pnlSanPham;
    private NhapKhoPanel pnlNhapKho;
    private XuatKhoPanel pnlXuatKho;
    private TonKhoPanel pnlTonKho;
    private ThongKePanel pnlThongKe;
    private LoHangPanel pnlLoHang;
    private LichSuLoHangPanel pnlLichSuLoHang;

    public MainFrame() {
        initComponents();
    }

    private void initComponents() {
        // Thiết lập thông số cơ bản cho Frame
        setTitle("Mekong Agri-Chain – Hệ thống Quản lý Chuỗi cung ứng Nông sản");
        setSize(1366, 768);
        setMinimumSize(new Dimension(1100, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Khởi tạo các khu vực
        buildContentArea();
        buildTopHeader(); // Gọi hàm tạo Header giỏ hàng
        buildSidebar();

        // Ẩn topHeader để loại bỏ khoảng trống thừa ở đầu content
        topHeader.setVisible(false);

        // Tạo Wrapper chứa Content
        JPanel mainContentWrapper = new JPanel(new BorderLayout());
        mainContentWrapper.add(contentPanel, BorderLayout.CENTER);

        // Thêm vào Frame chính
        add(sidebarPanel, BorderLayout.WEST);
        add(mainContentWrapper, BorderLayout.CENTER); 
        
        // Cập nhật số lượng giỏ hàng lần đầu
        updateCartBadge();

        // Cập nhật số lượng thông báo chưa đọc lần đầu
        updateUnreadNotificationCount();

        // Khởi động Timer định kỳ cập nhật thông báo (mỗi 10 giây)
        notificationTimer = new Timer(10000, e -> updateUnreadNotificationCount());
        notificationTimer.start();
    }

    // ================================================================
    // HÀM ĐIỀU HƯỚNG TRUNG TÂM (ĐÓNG MỞ HEADER TỰ ĐỘNG)
    // ================================================================
    private void ensurePanelCreated(String cardName) {
        switch (cardName) {

            case "SanPham":
                if (pnlSanPham == null) {
                    pnlSanPham = new SanPhamPanel();
                    contentPanel.add(pnlSanPham, "SanPham");
                }
                break;
            case "KhachHang":
                if (pnlKhachHang == null) {
                    pnlKhachHang = new KhachHangPanel();
                    contentPanel.add(pnlKhachHang, "KhachHang");
                }
                break;
            case "NhanVien":
                if (pnlNhanVien == null) {
                    pnlNhanVien = new NhanVienPanel();
                    contentPanel.add(pnlNhanVien, "NhanVien");
                }
                break;
            case "NhaCungCap":
                if (pnlNhaCungCap == null) {
                    pnlNhaCungCap = new NhaCungCapPanel();
                    contentPanel.add(pnlNhaCungCap, "NhaCungCap");
                }
                break;
            case "Kho":
                if (pnlKho == null) {
                    pnlKho = new KhoPanel();
                    contentPanel.add(pnlKho, "Kho");
                }
                break;
            case "ThamSo":
                if (pnlThamSo == null) {
                    pnlThamSo = new ThamSoPanel();
                    contentPanel.add(pnlThamSo, "ThamSo");
                }
                break;
            case "ThongKe":
                if (pnlThongKe == null) {
                    pnlThongKe = new ThongKePanel();
                    contentPanel.add(pnlThongKe, "ThongKe");
                }
                break;
            case "DonHang":
                if (pnlDonHang == null) {
                    pnlDonHang = new DonHangPanel();
                    contentPanel.add(pnlDonHang, "DonHang");
                }
                break;
            case "GiaoHang":
                if (pnlGiaoHang == null) {
                    pnlGiaoHang = new GiaoHangPanel(Session.maNV);
                    contentPanel.add(pnlGiaoHang, "GiaoHang");
                }
                break;
            case "CuaHang":
                if (pnlCuaHang == null) {
                    pnlCuaHang = new CuaHangPanel(this);
                    contentPanel.add(pnlCuaHang, "CuaHang");
                }
                break;
            case "LoaiSanPham":
                if (pnlLoaiSanPham == null) {
                    pnlLoaiSanPham = new LoaiSanPhamPanel();
                    contentPanel.add(pnlLoaiSanPham, "LoaiSanPham");
                }
                break;
            case "Profile":
                if (pnlUserProfile == null) {
                    pnlUserProfile = new UserProfilePanel();
                    contentPanel.add(pnlUserProfile, "Profile");
                }
                break;
            case "NhapKho":
                if (pnlNhapKho == null) {
                    pnlNhapKho = new NhapKhoPanel();
                    contentPanel.add(pnlNhapKho, "NhapKho");
                }
                break;
            case "XuatKho":
                if (pnlXuatKho == null) {
                    pnlXuatKho = new XuatKhoPanel();
                    contentPanel.add(pnlXuatKho, "XuatKho");
                }
                break;
            case "TonKho":
                if (pnlTonKho == null) {
                    pnlTonKho = new TonKhoPanel();
                    contentPanel.add(pnlTonKho, "TonKho");
                }
                break;
            case "LoHang":
                if (pnlLoHang == null) {
                    pnlLoHang = new LoHangPanel();
                    contentPanel.add(pnlLoHang, "LoHang");
                }
                break;
            case "LichSuLoHang":
                if (pnlLichSuLoHang == null) {
                    pnlLichSuLoHang = new LichSuLoHangPanel();
                    contentPanel.add(pnlLichSuLoHang, "LichSuLoHang");
                }
                break;
            case "GioHang":
                if (pnlGioHang == null) {
                    pnlGioHang = new GioHangPanel(this);
                    contentPanel.add(pnlGioHang, "GioHang");
                }
                break;
        }
    }

    private void navigateToCard(String cardName) {
        ensurePanelCreated(cardName);
        cardLayout.show(contentPanel, cardName);
        
        // Tự động làm mới dữ liệu cho các Panel chức năng khi chuyển trang
        if (cardName.equals("DonHang") && pnlDonHang != null) {
            pnlDonHang.loadData(null);
        }
        // Làm mới danh sách sản phẩm khi quay lại cửa hàng (đồng bộ tồn kho sau khi thay đổi giỏ)
        if (cardName.equals("CuaHang") && pnlCuaHang != null) {
            pnlCuaHang.refreshProducts();
        }
        // Làm mới giỏ hàng khi điều hướng tới
        if (cardName.equals("GioHang") && pnlGioHang != null) {
            pnlGioHang.loadCartItemsFromDBAsync();
        }
    }

    // ================================================================
    // HEADER & GIỎ HÀNG 
    // ================================================================
    private void buildTopHeader() {
        topHeader = new JPanel(new BorderLayout());
        topHeader.setBackground(AppColor.BACKGROUND);
        
        topHeader.setBorder(new EmptyBorder(0, 20, 0, 20));
        topHeader.setPreferredSize(new Dimension(0, 45)); 

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 3));
        rightPanel.setBackground(AppColor.BACKGROUND);

        // --- Nút Giỏ Hàng ---
        JButton btnCart = new JButton() {
            private boolean isHovered = false;

            {
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { isHovered = true; repaint(); }
                    @Override public void mouseExited(MouseEvent e) { isHovered = false; repaint(); }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                int btnW = getWidth();
                int btnH = getHeight();
                int size = 32; 
                int x = (btnW - size) / 2;     
                int y = (btnH - size) / 2;     

                if (isHovered) g2.setColor(new Color(226, 232, 240));
                else g2.setColor(Color.WHITE);
                g2.fillOval(x, y, size, size);

                g2.setColor(new Color(203, 213, 225));
                g2.drawOval(x, y, size, size);

                g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 15));
                g2.setColor(Color.BLACK);
                String emoji = "🛒";
                FontMetrics fm = g2.getFontMetrics();
                int textX = x + (size - fm.stringWidth(emoji)) / 2;
                int textY = y + ((size - fm.getHeight()) / 2) + fm.getAscent() - 1;
                g2.drawString(emoji, textX, textY);

                int count = Session.getCartItemCount();
                if (count > 0) {
                    String countStr = String.valueOf(count);
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 9));
                    FontMetrics badgeFm = g2.getFontMetrics();
                    
                    int badgeW = Math.max(14, badgeFm.stringWidth(countStr) + 6);
                    int badgeH = 14;
                    int badgeX = x + size - badgeW / 2 - 1;
                    int badgeY = y - 1;

                    g2.setColor(new Color(239, 68, 68)); 
                    g2.fillRoundRect(badgeX, badgeY, badgeW, badgeH, badgeH, badgeH);
                    
                    g2.setColor(Color.WHITE); 
                    int countX = badgeX + (badgeW - badgeFm.stringWidth(countStr)) / 2;
                    int countY = badgeY + ((badgeH - badgeFm.getHeight()) / 2) + badgeFm.getAscent();
                    g2.drawString(countStr, countX, countY);
                }
                g2.dispose();
            }
        };

        btnCart.setPreferredSize(new Dimension(38, 38));
        btnCart.setContentAreaFilled(false);
        btnCart.setBorderPainted(false);
        btnCart.setFocusPainted(false);
        btnCart.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnCart.addActionListener(e -> {
            try {
                navigateToGioHang(); 
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi hiển thị Giỏ hàng: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        int roleId = Session.isLogged() ? Session.currentUser.getLoaiTK() : 2;
        if (roleId == 2) {
            rightPanel.add(btnCart);
        }
        topHeader.add(rightPanel, BorderLayout.EAST);
    }

    public void updateCartBadge() {
        if (topHeader != null) {
            topHeader.repaint(); 
        }
    }

    public void updateUnreadNotificationCount() {
        if (!Session.isLogged() || Session.currentUser == null) {
            unreadNotificationCount = 0;
            if (btnBell != null) btnBell.repaint();
            return;
        }
        
        String role = Session.chucVu;
        String userId = Session.currentUser.getLoaiTK() == 2 ? Session.maKH : Session.maNV;
        
        new SwingWorker<Integer, Void>() {
            @Override
            protected Integer doInBackground() throws Exception {
                return thongBaoBUS.getUnreadCount(role, userId);
            }

            @Override
            protected void done() {
                try {
                    unreadNotificationCount = get();
                    if (btnBell != null) {
                        btnBell.repaint();
                    }
                } catch (Exception e) {
                    System.err.println("Lỗi khi tải số lượng thông báo chưa đọc: " + e.getMessage());
                }
            }
        }.execute();
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

        JLabel lblAppName = new JLabel("Mekong Agri-Chain");
        lblAppName.setFont(new Font("Segoe UI", Font.BOLD, 16));
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

        // ---------- 2. MENU ITEMS ----------
        JPanel menuPanel = new JPanel();
        menuPanel.setBackground(AppColor.SIDEBAR_BG);
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBorder(new EmptyBorder(16, 0, 12, 0));

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
        JButton btnLichSuLoHang = createMenuButton("🕘", "Lịch sử nhập hàng", "LichSuLoHang");
        JButton btnCuaHang = createMenuButton("🛒", "Cửa hàng Nông sản", "CuaHang");

        JButton defaultActiveBtn = null;

        btnBell = createSidebarActionButton("🔔", "Hộp thư thông báo", e -> {
            try {
                gui.dialog.ThongBaoForm form = new gui.dialog.ThongBaoForm(this);
                form.setVisible(true);
                updateUnreadNotificationCount();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi hiển thị Thông báo: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        menuPanel.add(buildSectionLabel("CÁ NHÂN"));
        menuPanel.add(btnBell);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 4)));

        if (roleId == 0) { // ADMIN
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

            defaultActiveBtn = btnKhachHang;

        } else if (roleId == 1) { // NHÂN VIÊN
            String cv = util.Session.chucVu != null ? util.Session.chucVu.toLowerCase() : "";
            
            if (cv.contains("kho")) {
                menuPanel.add(buildSectionLabel("QUẢN LÝ KHO BÃI"));
                menuPanel.add(btnTonKho); menuPanel.add(Box.createRigidArea(new Dimension(0, 4)));
                menuPanel.add(btnNhapKho); menuPanel.add(Box.createRigidArea(new Dimension(0, 4)));
                menuPanel.add(btnXuatKho); menuPanel.add(Box.createRigidArea(new Dimension(0, 4)));
                defaultActiveBtn = btnTonKho;
                
            } else if (cv.contains("thu mua")) {
                menuPanel.add(buildSectionLabel("ĐỐI TÁC & SẢN PHẨM"));
                menuPanel.add(btnNhaCungCap); menuPanel.add(Box.createRigidArea(new Dimension(0, 4)));
                menuPanel.add(btnSanPham); menuPanel.add(Box.createRigidArea(new Dimension(0, 4)));
                menuPanel.add(btnLoHang); menuPanel.add(Box.createRigidArea(new Dimension(0, 4)));
                menuPanel.add(btnLichSuLoHang); menuPanel.add(Box.createRigidArea(new Dimension(0, 4)));
                defaultActiveBtn = btnNhaCungCap;
            } else if (cv.contains("giao hàng")) {
                menuPanel.add(buildSectionLabel("VẬN CHUYỂN"));
                menuPanel.add(btnGiaoHang); menuPanel.add(Box.createRigidArea(new Dimension(0, 4)));
                defaultActiveBtn = btnGiaoHang;
            }
        } else { // KHÁCH HÀNG
            menuPanel.add(buildSectionLabel("MUA SẮM"));
            menuPanel.add(btnCuaHang);
            menuPanel.add(Box.createRigidArea(new Dimension(0, 4)));
            
            btnGioHangMenu = createMenuButton("🛍", "Giỏ hàng", "GioHang");
            menuPanel.add(btnGioHangMenu);
            menuPanel.add(Box.createRigidArea(new Dimension(0, 4)));
            
            menuPanel.add(btnDonHang);
            menuPanel.add(Box.createRigidArea(new Dimension(0, 4)));
            defaultActiveBtn = btnCuaHang;
        }

        JScrollPane scrollMenu = new JScrollPane(menuPanel);
        scrollMenu.setBorder(null);
        scrollMenu.getViewport().setBackground(AppColor.SIDEBAR_BG);
        scrollMenu.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0)); 

        JPanel footerPanel = buildFooter();

        sidebarPanel.add(logoPanel, BorderLayout.NORTH);
        sidebarPanel.add(scrollMenu, BorderLayout.CENTER);
        sidebarPanel.add(footerPanel, BorderLayout.SOUTH);

        if (defaultActiveBtn != null) {
            final JButton finalDefaultBtn = defaultActiveBtn;
            setActiveMenu(finalDefaultBtn);
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

        JLabel lblViewProfile = new JLabel("Xem hồ sơ") {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                setForeground(new Color(96, 165, 250));
            }
        };
        lblViewProfile.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblViewProfile.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblViewProfile.setForeground(new Color(96, 165, 250));
        lblViewProfile.setBorder(new EmptyBorder(4, 0, 0, 0));
        lblViewProfile.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showUserProfile();
            }
        });

        userText.add(lblViewProfile);

        JButton btnLogout = new JButton("Đăng xuất") {
            private boolean isHovered = false;
            {
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { isHovered = true; repaint(); }
                    @Override public void mouseExited(MouseEvent e) { isHovered = false; repaint(); }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (isHovered) {
                    g2.setColor(new Color(220, 38, 38)); 
                    setForeground(Color.WHITE);
                } else {
                    g2.setColor(Color.GRAY); 
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
                if (notificationTimer != null) {
                    notificationTimer.stop();
                }
                Session.clear(); 
                dispose();
                new AuthFrame().setVisible(true);
            }
        });

        footer.add(avatar, BorderLayout.WEST);
        footer.add(userText, BorderLayout.CENTER);
        footer.add(btnLogout, BorderLayout.SOUTH);
        return footer;
    }

    private void showUserProfile() {
        ensurePanelCreated("Profile");
        if (pnlUserProfile != null) {
            pnlUserProfile.loadProfile();
            navigateToCard("Profile");
            setActiveMenu(null);
        }
    }

    private Icon createPowerIcon(int size) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(c.getForeground()); 
                g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                
                int cx = x + size / 2;
                int cy = y + size / 2;
                
                g2.drawArc(x + 2, y + 3, size - 4, size - 4, -60, 300);
                g2.drawLine(cx, y + 1, cx, cy);
                g2.dispose();
            }
            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    // ================================================================
    // CONTENT AREA 
    // ================================================================
    private void buildContentArea() {
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(AppColor.BACKGROUND);
    }

    private JButton createMenuButton(String emojiStr, String label, String cardName) {
        JButton btn = new JButton(label) {
            private boolean isHovered = false;
            {
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { isHovered = true; repaint(); }
                    @Override public void mouseExited(MouseEvent e) { isHovered = false; repaint(); }
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
                    g2.setColor(AppColor.SIDEBAR_ACTIVE);
                    g2.fillRoundRect(marginX, marginY, w, h, arc, arc);
                    g2.setColor(Color.WHITE);
                    g2.fillRoundRect(marginX, marginY + 8, 4, h - 16, 4, 4);
                } else if (isHovered) {
                    g2.setColor(new Color(255, 255, 255, 20));
                    g2.fillRoundRect(marginX, marginY, w, h, arc, arc);
                }

                g2.dispose();
                super.paintComponent(g);
            }
        };

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
            navigateToCard(cardName); // Đã thay thế cardLayout.show() bằng hàm điều hướng
            setActiveMenu(btn);
        });

        menuButtons.add(btn);
        return btn;
    }

    private JButton createSidebarActionButton(String emojiStr, String label, ActionListener action) {
        JButton btn = new JButton(label) {
            private boolean isHovered = false;
            {
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { isHovered = true; repaint(); }
                    @Override public void mouseExited(MouseEvent e) { isHovered = false; repaint(); }
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

                if (isHovered) {
                    g2.setColor(new Color(255, 255, 255, 20));
                    g2.fillRoundRect(marginX, marginY, w, h, arc, arc);
                }

                g2.dispose();
                super.paintComponent(g);

                if (unreadNotificationCount > 0) {
                    Graphics2D gBadge = (Graphics2D) g.create();
                    gBadge.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    String countStr = String.valueOf(unreadNotificationCount);
                    gBadge.setFont(new Font("Segoe UI", Font.BOLD, 10));
                    FontMetrics badgeFm = gBadge.getFontMetrics();
                    
                    int badgeW = Math.max(16, badgeFm.stringWidth(countStr) + 6);
                    int badgeH = 16;
                    int badgeX = getWidth() - marginX - badgeW - 10;
                    int badgeY = (getHeight() - badgeH) / 2;

                    gBadge.setColor(new Color(239, 68, 68)); 
                    gBadge.fillRoundRect(badgeX, badgeY, badgeW, badgeH, badgeH, badgeH);
                    
                    gBadge.setColor(Color.WHITE); 
                    int countX = badgeX + (badgeW - badgeFm.stringWidth(countStr)) / 2;
                    int countY = badgeY + ((badgeH - badgeFm.getHeight()) / 2) + badgeFm.getAscent();
                    gBadge.drawString(countStr, countX, countY);
                    gBadge.dispose();
                }
            }
        };

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

        btn.addActionListener(action);
        return btn;
    }

    private void setActiveMenu(JButton activeBtn) {
        for (JButton b : menuButtons) {
            b.setBackground(AppColor.SIDEBAR_BG);
        }
        if (activeBtn != null) {
            activeBtn.setBackground(AppColor.SIDEBAR_ACTIVE);
        }
    }

    private Icon createEmojiIcon(String emojiText, int size) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, size));
                g2.setColor(c.getForeground());
                FontMetrics fm = g2.getFontMetrics();
                
                int textX = x + (getIconWidth() - fm.stringWidth(emojiText)) / 2;
                int textY = y + ((getIconHeight() - fm.getHeight()) / 2) + fm.getAscent();
                
                g2.drawString(emojiText, textX, textY);
                g2.dispose();
            }

            @Override public int getIconWidth() { return size + 4; }
            @Override public int getIconHeight() { return size + 4; }
        };
    }

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

    public void navigateToGioHang() {
        navigateToCard("GioHang");
        if (btnGioHangMenu != null) {
            setActiveMenu(btnGioHangMenu);
        }
        if (pnlGioHang != null) {
            pnlGioHang.loadCartItemsFromDBAsync(); 
        }
    }
}