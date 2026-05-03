package dto;

import java.util.Date;

public class NhapKhoDTO {
    private String maCTLH;
    private String loaiKho;
    private String viTri;
    private Date tgHetHan;

    public String getMaCTLH() {
        return maCTLH;
    }

    public void setMaCTLH(String maCTLH) {
        this.maCTLH = maCTLH;
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