package gui;

import gui.panel.KhachHangPanel;
import gui.panel.NhaCungCapPanel;
import gui.panel.NhanVienPanel;
import util.AppColor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MainFrame extends JFrame {

    private JPanel sidebarPanel;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private List<JButton> menuButtons; // Dùng để quản lý trạng thái hover/active của menu

    public MainFrame() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Admin Panel - Hệ thống Quản lý Chuỗi cung ứng");
        setSize(1280, 720); // Kích thước khung cửa sổ
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Giữa màn hình
        setLayout(new BorderLayout());

        menuButtons = new ArrayList<>();

        // ==================== 1. SIDEBAR (Bên trái) ====================
        sidebarPanel = new JPanel();
        sidebarPanel.setBackground(AppColor.PRIMARY);
        sidebarPanel.setPreferredSize(new Dimension(260, 0));
        sidebarPanel.setLayout(new BorderLayout());

        // --- Header của Sidebar (Logo) ---
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 20));
        logoPanel.setBackground(AppColor.PRIMARY);
        JLabel lblLogo = new JLabel("<html><b style='font-size:16px'>⬢ Admin Panel</b></html>");
        lblLogo.setForeground(Color.WHITE);
        logoPanel.add(lblLogo);
        sidebarPanel.add(logoPanel, BorderLayout.NORTH);

        // --- Các nút Menu ---
        JPanel menuPanel = new JPanel();
        menuPanel.setBackground(AppColor.PRIMARY);
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBorder(new EmptyBorder(10, 0, 10, 0));

        // Tạo các nút (Sử dụng Unicode icon tạm, bạn có thể thay bằng ImageIcon sau)
        JButton btnTrangChu = createMenuButton("🏠  Trang chủ", "TrangChu");
        JButton btnSanPham = createMenuButton("📦  Quản lý Sản phẩm", "SanPham");
        JButton btnKhachHang = createMenuButton("👥  Quản lý Khách hàng", "KhachHang");
        JButton btnNhanVien = createMenuButton("👤  Quản lý Nhân viên", "NhanVien");
        JButton btnNhaCungCap = createMenuButton("🏢  Quản lý Nhà cung cấp", "NhaCungCap");
        JButton btnKho = createMenuButton("🏭  Quản lý Kho", "Kho");
        JButton btnThongKe = createMenuButton("📊  Báo cáo Thống kê", "ThongKe");

        menuPanel.add(btnTrangChu);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        menuPanel.add(btnSanPham);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        menuPanel.add(btnKhachHang);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        menuPanel.add(btnNhanVien);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        menuPanel.add(btnNhaCungCap);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        menuPanel.add(btnKho);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        menuPanel.add(btnThongKe);

        sidebarPanel.add(menuPanel, BorderLayout.CENTER);

        // --- Footer của Sidebar (Đăng xuất) ---
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(AppColor.PRIMARY);
        footerPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel lblUser = new JLabel("<html><b>Quản trị viên</b><br><i style='font-size:9px'>admin@system.vn</i></html>");
        lblUser.setForeground(Color.WHITE);
        
        JButton btnLogout = new JButton("Đăng xuất");
        btnLogout.setBackground(AppColor.PRIMARY_ACTIVE);
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.setBorderPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));

        footerPanel.add(lblUser, BorderLayout.CENTER);
        footerPanel.add(btnLogout, BorderLayout.SOUTH);

        sidebarPanel.add(footerPanel, BorderLayout.SOUTH);

        // ==================== 2. CONTENT AREA (Bên phải) ====================
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(AppColor.BACKGROUND);

        // Tạo sẵn các Panel chức năng (Các phần chưa code thì để 1 Panel trống làm Placeholder)
        JPanel pnlTrangChu = createPlaceholder("Giao diện Trang chủ (Dashboard)");
        JPanel pnlSanPham = createPlaceholder("Giao diện Quản lý Sản phẩm");
        KhachHangPanel pnlKhachHang = new KhachHangPanel(); // Đã code
        NhanVienPanel pnlNhanVien = new NhanVienPanel(); // Đã code
        NhaCungCapPanel pnlNhaCungCap = new NhaCungCapPanel(); // Đã code
        JPanel pnlKho = createPlaceholder("Giao diện Quản lý Kho");
        JPanel pnlThongKe = createPlaceholder("Giao diện Báo cáo Thống kê");

        // Thêm vào CardLayout với từ khóa tương ứng
        contentPanel.add(pnlTrangChu, "TrangChu");
        contentPanel.add(pnlSanPham, "SanPham");
        contentPanel.add(pnlKhachHang, "KhachHang");
        contentPanel.add(pnlNhanVien, "NhanVien");
        contentPanel.add(pnlNhaCungCap, "NhaCungCap");
        contentPanel.add(pnlKho, "Kho");
        contentPanel.add(pnlThongKe, "ThongKe");

        // ==================== 3. LẮP RÁP ====================
        add(sidebarPanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
        
        // Mặc định chọn Trang chủ
        setActiveMenu(btnTrangChu);
    }

    // Helper tạo nút Menu
    private JButton createMenuButton(String text, String cardName) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setBackground(AppColor.PRIMARY);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(10, 20, 10, 10));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Sự kiện hover và click
        btn.addActionListener(e -> {
            cardLayout.show(contentPanel, cardName);
            setActiveMenu(btn);
        });

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (btn.getBackground() != AppColor.PRIMARY_ACTIVE) {
                    btn.setBackground(AppColor.PRIMARY_HOVER);
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (btn.getBackground() != AppColor.PRIMARY_ACTIVE) {
                    btn.setBackground(AppColor.PRIMARY);
                }
            }
        });

        menuButtons.add(btn);
        return btn;
    }

    // Helper set trạng thái nút Menu đang được chọn
    private void setActiveMenu(JButton activeBtn) {
        for (JButton btn : menuButtons) {
            btn.setBackground(AppColor.PRIMARY);
        }
        activeBtn.setBackground(AppColor.PRIMARY_ACTIVE); // Màu xanh đậm hơn cho tab đang mở
    }

    // Helper tạo panel tạm thời cho các trang chưa code
    private JPanel createPlaceholder(String title) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(AppColor.BACKGROUND);
        JLabel lbl = new JLabel(title, SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lbl.setForeground(AppColor.TEXT_SECONDARY);
        p.add(lbl, BorderLayout.CENTER);
        return p;
    }
}