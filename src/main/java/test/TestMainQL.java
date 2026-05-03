package test;

import gui.MainFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class TestMainQL {
    public static void main(String[] args) {
        // Chạy giao diện trên luồng (Event Dispatch Thread) để tránh lỗi giật lag
        SwingUtilities.invokeLater(() -> {
            try {
                // Cố gắng sử dụng giao diện hiện đại của hệ điều hành (Windows/Mac)
                // Nếu bạn có cài thư viện FlatLaf, hãy đổi thành: UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf());
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException e) {
                System.err.println("Không thể thiết lập giao diện hệ thống. Sử dụng mặc định của Java.");
            }

            // Khởi tạo và hiển thị MainFrame
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }
}