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
CREATE OR REPLACE PROCEDURE SP_THEM_DH (p_MaKH IN VARCHAR2, p_MaNV IN VARCHAR2, p_DiaChiGiaoHang IN NVARCHAR2) IS
BEGIN
    INSERT INTO DONHANG (MaKH, MaNV, DiaChiGiaoHang) VALUES (p_MaKH, p_MaNV, p_DiaChiGiaoHang);
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

-- ================================= XUAT & NHAP KHO =================================
-- NHẬP KHO: Chuyển toàn bộ Chi tiết lô hàng vào Tồn kho
CREATE OR REPLACE PROCEDURE SP_NHAPKHO (
    p_MaLH IN VARCHAR2,
    p_MaKho IN VARCHAR2,
    p_SoNgayHSD IN NUMBER -- Hạn sử dụng sau khi nhập kho là bao nhiêu ngày
) IS
    v_TrangThaiLH NVARCHAR2(50);
BEGIN
    SELECT TrangThaiLH INTO v_TrangThaiLH FROM LOHANG WHERE MaLH = p_MaLH;
    IF v_TrangThaiLH = 'Đã nhập kho' THEN
        RAISE_APPLICATION_ERROR(-20022, 'Lô hàng này đã được xử lý nhập kho!');
    END IF;

    -- Đọc từng chi tiết và đẩy vào bảng TONKHO
    FOR rec IN (SELECT MaCTLH, SoLuong FROM CHITIETLOHANG WHERE MaLH = p_MaLH) LOOP
        INSERT INTO TONKHO (MaKho, MaCTLH, SLConLai, TGHetHan, ViTri)
        VALUES (p_MaKho, rec.MaCTLH, rec.SoLuong, TRUNC(SYSDATE) + p_SoNgayHSD, 'Khu vực chờ');
    END LOOP;

    UPDATE LOHANG SET TrangThaiLH = 'Đã nhập kho' WHERE MaLH = p_MaLH;
    COMMIT;
EXCEPTION 
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
/

-- XUẤT KHO THÔNG MINH (FEFO)
CREATE OR REPLACE PROCEDURE SP_XUATKHO (p_MaDH IN VARCHAR2) IS
    v_SoLuongCan NUMBER;
    v_SoLuongXuat NUMBER;
    v_TrangThaiDH NVARCHAR2(50);
BEGIN
    SELECT TrangThaiDH INTO v_TrangThaiDH FROM DONHANG WHERE MaDH = p_MaDH;
    IF v_TrangThaiDH IN ('Đang giao', 'Hoàn thành', 'Đã huỷ') THEN
        RAISE_APPLICATION_ERROR(-20023, 'Đơn hàng này không hợp lệ để xuất kho!');
    END IF;

    -- Duyệt từng sản phẩm trong đơn hàng
    FOR rec_CTDH IN (SELECT MaCTDH, MaSP, SoLuong FROM CHITIETDONHANG WHERE MaDH = p_MaDH) LOOP
        v_SoLuongCan := rec_CTDH.SoLuong;

        -- Tìm kho theo Hạn sử dụng (FEFO: TGHetHan ASC)
        FOR rec_TK IN (
            SELECT TK.MaTonKho, TK.SLConLai 
            FROM TONKHO TK
            JOIN CHITIETLOHANG CTLH ON TK.MaCTLH = CTLH.MaCTLH
            WHERE CTLH.MaSP = rec_CTDH.MaSP AND TK.SLConLai > 0 AND TK.TGHetHan >= TRUNC(SYSDATE)
            ORDER BY TK.TGHetHan ASC
        ) LOOP
            EXIT WHEN v_SoLuongCan = 0;

            IF rec_TK.SLConLai >= v_SoLuongCan THEN
                v_SoLuongXuat := v_SoLuongCan;
                v_SoLuongCan := 0;
            ELSE
                v_SoLuongXuat := rec_TK.SLConLai;
                v_SoLuongCan := v_SoLuongCan - rec_TK.SLConLai;
            END IF;

            -- Việc TRỪ TỒN KHO đã được lo bởi TRG_XK_TRU_TONKHO
            INSERT INTO XUATKHO (MaCTDH, MaTonKho, SLXuat) 
            VALUES (rec_CTDH.MaCTDH, rec_TK.MaTonKho, v_SoLuongXuat);
        END LOOP;

        IF v_SoLuongCan > 0 THEN
            RAISE_APPLICATION_ERROR(-20024, 'Kho không đủ hàng (hoặc đã hết hạn) cho SP: ' || rec_CTDH.MaSP);
        END IF;
    END LOOP;

    -- Đổi trạng thái đơn hàng
    UPDATE DONHANG SET TrangThaiDH = 'Đang giao' WHERE MaDH = p_MaDH;
    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
/