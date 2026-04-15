package java.dto;

public class ChiTietLoHangDTO {
    private String maCTLH;
    private String maLH;
    private String maSP;
    private double giaMua;
    private double soLuong;
    private double thanhTien;

    public ChiTietLoHangDTO() {}

    public ChiTietLoHangDTO(String maCTLH, String maLH, String maSP, double giaMua, double soLuong, double thanhTien) {
        this.maCTLH = maCTLH; this.maLH = maLH; this.maSP = maSP; 
        this.giaMua = giaMua; this.soLuong = soLuong; this.thanhTien = thanhTien;
    }

    public String getMaCTLH() { return maCTLH; }
    public void setMaCTLH(String maCTLH) { this.maCTLH = maCTLH; }
    public String getMaLH() { return maLH; }
    public void setMaLH(String maLH) { this.maLH = maLH; }
    public String getMaSP() { return maSP; }
    public void setMaSP(String maSP) { this.maSP = maSP; }
    public double getGiaMua() { return giaMua; }
    public void setGiaMua(double giaMua) { this.giaMua = giaMua; }
    public double getSoLuong() { return soLuong; }
    public void setSoLuong(double soLuong) { this.soLuong = soLuong; }
    public double getThanhTien() { return thanhTien; }
    public void setThanhTien(double thanhTien) { this.thanhTien = thanhTien; }
}