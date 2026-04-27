import util.DBConnection;

public class Main {
    public static void main(String[] args) {
        if (DBConnection.getConnection() != null) {
            System.out.println("Kết nối thành công!");
        } else {
            System.out.println("Kết nối thất bại!");
        }
    }
}