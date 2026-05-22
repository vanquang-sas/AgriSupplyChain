package dto;

import java.util.Date;

public class XuatKhoChiTietDTO {
    private String maXK;
    private String maDH;
    private String maTonKho;
    private String tenSP;
    private String maSP;
    private String maLH;
    private Date tgHetHan;
    private double slXuat;
    private String viTri;
    private String maNV;
    private String tenNV;
    private String trangThaiXK;

    public XuatKhoChiTietDTO() {}

    public XuatKhoChiTietDTO(String maXK, String maDH, String maTonKho,
            String tenSP, String maSP, String maLH, Date tgHetHan,
            double slXuat, String viTri, String maNV, String tenNV, String trangThaiXK) {
        this.maXK = maXK;
        this.maDH = maDH;
        this.maTonKho = maTonKho;
        this.tenSP = tenSP;
        this.maSP = maSP;
        this.maLH = maLH;
        this.tgHetHan = tgHetHan;
        this.slXuat = slXuat;
        this.viTri = viTri;
        this.maNV = maNV;
        this.tenNV = tenNV;
        this.trangThaiXK = trangThaiXK;
    }

    public String getMaXK() { return maXK; }
    public String getMaDH() { return maDH; }
    public String getMaTonKho() { return maTonKho; }
    public String getTenSP() { return tenSP; }
    public String getMaSP() { return maSP; }
    public String getMaLH() { return maLH; }
    public void setMaLH(String maLH) { this.maLH = maLH; }
    public Date getTGHetHan() { return tgHetHan; }
    public double getSLXuat() { return slXuat; }
    public String getViTri() { return viTri; }
    public String getMaNV() { return maNV; }
    public String getTenNV() { return tenNV; }
    public String getTrangThaiXK() { return trangThaiXK; }
    public void setMaNV(String maNV) { this.maNV = maNV; }
    public void setTenNV(String tenNV) { this.tenNV = tenNV; }
    public void setTrangThaiXK(String trangThaiXK) { this.trangThaiXK = trangThaiXK; }
}