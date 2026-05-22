package dto;

import java.util.Date;

public class NhapKhoDTO {
    private String maLH;
    private String maSP;
    private String loaiKho;
    private String viTri;
    private Date tgHetHan;

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

    public String getLoaiKho() {
        return loaiKho;
    }

    public void setLoaiKho(String loaiKho) {
        this.loaiKho = loaiKho;
    }

    public String getViTri() {
        return viTri;
    }

    public void setViTri(String viTri) {
        this.viTri = viTri;
    }

    public Date getTgHetHan() {
        return tgHetHan;
    }

    public void setTgHetHan(Date tgHetHan) {
        this.tgHetHan = tgHetHan;
    }
}