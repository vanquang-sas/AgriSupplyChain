/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bus;

import dao.ThongBaoDAO;
import java.util.ArrayList;

public class ThongBaoBUS {
    private ThongBaoDAO thongBaoDAO = new ThongBaoDAO();

    public ArrayList<Object[]> getDanhSachThongBao() {
        return thongBaoDAO.getDanhSachThongBao();
    }
    
    public int getSoLuongChuaDoc() {
        return thongBaoDAO.getSoLuongChuaDoc();
    }
    
    public void danhDauDaDocTatCa() {
        thongBaoDAO.danhDauDaDocTatCa();
    }
    
    public void kiemTraHetHan() {
        thongBaoDAO.kiemTraHetHan();
    }

    public void xoaTatCaThongBao() {
        thongBaoDAO.xoaTatCaThongBao();
    }
}
