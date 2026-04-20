package dto;

public class LoaiSanPhamDTO {
    private String maLSP;
    private String tenLSP;
    private String moTa;

    public LoaiSanPhamDTO() {}

    public LoaiSanPhamDTO(String maLSP, String tenLSP, String moTa) {
        this.maLSP = maLSP; this.tenLSP = tenLSP; this.moTa = moTa;
    }

    public String getMaLSP() { return maLSP; }
    public void setMaLSP(String maLSP) { this.maLSP = maLSP; }
    public String getTenLSP() { return tenLSP; }
    public void setTenLSP(String tenLSP) { this.tenLSP = tenLSP; }
    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }
}