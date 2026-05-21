-- ====================================================================================
--                              PHẦN 3: TẠO FUNCTION
-- ====================================================================================

-- Tính phí vận chuyển của 1 đơn hàng
-- Ý tưởng: Khi người dùng nhập DiaChiGiaoHang, gọi API tới bên thứ 3
-- để tính khoảng cách giao hàng rồi truyền vào hàm để tính phí
CREATE OR REPLACE FUNCTION FN_TINH_PHIVANCHUYEN (
    p_MaKH IN VARCHAR2
) RETURN NUMBER
IS
    v_LoaiKH NVARCHAR2(50) := 'Thường';
    v_PhiVanChuyen NUMBER := 0;
BEGIN
    -- Lấy loại khách hàng
    SELECT NVL(LoaiKH, 'Thường') INTO v_LoaiKH
    FROM KHACHHANG
    WHERE MaKH = p_MaKH;

    -- Lấy phí ship từ bảng tham số dựa trên loại khách hàng
    IF v_LoaiKH = 'VIP' THEN
        SELECT GiaTri INTO v_PhiVanChuyen FROM THAMSO WHERE TenTS = 'SHIP_VIP';
    ELSIF v_LoaiKH = 'Thân thiết' THEN
        SELECT GiaTri INTO v_PhiVanChuyen FROM THAMSO WHERE TenTS = 'SHIP_THANTHIET';
    ELSE
        SELECT GiaTri INTO v_PhiVanChuyen FROM THAMSO WHERE TenTS = 'SHIP_THUONG';
    END IF;

    RETURN v_PhiVanChuyen;

EXCEPTION
    WHEN OTHERS THEN
        BEGIN
            SELECT GiaTri INTO v_PhiVanChuyen FROM THAMSO WHERE TenTS = 'SHIP_THUONG';
            RETURN v_PhiVanChuyen;
        EXCEPTION
            WHEN OTHERS THEN
                RETURN 500000; -- Mức phí dự phòng tuyệt đối
        END;
END;
/

-- Lấy danh sách đơn hàng chờ tạo yêu cầu xuất kho, hoặc đã có yêu cầu xuất kho tạm giữ
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
        GROUP BY DH.MaDH, KH.TenKH, DH.TrangThaiDH
        ORDER BY DH.MaDH ASC;
    RETURN v_cursor;
END;
/

-- Đếm số đơn hàng chờ xuất nhưng thiếu tồn kho khả dụng
CREATE OR REPLACE FUNCTION FN_GET_SO_DONHANG_THIEU_TONKHO
RETURN NUMBER
IS
    v_count NUMBER;
BEGIN
    SELECT COUNT(*)
    INTO v_count
    FROM DONHANG DH
    WHERE DH.TrangThaiDH IN (N'Đã đặt', N'Chờ xử lý')
      AND NOT EXISTS (
          SELECT 1
          FROM XUATKHO XK
          JOIN CHITIETDONHANG CT ON XK.MaCTDH = CT.MaCTDH
          WHERE CT.MaDH = DH.MaDH
            AND XK.TrangThaiXK = N'Tạm giữ'
      )
      AND EXISTS (
          SELECT 1
          FROM CHITIETDONHANG CTDH
          WHERE CTDH.MaDH = DH.MaDH
          GROUP BY CTDH.MaSP
          HAVING SUM(CTDH.SoLuong) > (
              SELECT NVL(SUM(TK.SLKhaDung), 0)
              FROM TONKHO TK
              JOIN CHITIETLOHANG CTLH ON TK.MaCTLH = CTLH.MaCTLH
              WHERE CTLH.MaSP = CTDH.MaSP
                AND TK.SLKhaDung > 0
                AND TK.TGHetHan >= TRUNC(SYSDATE)
          )
      );

    RETURN v_count;
END;
/

-- Lấy danh sách soạn hàng xuất kho theo FEFO
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
        WHERE LH.TrangThaiLH IN (N'Chờ nhập kho', N'Chờ kiểm duyệt');
    RETURN v_cursor;
END;
/

-- Lấy chi tiết đơn hàng đọc trực tiếp từ bảng XUATKHO
CREATE OR REPLACE FUNCTION FN_GET_CHITIET_DONHANG (
    p_MaDH IN VARCHAR2
)
RETURN SYS_REFCURSOR
IS
    v_cursor SYS_REFCURSOR;
BEGIN
    OPEN v_cursor FOR
        SELECT
            SP.MaSP,
            SP.TenSP,
            CTDH.SoLuong AS SoLuongYeuCau,
            XK.SLXuat AS SoLuongXuat,
            TK.ViTri,
            TO_CHAR(TK.TGHetHan, 'DD/MM/YYYY') AS NgayHetHan
        FROM XUATKHO XK
        JOIN CHITIETDONHANG CTDH ON XK.MaCTDH = CTDH.MaCTDH
        JOIN SANPHAM SP ON CTDH.MaSP = SP.MaSP
        JOIN TONKHO TK ON XK.MaTonKho = TK.MaTonKho
        WHERE CTDH.MaDH = p_MaDH AND XK.TrangThaiXK = N'Tạm giữ'
        ORDER BY SP.TenSP ASC, TK.TGHetHan ASC;
    RETURN v_cursor;
END;
/
