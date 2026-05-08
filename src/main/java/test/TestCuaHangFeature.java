package test;

import bus.CuaHangBUS;
import dto.SanPhamDTO;

import java.util.List;

public class TestCuaHangFeature {

    public static void main(String[] args) {

        CuaHangBUS bus = new CuaHangBUS();

        // =================================================
        // 1. TEST LẤY TẤT CẢ SẢN PHẨM
        // =================================================
        System.out.println("=== DANH SÁCH SẢN PHẨM ===");

        List<SanPhamDTO> dsSP = bus.getAllSanPham();

        for (SanPhamDTO sp : dsSP) {

            System.out.println(
                    sp.getMaSP() + " | " +
                    sp.getTenSP() + " | " +
                    sp.getMaLSP() + " | " +
                    sp.getGiaBan()
            );
        }

        // =================================================
        // 2. TEST TÌM KIẾM
        // =================================================
        System.out.println("\n=== TÌM KIẾM: táo ===");

        List<SanPhamDTO> timKiem = bus.timKiem("táo");

        for (SanPhamDTO sp : timKiem) {

            System.out.println(
                    sp.getMaSP() + " | " +
                    sp.getTenSP() + " | " +
                    sp.getGiaBan()
            );
        }

        // =================================================
        // 3. TEST LỌC THEO LOẠI
        // =================================================
        System.out.println("\n=== LỌC THEO LOẠI: LSP001 ===");

        List<SanPhamDTO> locLoai = bus.locTheoLoai("LSP001");

        for (SanPhamDTO sp : locLoai) {

            System.out.println(
                    sp.getMaSP() + " | " +
                    sp.getTenSP() + " | " +
                    sp.getMaLSP() + " | " +
                    sp.getGiaBan()
            );
        }
    }
}