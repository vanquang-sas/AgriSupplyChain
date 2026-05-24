package dto;

public class ThongKeDTO {
    // Inner class cho Doanh thu
    public static class DoanhThu {
        public int thang;
        public double doanhThu;
        public DoanhThu(int thang, double doanhThu) { this.thang = thang; this.doanhThu = doanhThu; }
    }

    // Inner class cho Sản Phẩm
    public static class SanPham {
        public String maSP;
        public String tenSP;
        public int soLuong;
        public double doanhThu;
        public double loiNhuan;
        public double bienDoLN;
    }

    // Inner class cho Trạng Thái Đơn Hàng
    public static class TrangThai {
        public String trangThai;
        public int soLuong;
        public double tyLe;

        public TrangThai() {
        }

        public TrangThai(String trangThai, int soLuong) {
            this.trangThai = trangThai;
            this.soLuong = soLuong;
        }

        public TrangThai(String trangThai, int soLuong, double tyLe) {
            this.trangThai = trangThai;
            this.soLuong = soLuong;
            this.tyLe = tyLe;
        }
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

    // Dành cho Lịch sử giá sản phẩm
    public static class LichSuGia {
        public String maGia;
        public String maSP;
        public String tenSP;
        public double giaMua;
        public double giaBan;
        public double loiNhuan;
        public java.util.Date tgApDung;

        public LichSuGia() {}

        public LichSuGia(String maGia, String maSP, String tenSP, double giaMua, double giaBan, double loiNhuan, java.util.Date tgApDung) {
            this.maGia = maGia;
            this.maSP = maSP;
            this.tenSP = tenSP;
            this.giaMua = giaMua;
            this.giaBan = giaBan;
            this.loiNhuan = loiNhuan;
            this.tgApDung = tgApDung;
        }
    }

    // Inner class cho Quản lý công nợ
    public static class CongNo {
        public String maKH;
        public String tenKH;
        public int soDonGhiNo;
        public double tongTien;
        public double daThanhToan;
        public double conNo;

        public CongNo() {}

        public CongNo(String maKH, String tenKH, int soDonGhiNo, double tongTien, double daThanhToan, double conNo) {
            this.maKH = maKH;
            this.tenKH = tenKH;
            this.soDonGhiNo = soDonGhiNo;
            this.tongTien = tongTien;
            this.daThanhToan = daThanhToan;
            this.conNo = conNo;
        }
    }
}