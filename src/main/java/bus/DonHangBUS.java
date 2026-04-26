package bus;

import dao.DonHangDAO;
import dto.DonHangDTO;

import java.util.List;

public class DonHangBUS {
    private DonHangDAO donHangDAO;

    public DonHangBUS() {
        this.donHangDAO = new DonHangDAO();
    }


    public List<DonHangDTO> getDanhSachDonHang(String maKH) throws Exception {
        // 1. VALIDATION: Kiểm tra tham số đầu vào
        if (maKH == null || maKH.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã khách hàng không được để trống!");
        }

        // 2. GỌI DAO: Lấy danh sách đơn hàng từ Database
        try {
            List<DonHangDTO> danhSach = donHangDAO.getLichSuDonHang(maKH);
            
            if (danhSach.isEmpty()) {
                System.out.println("! Khách hàng [" + maKH + "] chưa có đơn hàng nào");
            }
            
            return danhSach;

        } catch (Exception e) {
            System.err.println("✗ BUS Error - getDanhSachDonHang:");
            throw new Exception("Không thể lấy danh sách đơn hàng: " + e.getMessage(), e);
        }
    }


    public void huyDonHang(DonHangDTO donHang) throws Exception {
        // 1. VALIDATION: Kiểm tra object có null không
        if (donHang == null) {
            throw new IllegalArgumentException("Đối tượng đơn hàng không được null!");
        }

        if (donHang.getMaDH() == null || donHang.getMaDH().trim().isEmpty()) {
            throw new IllegalArgumentException("Mã đơn hàng không được để trống!");
        }

        // 2. VALIDATION LOGIC NGHIỆP VỤ (Critical business rule):
        // Chỉ cho phép hủy nếu: TrangThaiDH = "Đã đặt" HOẶC TrangThaiDH = "Chờ xử lý"
        String trangThaiDH = donHang.getTrangThaiDH();
        boolean coTheHuy = "Đã đặt".equalsIgnoreCase(trangThaiDH) || 
                           "Chờ xử lý".equalsIgnoreCase(trangThaiDH);

        if (!coTheHuy) {
            // Giải thích chi tiết lý do không thể hủy
            String lyDoKhongTheHuy = "Không thể hủy đơn hàng này!\n\n" +
                    "Lý do:\n" +
                    "• Trạng thái đơn hàng hiện tại: " + trangThaiDH + "\n\n" +
                    "Chỉ có thể hủy đơn khi trạng thái là:\n" +
                    "✓ \"Đã đặt\" (vừa tạo đơn)\n" +
                    "✓ \"Chờ xử lý\" (đang xử lý)";
            
            throw new IllegalStateException(lyDoKhongTheHuy);
        }

        // 3. GỌI DAO: Thực hiện hủy (soft delete)
        try {
            donHangDAO.xoaDonHang(donHang.getMaDH());
            System.out.println("✓ BUS - Hủy đơn hàng [" + donHang.getMaDH() + "] thành công");

        } catch (Exception e) {
            System.err.println("✗ BUS Error - huyDonHang:");
            // Nếu DAO trả về lỗi về điều kiện không thỏa, ném lại
            if (e.getMessage().contains("không thể huỷ")) {
                throw new IllegalStateException(e.getMessage());
            }
            throw new Exception("Lỗi khi hủy đơn hàng: " + e.getMessage(), e);
        }
    }

    /**
     * Lấy đối tượng DAO (có thể dùng cho testing)
     */
    public DonHangDAO getDonHangDAO() {
        return donHangDAO;
    }
}
