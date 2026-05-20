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

-- Chạy đoạn script này trong Oracle Database của bạn
CREATE OR REPLACE VIEW V_THONGKE_NHANVIEN_CHITIET AS
-- 1. Thu mua (Lô hàng)
SELECT MaNV, TRUNC(TGNhap, 'MM') AS ThangThongKe, COUNT(MaLH) AS SoLuong, SUM(TongTien) AS TongGiaTri
FROM LOHANG 
GROUP BY MaNV, TRUNC(TGNhap, 'MM')
UNION ALL
-- 2. Giao hàng (Đơn hàng) - Chỉ tính đơn hàng đã giao thành công (Hoàn thành)
SELECT MaNV, TRUNC(TGDat, 'MM') AS ThangThongKe, COUNT(MaDH) AS SoLuong, SUM(TongTien) AS TongGiaTri
FROM DONHANG 
WHERE TrangThaiDH = 'Hoàn thành'
GROUP BY MaNV, TRUNC(TGDat, 'MM')
UNION ALL
-- 3. Kho (Xuất kho) - Chỉ tính số lần xuất kho thành công, tổng tiền = 0
SELECT MaNV, TRUNC(TGCapNhat, 'MM') AS ThangThongKe, COUNT(MaXK) AS SoLuong, 0 AS TongGiaTri
FROM XUATKHO 
WHERE TrangThaiXK = 'Đã xuất'
GROUP BY MaNV, TRUNC(TGCapNhat, 'MM');

CREATE OR REPLACE VIEW V_THONGKE_SP AS
SELECT 
    ct.MaSP,
    sp.TenSP,
    dh.TGDat AS NgayGD,
    ct.SoLuong AS SoLuongBan,
    (ct.SoLuong * ct.GiaBan) AS DoanhThu,
    (ct.SoLuong * NVL(
        (SELECT GiaMua FROM (
            SELECT GiaMua, MaSP, TGApDung FROM LICHSUGIA ORDER BY TGApDung DESC
        ) lg WHERE lg.MaSP = sp.MaSP AND ROWNUM = 1), 
    ct.GiaBan * 0.7)) AS ChiPhi
FROM CHITIETDONHANG ct
JOIN DONHANG dh ON ct.MaDH = dh.MaDH
JOIN SANPHAM sp ON ct.MaSP = sp.MaSP
WHERE dh.TrangThaiDH NOT IN ('Đã hủy', 'Trả hàng');