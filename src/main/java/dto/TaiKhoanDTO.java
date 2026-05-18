package dto;
import java.util.Date;

public class TaiKhoanDTO {
    private String username;
    private String password;
    private int loaiTK;
    private int trangThaiTK;
    private String diaChi;
    private String sdt;
    private String email;
    private Date tgTao;

    public TaiKhoanDTO() {}

    public TaiKhoanDTO(String username, String password, int loaiTK, int trangThaiTK, String diaChi, String sdt, String email, Date tgTao) {
        this.username = username;
        this.password = password;
        this.loaiTK = loaiTK;
        this.trangThaiTK = trangThaiTK;
        this.diaChi = diaChi;
        this.sdt = sdt;
        this.email = email;
        this.tgTao = tgTao;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public int getLoaiTK() { return loaiTK; }
    public void setLoaiTK(int loaiTK) { this.loaiTK = loaiTK; }
    public int getTrangThaiTK() { return trangThaiTK; }
    public void setTrangThaiTK(int trangThaiTK) { this.trangThaiTK = trangThaiTK; }
    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }
    public String getSdt() { return sdt; }
    public void setSdt(String sdt) { this.sdt = sdt; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Date getTgTao() { return tgTao; }
    public void setTgTao(Date tgTao) { this.tgTao = tgTao; }
}