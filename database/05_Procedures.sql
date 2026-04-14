-- ====================================================================================
--                              PHẦN 5: TẠO PROCEDURE
-- ====================================================================================
CREATE OR REPLACE PROCEDURE PROC_THEM_SANPHAM (
    p_MaSP IN VARCHAR2,
    p_TenSP IN VARCHAR2,
    p_GiaMua IN NUMBER,
    p_GiaBan IN NUMBER
) 
IS
BEGIN
    INSERT INTO SANPHAM (MaSP, TenSP, GiaMua, GiaBan) 
    VALUES (p_MaSP, p_TenSP, p_GiaMua, p_GiaBan);
    
    COMMIT;
END;
/

-- Cập nhật giá một sản phẩm
CREATE OR REPLACE PROCEDURE SP_CAPNHAT_GIA (
    p_MaSP IN VARCHAR2,
    p_GiaMuaMoi IN NUMBER,
    p_GiaBanMoi IN NUMBER
)
IS
    v_MaGia VARCHAR2(10);
BEGIN
    -- 1. Cập nhật giá trong bảng SANPHAM
    UPDATE SANPHAM
    SET GiaMua = p_GiaMuaMoi,
        GiaBan = p_GiaBanMoi
    WHERE MaSP = p_MaSP;

    -- 2. Ghi nhận vào LICHSUGIA
    INSERT INTO LICHSUGIA (MaSP, GiaMua, GiaBan)
    VALUES (p_MaSP, p_GiaMuaMoi, p_GiaBanMoi);

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20010, 'Lỗi cập nhật giá sản phẩm: ' || SQLERRM);
END;
/

Đối với 1 procedure SỬA (update) có thể có nhiều tham số (có thể NULL)
SP_SUA_THAMSO     -- Sửa giá trị của tham số

SP_DOC_THONGBAO   -- Đánh dấu đã đọc thông báo này

SP_THEM_TAIKHOAN  -- Thêm tài khoản mới
SP_DOI_PASSWORD   -- Đổi pass, dùng cho Use-case quên mật khẩu
SP_KHOA_TAIKHOAN  -- Khoá tài khoản chứ không thực sự xoá, TrangThaiTK = 0

SP_THEM_KH     -- Thêm khách hàng mới
SP_SUA_KH     -- Sửa thông tin khách

SP_THEM_NV  -- Thêm nhân viên mới
SP_SUA_NV   -- Sửa thông tin nhân viên

SP_THEM_NCC       -- Thêm nhà cung cấp mới
SP_SUA_NCC        -- Sửa thông tin nhà cung cấp
SP_NGUNG_HOPTAC   -- TrangThaiHopTac = 0

SP_THEM_LSP  -- Thêm loại sản phẩm mới
SP_SUA_LSP   -- Sửa thông tin loại sản phẩm

SP_THEM_SP  -- Thêm sản phẩm mới
SP_SUA_SP   -- Sửa thông tin sản phẩm