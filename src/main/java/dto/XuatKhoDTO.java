package dto;
import java.util.Date;

public class XuatKhoDTO {
    private String maXK;
    private String maDH;
    private String maSP;
    private String maTonKho;
    private String maNV;
    private double slXuat;
    private Date tgCapNhat;       
    private String trangThaiXK;   

    public XuatKhoDTO() {}

    public XuatKhoDTO(String maXK, String maDH, String maSP, String maTonKho, String maNV, double slXuat, Date tgCapNhat, String trangThaiXK) {
        this.maXK = maXK; this.maDH = maDH; this.maSP = maSP; this.maTonKho = maTonKho; 
        this.maNV = maNV; this.slXuat = slXuat; this.tgCapNhat = tgCapNhat; this.trangThaiXK = trangThaiXK;
    }

    public String getMaXK() { return maXK; }
    public void setMaXK(String maXK) { this.maXK = maXK; }
    public String getMaDH() { return maDH; }
    public void setMaDH(String maDH) { this.maDH = maDH; }
    public String getMaSP() { return maSP; }
    public void setMaSP(String maSP) { this.maSP = maSP; }
    public String getMaTonKho() { return maTonKho; }
    public void setMaTonKho(String maTonKho) { this.maTonKho = maTonKho; }
    public String getMaNV() { return maNV; }
    public void setMaNV(String maNV) { this.maNV = maNV; }
    public double getSlXuat() { return slXuat; }
    public void setSlXuat(double slXuat) { this.slXuat = slXuat; }
    public Date getTgCapNhat() { return tgCapNhat; }
    public void setTgCapNhat(Date tgCapNhat) { this.tgCapNhat = tgCapNhat; }
    public String getTrangThaiXK() { return trangThaiXK; }
    public void setTrangThaiXK(String trangThaiXK) { this.trangThaiXK = trangThaiXK; }
}