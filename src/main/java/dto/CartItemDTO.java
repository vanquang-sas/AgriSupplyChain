package dto;

public class CartItemDTO {
    private String maSP;
    private String tenSP;
    private String hinhAnh;
    private String donViTinh;
    private double giaBan;
    private int soLuong;

    public CartItemDTO() {}

    public CartItemDTO(String maSP, String tenSP, String hinhAnh,
                       String donViTinh, double giaBan, int soLuong) {
        this.maSP = maSP;
        this.tenSP = tenSP;
        this.hinhAnh = hinhAnh;
        this.donViTinh = donViTinh;
        this.giaBan = giaBan;
        this.soLuong = soLuong;
    }

    /** Tiện ích: tính thành tiền của 1 dòng giỏ hàng */
    public double getThanhTien() {
        return giaBan * soLuong;
    }

    public String getMaSP()            { return maSP; }
    public void   setMaSP(String v)    { this.maSP = v; }
    public String getTenSP()           { return tenSP; }
    public void   setTenSP(String v)   { this.tenSP = v; }
    public String getHinhAnh()         { return hinhAnh; }
    public void   setHinhAnh(String v) { this.hinhAnh = v; }
    public String getDonViTinh()           { return donViTinh; }
    public void   setDonViTinh(String v)   { this.donViTinh = v; }
    public double getGiaBan()          { return giaBan; }
    public void   setGiaBan(double v)  { this.giaBan = v; }
    public int    getSoLuong()         { return soLuong; }
    public void   setSoLuong(int v)    { this.soLuong = v; }
}