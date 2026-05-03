package gui.component;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import java.awt.Cursor;
import java.awt.Font;
import javax.swing.Icon;
import javax.swing.JTextField;

public class IntegratedSearch extends JTextField {

    public IntegratedSearch() {
        initComponent();
    }

    private void initComponent() {
        // 1. Chữ mờ gợi ý (Placeholder)
        this.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Tìm kiếm mã vận đơn, tên nông sản, kho bãi...");

        // 2. Nút X (Clear) tự động hiện ra ở cuối khi người dùng gõ chữ
        this.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);

        // 3. Bo góc tròn và chỉnh khoảng cách viền 
        this.putClientProperty(FlatClientProperties.STYLE, ""
                + "arc:20;"
                + "margin:5,10,5,10;"
                + "focusColor:#16A34A;" 
                + "hoverBorderColor:#22C55E;"); 

        try {
            // Khuyến nghị dùng file SVG để icon không bị vỡ hạt khi phóng to màn hình
            Icon searchIcon = new FlatSVGIcon("icons/search.svg", 16, 16, getClass().getClassLoader());

            this.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, searchIcon);
        } catch (Exception e) {
            System.err.println("Lưu ý: Chưa tìm thấy file icon/search.svg");
        }

        this.setFont(new Font("Arial", Font.PLAIN, 14));
        this.setCursor(new Cursor(Cursor.TEXT_CURSOR));
    }
//    public static void main(String[] args) {
//        // Cài đặt FlatLaf để thanh search hiển thị đúng style
//        try {
//            javax.swing.UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf());
//        } catch (Exception ex) {
//            System.err.println("Lỗi thiết lập FlatLaf");
//        }
//
//        // Tạo một khung cửa sổ (Frame) tạm thời để chứa thanh search
//        java.awt.EventQueue.invokeLater(() -> {
//            javax.swing.JFrame frame = new javax.swing.JFrame("Test Integrated Search");
//            frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
//            frame.setSize(500, 200);
//            frame.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 20, 50)); // Căn giữa
//
//            // Khởi tạo thanh search của bạn
//            IntegratedSearch searchBar = new IntegratedSearch();
//            searchBar.setPreferredSize(new java.awt.Dimension(350, 40)); // Kích thước test
//
//            frame.add(searchBar);
//            frame.setLocationRelativeTo(null); // Hiển thị ở giữa màn hình
//            frame.setVisible(true);
//        });
//    }
// hàm main có để kéo thả vô thẳng main form (kéo từ thư mục) nhma t tính lưu nó dô pallete kéo cho dễ
}