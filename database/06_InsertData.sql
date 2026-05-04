-- ====================================================================================
--                              PHẦN 6: Thêm dữ liệu cần thiết
-- ====================================================================================

-- 1. THAMSO
INSERT ALL
    INTO THAMSO(MaTS, TenTS, GiaTri, MoTa) VALUES ('TS000001', 'MIN_TONKHO', 10, 'Số lượng tồn kho tối thiểu')
    INTO THAMSO(MaTS, TenTS, GiaTri, MoTa) VALUES ('TS000002', 'CANHBAO_HETHAN', 7, 'Số ngày trước khi hết hạn sẽ hiện cảnh báo')
    INTO THAMSO(MaTS, TenTS, GiaTri, MoTa) VALUES ('TS000003', 'MAX_TG_THANHTOAN', 24, 'Thời gian tối đa cho phép chờ thanh toán, nếu không sẽ huỷ đơn hàng')
    INTO THAMSO(MaTS, TenTS, GiaTri, MoTa) VALUES ('TS000004', 'DON_GIA_VANCHUYEN', 20000, 'Đơn giá vận chuyển 1 km')
    INTO THAMSO(MaTS, TenTS, GiaTri, MoTa) VALUES ('TS000005', 'GG_THUONG', 0.00, 'Giảm giá cho khách hàng loại thường')
    INTO THAMSO(MaTS, TenTS, GiaTri, MoTa) VALUES ('TS000006', 'GG_THANTHIET', 0.02, 'Giảm giá cho khách hàng loại thường')
    INTO THAMSO(MaTS, TenTS, GiaTri, MoTa) VALUES ('TS000007', 'GG_VIP', 0.05, 'Giảm giá cho khách hàng loại thường')
SELECT * FROM dual;

-- 3. TAIKHOAN
INSERT ALL
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK) VALUES ('quanly01', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 0, 1)
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK) VALUES ('quanly02', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 0, 1)
SELECT * FROM dual;

-- 5. NHANVIEN 
INSERT ALL
    INTO NHANVIEN(MaNV, Username, TenNV, ChucVu, SDT, Luong) 
        VALUES ('NV000001', 'quanly01', N'Nguyễn ABC', N'Quản lý', '0901234567', 25000000)
    INTO NHANVIEN(MaNV, Username, TenNV, ChucVu, SDT, Luong) 
        VALUES ('NV000002', 'quanly02', N'Nguyễn DEF', N'Quản lý', '0901234568', 25000000)
SELECT * FROM dual;