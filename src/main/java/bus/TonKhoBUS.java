/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bus;

import dao.TonKhoDAO;
import java.util.ArrayList;

public class TonKhoBUS {
    private TonKhoDAO tonKhoDAO = new TonKhoDAO();

    public TonKhoBUS() {
        // Constructor rỗng
    }

    // Hàm lấy danh sách hiển thị
    public ArrayList<Object[]> getDanhSachTonKho() {
        return tonKhoDAO.getDanhSachTonKho();
    }
    
    public boolean capNhatSoLuong(String maTonKho, double soLuongMoi) {
        return tonKhoDAO.capNhatSoLuong(maTonKho, soLuongMoi);
    }

    public boolean xoaTonKho(String maTonKho) {
        return tonKhoDAO.xoaTonKho(maTonKho);
    }
    
}
