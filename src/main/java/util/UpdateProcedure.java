package util;

import java.sql.Connection;
import java.sql.Statement;

public class UpdateProcedure {

    public static void main(String[] args) {
        System.out.println("=== BAT DAU CAP NHAT STORED PROCEDURE SP_YEUCAU_XUATKHO ===");
        
        String sql = "CREATE OR REPLACE PROCEDURE SP_YEUCAU_XUATKHO (p_MaDH IN VARCHAR2) IS\n" +
                "    v_SoLuongCan NUMBER;\n" +
                "    v_SoLuongXuat NUMBER;\n" +
                "    v_TrangThaiDH NVARCHAR2(50);\n" +
                "\n" +
                "BEGIN\n" +
                "    SELECT TrangThaiDH INTO v_TrangThaiDH FROM DONHANG WHERE MaDH = p_MaDH;\n" +
                "    IF v_TrangThaiDH IN (N'Đang giao', N'Hoàn thành', N'Đã huỷ') THEN\n" +
                "        RAISE_APPLICATION_ERROR(-20023, 'Trạng thái đơn hàng không hợp lệ!');\n" +
                "    END IF;\n" +
                "\n" +
                "    FOR rec_CTDH IN (SELECT MaSP, SoLuong FROM CHITIETDONHANG WHERE MaDH = p_MaDH) LOOP\n" +
                "        v_SoLuongCan := rec_CTDH.SoLuong;\n" +
                "        FOR rec_TK IN (\n" +
                "            SELECT TK.MaTonKho, TK.SLKhaDung FROM TONKHO TK\n" +
                "            WHERE TK.MaSP = rec_CTDH.MaSP AND TK.SLKhaDung > 0 AND TK.TGHetHan >= TRUNC(SYSDATE)\n" +
                "            ORDER BY TK.TGHetHan ASC, TK.TGNhapKho ASC \n" +
                "            FOR UPDATE\n" +
                "        ) LOOP\n" +
                "            EXIT WHEN v_SoLuongCan = 0;\n" +
                "            IF rec_TK.SLKhaDung >= v_SoLuongCan THEN\n" +
                "                v_SoLuongXuat := v_SoLuongCan; v_SoLuongCan := 0;\n" +
                "            ELSE\n" +
                "                v_SoLuongXuat := rec_TK.SLKhaDung; v_SoLuongCan := v_SoLuongCan - rec_TK.SLKhaDung;\n" +
                "            END IF;\n" +
                "            INSERT INTO XUATKHO (MaDH, MaSP, MaTonKho, MaNV, SLXuat, TGCapNhat, TrangThaiXK) \n" +
                "            VALUES (p_MaDH, rec_CTDH.MaSP, rec_TK.MaTonKho, NULL, v_SoLuongXuat, SYSDATE, N'Tạm giữ');\n" +
                "        END LOOP;\n" +
                "        IF v_SoLuongCan > 0 THEN \n" +
                "            RAISE_APPLICATION_ERROR(-20024, 'Kho không đủ hàng cho: ' || rec_CTDH.MaSP); \n" +
                "        END IF;\n" +
                "    END LOOP;\n" +
                "EXCEPTION\n" +
                "    WHEN OTHERS THEN RAISE;\n" +
                "END;";

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                System.err.println("Loi: Khong the ket noi toi CSDL! Hay kiem tra lai DBConnection.java");
                return;
            }
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(sql);
                System.out.println("Thành công: Đã cập nhật stored procedure SP_YEUCAU_XUATKHO trong database!");
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi chạy cập nhật database:");
            e.printStackTrace();
        }
    }
}
