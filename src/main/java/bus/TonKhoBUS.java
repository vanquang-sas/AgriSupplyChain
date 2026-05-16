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

    public ArrayList<Object[]> getDanhSachTonKhoTongHop() {
        return tonKhoDAO.getDanhSachTonKhoTongHop();
    }

    public ArrayList<Object[]> getChiTietTonKhoByMaSP(String maSP) {
        return tonKhoDAO.getChiTietTonKhoByMaSP(maSP);
    }

    public boolean capNhatTonKho(String maTonKho, double soLuongMoi, String viTriMoi) {
        return tonKhoDAO.capNhatTonKho(maTonKho, soLuongMoi, viTriMoi);
    }

    public boolean xoaTonKho(String maTonKho) {
        return tonKhoDAO.xoaTonKho(maTonKho);
    }

}
