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
    SET GiaMua = p_GiaMuaMoi,
        GiaBan = p_GiaBanMoi
    WHERE MaSP = p_MaSP;
    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20010, 'Lỗi cập nhật giá sản phẩm: ' || SQLERRM);
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
END;
/

CREATE OR REPLACE PROCEDURE SP_CAPNHAT_SP (
    p_MaSP IN VARCHAR2, p_TenSP IN NVARCHAR2, p_MaLSP IN VARCHAR2, p_ChatLuong IN NVARCHAR2,
    p_GiaMua IN NUMBER, p_GiaBan IN NUMBER, p_DonViTinh IN NVARCHAR2, p_BaoQuan IN NVARCHAR2
) IS
BEGIN
    UPDATE SANPHAM
    SET TenSP = NVL(p_TenSP, TenSP), MaLSP = NVL(p_MaLSP, MaLSP), ChatLuong = NVL(p_ChatLuong, ChatLuong),
        GiaMua = NVL(p_GiaMua, GiaMua), GiaBan = NVL(p_GiaBan, GiaBan), 
        DonViTinh = NVL(p_DonViTinh, DonViTinh), BaoQuan = NVL(p_BaoQuan, BaoQuan)
    WHERE MaSP = p_MaSP;
    COMMIT;
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