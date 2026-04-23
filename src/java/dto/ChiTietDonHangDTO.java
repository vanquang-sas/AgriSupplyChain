package dto;

public class ChiTietDonHangDTO {
    private String maCTDH;
    private String maDH;
    private String maSP;
    private double giaBan;
    private double soLuong;
    private double thanhTien;

    public ChiTietDonHangDTO() {}

    public ChiTietDonHangDTO(String maCTDH, String maDH, String maSP,
                              double giaBan, double soLuong, double thanhTien) {
        this.maCTDH = maCTDH;
        this.maDH = maDH;
        this.maSP = maSP;
        this.giaBan = giaBan;
        this.soLuong = soLuong;
        this.thanhTien = thanhTien;
    }

    // getter/setter
}