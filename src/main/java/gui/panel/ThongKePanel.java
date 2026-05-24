package gui.panel;

import util.AppColor;
import javax.swing.*;

import gui.panel.BaoCaoThongKe.DoanhThuPanel;
import gui.panel.BaoCaoThongKe.TopSanPhamPanel;
import gui.panel.BaoCaoThongKe.TrangThaiPanel;
import gui.panel.BaoCaoThongKe.NhanVienPanel;
import gui.panel.BaoCaoThongKe.LichSuGiaPanel;
import gui.panel.BaoCaoThongKe.CongNoPanel;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ThongKePanel extends JPanel {
    private CardLayout cardLayout;
    private JPanel mainContent;
    
    // Khai báo các Panel con chuyên biệt
    private TopSanPhamPanel pnlTopSanPham;
    private DoanhThuPanel pnlDoanhThu; 
    private TrangThaiPanel pnlTrangThai;
    private NhanVienPanel pnlNhanVien;
    private LichSuGiaPanel pnlLichSuGia;
    private CongNoPanel pnlCongNo;

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
        
        // 2. Khởi tạo và add Panel đã có
        pnlTopSanPham = new TopSanPhamPanel();
        pnlTrangThai  = new TrangThaiPanel();
        pnlDoanhThu   = new DoanhThuPanel();
        pnlNhanVien   = new NhanVienPanel();
        pnlLichSuGia  = new LichSuGiaPanel();
        pnlCongNo     = new CongNoPanel();
        mainContent.add(wrapWithBackButton(pnlTopSanPham, "Thống kê Top sản phẩm"), "TOPSP");
        mainContent.add(wrapWithBackButton(pnlTrangThai, "Trạng thái đơn hàng"), "TRANGTHAI");
        mainContent.add(wrapWithBackButton(pnlDoanhThu, "Báo cáo Doanh thu"), "DOANHTHU");
        mainContent.add(wrapWithBackButton(pnlLichSuGia, "Báo cáo biến động giá"), "LICHSUGIA");
        mainContent.add(wrapWithBackButton(pnlNhanVien, "Thống kê Nhân viên"), "NHANVIEN");
        mainContent.add(wrapWithBackButton(pnlCongNo, "Quản lý công nợ"), "CONGNO");

    }

    // --- MÀN HÌNH MENU CHÍNH ---
    private JPanel createMenuThongKe() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(AppColor.BACKGROUND);
        
        JPanel grid = new JPanel(new GridLayout(2, 0, 30, 20));
        grid.setBackground(AppColor.BACKGROUND);
        
        grid.add(createMenuButton("💰", "Báo cáo Doanh thu", "Xem theo tháng/năm", "DOANHTHU"));
        grid.add(createMenuButton("📦", "Top Sản phẩm", "Lọc theo số lượng/thời gian", "TOPSP"));
        grid.add(createMenuButton("🥧", "Trạng thái Đơn hàng", "Tỷ lệ đơn hàng thành công", "TRANGTHAI"));
        grid.add(createMenuButton("📈", "Biến động giá", "Lịch sử mua/bán giá sản phẩm", "LICHSUGIA"));
        grid.add(createMenuButton("👨‍💼", "Thống kê Nhân viên", "Xem hiệu suất và thông tin nhân viên", "NHANVIEN"));
        grid.add(createMenuButton("💳", "Quản lý công nợ", "Thống kê chi tiết công nợ", "CONGNO"));
        
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

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        header.setBackground(AppColor.BACKGROUND);
        
        // -- NÚT QUAY LẠI --
        JButton btnBack = new JButton("<< Quay lại Menu");
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnBack.setForeground(AppColor.PRIMARY); 
        btnBack.setBorderPainted(false); 
        btnBack.setContentAreaFilled(false); 
        btnBack.setFocusPainted(false);
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hiệu ứng hover nhạt màu khi đưa chuột vào
        btnBack.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnBack.setForeground(new Color(0, 102, 204)); 
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnBack.setForeground(AppColor.PRIMARY);
            }
        });

        btnBack.addActionListener(e -> cardLayout.show(mainContent, "MENU"));
        
        JLabel lblTitle = new JLabel("|  " + titleText);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));

        header.add(btnBack);
        header.add(lblTitle);

        wrapper.add(header, BorderLayout.NORTH);
        wrapper.add(childPanel, BorderLayout.CENTER);
        return wrapper;
    }

}