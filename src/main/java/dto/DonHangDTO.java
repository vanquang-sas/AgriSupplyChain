package dto;
import java.util.Date;

public class DonHangDTO {
    private String maDH;              
    private String maKH;              
    private String maNV;             
    private String diaChiGiaoHang;     
    private Date tgDat;               
    private Date tgGiaoDK;            
    private double phiVanChuyen;       
    private double tongTien;          
    private String trangThaiDH;       
    

    private int trangThaiTT;          
    private String danhSachSP;        // Chuỗi danh sách sản phẩm từ LISTAGG (ví dụ: "2 Xoài, 1 Chuối")

    public DonHangDTO() {}

    public DonHangDTO(String maDH, String maKH, String maNV, String diaChiGiaoHang, Date tgDat, Date tgGiaoDK, double phiVanChuyen, double tongTien, String trangThaiDH) {
        this.maDH = maDH; 
        this.maKH = maKH; 
        this.maNV = maNV; 
        this.diaChiGiaoHang = diaChiGiaoHang;
        this.tgDat = tgDat; 
        this.tgGiaoDK = tgGiaoDK; 
        this.phiVanChuyen = phiVanChuyen; 
        this.tongTien = tongTien; 
        this.trangThaiDH = trangThaiDH;
    }

    public DonHangDTO(String maDH, String maKH, String maNV, String diaChiGiaoHang, 
                      Date tgDat, Date tgGiaoDK, double phiVanChuyen, double tongTien, 
                      String trangThaiDH, int trangThaiTT, String danhSachSP) {
        this(maDH, maKH, maNV, diaChiGiaoHang, tgDat, tgGiaoDK, phiVanChuyen, tongTien, trangThaiDH);
        this.trangThaiTT = trangThaiTT;
        this.danhSachSP = danhSachSP;
    }

    public double getPhiVanChuyen() { return phiVanChuyen; }
    public void setPhiVanChuyen(double phiVanChuyen) { this.phiVanChuyen = phiVanChuyen; }
    
    public String getMaDH() { return maDH; }
    public void setMaDH(String maDH) { this.maDH = maDH; }
    public String getMaKH() { return maKH; }
    public void setMaKH(String maKH) { this.maKH = maKH; }
    public String getMaNV() { return maNV; }
    public void setMaNV(String maNV) { this.maNV = maNV; }
    public String getDiaChiGiaoHang() { return diaChiGiaoHang; }
    public void setDiaChiGiaoHang(String diaChiGiaoHang) { this.diaChiGiaoHang = diaChiGiaoHang; }
    public Date getTgDat() { return tgDat; }
    public void setTgDat(Date tgDat) { this.tgDat = tgDat; }
    public Date getTgGiaoDK() { return tgGiaoDK; }
    public void setTgGiaoDK(Date tgGiaoDK) { this.tgGiaoDK = tgGiaoDK; }
    public double getTongTien() { return tongTien; }
    public void setTongTien(double tongTien) { this.tongTien = tongTien; }
    public String getTrangThaiDH() { return trangThaiDH; }
    public void setTrangThaiDH(String trangThaiDH) { this.trangThaiDH = trangThaiDH; }
    

    public int getTrangThaiTT() { return trangThaiTT; }
    public void setTrangThaiTT(int trangThaiTT) { this.trangThaiTT = trangThaiTT; }
    
    public String getDanhSachSP() { return danhSachSP; }
    public void setDanhSachSP(String danhSachSP) { this.danhSachSP = danhSachSP; }
}