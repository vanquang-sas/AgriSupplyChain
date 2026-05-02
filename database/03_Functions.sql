-- ====================================================================================
--                              PHẦN 3: TẠO FUNCTION
-- ====================================================================================

-- Tính phí vận chuyển của 1 đơn hàng
-- Ý tưởng: Khi người dùng nhập DiaChiGiaoHang, gọi API tới bên thứ 3 
-- để tính khoảng cách giao hàng rồi truyền vào hàm để tính phí
CREATE OR REPLACE FUNCTION FN_TINH_PHIVANCHUYEN (
    p_KhoangCach IN NUMBER
) RETURN NUMBER 
IS
    v_DonGia NUMBER := 0;
    v_PhiVanChuyen NUMBER := 0;
BEGIN
    -- 1. Lấy đơn giá vận chuyển từ bảng THAMSO
    SELECT GiaTri INTO v_DonGia
    FROM THAMSO
    WHERE TenTS = 'DON_GIA_VANCHUYEN';

    -- 2. Tính toán phí vận chuyển
    v_PhiVanChuyen := p_KhoangCach * v_DonGia;

    -- 3. Trả về kết quả
    RETURN v_PhiVanChuyen;

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        -- Xử lý trường hợp không tìm thấy tham số trong bảng
        RETURN -1;
END;
/