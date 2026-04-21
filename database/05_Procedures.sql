-- ====================================================================================
--                              PHẦN 5: TẠO PROCEDURE
-- ====================================================================================


-- Cập nhật giá một sản phẩm
CREATE OR REPLACE PROCEDURE SP_CAPNHAT_GIA (
    p_MaSP IN VARCHAR2,
    p_GiaMuaMoi IN NUMBER,
    p_GiaBanMoi IN NUMBER
)
IS
    v_MaGia VARCHAR2(10);
BEGIN
    -- 1. Cập nhật giá trong bảng SANPHAM
    UPDATE SANPHAM
    SET GiaMua = p_GiaMuaMoi,
        GiaBan = p_GiaBanMoi
    WHERE MaSP = p_MaSP;

    -- 2. Ghi nhận vào LICHSUGIA
    INSERT INTO LICHSUGIA (MaSP, GiaMua, GiaBan)
    VALUES (p_MaSP, p_GiaMuaMoi, p_GiaBanMoi);

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20010, 'Lỗi cập nhật giá sản phẩm: ' || SQLERRM);
END;
/

-- ================================= Bảng THAMSO =================================
-- Cập nhật tham số
CREATE OR REPLACE PROCEDURE SP_CAPNHAT_THAMSO (
    p_MaTS IN VARCHAR2,
    p_GiaTri IN NUMBER,
    p_MoTa IN NVARCHAR2
) IS
BEGIN
    UPDATE THAMSO 
    SET GiaTri = NVL(p_GiaTri, GiaTri),
        MoTa = NVL(p_MoTa, MoTa)
    WHERE MaTS = p_MaTS;
    COMMIT;
END;
/

-- ================================= Bảng THONGBAO =================================
-- Đánh dấu đã đọc thông báo
CREATE OR REPLACE PROCEDURE SP_DOC_THONGBAO (p_MaTB IN VARCHAR2) IS
BEGIN
    UPDATE THONGBAO SET TrangThaiTB = 1 WHERE MaTB = p_MaTB;
    COMMIT;
END;
/

-- ================================= Bảng TAIKHOAN =================================
-- Thêm tài khoản
CREATE OR REPLACE PROCEDURE SP_THEM_TAIKHOAN (
    p_Username IN NVARCHAR2,
    p_Password IN VARCHAR2,
    p_LoaiTK IN NUMBER
) IS
BEGIN
    -- TrangThaiTK mặc định là 1 (Hoạt động), TGTao mặc định SYSDATE
    INSERT INTO TAIKHOAN (Username, Password, LoaiTK, TrangThaiTK) 
    VALUES (p_Username, p_Password, p_LoaiTK, 1);
    COMMIT;
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

-- ================================= Bảng KHACHHANG =================================
CREATE OR REPLACE PROCEDURE SP_THEM_KH (
    p_Username IN NVARCHAR2, p_TenKH IN NVARCHAR2, 
    p_LoaiKH IN NVARCHAR2, p_DiaChi IN NVARCHAR2, 
    p_SDT IN VARCHAR2, p_Email IN NVARCHAR2
) IS
BEGIN
    INSERT INTO KHACHHANG (Username, TenKH, LoaiKH, DiaChi, SDT, Email)
    VALUES (p_Username, p_TenKH, p_LoaiKH, p_DiaChi, p_SDT, p_Email);
    COMMIT;
END;
/

CREATE OR REPLACE PROCEDURE SP_CAPNHAT_KH (
    p_MaKH IN VARCHAR2, p_TenKH IN NVARCHAR2, p_LoaiKH IN NVARCHAR2, 
    p_DiaChi IN NVARCHAR2, p_SDT IN VARCHAR2, p_Email IN NVARCHAR2
) IS
BEGIN
    UPDATE KHACHHANG
    SET TenKH = NVL(p_TenKH, TenKH), LoaiKH = NVL(p_LoaiKH, LoaiKH),
        DiaChi = NVL(p_DiaChi, DiaChi), SDT = NVL(p_SDT, SDT), Email = NVL(p_Email, Email)
    WHERE MaKH = p_MaKH;
    COMMIT;
END;
/

-- ================================= Bảng NHANVIEN =================================
CREATE OR REPLACE PROCEDURE SP_THEM_NV (
    p_Username IN NVARCHAR2, p_TenNV IN NVARCHAR2, 
    p_ChucVu IN NVARCHAR2, p_SDT IN VARCHAR2, p_Luong IN NUMBER
) IS
BEGIN
    INSERT INTO NHANVIEN (Username, TenNV, ChucVu, SDT, Luong)
    VALUES (p_Username, p_TenNV, p_ChucVu, p_SDT, p_Luong);
    COMMIT;
END;
/

CREATE OR REPLACE PROCEDURE SP_CAPNHAT_NV (
    p_MaNV IN VARCHAR2, p_TenNV IN NVARCHAR2, p_ChucVu IN NVARCHAR2, 
    p_SDT IN VARCHAR2, p_Luong IN NUMBER
) IS
BEGIN
    UPDATE NHANVIEN
    SET TenNV = NVL(p_TenNV, TenNV), ChucVu = NVL(p_ChucVu, ChucVu),
        SDT = NVL(p_SDT, SDT), Luong = NVL(p_Luong, Luong)
    WHERE MaNV = p_MaNV;
    COMMIT;
END;
/

-- ================================= Bảng NHACUNGCAP =================================
CREATE OR REPLACE PROCEDURE SP_THEM_NCC (
    p_TenNCC IN NVARCHAR2, p_DiaChi IN NVARCHAR2, 
    p_SDT IN VARCHAR2, p_Email IN VARCHAR2, p_ChungNhanCL IN VARCHAR2
) IS
BEGIN
    INSERT INTO NHACUNGCAP (TenNCC, DiaChi, SDT, Email, ChungNhanCL, TrangThaiHopTac)
    VALUES (p_TenNCC, p_DiaChi, p_SDT, p_Email, p_ChungNhanCL, 1);
    COMMIT;
END;
/

CREATE OR REPLACE PROCEDURE SP_CAPNHAT_NCC (
    p_MaNCC IN VARCHAR2, p_TenNCC IN NVARCHAR2, p_DiaChi IN NVARCHAR2, 
    p_SDT IN VARCHAR2, p_Email IN VARCHAR2, p_ChungNhanCL IN VARCHAR2
) IS
BEGIN
    UPDATE NHACUNGCAP
    SET TenNCC = NVL(p_TenNCC, TenNCC), DiaChi = NVL(p_DiaChi, DiaChi),
        SDT = NVL(p_SDT, SDT), Email = NVL(p_Email, Email), 
        ChungNhanCL = NVL(p_ChungNhanCL, ChungNhanCL)
    WHERE MaNCC = p_MaNCC;
    COMMIT;
END;
/

CREATE OR REPLACE PROCEDURE SP_NGUNG_HOPTAC (p_MaNCC IN VARCHAR2) IS
BEGIN
    UPDATE NHACUNGCAP SET TrangThaiHopTac = 0 WHERE MaNCC = p_MaNCC;
    COMMIT;
END;
/

-- ================================= Bảng LOAISANPHAM =================================
CREATE OR REPLACE PROCEDURE SP_THEM_LSP (p_TenLSP IN NVARCHAR2, p_MoTa IN NVARCHAR2) IS
BEGIN
    INSERT INTO LOAISANPHAM (TenLSP, MoTa) VALUES (p_TenLSP, p_MoTa);
    COMMIT;
END;
/

CREATE OR REPLACE PROCEDURE SP_CAPNHAT_LSP (p_MaLSP IN VARCHAR2, p_TenLSP IN NVARCHAR2, p_MoTa IN NVARCHAR2) IS
BEGIN
    UPDATE LOAISANPHAM SET TenLSP = NVL(p_TenLSP, TenLSP), MoTa = NVL(p_MoTa, MoTa) WHERE MaLSP = p_MaLSP;
    COMMIT;
END;
/

-- ================================= Bảng SANPHAM =================================
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
    -- Việc lưu lịch sử giá đã có TRG_SP_LUU_LSG
    UPDATE SANPHAM
    SET TenSP = NVL(p_TenSP, TenSP), MaLSP = NVL(p_MaLSP, MaLSP), ChatLuong = NVL(p_ChatLuong, ChatLuong),
        GiaMua = NVL(p_GiaMua, GiaMua), GiaBan = NVL(p_GiaBan, GiaBan), 
        DonViTinh = NVL(p_DonViTinh, DonViTinh), BaoQuan = NVL(p_BaoQuan, BaoQuan)
    WHERE MaSP = p_MaSP;
    COMMIT;
END;
/

-- ================================= Bảng KHO =================================
CREATE OR REPLACE PROCEDURE SP_THEM_KHO (p_TenKho IN NVARCHAR2, p_LoaiKho IN NVARCHAR2, p_DiaChi IN NVARCHAR2, p_MoTa IN NVARCHAR2) IS
BEGIN
    INSERT INTO KHO (TenKho, LoaiKho, DiaChi, MoTa) VALUES (p_TenKho, p_LoaiKho, p_DiaChi, p_MoTa);
    COMMIT;
END;
/

CREATE OR REPLACE PROCEDURE SP_CAPNHAT_KHO (p_MaKho IN VARCHAR2, p_TenKho IN NVARCHAR2, p_LoaiKho IN NVARCHAR2, p_DiaChi IN NVARCHAR2, p_MoTa IN NVARCHAR2) IS
BEGIN
    UPDATE KHO 
    SET TenKho = NVL(p_TenKho, TenKho), LoaiKho = NVL(p_LoaiKho, LoaiKho), 
        DiaChi = NVL(p_DiaChi, DiaChi), MoTa = NVL(p_MoTa, MoTa) 
    WHERE MaKho = p_MaKho;
    COMMIT;
END;
/

-- ================================= Bảng LOHANG =================================
CREATE OR REPLACE PROCEDURE SP_THEM_LH (p_MaNCC IN VARCHAR2, p_MaNV IN VARCHAR2) IS
BEGIN
    -- TongTien được TRG_CTLH_TONGTIEN cập nhật, TrangThaiLH mặc định 'Chờ kiểm duyệt'
    INSERT INTO LOHANG (MaNCC, MaNV) VALUES (p_MaNCC, p_MaNV);
    COMMIT;
END;
/

CREATE OR REPLACE PROCEDURE SP_CAPNHAT_LH (p_MaLH IN VARCHAR2, p_MaNV IN VARCHAR2, p_TrangThaiLH IN NVARCHAR2) IS
BEGIN
    -- Chỉ cho phép cập nhật nhân viên phụ trách hoặc Trạng thái
    UPDATE LOHANG SET MaNV = NVL(p_MaNV, MaNV), TrangThaiLH = NVL(p_TrangThaiLH, TrangThaiLH) WHERE MaLH = p_MaLH;
    COMMIT;
END;
/

CREATE OR REPLACE PROCEDURE SP_XOA_LH (p_MaLH IN VARCHAR2) IS
    v_TrangThai NVARCHAR2(50);
BEGIN
    SELECT TrangThaiLH INTO v_TrangThai FROM LOHANG WHERE MaLH = p_MaLH;
    IF v_TrangThai = 'Đã nhập kho' THEN
        RAISE_APPLICATION_ERROR(-20020, 'Không thể xoá lô hàng đã nhập kho!');
    END IF;
    -- Cần xoá chi tiết trước (toàn vẹn dữ liệu)
    DELETE FROM CHITIETLOHANG WHERE MaLH = p_MaLH;
    DELETE FROM LOHANG WHERE MaLH = p_MaLH;
    COMMIT;
END;
/

-- ================================= Bảng CHITIETLOHANG =================================
CREATE OR REPLACE PROCEDURE SP_THEM_CTLH (p_MaLH IN VARCHAR2, p_MaSP IN VARCHAR2, p_SoLuong IN NUMBER) IS
BEGIN
    -- GiaMua và ThanhTien sẽ do TRG_CTLH_THANHTIEN tự điền
    INSERT INTO CHITIETLOHANG (MaLH, MaSP, SoLuong) VALUES (p_MaLH, p_MaSP, p_SoLuong);
    COMMIT;
END;
/

CREATE OR REPLACE PROCEDURE SP_CAPNHAT_CTLH (p_MaCTLH IN VARCHAR2, p_SoLuong IN NUMBER) IS
BEGIN
    -- Chỉ cho phép cập nhật số lượng (Trigger sẽ tự tính lại thành tiền và tổng tiền)
    UPDATE CHITIETLOHANG SET SoLuong = NVL(p_SoLuong, SoLuong) WHERE MaCTLH = p_MaCTLH;
    COMMIT;
END;
/

CREATE OR REPLACE PROCEDURE SP_XOA_CTLH (p_MaCTLH IN VARCHAR2) IS
BEGIN
    DELETE FROM CHITIETLOHANG WHERE MaCTLH = p_MaCTLH;
    COMMIT;
END;
/

-- ================================= Bảng DONHANG =================================
CREATE OR REPLACE PROCEDURE SP_THEM_DH (
    p_MaKH IN VARCHAR2, p_MaNV IN VARCHAR2, p_DiaChiGiaoHang IN NVARCHAR2, p_PhiVanChuyen IN NUMBER
) IS
BEGIN
    -- Khởi tạo TongTien ban đầu bằng với phí vận chuyển. 
    -- PhiVanChuyen = DON_GIA_VANCHUYEN * Khoảng cách
    -- Phí vận chuyển được tính ở phần code java và truyền xuống
    INSERT INTO DONHANG (MaKH, MaNV, DiaChiGiaoHang, PhiVanChuyen, TongTien)
    VALUES (p_MaKH, p_MaNV, p_DiaChiGiaoHang, p_PhiVanChuyen, p_PhiVanChuyen);
    COMMIT;
END;
/
CREATE OR REPLACE PROCEDURE SP_CAPNHAT_DH (
    p_MaDH IN VARCHAR2, p_DiaChiGiaoHang IN NVARCHAR2, 
    p_TGGiaoDK IN DATE, p_TrangThaiDH IN NVARCHAR2
) IS
BEGIN
    UPDATE DONHANG 
    SET DiaChiGiaoHang = NVL(p_DiaChiGiaoHang, DiaChiGiaoHang), 
        TGGiaoDK = NVL(p_TGGiaoDK, TGGiaoDK), 
        TrangThaiDH = NVL(p_TrangThaiDH, TrangThaiDH) 
    WHERE MaDH = p_MaDH;
    COMMIT;
END;
/

CREATE OR REPLACE PROCEDURE SP_XOA_DH (p_MaDH IN VARCHAR2) IS
    v_TrangThai NVARCHAR2(50);
BEGIN
    SELECT TrangThaiDH INTO v_TrangThai FROM DONHANG WHERE MaDH = p_MaDH;
    IF v_TrangThai NOT IN ('Đã đặt', 'Chờ thanh toán') THEN
        RAISE_APPLICATION_ERROR(-20021, 'Chỉ có thể xoá đơn hàng mới đặt hoặc chờ thanh toán!');
    END IF;
    DELETE FROM CHITIETDONHANG WHERE MaDH = p_MaDH;
    DELETE FROM DONHANG WHERE MaDH = p_MaDH;
    COMMIT;
END;
/

-- ================================= Bảng CHITIETDONHANG =================================
CREATE OR REPLACE PROCEDURE SP_THEM_CTDH (p_MaDH IN VARCHAR2, p_MaSP IN VARCHAR2, p_SoLuong IN NUMBER) IS
BEGIN
    -- GiaBan và ThanhTien sẽ do TRG_CTDH_THANHTIEN tự điền
    INSERT INTO CHITIETDONHANG (MaDH, MaSP, SoLuong) VALUES (p_MaDH, p_MaSP, p_SoLuong);
    COMMIT;
END;
/

CREATE OR REPLACE PROCEDURE SP_CAPNHAT_CTDH (p_MaCTDH IN VARCHAR2, p_SoLuong IN NUMBER) IS
BEGIN
    UPDATE CHITIETDONHANG SET SoLuong = NVL(p_SoLuong, SoLuong) WHERE MaCTDH = p_MaCTDH;
    COMMIT;
END;
/

CREATE OR REPLACE PROCEDURE SP_XOA_CTDH (p_MaCTDH IN VARCHAR2) IS
BEGIN
    DELETE FROM CHITIETDONHANG WHERE MaCTDH = p_MaCTDH;
    COMMIT;
END;
/

-- ================================= NHẬP KHO =================================
-- NHẬP KHO: Chuyển toàn bộ Chi tiết lô hàng vào Tồn kho
CREATE OR REPLACE PROCEDURE SP_YEUCAU_NHAPKHO (
    p_MaLH IN VARCHAR2
) IS
    v_TrangThaiLH NVARCHAR2(50);
BEGIN
    SELECT TrangThaiLH INTO v_TrangThaiLH FROM LOHANG WHERE MaLH = p_MaLH;
    
    IF v_TrangThaiLH IN ('Chờ nhập kho', 'Đã nhập kho') THEN
        RAISE_APPLICATION_ERROR(-20022, 'Lô hàng này đã được yêu cầu hoặc đã hoàn tất nhập kho!');
    END IF;

    -- Đổi trạng thái để báo hiệu cho bộ phận Kho
    UPDATE LOHANG SET TrangThaiLH = 'Chờ nhập kho' WHERE MaLH = p_MaLH;
    
    COMMIT;
EXCEPTION 
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
/

-- Nhân viên kho xác nhập sau khi xếp hàng vào kho
CREATE OR REPLACE PROCEDURE SP_XACNHAN_VITRI_CTLH (
p_MaCTLH IN VARCHAR2,
    p_MaKho IN VARCHAR2,       -- Truyền từ ComboBox Kho trên giao diện
    p_TGHetHan IN DATE,        -- Truyền từ DatePicker trên giao diện
    p_ViTri IN NVARCHAR2       -- Truyền từ ô Text Vị trí trên giao diện
) IS
    v_MaLH VARCHAR2(10);
    v_MaSP VARCHAR2(10);
    v_SoLuong NUMBER(10,2);
    v_BaoQuan NVARCHAR2(100);
    v_LoaiKho NVARCHAR2(100);
    v_TonTai NUMBER;
    v_ChuaXepXong NUMBER;
    v_TrangThaiLH NVARCHAR2(50);
BEGIN
    -- 1. Lấy thông tin Chi tiết lô hàng và Yêu cầu bảo quản của sản phẩm
    SELECT CTLH.MaLH, CTLH.MaSP, CTLH.SoLuong, SP.BaoQuan 
    INTO v_MaLH, v_MaSP, v_SoLuong, v_BaoQuan
    FROM CHITIETLOHANG CTLH
    JOIN SANPHAM SP ON CTLH.MaSP = SP.MaSP
    WHERE CTLH.MaCTLH = p_MaCTLH;

    -- Kiểm tra trạng thái Lô hàng (Chỉ xử lý khi đang chờ nhập kho)
    SELECT TrangThaiLH INTO v_TrangThaiLH FROM LOHANG WHERE MaLH = v_MaLH;
    IF v_TrangThaiLH != 'Chờ nhập kho' THEN
        RAISE_APPLICATION_ERROR(-20026, 'Lô hàng không ở trạng thái Chờ nhập kho!');
    END IF;

    -- 2. ĐỐI CHIẾU AN TOÀN: Kiểm tra Kho nhân viên chọn có khớp chuẩn bảo quản không
    SELECT LoaiKho INTO v_LoaiKho FROM KHO WHERE MaKho = p_MaKho;
    IF v_LoaiKho != v_BaoQuan THEN
        RAISE_APPLICATION_ERROR(-20028, 'Bảo quản sai quy cách! Sản phẩm ' || v_MaSP || ' yêu cầu kho [' || v_BaoQuan || '] nhưng bạn lại chọn kho [' || v_LoaiKho || '].');
    END IF;

    -- 3. Kiểm tra chống nhập đúp (1 mã chi tiết chỉ được cất 1 lần)
    SELECT COUNT(*) INTO v_TonTai FROM TONKHO WHERE MaCTLH = p_MaCTLH;
    IF v_TonTai > 0 THEN
        RAISE_APPLICATION_ERROR(-20027, 'Sản phẩm của chi tiết lô hàng này đã được xác nhận cất vào kho rồi!');
    END IF;

    -- 4. INSERT dữ liệu chính thức vào bảng Tồn Kho
    INSERT INTO TONKHO (MaKho, MaCTLH, SLConLai, SLKhaDung, TGNhapKho, TGHetHan, ViTri)
    VALUES (p_MaKho, p_MaCTLH, v_SoLuong, v_SoLuong, SYSDATE, p_TGHetHan, p_ViTri);

    -- 5. TỰ ĐỘNG ĐÓNG LÔ HÀNG: Đếm xem Lô hàng này còn sản phẩm nào chưa cất không?
    SELECT COUNT(*) INTO v_ChuaXepXong
    FROM CHITIETLOHANG C
    WHERE C.MaLH = v_MaLH 
      AND C.MaCTLH NOT IN (SELECT MaCTLH FROM TONKHO);

    IF v_ChuaXepXong = 0 THEN
        -- Nếu tất cả đã cất xong -> Cập nhật Lô hàng hoàn tất
        UPDATE LOHANG SET TrangThaiLH = 'Đã nhập kho' WHERE MaLH = v_MaLH;
    END IF;

    -- Không COMMIT và EXCEPTION ROLLBACK ở đây.
    -- Vì NẾU COMMIT dữ liệu sẽ được lưu ngay vào DB và có thể dẫn đến bị trùng
    -- Vì hệ thống sẽ lưu nhiều dòng cùng một lúc (Batch), nếu xảy ra lỗi ở dòng số 3, 
    -- hệ thống phải huỷ (rollback) toàn bộ các dòng 1 và 2 đã chạy trước đó để tránh tình trạng dữ liệu lưu nửa vời.
END;
/

-- ================================= XUẤT KHO =================================
CREATE OR REPLACE PROCEDURE SP_YEUCAU_XUATKHO (p_MaDH IN VARCHAR2) IS
    v_SoLuongCan NUMBER;
    v_SoLuongXuat NUMBER;
    v_TrangThaiDH NVARCHAR2(50);
BEGIN
    SELECT TrangThaiDH INTO v_TrangThaiDH FROM DONHANG WHERE MaDH = p_MaDH;
    
    -- Kiểm tra đơn hàng có đủ điều kiện để phân bổ hàng không
    IF v_TrangThaiDH IN ('Đang giao', 'Hoàn thành', 'Đã huỷ') THEN
        RAISE_APPLICATION_ERROR(-20023, 'Trạng thái đơn hàng không hợp lệ!');
    END IF;

    -- Duyệt từng sản phẩm trong chi tiết đơn hàng
    FOR rec_CTDH IN (SELECT MaCTDH, MaSP, SoLuong FROM CHITIETDONHANG WHERE MaDH = p_MaDH) LOOP
        v_SoLuongCan := rec_CTDH.SoLuong;

        -- Tìm hàng theo FEFO (Hết hạn trước xuất trước) và FIFO (Nhập trước xuất trước)
        FOR rec_TK IN (
            SELECT TK.MaTonKho, TK.SLKhaDung 
            FROM TONKHO TK
            JOIN CHITIETLOHANG CTLH ON TK.MaCTLH = CTLH.MaCTLH
            WHERE CTLH.MaSP = rec_CTDH.MaSP 
              AND TK.SLKhaDung > 0 
              AND TK.TGHetHan >= TRUNC(SYSDATE)
            ORDER BY TK.TGHetHan ASC, TK.TGNhapKho ASC
            FOR UPDATE -- Thêm dòng này để lock record
        ) LOOP
            EXIT WHEN v_SoLuongCan = 0;

            IF rec_TK.SLKhaDung >= v_SoLuongCan THEN
                v_SoLuongXuat := v_SoLuongCan;
                v_SoLuongCan := 0;
            ELSE
                v_SoLuongXuat := rec_TK.SLKhaDung;
                v_SoLuongCan := v_SoLuongCan - rec_TK.SLKhaDung;
            END IF;

            -- Insert với MaNV để NULL và trạng thái 'Tạm giữ'
            -- Trigger sẽ tự động trừ SLKhaDung trong bảng TONKHO
            INSERT INTO XUATKHO (MaCTDH, MaTonKho, MaNV, SLXuat, TGCapNhat, TrangThaiXK) 
            VALUES (rec_CTDH.MaCTDH, rec_TK.MaTonKho, NULL, v_SoLuongXuat, SYSDATE, 'Tạm giữ');
            
        END LOOP;

        IF v_SoLuongCan > 0 THEN
            RAISE_APPLICATION_ERROR(-20024, 'Kho không đủ hàng cho sản phẩm: ' || rec_CTDH.MaSP);
        END IF;
    END LOOP;

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
/

-- Nhân viên xác nhận xuất kho để lấy hàng giao cho khách
CREATE OR REPLACE PROCEDURE SP_XACNHAN_XUATKHO (
    p_MaDH IN VARCHAR2, 
    p_MaNV IN VARCHAR2
) IS
BEGIN
    -- 1. Cập nhật trạng thái xuất kho thành 'Đã xuất' và lưu MaNV
    -- Trigger sẽ tự động trừ SLConLai (hàng thực tế rời kho)
    UPDATE XUATKHO 
    SET TrangThaiXK = 'Đã xuất', 
        MaNV = p_MaNV, 
        TGCapNhat = SYSDATE
    WHERE MaCTDH IN (SELECT MaCTDH FROM CHITIETDONHANG WHERE MaDH = p_MaDH)
      AND TrangThaiXK = 'Tạm giữ';

    -- 2. Cập nhật trạng thái đơn hàng sang 'Chờ giao hàng'
    UPDATE DONHANG 
    SET TrangThaiDH = 'Chờ giao hàng'
    WHERE MaDH = p_MaDH;

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
/

-- Nhân viên giao hàng bấm xác nhận giao hàng
CREATE OR REPLACE PROCEDURE SP_XACNHAN_GIAOHANG (
    p_MaDH IN VARCHAR2, 
    p_MaNV_GiaoHang IN VARCHAR2
) IS
BEGIN
    UPDATE DONHANG 
    SET MaNV = p_MaNV_GiaoHang
    WHERE MaDH = p_MaDH;

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
/

-- Nhân viên giao hàng bấm xác nhận giao hàng THÀNH CÔNG
CREATE OR REPLACE PROCEDURE SP_GIAOHANG_THANHCONG (
    p_MaDH IN VARCHAR2, 
    p_MaNV_GiaoHang IN VARCHAR2
) IS
BEGIN
    UPDATE DONHANG 
    SET TrangThaiDH = 'Hoàn thành'
    WHERE MaDH = p_MaDH;

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
/