package dto;
import java.util.Date;

public class XuatKhoDTO {
    private String maXK;
    private String maCTDH;
    private String maTonKho;
    private double slXuat;
    private Date tgXuat;

    public XuatKhoDTO() {}

    public XuatKhoDTO(String maXK, String maCTDH, String maTonKho, double slXuat, Date tgXuat) {
        this.maXK = maXK; this.maCTDH = maCTDH; this.maTonKho = maTonKho; 
        this.slXuat = slXuat; this.tgXuat = tgXuat;
    }

    public String getMaXK() { return maXK; }
    public void setMaXK(String maXK) { this.maXK = maXK; }
    public String getMaCTDH() { return maCTDH; }
    public void setMaCTDH(String maCTDH) { this.maCTDH = maCTDH; }
    public String getMaTonKho() { return maTonKho; }
    public void setMaTonKho(String maTonKho) { this.maTonKho = maTonKho; }
    public double getSlXuat() { return slXuat; }
    public void setSlXuat(double slXuat) { this.slXuat = slXuat; }
    public Date getTgXuat() { return tgXuat; }
    public void setTgXuat(Date tgXuat) { this.tgXuat = tgXuat; }
}
