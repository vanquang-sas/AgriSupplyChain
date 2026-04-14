-- ====================================================================================
--                              PHẦN 3: TẠO FUNCTION
-- ====================================================================================

-- Tính tổng tồn kho khả dụng của 1 sản phẩm
CREATE OR REPLACE FUNCTION FN_TINHTONG_TONKHO_KHADUNG (p_MaSP IN SANPHAM.MaSP%TYPE) 
RETURN NUMBER 
IS
    v_TongTonKho NUMBER(10, 2);
BEGIN
    -- Chỉ tính những lô hàng có Ngày hết hạn >= Ngày hiện tại
    SELECT NVL(SUM(TK.SLConLai), 0) INTO v_TongTonKho
    FROM TONKHO TK
    JOIN CHITIETLOHANG CTLH ON TK.MaCTLH = CTLH.MaCTLH
    WHERE CTLH.MaSP = p_MaSP
        AND TK.TGHetHan >= TRUNC(SYSDATE);

    RETURN v_TongTonKho;
END;
/