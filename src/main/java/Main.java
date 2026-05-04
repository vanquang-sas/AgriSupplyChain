import gui.AuthFrame;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        // Áp dụng LookAndFeel của hệ điều hành cho giao diện đẹp hơn
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Khởi chạy màn hình Đăng nhập tổng
        java.awt.EventQueue.invokeLater(() -> {
            new AuthFrame().setVisible(true);
        });
    }
}