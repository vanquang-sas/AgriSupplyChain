package java.dto;

public class KhoDTO {
    private String maKho;
    private String tenKho;
    private String loaiKho;
    private String diaChi;
    private String moTa;

    public KhoDTO() {}

    public KhoDTO(String maKho, String tenKho, String loaiKho, String diaChi, String moTa) {
        this.maKho = maKho; this.tenKho = tenKho; this.loaiKho = loaiKho; this.diaChi = diaChi; this.moTa = moTa;
    }

    public String getMaKho() { return maKho; }
    public void setMaKho(String maKho) { this.maKho = maKho; }
    public String getTenKho() { return tenKho; }
    public void setTenKho(String tenKho) { this.tenKho = tenKho; }
    public String getLoaiKho() { return loaiKho; }
    public void setLoaiKho(String loaiKho) { this.loaiKho = loaiKho; }
    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }
    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }
}