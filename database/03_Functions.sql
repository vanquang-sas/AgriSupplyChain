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

--function mới thêm

-- Lấy danh sách đơn hàng chờ tạo yêu cầu xuất kho (chưa có phiếu Tạm giữ)
CREATE OR REPLACE FUNCTION FN_GET_DS_DONHANG_CHO_XUAT
RETURN SYS_REFCURSOR
IS
    v_cursor SYS_REFCURSOR;
BEGIN
    OPEN v_cursor FOR
        SELECT DH.MaDH,
               NVL(KH.TenKH, N'Khách lẻ') AS TenKH,
               COUNT(CTDH.MaSP) AS SoLuongSP,
               SUM(CTDH.SoLuong) AS TongSL,
               DH.TrangThaiDH
        FROM DONHANG DH
        JOIN CHITIETDONHANG CTDH ON DH.MaDH = CTDH.MaDH
        LEFT JOIN KHACHHANG KH ON DH.MaKH = KH.MaKH
        WHERE DH.TrangThaiDH IN (N'Đã đặt', N'Chờ xử lý')
          AND NOT EXISTS (
              SELECT 1
              FROM XUATKHO XK
              JOIN CHITIETDONHANG CT ON XK.MaCTDH = CT.MaCTDH
              WHERE CT.MaDH = DH.MaDH
                AND XK.TrangThaiXK = N'Tạm giữ'
          )
        GROUP BY DH.MaDH, KH.TenKH, DH.TrangThaiDH
        ORDER BY DH.MaDH ASC;
    RETURN v_cursor;
END;
/

-- Lấy danh sách soạn hàng xuất kho (các phiếu Tạm giữ, sắp xếp theo FEFO)
CREATE OR REPLACE FUNCTION FN_GET_DS_SOANHANG
RETURN SYS_REFCURSOR
IS
    v_cursor SYS_REFCURSOR;
BEGIN
    OPEN v_cursor FOR
        SELECT
            DH.MaDH,
            SP.TenSP,
            XK.SLXuat,
            TO_CHAR(TK.TGHetHan, 'DD/MM/YYYY') AS NgayHetHan,
            TK.ViTri,
            ' ' AS NhanVien,
            XK.TrangThaiXK
        FROM XUATKHO XK
        LEFT JOIN CHITIETDONHANG CTDH ON XK.MaCTDH = CTDH.MaCTDH
        LEFT JOIN DONHANG DH ON CTDH.MaDH = DH.MaDH
        LEFT JOIN SANPHAM SP ON CTDH.MaSP = SP.MaSP
        LEFT JOIN TONKHO TK ON XK.MaTonKho = TK.MaTonKho
        WHERE XK.TrangThaiXK = N'Tạm giữ'
        ORDER BY DH.MaDH ASC, TK.TGHetHan ASC;
    RETURN v_cursor;
END;
/

-- Lấy danh sách lô hàng đang chờ nhập kho
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
        WHERE LH.TrangThaiLH = N'Chờ nhập kho';
    RETURN v_cursor;
END;
/

-- Lấy chi tiết đơn hàng kèm preview phân bổ thực tế theo FEFO (có thể trả về nhiều dòng cho 1 SP nếu nằm ở nhiều vị trí)
CREATE OR REPLACE FUNCTION FN_GET_CHITIET_DONHANG (
    p_MaDH IN VARCHAR2
)
RETURN SYS_REFCURSOR
IS
    v_cursor SYS_REFCURSOR;
BEGIN
    OPEN v_cursor FOR
        WITH Allocation AS (
            SELECT 
                CTDH.MaSP,
                SP.TenSP,
                CTDH.SoLuong AS SoLuongYeuCau,
                TK.SLKhaDung,
                TK.ViTri,
                TK.TGHetHan,
                -- Tính tổng lũy kế số lượng khả dụng của từng sản phẩm theo thứ tự FEFO
                SUM(TK.SLKhaDung) OVER (
                    PARTITION BY CTDH.MaSP 
                    ORDER BY TK.TGHetHan ASC, TK.TGNhapKho ASC, TK.MaTonKho ASC
                ) AS LuyKe
            FROM CHITIETDONHANG CTDH
            JOIN SANPHAM SP ON CTDH.MaSP = SP.MaSP
            JOIN CHITIETLOHANG CTLH ON SP.MaSP = CTLH.MaSP
            JOIN TONKHO TK ON CTLH.MaCTLH = TK.MaCTLH
            WHERE CTDH.MaDH = p_MaDH
              AND TK.SLKhaDung > 0
              AND TK.TGHetHan >= TRUNC(SYSDATE)
        )
        SELECT 
            MaSP,
            TenSP,
            SoLuongYeuCau,
            -- Số lượng thực tế lấy từ lô này
            CASE 
                WHEN LuyKe <= SoLuongYeuCau THEN SLKhaDung
                ELSE SoLuongYeuCau - (LuyKe - SLKhaDung)
            END AS SoLuongXuat,
            ViTri,
            TO_CHAR(TGHetHan, 'DD/MM/YYYY') AS NgayHetHan
        FROM Allocation
        -- Chỉ lấy những dòng mà lũy kế trước đó chưa vượt quá số lượng yêu cầu
        WHERE LuyKe - SLKhaDung < SoLuongYeuCau
        ORDER BY TenSP ASC, TGHetHan ASC;
        
    RETURN v_cursor;
END;
/
