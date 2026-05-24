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

-- ================================= Bảng TAIKHOAN =================================
-- 2. Kiểm tra SĐT tài khoản khi có giá trị (10-12 số)
CREATE OR REPLACE TRIGGER TRG_TK_SDT
BEFORE INSERT OR UPDATE ON TAIKHOAN
FOR EACH ROW
BEGIN
    IF :NEW.SDT IS NOT NULL THEN
        IF LENGTH(:NEW.SDT) < 10 OR LENGTH(:NEW.SDT) > 12 OR REGEXP_LIKE(:NEW.SDT, '[^0-9]') THEN
            RAISE_APPLICATION_ERROR(-20001, 'Số điện thoại phải là chuỗi số từ 10 - 12 ký tự.');
        END IF;
    END IF;
END;
/

-- ================================= Bảng CHITIETDONHANG =================================
-- 3. Tự động lấy GiaBan hiện hành từ bảng SANPHAM điền vào GiaBan và tính ThanhTien
CREATE OR REPLACE TRIGGER TRG_CTDH_THANHTIEN
BEFORE INSERT OR UPDATE ON CHITIETDONHANG
FOR EACH ROW
DECLARE
    v_GiaBan NUMBER(15, 2);
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
    v_TongTienHangMoi := NVL(v_TongTienHangMoi, 0) + v_Delta;
    -- 3. Xác định tỷ lệ giảm giá từ bảng THAMSO dựa trên LoaiKH
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

    IF v_TrangThai IN ('Chờ xử lý', 'Hoàn thành', 'Chờ thanh toán') THEN
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
    v_GiaMua NUMBER(15, 2);
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

-- 9. Không cho phép xóa lô hàng đã nhập kho
CREATE OR REPLACE TRIGGER TRG_LH_XOA
BEFORE DELETE ON LOHANG
FOR EACH ROW
BEGIN
    IF :OLD.TrangThaiLH = N'Đã nhập kho' THEN
        RAISE_APPLICATION_ERROR(-20021, 'Không thể xoá lô hàng đã nhập kho!');
    END IF;
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

-- TRIGGER 1: Cập nhật trạng thái TONKHO trước khi INSERT/UPDATE
CREATE OR REPLACE TRIGGER TRG_CAPNHAT_TRANGTHAI_TONKHO
BEFORE INSERT OR UPDATE OF TGHetHan, SLConLai, TrangThai ON TONKHO
FOR EACH ROW
DECLARE
    v_SoNgayCB NUMBER;
    v_NgayHienTai DATE := TRUNC(SYSDATE);
    v_TenSP NVARCHAR2(100);
BEGIN
    -- 1. Lấy quy định từ tham số
    BEGIN
        SELECT GiaTri INTO v_SoNgayCB FROM THAMSO WHERE TenTS = 'CANHBAO_HETHAN';
    EXCEPTION WHEN NO_DATA_FOUND THEN v_SoNgayCB := 7;
    END;

    -- 2. Phân loại trạng thái dựa trên TGHetHan
    IF TRUNC(:NEW.TGHetHan) < v_NgayHienTai THEN
        :NEW.TrangThai := N'Hết hạn';
        :NEW.SLKhaDung := 0;
    ELSIF (TRUNC(:NEW.TGHetHan) - v_NgayHienTai) <= v_SoNgayCB THEN
        :NEW.TrangThai := N'Sắp hết hạn';
        :NEW.SLKhaDung := :NEW.SLConLai;
    ELSE
        :NEW.TrangThai := N'Còn hạn';
        :NEW.SLKhaDung := :NEW.SLConLai;
    END IF;
END;
/

-- TRIGGER 2: Cảnh báo HẾT HẠN (dựa trên TrangThai)
CREATE OR REPLACE TRIGGER TRG_CANHBAO_HETHAN
AFTER INSERT OR UPDATE OF TrangThai ON TONKHO
FOR EACH ROW
DECLARE
    PRAGMA AUTONOMOUS_TRANSACTION;
    v_TenSP NVARCHAR2(100);
    v_NoiDung NVARCHAR2(500);
    v_CheckExists NUMBER;
BEGIN
    -- Chỉ xử lý khi TrangThai = 'Hết hạn'
    IF :NEW.TrangThai = N'Hết hạn' THEN
        -- Lấy tên SP
        BEGIN
            SELECT SP.TenSP INTO v_TenSP FROM SANPHAM SP 
            WHERE SP.MaSP = :NEW.MaSP;
        EXCEPTION WHEN OTHERS THEN v_TenSP := N'Chưa rõ';
        END;

        v_NoiDung := N'Lô ' || :NEW.MaTonKho || N' của SP ' || v_TenSP || N' đã HẾT HẠN vào ngày ' || TO_CHAR(:NEW.TGHetHan, 'DD/MM/YYYY');
        
        -- Chống trùng
        SELECT COUNT(*) INTO v_CheckExists FROM THONGBAO WHERE NoiDung = v_NoiDung;
        
        IF v_CheckExists = 0 THEN
            INSERT INTO THONGBAO (MaTB, LoaiTB, NoiDung, TrangThaiTB, TGTao, NguoiNhan)
            VALUES ('TB' || LPAD(SEQ_THONGBAO.NEXTVAL, 8, '0'), N'Hết hạn', v_NoiDung, 0, SYSDATE, N'Nhân viên kho/thu mua');
        END IF;
    END IF;

    -- Xử lý khi TrangThai = 'Sắp hết hạn'
    IF :NEW.TrangThai = N'Sắp hết hạn' THEN
        BEGIN
            SELECT SP.TenSP INTO v_TenSP FROM SANPHAM SP 
            WHERE SP.MaSP = :NEW.MaSP;
        EXCEPTION WHEN OTHERS THEN v_TenSP := N'Chưa rõ';
        END;

        v_NoiDung := N'Lô ' || :NEW.MaTonKho || N' của SP ' || v_TenSP || N' sẽ hết hạn vào ' || TO_CHAR(:NEW.TGHetHan, 'DD/MM/YYYY');

        SELECT COUNT(*) INTO v_CheckExists FROM THONGBAO WHERE NoiDung = v_NoiDung;

        IF v_CheckExists = 0 THEN
            INSERT INTO THONGBAO (MaTB, LoaiTB, NoiDung, TrangThaiTB, TGTao, NguoiNhan)
            VALUES ('TB' || LPAD(SEQ_THONGBAO.NEXTVAL, 8, '0'), N'Sắp hết hạn', v_NoiDung, 0, SYSDATE, N'Nhân viên kho/thu mua');
        END IF;
    END IF;

    COMMIT;
EXCEPTION WHEN OTHERS THEN
    ROLLBACK;
    DBMS_OUTPUT.PUT_LINE('Lỗi TRG_CANHBAO_HETHAN: ' || SQLERRM);
    COMMIT;
END;
/

-- TRIGGER 3: Cảnh báo HẾT/SẮP HẾT HÀNG (dựa trên SLConLai)
CREATE OR REPLACE TRIGGER TRG_CANHBAO_NHAP_HANG
AFTER INSERT OR UPDATE OF SLConLai ON TONKHO
FOR EACH ROW
DECLARE
    PRAGMA AUTONOMOUS_TRANSACTION; 
    v_MinTonKho NUMBER;
    v_TongTon NUMBER;
    v_MaSP VARCHAR2(10);
    v_TenSP NVARCHAR2(100);
    v_CheckExists NUMBER;
    v_LoaiTB NVARCHAR2(50);
    v_NoiDung NVARCHAR2(500);
BEGIN
    -- 1. Lấy ngưỡng tối thiểu
    BEGIN
        SELECT GiaTri INTO v_MinTonKho FROM THAMSO WHERE TenTS = 'MIN_TONKHO';
    EXCEPTION WHEN NO_DATA_FOUND THEN v_MinTonKho := 10;
    END;

    -- 2. Tìm thông tin sản phẩm
    BEGIN
        v_MaSP := :NEW.MaSP;
        SELECT TenSP INTO v_TenSP FROM SANPHAM WHERE MaSP = v_MaSP;
    EXCEPTION WHEN NO_DATA_FOUND THEN
        COMMIT;
        RETURN;
    END;

    -- 3. Tính tổng tồn hiện tại (TÍNH TỪ COMMITTED DATA, bỏ qua lô hết hạn/hủy bỏ)
    SELECT NVL(SUM(SLConLai), 0) INTO v_TongTon 
    FROM TONKHO TK
    WHERE TK.MaSP = v_MaSP 
      AND TK.TrangThai NOT IN (N'Hết hạn', N'Hủy bỏ');

    -- 4. Điều chỉnh v_TongTon với thay đổi hiện tại
    IF UPDATING THEN
        v_TongTon := v_TongTon - :OLD.SLConLai + :NEW.SLConLai;
    ELSE -- INSERT
        v_TongTon := v_TongTon + :NEW.SLConLai;
    END IF;

    -- 5. Phân loại và chèn thông báo
    IF v_TongTon <= 0 THEN
        v_LoaiTB := N'Hết hàng';
        v_NoiDung := N'CẢNH BÁO: Sản phẩm [' || v_TenSP || N'] đã HẾT HÀNG hoàn toàn. Yêu cầu nhập hàng mới ngay!';
    ELSIF v_TongTon < v_MinTonKho THEN
        v_LoaiTB := N'Sắp hết hàng';
        v_NoiDung := N'Thông báo: Sản phẩm [' || v_TenSP || N'] sắp hết hàng. Hiện chỉ còn ' || ROUND(v_TongTon, 2) || N' đơn vị. Yêu cầu nhập thêm hàng!';
    ELSE
        COMMIT;
        RETURN;
    END IF;

    -- 6. Chống trùng: Chỉ chèn nếu chưa có thông báo CHƯA ĐỌC tương tự
    SELECT COUNT(*) INTO v_CheckExists FROM THONGBAO 
    WHERE LoaiTB = v_LoaiTB 
      AND INSTR(NoiDung, v_TenSP) > 0
      AND TrangThaiTB = 0;

    IF v_CheckExists = 0 THEN
        INSERT INTO THONGBAO (MaTB, LoaiTB, NoiDung, TrangThaiTB, TGTao, NguoiNhan)
        VALUES ('TB' || LPAD(SEQ_THONGBAO.NEXTVAL, 8, '0'), v_LoaiTB, v_NoiDung, 0, SYSDATE, N'Nhân viên kho/thu mua');
    END IF;

    COMMIT;
EXCEPTION WHEN OTHERS THEN
    ROLLBACK;
    DBMS_OUTPUT.PUT_LINE('Lỗi TRG_CANHBAO_NHAP_HANG: ' || SQLERRM);
    COMMIT;
END;
/

-- TRIGGER 4: Xử lý khi xóa lô hàng
CREATE OR REPLACE TRIGGER TRG_CAPNHAT_KHI_XOA_LO
AFTER DELETE ON TONKHO
FOR EACH ROW
BEGIN
    UPDATE CHITIETLOHANG 
    SET SoLuong = SoLuong - :OLD.SLConLai
    WHERE MaLH = :OLD.MaLH AND MaSP = :OLD.MaSP;
    
    -- Xóa luôn các thông báo liên quan đến lô hàng vừa xóa
    DELETE FROM THONGBAO 
    WHERE NoiDung LIKE N'%Lô ' || :OLD.MaTonKho || N'%';
END;
/

CREATE OR REPLACE TRIGGER TRG_GIOHANG_THANHTIEN
BEFORE INSERT OR UPDATE ON GIOHANG
FOR EACH ROW
DECLARE
    v_GiaBan NUMBER(15,2);
BEGIN
    SELECT GiaBan INTO v_GiaBan FROM SANPHAM WHERE MaSP = :NEW.MaSP;
    :NEW.ThanhTien := :NEW.SoLuong * v_GiaBan;
END;
/

-- TRIGGER 5: Thông báo Đơn hàng & Giao hàng
CREATE OR REPLACE TRIGGER TRG_DONHANG_THONGBAO
AFTER INSERT OR UPDATE OF TrangThaiDH ON DONHANG
FOR EACH ROW
BEGIN
    IF INSERTING THEN
        INSERT INTO THONGBAO (MaTB, LoaiTB, NoiDung, TrangThaiTB, TGTao, NguoiNhan)
        VALUES ('TB' || LPAD(SEQ_THONGBAO.NEXTVAL, 8, '0'), N'Đơn hàng', N'Đơn hàng ' || :NEW.MaDH || N' đã được đặt thành công.', 0, SYSDATE, :NEW.MaKH);
    ELSIF UPDATING THEN
        IF :NEW.TrangThaiDH = N'Chờ giao hàng' AND (:OLD.TrangThaiDH IS NULL OR :OLD.TrangThaiDH <> N'Chờ giao hàng') THEN
            INSERT INTO THONGBAO (MaTB, LoaiTB, NoiDung, TrangThaiTB, TGTao, NguoiNhan)
            VALUES ('TB' || LPAD(SEQ_THONGBAO.NEXTVAL, 8, '0'), N'Giao hàng', N'Đơn hàng ' || :NEW.MaDH || N' đã được xuất kho, sẵn sàng để giao hàng.', 0, SYSDATE, N'Nhân viên giao hàng');
        ELSIF :NEW.TrangThaiDH = N'Hoàn thành' AND (:OLD.TrangThaiDH IS NULL OR :OLD.TrangThaiDH <> N'Hoàn thành') THEN
            INSERT INTO THONGBAO (MaTB, LoaiTB, NoiDung, TrangThaiTB, TGTao, NguoiNhan)
            VALUES ('TB' || LPAD(SEQ_THONGBAO.NEXTVAL, 8, '0'), N'Đơn hàng', N'Đơn hàng ' || :NEW.MaDH || N' đã được giao thành công.', 0, SYSDATE, :NEW.MaKH);
        ELSIF :NEW.TrangThaiDH = N'Chờ thanh toán' AND (:OLD.TrangThaiDH IS NULL OR :OLD.TrangThaiDH <> N'Chờ thanh toán') THEN
            INSERT INTO THONGBAO (MaTB, LoaiTB, NoiDung, TrangThaiTB, TGTao, NguoiNhan)
            VALUES ('TB' || LPAD(SEQ_THONGBAO.NEXTVAL, 8, '0'), N'Đơn hàng', N'Đơn hàng ' || :NEW.MaDH || N' đã được giao thành công và đang chờ thanh toán ghi nợ.', 0, SYSDATE, :NEW.MaKH);
        ELSIF :NEW.TrangThaiDH = N'Đã huỷ' AND (:OLD.TrangThaiDH IS NULL OR :OLD.TrangThaiDH <> N'Đã huỷ') THEN
            INSERT INTO THONGBAO (MaTB, LoaiTB, NoiDung, TrangThaiTB, TGTao, NguoiNhan)
            VALUES ('TB' || LPAD(SEQ_THONGBAO.NEXTVAL, 8, '0'), N'Đơn hàng', N'Đơn hàng ' || :NEW.MaDH || N' giao thất bại. Lý do: ' || NVL(:NEW.LyDoHuy, N'Chưa rõ'), 0, SYSDATE, :NEW.MaKH);
        END IF;
    END IF;
END;
/
