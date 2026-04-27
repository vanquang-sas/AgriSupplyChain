-- ====================================================================================
--                              PHẦN 6: Thêm dữ liệu cần thiết
-- ====================================================================================

-- 1. THAMSO
INSERT ALL
    INSERT INTO THAMSO(TenTS, GiaTri, MoTa) VALUES ('MIN_TONKHO', 10, 'Số lượng tồn kho tối thiểu');
    INSERT INTO THAMSO(TenTS, GiaTri, MoTa) VALUES ('CANHBAO_HETHAN', 7, 'Số ngày trước khi hết hạn sẽ hiện cảnh báo');
    INSERT INTO THAMSO(TenTS, GiaTri, MoTa) VALUES ('MAX_TG_THANHTOAN', 24, 'Thời gian tối đa cho phép chờ thanh toán, nếu không sẽ huỷ đơn hàng');
    INSERT INTO THAMSO(TenTS, GiaTri, MoTa) VALUES ('DON_GIA_VANCHUYEN', 20000, 'Đơn giá vận chuyển 1 km');
    INSERT INTO THAMSO(TenTS, GiaTri, MoTa) VALUES ('GG_THUONG', 0.00, 'Giảm giá cho khách hàng loại thường');
    INSERT INTO THAMSO(TenTS, GiaTri, MoTa) VALUES ('GG_THANTHIET', 0.02, 'Giảm giá cho khách hàng loại thường');
    INSERT INTO THAMSO(TenTS, GiaTri, MoTa) VALUES ('GG_VIP', 0.05, 'Giảm giá cho khách hàng loại thường');
SELECT * FROM dual;

-- 2. THONGBAO

-- 3. TAIKHOAN
INSERT ALL
    INSERT INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK) VALUES ('quanly01', 'pass01', 0, 1);
    INSERT INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK) VALUES ('quanly02', 'pass02', 0, 1);
SELECT * FROM dual;

-- 4. KHACHHANG