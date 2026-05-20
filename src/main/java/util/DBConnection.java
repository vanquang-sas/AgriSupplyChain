package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    // private static final String URL = "jdbc:oracle:thin:@localhost:1521/orclpdb";
    private static final String URL = "jdbc:oracle:thin:@localhost:1521/XEPDB1";

    // private static final String USER = "AGRIAPP";
    private static final String USER = "AGRI";
    private static final String PASS = "123456";

    public static Connection getConnection() {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Lỗi kết nối CSDL! Hãy kiểm tra lại DBConnection.java");
            e.printStackTrace();
            return null;
        }
    }
}