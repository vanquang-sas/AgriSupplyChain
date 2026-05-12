package test;

import bus.GiaoHangBUS;
import dto.DonHangDTO;
import java.util.List;

public class TestGiaoHangFeature {
    public static void main(String[] args) {

        GiaoHangBUS bus = new GiaoHangBUS();

        // 1. Test lấy danh sách đơn chờ giao
        System.out.println("=== DANH SÁCH ĐƠN CHỜ GIAO ===");
        List<DonHangDTO> list = bus.layDonChoGiao("NV000011");

        for (DonHangDTO dh : list) {
            System.out.println(dh.getMaDH() + " - " + dh.getTrangThaiDH());
        }

        // 2. Test nhận đơn
        System.out.println("\n=== NHẬN ĐƠN ===");
        boolean kq1 = bus.nhanDonGiao("DH000001", "NV000012");
        System.out.println("Kết quả: " + kq1);

        // 3. Test giao thành công
        System.out.println("\n=== GIAO THÀNH CÔNG ===");
        boolean kq2 = bus.giaoThanhCong("DH000033","NV000011");
        System.out.println("Kết quả: " + kq2);

        // 4. Test giao thất bại
        System.out.println("\n=== GIAO THẤT BẠI ===");
        boolean kq3 = bus.giaoThatBai("DH000002","NV000001","Dat nham");
        System.out.println("Kết quả: " + kq3);

        // 5. Test lịch sử giao hàng
        System.out.println("\n=== LỊCH SỬ ===");
        List<DonHangDTO> ls = bus.lichSuGiaoHang("NV000011");

        for (DonHangDTO dh : ls) {
            System.out.println(dh.getMaDH() + " - " + dh.getTrangThaiDH());
        }
    }
}