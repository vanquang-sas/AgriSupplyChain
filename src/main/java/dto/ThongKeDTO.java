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

    // Class cho dòng tiền
    public static class TaiChinh {
        public String thangNam;
        public double doanhThu;
        public double chiPhi;
        public TaiChinh(String thangNam, double doanhThu, double chiPhi) {
            this.thangNam = thangNam;
            this.doanhThu = doanhThu;
            this.chiPhi = chiPhi;
        }
    }

    // Dành cho Danh sách bảng
    public static class NhanVienThongKe {
        public String maNV;
        public String tenNV;
        public String chucVu;
        public int tongCongViec;
        public double luong;
    }

    // Dành cho Biểu đồ chi tiết (Line + Bar)
    public static class HieuSuatChiTiet {
        public String thangNam;
        public int soLuong;
        public double tongGiaTri;
    }

    // Dành cho Summary Cards
    public static class SummaryNhanVien {
        public String label1, label2, label3;
        public String value1, value2, value3;
    }
}