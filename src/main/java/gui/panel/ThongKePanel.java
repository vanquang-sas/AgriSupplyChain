package gui.panel;

import util.AppColor;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ThongKePanel extends JPanel {
    private CardLayout cardLayout;
    private JPanel mainContent;
    
    // Khai báo các Panel con chuyên biệt
    private TopSanPhamPanel pnlTopSanPham;
    // private DoanhThuPanel pnlDoanhThu; // Sẽ khởi tạo khi bạn code xong file này
    // private TrangThaiPanel pnlTrangThai; // Sẽ khởi tạo khi bạn code xong file này

    public ThongKePanel() {
        setLayout(new BorderLayout());
        setBackground(AppColor.BACKGROUND);
        
        cardLayout = new CardLayout();
        mainContent = new JPanel(cardLayout);
        
        initComponents();
        
        add(mainContent, BorderLayout.CENTER);
        cardLayout.show(mainContent, "MENU");
    }

    private void initComponents() {
        // 1. Khởi tạo màn hình Menu chính
        mainContent.add(createMenuThongKe(), "MENU");
        
        // 2. Khởi tạo và add TopSanPhamPanel đã có
        pnlTopSanPham = new TopSanPhamPanel();
        
        // Thêm nút "Quay lại" vào TopSanPhamPanel từ bên ngoài 
        // Hoặc bạn có thể thêm nút này trực tiếp trong file TopSanPhamPanel.java
        mainContent.add(wrapWithBackButton(pnlTopSanPham, "Thống kê Top Nông sản"), "TOPSP");
        
        // 3. Các chức năng còn lại (Tạm thời dùng Placeholder hoặc khởi tạo Panel riêng)
        mainContent.add(createDetailPlaceholder("DOANHTHU", "Báo cáo Doanh thu"), "DOANHTHU");
        mainContent.add(createDetailPlaceholder("TRANGTHAI", "Trạng thái đơn hàng"), "TRANGTHAI");
    }

    // --- MÀN HÌNH MENU CHÍNH ---
    private JPanel createMenuThongKe() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(AppColor.BACKGROUND);
        
        JPanel grid = new JPanel(new GridLayout(1, 3, 30, 0));
        grid.setBackground(AppColor.BACKGROUND);
        
        grid.add(createMenuButton("💰", "Báo cáo Doanh thu", "Xem theo tháng/năm", "DOANHTHU"));
        grid.add(createMenuButton("📦", "Top Sản phẩm", "Lọc theo số lượng/thời gian", "TOPSP"));
        grid.add(createMenuButton("🥧", "Trạng thái Đơn hàng", "Tỷ lệ đơn hàng thành công", "TRANGTHAI"));
        
        panel.add(grid);
        return panel;
    }

    private JPanel createMenuButton(String icon, String title, String sub, String cardName) {
        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.Y_AXIS));
        btnPanel.setBackground(AppColor.SURFACE);
        btnPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppColor.BORDER, 1, true),
                BorderFactory.createEmptyBorder(40, 40, 40, 40)));
        btnPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel lblIcon = new JLabel(icon);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 50));
        lblIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblSub = new JLabel(sub);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(Color.GRAY);
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnPanel.add(lblIcon);
        btnPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        btnPanel.add(lblTitle);
        btnPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        btnPanel.add(lblSub);

        btnPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cardLayout.show(mainContent, cardName);
            }
            @Override
            public void mouseEntered(MouseEvent e) { btnPanel.setBackground(new Color(245, 250, 255)); }
            @Override
            public void mouseExited(MouseEvent e) { btnPanel.setBackground(AppColor.SURFACE); }
        });

        return btnPanel;
    }

    // --- HÀM TRANG TRÍ PANEL CON VỚI NÚT QUAY LẠI ---
    private JPanel wrapWithBackButton(JPanel childPanel, String titleText) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(AppColor.BACKGROUND);

        // Header có nút Back
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        header.setBackground(AppColor.BACKGROUND);
        
        JButton btnBack = new JButton("⬅ Quay lại Menu");
        btnBack.setFocusPainted(false);
        btnBack.addActionListener(e -> cardLayout.show(mainContent, "MENU"));
        
        JLabel lblTitle = new JLabel("|  " + titleText);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));

        header.add(btnBack);
        header.add(lblTitle);

        wrapper.add(header, BorderLayout.NORTH);
        wrapper.add(childPanel, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel createDetailPlaceholder(String cardName, String title) {
        JPanel p = new JPanel(new BorderLayout());
        p.add(new JLabel("Đang phát triển chức năng " + title, SwingConstants.CENTER));
        return wrapWithBackButton(p, title);
    }
}