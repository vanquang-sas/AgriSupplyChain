package dto;
import java.util.Date;

public class LichSuGiaDTO {
    private String maGia;
    private String maSP;
    private double giaMua;
    private double giaBan;
    private Date tgApDung;

    public LichSuGiaDTO() {}

    public LichSuGiaDTO(String maGia, String maSP, double giaMua, double giaBan, Date tgApDung) {
        this.maGia = maGia; this.maSP = maSP; this.giaMua = giaMua; this.giaBan = giaBan; this.tgApDung = tgApDung;
    }

    public String getMaGia() { return maGia; }
    public void setMaGia(String maGia) { this.maGia = maGia; }
    public String getMaSP() { return maSP; }
    public void setMaSP(String maSP) { this.maSP = maSP; }
    public double getGiaMua() { return giaMua; }
    public void setGiaMua(double giaMua) { this.giaMua = giaMua; }
    public double getGiaBan() { return giaBan; }
    public void setGiaBan(double giaBan) { this.giaBan = giaBan; }
    public Date getTgApDung() { return tgApDung; }
    public void setTgApDung(Date tgApDung) { this.tgApDung = tgApDung; }
}