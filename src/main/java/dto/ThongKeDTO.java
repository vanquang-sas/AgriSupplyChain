package dto;

public class ThongKeDTO {
    // Inner class cho Doanh thu
    public static class DoanhThu {
        public int thang;
        public double doanhThu;
        public DoanhThu(int thang, double doanhThu) { this.thang = thang; this.doanhThu = doanhThu; }
    }

    // Inner class cho Top Sản Phẩm
    public static class TopSanPham {
        public String tenSP;
        public int soLuongBan;
        public TopSanPham(String tenSP, int soLuongBan) { this.tenSP = tenSP; this.soLuongBan = soLuongBan; }
    }

    // Inner class cho Trạng Thái Đơn Hàng
    public static class TrangThai {
        public String trangThai;
        public int soLuong;
        public TrangThai(String trangThai, int soLuong) { this.trangThai = trangThai; this.soLuong = soLuong; }
    }
}