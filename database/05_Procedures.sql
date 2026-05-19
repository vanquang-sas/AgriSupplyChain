-- ====================================================================================
--                              PHẦN 5: TẠO PROCEDURE
-- ====================================================================================

-- ------------------------------------------------------------------------------------
-- 1. QUẢN LÝ SẢN PHẨM & GIÁ
-- ------------------------------------------------------------------------------------
CREATE OR REPLACE PROCEDURE SP_CAPNHAT_GIA (
    p_MaSP IN VARCHAR2,
    p_GiaMuaMoi IN NUMBER,
    p_GiaBanMoi IN NUMBER
) IS
BEGIN
    UPDATE SANPHAM
    SET GiaMua = NVL(p_GiaMuaMoi, GiaMua),
        GiaBan = NVL(p_GiaBanMoi, GiaBan)
    WHERE MaSP = p_MaSP;

    COMMIT;

EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;

        RAISE_APPLICATION_ERROR(-20010,'Lỗi cập nhật giá sản phẩm: ' || SQLERRM);
END;
/

CREATE OR REPLACE PROCEDURE SP_THEM_SP (
    p_TenSP IN NVARCHAR2, p_MaLSP IN VARCHAR2, p_ChatLuong IN NVARCHAR2,
    p_GiaMua IN NUMBER, p_GiaBan IN NUMBER, p_DonViTinh IN NVARCHAR2, p_BaoQuan IN NVARCHAR2
) IS
BEGIN
    INSERT INTO SANPHAM (TenSP, MaLSP, ChatLuong, GiaMua, GiaBan, DonViTinh, BaoQuan)
    VALUES (p_TenSP, p_MaLSP, p_ChatLuong, p_GiaMua, p_GiaBan, p_DonViTinh, p_BaoQuan);
    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20011, 'Lỗi thêm sản phẩm: ' || SQLERRM);
END;

/

-- Procedure Xóa Loại Sản Phẩm 
CREATE OR REPLACE PROCEDURE SP_XOA_LSP (
    p_MaLSP IN VARCHAR2
) IS
    v_count NUMBER;
BEGIN
    -- 1. Đếm số lượng sản phẩm đang có của loại này
    SELECT COUNT(*) INTO v_count FROM SANPHAM WHERE MaLSP = p_MaLSP;
    
    -- 2. Nếu có >= 1 sản phẩm, lập tức văng lỗi và dừng chương trình
    IF v_count > 0 THEN
        RAISE_APPLICATION_ERROR(-20040, 'Không thể xóa vì đang có sản phẩm thuộc loại này!');
    END IF;

    -- 3. Nếu an toàn (count = 0) thì mới xóa
    DELETE FROM LOAISANPHAM WHERE MaLSP = p_MaLSP;
    COMMIT;
END;
/

-- ================================= Bảng SANPHAM =================================
-- 1. Procedure lấy danh sách Sản phẩm (Có tên loại)
CREATE OR REPLACE PROCEDURE SP_LAY_DS_SANPHAM (
    p_Cursor OUT SYS_REFCURSOR
) AS
BEGIN
    -- Trả về danh sách sản phẩm kèm tên loại (nếu có)
    OPEN p_Cursor FOR
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
END;
/

-- ------------------------------------------------------------------------------------
-- 2. QUẢN LÝ ĐƠN HÀNG & HUỶ ĐƠN
-- ------------------------------------------------------------------------------------
CREATE OR REPLACE PROCEDURE SP_HUY_DH (
    p_MaDH IN VARCHAR2,
    p_LyDoHuy IN NVARCHAR2
) IS
    v_TrangThaiDH NVARCHAR2(50);
BEGIN
    SELECT TrangThaiDH INTO v_TrangThaiDH FROM DONHANG WHERE MaDH = p_MaDH;
    
    IF v_TrangThaiDH IN ('Hoàn thành', 'Đã huỷ') THEN
        RAISE_APPLICATION_ERROR(-20030, 'Không thể huỷ đơn hàng đã hoàn thành hoặc đã huỷ!');
    END IF;

    UPDATE DONHANG 
    SET TrangThaiDH = 'Đã huỷ',
        LyDoHuy = p_LyDoHuy
    WHERE MaDH = p_MaDH;

    UPDATE XUATKHO 
    SET TrangThaiXK = 'Đã huỷ', 
        TGCapNhat = SYSDATE
    WHERE MaCTDH IN (SELECT MaCTDH FROM CHITIETDONHANG WHERE MaDH = p_MaDH);

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20031, 'Lỗi xử lý huỷ đơn hàng: ' || SQLERRM);
END;
/

-- ------------------------------------------------------------------------------------
-- 3. QUẢN LÝ NHẬP KHO & XÁC NHẬN VỊ TRÍ
-- ------------------------------------------------------------------------------------
CREATE OR REPLACE PROCEDURE SP_YEUCAU_NHAPKHO (p_MaLH IN VARCHAR2) IS
    v_TrangThaiLH NVARCHAR2(50);
BEGIN
    SELECT TrangThaiLH INTO v_TrangThaiLH FROM LOHANG WHERE MaLH = p_MaLH;
    IF v_TrangThaiLH IN ('Chờ nhập kho', 'Đã nhập kho') THEN
        RAISE_APPLICATION_ERROR(-20022, 'Lô hàng này đã được yêu cầu hoặc đã hoàn tất nhập kho!');
    END IF;
    UPDATE LOHANG SET TrangThaiLH = 'Chờ nhập kho' WHERE MaLH = p_MaLH;
    COMMIT;
EXCEPTION 
    WHEN OTHERS THEN ROLLBACK; RAISE;
END;
/

CREATE OR REPLACE PROCEDURE SP_XACNHAN_VITRI_CTLH (
    p_MaCTLH IN VARCHAR2,
    p_MaKho IN VARCHAR2,
    p_TGHetHan IN DATE,
    p_ViTri IN NVARCHAR2
) IS
    v_MaLH VARCHAR2(10);
    v_MaSP VARCHAR2(10);
    v_SoLuong NUMBER(10,2);
    v_BaoQuan NVARCHAR2(100);
    v_LoaiKho NVARCHAR2(100);
    v_TonTai NUMBER;
    v_ChuaXepXong NUMBER;
BEGIN
    SELECT CTLH.MaLH, CTLH.MaSP, CTLH.SoLuong, SP.BaoQuan 
    INTO v_MaLH, v_MaSP, v_SoLuong, v_BaoQuan
    FROM CHITIETLOHANG CTLH JOIN SANPHAM SP ON CTLH.MaSP = SP.MaSP
    WHERE CTLH.MaCTLH = p_MaCTLH;

    SELECT LoaiKho INTO v_LoaiKho FROM KHO WHERE MaKho = p_MaKho;
    IF v_LoaiKho != v_BaoQuan THEN
        RAISE_APPLICATION_ERROR(-20028, 'Bảo quản sai quy cách! Yêu cầu [' || v_BaoQuan || '] nhưng chọn kho [' || v_LoaiKho || '].');
    END IF;

    SELECT COUNT(*) INTO v_TonTai FROM TONKHO WHERE MaCTLH = p_MaCTLH;
    IF v_TonTai > 0 THEN
        RAISE_APPLICATION_ERROR(-20027, 'Chi tiết lô hàng này đã được nhập kho rồi!');
    END IF;

    INSERT INTO TONKHO (MaKho, MaCTLH, SLConLai, SLKhaDung, TGNhapKho, TGHetHan, ViTri)
    VALUES (p_MaKho, p_MaCTLH, v_SoLuong, v_SoLuong, SYSDATE, p_TGHetHan, p_ViTri);

    SELECT COUNT(*) INTO v_ChuaXepXong FROM CHITIETLOHANG C
    WHERE C.MaLH = v_MaLH AND C.MaCTLH NOT IN (SELECT MaCTLH FROM TONKHO);

    IF v_ChuaXepXong = 0 THEN
        UPDATE LOHANG SET TrangThaiLH = 'Đã nhập kho' WHERE MaLH = v_MaLH;
    END IF;
    COMMIT;
END;
/

-- ------------------------------------------------------------------------------------
-- 4. QUẢN LÝ XUẤT KHO & GIAO HÀNG
-- ------------------------------------------------------------------------------------
CREATE OR REPLACE PROCEDURE SP_YEUCAU_XUATKHO (p_MaDH IN VARCHAR2) IS
    v_SoLuongCan NUMBER;
    v_SoLuongXuat NUMBER;
    v_TrangThaiDH NVARCHAR2(50);
BEGIN
    SELECT TrangThaiDH INTO v_TrangThaiDH FROM DONHANG WHERE MaDH = p_MaDH;
    IF v_TrangThaiDH IN ('Đang giao', 'Hoàn thành', 'Đã huỷ') THEN
        RAISE_APPLICATION_ERROR(-20023, 'Trạng thái đơn hàng không hợp lệ!');
    END IF;

    FOR rec_CTDH IN (SELECT MaCTDH, MaSP, SoLuong FROM CHITIETDONHANG WHERE MaDH = p_MaDH) LOOP
        v_SoLuongCan := rec_CTDH.SoLuong;
        FOR rec_TK IN (
            SELECT TK.MaTonKho, TK.SLKhaDung FROM TONKHO TK
            JOIN CHITIETLOHANG CTLH ON TK.MaCTLH = CTLH.MaCTLH
            WHERE CTLH.MaSP = rec_CTDH.MaSP AND TK.SLKhaDung > 0 AND TK.TGHetHan >= TRUNC(SYSDATE)
            ORDER BY TK.TGHetHan ASC, TK.TGNhapKho ASC FOR UPDATE
        ) LOOP
            EXIT WHEN v_SoLuongCan = 0;
            IF rec_TK.SLKhaDung >= v_SoLuongCan THEN
                v_SoLuongXuat := v_SoLuongCan; v_SoLuongCan := 0;
            ELSE
                v_SoLuongXuat := rec_TK.SLKhaDung; v_SoLuongCan := v_SoLuongCan - rec_TK.SLKhaDung;
            END IF;
            INSERT INTO XUATKHO (MaCTDH, MaTonKho, MaNV, SLXuat, TGCapNhat, TrangThaiXK) 
            VALUES (rec_CTDH.MaCTDH, rec_TK.MaTonKho, NULL, v_SoLuongXuat, SYSDATE, 'Tạm giữ');
        END LOOP;
        IF v_SoLuongCan > 0 THEN RAISE_APPLICATION_ERROR(-20024, 'Kho không đủ hàng cho: ' || rec_CTDH.MaSP); END IF;
    END LOOP;
    COMMIT;
EXCEPTION
    WHEN OTHERS THEN ROLLBACK; RAISE;
END;
/

CREATE OR REPLACE PROCEDURE SP_XACNHAN_XUATKHO (p_MaDH IN VARCHAR2, p_MaNV IN VARCHAR2) IS
BEGIN
    UPDATE XUATKHO SET TrangThaiXK = 'Đã xuất', MaNV = p_MaNV, TGCapNhat = SYSDATE
    WHERE MaCTDH IN (SELECT MaCTDH FROM CHITIETDONHANG WHERE MaDH = p_MaDH) AND TrangThaiXK = 'Tạm giữ';
    UPDATE DONHANG SET TrangThaiDH = 'Chờ giao hàng' WHERE MaDH = p_MaDH;
    COMMIT;
EXCEPTION
    WHEN OTHERS THEN ROLLBACK; RAISE;
END;
/

-- ------------------------------------------------------------------------------------
-- 5. QUẢN LÝ THÔNG BÁO & KIỂM TRA HỆ THỐNG
-- ------------------------------------------------------------------------------------

-- ================================= KIỂM TRA HẾT HẠN HÀNG NGÀY =================================
-- Procedure này quét toàn bộ kho để cập nhật trạng thái Hết hạn/Sắp hết hạn 
-- và sinh thông báo nếu cần. Nên được gọi khi khởi động ứng dụng.
-- ================================= KIỂM TRA HẾT HẠN HÀNG NGÀY =================================
CREATE OR REPLACE PROCEDURE SP_KIEMTRA_HETHAN_THONGBAO IS
    v_SoNgayHSD NUMBER;
    v_MinTonKho NUMBER;
    v_NgayHienTai DATE := TRUNC(SYSDATE);
BEGIN
    -- 1. Lấy cấu hình
    BEGIN SELECT GiaTri INTO v_SoNgayHSD FROM THAMSO WHERE TenTS = 'CANHBAO_HETHAN'; EXCEPTION WHEN NO_DATA_FOUND THEN v_SoNgayHSD := 7; END;
    BEGIN SELECT GiaTri INTO v_MinTonKho FROM THAMSO WHERE TenTS = 'MIN_TONKHO'; EXCEPTION WHEN NO_DATA_FOUND THEN v_MinTonKho := 20; END;

    -- 2. Cập nhật HẾT HẠN
    UPDATE TONKHO SET TrangThai = N'Hết hạn', SLKhaDung = 0 WHERE TGHetHan < v_NgayHienTai AND TrangThai <> N'Hết hạn';
    -- 3. Cập nhật SẮP HẾT HẠN
    UPDATE TONKHO SET TrangThai = N'Sắp hết hạn' WHERE TGHetHan >= v_NgayHienTai AND (TGHetHan - v_NgayHienTai) <= v_SoNgayHSD AND TrangThai = N'Còn hạn';

    -- 4. Thông báo Hết hạn (Lô)
    FOR tk IN (
        SELECT TK.MaTonKho, SP.TenSP, TK.TGHetHan FROM TONKHO TK JOIN CHITIETLOHANG CTLH ON TK.MaCTLH = CTLH.MaCTLH JOIN SANPHAM SP ON CTLH.MaSP = SP.MaSP WHERE TK.TrangThai = N'Hết hạn'
    ) LOOP
        DECLARE 
            v_Msg NVARCHAR2(500) := N'Lô ' || tk.MaTonKho || N' của SP ' || tk.TenSP || N' đã HẾT HẠN vào ngày ' || TO_CHAR(tk.TGHetHan, 'DD/MM/YYYY');
            v_Count NUMBER;
        BEGIN
            SELECT COUNT(*) INTO v_Count FROM THONGBAO WHERE NoiDung = v_Msg;
            IF v_Count = 0 THEN INSERT INTO THONGBAO (MaTB, LoaiTB, NoiDung, TrangThaiTB, TGTao) VALUES ('TB' || LPAD(SEQ_THONGBAO.NEXTVAL, 8, '0'), N'Hết hạn', v_Msg, 0, SYSDATE); END IF;
        END;
    END LOOP;

    -- 5. Thông báo Sắp hết hạn (Lô)
    FOR tk IN (
        SELECT TK.MaTonKho, SP.TenSP, TK.TGHetHan FROM TONKHO TK JOIN CHITIETLOHANG CTLH ON TK.MaCTLH = CTLH.MaCTLH JOIN SANPHAM SP ON CTLH.MaSP = SP.MaSP WHERE TK.TrangThai = N'Sắp hết hạn'
    ) LOOP
        DECLARE 
            v_Msg NVARCHAR2(500) := N'Lô ' || tk.MaTonKho || N' của SP ' || tk.TenSP || N' sẽ hết hạn vào ' || TO_CHAR(tk.TGHetHan, 'DD/MM/YYYY');
            v_Count NUMBER;
        BEGIN
            SELECT COUNT(*) INTO v_Count FROM THONGBAO WHERE NoiDung = v_Msg;
            IF v_Count = 0 THEN INSERT INTO THONGBAO (MaTB, LoaiTB, NoiDung, TrangThaiTB, TGTao) VALUES ('TB' || LPAD(SEQ_THONGBAO.NEXTVAL, 8, '0'), N'Sắp hết hạn', v_Msg, 0, SYSDATE); END IF;
        END;
    END LOOP;

    -- 6. TÍNH TỔNG TỒN KHO CHO MỖI SẢN PHẨM 
    -- (Đã xóa bỏ logic ép hàng hết hạn về 0. Giờ hệ thống tính tổng theo số lượng vật lý thực tế)
    FOR rec IN (
        SELECT SP.MaSP, SP.TenSP, 
               NVL(SUM(TK.SLConLai), 0) AS TongTon
        FROM SANPHAM SP
        LEFT JOIN CHITIETLOHANG CTLH ON SP.MaSP = CTLH.MaSP
        LEFT JOIN TONKHO TK ON CTLH.MaCTLH = TK.MaCTLH
        GROUP BY SP.MaSP, SP.TenSP
    ) LOOP
        DECLARE
            v_Msg NVARCHAR2(500);
            v_Type NVARCHAR2(50);
            v_Count NUMBER;
        BEGIN
            IF rec.TongTon <= 0 THEN
                v_Type := N'Hết hàng';
                v_Msg := N'CẢNH BÁO: Sản phẩm [' || rec.TenSP || N'] đã HẾT HÀNG hoàn toàn. Yêu cầu nhập hàng mới ngay!';
            ELSIF rec.TongTon < v_MinTonKho THEN
                v_Type := N'Sắp hết hàng';
                v_Msg := N'Thông báo: Sản phẩm [' || rec.TenSP || N'] sắp hết hàng. Hiện chỉ còn ' || rec.TongTon || N' đơn vị. Yêu cầu nhập thêm hàng!';
            ELSE
                CONTINUE; 
            END IF;

            -- Logic chống trùng lặp theo ngày
            SELECT COUNT(*) INTO v_Count 
            FROM THONGBAO 
            WHERE LoaiTB = v_Type 
              AND NoiDung LIKE N'%[' || rec.TenSP || N']%'
              AND TRUNC(TGTao) = TRUNC(SYSDATE);
            
            IF v_Count = 0 THEN
                INSERT INTO THONGBAO (MaTB, LoaiTB, NoiDung, TrangThaiTB, TGTao) 
                VALUES ('TB' || LPAD(SEQ_THONGBAO.NEXTVAL, 8, '0'), v_Type, v_Msg, 0, SYSDATE);
            END IF;
        END;
    END LOOP;

    COMMIT;
END;
/

-- ================================= Bảng KHACHHANG =================================
CREATE OR REPLACE PROCEDURE SP_THEM_KH (
    p_Username IN NVARCHAR2,
    p_TenKH IN NVARCHAR2,
    p_LoaiKH IN NVARCHAR2
) IS
BEGIN
    INSERT INTO KHACHHANG (Username, TenKH, LoaiKH)
    VALUES (p_Username, p_TenKH, p_LoaiKH);
END;
/

CREATE OR REPLACE PROCEDURE SP_CAPNHAT_KH (
    p_MaKH IN VARCHAR2,
    p_TenKH IN NVARCHAR2,
    p_LoaiKH IN NVARCHAR2
) IS
BEGIN
    UPDATE KHACHHANG
    SET TenKH = NVL(p_TenKH, TenKH),
        LoaiKH = NVL(p_LoaiKH, LoaiKH)
    WHERE MaKH = p_MaKH;
END;
/

CREATE OR REPLACE PROCEDURE SP_THEM_NV (
    p_Username IN NVARCHAR2,
    p_TenNV IN NVARCHAR2,
    p_ChucVu IN NVARCHAR2,
    p_Luong IN NUMBER
) IS
BEGIN
    INSERT INTO NHANVIEN (Username, TenNV, ChucVu, Luong)
    VALUES (p_Username, p_TenNV, p_ChucVu, p_Luong);
END;
/

CREATE OR REPLACE PROCEDURE SP_CAPNHAT_NV (
    p_MaNV IN VARCHAR2,
    p_TenNV IN NVARCHAR2,
    p_ChucVu IN NVARCHAR2,
    p_Luong IN NUMBER
) IS
BEGIN
    UPDATE NHANVIEN
    SET TenNV = NVL(p_TenNV, TenNV),
        ChucVu = NVL(p_ChucVu, ChucVu),
        Luong = NVL(p_Luong, Luong)
    WHERE MaNV = p_MaNV;
END;
/
-- ================================= Bảng TAIKHOAN =================================
CREATE OR REPLACE PROCEDURE SP_THEM_TAIKHOAN (
    p_Username IN NVARCHAR2,
    p_Password IN VARCHAR2,
    p_LoaiTK IN NUMBER,
    p_DiaChi IN NVARCHAR2,
    p_SDT IN VARCHAR2,
    p_Email IN NVARCHAR2
) IS
BEGIN
    -- Mặc định TrangThaiTK = 1 (Đang hoạt động)
    INSERT INTO TAIKHOAN (Username, Password, LoaiTK, TrangThaiTK, DiaChi, SDT, Email) 
    VALUES (p_Username, p_Password, p_LoaiTK, 1, p_DiaChi, p_SDT, p_Email);
END;
/

CREATE OR REPLACE PROCEDURE SP_CAPNHAT_TAIKHOAN (
    p_Username IN NVARCHAR2,
    p_DiaChi IN NVARCHAR2,
    p_SDT IN VARCHAR2,
    p_Email IN NVARCHAR2
) IS
BEGIN
    UPDATE TAIKHOAN
    SET DiaChi = NVL(p_DiaChi, DiaChi),
        SDT = NVL(p_SDT, SDT),
        Email = NVL(p_Email, Email)
    WHERE Username = p_Username;
END;
/

CREATE OR REPLACE PROCEDURE SP_DANGKY_TAIKHOAN (
    p_Username IN NVARCHAR2,
    p_Password IN VARCHAR2,
    p_LoaiTK IN NUMBER,       -- 0: Quản lý, 1: Nhân viên, 2: Khách hàng
    p_Ten IN NVARCHAR2,
    p_DiaChi IN NVARCHAR2,
    p_SDT IN VARCHAR2,
    p_Email IN NVARCHAR2,
    p_LoaiKHHoacChucVu IN NVARCHAR2, -- Nhận LoaiKH (Thường/VIP) hoặc ChucVu (Quản lý/NV kho...)
    p_Luong IN NUMBER         -- Chỉ dùng cho nhân viên, KH truyền NULL
) 
IS
BEGIN
    -- 1. Gọi SP tạo tài khoản
    SP_THEM_TAIKHOAN(p_Username, p_Password, p_LoaiTK, p_DiaChi, p_SDT, p_Email);

    -- 2. Phân nhánh thêm thông tin chi tiết
    IF p_LoaiTK = 2 THEN 
        -- Nếu là Khách hàng (Mặc định loại KH = 'Thường')
        SP_THEM_KH(p_Username, p_Ten, NVL(p_LoaiKHHoacChucVu, 'Thường'));
    ELSIF p_LoaiTK IN (0, 1) THEN 
        -- Nếu là Quản lý hoặc Nhân viên
        SP_THEM_NV(p_Username, p_Ten, p_LoaiKHHoacChucVu, NVL(p_Luong, 0));
    END IF;

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20040, 'Lỗi hệ thống khi đăng ký tài khoản: ' || SQLERRM);
END;
/



-- Đổi mật khẩu
CREATE OR REPLACE PROCEDURE SP_DOI_PASSWORD (
    p_Username IN NVARCHAR2,
    p_NewPassword IN VARCHAR2
) IS
BEGIN
    UPDATE TAIKHOAN SET Password = p_NewPassword WHERE Username = p_Username;
    COMMIT;
END;
/

-- Khoá tài khoản
CREATE OR REPLACE PROCEDURE SP_KHOA_TAIKHOAN (p_Username IN NVARCHAR2) IS
BEGIN
    UPDATE TAIKHOAN SET TrangThaiTK = 0 WHERE Username = p_Username;
    COMMIT;
END;
/

-- Lấy danh sách đơn hàng của một khách hàng kèm chi tiết sản phẩm gom bằng LISTAGG
CREATE OR REPLACE PROCEDURE SP_LAY_DS_DONHANG_BY_KH (
    p_MaKH IN VARCHAR2,
    p_Cursor OUT SYS_REFCURSOR
) IS
BEGIN
    OPEN p_Cursor FOR
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
    
EXCEPTION
    WHEN OTHERS THEN
        RAISE_APPLICATION_ERROR(-20040, 'Lỗi khi lấy danh sách đơn hàng: ' || SQLERRM);
END SP_LAY_DS_DONHANG_BY_KH;
/

-- Thống kê Top N Sản phẩm bán chạy nhất
CREATE OR REPLACE PROCEDURE SP_THONGKE_SANPHAM (
    p_Limit IN NUMBER,
    p_Type IN VARCHAR2,
    p_FromDate IN DATE,
    p_ToDate IN DATE,
    p_Cursor OUT SYS_REFCURSOR
)
AS
    v_Limit NUMBER := p_Limit;
BEGIN
    IF v_Limit = 0 THEN 
        v_Limit := 999999; 
    END IF;

    IF p_Type = 'BEST' THEN
        OPEN p_Cursor FOR
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
        OPEN p_Cursor FOR
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
END;
/

CREATE OR REPLACE PROCEDURE SP_THONGKE_TRANGTHAI (
    p_FromDate IN DATE,
    p_ToDate IN DATE,
    p_Cursor OUT SYS_REFCURSOR
)
AS
BEGIN
    OPEN p_Cursor FOR
    SELECT TrangThaiDH, COUNT(MaDH) as SoLuong 
    FROM DONHANG 
    WHERE TRUNC(TGDat) BETWEEN p_FromDate AND p_ToDate
    GROUP BY TrangThaiDH
    ORDER BY SoLuong DESC;
END;
/

CREATE OR REPLACE PROCEDURE SP_THONGKE_TAICHINH (
    p_LoaiThongKe IN VARCHAR2, -- 'MONTH' hoặc 'DAY'
    p_Period IN NUMBER,
    p_Cursor OUT SYS_REFCURSOR
)
AS
    v_AnchorDate DATE := SYSDATE;
BEGIN
    IF p_LoaiThongKe = 'MONTH' THEN
        OPEN p_Cursor FOR
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
            TO_CHAR(mr.ThoiGian, 'MM/YYYY') AS ThangNam, -- Giữ tên cột là ThangNam để tương thích Java DTO
            NVL(rd.DoanhThu, 0) AS DoanhThu,
            NVL(cd.ChiPhi, 0) AS ChiPhi
        FROM MonthRange mr
        LEFT JOIN RevenueData rd ON mr.ThoiGian = rd.ThoiGian
        LEFT JOIN CostData cd ON mr.ThoiGian = cd.ThoiGian
        ORDER BY mr.ThoiGian ASC;
        
    ELSE -- Xử lý cho chế độ theo NGÀY
        OPEN p_Cursor FOR
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
END;
/

-- ================================= Giỏ hàng =================================

CREATE OR REPLACE PROCEDURE PROC_CLEANUP_GIOHANG IS
BEGIN
    DELETE FROM GIOHANG
    WHERE (SYSTIMESTAMP - TGCapNhat) > INTERVAL '12' HOUR;
 
    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        DBMS_OUTPUT.PUT_LINE('Lỗi PROC_CLEANUP_GIOHANG: ' || SQLERRM);
END PROC_CLEANUP_GIOHANG;
/
 

BEGIN
    BEGIN
        DBMS_SCHEDULER.DROP_JOB(job_name => 'JOB_CLEANUP_GIOHANG', force => TRUE);
    EXCEPTION
        WHEN OTHERS THEN NULL;
    END;
 
    DBMS_SCHEDULER.CREATE_JOB(
        job_name        => 'JOB_CLEANUP_GIOHANG',
        job_type        => 'STORED_PROCEDURE',
        job_action      => 'PROC_CLEANUP_GIOHANG',
        start_date      => SYSTIMESTAMP,
        repeat_interval => 'FREQ=MINUTELY; INTERVAL=30',
        enabled         => TRUE,
        auto_drop       => FALSE,
        comments        => 'Dọn sạch giỏ hàng bỏ quên quá 12 giờ'
    );
END;
/
 
-- Procedure Nhận đơn giao
CREATE OR REPLACE PROCEDURE SP_XACNHAN_GIAOHANG (
    p_MaDH IN VARCHAR2,
    p_MaNV IN VARCHAR2
) IS
BEGIN
    UPDATE DONHANG 
    SET TrangThaiDH = 'Đang giao', MaNV = p_MaNV 
    WHERE MaDH = p_MaDH AND TrangThaiDH = 'Chờ giao hàng';
    COMMIT;
END;
/

-- 1. Procedure Nhận đơn giao (Chỉ ghi nhận mã nhân viên, giữ nguyên trạng thái Chờ giao hàng)
CREATE OR REPLACE PROCEDURE SP_XACNHAN_GIAOHANG (
    p_MaDH IN VARCHAR2,
    p_MaNV IN VARCHAR2
) IS
BEGIN
    UPDATE DONHANG 
    SET MaNV = p_MaNV 
    WHERE MaDH = p_MaDH AND TrangThaiDH = N'Chờ giao hàng';
    COMMIT;
END;
/

-- 2. Procedure Giao hàng thành công (Cập nhật Hoàn thành và thêm SYSDATE vào TGGIAOTT)
CREATE OR REPLACE PROCEDURE SP_GIAOHANG_THANHCONG (
    p_MaDH IN VARCHAR2,
    p_MaNV IN VARCHAR2
) IS
BEGIN
    UPDATE DONHANG 
    SET TrangThaiDH = N'Hoàn thành', 
        TGGiaoTT = SYSDATE,
        TrangThaiTT = 1 
    WHERE MaDH = p_MaDH AND MaNV = p_MaNV AND TrangThaiDH = N'Chờ giao hàng';
    COMMIT;
END;
/

-- 3. Procedure Giao hàng thất bại (Cập nhật Đã huỷ và ghi nhận lý do huỷ)
CREATE OR REPLACE PROCEDURE SP_GIAOHANG_THATBAI (
    p_MaDH IN VARCHAR2,
    p_MaNV IN VARCHAR2,
    p_LyDo IN NVARCHAR2
) IS
BEGIN
    UPDATE DONHANG 
    SET TrangThaiDH = N'Đã huỷ', 
        TGGiaoTT = SYSDATE,
        LyDoHuy = p_LyDo
    WHERE MaDH = p_MaDH AND MaNV = p_MaNV AND TrangThaiDH = N'Chờ giao hàng';
    COMMIT;
END;
/