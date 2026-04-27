package bus;

import dao.DonHangDAO;
import dto.CartItemDTO;
import dto.DonHangDTO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class DonHangBUS {

    private final DonHangDAO donHangDAO = new DonHangDAO();

    /** Giỏ hàng tạm trên RAM (singleton theo session đăng nhập) */
    private final List<CartItemDTO> gioHang = new ArrayList<>();

    //  QUẢN LÝ GIỎ HÀNG                                        

    /** Thêm sản phẩm vào giỏ. Nếu đã có thì cộng dồn số lượng. */
    public void themVaoGio(CartItemDTO item) {
        for (CartItemDTO existing : gioHang) {
            if (existing.getMaSP().equals(item.getMaSP())) {
                existing.setSoLuong(existing.getSoLuong() + item.getSoLuong());
                return;
            }
        }
        gioHang.add(item);
    }

    /** Xóa 1 dòng khỏi giỏ theo maSP. */
    public void xoaKhoiGio(String maSP) {
        gioHang.removeIf(i -> i.getMaSP().equals(maSP));
    }

    /** Cập nhật số lượng. Nếu soLuong <= 0 thì xóa luôn. */
    public void capNhatSoLuong(String maSP, int soLuong) {
        if (soLuong <= 0) { xoaKhoiGio(maSP); return; }
        for (CartItemDTO item : gioHang) {
            if (item.getMaSP().equals(maSP)) {
                item.setSoLuong(soLuong);
                return;
            }
        }
    }

    /** Xóa toàn bộ giỏ hàng (sau khi đặt xong). */
    public void xoaToanBoGio() {
        gioHang.clear();
    }

    public List<CartItemDTO> getGioHang() { return gioHang; }

    public boolean isGioHangRong() { return gioHang.isEmpty(); }

    /** Tổng tiền hàng (chưa tính phí ship, chưa giảm giá). */
    public double tinhTongTienHang() {
        return gioHang.stream().mapToDouble(CartItemDTO::getThanhTien).sum();
    }

    //  TÍNH PHÍ VẬN CHUYỂN                                               //

    /**
     * Validate địa chỉ rồi gọi DB tính phí.
     * @throws IllegalArgumentException nếu địa chỉ rỗng
     */
    public double tinhPhiVanChuyen(String diaChi) {
        if (diaChi == null || diaChi.trim().isEmpty()) {
            throw new IllegalArgumentException("Địa chỉ giao hàng không được để trống!");
        }
        return donHangDAO.tinhPhiVanChuyen(diaChi.trim());
    }

    //  THANH TOÁN                                                         //

    /**
     * Luồng thanh toán chính.
     *
     * @param maKH          mã khách hàng đang đăng nhập
     * @param diaChiGiao    địa chỉ nhận hàng
     * @param tgGiaoYC      ngày khách muốn nhận
     * @param phuongThucTT  'COD' | 'Chuyển khoản' | 'Ví điện tử'
     * @return maDH vừa tạo nếu thành công, null nếu thất bại
     * @throws IllegalArgumentException nếu dữ liệu đầu vào không hợp lệ
     */
    public String thanhToan(String maKH, String diaChiGiao,
                            Date tgGiaoYC, String phuongThucTT, String trangThaiThanhToan) {

        // // --- Validate ---
        // if (isGioHangRong())
        //     throw new IllegalArgumentException("Giỏ hàng đang trống!");
        // if (maKH == null || maKH.trim().isEmpty())
        //     throw new IllegalArgumentException("Không xác định được khách hàng!");
        // if (diaChiGiao == null || diaChiGiao.trim().isEmpty())
        //     throw new IllegalArgumentException("Địa chỉ giao hàng không được để trống!");
        // if (tgGiaoYC == null || tgGiaoYC.before(new Date()))
        //     throw new IllegalArgumentException("Ngày giao hàng phải là ngày trong tương lai!");
        // if (phuongThucTT == null || phuongThucTT.trim().isEmpty())
        //     throw new IllegalArgumentException("Vui lòng chọn phương thức thanh toán!");

        // // --- Tính tiền ---
        // double tongTienHang  = tinhTongTienHang();
        // double phiShip       = donHangDAO.tinhPhiVanChuyen(diaChiGiao.trim());
        // double giamGia       = 0; // mở rộng sau nếu có mã giảm giá
        // double tongTien      = tongTienHang + phiShip - giamGia;
        // int    trangThaiTT   = phuongThucTT.equals("COD") ? 0 : 1;

        // // --- Tạo mã đơn hàng ---
        // String maDH = "DH" + UUID.randomUUID().toString()
        //                           .replace("-", "").substring(0, 8).toUpperCase();

        // // --- Build DTO ---
        // DonHangDTO dh = new DonHangDTO(
        //     maDH, maKH.trim(), null,          // maNV để null
        //     diaChiGiao.trim(),
        //     new Date(), tgGiaoYC,
        //     phiShip,
        //     tongTienHang,                      // <-- field mới
        //     tongTien,
        //     "Đã đặt",
        //     phuongThucTT.trim()               // <-- field mới
        // );

        // // --- Gọi DAO tạo đơn cha ---
        // if (!donHangDAO.themDonHang(dh)) return null;

        // // --- Gọi DAO tạo từng chi tiết ---
        // for (CartItemDTO item : gioHang) {
        //     if (!donHangDAO.themChiTietDonHang(maDH, item.getMaSP(), item.getSoLuong())) {
        //         // Nếu 1 dòng lỗi, toàn bộ nên rollback – cần bật transaction ở DB
        //         return null;
        //     }
        // }

        // // --- Làm sạch giỏ ---
        // xoaToanBoGio();
        // return "DH_" + System.currentTimeMillis();
        System.out.println("--- ĐANG TEST UI ---");
        System.out.println("Khách: " + maKH);
        System.out.println("Địa chỉ: " + diaChiGiao);
        System.out.println("Phương thức: " + phuongThucTT);
        System.out.println("Trạng thái: " + trangThaiThanhToan);
        System.out.println("--------------------");

        // Trả về một mã đơn hàng ảo để hệ thống hiểu là đã lưu thành công
        return "DH_TEST_001";
    }

    //  LỊCH SỬ ĐƠN HÀNG                                                  //

    public List<DonHangDTO> layLichSuDonHang(String maKH) {
        if (maKH == null || maKH.trim().isEmpty()) return new ArrayList<>();
        return donHangDAO.layDonHangTheoKH(maKH);
    }

    /**
     * Hủy đơn hàng – chỉ cho phép nếu trạng thái là 'Đã đặt' hoặc 'Chờ thanh toán'.
     * Việc kiểm tra trạng thái được DB (SP_XOA_DH) xử lý, BUS chỉ validate input.
     */
    public boolean huyDonHang(String maDH) {
        if (maDH == null || maDH.trim().isEmpty())
            throw new IllegalArgumentException("Mã đơn hàng không hợp lệ!");
        return donHangDAO.huyDonHang(maDH);
    }
}