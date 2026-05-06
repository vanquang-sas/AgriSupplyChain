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

// import util.DBConnection;
// import java.sql.Connection;
// import java.sql.ResultSet;
// import java.sql.Statement;

// public class Main {
//     public static void main(String[] args) {
//         try {
//             Connection con = DBConnection.getConnection();

//             if (con != null) {
//                 System.out.println("Kết nối thành công!");

//                 Statement st = con.createStatement();
//                 ResultSet rs = st.executeQuery("SELECT USER FROM dual");

//                 if (rs.next()) {
//                     System.out.println("Đang kết nối với USER: " + rs.getString(1));
//                 }

//             } else {
//                 System.out.println("Kết nối thất bại!");
//             }

//         } catch (Exception e) {
//             e.printStackTrace();
//         }
//     }
// }