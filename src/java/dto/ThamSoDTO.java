package java.dto;

public class ThamSoDTO {
    private String maTS;
    private String tenTS;
    private double giaTri;
    private String moTa;

    public ThamSoDTO() {}

    public ThamSoDTO(String maTS, String tenTS, double giaTri, String moTa) {
        this.maTS = maTS; this.tenTS = tenTS; this.giaTri = giaTri; this.moTa = moTa;
    }

    public String getMaTS() { return maTS; }
    public void setMaTS(String maTS) { this.maTS = maTS; }
    public String getTenTS() { return tenTS; }
    public void setTenTS(String tenTS) { this.tenTS = tenTS; }
    public double getGiaTri() { return giaTri; }
    public void setGiaTri(double giaTri) { this.giaTri = giaTri; }
    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }
}   