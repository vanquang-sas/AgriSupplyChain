package test;

import bus.LoaiSanPhamBUS;
import dto.LoaiSanPhamDTO;

public class TestMainLSP {
    public static void main(String[] args) {
        System.out.println("--- BẮT ĐẦU TEST MODULE LOẠI SẢN PHẨM ---");

        // 1. Giả lập việc người dùng nhập liệu trên giao diện và bọc vào DTO
        LoaiSanPhamDTO newLSP = new LoaiSanPhamDTO();
        newLSP.setTenLSP("Rau củ hữu cơ");
        newLSP.setMoTa("Các loại rau củ đạt chuẩn VietGAP");

        // 2. Khởi tạo BUS và truyền DTO xuống xử lý
        LoaiSanPhamBUS bus = new LoaiSanPhamBUS();
        String ketQua = bus.addLoaiSanPham(newLSP);

        // 3. In kết quả phản hồi (Giả lập việc hiển thị JOptionPane lên màn hình)
        System.out.println(ketQua);
        
        System.out.println("--- KẾT THÚC TEST ---");
    }
}
