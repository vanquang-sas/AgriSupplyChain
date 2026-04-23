package dto;

public class KhachHangDTO {
    private String maKH;
    private String username;
    private String tenKH;
    private String loaiKH;
    private String diaChi;
    private String sdt;
    private String email;

    public KhachHangDTO() {}

    public KhachHangDTO(String maKH, String username, String tenKH, String loaiKH, String diaChi, String sdt, String email) {
        this.maKH = maKH; this.username = username; this.tenKH = tenKH; this.loaiKH = loaiKH;
        this.diaChi = diaChi; this.sdt = sdt; this.email = email;
    }

    public String getMaKH() { return maKH; }
    public void setMaKH(String maKH) { this.maKH = maKH; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getTenKH() { return tenKH; }
    public void setTenKH(String tenKH) { this.tenKH = tenKH; }
    public String getLoaiKH() { return loaiKH; }
    public void setLoaiKH(String loaiKH) { this.loaiKH = loaiKH; }
    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }
    public String getSdt() { return sdt; }
    public void setSdt(String sdt) { this.sdt = sdt; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}