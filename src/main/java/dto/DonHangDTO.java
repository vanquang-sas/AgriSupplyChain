package dto;
import java.util.Date;

public class DonHangDTO {
    private String maDH;
    private String maKH;
    private String maNV;
    private String diaChiGiaoHang;
    private Date tgDat;
    private Date tgGiaoDK;
    private double tongTien;
    private String trangThaiDH;

    public DonHangDTO() {}

    public DonHangDTO(String maDH, String maKH, String maNV, String diaChiGiaoHang, Date tgDat, Date tgGiaoDK, double tongTien, String trangThaiDH) {
        this.maDH = maDH; this.maKH = maKH; this.maNV = maNV; this.diaChiGiaoHang = diaChiGiaoHang;
        this.tgDat = tgDat; this.tgGiaoDK = tgGiaoDK; this.tongTien = tongTien; this.trangThaiDH = trangThaiDH;
    }

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
}
