# DANH MỤC ĐỐI TƯỢNG DATABASE (TRIGGER, FUNCTION, PROCEDURE)

Tài liệu liệt kê các thành phần logic nghiệp vụ được cài đặt trong hệ thống cơ sở dữ liệu.

## 1. Danh sách Trigger (Trình kích hoạt)

| Tên Trigger | Ý nghĩa (Mục đích sử dụng) |
| :--- | :--- |
| **Các Trigger sinh ID tự động** | (Bao gồm 15 trigger: `TRG_THAMSO_ID`, `TRG_THONGBAO_ID`, `TRG_KHACHHANG_ID`, `TRG_NHANVIEN_ID`, `TRG_NCC_ID`, `TRG_LSP_ID`, `TRG_SANPHAM_ID`, `TRG_KHO_ID`, `TRG_LSG_ID`, `TRG_LOHANG_ID`, `TRG_CTLH_ID`, `TRG_TONKHO_ID`, `TRG_DONHANG_ID`, `TRG_CTDH_ID`, `TRG_XUATKHO_ID`) **Tự động tạo và điền mã PK chuẩn hóa** (vd: KH000001) trước khi Insert nếu chưa có mã. |
| **TRG_NCC_SDT** | Kiểm tra ràng buộc SĐT của Nhà cung cấp (**chỉ chứa số, độ dài 10-12 ký tự**). |
| **TRG_KH_SDT** | Kiểm tra ràng buộc SĐT của Khách hàng (**chỉ chứa số, độ dài 10-12 ký tự**). |
| **TRG_CTDH_THANHTIEN** | Tự động lấy giá bán hiện hành từ bảng `SANPHAM` và tính `ThanhTien` cho chi tiết đơn hàng. |
| **TRG_CTDH_TONGTIEN** | Tự động tính toán lại `TongTien` của đơn hàng khi chi tiết đơn hàng thay đổi. |
| **TRG_CTDH_CHECK_TRANGTHAIDH** | Ngăn chặn việc thêm/sửa/xoá chi tiết đơn hàng nếu trạng thái đơn hàng là **'Đang giao'** hoặc **'Hoàn thành'**. |
| **TRG_CTLH_THANHTIEN** | Tự động lấy giá mua hiện hành từ bảng `SANPHAM` và tính `ThanhTien` cho chi tiết lô hàng. |
| **TRG_CTLH_TONGTIEN** | Tự động tính toán lại `TongTien` của lô hàng khi có sự thay đổi chi tiết lô hàng. |
| **TRG_DH_CHECK_KH** | Kiểm tra trạng thái tài khoản của khách hàng trước khi tạo đơn hàng, **chặn tạo đơn nếu tài khoản bị khóa**. |
| **TRG_LH_CHECK_NCC** | Kiểm tra trạng thái hợp tác của nhà cung cấp, **chặn nhập lô hàng nếu đang ngừng hợp tác**. |
| **TRG_XK_CAPNHAT_TONKHO** | **Tự động trừ/hoàn trả** số lượng khả dụng (`SLKhaDung`) và số lượng tồn thực tế (`SLConLai`) trong kho khi xuất hàng. |
| **TRG_SP_LUU_LSG** | Tự động ghi lại log vào bảng `LICHSUGIA` mỗi khi sản phẩm có thay đổi về **Giá Mua** hoặc **Giá Bán**. |
| **TRG_CANHBAO_HETHAN** | Theo dõi ngày hết hạn trong tồn kho, nếu lô hàng sắp hết hạn sẽ **tự sinh thông báo cảnh báo**. |
| **TRG_CANHBAO_MIN_TONKHO** | Theo dõi tồn kho tổng thể, nếu tụt xuống dưới mức quy định thì **tạo thông báo nhắc nhở nhập hàng**. |

## 2. Danh sách Function (Hàm)

| Tên Function | Ý nghĩa (Mục đích sử dụng) |
| :--- | :--- |
| **FN_TINH_PHIVANCHUYEN** | Tính toán phí vận chuyển dựa trên thông số khoảng cách (km) nhân với đơn giá vận chuyển từ bảng `THAMSO`. |

## 3. Danh sách Procedure (Thủ tục)

| Tên Procedure | Ý nghĩa (Mục đích sử dụng) |
| :--- | :--- |
| **SP_CAPNHAT_GIA** | Cập nhật đồng thời giá mua/bán mới của sản phẩm và thủ công lưu vào bảng lịch sử giá. |
| **SP_CAPNHAT_THAMSO** | Cập nhật giá trị và mô tả của một tham số hệ thống. |
| **SP_DOC_THONGBAO** | Đánh dấu thay đổi trạng thái của thông báo thành **"Đã đọc"**. |
| **SP_THEM_TAIKHOAN / SP_DOI_PASSWORD / SP_KHOA_TAIKHOAN** | Xử lý các thao tác quản trị tài khoản (Thêm mới, Đổi mật khẩu, Khóa tài khoản). |
| **SP_THEM_KH / SP_CAPNHAT_KH** | Thêm mới và cập nhật thông tin hồ sơ Khách hàng. |
| **SP_THEM_NV / SP_CAPNHAT_NV** | Thêm mới và cập nhật thông tin hồ sơ Nhân viên. |
| **SP_THEM_NCC / SP_CAPNHAT_NCC / SP_NGUNG_HOPTAC** | Quản lý hồ sơ Nhà cung cấp và trạng thái hợp tác. |
| **SP_THEM_LSP / SP_CAPNHAT_LSP** | Thêm mới và cập nhật phân loại (nhóm) Sản phẩm. |
| **SP_THEM_SP / SP_CAPNHAT_SP** | Thêm mới và cập nhật thông tin danh mục Sản phẩm. |
| **SP_THEM_KHO / SP_CAPNHAT_KHO** | Quản lý thông tin các Kho chứa hàng. |
| **SP_THEM_LH / SP_CAPNHAT_LH / SP_XOA_LH** | Quản lý Lô hàng (chỉ xoá được nếu hàng chưa thực hiện nhập vào kho). |
| **SP_THEM_CTLH / SP_CAPNHAT_CTLH / SP_XOA_CTLH** | Thao tác trên chi tiết từng mặt hàng trong một Lô hàng cụ thể. |
| **SP_THEM_DH / SP_CAPNHAT_DH / SP_XOA_DH** | Quản lý đơn đặt hàng (Thêm mới, cập nhật trạng thái/địa chỉ và xóa đơn). |
| **SP_THEM_CTDH / SP_CAPNHAT_CTDH / SP_XOA_CTDH** | Thao tác thêm, cập nhật số lượng và xoá các mặt hàng bên trong đơn hàng. |
| **SP_YEUCAU_NHAPKHO** | Chuyển trạng thái lô hàng thành **yêu cầu nhập kho** để thông báo cho bộ phận Kho. |
| **SP_XACNHAN_NHAPKHO** | Thủ kho xác nhận vị trí lưu trữ, sinh record `TONKHO`. Tự động chốt hoàn tất nhập lô. |
| **SP_YEUCAU_XUATKHO** | Tìm lô hàng khả dụng theo quy tắc **FEFO / FIFO**, phân bổ hàng và tạo phiếu xuất tạm giữ. |
| **SP_XACNHAN_XUATKHO** | Xác nhận hàng đã rời kho thực tế. Đổi trạng thái đơn sang **"Chờ giao hàng"**. |
| **SP_XACNHAN_GIAOHANG** | Chỉ định và cập nhật nhân viên phụ trách việc vận chuyển đơn hàng. |
| **SP_GIAOHANG_THANHCONG** | Kết thúc vòng đời đơn hàng, xác nhận giao dịch thành công. |