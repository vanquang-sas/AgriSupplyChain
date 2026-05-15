package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:oracle:thin:@localhost:1521:xe";
    private static final String USER = "AGRIAPP";
    private static final String PASS = "123456";

    public static Connection getConnection() {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Connection con = DriverManager.getConnection(URL, USER, PASS);
            System.out.println("DEBUG: [DBConnection] Kết nối thành công tới " + URL + " as " + USER);
            return con;
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Lỗi kết nối CSDL! Hãy kiểm tra lại DBConnection.java");
            e.printStackTrace();
            return null;
        }
    }
}