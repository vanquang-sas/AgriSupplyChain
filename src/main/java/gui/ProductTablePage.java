package gui;

import com.formdev.flatlaf.FlatClientProperties;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import raven.modal.Toast;

public class ProductTablePage extends JPanel {

    public ProductTablePage() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // --- 1. TIÊU ĐỀ VÀ MÔ TẢ ---
        JPanel headerPanel = new JPanel(new GridLayout(2, 1));
        JLabel title = new JLabel("Danh sách Nông sản");
        title.setFont(new Font("sansserif", Font.BOLD, 24));
        JLabel subtitle = new JLabel("Quản lý và theo dõi thông tin hàng hóa trong chuỗi cung ứng.");
        subtitle.setForeground(Color.GRAY);
        headerPanel.add(title);
        headerPanel.add(subtitle);

        // --- 2. THANH CÔNG CỤ (Search & Buttons) ---
        JPanel actionPanel = new JPanel(new BorderLayout());
        actionPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

        // Thanh tìm kiếm (Style FlatLaf)
        JTextField txtSearch = new JTextField();
        txtSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Tìm kiếm sản phẩm...");
        txtSearch.setPreferredSize(new Dimension(300, 35));

        // Nhóm nút chức năng
        JPanel buttonsGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnCreate = new JButton("Thêm mới");
        JButton btnEdit = new JButton("Sửa");
        JButton btnDelete = new JButton("Xóa");
        
        // Style nhanh cho nút
        btnCreate.setBackground(new Color(46, 204, 113));
        btnCreate.setForeground(Color.WHITE);

        buttonsGroup.add(btnCreate);
        buttonsGroup.add(btnEdit);
        buttonsGroup.add(btnDelete);

        actionPanel.add(txtSearch, BorderLayout.WEST);
        actionPanel.add(buttonsGroup, BorderLayout.EAST);

        // --- 3. BẢNG DỮ LIỆU (JTable) ---
        String[] columns = {"#", "Tên Sản Phẩm", "Ngày Nhập", "Giá Trị", "Vị Trí", "Mô Tả"};
        Object[][] data = {
            {"1", "Gạo ST25", "20-April-2026", "$1,750", "Kho Sóc Trăng", "Gạo chất lượng cao xuất khẩu..."},
            {"2", "Xoài Cát Hòa Lộc", "15-May-2026", "$1,200", "Tiền Giang", "Trái cây đặc sản vùng ĐBSCL..."},
            {"3", "Cà Phê Robusta", "20-May-2026", "$1,500", "Đắk Lắk", "Hạt cà phê nguyên chất từ vùng cao..."},
            {"4", "Thanh Long", "25-May-2026", "$1,300", "Bình Thuận", "Sản phẩm đạt chuẩn GlobalGAP..."}
        };

        DefaultTableModel model = new DefaultTableModel(data, columns);
        JTable table = new JTable(model);
        
        // Làm đẹp bảng với FlatLaf
        table.setRowHeight(40);
        table.setShowVerticalLines(false);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setFont(new Font("sansserif", Font.BOLD, 12));
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));

        // --- Gắn tất cả vào Panel chính ---
        add(headerPanel, BorderLayout.NORTH);
        add(actionPanel, BorderLayout.CENTER); // Phần chứa Search và Table thực tế nên dùng JPanel lồng
        
        // Tạo một Panel trung tâm để chứa cả Action và Table
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(actionPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(centerPanel, BorderLayout.CENTER);
        
        // Sự kiện mẫu cho nút
        btnDelete.addActionListener(e -> {
            Toast.show(this, Toast.Type.WARNING, "Bạn cần chọn một dòng để xóa!");
        });
    }
}