package gui.khohang;



import javax.swing.*;
import java.awt.*;
import util.AppColor;

public class MainFrame extends javax.swing.JFrame {

    // Panel chính bên phải dùng CardLayout để chuyển đổi nội dung
    private JPanel mainContent;
    private CardLayout cardLayout;

    public MainFrame() {
        initComponents();
        setTitle("Hệ Thống Quản Lý Kho");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void initComponents() {
        // Layout tổng quát của JFrame là BorderLayout
        setLayout(new BorderLayout());

        // --- 1. SIDEBAR PANEL (Bên trái) ---
        JPanel sideBar = new JPanel();
        sideBar.setPreferredSize(new Dimension(200, 600));
        sideBar.setBackground(AppColor.SURFACE);
        sideBar.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 20));

        // Logo tượng trưng
        JLabel lbLogo = new JLabel("AGRI APP");
        lbLogo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lbLogo.setForeground(AppColor.TEXT_PRIMARY);
        sideBar.add(lbLogo);

        // Các nút chức năng
        JButton btnNhap = createSideBarButton("Nhập Kho");
        JButton btnXuat = createSideBarButton("Xuất Kho");
        JButton btnTon = createSideBarButton("Tồn Kho");

        sideBar.add(btnNhap);
        sideBar.add(btnXuat);
        sideBar.add(btnTon);

        // --- 2. CONTENT AREA (Bên phải) ---
        cardLayout = new CardLayout();
        mainContent = new JPanel(cardLayout);

        // Trang Nhập Kho: Lấy content từ class NhapKho của bạn
        // Lưu ý: Bạn nên sửa class NhapKho thành JPanel thay vì JFrame 
        // để add vào đây mượt mà hơn.
        NhapKhoPanel NhapKho = new NhapKhoPanel();
        // Nếu NhapKho là JFrame, ta lấy ContentPane của nó
        mainContent.add(NhapKho, "PAGE_NHAP");

        // Trang Xuất Kho & Tồn Kho (Tượng trưng)
        mainContent.add(createDummyPanel("Giao diện XUẤT KHO"), "PAGE_XUAT");
        mainContent.add(createDummyPanel("Giao diện TỒN KHO"), "PAGE_TON");

        // --- 3. SỰ KIỆN CHUYỂN TRANG ---
        btnNhap.addActionListener(e -> cardLayout.show(mainContent, "PAGE_NHAP"));
        btnXuat.addActionListener(e -> cardLayout.show(mainContent, "PAGE_XUAT"));
        btnTon.addActionListener(e -> cardLayout.show(mainContent, "PAGE_TON"));

        // Thêm vào JFrame
        add(sideBar, BorderLayout.WEST);
        add(mainContent, BorderLayout.CENTER);
    }
    
    private JButton activeBtn = null; // Lưu nút đang được chọn
    
    //khi an vao thi giu mau tai do
    private void updateActiveButton(JButton clickedBtn) {
    // 1. Reset nút cũ về màu bình thường 
    if (activeBtn != null) {
        activeBtn.setBackground(AppColor.SURFACE);
        activeBtn.setForeground(AppColor.TEXT_PRIMARY);
    }

    // 2. Thiết lập nút mới là active
    activeBtn = clickedBtn;

    // 3. Đổi màu nút mới
    activeBtn.setBackground(AppColor.PRIMARY_ACTIVE);
    activeBtn.setForeground(AppColor.TEXT_PRIMARY);
    }

    // Hàm tạo nút sidebar cho đẹp và đồng nhất
    private JButton createSideBarButton(String text) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(180, 40));
        btn.setFocusPainted(false);
        btn.setBackground(AppColor.SURFACE);
        btn.setForeground(AppColor.TEXT_PRIMARY);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        // --- THÊM HIỆU ỨNG HOVER TẠI ĐÂY ---
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                //Hieu ung HOVER
                if (btn != activeBtn) {
                btn.setBackground(AppColor.PRIMARY_HOVER);
            }
                btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR)); // Đổi con trỏ thành hình bàn tay
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                // Khi di chuột ra: Trả lại màu mặc định ban đầu
               if (btn != activeBtn) {
                btn.setBackground(AppColor.SURFACE);
            }
            }

            @Override
            public void mousePressed(java.awt.event.MouseEvent evt) {
                // Hiệu ứng khi click xuống 
                updateActiveButton(btn);
            }
        });
        return btn;
    }

    // Hàm tạo panel tạm thời cho Xuất/Tồn kho
    private JPanel createDummyPanel(String text) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.add(new JLabel(text, JLabel.CENTER));
        return panel;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        java.awt.EventQueue.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}