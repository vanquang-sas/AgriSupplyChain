package dto;

public class SanPhamDTO {
    private String maSP;
    private String tenSP;
    private String maLSP;
    private String tenLSP; // Thuộc tính mới lấy từ lệnh JOIN
    private String chatLuong;
    private double giaMua;
    private double giaBan;
    private String donViTinh;
    private String baoQuan;
    private String hinhAnh;

    public SanPhamDTO() {}

    public SanPhamDTO(String maSP, String tenSP, String maLSP, String tenLSP, String chatLuong, 
                      double giaMua, double giaBan, String donViTinh, String baoQuan, String hinhAnh) {
        this.maSP = maSP;
        this.tenSP = tenSP;
        this.maLSP = maLSP;
        this.tenLSP = tenLSP;
        this.chatLuong = chatLuong;
        this.giaMua = giaMua;
        this.giaBan = giaBan;
        this.donViTinh = donViTinh;
        this.baoQuan = baoQuan;
        this.hinhAnh = hinhAnh;
    }

    public String getMaSP() { return maSP; }
    public void setMaSP(String maSP) { this.maSP = maSP; }

    public String getTenSP() { return tenSP; }
    public void setTenSP(String tenSP) { this.tenSP = tenSP; }

    public String getMaLSP() { return maLSP; }
    public void setMaLSP(String maLSP) { this.maLSP = maLSP; }

    public String getTenLSP() { return tenLSP; }
    public void setTenLSP(String tenLSP) { this.tenLSP = tenLSP; }

    public String getChatLuong() { return chatLuong; }
    public void setChatLuong(String chatLuong) { this.chatLuong = chatLuong; }

    public double getGiaMua() { return giaMua; }
    public void setGiaMua(double giaMua) { this.giaMua = giaMua; }

    public double getGiaBan() { return giaBan; }
    public void setGiaBan(double giaBan) { this.giaBan = giaBan; }

    public String getDonViTinh() { return donViTinh; }
    public void setDonViTinh(String donViTinh) { this.donViTinh = donViTinh; }

    public String getBaoQuan() { return baoQuan; }
    public void setBaoQuan(String baoQuan) { this.baoQuan = baoQuan; }

    public String getHinhAnh() { return hinhAnh; }
    public void setHinhAnh(String hinhAnh) { this.hinhAnh = hinhAnh; }
}