package java.dto;

public class ChiTietDonHangDTO {
    private String maCTDH;
    private String maDH;
    private String maSP;
    private double giaBan;
    private double soLuong;
    private double thanhTien;

    public ChiTietDonHangDTO() {}

    public ChiTietDonHangDTO(String maCTDH, String maDH, String maSP, double giaBan, double soLuong, double thanhTien) {
        this.maCTDH = maCTDH; this.maDH = maDH; this.maSP = maSP; 
        this.giaBan = giaBan; this.soLuong = soLuong; this.thanhTien = thanhTien;
    }

    public String getMaCTDH() { return maCTDH; }
    public void setMaCTDH(String maCTDH) { this.maCTDH = maCTDH; }
    public String getMaDH() { return maDH; }
    public void setMaDH(String maDH) { this.maDH = maDH; }
    public String getMaSP() { return maSP; }
    public void setMaSP(String maSP) { this.maSP = maSP; }
    public double getGiaBan() { return giaBan; }
    public void setGiaBan(double giaBan) { this.giaBan = giaBan; }
    public double getSoLuong() { return soLuong; }
    public void setSoLuong(double soLuong) { this.soLuong = soLuong; }
    public double getThanhTien() { return thanhTien; }
    public void setThanhTien(double thanhTien) { this.thanhTien = thanhTien; }
}