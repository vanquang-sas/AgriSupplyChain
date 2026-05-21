CREATE OR REPLACE FUNCTION FN_GET_DS_NHAPKHO
RETURN SYS_REFCURSOR
IS
    v_cursor SYS_REFCURSOR;
BEGIN
    OPEN v_cursor FOR
        SELECT
            CTLH.MaCTLH,
            SP.TenSP,
            CTLH.SoLuong,
            ' ' AS MaKho,
            SP.BaoQuan,
            ' ' AS ViTri,
            ' ' AS NgayHetHan,
            LH.TrangThaiLH
        FROM CHITIETLOHANG CTLH
        JOIN SANPHAM SP ON CTLH.MaSP = SP.MaSP
        JOIN LOHANG LH ON CTLH.MaLH = LH.MaLH
        WHERE LH.TrangThaiLH IN (N'Chờ nhập kho', N'Chờ kiểm duyệt');
    RETURN v_cursor;
END;
/
