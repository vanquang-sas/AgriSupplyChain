import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class FixDB {
    public static void main(String[] args) {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521/orclpdb", "AGRIAPP", "123456");
            Statement stmt = con.createStatement();
            String sql = "CREATE OR REPLACE FUNCTION FN_GET_DS_NHAPKHO\n" +
                    "RETURN SYS_REFCURSOR\n" +
                    "IS\n" +
                    "    v_cursor SYS_REFCURSOR;\n" +
                    "BEGIN\n" +
                    "    OPEN v_cursor FOR\n" +
                    "        SELECT\n" +
                    "            CTLH.MaCTLH,\n" +
                    "            SP.TenSP,\n" +
                    "            CTLH.SoLuong,\n" +
                    "            ' ' AS MaKho,\n" +
                    "            SP.BaoQuan,\n" +
                    "            ' ' AS ViTri,\n" +
                    "            ' ' AS NgayHetHan,\n" +
                    "            LH.TrangThaiLH\n" +
                    "        FROM CHITIETLOHANG CTLH\n" +
                    "        JOIN SANPHAM SP ON CTLH.MaSP = SP.MaSP\n" +
                    "        JOIN LOHANG LH ON CTLH.MaLH = LH.MaLH\n" +
                    "        WHERE LH.TrangThaiLH IN (N'Chờ nhập kho', N'Chờ kiểm duyệt');\n" +
                    "    RETURN v_cursor;\n" +
                    "END;";
            stmt.execute(sql);
            System.out.println("Function updated successfully.");
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
