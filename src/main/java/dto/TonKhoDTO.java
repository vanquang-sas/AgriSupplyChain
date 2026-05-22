package dto;

import java.util.Date;

public class TonKhoDTO {
    private String maTonKho;
    private String maKho;
    private String maLH;
    private String maSP;
    private double slConLai;
    private double slKhaDung;
    private Date tgNhapKho;
    private Date tgHetHan;
    private String viTri;
    private String trangThai;

    public TonKhoDTO() {
    }

    public TonKhoDTO(String maTonKho, String maKho, String maLH, String maSP, double slConLai, double slKhaDung,
            Date tgNhapKho, Date tgHetHan, String viTri, String trangThai) {
        this.maTonKho = maTonKho;
        this.maKho = maKho;
        this.maLH = maLH;
        this.maSP = maSP;
        this.slConLai = slConLai;
        this.slKhaDung = slKhaDung;
        this.tgNhapKho = tgNhapKho;
        this.tgHetHan = tgHetHan;
        this.viTri = viTri;
        this.trangThai = trangThai;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getMaTonKho() {
        return maTonKho;
    }

    public void setMaTonKho(String maTonKho) {
        this.maTonKho = maTonKho;
    }

    public String getMaKho() {
        return maKho;
    }

    public void setMaKho(String maKho) {
        this.maKho = maKho;
    }

    public String getMaLH() {
        return maLH;
    }

    public void setMaLH(String maLH) {
        this.maLH = maLH;
    }

    public String getMaSP() {
        return maSP;
    }

    public void setMaSP(String maSP) {
        this.maSP = maSP;
    }

    public double getSlConLai() {
        return slConLai;
    }

    public void setSlConLai(double slConLai) {
        this.slConLai = slConLai;
    }

    public double getSlKhaDung() {
        return slKhaDung;
    }

    public void setSlKhaDung(double slKhaDung) {
        this.slKhaDung = slKhaDung;
    }

    public Date getTgNhapKho() {
        return tgNhapKho;
    }

    public void setTgNhapKho(Date tgNhapKho) {
        this.tgNhapKho = tgNhapKho;
    }

    public Date getTgHetHan() {
        return tgHetHan;
    }

    public void setTgHetHan(Date tgHetHan) {
        this.tgHetHan = tgHetHan;
    }

    public String getViTri() {
        return viTri;
    }

    public void setViTri(String viTri) {
        this.viTri = viTri;
    }
}