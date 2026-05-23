-- ====================================================================================
--                              PHẦN 3: TẠO FUNCTION
-- ====================================================================================

-- Tính phí vận chuyển của 1 đơn hàng
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
          JOIN CHITIETDONHANG CT ON XK.MaDH = CT.MaDH AND XK.MaSP = CT.MaSP
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
              JOIN CHITIETLOHANG CTLH ON TK.MaLH = CTLH.MaLH AND TK.MaSP = CTLH.MaSP
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
        LEFT JOIN CHITIETDONHANG CTDH ON XK.MaDH = CTDH.MaDH AND XK.MaSP = CTDH.MaSP
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
            (CTLH.MaLH || '_' || CTLH.MaSP) AS MaCTLH,
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
        JOIN CHITIETDONHANG CTDH ON XK.MaDH = CTDH.MaDH AND XK.MaSP = CTDH.MaSP
        JOIN SANPHAM SP ON CTDH.MaSP = SP.MaSP
        JOIN TONKHO TK ON XK.MaTonKho = TK.MaTonKho
        WHERE CTDH.MaDH = p_MaDH AND XK.TrangThaiXK = N'Tạm giữ'
        ORDER BY SP.TenSP ASC, TK.TGHetHan ASC;
    RETURN v_cursor;
END;
/


-- 1. Function lấy danh sách Sản phẩm (Có tên loại)
CREATE OR REPLACE FUNCTION FN_LAY_DS_SANPHAM
RETURN SYS_REFCURSOR
AS
    v_cursor SYS_REFCURSOR;
BEGIN
    -- Trả về danh sách sản phẩm kèm tên loại (nếu có)
    OPEN v_cursor FOR
        SELECT SP.MaSP,
               SP.TenSP,
               SP.MaLSP,
               LSP.TenLSP,
               SP.ChatLuong,
               SP.GiaMua,
               SP.GiaBan,
               SP.DonViTinh,
               SP.BaoQuan,
               SP.HinhAnh
        FROM SANPHAM SP
        LEFT JOIN LOAISANPHAM LSP ON SP.MaLSP = LSP.MaLSP
        ORDER BY SP.MaSP;
    RETURN v_cursor;
END;
/

-- 2. Lấy danh sách đơn hàng của một khách hàng kèm chi tiết sản phẩm gom bằng LISTAGG
CREATE OR REPLACE FUNCTION FN_LAY_DS_DONHANG_BY_KH (
    p_MaKH IN VARCHAR2
) RETURN SYS_REFCURSOR
IS
    v_cursor SYS_REFCURSOR;
BEGIN
    OPEN v_cursor FOR
    SELECT 
        DH.MaDH,
        DH.MaKH,
        DH.TGDat,
        DH.TongTien,
        DH.TrangThaiDH,
        DH.TrangThaiTT,
        -- Gom tất cả tên sản phẩm cùng số lượng thành một chuỗi (có chống overflow)
        LISTAGG(CTDH.SoLuong || ' ' || SP.TenSP, ', ' ON OVERFLOW TRUNCATE) 
            WITHIN GROUP (ORDER BY SP.TenSP) AS DanhSachSP
    FROM DONHANG DH
    LEFT JOIN CHITIETDONHANG CTDH ON DH.MaDH = CTDH.MaDH
    LEFT JOIN SANPHAM SP ON CTDH.MaSP = SP.MaSP
    WHERE DH.MaKH = p_MaKH
    GROUP BY DH.MaDH, DH.MaKH, DH.TGDat, DH.TongTien, DH.TrangThaiDH, DH.TrangThaiTT
    ORDER BY DH.TGDat DESC;
    
    RETURN v_cursor;
EXCEPTION
    WHEN OTHERS THEN
        RAISE_APPLICATION_ERROR(-20040, 'Lỗi khi lấy danh sách đơn hàng: ' || SQLERRM);
END FN_LAY_DS_DONHANG_BY_KH;
/

-- 3. Thống kê Top N Sản phẩm bán chạy nhất
CREATE OR REPLACE FUNCTION FN_THONGKE_SANPHAM (
    p_Limit IN NUMBER,
    p_Type IN VARCHAR2,
    p_FromDate IN DATE,
    p_ToDate IN DATE
) RETURN SYS_REFCURSOR
AS
    v_cursor SYS_REFCURSOR;
    v_Limit NUMBER := p_Limit;
BEGIN
    IF v_Limit = 0 THEN 
        v_Limit := 999999; 
    END IF;

    IF p_Type = 'BEST' THEN
        OPEN v_cursor FOR
        SELECT TenSP, TongSoLuong FROM (
            SELECT sp.TenSP, NVL(SUM(ct.SoLuong), 0) AS TongSoLuong
            FROM SANPHAM sp
            -- Lọc ngày và trạng thái trước, sau đó mới LEFT JOIN để đếm số lượng
            LEFT JOIN (
                SELECT ctdh.MaSP, ctdh.SoLuong
                FROM CHITIETDONHANG ctdh
                JOIN DONHANG dh ON ctdh.MaDH = dh.MaDH
                WHERE dh.TrangThaiDH = 'Hoàn thành'
                  AND TRUNC(dh.TGDat) BETWEEN p_FromDate AND p_ToDate
            ) ct ON sp.MaSP = ct.MaSP
            GROUP BY sp.TenSP
            ORDER BY TongSoLuong DESC 
        ) WHERE ROWNUM <= v_Limit ORDER BY TongSoLuong DESC; 
    ELSE
        OPEN v_cursor FOR
        SELECT TenSP, TongSoLuong FROM (
            SELECT sp.TenSP, NVL(SUM(ct.SoLuong), 0) AS TongSoLuong
            FROM SANPHAM sp
            LEFT JOIN (
                SELECT ctdh.MaSP, ctdh.SoLuong
                FROM CHITIETDONHANG ctdh
                JOIN DONHANG dh ON ctdh.MaDH = dh.MaDH
                WHERE dh.TrangThaiDH = 'Hoàn thành'
                  AND TRUNC(dh.TGDat) BETWEEN p_FromDate AND p_ToDate
            ) ct ON sp.MaSP = ct.MaSP
            GROUP BY sp.TenSP
            ORDER BY TongSoLuong ASC 
        ) WHERE ROWNUM <= v_Limit ORDER BY TongSoLuong DESC; 
    END IF;
    RETURN v_cursor;
END;
/

-- 5. Thống kê tài chính
CREATE OR REPLACE FUNCTION FN_THONGKE_TAICHINH (
    p_LoaiThongKe IN VARCHAR2, -- 'MONTH' hoặc 'DAY'
    p_Period IN NUMBER
) RETURN SYS_REFCURSOR
AS
    v_cursor SYS_REFCURSOR;
    v_AnchorDate DATE := SYSDATE;
BEGIN
    IF p_LoaiThongKe = 'MONTH' THEN
        OPEN v_cursor FOR
        WITH MonthRange AS (
            SELECT ADD_MONTHS(TRUNC(v_AnchorDate, 'MM'), -LEVEL + 1) AS ThoiGian
            FROM DUAL
            CONNECT BY LEVEL <= p_Period
        ),
        RevenueData AS (
            SELECT TRUNC(TGDat, 'MM') AS ThoiGian, SUM(TongTien) AS DoanhThu
            FROM DONHANG
            WHERE (TrangThaiDH = 'HoanThanh' OR TrangThaiDH = 'Hoàn thành')
              AND TGDat >= ADD_MONTHS(TRUNC(v_AnchorDate, 'MM'), -p_Period + 1)
            GROUP BY TRUNC(TGDat, 'MM')
        ),
        CostData AS (
            SELECT TRUNC(TGNhap, 'MM') AS ThoiGian, SUM(TongTien) AS ChiPhi
            FROM LOHANG
            WHERE TrangThaiLH = 'Đã nhập kho' 
              AND TGNhap >= ADD_MONTHS(TRUNC(v_AnchorDate, 'MM'), -p_Period + 1)
            GROUP BY TRUNC(TGNhap, 'MM')
        )
        SELECT 
            TO_CHAR(mr.ThoiGian, 'MM/YYYY') AS ThangNam, 
            NVL(rd.DoanhThu, 0) AS DoanhThu,
            NVL(cd.ChiPhi, 0) AS ChiPhi
        FROM MonthRange mr
        LEFT JOIN RevenueData rd ON mr.ThoiGian = rd.ThoiGian
        LEFT JOIN CostData cd ON mr.ThoiGian = cd.ThoiGian
        ORDER BY mr.ThoiGian ASC;
        
    ELSE -- Xử lý cho chế độ theo NGÀY
        OPEN v_cursor FOR
        WITH DayRange AS (
            SELECT TRUNC(v_AnchorDate) - LEVEL + 1 AS ThoiGian
            FROM DUAL
            CONNECT BY LEVEL <= p_Period
        ),
        RevenueData AS (
            SELECT TRUNC(TGDat) AS ThoiGian, SUM(TongTien) AS DoanhThu
            FROM DONHANG
            WHERE (TrangThaiDH = 'HoanThanh' OR TrangThaiDH = 'Hoàn thành')
              AND TGDat >= TRUNC(v_AnchorDate) - p_Period + 1
            GROUP BY TRUNC(TGDat)
        ),
        CostData AS (
            SELECT TRUNC(TGNhap) AS ThoiGian, SUM(TongTien) AS ChiPhi
            FROM LOHANG
            WHERE TrangThaiLH = 'Đã nhập kho' 
              AND TGNhap >= TRUNC(v_AnchorDate) - p_Period + 1
            GROUP BY TRUNC(TGNhap)
        )
        SELECT 
            TO_CHAR(dr.ThoiGian, 'DD/MM/YYYY') AS ThangNam,
            NVL(rd.DoanhThu, 0) AS DoanhThu,
            NVL(cd.ChiPhi, 0) AS ChiPhi
        FROM DayRange dr
        LEFT JOIN RevenueData rd ON dr.ThoiGian = rd.ThoiGian
        LEFT JOIN CostData cd ON dr.ThoiGian = cd.ThoiGian
        ORDER BY dr.ThoiGian ASC;
    END IF;
    RETURN v_cursor;
END;
/

-- 6. Thống kê Doanh thu theo Năm (trả về 12 tháng)
CREATE OR REPLACE FUNCTION FN_THONGKE_DOANHTHU_NAM (
    p_Nam IN NUMBER
) RETURN SYS_REFCURSOR
IS
    v_cursor SYS_REFCURSOR;
BEGIN
    OPEN v_cursor FOR
        WITH Months AS (
            SELECT LEVEL AS Thang FROM DUAL CONNECT BY LEVEL <= 12
        ),
        MonthlyRevenue AS (
            SELECT EXTRACT(MONTH FROM TGDat) AS Thang, SUM(TongTien) AS DoanhThu
            FROM DONHANG
            WHERE EXTRACT(YEAR FROM TGDat) = p_Nam AND TrangThaiDH = 'Hoàn thành'
            GROUP BY EXTRACT(MONTH FROM TGDat)
        )
        SELECT m.Thang, NVL(r.DoanhThu, 0) AS DoanhThu
        FROM Months m
        LEFT JOIN MonthlyRevenue r ON m.Thang = r.Thang
        ORDER BY m.Thang;
    RETURN v_cursor;
END;
/

-- 7. Thống kê số lượng đơn hàng theo Trạng thái
CREATE OR REPLACE FUNCTION FN_THONGKE_TRANGTHAI_DH
RETURN SYS_REFCURSOR
IS
    v_cursor SYS_REFCURSOR;
BEGIN
    OPEN v_cursor FOR
        SELECT TrangThaiDH, COUNT(*) AS SoLuong
        FROM DONHANG
        GROUP BY TrangThaiDH
        ORDER BY SoLuong DESC;
    RETURN v_cursor;
END;
/

