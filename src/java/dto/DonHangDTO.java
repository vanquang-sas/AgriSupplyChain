package dto;

import java.util.Date;

public class DonHangDTO {
    private String maDH;
    private String maKH;
    private String maNV;
    private String diaChiGiaoHang;
    private Date tgDat;
    private Date tgGiaoDK;
    private double tongTien;
    private String trangThaiDH;

    public DonHangDTO() {}

    public DonHangDTO(String maDH, String maKH, String maNV, String diaChiGiaoHang,
                      Date tgDat, Date tgGiaoDK, double tongTien, String trangThaiDH) {
        this.maDH = maDH;
        this.maKH = maKH;
        this.maNV = maNV;
        this.diaChiGiaoHang = diaChiGiaoHang;
        this.tgDat = tgDat;
        this.tgGiaoDK = tgGiaoDK;
        this.tongTien = tongTien;
        this.trangThaiDH = trangThaiDH;
    }

    // getter/setter
}