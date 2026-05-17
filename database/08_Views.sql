-- Doanh thu theo thời gian (Ngày/Tháng/Năm)
CREATE OR REPLACE VIEW V_DOANH_THU_THOI_GIAN AS
SELECT 
    TRUNC(TGDat) AS Ngay_Giao_Dich,
    EXTRACT(DAY FROM TGDat) AS Ngay,
    EXTRACT(MONTH FROM TGDat) AS Thang,
    EXTRACT(YEAR FROM TGDat) AS Nam,
    COUNT(MaDH) AS So_Luong_Don,
    SUM(TongTienHang) AS Tong_Tien_Hang,
    SUM(GiamGia) AS Tong_Giam_Gia,
    SUM(TongTien) AS Doanh_Thu_Thuc_Te
FROM DONHANG
WHERE TrangThaiDH = 'Hoàn thành'
GROUP BY TRUNC(TGDat), EXTRACT(DAY FROM TGDat), EXTRACT(MONTH FROM TGDat), EXTRACT(YEAR FROM TGDat);

-- Top Khách hàng
CREATE OR REPLACE VIEW V_TOP_KHACH_HANG AS
SELECT 
    KH.MaKH,
    KH.TenKH,
    KH.LoaiKH,
    COUNT(DISTINCT DH.MaDH) AS So_Don_Hang,
    SUM(CT.SoLuong) AS Tong_So_Luong_Mua,
    SUM(CT.ThanhTien) AS Tong_Chi_Tieu
FROM KHACHHANG KH
JOIN DONHANG DH 
    ON KH.MaKH = DH.MaKH
JOIN CHITIETDONHANG CT 
    ON DH.MaDH = CT.MaDH
WHERE DH.TrangThaiDH = 'Hoàn thành'
GROUP BY KH.MaKH, KH.TenKH, KH.LoaiKH;

-- Top Sản phẩm
CREATE OR REPLACE VIEW V_TOP_SAN_PHAM AS
SELECT 
    SP.MaSP,
    SP.TenSP,
    LSP.TenLSP AS Loai_San_Pham,
    SUM(CT.SoLuong) AS Tong_So_Luong_Ban,
    SUM(CT.ThanhTien) AS Tong_Doanh_Thu_SP
FROM SANPHAM SP
JOIN LOAISANPHAM LSP 
    ON SP.MaLSP = LSP.MaLSP
JOIN CHITIETDONHANG CT 
    ON SP.MaSP = CT.MaSP
JOIN DONHANG DH 
    ON CT.MaDH = DH.MaDH
WHERE DH.TrangThaiDH = 'Hoàn thành'
GROUP BY SP.MaSP, SP.TenSP, LSP.TenLSP;

-- Lịch sử giá sản phẩm theo thời gian
CREATE OR REPLACE VIEW V_LICHSU_GIA_SANPHAM AS
SELECT
    L.MaGia,
    L.MaSP,
    SP.TenSP,
    TRUNC(L.TGApDung) AS TGApDung,
    L.GiaMua,
    L.GiaBan,
    CASE WHEN L.GiaMua <> 0 THEN ROUND((L.GiaBan - L.GiaMua) / L.GiaMua * 100, 2) ELSE 0 END AS LoiNhuan
FROM LICHSUGIA L
JOIN SANPHAM SP ON L.MaSP = SP.MaSP;

-- Giá trị tồn kho theo từng kho
CREATE OR REPLACE VIEW V_GIA_TRI_TON_KHO AS
SELECT 
    K.MaKho,
    K.TenKho,
    K.LoaiKho,
    SP.MaSP,
    SP.TenSP,
    SUM(TK.SLConLai) AS Tong_Ton_Kho,
    SUM(TK.SLKhaDung) AS Ton_Kha_Dung,
    SUM(TK.SLConLai * CTLH.GiaMua) AS Tong_Gia_Tri_Ton
FROM KHO K
JOIN TONKHO TK ON K.MaKho = TK.MaKho
JOIN CHITIETLOHANG CTLH ON TK.MaCTLH = CTLH.MaCTLH
JOIN SANPHAM SP ON CTLH.MaSP = SP.MaSP
WHERE TK.SLConLai > 0
GROUP BY K.MaKho, K.TenKho, K.LoaiKho, SP.MaSP, SP.TenSP;

-- Cảnh báo hàng sắp hết hạn
CREATE OR REPLACE VIEW V_CANH_BAO_HET_HAN AS
SELECT 
    TK.MaTonKho,
    K.TenKho,
    SP.TenSP,
    TK.SLConLai,
    TK.TGNhapKho,
    TK.TGHetHan,
    TRUNC(TK.TGHetHan) - TRUNC(SYSDATE) AS So_Ngay_Con_Lai
FROM TONKHO TK
JOIN KHO K ON TK.MaKho = K.MaKho
JOIN CHITIETLOHANG CTLH ON TK.MaCTLH = CTLH.MaCTLH
JOIN SANPHAM SP ON CTLH.MaSP = SP.MaSP
WHERE TK.SLConLai > 0 AND TK.TGHetHan IS NOT NULL;

-- Tổng chi phí nhập hàng theo Nhà Cung Cấp
CREATE OR REPLACE VIEW V_CHI_PHI_NHAP_HANG AS
SELECT 
    NCC.MaNCC,
    NCC.TenNCC,
    NCC.TrangThaiHopTac,
    COUNT(LH.MaLH) AS So_Lan_Nhap,
    SUM(LH.TongTien) AS Tong_Tien_Da_Nhap
FROM NHACUNGCAP NCC
JOIN LOHANG LH ON NCC.MaNCC = LH.MaNCC
WHERE LH.TrangThaiLH = 'Đã nhập kho'
GROUP BY NCC.MaNCC, NCC.TenNCC, NCC.TrangThaiHopTac;

-- Tỷ lệ huỷ đơn và giao trễ
CREATE OR REPLACE VIEW V_VAN_HANH_DON_HANG AS
SELECT 
    MaDH,
    MaKH,
    TGDat,
    TGGiaoYC,
    TGGiaoTT,
    TrangThaiDH,
    -- Cờ kiểm tra đơn bị huỷ (1 là Huỷ, 0 là Không)
    CASE WHEN TrangThaiDH = 'Đã huỷ' THEN 1 ELSE 0 END AS Don_Bi_Huy,
    -- Cờ kiểm tra giao trễ (Thời gian thực tế lớn hơn Yêu cầu)
    CASE 
        WHEN TGGiaoTT IS NOT NULL AND TGGiaoTT > TGGiaoYC THEN 1 
        ELSE 0 
    END AS Don_Giao_Tre,
    -- Số ngày giao trễ (nếu có)
    CASE 
        WHEN TGGiaoTT IS NOT NULL AND TGGiaoTT > TGGiaoYC 
        THEN TRUNC(TGGiaoTT) - TRUNC(TGGiaoYC) 
        ELSE 0 
    END AS So_Ngay_Tre
FROM DONHANG;

-- Tai chính theo tháng (Doanh thu - Chi phí)
CREATE OR REPLACE VIEW V_TAI_CHINH_THEO_THANG AS
WITH 
LUONG_NV AS (
    -- Lấy tổng lương của tất cả nhân viên làm chi phí vận hành cố định mỗi tháng
    SELECT NVL(SUM(Luong), 0) AS Tong_Luong 
    FROM NHANVIEN
),
DT AS (
    -- Tổng hợp Doanh thu từ các đơn hàng đã Hoàn thành theo từng Tháng/Năm
    SELECT 
        EXTRACT(YEAR FROM TGDat) AS Nam, 
        EXTRACT(MONTH FROM TGDat) AS Thang, 
        SUM(TongTien) AS Doanh_Thu
    FROM DONHANG 
    WHERE TrangThaiDH = 'Hoàn thành'
    GROUP BY EXTRACT(YEAR FROM TGDat), EXTRACT(MONTH FROM TGDat)
),
CP_N AS (
    -- Tổng hợp Chi phí nhập hàng từ các lô hàng Đã nhập kho
    SELECT 
        EXTRACT(YEAR FROM TGNhap) AS Nam, 
        EXTRACT(MONTH FROM TGNhap) AS Thang, 
        SUM(TongTien) AS Chi_Phi_Nhap
    FROM LOHANG 
    WHERE TrangThaiLH = 'Đã nhập kho'
    GROUP BY EXTRACT(YEAR FROM TGNhap), EXTRACT(MONTH FROM TGNhap)
)
SELECT 
    NVL(DT.Nam, CP_N.Nam) AS Nam,
    NVL(DT.Thang, CP_N.Thang) AS Thang,
    (NVL(DT.Nam, CP_N.Nam) * 100 + NVL(DT.Thang, CP_N.Thang)) AS Nam_Thang_Sort,
    'Tháng ' || TO_CHAR(NVL(DT.Thang, CP_N.Thang), 'FM00') || '/' || TO_CHAR(NVL(DT.Nam, CP_N.Nam)) AS Thang_Nam_Label,
    NVL(DT.Doanh_Thu, 0) AS Doanh_Thu,
    L.Tong_Luong AS Chi_Phi_Van_Hanh,
    NVL(CP_N.Chi_Phi_Nhap, 0) AS Chi_Phi_Nhap,
    (L.Tong_Luong + NVL(CP_N.Chi_Phi_Nhap, 0)) AS Chi_Phi,
    (NVL(DT.Doanh_Thu, 0) - (L.Tong_Luong + NVL(CP_N.Chi_Phi_Nhap, 0))) AS Loi_Nhuan
FROM DT
FULL OUTER JOIN CP_N ON DT.Nam = CP_N.Nam AND DT.Thang = CP_N.Thang
CROSS JOIN LUONG_NV L
ORDER BY Nam DESC, Thang DESC;

-- Chạy đoạn script này trong Oracle Database của bạn
CREATE OR REPLACE VIEW V_THONGKE_NHANVIEN_CHITIET AS
-- 1. Thu mua (Lô hàng)
SELECT MaNV, TRUNC(TGNhap, 'MM') AS ThangThongKe, COUNT(MaLH) AS SoLuong, SUM(TongTien) AS TongGiaTri
FROM LOHANG 
GROUP BY MaNV, TRUNC(TGNhap, 'MM')
UNION ALL
-- 2. Giao hàng (Đơn hàng)
SELECT MaNV, TRUNC(TGDat, 'MM') AS ThangThongKe, COUNT(MaDH) AS SoLuong, SUM(TongTien) AS TongGiaTri
FROM DONHANG 
GROUP BY MaNV, TRUNC(TGDat, 'MM')
UNION ALL
-- 3. Kho (Xuất kho) - Chỉ đếm số lượng xuất, tổng tiền = 0
SELECT MaNV, TRUNC(TGCapNhat, 'MM') AS ThangThongKe, SUM(SLXuat) AS SoLuong, 0 AS TongGiaTri
FROM XUATKHO 
GROUP BY MaNV, TRUNC(TGCapNhat, 'MM');