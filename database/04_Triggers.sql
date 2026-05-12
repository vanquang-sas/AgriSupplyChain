-- ====================================================================================
--                          PHẦN 4: Tạo TRIGGER
-- ====================================================================================

-- ================================= Bảng NHACUNGCAP =================================
-- 1. Kiểm tra SĐT nhà cung cấp (10-12 số)
CREATE OR REPLACE TRIGGER TRG_NCC_SDT
BEFORE INSERT OR UPDATE ON NHACUNGCAP
FOR EACH ROW
BEGIN
    IF LENGTH(:NEW.SDT) < 10 OR LENGTH(:NEW.SDT) > 12 OR REGEXP_LIKE(:NEW.SDT, '[^0-9]') THEN
        RAISE_APPLICATION_ERROR(-20001, 'Số điện thoại phải là chuỗi số từ 10 - 12 ký tự.');
    END IF;
END;
/

-- ================================= Bảng KHACHHANG =================================
-- 2. Kiểm tra SĐT khách hàng (10-12 số)
CREATE OR REPLACE TRIGGER TRG_KH_SDT
BEFORE INSERT OR UPDATE ON KHACHHANG
FOR EACH ROW
BEGIN
    IF LENGTH(:NEW.SDT) < 10 OR LENGTH(:NEW.SDT) > 12 OR REGEXP_LIKE(:NEW.SDT, '[^0-9]') THEN
        RAISE_APPLICATION_ERROR(-20001, 'Số điện thoại phải là chuỗi số từ 10 - 12 ký tự.');
    END IF;
END;
/

-- ================================= Bảng CHITIETDONHANG =================================
-- 3. Tự động lấy GiaBan hiện hành từ bảng SANPHAM điền vào GiaBan và tính ThanhTien
CREATE OR REPLACE TRIGGER TRG_CTDH_THANHTIEN
BEFORE INSERT OR UPDATE ON CHITIETDONHANG
FOR EACH ROW
DECLARE
    v_GiaBan NUMBER(12, 2);
BEGIN
    SELECT GiaBan INTO v_GiaBan
    FROM SANPHAM
    WHERE MaSP = :NEW.MaSP;

    :NEW.GiaBan := v_GiaBan;
    :NEW.ThanhTien := :NEW.SoLuong * v_GiaBan;

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RAISE_APPLICATION_ERROR(-20002, 'Lỗi: Không tìm thấy sản phẩm có mã ' || :NEW.MaSP);
END;
/

-- 4. Tự động tính TongTien DONHANG khi thay đổi CHITIETDONHANG
CREATE OR REPLACE TRIGGER TRG_CTDH_TONGTIEN
AFTER INSERT OR UPDATE OR DELETE ON CHITIETDONHANG
FOR EACH ROW
DECLARE
    v_Delta NUMBER := 0;
    v_MaDH VARCHAR2(10);
    v_LoaiKH NVARCHAR2(50);
    
    v_TyLeGiamGia NUMBER := 0;
    v_TongTienHangMoi NUMBER := 0;
    v_PhiVanChuyen NUMBER := 0;
    v_GiamGiaMoi NUMBER := 0;
BEGIN
    -- 1. Xác định mức thay đổi của ThanhTien (Delta)
    IF INSERTING THEN
        v_Delta := :NEW.ThanhTien;
        v_MaDH := :NEW.MaDH;
    ELSIF UPDATING THEN
        v_Delta := :NEW.ThanhTien - :OLD.ThanhTien;
        v_MaDH := :NEW.MaDH;
    ELSIF DELETING THEN
        v_Delta := -:OLD.ThanhTien;
        v_MaDH := :OLD.MaDH;
    END IF;

    -- 2. Lấy Tổng tiền hàng hiện tại, Phí vận chuyển và Loại khách hàng
    SELECT DH.TongTienHang, DH.PhiVanChuyen, KH.LoaiKH 
    INTO v_TongTienHangMoi, v_PhiVanChuyen, v_LoaiKH
    FROM DONHANG DH
    JOIN KHACHHANG KH ON DH.MaKH = KH.MaKH
    WHERE DH.MaDH = v_MaDH;

    -- Cộng dồn Delta để ra Tổng tiền hàng mới
    v_TongTienHangMoi := NVL(v_TongTienHangMoi, 0) + v_Delta;

    -- 3. Xác định tỷ lệ giảm giá từ bảng THAMSO dựa trên LoaiKH
    -- Giả sử giá trị lưu trong THAMSO là số thập phân (Ví dụ: 0.05 tương đương 5%)
    BEGIN
        IF v_LoaiKH = 'Thường' THEN
            SELECT GiaTri INTO v_TyLeGiamGia FROM THAMSO WHERE TenTS = 'GG_THUONG';
        ELSIF v_LoaiKH = 'Thân thiết' THEN
            SELECT GiaTri INTO v_TyLeGiamGia FROM THAMSO WHERE TenTS = 'GG_THANTHIET';
        ELSIF v_LoaiKH = 'VIP' THEN
            SELECT GiaTri INTO v_TyLeGiamGia FROM THAMSO WHERE TenTS = 'GG_VIP';
        ELSE
            v_TyLeGiamGia := 0;
        END IF;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN 
            v_TyLeGiamGia := 0; 
    END;

    -- 4. Tính toán số tiền Giảm giá mới
    v_GiamGiaMoi := v_TongTienHangMoi * v_TyLeGiamGia;

    -- 5. Cập nhật TongTien = TongTienHang + PhiVanChuyen - GiamGia
    UPDATE DONHANG 
    SET TongTienHang = v_TongTienHangMoi,
        GiamGia = v_GiamGiaMoi,
        TongTien = v_TongTienHangMoi + NVL(v_PhiVanChuyen, 0) - v_GiamGiaMoi
    WHERE MaDH = v_MaDH;
END;
/

-- 5. Không cho phép thêm, sửa, xóa chi tiết đơn hàng nếu chờ xử lý hoặc hoàn thành
CREATE OR REPLACE TRIGGER TRG_CTDH_CHECK_TRANGTHAIDH
BEFORE INSERT OR UPDATE OR DELETE ON CHITIETDONHANG
FOR EACH ROW
DECLARE
    v_TrangThai NVARCHAR2(50);
    v_MaDH_Check VARCHAR2(10);
BEGIN
    -- Dùng OLD khi DELETE, dùng NEW khi INSERT/UPDATE
    IF DELETING THEN
        v_MaDH_Check := :OLD.MaDH;
    ELSE
        v_MaDH_Check := :NEW.MaDH;
    END IF;

    SELECT TrangThaiDH INTO v_TrangThai
    FROM DONHANG
    WHERE MaDH = v_MaDH_Check;

    IF v_TrangThai IN ('Chờ xử lý', 'Hoàn thành') THEN
        RAISE_APPLICATION_ERROR(-20003, 
            'Khong the thay doi chi tiet don hang vi don hang dang o trang thai: ' || v_TrangThai);
    END IF;
    
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RAISE_APPLICATION_ERROR(-20004, 'Khong tim thay thong tin don hang tuong ung.');
END;
/

-- ================================= Bảng CHITIETLOHANG =================================
-- 5. Tự động lấy GiaMua hiện hành từ bảng SANPHAM điền vào GiaMua và tính ThanhTien
CREATE OR REPLACE TRIGGER TRG_CTLH_THANHTIEN
BEFORE INSERT OR UPDATE ON CHITIETLOHANG
FOR EACH ROW
DECLARE
    v_GiaMua NUMBER(12, 2);
BEGIN
    SELECT GiaMua INTO v_GiaMua
    FROM SANPHAM
    WHERE MaSP = :NEW.MaSP;

    :NEW.GiaMua := v_GiaMua;
    :NEW.ThanhTien := :NEW.SoLuong * v_GiaMua;

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RAISE_APPLICATION_ERROR(-20005, 'Lỗi: Không tìm thấy sản phẩm có mã ' || :NEW.MaSP);
END;
/

-- 6. Tự động tính TongTien LOHANG khi thay đổi CHITIETLOHANG
CREATE OR REPLACE TRIGGER TRG_CTLH_TONGTIEN
AFTER INSERT OR UPDATE OR DELETE ON CHITIETLOHANG
FOR EACH ROW
BEGIN
    IF INSERTING THEN
        UPDATE LOHANG SET TongTien = NVL(TongTien, 0) + :NEW.ThanhTien WHERE MaLH = :NEW.MaLH;
    ELSIF UPDATING THEN
        UPDATE LOHANG SET TongTien = NVL(TongTien, 0) - :OLD.ThanhTien + :NEW.ThanhTien WHERE MaLH = :NEW.MaLH;
    ELSIF DELETING THEN
        UPDATE LOHANG SET TongTien = NVL(TongTien, 0) - :OLD.ThanhTien WHERE MaLH = :OLD.MaLH;
    END IF;
END;
/

-- ================================= Bảng DONHANG =================================
-- 7. Kiểm tra trạng thái tài khoản khách hàng trước khi tạo đơn hàng mới
CREATE OR REPLACE TRIGGER TRG_DH_CHECK_KH
BEFORE INSERT ON DONHANG
FOR EACH ROW
DECLARE
    v_TrangThaiTK NUMBER(1);
BEGIN
    SELECT TK.TrangThaiTK INTO v_TrangThaiTK
    FROM KHACHHANG KH
    JOIN TAIKHOAN TK ON KH.Username = TK.Username
    WHERE KH.MaKH = :NEW.MaKH;

    IF v_TrangThaiTK = 0 THEN
        RAISE_APPLICATION_ERROR(-20006, 'Từ chối giao dịch: Tài khoản của khách hàng này đang bị khóa.');
    END IF;

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RAISE_APPLICATION_ERROR(-20007, 'Lỗi: Không tìm thấy thông tin khách hàng hoặc tài khoản liên kết.');
END;
/

-- ================================= Bảng LOHANG =================================
-- 8. Kiểm tra trạng thái hợp tác của nhà cung cấp trước khi tạo lô hàng mới
CREATE OR REPLACE TRIGGER TRG_LH_CHECK_NCC
BEFORE INSERT ON LOHANG
FOR EACH ROW
DECLARE
    v_TrangThaiHopTac NUMBER(1);
BEGIN
    SELECT TrangThaiHopTac INTO v_TrangThaiHopTac
    FROM NHACUNGCAP
    WHERE MaNCC = :NEW.MaNCC;

    IF v_TrangThaiHopTac = 0 THEN
        RAISE_APPLICATION_ERROR(-20008, 'Từ chối giao dịch: Nhà cung cấp đang trong trạng thái tạm ngừng hợp tác.');
    END IF;

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RAISE_APPLICATION_ERROR(-20009, 'Lỗi: Mã Nhà cung cấp không tồn tại trong hệ thống.');
END;
/

-- ================================= Bảng XUATKHO =================================
-- 9. Xử lý trừ tồn kho dựa trên TrangThaiXK
CREATE OR REPLACE TRIGGER TRG_XK_CAPNHAT_TONKHO
BEFORE INSERT OR UPDATE OF TrangThaiXK OR DELETE ON XUATKHO
FOR EACH ROW
DECLARE
    v_SLConLai NUMBER;
    v_SLKhaDung NUMBER;
    v_MaTonKho_Check VARCHAR2(10);
BEGIN
    -- Lấy mã tồn kho tuỳ theo thao tác
    IF DELETING THEN v_MaTonKho_Check := :OLD.MaTonKho;
    ELSE v_MaTonKho_Check := :NEW.MaTonKho; END IF;

    -- Lấy số lượng hiện tại trong kho để kiểm tra
    SELECT SLConLai, SLKhaDung INTO v_SLConLai, v_SLKhaDung 
    FROM TONKHO WHERE MaTonKho = v_MaTonKho_Check;

    IF INSERTING THEN
        -- Lúc INSERT TrangThaiXK luôn là 'Tạm giữ', khi nào nhân viên xác nhận xuất hàng mới chuyển thành 'Đã xuất'
        IF v_SLKhaDung < :NEW.SLXuat THEN
            RAISE_APPLICATION_ERROR(-20010, 'Lỗi: Kho không đủ SL khả dụng (' || v_SLKhaDung || ') cho lô ' || :NEW.MaTonKho);
        ELSE
            UPDATE TONKHO SET SLKhaDung = SLKhaDung - :NEW.SLXuat WHERE MaTonKho = :NEW.MaTonKho;
        END IF;

    ELSIF UPDATING THEN
        -- Chuyển từ "Tạm giữ" -> "Đã xuất": Lấy hàng ra khỏi kho (Trừ SLConLai, còn SLKhaDung đã trừ trước đó rồi)
        IF :OLD.TrangThaiXK = 'Tạm giữ' AND :NEW.TrangThaiXK = 'Đã xuất' THEN
            UPDATE TONKHO SET SLConLai = SLConLai - :NEW.SLXuat WHERE MaTonKho = :NEW.MaTonKho;
            
        -- Chuyển từ "Tạm giữ" -> "Đã huỷ": Khách huỷ đơn trước khi giao, hoàn lại SLKhaDung
        ELSIF :OLD.TrangThaiXK = 'Tạm giữ' AND :NEW.TrangThaiXK = 'Đã huỷ' THEN
            UPDATE TONKHO SET SLKhaDung = SLKhaDung + :OLD.SLXuat WHERE MaTonKho = :NEW.MaTonKho;
            
        -- Chuyển từ "Đã xuất" -> "Đã huỷ": Hàng đã đi nhưng bị trả về, hoàn lại cả 2
        ELSIF :OLD.TrangThaiXK = 'Đã xuất' AND :NEW.TrangThaiXK = 'Đã huỷ' THEN
            UPDATE TONKHO 
            SET SLConLai = SLConLai + :OLD.SLXuat, SLKhaDung = SLKhaDung + :OLD.SLXuat 
            WHERE MaTonKho = :NEW.MaTonKho;
        END IF;

    ELSIF DELETING THEN
        IF :OLD.TrangThaiXK = 'Tạm giữ' THEN
            UPDATE TONKHO SET SLKhaDung = SLKhaDung + :OLD.SLXuat WHERE MaTonKho = :OLD.MaTonKho;
        ELSIF :OLD.TrangThaiXK = 'Đã xuất' THEN
            UPDATE TONKHO 
            SET SLConLai = SLConLai + :OLD.SLXuat, SLKhaDung = SLKhaDung + :OLD.SLXuat 
            WHERE MaTonKho = :OLD.MaTonKho;
        END IF;
    END IF;
END;
/
-- ================================= Bảng SANPHAM =================================
-- 10. Tự động ghi nhận lịch sử giá mỗi khi thay đổi giá mua hoặc bán của sản phẩm
CREATE OR REPLACE TRIGGER TRG_SP_LUU_LSG
AFTER INSERT OR UPDATE OF GiaMua, GiaBan ON SANPHAM
FOR EACH ROW
BEGIN
    IF  INSERTING OR (UPDATING AND (:OLD.GiaMua <> :NEW.GiaMua) OR (:OLD.GiaBan <> :NEW.GiaBan)) THEN
        INSERT INTO LICHSUGIA (
            MaSP, 
            GiaMua, 
            GiaBan
        ) 
        VALUES (
            :NEW.MaSP, 
            :NEW.GiaMua, 
            :NEW.GiaBan
        );
    END IF;
END;
/

-- ================================= Bảng TONKHO =================================
CREATE OR REPLACE TRIGGER TRG_CANHBAO_HETHAN
AFTER INSERT OR UPDATE OF TGHetHan ON TONKHO
FOR EACH ROW
DECLARE
    v_SoNgayHSD NUMBER;
    v_MaTB VARCHAR2(10);
    v_TenSP NVARCHAR2(100);
BEGIN
    -- 1. Lấy quy định số ngày cảnh báo từ bảng THAMSO
    BEGIN
        SELECT GiaTri INTO v_SoNgayHSD FROM THAMSO WHERE TenTS = 'CANHBAO_HETHAN';
    EXCEPTION 
        WHEN NO_DATA_FOUND THEN v_SoNgayHSD := 7; -- Mặc định là 7 ngày nếu không cấu hình
    END;

    -- 2. Kiểm tra nếu thời gian hết hạn sắp tới
    IF :NEW.TGHetHan IS NOT NULL AND (:NEW.TGHetHan - TRUNC(SYSDATE)) <= v_SoNgayHSD THEN
        
        -- Lấy tên sản phẩm thông qua Chi tiết lô hàng
        SELECT SP.TenSP INTO v_TenSP 
        FROM CHITIETLOHANG CTLH 
        JOIN SANPHAM SP ON CTLH.MaSP = SP.MaSP 
        WHERE CTLH.MaCTLH = :NEW.MaCTLH;

        -- Insert vào bảng THONGBAO
        INSERT INTO THONGBAO (LoaiTB, NoiDung, TrangThaiTB, TGTao)
        VALUES (N'Sắp hết hạn', N'Lô ' || :NEW.MaTonKho || N' của SP ' || v_TenSP || N' sẽ hết hạn vào ' || TO_CHAR(:NEW.TGHetHan, 'DD/MM/YYYY'), 0, SYSDATE);
    END IF;
EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Lỗi trigger TRG_CANHBAO_HETHAN: ' || SQLERRM);
END;
/

CREATE OR REPLACE TRIGGER TRG_CANHBAO_MIN_TONKHO
AFTER UPDATE OF SLConLai ON TONKHO
FOR EACH ROW
DECLARE
    -- Sử dụng AUTONOMOUS_TRANSACTION để tránh lỗi (Mutating Table) khi SELECT SUM trên chính bảng đang UPDATE
    PRAGMA AUTONOMOUS_TRANSACTION; 
    v_MinTonKho NUMBER;
    v_TongTon NUMBER;
    v_MaSP VARCHAR2(10);
    v_TenSP NVARCHAR2(100);
    v_MaTB VARCHAR2(10);
BEGIN
    -- Chỉ kiểm tra khi số lượng bị giảm đi
    IF :NEW.SLConLai < :OLD.SLConLai THEN
        -- 1. Lấy ngưỡng tồn kho tối thiểu từ THAMSO
        BEGIN
            SELECT GiaTri INTO v_MinTonKho FROM THAMSO WHERE TenTS = 'MIN_TONKHO';
        EXCEPTION 
            WHEN NO_DATA_FOUND THEN v_MinTonKho := 20; -- Mặc định
        END;

        -- 2. Tìm Mã SP của lô hàng vừa bị xuất kho
        SELECT MaSP INTO v_MaSP FROM CHITIETLOHANG WHERE MaCTLH = :NEW.MaCTLH;

        -- 3. Tính tổng tồn kho khả dụng của sản phẩm đó
        SELECT SUM(TK.SLConLai) INTO v_TongTon
        FROM TONKHO TK
        JOIN CHITIETLOHANG CTLH ON TK.MaCTLH = CTLH.MaCTLH
        WHERE CTLH.MaSP = v_MaSP AND TK.TGHetHan >= TRUNC(SYSDATE);

        -- 4. Nếu tổng kho < mức quy định, tạo thông báo
        IF NVL(v_TongTon, 0) < v_MinTonKho THEN
            SELECT TenSP INTO v_TenSP FROM SANPHAM WHERE MaSP = v_MaSP;
            v_MaTB := 'TB' || DBMS_RANDOM.STRING('X', 8);

            INSERT INTO THONGBAO (LoaiTB, NoiDung)
            VALUES ('Cảnh báo tồn kho', 'Sản phẩm ' || v_TenSP || ' sắp hết hàng (Chỉ còn ' || v_TongTon || ' đơn vị).');
            
            COMMIT; -- Bắt buộc phải có COMMIT khi dùng AUTONOMOUS_TRANSACTION
        END IF;
    END IF;
END;
/

CREATE OR REPLACE TRIGGER TRG_CAPNHAT_TRANGTHAI_TONKHO
BEFORE INSERT OR UPDATE OF TGHetHan, MaCTLH ON TONKHO
FOR EACH ROW
DECLARE
    v_SoNgayHSD NUMBER;
    v_TenSP NVARCHAR2(100);
BEGIN
    -- 1. Lấy quy định số ngày cảnh báo từ bảng THAMSO
    BEGIN
        SELECT GiaTri INTO v_SoNgayHSD 
        FROM THAMSO 
        WHERE TenTS = 'CANHBAO_HETHAN';
    EXCEPTION 
        WHEN NO_DATA_FOUND THEN 
            v_SoNgayHSD := 7; -- Mặc định là 7 ngày nếu không tìm thấy cấu hình
    END;

    -- 2. Logic phân loại trạng thái dựa trên thời gian
    -- TRUNC(SYSDATE) để chỉ lấy ngày, bỏ qua giờ phút giây cho chính xác
    IF :NEW.TGHetHan IS NULL THEN
        :NEW.TrangThai := N'Còn hạn';
        
    ELSIF (TRUNC(:NEW.TGHetHan) < TRUNC(SYSDATE)) THEN
        :NEW.TrangThai := N'Hết hạn';
        
    ELSIF (TRUNC(:NEW.TGHetHan) - TRUNC(SYSDATE)) <= v_SoNgayHSD THEN
        :NEW.TrangThai := N'Sắp hết hạn';

        -- 3. Gửi thông báo khi rơi vào trạng thái "Sắp hết hạn"
        BEGIN
            SELECT SP.TenSP INTO v_TenSP 
            FROM CHITIETLOHANG CTLH 
            JOIN SANPHAM SP ON CTLH.MaSP = SP.MaSP 
            WHERE CTLH.MaCTLH = :NEW.MaCTLH;

            INSERT INTO THONGBAO (LoaiTB, NoiDung, TrangThaiTB, TGTao)
            VALUES (N'Sắp hết hạn', 
                    N'Lô ' || :NEW.MaTonKho || N' của SP ' || v_TenSP || N' sẽ hết hạn vào ' || TO_CHAR(:NEW.TGHetHan, 'DD/MM/YYYY'), 
                    0, SYSDATE);
        EXCEPTION 
            WHEN OTHERS THEN NULL; -- Tránh lỗi Trigger nếu không lấy được tên SP
        END;
        
    ELSE
        :NEW.TrangThai := N'Còn hạn';
    END IF;

EXCEPTION
    WHEN OTHERS THEN
        -- Ghi log lỗi ra console của Database nếu có sự cố
        DBMS_OUTPUT.PUT_LINE('Lỗi trigger TRG_CAPNHAT_TRANGTHAI_TONKHO: ' || SQLERRM);
END;
/