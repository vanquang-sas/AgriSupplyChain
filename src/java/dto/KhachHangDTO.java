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

    public KhachHangDTO(String maKH, String username, String tenKH, String loaiKH,
                        String diaChi, String sdt, String email) {
        this.maKH = maKH;
        this.username = username;
        this.tenKH = tenKH;
        this.loaiKH = loaiKH;
        this.diaChi = diaChi;
        this.sdt = sdt;
        this.email = email;
    }

        // getter/setter
}