package dto;

import java.util.Date;

public class DonHangDTO {
private String maDH;
    private String maKH;
    private String maNV;
    private Date tgDat;
    private Date tgGiaoYC;
    private Date tgGiaoTT;
    private String diaChiGiaoHang;
    private String lyDoHuy;
    private double phiVanChuyen;
    private double tongTienHang;
    private double giamGia;
    private double tongTien;
    private String trangThaiDH;
    private int trangThaiTT;
    private String phuongThucTT;
    private String danhSachSP; 

    public DonHangDTO() {}

    public DonHangDTO(String maDH, String maKH, String maNV, Date tgDat, Date tgGiaoYC, Date tgGiaoTT, 
                      String diaChiGiaoHang, String lyDoHuy, double phiVanChuyen, double tongTienHang, 
                      double giamGia, double tongTien, String trangThaiDH, int trangThaiTT, 
                      String phuongThucTT, String danhSachSP) {
        this.maDH = maDH; this.maKH = maKH; this.maNV = maNV;
        this.tgDat = tgDat; this.tgGiaoYC = tgGiaoYC; this.tgGiaoTT = tgGiaoTT;
        this.diaChiGiaoHang = diaChiGiaoHang; this.lyDoHuy = lyDoHuy;
        this.phiVanChuyen = phiVanChuyen; this.tongTienHang = tongTienHang;
        this.giamGia = giamGia; this.tongTien = tongTien;
        this.trangThaiDH = trangThaiDH; this.trangThaiTT = trangThaiTT; 
        this.phuongThucTT = phuongThucTT; this.danhSachSP = danhSachSP;
    }

    public String getMaDH() { return maDH; }
    public void setMaDH(String maDH) { this.maDH = maDH; }
    
    public String getMaKH() { return maKH; }
    public void setMaKH(String maKH) { this.maKH = maKH; }
    
    public String getMaNV() { return maNV; }
    public void setMaNV(String maNV) { this.maNV = maNV; }
    
    public Date getTgDat() { return tgDat; }
    public void setTgDat(Date tgDat) { this.tgDat = tgDat; }
    
    public Date getTgGiaoYC() { return tgGiaoYC; }
    public void setTgGiaoYC(Date tgGiaoYC) { this.tgGiaoYC = tgGiaoYC; }
    
    public Date getTgGiaoTT() { return tgGiaoTT; }
    public void setTgGiaoTT(Date tgGiaoTT) { this.tgGiaoTT = tgGiaoTT; }
    
    public String getDiaChiGiaoHang() { return diaChiGiaoHang; }
    public void setDiaChiGiaoHang(String diaChiGiaoHang) { this.diaChiGiaoHang = diaChiGiaoHang; }
    
    public String getLyDoHuy() { return lyDoHuy; }
    public void setLyDoHuy(String lyDoHuy) { this.lyDoHuy = lyDoHuy; }
    
    public double getPhiVanChuyen() { return phiVanChuyen; }
    public void setPhiVanChuyen(double phiVanChuyen) { this.phiVanChuyen = phiVanChuyen; }
    
    public double getTongTienHang() { return tongTienHang; }
    public void setTongTienHang(double tongTienHang) { this.tongTienHang = tongTienHang; }
    
    public double getGiamGia() { return giamGia; }
    public void setGiamGia(double giamGia) { this.giamGia = giamGia; }
    
    public double getTongTien() { return tongTien; }
    public void setTongTien(double tongTien) { this.tongTien = tongTien; }
    
    public String getTrangThaiDH() { return trangThaiDH; }
    public void setTrangThaiDH(String trangThaiDH) { this.trangThaiDH = trangThaiDH; }
    


    public int getTrangThaiTT() { return trangThaiTT; }
    public void setTrangThaiTT(int trangThaiTT) { this.trangThaiTT = trangThaiTT; }
    
    public String getDanhSachSP() { return danhSachSP; }
    public void setDanhSachSP(String danhSachSP) { this.danhSachSP = danhSachSP; }

    
    public String getPhuongThucTT() { return phuongThucTT; }
    public void setPhuongThucTT(String phuongThucTT) { this.phuongThucTT = phuongThucTT; }

}