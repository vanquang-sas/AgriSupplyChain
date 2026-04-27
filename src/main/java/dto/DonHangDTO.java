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
    private double tongTienHang; 
    private String phuongThucTT;

    public DonHangDTO() {}

    public DonHangDTO(String maDH, String maKH, String maNV, String diaChiGiaoHang, Date tgDat, Date tgGiaoDK, double phiVanChuyen, double tongTien, String trangThaiDH) {
        this.maDH = maDH; this.maKH = maKH; this.maNV = maNV; this.diaChiGiaoHang = diaChiGiaoHang;
        this.tgDat = tgDat; this.tgGiaoDK = tgGiaoDK; this.phiVanChuyen = phiVanChuyen; 
        this.tongTien = tongTien; this.trangThaiDH = trangThaiDH;
    }

    public DonHangDTO(String maDH, String maKH, String maNV, String diaChiGiaoHang,
                  Date tgDat, Date tgGiaoDK, double phiVanChuyen,
                  double tongTienHang, double tongTien,
                  String trangThaiDH, String phuongThucTT) {
        this.maDH = maDH; this.maKH = maKH; this.maNV = maNV;
        this.diaChiGiaoHang = diaChiGiaoHang;
        this.tgDat = tgDat; this.tgGiaoDK = tgGiaoDK;
        this.phiVanChuyen = phiVanChuyen;
        this.tongTienHang = tongTienHang;
        this.tongTien = tongTien;
        this.trangThaiDH = trangThaiDH;
        this.phuongThucTT = phuongThucTT;
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

    public double getTongTienHang() { return tongTienHang; }
    public void setTongTienHang(double tongTienHang) { this.tongTienHang = tongTienHang; }
    public String getPhuongThucTT() { return phuongThucTT; }
    public void setPhuongThucTT(String phuongThucTT) { this.phuongThucTT = phuongThucTT; }
}