-- ====================================================================================
--                              PHẦN 6: Thêm dữ liệu cần thiết
-- ====================================================================================

-- 1. THAMSO
INSERT ALL
    INSERT INTO THAMSO(TenTS, GiaTri, MoTa) VALUES ('MIN_TONKHO', 10, 'Số lượng tồn kho tối thiểu');
    INSERT INTO THAMSO(TenTS, GiaTri, MoTa) VALUES ('MIN_TONKHO', 10, 'Số lượng tồn kho tối thiểu');
SELECT * FROM dual;

-- 2. THONGBAO

-- 3. TAIKHOAN
INSERT ALL
    INSERT INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK) VALUES ('quanly01', 'pass01', 0, 1);
    INSERT INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK) VALUES ('quanly02', 'pass02', 0, 1);
SELECT * FROM dual;

-- 4. KHACHHANG