package dto;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class GioHangDTO {

    private String    maKH;
    private String    maSP;
    private double    soLuong;
    private Timestamp tgCapNhat;

    private String     tenSP;
    private BigDecimal donGia;
    private String     hinhAnh;
    private BigDecimal thanhTien;

    public GioHangDTO() {}

    public GioHangDTO(String maKH, String maSP, double soLuong,
                      Timestamp tgCapNhat, String tenSP, BigDecimal donGia, String hinhAnh, BigDecimal thanhTien) {
        this.maKH       = maKH;
        this.maSP       = maSP;
        this.soLuong    = soLuong;
        this.tgCapNhat  = tgCapNhat;
        this.tenSP      = tenSP;
        this.donGia     = donGia;
        this.hinhAnh    = hinhAnh;
        this.thanhTien  = thanhTien;
    }

    public BigDecimal getThanhTien() {
        return thanhTien;
    }

    public void setThanhTien(BigDecimal thanhTien) {
        this.thanhTien = thanhTien;
    }

    public String getMaKH() { return maKH; }
    public void setMaKH(String v) { maKH = v; }

    public String getMaSP() { return maSP; }
    public void setMaSP(String v) { maSP = v; }

    public double getSoLuong() { return soLuong; }
    public void setSoLuong(double v) { soLuong = v; }

    public Timestamp getTgCapNhat() { return tgCapNhat; }
    public void setTgCapNhat(Timestamp v) { tgCapNhat = v; }

    public String getTenSP() { return tenSP; }
    public void setTenSP(String v) { tenSP = v; }

    public BigDecimal getDonGia() { return donGia; }
    public void setDonGia(BigDecimal v) { donGia = v; }

    public String getHinhAnh() { return hinhAnh; }
    public void setHinhAnh(String v) { hinhAnh = v; }

    @Override
    public String toString() {
        return "GioHangDTO{maKH='" + maKH + "', maSP='" + maSP
             + "', tenSP='" + tenSP + "', soLuong=" + soLuong
             + ", donGia=" + donGia + ", thanhTien=" + thanhTien + '}';
    }
}