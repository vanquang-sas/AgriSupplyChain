package dto;

public class ChiTietDonHangDTO {
    private String maDH;
    private String maSP;
    private double giaBan;
    private double soLuong;
    private double thanhTien;

    public ChiTietDonHangDTO() {}

    public ChiTietDonHangDTO(String maDH, String maSP, double giaBan, double soLuong, double thanhTien) {
        this.maDH = maDH; this.maSP = maSP; 
        this.giaBan = giaBan; this.soLuong = soLuong; this.thanhTien = thanhTien;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChiTietDonHangDTO that = (ChiTietDonHangDTO) o;
        return java.util.Objects.equals(maDH, that.maDH) &&
               java.util.Objects.equals(maSP, that.maSP);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(maDH, maSP);
    }
}