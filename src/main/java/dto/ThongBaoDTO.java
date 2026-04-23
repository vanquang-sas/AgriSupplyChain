package dto;
import java.util.Date;

public class ThongBaoDTO {
    private String maTB;
    private String loaiTB;
    private String noiDung;
    private int trangThaiTB;
    private Date tgTao;

    public ThongBaoDTO() {}

    public ThongBaoDTO(String maTB, String loaiTB, String noiDung, int trangThaiTB, Date tgTao) {
        this.maTB = maTB; this.loaiTB = loaiTB; this.noiDung = noiDung; 
        this.trangThaiTB = trangThaiTB; this.tgTao = tgTao;
    }

    public String getMaTB() { return maTB; }
    public void setMaTB(String maTB) { this.maTB = maTB; }
    public String getLoaiTB() { return loaiTB; }
    public void setLoaiTB(String loaiTB) { this.loaiTB = loaiTB; }
    public String getNoiDung() { return noiDung; }
    public void setNoiDung(String noiDung) { this.noiDung = noiDung; }
    public int getTrangThaiTB() { return trangThaiTB; }
    public void setTrangThaiTB(int trangThaiTB) { this.trangThaiTB = trangThaiTB; }
    public Date getTgTao() { return tgTao; }
    public void setTgTao(Date tgTao) { this.tgTao = tgTao; }
}