package dto;

public class NhanVienDTO {
    private String maNV;
    private String username;
    private String tenNV;
    private String chucVu;
    private String sdt;
    private double luong;

    public NhanVienDTO() {}

    public NhanVienDTO(String maNV, String username, String tenNV, String chucVu, String sdt, double luong) {
        this.maNV = maNV; this.username = username; this.tenNV = tenNV; 
        this.chucVu = chucVu; this.sdt = sdt; this.luong = luong;
    }

    public String getMaNV() { return maNV; }
    public void setMaNV(String maNV) { this.maNV = maNV; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getTenNV() { return tenNV; }
    public void setTenNV(String tenNV) { this.tenNV = tenNV; }
    public String getChucVu() { return chucVu; }
    public void setChucVu(String chucVu) { this.chucVu = chucVu; }
    public String getSdt() { return sdt; }
    public void setSdt(String sdt) { this.sdt = sdt; }
    public double getLuong() { return luong; }
    public void setLuong(double luong) { this.luong = luong; }
}
