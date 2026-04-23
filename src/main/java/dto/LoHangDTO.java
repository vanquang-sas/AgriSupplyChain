package dto;
import java.util.Date;

public class LoHangDTO {
    private String maLH;
    private String maNCC;
    private String maNV;
    private Date tgNhap;
    private double tongTien;
    private String trangThaiLH;

    public LoHangDTO() {}

    public LoHangDTO(String maLH, String maNCC, String maNV, Date tgNhap, double tongTien, String trangThaiLH) {
        this.maLH = maLH; this.maNCC = maNCC; this.maNV = maNV; 
        this.tgNhap = tgNhap; this.tongTien = tongTien; this.trangThaiLH = trangThaiLH;
    }

    public String getMaLH() { return maLH; }
    public void setMaLH(String maLH) { this.maLH = maLH; }
    public String getMaNCC() { return maNCC; }
    public void setMaNCC(String maNCC) { this.maNCC = maNCC; }
    public String getMaNV() { return maNV; }
    public void setMaNV(String maNV) { this.maNV = maNV; }
    public Date getTgNhap() { return tgNhap; }
    public void setTgNhap(Date tgNhap) { this.tgNhap = tgNhap; }
    public double getTongTien() { return tongTien; }
    public void setTongTien(double tongTien) { this.tongTien = tongTien; }
    public String getTrangThaiLH() { return trangThaiLH; }
    public void setTrangThaiLH(String trangThaiLH) { this.trangThaiLH = trangThaiLH; }
}