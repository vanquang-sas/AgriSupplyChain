package dto;

public class NhaCungCapDTO {
    private String maNCC;
    private String tenNCC;
    private String diaChi;
    private String sdt;
    private String email;
    private String chungNhanCL;
    private int trangThaiHopTac;

    public NhaCungCapDTO() {}

    public NhaCungCapDTO(String maNCC, String tenNCC, String diaChi, String sdt, String email, String chungNhanCL, int trangThaiHopTac) {
        this.maNCC = maNCC; this.tenNCC = tenNCC; this.diaChi = diaChi; this.sdt = sdt;
        this.email = email; this.chungNhanCL = chungNhanCL; this.trangThaiHopTac = trangThaiHopTac;
    }

    // Getters and Setters...
    public String getMaNCC() { return maNCC; }
    public void setMaNCC(String maNCC) { this.maNCC = maNCC; }
    public String getTenNCC() { return tenNCC; }
    public void setTenNCC(String tenNCC) { this.tenNCC = tenNCC; }
    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }
    public String getSdt() { return sdt; }
    public void setSdt(String sdt) { this.sdt = sdt; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getChungNhanCL() { return chungNhanCL; }
    public void setChungNhanCL(String chungNhanCL) { this.chungNhanCL = chungNhanCL; }
    public int getTrangThaiHopTac() { return trangThaiHopTac; }
    public void setTrangThaiHopTac(int trangThaiHopTac) { this.trangThaiHopTac = trangThaiHopTac; }
}