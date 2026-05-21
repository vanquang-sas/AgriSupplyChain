-- ====================================================================================
--                              PHẦN 6: Thêm dữ liệu cần thiết
-- ====================================================================================

-- 1. THAMSO
INSERT ALL
    INTO THAMSO(MaTS, TenTS, GiaTri, MoTa) VALUES ('TS000001', 'MIN_TONKHO', 10, 'Số lượng tồn kho tối thiểu')
    INTO THAMSO(MaTS, TenTS, GiaTri, MoTa) VALUES ('TS000002', 'CANHBAO_HETHAN', 7, 'Số ngày trước khi hết hạn sẽ hiện cảnh báo')
    INTO THAMSO(MaTS, TenTS, GiaTri, MoTa) VALUES ('TS000003', 'MAX_TG_THANHTOAN', 24, 'Thời gian tối đa cho phép chờ thanh toán, nếu không sẽ huỷ đơn hàng')
    INTO THAMSO(MaTS, TenTS, GiaTri, MoTa) VALUES ('TS000004', 'SHIP_THUONG', 500000, 'Phí vận chuyển cho khách hàng loại thường')
    INTO THAMSO(MaTS, TenTS, GiaTri, MoTa) VALUES ('TS000008', 'SHIP_THANTHIET', 300000, 'Phí vận chuyển cho khách hàng loại thân thiết')
    INTO THAMSO(MaTS, TenTS, GiaTri, MoTa) VALUES ('TS000009', 'SHIP_VIP', 100000, 'Phí vận chuyển cho khách hàng loại VIP')
    INTO THAMSO(MaTS, TenTS, GiaTri, MoTa) VALUES ('TS000005', 'GG_THUONG', 0.00, 'Giảm giá cho khách hàng loại thường')
    INTO THAMSO(MaTS, TenTS, GiaTri, MoTa) VALUES ('TS000006', 'GG_THANTHIET', 0.02, 'Giảm giá cho khách hàng loại thường')
    INTO THAMSO(MaTS, TenTS, GiaTri, MoTa) VALUES ('TS000007', 'GG_VIP', 0.05, 'Giảm giá cho khách hàng loại thường')
SELECT * FROM dual;

-- 3. TAIKHOAN
INSERT ALL
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK, DiaChi, SDT, Email) VALUES ('quanly01', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 0, 1, NULL, '0901234567', NULL)
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK, DiaChi, SDT, Email) VALUES ('quanly02', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 0, 1, NULL, '0901234568', NULL)
SELECT * FROM dual;

-- 5. NHANVIEN 
INSERT ALL
    INTO NHANVIEN(MaNV, Username, TenNV, ChucVu, Luong) 
        VALUES ('NV000001', 'quanly01', N'Nguyễn ABC', N'Quản lý', 25000000)
    INTO NHANVIEN(MaNV, Username, TenNV, ChucVu, Luong) 
        VALUES ('NV000002', 'quanly02', N'Nguyễn DEF', N'Quản lý', 25000000)
SELECT * FROM dual;