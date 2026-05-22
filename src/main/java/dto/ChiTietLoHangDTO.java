package dto;

public class ChiTietLoHangDTO {
    private String maLH;
    private String maSP;
    private double giaMua;
    private double soLuong;
    private double thanhTien;

    public ChiTietLoHangDTO() {}

    public ChiTietLoHangDTO(String maLH, String maSP, double giaMua, double soLuong, double thanhTien) {
        this.maLH = maLH; this.maSP = maSP; 
        this.giaMua = giaMua; this.soLuong = soLuong; this.thanhTien = thanhTien;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChiTietLoHangDTO that = (ChiTietLoHangDTO) o;
        return java.util.Objects.equals(maLH, that.maLH) &&
               java.util.Objects.equals(maSP, that.maSP);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(maLH, maSP);
    }
}