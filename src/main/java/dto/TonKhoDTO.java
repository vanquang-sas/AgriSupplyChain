package dto;
import java.util.Date;

public class TonKhoDTO {
    private String maTonKho;
    private String maKho;
    private String maCTLH;
    private double slConLai;
    private double slKhaDung;
    private Date tgNhapKho;
    private Date tgHetHan;
    private String viTri;

    public TonKhoDTO() {}

    public TonKhoDTO(String maTonKho, String maKho, String maCTLH, double slConLai, double slKhaDung, Date tgNhapKho, Date tgHetHan, String viTri) {
        this.maTonKho = maTonKho; this.maKho = maKho; this.maCTLH = maCTLH; 
        this.slConLai = slConLai; this.slKhaDung = slKhaDung; 
        this.tgNhapKho = tgNhapKho; this.tgHetHan = tgHetHan; this.viTri = viTri;
    }

    public String getMaTonKho() { return maTonKho; }
    public void setMaTonKho(String maTonKho) { this.maTonKho = maTonKho; }
    public String getMaKho() { return maKho; }
    public void setMaKho(String maKho) { this.maKho = maKho; }
    public String getMaCTLH() { return maCTLH; }
    public void setMaCTLH(String maCTLH) { this.maCTLH = maCTLH; }
    public double getSlConLai() { return slConLai; }
    public void setSlConLai(double slConLai) { this.slConLai = slConLai; }
    public double getSlKhaDung() { return slKhaDung; }
    public void setSlKhaDung(double slKhaDung) { this.slKhaDung = slKhaDung; }
    public Date getTgNhapKho() { return tgNhapKho; }
    public void setTgNhapKho(Date tgNhapKho) { this.tgNhapKho = tgNhapKho; }
    public Date getTgHetHan() { return tgHetHan; }
    public void setTgHetHan(Date tgHetHan) { this.tgHetHan = tgHetHan; }
    public String getViTri() { return viTri; }
    public void setViTri(String viTri) { this.viTri = viTri; }
}