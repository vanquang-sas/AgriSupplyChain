package dto;
import java.util.Date;

public class ThongBaoDTO {
    private String maTB;
    private String loaiTB;
    private String noiDung;
    private int trangThaiTB;
    private Date tgTao;
    private String nguoiNhan;
    private String vaiTro;

    public ThongBaoDTO() {}

    public ThongBaoDTO(String maTB, String loaiTB, String noiDung, int trangThaiTB, Date tgTao, String nguoiNhan, String vaiTro) {
        this.maTB = maTB; 
        this.loaiTB = loaiTB; 
        this.noiDung = noiDung; 
        this.trangThaiTB = trangThaiTB; 
        this.tgTao = tgTao;
        this.nguoiNhan = nguoiNhan;
        this.vaiTro = vaiTro;
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

    public String getNguoiNhan() { return nguoiNhan; }
    public void setNguoiNhan(String nguoiNhan) { this.nguoiNhan = nguoiNhan; }

    public String getVaiTro() { return vaiTro; }
    public void setVaiTro(String vaiTro) { this.vaiTro = vaiTro; }
}