-- ====================================================================================
--                           PHẦN 7: Thêm dữ liệu cho demo
-- ====================================================================================
-- Vô hiệu hoá khoá ngoại -> Xoá dữ liệu cũ -> Kích hoạt lại khoá ngoại -> Thêm dữ liệu mới
BEGIN
    -- Disable tất cả FK
    FOR c IN (
        SELECT table_name, constraint_name
        FROM user_constraints
        WHERE constraint_type = 'R'
    ) LOOP
        EXECUTE IMMEDIATE 'ALTER TABLE ' || c.table_name ||
                          ' DISABLE CONSTRAINT ' || c.constraint_name;
    END LOOP;

    -- Xoá dữ liệu tất cả bảng
    FOR t IN (
        SELECT table_name FROM user_tables
    ) LOOP
        EXECUTE IMMEDIATE 'DELETE FROM ' || t.table_name;
    END LOOP;

    -- Enable lại FK
    FOR c IN (
        SELECT table_name, constraint_name
        FROM user_constraints
        WHERE constraint_type = 'R'
    ) LOOP
        EXECUTE IMMEDIATE 'ALTER TABLE ' || c.table_name ||
                          ' ENABLE CONSTRAINT ' || c.constraint_name;
    END LOOP;
END;
/
-- -------------------------------------------------------------------------
-- 1. KHO (3 Kho theo đúng 3 loại quy định)
-- -------------------------------------------------------------------------
INSERT ALL
    INTO KHO(MaKho, TenKho, LoaiKho, DiaChi, MoTa) VALUES ('KHO00001', N'Kho Mát Bình Dương', N'Mát', N'Dĩ An, Bình Dương', N'Kho nhiệt độ 15-20 độ C')
    INTO KHO(MaKho, TenKho, LoaiKho, DiaChi, MoTa) VALUES ('KHO00002', N'Kho Lạnh Thủ Đức', N'Lạnh', N'Thủ Đức, TP.HCM', N'Kho nhiệt độ 0-5 độ C')
    INTO KHO(MaKho, TenKho, LoaiKho, DiaChi, MoTa) VALUES ('KHO00003', N'Kho Đông Quận 7', N'Đông', N'Quận 7, TP.HCM', N'Kho nhiệt độ âm 18 độ C')
SELECT * FROM dual;

-- -------------------------------------------------------------------------
-- 2. LOAISANPHAM (5 Loại nông/thủy sản)
-- -------------------------------------------------------------------------
INSERT ALL
    INTO LOAISANPHAM(MaLSP, TenLSP, MoTa) VALUES ('LSP00001', N'Thịt các loại', N'Thịt gia súc, gia cầm cần bảo quản đông')
    INTO LOAISANPHAM(MaLSP, TenLSP, MoTa) VALUES ('LSP00002', N'Thủy hải sản', N'Hải sản tươi sống và sơ chế bảo quản lạnh/đông')
    INTO LOAISANPHAM(MaLSP, TenLSP, MoTa) VALUES ('LSP00003', N'Rau củ quả', N'Rau củ tươi thu hoạch trong ngày')
    INTO LOAISANPHAM(MaLSP, TenLSP, MoTa) VALUES ('LSP00004', N'Trái cây', N'Trái cây nội địa và nhập khẩu')
    INTO LOAISANPHAM(MaLSP, TenLSP, MoTa) VALUES ('LSP00005', N'Ngũ cốc và hạt', N'Các loại hạt sấy khô và ngũ cốc')
SELECT * FROM dual;

-- -------------------------------------------------------------------------
-- 3. SANPHAM (Mỗi loại 5 sản phẩm -> 25 SP)
-- -------------------------------------------------------------------------
INSERT ALL
    INTO SANPHAM(MaSP, TenSP, MaLSP, ChatLuong, GiaMua, GiaBan, DonViTinh, BaoQuan) VALUES ('SP000001', N'Thịt heo Iberico', 'LSP00001', N'Loại 1', 150000, 200000, 'Kg', N'Đông')
    INTO SANPHAM(MaSP, TenSP, MaLSP, ChatLuong, GiaMua, GiaBan, DonViTinh, BaoQuan) VALUES ('SP000002', N'Thịt bò Kobe', 'LSP00001', N'Loại 1', 1500000, 2200000, 'Kg', N'Đông')
    INTO SANPHAM(MaSP, TenSP, MaLSP, ChatLuong, GiaMua, GiaBan, DonViTinh, BaoQuan) VALUES ('SP000003', N'Thịt gà ta thả vườn', 'LSP00001', N'Loại 2', 90000, 130000, 'Kg', N'Lạnh')
    INTO SANPHAM(MaSP, TenSP, MaLSP, ChatLuong, GiaMua, GiaBan, DonViTinh, BaoQuan) VALUES ('SP000004', N'Đùi cừu Úc', 'LSP00001', N'Loại 1', 300000, 420000, 'Kg', N'Đông')
    INTO SANPHAM(MaSP, TenSP, MaLSP, ChatLuong, GiaMua, GiaBan, DonViTinh, BaoQuan) VALUES ('SP000005', N'Thịt vịt xiêm', 'LSP00001', N'Loại 2', 80000, 110000, 'Kg', N'Lạnh')
    INTO SANPHAM(MaSP, TenSP, MaLSP, ChatLuong, GiaMua, GiaBan, DonViTinh, BaoQuan) VALUES ('SP000006', N'Cá hồi NaUy nguyên con', 'LSP00002', N'Loại 1', 350000, 500000, 'Kg', N'Lạnh')
    INTO SANPHAM(MaSP, TenSP, MaLSP, ChatLuong, GiaMua, GiaBan, DonViTinh, BaoQuan) VALUES ('SP000007', N'Mực ống Trường Sa', 'LSP00002', N'Loại 1', 250000, 320000, 'Kg', N'Đông')
    INTO SANPHAM(MaSP, TenSP, MaLSP, ChatLuong, GiaMua, GiaBan, DonViTinh, BaoQuan) VALUES ('SP000008', N'Tôm sú Cà Mau', 'LSP00002', N'Loại 2', 200000, 280000, 'Kg', N'Đông')
    INTO SANPHAM(MaSP, TenSP, MaLSP, ChatLuong, GiaMua, GiaBan, DonViTinh, BaoQuan) VALUES ('SP000009', N'Cua thịt Năm Căn', 'LSP00002', N'Loại 1', 400000, 550000, 'Kg', N'Mát')
    INTO SANPHAM(MaSP, TenSP, MaLSP, ChatLuong, GiaMua, GiaBan, DonViTinh, BaoQuan) VALUES ('SP000010', N'Bạch tuộc sữa', 'LSP00002', N'Loại 3', 120000, 160000, 'Kg', N'Đông')
    INTO SANPHAM(MaSP, TenSP, MaLSP, ChatLuong, GiaMua, GiaBan, DonViTinh, BaoQuan) VALUES ('SP000011', N'Cải bắp Đà Lạt', 'LSP00003', N'Loại 1', 15000, 25000, 'Kg', N'Mát')
    INTO SANPHAM(MaSP, TenSP, MaLSP, ChatLuong, GiaMua, GiaBan, DonViTinh, BaoQuan) VALUES ('SP000012', N'Cà chua Cherry', 'LSP00003', N'Loại 1', 40000, 60000, 'Kg', N'Mát')
    INTO SANPHAM(MaSP, TenSP, MaLSP, ChatLuong, GiaMua, GiaBan, DonViTinh, BaoQuan) VALUES ('SP000013', N'Súp lơ xanh', 'LSP00003', N'Loại 2', 20000, 35000, 'Kg', N'Mát')
    INTO SANPHAM(MaSP, TenSP, MaLSP, ChatLuong, GiaMua, GiaBan, DonViTinh, BaoQuan) VALUES ('SP000014', N'Cà rốt Baby', 'LSP00003', N'Loại 1', 30000, 50000, 'Kg', N'Mát')
    INTO SANPHAM(MaSP, TenSP, MaLSP, ChatLuong, GiaMua, GiaBan, DonViTinh, BaoQuan) VALUES ('SP000015', N'Khoai tây mầm', 'LSP00003', N'Loại 3', 10000, 18000, 'Kg', N'Mát')
    INTO SANPHAM(MaSP, TenSP, MaLSP, ChatLuong, GiaMua, GiaBan, DonViTinh, BaoQuan) VALUES ('SP000016', N'Táo Envy Mỹ', 'LSP00004', N'Loại 1', 180000, 250000, 'Kg', N'Mát')
    INTO SANPHAM(MaSP, TenSP, MaLSP, ChatLuong, GiaMua, GiaBan, DonViTinh, BaoQuan) VALUES ('SP000017', N'Nho mẫu đơn Hàn Quốc', 'LSP00004', N'Loại 1', 500000, 750000, 'Chùm', N'Mát')
    INTO SANPHAM(MaSP, TenSP, MaLSP, ChatLuong, GiaMua, GiaBan, DonViTinh, BaoQuan) VALUES ('SP000018', N'Dâu tây Mộc Châu', 'LSP00004', N'Loại 2', 120000, 180000, 'Hộp', N'Mát')
    INTO SANPHAM(MaSP, TenSP, MaLSP, ChatLuong, GiaMua, GiaBan, DonViTinh, BaoQuan) VALUES ('SP000019', N'Dưa lưới Taki', 'LSP00004', N'Loại 1', 60000, 95000, 'Kg', N'Mát')
    INTO SANPHAM(MaSP, TenSP, MaLSP, ChatLuong, GiaMua, GiaBan, DonViTinh, BaoQuan) VALUES ('SP000020', N'Bơ sáp Đắk Lắk', 'LSP00004', N'Loại 2', 35000, 55000, 'Kg', N'Mát')
    INTO SANPHAM(MaSP, TenSP, MaLSP, ChatLuong, GiaMua, GiaBan, DonViTinh, BaoQuan) VALUES ('SP000021', N'Hạt điều rang củi', 'LSP00005', N'Loại 1', 250000, 350000, 'Kg', N'Mát')
    INTO SANPHAM(MaSP, TenSP, MaLSP, ChatLuong, GiaMua, GiaBan, DonViTinh, BaoQuan) VALUES ('SP000022', N'Hạt macca Úc', 'LSP00005', N'Loại 1', 300000, 420000, 'Kg', N'Mát')
    INTO SANPHAM(MaSP, TenSP, MaLSP, ChatLuong, GiaMua, GiaBan, DonViTinh, BaoQuan) VALUES ('SP000023', N'Đậu phộng sấy', 'LSP00005', N'Loại 3', 50000, 80000, 'Kg', N'Mát')
    INTO SANPHAM(MaSP, TenSP, MaLSP, ChatLuong, GiaMua, GiaBan, DonViTinh, BaoQuan) VALUES ('SP000024', N'Hạt dẻ cười Mỹ', 'LSP00005', N'Loại 1', 280000, 400000, 'Kg', N'Mát')
    INTO SANPHAM(MaSP, TenSP, MaLSP, ChatLuong, GiaMua, GiaBan, DonViTinh, BaoQuan) VALUES ('SP000025', N'Gạo ST25', 'LSP00005', N'Loại 1', 30000, 45000, 'Kg', N'Mát')
SELECT * FROM dual;

-- -------------------------------------------------------------------------
-- 4. NHACUNGCAP (20 NCC)
-- -------------------------------------------------------------------------
INSERT ALL
    INTO NHACUNGCAP(MaNCC, TenNCC, DiaChi, SDT, Email, ChungNhanCL, TrangThaiHopTac) VALUES ('NCC00001', N'HTX Rau sạch Đà Lạt', N'Phường 8, Đà Lạt, Lâm Đồng', '0901000001', 'dalat@rau.vn', 'VietGAP', 1)
    INTO NHACUNGCAP(MaNCC, TenNCC, DiaChi, SDT, Email, ChungNhanCL, TrangThaiHopTac) VALUES ('NCC00002', N'Vựa hải sản Vân Đồn', N'Vân Đồn, Quảng Ninh', '0901000002', 'vandon@seafood.vn', 'ISO 9001', 1)
    INTO NHACUNGCAP(MaNCC, TenNCC, DiaChi, SDT, Email, ChungNhanCL, TrangThaiHopTac) VALUES ('NCC00003', N'Công ty CP Chăn nuôi C.P', N'Biên Hòa, Đồng Nai', '0901000003', 'contact@cp.vn', 'GlobalGAP', 1)
    INTO NHACUNGCAP(MaNCC, TenNCC, DiaChi, SDT, Email, ChungNhanCL, TrangThaiHopTac) VALUES ('NCC00004', N'Trang trại bò Úc MeatDeli', N'Củ Chi, TP.HCM', '0901000004', 'info@meatdeli.vn', 'HACCP', 1)
    INTO NHACUNGCAP(MaNCC, TenNCC, DiaChi, SDT, Email, ChungNhanCL, TrangThaiHopTac) VALUES ('NCC00005', N'Vườn cây ăn trái Tiền Giang', N'Cái Bè, Tiền Giang', '0901000005', 'fruit@tiengiang.vn', 'VietGAP', 1)
    INTO NHACUNGCAP(MaNCC, TenNCC, DiaChi, SDT, Email, ChungNhanCL, TrangThaiHopTac) VALUES ('NCC00006', N'Công ty TNHH Macca Đắk Nông', N'Gia Nghĩa, Đắk Nông', '0901000006', 'macca@daknong.vn', 'Organic', 1)
    INTO NHACUNGCAP(MaNCC, TenNCC, DiaChi, SDT, Email, ChungNhanCL, TrangThaiHopTac) VALUES ('NCC00007', N'HTX Gạo Ông Cua', N'Trần Đề, Sóc Trăng', '0901000007', 'st25@ongcua.vn', 'VietGAP', 1)
    INTO NHACUNGCAP(MaNCC, TenNCC, DiaChi, SDT, Email, ChungNhanCL, TrangThaiHopTac) VALUES ('NCC00008', N'Tập đoàn Vissan', N'Bình Thạnh, TP.HCM', '0901000008', 'sales@vissan.vn', 'ISO 22000', 1)
    INTO NHACUNGCAP(MaNCC, TenNCC, DiaChi, SDT, Email, ChungNhanCL, TrangThaiHopTac) VALUES ('NCC00009', N'Hải sản sạch Nha Trang', N'Nha Trang, Khánh Hòa', '0901000009', 'nhatrang@seafood.vn', 'HACCP', 1)
    INTO NHACUNGCAP(MaNCC, TenNCC, DiaChi, SDT, Email, ChungNhanCL, TrangThaiHopTac) VALUES ('NCC00010', N'Nhập khẩu trái cây Klever Fruit', N'Đống Đa, Hà Nội', '0901000010', 'import@klever.vn', 'GlobalGAP', 1)
    INTO NHACUNGCAP(MaNCC, TenNCC, DiaChi, SDT, Email, ChungNhanCL, TrangThaiHopTac) VALUES ('NCC00011', N'Trang trại heo sạch BaF', N'Bến Cát, Bình Dương', '0901000011', 'sale@baf.vn', 'VietGAP', 1)
    INTO NHACUNGCAP(MaNCC, TenNCC, DiaChi, SDT, Email, ChungNhanCL, TrangThaiHopTac) VALUES ('NCC00012', N'Nông sản sấy Vinamit', N'Bến Cát, Bình Dương', '0901000012', 'contact@vinamit.vn', 'ISO 9001', 1)
    INTO NHACUNGCAP(MaNCC, TenNCC, DiaChi, SDT, Email, ChungNhanCL, TrangThaiHopTac) VALUES ('NCC00013', N'HTX Nông nghiệp Cần Thơ', N'Ninh Kiều, Cần Thơ', '0901000013', 'agri@cantho.vn', 'VietGAP', 1)
    INTO NHACUNGCAP(MaNCC, TenNCC, DiaChi, SDT, Email, ChungNhanCL, TrangThaiHopTac) VALUES ('NCC00014', N'Vựa tôm Cà Mau Minh Phú', N'Cà Mau', '0901000014', 'shrimp@minhphu.vn', 'BAP', 1)
    INTO NHACUNGCAP(MaNCC, TenNCC, DiaChi, SDT, Email, ChungNhanCL, TrangThaiHopTac) VALUES ('NCC00015', N'Nông trại Mộc Châu', N'Mộc Châu, Sơn La', '0901000015', 'farm@mocchau.vn', 'GlobalGAP', 1)
    INTO NHACUNGCAP(MaNCC, TenNCC, DiaChi, SDT, Email, ChungNhanCL, TrangThaiHopTac) VALUES ('NCC00016', N'Hạt điều Bình Phước', N'Đồng Xoài, Bình Phước', '0901000016', 'cashew@binhphuoc.vn', 'ISO 22000', 1)
    INTO NHACUNGCAP(MaNCC, TenNCC, DiaChi, SDT, Email, ChungNhanCL, TrangThaiHopTac) VALUES ('NCC00017', N'Công ty rau quả xuất khẩu', N'Hải Phòng', '0901000017', 'export@veg.vn', 'HACCP', 0) -- NCC đang ngừng hợp tác
    INTO NHACUNGCAP(MaNCC, TenNCC, DiaChi, SDT, Email, ChungNhanCL, TrangThaiHopTac) VALUES ('NCC00018', N'Hải sản nhập khẩu Đại Dương', N'Quận 1, TP.HCM', '0901000018', 'ocean@import.vn', 'ISO 9001', 1)
    INTO NHACUNGCAP(MaNCC, TenNCC, DiaChi, SDT, Email, ChungNhanCL, TrangThaiHopTac) VALUES ('NCC00019', N'Trại nấm bào ngư Xanh', N'Hóc Môn, TP.HCM', '0901000019', 'mushroom@xanh.vn', 'VietGAP', 1)
    INTO NHACUNGCAP(MaNCC, TenNCC, DiaChi, SDT, Email, ChungNhanCL, TrangThaiHopTac) VALUES ('NCC00020', N'Nhà vườn sầu riêng Dona', N'Long Khánh, Đồng Nai', '0901000020', 'dona@fruit.vn', 'GlobalGAP', 1)
SELECT * FROM dual;

-- -------------------------------------------------------------------------
-- 5. TAIKHOAN (Tạo 13 TK Nhân viên và 20 TK Khách hàng)
-- Ghi chú: LoaiTK (1: Nhân viên, 2: Khách hàng)
-- -------------------------------------------------------------------------
INSERT ALL
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK) VALUES ('nvtm01', '123456', 1, 1)
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK) VALUES ('nvtm02', '123456', 1, 1)
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK) VALUES ('nvtm03', '123456', 1, 1)
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK) VALUES ('nvtm04', '123456', 1, 1)
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK) VALUES ('nvkho01', '123456', 1, 1)
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK) VALUES ('nvkho02', '123456', 1, 1)
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK) VALUES ('nvkho03', '123456', 1, 1)
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK) VALUES ('nvkho04', '123456', 1, 1)
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK) VALUES ('nvgh01', '123456', 1, 1)
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK) VALUES ('nvgh02', '123456', 1, 1)
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK) VALUES ('nvgh03', '123456', 1, 1)
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK) VALUES ('nvgh04', '123456', 1, 1)
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK) VALUES ('nvgh05', '123456', 1, 1)
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK) VALUES ('kh01', 'passkh', 2, 1)
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK) VALUES ('kh02', 'passkh', 2, 1)
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK) VALUES ('kh03', 'passkh', 2, 1)
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK) VALUES ('kh04', 'passkh', 2, 1)
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK) VALUES ('kh05', 'passkh', 2, 1)
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK) VALUES ('kh06', 'passkh', 2, 1)
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK) VALUES ('kh07', 'passkh', 2, 1)
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK) VALUES ('kh08', 'passkh', 2, 1)
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK) VALUES ('kh09', 'passkh', 2, 1)
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK) VALUES ('kh10', 'passkh', 2, 1)
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK) VALUES ('kh11', 'passkh', 2, 1)
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK) VALUES ('kh12', 'passkh', 2, 1)
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK) VALUES ('kh13', 'passkh', 2, 1)
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK) VALUES ('kh14', 'passkh', 2, 1)
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK) VALUES ('kh15', 'passkh', 2, 1)
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK) VALUES ('kh16', 'passkh', 2, 1)
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK) VALUES ('kh17', 'passkh', 2, 1)
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK) VALUES ('kh18', 'passkh', 2, 1)
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK) VALUES ('kh19', 'passkh', 2, 1)
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK) VALUES ('kh20', 'passkh', 2, 1)
SELECT * FROM dual;

-- -------------------------------------------------------------------------
-- 6. NHANVIEN (13 Nhân viên mới - Nối tiếp 2 quản lý ở file 06)
-- -------------------------------------------------------------------------
INSERT ALL
    INTO NHANVIEN(MaNV, Username, TenNV, ChucVu, SDT, Luong) VALUES ('NV000003', 'nvtm01', N'Trần Hữu Trọng', N'NV thu mua', '0981112221', 12000000)
    INTO NHANVIEN(MaNV, Username, TenNV, ChucVu, SDT, Luong) VALUES ('NV000004', 'nvtm02', N'Phạm Tấn Tài', N'NV thu mua', '0981112222', 12500000)
    INTO NHANVIEN(MaNV, Username, TenNV, ChucVu, SDT, Luong) VALUES ('NV000005', 'nvtm03', N'Lê Tú Anh', N'NV thu mua', '0981112223', 11000000)
    INTO NHANVIEN(MaNV, Username, TenNV, ChucVu, SDT, Luong) VALUES ('NV000006', 'nvtm04', N'Đinh Hoàng Hải', N'NV thu mua', '0981112224', 13000000)
    INTO NHANVIEN(MaNV, Username, TenNV, ChucVu, SDT, Luong) VALUES ('NV000007', 'nvkho01', N'Nguyễn Hữu Quyết', N'NV kho', '0982223331', 10000000)
    INTO NHANVIEN(MaNV, Username, TenNV, ChucVu, SDT, Luong) VALUES ('NV000008', 'nvkho02', N'Lâm Tiến Đạt', N'NV kho', '0982223332', 9500000)
    INTO NHANVIEN(MaNV, Username, TenNV, ChucVu, SDT, Luong) VALUES ('NV000009', 'nvkho03', N'Phan Công Minh', N'NV kho', '0982223333', 10500000)
    INTO NHANVIEN(MaNV, Username, TenNV, ChucVu, SDT, Luong) VALUES ('NV000010', 'nvkho04', N'Bùi Trọng Đạo', N'NV kho', '0982223334', 11000000)
    INTO NHANVIEN(MaNV, Username, TenNV, ChucVu, SDT, Luong) VALUES ('NV000011', 'nvgh01', N'Võ Thanh Hùng', N'NV giao hàng', '0983334441', 8000000)
    INTO NHANVIEN(MaNV, Username, TenNV, ChucVu, SDT, Luong) VALUES ('NV000012', 'nvgh02', N'Đỗ Quốc Cường', N'NV giao hàng', '0983334442', 8500000)
    INTO NHANVIEN(MaNV, Username, TenNV, ChucVu, SDT, Luong) VALUES ('NV000013', 'nvgh03', N'Lê Thanh Sang', N'NV giao hàng', '0983334443', 9000000)
    INTO NHANVIEN(MaNV, Username, TenNV, ChucVu, SDT, Luong) VALUES ('NV000014', 'nvgh04', N'Nguyễn Văn Tuấn', N'NV giao hàng', '0983334444', 8000000)
    INTO NHANVIEN(MaNV, Username, TenNV, ChucVu, SDT, Luong) VALUES ('NV000015', 'nvgh05', N'Trần Đình Bảo', N'NV giao hàng', '0983334445', 8500000)
SELECT * FROM dual;

-- -------------------------------------------------------------------------
-- 7. KHACHHANG (20 Khách hàng)
-- -------------------------------------------------------------------------
INSERT ALL
    INTO KHACHHANG(MaKH, Username, TenKH, LoaiKH, DiaChi, SDT, Email) VALUES ('KH000001', 'kh01', N'Nguyễn Thu Trà', N'VIP', N'Quận 1, TP.HCM', '0912345001', 'tra.nguyen@gmail.com')
    INTO KHACHHANG(MaKH, Username, TenKH, LoaiKH, DiaChi, SDT, Email) VALUES ('KH000002', 'kh02', N'Trần Bích Lập', N'Thường', N'Quận 3, TP.HCM', '0912345002', 'lap.tran@gmail.com')
    INTO KHACHHANG(MaKH, Username, TenKH, LoaiKH, DiaChi, SDT, Email) VALUES ('KH000003', 'kh03', N'Lê Hoàng Nam', N'Thân thiết', N'Thủ Đức, TP.HCM', '0912345003', 'nam.le@yahoo.com')
    INTO KHACHHANG(MaKH, Username, TenKH, LoaiKH, DiaChi, SDT, Email) VALUES ('KH000004', 'kh04', N'Phạm Quang Dũng', N'Thường', N'Bình Thạnh, TP.HCM', '0912345004', 'dung.pham@gmail.com')
    INTO KHACHHANG(MaKH, Username, TenKH, LoaiKH, DiaChi, SDT, Email) VALUES ('KH000005', 'kh05', N'Võ Thị Yến', N'Thường', N'Gò Vấp, TP.HCM', '0912345005', 'yen.vo@hotmail.com')
    INTO KHACHHANG(MaKH, Username, TenKH, LoaiKH, DiaChi, SDT, Email) VALUES ('KH000006', 'kh06', N'Đặng Thái Sơn', N'VIP', N'Tân Bình, TP.HCM', '0912345006', 'son.dang@gmail.com')
    INTO KHACHHANG(MaKH, Username, TenKH, LoaiKH, DiaChi, SDT, Email) VALUES ('KH000007', 'kh07', N'Bùi Thúy Hạnh', N'Thường', N'Quận 7, TP.HCM', '0912345007', 'hanh.bui@gmail.com')
    INTO KHACHHANG(MaKH, Username, TenKH, LoaiKH, DiaChi, SDT, Email) VALUES ('KH000008', 'kh08', N'Đỗ Hữu Nghĩa', N'Thân thiết', N'Quận 10, TP.HCM', '0912345008', 'nghia.do@gmail.com')
    INTO KHACHHANG(MaKH, Username, TenKH, LoaiKH, DiaChi, SDT, Email) VALUES ('KH000009', 'kh09', N'Hồ Thanh Thảo', N'Thường', N'Quận 4, TP.HCM', '0912345009', 'thao.ho@yahoo.com')
    INTO KHACHHANG(MaKH, Username, TenKH, LoaiKH, DiaChi, SDT, Email) VALUES ('KH000010', 'kh10', N'Dương Quốc Vượng', N'Thân thiết', N'Quận 5, TP.HCM', '0912345010', 'vuong.duong@gmail.com')
    INTO KHACHHANG(MaKH, Username, TenKH, LoaiKH, DiaChi, SDT, Email) VALUES ('KH000011', 'kh11', N'Vũ Đức Cảnh', N'Thường', N'Quận 8, TP.HCM', '0912345011', 'canh.vu@gmail.com')
    INTO KHACHHANG(MaKH, Username, TenKH, LoaiKH, DiaChi, SDT, Email) VALUES ('KH000012', 'kh12', N'Ngô Nhật Cường', N'VIP', N'Quận 2, TP.HCM', '0912345012', 'cuong.ngo@gmail.com')
    INTO KHACHHANG(MaKH, Username, TenKH, LoaiKH, DiaChi, SDT, Email) VALUES ('KH000013', 'kh13', N'Đoàn Tuấn Kiệt', N'Thường', N'Bình Tân, TP.HCM', '0912345013', 'kiet.doan@hotmail.com')
    INTO KHACHHANG(MaKH, Username, TenKH, LoaiKH, DiaChi, SDT, Email) VALUES ('KH000014', 'kh14', N'Lý Thảo Ngọc', N'Thân thiết', N'Phú Nhuận, TP.HCM', '0912345014', 'ngoc.ly@gmail.com')
    INTO KHACHHANG(MaKH, Username, TenKH, LoaiKH, DiaChi, SDT, Email) VALUES ('KH000015', 'kh15', N'Nguyễn Bá Thắng', N'Thường', N'Quận 12, TP.HCM', '0912345015', 'thang.nguyen@gmail.com')
    INTO KHACHHANG(MaKH, Username, TenKH, LoaiKH, DiaChi, SDT, Email) VALUES ('KH000016', 'kh16', N'Phan Thị Cẩm', N'Thường', N'Củ Chi, TP.HCM', '0912345016', 'cam.phan@gmail.com')
    INTO KHACHHANG(MaKH, Username, TenKH, LoaiKH, DiaChi, SDT, Email) VALUES ('KH000017', 'kh17', N'Trịnh Xuân Bách', N'Thân thiết', N'Hóc Môn, TP.HCM', '0912345017', 'bach.trinh@yahoo.com')
    INTO KHACHHANG(MaKH, Username, TenKH, LoaiKH, DiaChi, SDT, Email) VALUES ('KH000018', 'kh18', N'Đinh Thị Mười', N'Thường', N'Bình Chánh, TP.HCM', '0912345018', 'muoi.dinh@gmail.com')
    INTO KHACHHANG(MaKH, Username, TenKH, LoaiKH, DiaChi, SDT, Email) VALUES ('KH000019', 'kh19', N'Lâm Ánh Tuyết', N'VIP', N'Quận 11, TP.HCM', '0912345019', 'tuyet.lam@gmail.com')
    INTO KHACHHANG(MaKH, Username, TenKH, LoaiKH, DiaChi, SDT, Email) VALUES ('KH000020', 'kh20', N'Tạ Văn Thông', N'Thường', N'Quận 6, TP.HCM', '0912345020', 'thong.ta@hotmail.com')
SELECT * FROM dual;

-- ====================================================================================
-- 8. LOHANG (40 Lô hàng - Từ tháng 1 đến tháng 3/2026)
-- Trạng thái: 35 Đã nhập kho, 3 Chờ nhập kho, 2 Chờ kiểm duyệt
-- ====================================================================================
INSERT ALL
    INTO LOHANG (MaLH, MaNCC, MaNV, TGNhap, TrangThaiLH) VALUES ('LH000001', 'NCC00001', 'NV000003', TO_DATE('05/01/2026', 'DD/MM/YYYY'), N'Đã nhập kho')
    INTO LOHANG (MaLH, MaNCC, MaNV, TGNhap, TrangThaiLH) VALUES ('LH000002', 'NCC00002', 'NV000004', TO_DATE('10/01/2026', 'DD/MM/YYYY'), N'Đã nhập kho')
    INTO LOHANG (MaLH, MaNCC, MaNV, TGNhap, TrangThaiLH) VALUES ('LH000003', 'NCC00003', 'NV000005', TO_DATE('12/01/2026', 'DD/MM/YYYY'), N'Đã nhập kho')
    INTO LOHANG (MaLH, MaNCC, MaNV, TGNhap, TrangThaiLH) VALUES ('LH000004', 'NCC00004', 'NV000006', TO_DATE('15/01/2026', 'DD/MM/YYYY'), N'Đã nhập kho')
    INTO LOHANG (MaLH, MaNCC, MaNV, TGNhap, TrangThaiLH) VALUES ('LH000005', 'NCC00005', 'NV000003', TO_DATE('20/01/2026', 'DD/MM/YYYY'), N'Đã nhập kho')
    INTO LOHANG (MaLH, MaNCC, MaNV, TGNhap, TrangThaiLH) VALUES ('LH000006', 'NCC00006', 'NV000004', TO_DATE('25/01/2026', 'DD/MM/YYYY'), N'Đã nhập kho')
    INTO LOHANG (MaLH, MaNCC, MaNV, TGNhap, TrangThaiLH) VALUES ('LH000007', 'NCC00007', 'NV000005', TO_DATE('28/01/2026', 'DD/MM/YYYY'), N'Đã nhập kho')
    INTO LOHANG (MaLH, MaNCC, MaNV, TGNhap, TrangThaiLH) VALUES ('LH000008', 'NCC00008', 'NV000006', TO_DATE('02/02/2026', 'DD/MM/YYYY'), N'Đã nhập kho')
    INTO LOHANG (MaLH, MaNCC, MaNV, TGNhap, TrangThaiLH) VALUES ('LH000009', 'NCC00009', 'NV000003', TO_DATE('05/02/2026', 'DD/MM/YYYY'), N'Đã nhập kho')
    INTO LOHANG (MaLH, MaNCC, MaNV, TGNhap, TrangThaiLH) VALUES ('LH000010', 'NCC00010', 'NV000004', TO_DATE('10/02/2026', 'DD/MM/YYYY'), N'Đã nhập kho')
    INTO LOHANG (MaLH, MaNCC, MaNV, TGNhap, TrangThaiLH) VALUES ('LH000011', 'NCC00011', 'NV000005', TO_DATE('14/02/2026', 'DD/MM/YYYY'), N'Đã nhập kho')
    INTO LOHANG (MaLH, MaNCC, MaNV, TGNhap, TrangThaiLH) VALUES ('LH000012', 'NCC00012', 'NV000006', TO_DATE('18/02/2026', 'DD/MM/YYYY'), N'Đã nhập kho')
    INTO LOHANG (MaLH, MaNCC, MaNV, TGNhap, TrangThaiLH) VALUES ('LH000013', 'NCC00013', 'NV000003', TO_DATE('22/02/2026', 'DD/MM/YYYY'), N'Đã nhập kho')
    INTO LOHANG (MaLH, MaNCC, MaNV, TGNhap, TrangThaiLH) VALUES ('LH000014', 'NCC00014', 'NV000004', TO_DATE('25/02/2026', 'DD/MM/YYYY'), N'Đã nhập kho')
    INTO LOHANG (MaLH, MaNCC, MaNV, TGNhap, TrangThaiLH) VALUES ('LH000015', 'NCC00015', 'NV000005', TO_DATE('01/03/2026', 'DD/MM/YYYY'), N'Đã nhập kho')
    INTO LOHANG (MaLH, MaNCC, MaNV, TGNhap, TrangThaiLH) VALUES ('LH000016', 'NCC00016', 'NV000006', TO_DATE('05/03/2026', 'DD/MM/YYYY'), N'Đã nhập kho')
    INTO LOHANG (MaLH, MaNCC, MaNV, TGNhap, TrangThaiLH) VALUES ('LH000017', 'NCC00018', 'NV000003', TO_DATE('08/03/2026', 'DD/MM/YYYY'), N'Đã nhập kho')
    INTO LOHANG (MaLH, MaNCC, MaNV, TGNhap, TrangThaiLH) VALUES ('LH000018', 'NCC00019', 'NV000004', TO_DATE('12/03/2026', 'DD/MM/YYYY'), N'Đã nhập kho')
    INTO LOHANG (MaLH, MaNCC, MaNV, TGNhap, TrangThaiLH) VALUES ('LH000019', 'NCC00020', 'NV000005', TO_DATE('15/03/2026', 'DD/MM/YYYY'), N'Đã nhập kho')
    INTO LOHANG (MaLH, MaNCC, MaNV, TGNhap, TrangThaiLH) VALUES ('LH000020', 'NCC00001', 'NV000006', TO_DATE('20/03/2026', 'DD/MM/YYYY'), N'Đã nhập kho')
    INTO LOHANG (MaLH, MaNCC, MaNV, TGNhap, TrangThaiLH) VALUES ('LH000021', 'NCC00002', 'NV000003', TO_DATE('22/03/2026', 'DD/MM/YYYY'), N'Đã nhập kho')
    INTO LOHANG (MaLH, MaNCC, MaNV, TGNhap, TrangThaiLH) VALUES ('LH000022', 'NCC00003', 'NV000004', TO_DATE('25/03/2026', 'DD/MM/YYYY'), N'Đã nhập kho')
    INTO LOHANG (MaLH, MaNCC, MaNV, TGNhap, TrangThaiLH) VALUES ('LH000023', 'NCC00004', 'NV000005', TO_DATE('26/03/2026', 'DD/MM/YYYY'), N'Đã nhập kho')
    INTO LOHANG (MaLH, MaNCC, MaNV, TGNhap, TrangThaiLH) VALUES ('LH000024', 'NCC00005', 'NV000006', TO_DATE('27/03/2026', 'DD/MM/YYYY'), N'Đã nhập kho')
    INTO LOHANG (MaLH, MaNCC, MaNV, TGNhap, TrangThaiLH) VALUES ('LH000025', 'NCC00006', 'NV000003', TO_DATE('28/03/2026', 'DD/MM/YYYY'), N'Đã nhập kho')
    INTO LOHANG (MaLH, MaNCC, MaNV, TGNhap, TrangThaiLH) VALUES ('LH000026', 'NCC00007', 'NV000004', TO_DATE('29/03/2026', 'DD/MM/YYYY'), N'Đã nhập kho')
    INTO LOHANG (MaLH, MaNCC, MaNV, TGNhap, TrangThaiLH) VALUES ('LH000027', 'NCC00008', 'NV000005', TO_DATE('30/03/2026', 'DD/MM/YYYY'), N'Đã nhập kho')
    INTO LOHANG (MaLH, MaNCC, MaNV, TGNhap, TrangThaiLH) VALUES ('LH000028', 'NCC00009', 'NV000006', TO_DATE('01/04/2026', 'DD/MM/YYYY'), N'Đã nhập kho')
    INTO LOHANG (MaLH, MaNCC, MaNV, TGNhap, TrangThaiLH) VALUES ('LH000029', 'NCC00010', 'NV000003', TO_DATE('03/04/2026', 'DD/MM/YYYY'), N'Đã nhập kho')
    INTO LOHANG (MaLH, MaNCC, MaNV, TGNhap, TrangThaiLH) VALUES ('LH000030', 'NCC00011', 'NV000004', TO_DATE('05/04/2026', 'DD/MM/YYYY'), N'Đã nhập kho')
    INTO LOHANG (MaLH, MaNCC, MaNV, TGNhap, TrangThaiLH) VALUES ('LH000031', 'NCC00012', 'NV000005', TO_DATE('08/04/2026', 'DD/MM/YYYY'), N'Đã nhập kho')
    INTO LOHANG (MaLH, MaNCC, MaNV, TGNhap, TrangThaiLH) VALUES ('LH000032', 'NCC00013', 'NV000006', TO_DATE('10/04/2026', 'DD/MM/YYYY'), N'Đã nhập kho')
    INTO LOHANG (MaLH, MaNCC, MaNV, TGNhap, TrangThaiLH) VALUES ('LH000033', 'NCC00014', 'NV000003', TO_DATE('12/04/2026', 'DD/MM/YYYY'), N'Đã nhập kho')
    INTO LOHANG (MaLH, MaNCC, MaNV, TGNhap, TrangThaiLH) VALUES ('LH000034', 'NCC00015', 'NV000004', TO_DATE('15/04/2026', 'DD/MM/YYYY'), N'Đã nhập kho')
    INTO LOHANG (MaLH, MaNCC, MaNV, TGNhap, TrangThaiLH) VALUES ('LH000035', 'NCC00016', 'NV000005', TO_DATE('18/04/2026', 'DD/MM/YYYY'), N'Đã nhập kho')
    INTO LOHANG (MaLH, MaNCC, MaNV, TGNhap, TrangThaiLH) VALUES ('LH000036', 'NCC00018', 'NV000006', TO_DATE('24/04/2026', 'DD/MM/YYYY'), N'Chờ nhập kho')
    INTO LOHANG (MaLH, MaNCC, MaNV, TGNhap, TrangThaiLH) VALUES ('LH000037', 'NCC00019', 'NV000003', TO_DATE('25/04/2026', 'DD/MM/YYYY'), N'Chờ nhập kho')
    INTO LOHANG (MaLH, MaNCC, MaNV, TGNhap, TrangThaiLH) VALUES ('LH000038', 'NCC00020', 'NV000004', TO_DATE('26/04/2026', 'DD/MM/YYYY'), N'Chờ nhập kho')
    INTO LOHANG (MaLH, MaNCC, MaNV, TGNhap, TrangThaiLH) VALUES ('LH000039', 'NCC00001', 'NV000005', TO_DATE('27/04/2026', 'DD/MM/YYYY'), N'Chờ kiểm duyệt')
    INTO LOHANG (MaLH, MaNCC, MaNV, TGNhap, TrangThaiLH) VALUES ('LH000040', 'NCC00002', 'NV000006', TO_DATE('27/04/2026', 'DD/MM/YYYY'), N'Chờ kiểm duyệt')
SELECT 1 FROM dual;

-- ====================================================================================
-- 9. CHITIETLOHANG (GiaMua, ThanhTien, TongTienLH tự tính qua Trigger)
-- ====================================================================================
-- Để tránh lỗi mutating table khi chèn nhiều dòng cùng cập nhật 1 bảng LOHANG, ta dùng từng lệnh INSERT
INSERT INTO CHITIETLOHANG (MaCTLH, MaLH, MaSP, SoLuong) VALUES ('CTLH0001', 'LH000001', 'SP000011', 500); -- Cải bắp (Mát)
INSERT INTO CHITIETLOHANG (MaCTLH, MaLH, MaSP, SoLuong) VALUES ('CTLH0002', 'LH000001', 'SP000012', 300); -- Cà chua (Mát)
INSERT INTO CHITIETLOHANG (MaCTLH, MaLH, MaSP, SoLuong) VALUES ('CTLH0003', 'LH000001', 'SP000013', 200); -- Súp lơ (Mát)
INSERT INTO CHITIETLOHANG (MaCTLH, MaLH, MaSP, SoLuong) VALUES ('CTLH0004', 'LH000001', 'SP000014', 400); -- Cà rốt (Mát)

INSERT INTO CHITIETLOHANG (MaCTLH, MaLH, MaSP, SoLuong) VALUES ('CTLH0005', 'LH000002', 'SP000006', 200); -- Cá hồi (Lạnh)
INSERT INTO CHITIETLOHANG (MaCTLH, MaLH, MaSP, SoLuong) VALUES ('CTLH0006', 'LH000002', 'SP000003', 300); -- Gà ta (Lạnh)
INSERT INTO CHITIETLOHANG (MaCTLH, MaLH, MaSP, SoLuong) VALUES ('CTLH0007', 'LH000003', 'SP000001', 150); -- Heo (Đông)
INSERT INTO CHITIETLOHANG (MaCTLH, MaLH, MaSP, SoLuong) VALUES ('CTLH0008', 'LH000003', 'SP000002', 100); -- Bò (Đông)

-- Chèn ngẫu nhiên cho các lô hàng còn lại (Đảm bảo mỗi SP có ít nhất 1 lần nhập)
INSERT INTO CHITIETLOHANG (MaCTLH, MaLH, MaSP, SoLuong) VALUES ('CTLH0009', 'LH000004', 'SP000016', 250); 
INSERT INTO CHITIETLOHANG (MaCTLH, MaLH, MaSP, SoLuong) VALUES ('CTLH0010', 'LH000004', 'SP000017', 100); 
INSERT INTO CHITIETLOHANG (MaCTLH, MaLH, MaSP, SoLuong) VALUES ('CTLH0011', 'LH000005', 'SP000021', 400); 
INSERT INTO CHITIETLOHANG (MaCTLH, MaLH, MaSP, SoLuong) VALUES ('CTLH0012', 'LH000005', 'SP000022', 200); 
INSERT INTO CHITIETLOHANG (MaCTLH, MaLH, MaSP, SoLuong) VALUES ('CTLH0013', 'LH000006', 'SP000004', 300); 
INSERT INTO CHITIETLOHANG (MaCTLH, MaLH, MaSP, SoLuong) VALUES ('CTLH0014', 'LH000007', 'SP000007', 250); 
INSERT INTO CHITIETLOHANG (MaCTLH, MaLH, MaSP, SoLuong) VALUES ('CTLH0015', 'LH000008', 'SP000008', 350); 
INSERT INTO CHITIETLOHANG (MaCTLH, MaLH, MaSP, SoLuong) VALUES ('CTLH0016', 'LH000009', 'SP000009', 150); 
INSERT INTO CHITIETLOHANG (MaCTLH, MaLH, MaSP, SoLuong) VALUES ('CTLH0017', 'LH000010', 'SP000010', 400); 
INSERT INTO CHITIETLOHANG (MaCTLH, MaLH, MaSP, SoLuong) VALUES ('CTLH0018', 'LH000011', 'SP000018', 200); 
INSERT INTO CHITIETLOHANG (MaCTLH, MaLH, MaSP, SoLuong) VALUES ('CTLH0019', 'LH000012', 'SP000019', 300); 
INSERT INTO CHITIETLOHANG (MaCTLH, MaLH, MaSP, SoLuong) VALUES ('CTLH0020', 'LH000013', 'SP000020', 450); 
INSERT INTO CHITIETLOHANG (MaCTLH, MaLH, MaSP, SoLuong) VALUES ('CTLH0021', 'LH000014', 'SP000023', 500); 
INSERT INTO CHITIETLOHANG (MaCTLH, MaLH, MaSP, SoLuong) VALUES ('CTLH0022', 'LH000015', 'SP000024', 300); 
INSERT INTO CHITIETLOHANG (MaCTLH, MaLH, MaSP, SoLuong) VALUES ('CTLH0023', 'LH000016', 'SP000025', 1000); 
INSERT INTO CHITIETLOHANG (MaCTLH, MaLH, MaSP, SoLuong) VALUES ('CTLH0024', 'LH000017', 'SP000005', 250); 
INSERT INTO CHITIETLOHANG (MaCTLH, MaLH, MaSP, SoLuong) VALUES ('CTLH0025', 'LH000018', 'SP000015', 300); 
INSERT INTO CHITIETLOHANG (MaCTLH, MaLH, MaSP, SoLuong) VALUES ('CTLH0026', 'LH000019', 'SP000011', 400); 
INSERT INTO CHITIETLOHANG (MaCTLH, MaLH, MaSP, SoLuong) VALUES ('CTLH0027', 'LH000020', 'SP000001', 100); 
INSERT INTO CHITIETLOHANG (MaCTLH, MaLH, MaSP, SoLuong) VALUES ('CTLH0028', 'LH000021', 'SP000002', 150); 
INSERT INTO CHITIETLOHANG (MaCTLH, MaLH, MaSP, SoLuong) VALUES ('CTLH0029', 'LH000022', 'SP000006', 300); 
INSERT INTO CHITIETLOHANG (MaCTLH, MaLH, MaSP, SoLuong) VALUES ('CTLH0030', 'LH000023', 'SP000016', 200); 
INSERT INTO CHITIETLOHANG (MaCTLH, MaLH, MaSP, SoLuong) VALUES ('CTLH0031', 'LH000024', 'SP000021', 300); 
INSERT INTO CHITIETLOHANG (MaCTLH, MaLH, MaSP, SoLuong) VALUES ('CTLH0032', 'LH000025', 'SP000014', 500); 
INSERT INTO CHITIETLOHANG (MaCTLH, MaLH, MaSP, SoLuong) VALUES ('CTLH0033', 'LH000026', 'SP000008', 400); 
INSERT INTO CHITIETLOHANG (MaCTLH, MaLH, MaSP, SoLuong) VALUES ('CTLH0034', 'LH000027', 'SP000007', 200); 
INSERT INTO CHITIETLOHANG (MaCTLH, MaLH, MaSP, SoLuong) VALUES ('CTLH0035', 'LH000028', 'SP000025', 800); 
INSERT INTO CHITIETLOHANG (MaCTLH, MaLH, MaSP, SoLuong) VALUES ('CTLH0036', 'LH000029', 'SP000017', 150); 
INSERT INTO CHITIETLOHANG (MaCTLH, MaLH, MaSP, SoLuong) VALUES ('CTLH0037', 'LH000030', 'SP000022', 250); 
INSERT INTO CHITIETLOHANG (MaCTLH, MaLH, MaSP, SoLuong) VALUES ('CTLH0038', 'LH000031', 'SP000004', 150); 
INSERT INTO CHITIETLOHANG (MaCTLH, MaLH, MaSP, SoLuong) VALUES ('CTLH0039', 'LH000032', 'SP000012', 300); 
INSERT INTO CHITIETLOHANG (MaCTLH, MaLH, MaSP, SoLuong) VALUES ('CTLH0040', 'LH000033', 'SP000018', 200); 
INSERT INTO CHITIETLOHANG (MaCTLH, MaLH, MaSP, SoLuong) VALUES ('CTLH0041', 'LH000034', 'SP000003', 400); 
INSERT INTO CHITIETLOHANG (MaCTLH, MaLH, MaSP, SoLuong) VALUES ('CTLH0042', 'LH000035', 'SP000010', 300); 
-- Chi tiết cho các lô hàng đang chờ
INSERT INTO CHITIETLOHANG (MaCTLH, MaLH, MaSP, SoLuong) VALUES ('CTLH0043', 'LH000036', 'SP000019', 200); 
INSERT INTO CHITIETLOHANG (MaCTLH, MaLH, MaSP, SoLuong) VALUES ('CTLH0044', 'LH000037', 'SP000020', 300); 
INSERT INTO CHITIETLOHANG (MaCTLH, MaLH, MaSP, SoLuong) VALUES ('CTLH0045', 'LH000038', 'SP000013', 250); 
INSERT INTO CHITIETLOHANG (MaCTLH, MaLH, MaSP, SoLuong) VALUES ('CTLH0046', 'LH000039', 'SP000023', 500); 
INSERT INTO CHITIETLOHANG (MaCTLH, MaLH, MaSP, SoLuong) VALUES ('CTLH0047', 'LH000040', 'SP000024', 400); 

-- ====================================================================================
-- 10. TONKHO 
-- ====================================================================================
INSERT ALL
    INTO TONKHO (MaTonKho, MaKho, MaCTLH, SLConLai, SLKhaDung, TGNhapKho, TGHetHan, ViTri) VALUES ('TK000001', 'KHO00001', 'CTLH0001', 500, 500, TO_DATE('06/01/2026', 'DD/MM/YYYY'), TO_DATE('06/06/2026', 'DD/MM/YYYY'), N'Kệ A1')
    INTO TONKHO (MaTonKho, MaKho, MaCTLH, SLConLai, SLKhaDung, TGNhapKho, TGHetHan, ViTri) VALUES ('TK000002', 'KHO00001', 'CTLH0002', 300, 300, TO_DATE('06/01/2026', 'DD/MM/YYYY'), TO_DATE('06/06/2026', 'DD/MM/YYYY'), N'Kệ A2')
    INTO TONKHO (MaTonKho, MaKho, MaCTLH, SLConLai, SLKhaDung, TGNhapKho, TGHetHan, ViTri) VALUES ('TK000003', 'KHO00001', 'CTLH0003', 200, 200, TO_DATE('06/01/2026', 'DD/MM/YYYY'), TO_DATE('06/06/2026', 'DD/MM/YYYY'), N'Kệ A3')
    INTO TONKHO (MaTonKho, MaKho, MaCTLH, SLConLai, SLKhaDung, TGNhapKho, TGHetHan, ViTri) VALUES ('TK000004', 'KHO00001', 'CTLH0004', 400, 400, TO_DATE('06/01/2026', 'DD/MM/YYYY'), TO_DATE('06/06/2026', 'DD/MM/YYYY'), N'Kệ A4')
    INTO TONKHO (MaTonKho, MaKho, MaCTLH, SLConLai, SLKhaDung, TGNhapKho, TGHetHan, ViTri) VALUES ('TK000005', 'KHO00002', 'CTLH0005', 200, 200, TO_DATE('11/01/2026', 'DD/MM/YYYY'), TO_DATE('11/07/2026', 'DD/MM/YYYY'), N'Kệ L1')
    INTO TONKHO (MaTonKho, MaKho, MaCTLH, SLConLai, SLKhaDung, TGNhapKho, TGHetHan, ViTri) VALUES ('TK000006', 'KHO00002', 'CTLH0006', 300, 300, TO_DATE('11/01/2026', 'DD/MM/YYYY'), TO_DATE('11/07/2026', 'DD/MM/YYYY'), N'Kệ L2')
    INTO TONKHO (MaTonKho, MaKho, MaCTLH, SLConLai, SLKhaDung, TGNhapKho, TGHetHan, ViTri) VALUES ('TK000007', 'KHO00003', 'CTLH0007', 150, 150, TO_DATE('13/01/2026', 'DD/MM/YYYY'), TO_DATE('13/12/2026', 'DD/MM/YYYY'), N'Kệ D1')
    INTO TONKHO (MaTonKho, MaKho, MaCTLH, SLConLai, SLKhaDung, TGNhapKho, TGHetHan, ViTri) VALUES ('TK000008', 'KHO00003', 'CTLH0008', 100, 100, TO_DATE('13/01/2026', 'DD/MM/YYYY'), TO_DATE('13/12/2026', 'DD/MM/YYYY'), N'Kệ D2')
    INTO TONKHO (MaTonKho, MaKho, MaCTLH, SLConLai, SLKhaDung, TGNhapKho, TGHetHan, ViTri) VALUES ('TK000009', 'KHO00001', 'CTLH0009', 250, 250, TO_DATE('16/01/2026', 'DD/MM/YYYY'), TO_DATE('16/06/2026', 'DD/MM/YYYY'), N'Kệ A5')
    INTO TONKHO (MaTonKho, MaKho, MaCTLH, SLConLai, SLKhaDung, TGNhapKho, TGHetHan, ViTri) VALUES ('TK000010', 'KHO00001', 'CTLH0010', 100, 100, TO_DATE('16/01/2026', 'DD/MM/YYYY'), TO_DATE('16/06/2026', 'DD/MM/YYYY'), N'Kệ A6')
    INTO TONKHO (MaTonKho, MaKho, MaCTLH, SLConLai, SLKhaDung, TGNhapKho, TGHetHan, ViTri) VALUES ('TK000011', 'KHO00001', 'CTLH0011', 400, 400, TO_DATE('21/01/2026', 'DD/MM/YYYY'), TO_DATE('21/08/2026', 'DD/MM/YYYY'), N'Kệ A7')
    INTO TONKHO (MaTonKho, MaKho, MaCTLH, SLConLai, SLKhaDung, TGNhapKho, TGHetHan, ViTri) VALUES ('TK000012', 'KHO00001', 'CTLH0012', 200, 200, TO_DATE('21/01/2026', 'DD/MM/YYYY'), TO_DATE('21/08/2026', 'DD/MM/YYYY'), N'Kệ A8')
    INTO TONKHO (MaTonKho, MaKho, MaCTLH, SLConLai, SLKhaDung, TGNhapKho, TGHetHan, ViTri) VALUES ('TK000013', 'KHO00003', 'CTLH0013', 300, 300, TO_DATE('26/01/2026', 'DD/MM/YYYY'), TO_DATE('26/12/2026', 'DD/MM/YYYY'), N'Kệ D3')
    INTO TONKHO (MaTonKho, MaKho, MaCTLH, SLConLai, SLKhaDung, TGNhapKho, TGHetHan, ViTri) VALUES ('TK000014', 'KHO00003', 'CTLH0014', 250, 250, TO_DATE('29/01/2026', 'DD/MM/YYYY'), TO_DATE('29/12/2026', 'DD/MM/YYYY'), N'Kệ D4')
    INTO TONKHO (MaTonKho, MaKho, MaCTLH, SLConLai, SLKhaDung, TGNhapKho, TGHetHan, ViTri) VALUES ('TK000015', 'KHO00003', 'CTLH0015', 350, 350, TO_DATE('03/02/2026', 'DD/MM/YYYY'), TO_DATE('03/12/2026', 'DD/MM/YYYY'), N'Kệ D5')
    INTO TONKHO (MaTonKho, MaKho, MaCTLH, SLConLai, SLKhaDung, TGNhapKho, TGHetHan, ViTri) VALUES ('TK000016', 'KHO00001', 'CTLH0016', 150, 150, TO_DATE('06/02/2026', 'DD/MM/YYYY'), TO_DATE('06/06/2026', 'DD/MM/YYYY'), N'Kệ A9')
    INTO TONKHO (MaTonKho, MaKho, MaCTLH, SLConLai, SLKhaDung, TGNhapKho, TGHetHan, ViTri) VALUES ('TK000017', 'KHO00003', 'CTLH0017', 400, 400, TO_DATE('11/02/2026', 'DD/MM/YYYY'), TO_DATE('11/12/2026', 'DD/MM/YYYY'), N'Kệ D6')
    INTO TONKHO (MaTonKho, MaKho, MaCTLH, SLConLai, SLKhaDung, TGNhapKho, TGHetHan, ViTri) VALUES ('TK000018', 'KHO00001', 'CTLH0018', 200, 200, TO_DATE('15/02/2026', 'DD/MM/YYYY'), TO_DATE('15/06/2026', 'DD/MM/YYYY'), N'Kệ B1')
    INTO TONKHO (MaTonKho, MaKho, MaCTLH, SLConLai, SLKhaDung, TGNhapKho, TGHetHan, ViTri) VALUES ('TK000019', 'KHO00001', 'CTLH0019', 300, 300, TO_DATE('19/02/2026', 'DD/MM/YYYY'), TO_DATE('19/06/2026', 'DD/MM/YYYY'), N'Kệ B2')
    INTO TONKHO (MaTonKho, MaKho, MaCTLH, SLConLai, SLKhaDung, TGNhapKho, TGHetHan, ViTri) VALUES ('TK000020', 'KHO00001', 'CTLH0020', 450, 450, TO_DATE('23/02/2026', 'DD/MM/YYYY'), TO_DATE('23/06/2026', 'DD/MM/YYYY'), N'Kệ B3')
    INTO TONKHO (MaTonKho, MaKho, MaCTLH, SLConLai, SLKhaDung, TGNhapKho, TGHetHan, ViTri) VALUES ('TK000021', 'KHO00001', 'CTLH0021', 500, 500, TO_DATE('26/02/2026', 'DD/MM/YYYY'), TO_DATE('26/08/2026', 'DD/MM/YYYY'), N'Kệ B4')
    INTO TONKHO (MaTonKho, MaKho, MaCTLH, SLConLai, SLKhaDung, TGNhapKho, TGHetHan, ViTri) VALUES ('TK000022', 'KHO00001', 'CTLH0022', 300, 300, TO_DATE('02/03/2026', 'DD/MM/YYYY'), TO_DATE('02/08/2026', 'DD/MM/YYYY'), N'Kệ B5')
    INTO TONKHO (MaTonKho, MaKho, MaCTLH, SLConLai, SLKhaDung, TGNhapKho, TGHetHan, ViTri) VALUES ('TK000023', 'KHO00001', 'CTLH0023', 1000, 1000, TO_DATE('06/03/2026', 'DD/MM/YYYY'), TO_DATE('06/09/2026', 'DD/MM/YYYY'), N'Kệ B6')
    INTO TONKHO (MaTonKho, MaKho, MaCTLH, SLConLai, SLKhaDung, TGNhapKho, TGHetHan, ViTri) VALUES ('TK000024', 'KHO00002', 'CTLH0024', 250, 250, TO_DATE('09/03/2026', 'DD/MM/YYYY'), TO_DATE('09/07/2026', 'DD/MM/YYYY'), N'Kệ L3')
    INTO TONKHO (MaTonKho, MaKho, MaCTLH, SLConLai, SLKhaDung, TGNhapKho, TGHetHan, ViTri) VALUES ('TK000025', 'KHO00001', 'CTLH0025', 300, 300, TO_DATE('13/03/2026', 'DD/MM/YYYY'), TO_DATE('13/06/2026', 'DD/MM/YYYY'), N'Kệ B7')
    INTO TONKHO (MaTonKho, MaKho, MaCTLH, SLConLai, SLKhaDung, TGNhapKho, TGHetHan, ViTri) VALUES ('TK000026', 'KHO00001', 'CTLH0026', 400, 400, TO_DATE('16/03/2026', 'DD/MM/YYYY'), TO_DATE('16/06/2026', 'DD/MM/YYYY'), N'Kệ B8')
    INTO TONKHO (MaTonKho, MaKho, MaCTLH, SLConLai, SLKhaDung, TGNhapKho, TGHetHan, ViTri) VALUES ('TK000027', 'KHO00003', 'CTLH0027', 100, 100, TO_DATE('21/03/2026', 'DD/MM/YYYY'), TO_DATE('21/12/2026', 'DD/MM/YYYY'), N'Kệ D7')
    INTO TONKHO (MaTonKho, MaKho, MaCTLH, SLConLai, SLKhaDung, TGNhapKho, TGHetHan, ViTri) VALUES ('TK000028', 'KHO00003', 'CTLH0028', 150, 150, TO_DATE('23/03/2026', 'DD/MM/YYYY'), TO_DATE('23/12/2026', 'DD/MM/YYYY'), N'Kệ D8')
    INTO TONKHO (MaTonKho, MaKho, MaCTLH, SLConLai, SLKhaDung, TGNhapKho, TGHetHan, ViTri) VALUES ('TK000029', 'KHO00002', 'CTLH0029', 300, 300, TO_DATE('26/03/2026', 'DD/MM/YYYY'), TO_DATE('26/08/2026', 'DD/MM/YYYY'), N'Kệ L4')
    INTO TONKHO (MaTonKho, MaKho, MaCTLH, SLConLai, SLKhaDung, TGNhapKho, TGHetHan, ViTri) VALUES ('TK000030', 'KHO00001', 'CTLH0030', 200, 200, TO_DATE('27/03/2026', 'DD/MM/YYYY'), TO_DATE('27/06/2026', 'DD/MM/YYYY'), N'Kệ C1')
    INTO TONKHO (MaTonKho, MaKho, MaCTLH, SLConLai, SLKhaDung, TGNhapKho, TGHetHan, ViTri) VALUES ('TK000031', 'KHO00001', 'CTLH0031', 300, 300, TO_DATE('28/03/2026', 'DD/MM/YYYY'), TO_DATE('28/09/2026', 'DD/MM/YYYY'), N'Kệ C2')
    INTO TONKHO (MaTonKho, MaKho, MaCTLH, SLConLai, SLKhaDung, TGNhapKho, TGHetHan, ViTri) VALUES ('TK000032', 'KHO00001', 'CTLH0032', 500, 500, TO_DATE('29/03/2026', 'DD/MM/YYYY'), TO_DATE('29/06/2026', 'DD/MM/YYYY'), N'Kệ C3')
    INTO TONKHO (MaTonKho, MaKho, MaCTLH, SLConLai, SLKhaDung, TGNhapKho, TGHetHan, ViTri) VALUES ('TK000033', 'KHO00003', 'CTLH0033', 400, 400, TO_DATE('30/03/2026', 'DD/MM/YYYY'), TO_DATE('30/12/2026', 'DD/MM/YYYY'), N'Kệ D9')
    INTO TONKHO (MaTonKho, MaKho, MaCTLH, SLConLai, SLKhaDung, TGNhapKho, TGHetHan, ViTri) VALUES ('TK000034', 'KHO00003', 'CTLH0034', 200, 200, TO_DATE('31/03/2026', 'DD/MM/YYYY'), TO_DATE('31/12/2026', 'DD/MM/YYYY'), N'Kệ D10')
    INTO TONKHO (MaTonKho, MaKho, MaCTLH, SLConLai, SLKhaDung, TGNhapKho, TGHetHan, ViTri) VALUES ('TK000035', 'KHO00001', 'CTLH0035', 800, 800, TO_DATE('02/04/2026', 'DD/MM/YYYY'), TO_DATE('02/09/2026', 'DD/MM/YYYY'), N'Kệ C4')
    INTO TONKHO (MaTonKho, MaKho, MaCTLH, SLConLai, SLKhaDung, TGNhapKho, TGHetHan, ViTri) VALUES ('TK000036', 'KHO00001', 'CTLH0036', 150, 150, TO_DATE('04/04/2026', 'DD/MM/YYYY'), TO_DATE('04/07/2026', 'DD/MM/YYYY'), N'Kệ C5')
    INTO TONKHO (MaTonKho, MaKho, MaCTLH, SLConLai, SLKhaDung, TGNhapKho, TGHetHan, ViTri) VALUES ('TK000037', 'KHO00001', 'CTLH0037', 250, 250, TO_DATE('06/04/2026', 'DD/MM/YYYY'), TO_DATE('06/09/2026', 'DD/MM/YYYY'), N'Kệ C6')
    INTO TONKHO (MaTonKho, MaKho, MaCTLH, SLConLai, SLKhaDung, TGNhapKho, TGHetHan, ViTri) VALUES ('TK000038', 'KHO00003', 'CTLH0038', 150, 150, TO_DATE('09/04/2026', 'DD/MM/YYYY'), TO_DATE('09/12/2026', 'DD/MM/YYYY'), N'Kệ D11')
    INTO TONKHO (MaTonKho, MaKho, MaCTLH, SLConLai, SLKhaDung, TGNhapKho, TGHetHan, ViTri) VALUES ('TK000039', 'KHO00001', 'CTLH0039', 300, 300, TO_DATE('11/04/2026', 'DD/MM/YYYY'), TO_DATE('11/07/2026', 'DD/MM/YYYY'), N'Kệ C7')
    INTO TONKHO (MaTonKho, MaKho, MaCTLH, SLConLai, SLKhaDung, TGNhapKho, TGHetHan, ViTri) VALUES ('TK000040', 'KHO00001', 'CTLH0040', 200, 200, TO_DATE('13/04/2026', 'DD/MM/YYYY'), TO_DATE('13/07/2026', 'DD/MM/YYYY'), N'Kệ C8')
    INTO TONKHO (MaTonKho, MaKho, MaCTLH, SLConLai, SLKhaDung, TGNhapKho, TGHetHan, ViTri) VALUES ('TK000041', 'KHO00002', 'CTLH0041', 400, 400, TO_DATE('16/04/2026', 'DD/MM/YYYY'), TO_DATE('16/08/2026', 'DD/MM/YYYY'), N'Kệ L5')
    INTO TONKHO (MaTonKho, MaKho, MaCTLH, SLConLai, SLKhaDung, TGNhapKho, TGHetHan, ViTri) VALUES ('TK000042', 'KHO00003', 'CTLH0042', 300, 300, TO_DATE('19/04/2026', 'DD/MM/YYYY'), TO_DATE('19/12/2026', 'DD/MM/YYYY'), N'Kệ D12')
SELECT 1 FROM dual;

-- ====================================================================================
-- 11. DONHANG 
-- ====================================================================================
INSERT ALL
    INTO DONHANG(MaDH, MaKH, MaNV, TGDat, TGGiaoYC, TGGiaoTT, DiaChiGiaoHang, PhiVanChuyen, TrangThaiDH, TrangThaiTT, PhuongThucTT) VALUES ('DH000001', 'KH000001', 'NV000011', TO_DATE('10/02/2026','DD/MM/YYYY'), TO_DATE('12/02/2026','DD/MM/YYYY'), TO_DATE('12/02/2026','DD/MM/YYYY'), N'Quận 1, TP.HCM', 30000, N'Hoàn thành', 1, 'COD')
    INTO DONHANG(MaDH, MaKH, MaNV, TGDat, TGGiaoYC, TGGiaoTT, DiaChiGiaoHang, PhiVanChuyen, TrangThaiDH, TrangThaiTT, PhuongThucTT) VALUES ('DH000002', 'KH000002', 'NV000012', TO_DATE('15/02/2026','DD/MM/YYYY'), TO_DATE('16/02/2026','DD/MM/YYYY'), TO_DATE('16/02/2026','DD/MM/YYYY'), N'Quận 3, TP.HCM', 40000, N'Hoàn thành', 1, 'Chuyển khoản')
    INTO DONHANG(MaDH, MaKH, MaNV, TGDat, TGGiaoYC, TGGiaoTT, DiaChiGiaoHang, PhiVanChuyen, TrangThaiDH, TrangThaiTT, PhuongThucTT) VALUES ('DH000003', 'KH000003', 'NV000013', TO_DATE('18/02/2026','DD/MM/YYYY'), TO_DATE('20/02/2026','DD/MM/YYYY'), TO_DATE('20/02/2026','DD/MM/YYYY'), N'Thủ Đức, TP.HCM', 50000, N'Hoàn thành', 1, 'Ví điện tử')
    INTO DONHANG(MaDH, MaKH, MaNV, TGDat, TGGiaoYC, TGGiaoTT, DiaChiGiaoHang, PhiVanChuyen, TrangThaiDH, TrangThaiTT, PhuongThucTT) VALUES ('DH000004', 'KH000004', 'NV000014', TO_DATE('20/02/2026','DD/MM/YYYY'), TO_DATE('21/02/2026','DD/MM/YYYY'), TO_DATE('21/02/2026','DD/MM/YYYY'), N'Bình Thạnh, TP.HCM', 35000, N'Hoàn thành', 1, 'COD')
    INTO DONHANG(MaDH, MaKH, MaNV, TGDat, TGGiaoYC, TGGiaoTT, DiaChiGiaoHang, PhiVanChuyen, TrangThaiDH, TrangThaiTT, PhuongThucTT) VALUES ('DH000005', 'KH000005', 'NV000015', TO_DATE('25/02/2026','DD/MM/YYYY'), TO_DATE('27/02/2026','DD/MM/YYYY'), TO_DATE('27/02/2026','DD/MM/YYYY'), N'Gò Vấp, TP.HCM', 45000, N'Hoàn thành', 1, 'Chuyển khoản')
    INTO DONHANG(MaDH, MaKH, MaNV, TGDat, TGGiaoYC, TGGiaoTT, DiaChiGiaoHang, PhiVanChuyen, TrangThaiDH, TrangThaiTT, PhuongThucTT) VALUES ('DH000006', 'KH000006', 'NV000011', TO_DATE('01/03/2026','DD/MM/YYYY'), TO_DATE('02/03/2026','DD/MM/YYYY'), TO_DATE('02/03/2026','DD/MM/YYYY'), N'Tân Bình, TP.HCM', 30000, N'Hoàn thành', 1, 'COD')
    INTO DONHANG(MaDH, MaKH, MaNV, TGDat, TGGiaoYC, TGGiaoTT, DiaChiGiaoHang, PhiVanChuyen, TrangThaiDH, TrangThaiTT, PhuongThucTT) VALUES ('DH000007', 'KH000007', 'NV000012', TO_DATE('05/03/2026','DD/MM/YYYY'), TO_DATE('06/03/2026','DD/MM/YYYY'), TO_DATE('07/03/2026','DD/MM/YYYY'), N'Quận 7, TP.HCM', 60000, N'Hoàn thành', 1, 'Ví điện tử')
    INTO DONHANG(MaDH, MaKH, MaNV, TGDat, TGGiaoYC, TGGiaoTT, DiaChiGiaoHang, PhiVanChuyen, TrangThaiDH, TrangThaiTT, PhuongThucTT) VALUES ('DH000008', 'KH000008', 'NV000013', TO_DATE('08/03/2026','DD/MM/YYYY'), TO_DATE('09/03/2026','DD/MM/YYYY'), TO_DATE('09/03/2026','DD/MM/YYYY'), N'Quận 10, TP.HCM', 25000, N'Hoàn thành', 1, 'COD')
    INTO DONHANG(MaDH, MaKH, MaNV, TGDat, TGGiaoYC, TGGiaoTT, DiaChiGiaoHang, PhiVanChuyen, TrangThaiDH, TrangThaiTT, PhuongThucTT) VALUES ('DH000009', 'KH000009', 'NV000014', TO_DATE('10/03/2026','DD/MM/YYYY'), TO_DATE('11/03/2026','DD/MM/YYYY'), TO_DATE('11/03/2026','DD/MM/YYYY'), N'Quận 4, TP.HCM', 30000, N'Hoàn thành', 1, 'Chuyển khoản')
    INTO DONHANG(MaDH, MaKH, MaNV, TGDat, TGGiaoYC, TGGiaoTT, DiaChiGiaoHang, PhiVanChuyen, TrangThaiDH, TrangThaiTT, PhuongThucTT) VALUES ('DH000010', 'KH000010', 'NV000015', TO_DATE('12/03/2026','DD/MM/YYYY'), TO_DATE('14/03/2026','DD/MM/YYYY'), TO_DATE('14/03/2026','DD/MM/YYYY'), N'Quận 5, TP.HCM', 35000, N'Hoàn thành', 1, 'COD')
    INTO DONHANG(MaDH, MaKH, MaNV, TGDat, TGGiaoYC, TGGiaoTT, DiaChiGiaoHang, PhiVanChuyen, TrangThaiDH, TrangThaiTT, PhuongThucTT) VALUES ('DH000011', 'KH000011', 'NV000011', TO_DATE('15/03/2026','DD/MM/YYYY'), TO_DATE('16/03/2026','DD/MM/YYYY'), TO_DATE('16/03/2026','DD/MM/YYYY'), N'Quận 8, TP.HCM', 40000, N'Hoàn thành', 1, 'Ví điện tử')
    INTO DONHANG(MaDH, MaKH, MaNV, TGDat, TGGiaoYC, TGGiaoTT, DiaChiGiaoHang, PhiVanChuyen, TrangThaiDH, TrangThaiTT, PhuongThucTT) VALUES ('DH000012', 'KH000012', 'NV000012', TO_DATE('18/03/2026','DD/MM/YYYY'), TO_DATE('19/03/2026','DD/MM/YYYY'), TO_DATE('19/03/2026','DD/MM/YYYY'), N'Quận 2, TP.HCM', 45000, N'Hoàn thành', 1, 'COD')
    INTO DONHANG(MaDH, MaKH, MaNV, TGDat, TGGiaoYC, TGGiaoTT, DiaChiGiaoHang, PhiVanChuyen, TrangThaiDH, TrangThaiTT, PhuongThucTT) VALUES ('DH000013', 'KH000013', 'NV000013', TO_DATE('20/03/2026','DD/MM/YYYY'), TO_DATE('22/03/2026','DD/MM/YYYY'), TO_DATE('22/03/2026','DD/MM/YYYY'), N'Bình Tân, TP.HCM', 50000, N'Hoàn thành', 1, 'Chuyển khoản')
    INTO DONHANG(MaDH, MaKH, MaNV, TGDat, TGGiaoYC, TGGiaoTT, DiaChiGiaoHang, PhiVanChuyen, TrangThaiDH, TrangThaiTT, PhuongThucTT) VALUES ('DH000014', 'KH000014', 'NV000014', TO_DATE('22/03/2026','DD/MM/YYYY'), TO_DATE('23/03/2026','DD/MM/YYYY'), TO_DATE('23/03/2026','DD/MM/YYYY'), N'Phú Nhuận, TP.HCM', 30000, N'Hoàn thành', 1, 'COD')
    INTO DONHANG(MaDH, MaKH, MaNV, TGDat, TGGiaoYC, TGGiaoTT, DiaChiGiaoHang, PhiVanChuyen, TrangThaiDH, TrangThaiTT, PhuongThucTT) VALUES ('DH000015', 'KH000015', 'NV000015', TO_DATE('25/03/2026','DD/MM/YYYY'), TO_DATE('26/03/2026','DD/MM/YYYY'), TO_DATE('26/03/2026','DD/MM/YYYY'), N'Quận 12, TP.HCM', 55000, N'Hoàn thành', 1, 'Ví điện tử')
    INTO DONHANG(MaDH, MaKH, MaNV, TGDat, TGGiaoYC, TGGiaoTT, DiaChiGiaoHang, PhiVanChuyen, TrangThaiDH, TrangThaiTT, PhuongThucTT) VALUES ('DH000016', 'KH000016', 'NV000011', TO_DATE('28/03/2026','DD/MM/YYYY'), TO_DATE('29/03/2026','DD/MM/YYYY'), TO_DATE('30/03/2026','DD/MM/YYYY'), N'Củ Chi, TP.HCM', 80000, N'Hoàn thành', 1, 'COD')
    INTO DONHANG(MaDH, MaKH, MaNV, TGDat, TGGiaoYC, TGGiaoTT, DiaChiGiaoHang, PhiVanChuyen, TrangThaiDH, TrangThaiTT, PhuongThucTT) VALUES ('DH000017', 'KH000017', 'NV000012', TO_DATE('30/03/2026','DD/MM/YYYY'), TO_DATE('01/04/2026','DD/MM/YYYY'), TO_DATE('01/04/2026','DD/MM/YYYY'), N'Hóc Môn, TP.HCM', 70000, N'Hoàn thành', 1, 'Chuyển khoản')
    INTO DONHANG(MaDH, MaKH, MaNV, TGDat, TGGiaoYC, TGGiaoTT, DiaChiGiaoHang, PhiVanChuyen, TrangThaiDH, TrangThaiTT, PhuongThucTT) VALUES ('DH000018', 'KH000018', 'NV000013', TO_DATE('02/04/2026','DD/MM/YYYY'), TO_DATE('03/04/2026','DD/MM/YYYY'), TO_DATE('03/04/2026','DD/MM/YYYY'), N'Bình Chánh, TP.HCM', 75000, N'Hoàn thành', 1, 'COD')
    INTO DONHANG(MaDH, MaKH, MaNV, TGDat, TGGiaoYC, TGGiaoTT, DiaChiGiaoHang, PhiVanChuyen, TrangThaiDH, TrangThaiTT, PhuongThucTT) VALUES ('DH000019', 'KH000019', 'NV000014', TO_DATE('05/04/2026','DD/MM/YYYY'), TO_DATE('06/04/2026','DD/MM/YYYY'), TO_DATE('06/04/2026','DD/MM/YYYY'), N'Quận 11, TP.HCM', 35000, N'Hoàn thành', 1, 'Ví điện tử')
    INTO DONHANG(MaDH, MaKH, MaNV, TGDat, TGGiaoYC, TGGiaoTT, DiaChiGiaoHang, PhiVanChuyen, TrangThaiDH, TrangThaiTT, PhuongThucTT) VALUES ('DH000020', 'KH000020', 'NV000015', TO_DATE('08/04/2026','DD/MM/YYYY'), TO_DATE('09/04/2026','DD/MM/YYYY'), TO_DATE('09/04/2026','DD/MM/YYYY'), N'Quận 6, TP.HCM', 30000, N'Hoàn thành', 1, 'COD')
    INTO DONHANG(MaDH, MaKH, MaNV, TGDat, TGGiaoYC, TGGiaoTT, DiaChiGiaoHang, PhiVanChuyen, TrangThaiDH, TrangThaiTT, PhuongThucTT) VALUES ('DH000021', 'KH000001', 'NV000011', TO_DATE('10/04/2026','DD/MM/YYYY'), TO_DATE('11/04/2026','DD/MM/YYYY'), TO_DATE('11/04/2026','DD/MM/YYYY'), N'Quận 1, TP.HCM', 30000, N'Hoàn thành', 1, 'Chuyển khoản')
    INTO DONHANG(MaDH, MaKH, MaNV, TGDat, TGGiaoYC, TGGiaoTT, DiaChiGiaoHang, PhiVanChuyen, TrangThaiDH, TrangThaiTT, PhuongThucTT) VALUES ('DH000022', 'KH000002', 'NV000012', TO_DATE('12/04/2026','DD/MM/YYYY'), TO_DATE('13/04/2026','DD/MM/YYYY'), TO_DATE('13/04/2026','DD/MM/YYYY'), N'Quận 3, TP.HCM', 40000, N'Hoàn thành', 1, 'COD')
    INTO DONHANG(MaDH, MaKH, MaNV, TGDat, TGGiaoYC, TGGiaoTT, DiaChiGiaoHang, PhiVanChuyen, TrangThaiDH, TrangThaiTT, PhuongThucTT) VALUES ('DH000023', 'KH000003', 'NV000013', TO_DATE('14/04/2026','DD/MM/YYYY'), TO_DATE('15/04/2026','DD/MM/YYYY'), TO_DATE('15/04/2026','DD/MM/YYYY'), N'Thủ Đức, TP.HCM', 50000, N'Hoàn thành', 1, 'Ví điện tử')
    INTO DONHANG(MaDH, MaKH, MaNV, TGDat, TGGiaoYC, TGGiaoTT, DiaChiGiaoHang, PhiVanChuyen, TrangThaiDH, TrangThaiTT, PhuongThucTT) VALUES ('DH000024', 'KH000004', 'NV000014', TO_DATE('15/04/2026','DD/MM/YYYY'), TO_DATE('16/04/2026','DD/MM/YYYY'), TO_DATE('16/04/2026','DD/MM/YYYY'), N'Bình Thạnh, TP.HCM', 35000, N'Hoàn thành', 1, 'COD')
    INTO DONHANG(MaDH, MaKH, MaNV, TGDat, TGGiaoYC, TGGiaoTT, DiaChiGiaoHang, PhiVanChuyen, TrangThaiDH, TrangThaiTT, PhuongThucTT) VALUES ('DH000025', 'KH000005', 'NV000015', TO_DATE('16/04/2026','DD/MM/YYYY'), TO_DATE('17/04/2026','DD/MM/YYYY'), TO_DATE('17/04/2026','DD/MM/YYYY'), N'Gò Vấp, TP.HCM', 45000, N'Hoàn thành', 1, 'Chuyển khoản')
    INTO DONHANG(MaDH, MaKH, MaNV, TGDat, TGGiaoYC, TGGiaoTT, DiaChiGiaoHang, PhiVanChuyen, TrangThaiDH, TrangThaiTT, PhuongThucTT) VALUES ('DH000026', 'KH000006', 'NV000011', TO_DATE('18/04/2026','DD/MM/YYYY'), TO_DATE('19/04/2026','DD/MM/YYYY'), TO_DATE('19/04/2026','DD/MM/YYYY'), N'Tân Bình, TP.HCM', 30000, N'Hoàn thành', 1, 'COD')
    INTO DONHANG(MaDH, MaKH, MaNV, TGDat, TGGiaoYC, TGGiaoTT, DiaChiGiaoHang, PhiVanChuyen, TrangThaiDH, TrangThaiTT, PhuongThucTT) VALUES ('DH000027', 'KH000007', 'NV000012', TO_DATE('19/04/2026','DD/MM/YYYY'), TO_DATE('20/04/2026','DD/MM/YYYY'), TO_DATE('20/04/2026','DD/MM/YYYY'), N'Quận 7, TP.HCM', 60000, N'Hoàn thành', 1, 'Ví điện tử')
    INTO DONHANG(MaDH, MaKH, MaNV, TGDat, TGGiaoYC, TGGiaoTT, DiaChiGiaoHang, PhiVanChuyen, TrangThaiDH, TrangThaiTT, PhuongThucTT) VALUES ('DH000028', 'KH000008', 'NV000013', TO_DATE('20/04/2026','DD/MM/YYYY'), TO_DATE('21/04/2026','DD/MM/YYYY'), TO_DATE('21/04/2026','DD/MM/YYYY'), N'Quận 10, TP.HCM', 25000, N'Hoàn thành', 1, 'COD')
    INTO DONHANG(MaDH, MaKH, MaNV, TGDat, TGGiaoYC, TGGiaoTT, DiaChiGiaoHang, PhiVanChuyen, TrangThaiDH, TrangThaiTT, PhuongThucTT) VALUES ('DH000029', 'KH000009', 'NV000014', TO_DATE('21/04/2026','DD/MM/YYYY'), TO_DATE('22/04/2026','DD/MM/YYYY'), TO_DATE('22/04/2026','DD/MM/YYYY'), N'Quận 4, TP.HCM', 30000, N'Hoàn thành', 1, 'Chuyển khoản')
    INTO DONHANG(MaDH, MaKH, MaNV, TGDat, TGGiaoYC, TGGiaoTT, DiaChiGiaoHang, PhiVanChuyen, TrangThaiDH, TrangThaiTT, PhuongThucTT) VALUES ('DH000030', 'KH000010', 'NV000015', TO_DATE('22/04/2026','DD/MM/YYYY'), TO_DATE('23/04/2026','DD/MM/YYYY'), TO_DATE('23/04/2026','DD/MM/YYYY'), N'Quận 5, TP.HCM', 35000, N'Hoàn thành', 1, 'COD')
    INTO DONHANG(MaDH, MaKH, MaNV, TGDat, TGGiaoYC, TGGiaoTT, DiaChiGiaoHang, PhiVanChuyen, TrangThaiDH, TrangThaiTT, PhuongThucTT) VALUES ('DH000031', 'KH000011', 'NV000011', TO_DATE('23/04/2026','DD/MM/YYYY'), TO_DATE('24/04/2026','DD/MM/YYYY'), TO_DATE('24/04/2026','DD/MM/YYYY'), N'Quận 8, TP.HCM', 40000, N'Hoàn thành', 1, 'Ví điện tử')
    INTO DONHANG(MaDH, MaKH, MaNV, TGDat, TGGiaoYC, TGGiaoTT, DiaChiGiaoHang, PhiVanChuyen, TrangThaiDH, TrangThaiTT, PhuongThucTT) VALUES ('DH000032', 'KH000012', 'NV000012', TO_DATE('24/04/2026','DD/MM/YYYY'), TO_DATE('25/04/2026','DD/MM/YYYY'), TO_DATE('25/04/2026','DD/MM/YYYY'), N'Quận 2, TP.HCM', 45000, N'Hoàn thành', 1, 'COD')
    INTO DONHANG(MaDH, MaKH, MaNV, TGDat, TGGiaoYC, DiaChiGiaoHang, PhiVanChuyen, TrangThaiDH, TrangThaiTT, PhuongThucTT) VALUES ('DH000033', 'KH000013', 'NV000013', TO_DATE('25/04/2026','DD/MM/YYYY'), TO_DATE('27/04/2026','DD/MM/YYYY'), N'Bình Tân, TP.HCM', 50000, N'Chờ giao hàng', 0, 'COD')
    INTO DONHANG(MaDH, MaKH, MaNV, TGDat, TGGiaoYC, DiaChiGiaoHang, PhiVanChuyen, TrangThaiDH, TrangThaiTT, PhuongThucTT) VALUES ('DH000034', 'KH000014', 'NV000014', TO_DATE('25/04/2026','DD/MM/YYYY'), TO_DATE('27/04/2026','DD/MM/YYYY'), N'Phú Nhuận, TP.HCM', 30000, N'Chờ giao hàng', 1, 'Chuyển khoản')
    INTO DONHANG(MaDH, MaKH, MaNV, TGDat, TGGiaoYC, DiaChiGiaoHang, PhiVanChuyen, TrangThaiDH, TrangThaiTT, PhuongThucTT) VALUES ('DH000035', 'KH000015', 'NV000015', TO_DATE('26/04/2026','DD/MM/YYYY'), TO_DATE('28/04/2026','DD/MM/YYYY'), N'Quận 12, TP.HCM', 55000, N'Chờ giao hàng', 0, 'COD')
    INTO DONHANG(MaDH, MaKH, MaNV, TGDat, TGGiaoYC, DiaChiGiaoHang, PhiVanChuyen, TrangThaiDH, TrangThaiTT, PhuongThucTT) VALUES ('DH000036', 'KH000016', 'NV000011', TO_DATE('26/04/2026','DD/MM/YYYY'), TO_DATE('28/04/2026','DD/MM/YYYY'), N'Củ Chi, TP.HCM', 80000, N'Chờ giao hàng', 1, 'Ví điện tử')
    INTO DONHANG(MaDH, MaKH, MaNV, TGDat, TGGiaoYC, DiaChiGiaoHang, PhiVanChuyen, TrangThaiDH, TrangThaiTT, PhuongThucTT) VALUES ('DH000037', 'KH000017', NULL, TO_DATE('27/04/2026','DD/MM/YYYY'), TO_DATE('29/04/2026','DD/MM/YYYY'), N'Hóc Môn, TP.HCM', 70000, N'Chờ xử lý', 0, 'COD')
    INTO DONHANG(MaDH, MaKH, MaNV, TGDat, TGGiaoYC, DiaChiGiaoHang, PhiVanChuyen, TrangThaiDH, TrangThaiTT, PhuongThucTT) VALUES ('DH000038', 'KH000018', NULL, TO_DATE('27/04/2026','DD/MM/YYYY'), TO_DATE('29/04/2026','DD/MM/YYYY'), N'Bình Chánh, TP.HCM', 75000, N'Chờ xử lý', 1, 'Chuyển khoản')
    INTO DONHANG(MaDH, MaKH, MaNV, TGDat, TGGiaoYC, DiaChiGiaoHang, LyDoHuy, PhiVanChuyen, TrangThaiDH, TrangThaiTT, PhuongThucTT) VALUES ('DH000039', 'KH000019', NULL, TO_DATE('20/04/2026','DD/MM/YYYY'), TO_DATE('22/04/2026','DD/MM/YYYY'), N'Quận 11, TP.HCM', N'Khách hàng thay đổi ý định', 35000, N'Đã huỷ', 0, 'COD')
    INTO DONHANG(MaDH, MaKH, MaNV, TGDat, TGGiaoYC, DiaChiGiaoHang, LyDoHuy, PhiVanChuyen, TrangThaiDH, TrangThaiTT, PhuongThucTT) VALUES ('DH000040', 'KH000020', NULL, TO_DATE('22/04/2026','DD/MM/YYYY'), TO_DATE('24/04/2026','DD/MM/YYYY'), N'Quận 6, TP.HCM', N'Khách phát hiện đặt nhầm hàng', 30000, N'Đã huỷ', 0, 'COD')
SELECT 1 FROM dual;

-- ====================================================================================
-- 12. CHITIETDONHANG (Sử dụng lệnh rời rạc để tránh lỗi Trigger Mutating Table)
-- Đơn hàng 1 sẽ có 6 loại sản phẩm để đảm bảo tính đa dạng.
-- ====================================================================================
INSERT INTO CHITIETDONHANG (MaCTDH, MaDH, MaSP, SoLuong) VALUES ('CTDH0001', 'DH000001', 'SP000011', 15);
INSERT INTO CHITIETDONHANG (MaCTDH, MaDH, MaSP, SoLuong) VALUES ('CTDH0002', 'DH000001', 'SP000012', 10);
INSERT INTO CHITIETDONHANG (MaCTDH, MaDH, MaSP, SoLuong) VALUES ('CTDH0003', 'DH000001', 'SP000013', 20);
INSERT INTO CHITIETDONHANG (MaCTDH, MaDH, MaSP, SoLuong) VALUES ('CTDH0004', 'DH000001', 'SP000001', 5);
INSERT INTO CHITIETDONHANG (MaCTDH, MaDH, MaSP, SoLuong) VALUES ('CTDH0005', 'DH000001', 'SP000006', 10);
INSERT INTO CHITIETDONHANG (MaCTDH, MaDH, MaSP, SoLuong) VALUES ('CTDH0006', 'DH000001', 'SP000021', 5);

INSERT INTO CHITIETDONHANG (MaCTDH, MaDH, MaSP, SoLuong) VALUES ('CTDH0007', 'DH000002', 'SP000002', 8);
INSERT INTO CHITIETDONHANG (MaCTDH, MaDH, MaSP, SoLuong) VALUES ('CTDH0008', 'DH000002', 'SP000007', 12);

INSERT INTO CHITIETDONHANG (MaCTDH, MaDH, MaSP, SoLuong) VALUES ('CTDH0009', 'DH000003', 'SP000016', 30);
INSERT INTO CHITIETDONHANG (MaCTDH, MaDH, MaSP, SoLuong) VALUES ('CTDH0010', 'DH000004', 'SP000022', 15);
INSERT INTO CHITIETDONHANG (MaCTDH, MaDH, MaSP, SoLuong) VALUES ('CTDH0011', 'DH000005', 'SP000004', 20);
INSERT INTO CHITIETDONHANG (MaCTDH, MaDH, MaSP, SoLuong) VALUES ('CTDH0012', 'DH000006', 'SP000008', 25);
INSERT INTO CHITIETDONHANG (MaCTDH, MaDH, MaSP, SoLuong) VALUES ('CTDH0013', 'DH000007', 'SP000017', 10);
INSERT INTO CHITIETDONHANG (MaCTDH, MaDH, MaSP, SoLuong) VALUES ('CTDH0014', 'DH000008', 'SP000024', 15);
INSERT INTO CHITIETDONHANG (MaCTDH, MaDH, MaSP, SoLuong) VALUES ('CTDH0015', 'DH000009', 'SP000009', 20);
INSERT INTO CHITIETDONHANG (MaCTDH, MaDH, MaSP, SoLuong) VALUES ('CTDH0016', 'DH000010', 'SP000019', 30);
INSERT INTO CHITIETDONHANG (MaCTDH, MaDH, MaSP, SoLuong) VALUES ('CTDH0017', 'DH000011', 'SP000010', 40);
INSERT INTO CHITIETDONHANG (MaCTDH, MaDH, MaSP, SoLuong) VALUES ('CTDH0018', 'DH000012', 'SP000014', 50);
INSERT INTO CHITIETDONHANG (MaCTDH, MaDH, MaSP, SoLuong) VALUES ('CTDH0019', 'DH000013', 'SP000020', 15);
INSERT INTO CHITIETDONHANG (MaCTDH, MaDH, MaSP, SoLuong) VALUES ('CTDH0020', 'DH000014', 'SP000025', 100);
INSERT INTO CHITIETDONHANG (MaCTDH, MaDH, MaSP, SoLuong) VALUES ('CTDH0021', 'DH000015', 'SP000005', 20);
INSERT INTO CHITIETDONHANG (MaCTDH, MaDH, MaSP, SoLuong) VALUES ('CTDH0022', 'DH000016', 'SP000015', 35);
INSERT INTO CHITIETDONHANG (MaCTDH, MaDH, MaSP, SoLuong) VALUES ('CTDH0023', 'DH000017', 'SP000023', 45);
INSERT INTO CHITIETDONHANG (MaCTDH, MaDH, MaSP, SoLuong) VALUES ('CTDH0024', 'DH000018', 'SP000011', 25);
INSERT INTO CHITIETDONHANG (MaCTDH, MaDH, MaSP, SoLuong) VALUES ('CTDH0025', 'DH000019', 'SP000003', 40);
INSERT INTO CHITIETDONHANG (MaCTDH, MaDH, MaSP, SoLuong) VALUES ('CTDH0026', 'DH000020', 'SP000012', 15);
INSERT INTO CHITIETDONHANG (MaCTDH, MaDH, MaSP, SoLuong) VALUES ('CTDH0027', 'DH000021', 'SP000006', 12);
INSERT INTO CHITIETDONHANG (MaCTDH, MaDH, MaSP, SoLuong) VALUES ('CTDH0028', 'DH000022', 'SP000018', 22);
INSERT INTO CHITIETDONHANG (MaCTDH, MaDH, MaSP, SoLuong) VALUES ('CTDH0029', 'DH000023', 'SP000001', 10);
INSERT INTO CHITIETDONHANG (MaCTDH, MaDH, MaSP, SoLuong) VALUES ('CTDH0030', 'DH000024', 'SP000021', 15);
INSERT INTO CHITIETDONHANG (MaCTDH, MaDH, MaSP, SoLuong) VALUES ('CTDH0031', 'DH000025', 'SP000013', 20);
INSERT INTO CHITIETDONHANG (MaCTDH, MaDH, MaSP, SoLuong) VALUES ('CTDH0032', 'DH000026', 'SP000002', 8);
INSERT INTO CHITIETDONHANG (MaCTDH, MaDH, MaSP, SoLuong) VALUES ('CTDH0033', 'DH000027', 'SP000007', 18);
INSERT INTO CHITIETDONHANG (MaCTDH, MaDH, MaSP, SoLuong) VALUES ('CTDH0034', 'DH000028', 'SP000016', 25);
INSERT INTO CHITIETDONHANG (MaCTDH, MaDH, MaSP, SoLuong) VALUES ('CTDH0035', 'DH000029', 'SP000022', 12);
INSERT INTO CHITIETDONHANG (MaCTDH, MaDH, MaSP, SoLuong) VALUES ('CTDH0036', 'DH000030', 'SP000004', 16);
INSERT INTO CHITIETDONHANG (MaCTDH, MaDH, MaSP, SoLuong) VALUES ('CTDH0037', 'DH000031', 'SP000008', 22);
INSERT INTO CHITIETDONHANG (MaCTDH, MaDH, MaSP, SoLuong) VALUES ('CTDH0038', 'DH000032', 'SP000017', 5);
INSERT INTO CHITIETDONHANG (MaCTDH, MaDH, MaSP, SoLuong) VALUES ('CTDH0039', 'DH000033', 'SP000024', 20);
INSERT INTO CHITIETDONHANG (MaCTDH, MaDH, MaSP, SoLuong) VALUES ('CTDH0040', 'DH000034', 'SP000009', 15);
INSERT INTO CHITIETDONHANG (MaCTDH, MaDH, MaSP, SoLuong) VALUES ('CTDH0041', 'DH000035', 'SP000019', 25);
INSERT INTO CHITIETDONHANG (MaCTDH, MaDH, MaSP, SoLuong) VALUES ('CTDH0042', 'DH000036', 'SP000010', 30);
INSERT INTO CHITIETDONHANG (MaCTDH, MaDH, MaSP, SoLuong) VALUES ('CTDH0043', 'DH000037', 'SP000014', 40);
INSERT INTO CHITIETDONHANG (MaCTDH, MaDH, MaSP, SoLuong) VALUES ('CTDH0044', 'DH000038', 'SP000020', 10);
INSERT INTO CHITIETDONHANG (MaCTDH, MaDH, MaSP, SoLuong) VALUES ('CTDH0045', 'DH000039', 'SP000025', 50);
INSERT INTO CHITIETDONHANG (MaCTDH, MaDH, MaSP, SoLuong) VALUES ('CTDH0046', 'DH000040', 'SP000005', 15);

-- ====================================================================================
-- 13. XUATKHO 
-- ====================================================================================
INSERT ALL
    INTO XUATKHO (MaXK, MaCTDH, MaTonKho, MaNV, SLXuat, TGCapNhat, TrangThaiXK) VALUES ('XK000001', 'CTDH0001', 'TK000001', 'NV000007', 15, SYSDATE, N'Tạm giữ')
    INTO XUATKHO (MaXK, MaCTDH, MaTonKho, MaNV, SLXuat, TGCapNhat, TrangThaiXK) VALUES ('XK000002', 'CTDH0002', 'TK000002', 'NV000007', 10, SYSDATE, N'Tạm giữ')
    INTO XUATKHO (MaXK, MaCTDH, MaTonKho, MaNV, SLXuat, TGCapNhat, TrangThaiXK) VALUES ('XK000003', 'CTDH0003', 'TK000003', 'NV000007', 20, SYSDATE, N'Tạm giữ')
    INTO XUATKHO (MaXK, MaCTDH, MaTonKho, MaNV, SLXuat, TGCapNhat, TrangThaiXK) VALUES ('XK000004', 'CTDH0004', 'TK000007', 'NV000007', 5, SYSDATE, N'Tạm giữ')
    INTO XUATKHO (MaXK, MaCTDH, MaTonKho, MaNV, SLXuat, TGCapNhat, TrangThaiXK) VALUES ('XK000005', 'CTDH0005', 'TK000005', 'NV000007', 10, SYSDATE, N'Tạm giữ')
    INTO XUATKHO (MaXK, MaCTDH, MaTonKho, MaNV, SLXuat, TGCapNhat, TrangThaiXK) VALUES ('XK000006', 'CTDH0006', 'TK000011', 'NV000007', 5, SYSDATE, N'Tạm giữ')
    INTO XUATKHO (MaXK, MaCTDH, MaTonKho, MaNV, SLXuat, TGCapNhat, TrangThaiXK) VALUES ('XK000007', 'CTDH0007', 'TK000008', 'NV000008', 8, SYSDATE, N'Tạm giữ')
    INTO XUATKHO (MaXK, MaCTDH, MaTonKho, MaNV, SLXuat, TGCapNhat, TrangThaiXK) VALUES ('XK000008', 'CTDH0008', 'TK000014', 'NV000008', 12, SYSDATE, N'Tạm giữ')
    INTO XUATKHO (MaXK, MaCTDH, MaTonKho, MaNV, SLXuat, TGCapNhat, TrangThaiXK) VALUES ('XK000009', 'CTDH0009', 'TK000009', 'NV000009', 30, SYSDATE, N'Tạm giữ')
    INTO XUATKHO (MaXK, MaCTDH, MaTonKho, MaNV, SLXuat, TGCapNhat, TrangThaiXK) VALUES ('XK000010', 'CTDH0010', 'TK000012', 'NV000010', 15, SYSDATE, N'Tạm giữ')
    INTO XUATKHO (MaXK, MaCTDH, MaTonKho, MaNV, SLXuat, TGCapNhat, TrangThaiXK) VALUES ('XK000011', 'CTDH0011', 'TK000013', 'NV000007', 20, SYSDATE, N'Tạm giữ')
    INTO XUATKHO (MaXK, MaCTDH, MaTonKho, MaNV, SLXuat, TGCapNhat, TrangThaiXK) VALUES ('XK000012', 'CTDH0012', 'TK000015', 'NV000008', 25, SYSDATE, N'Tạm giữ')
    INTO XUATKHO (MaXK, MaCTDH, MaTonKho, MaNV, SLXuat, TGCapNhat, TrangThaiXK) VALUES ('XK000013', 'CTDH0013', 'TK000010', 'NV000009', 10, SYSDATE, N'Tạm giữ')
    INTO XUATKHO (MaXK, MaCTDH, MaTonKho, MaNV, SLXuat, TGCapNhat, TrangThaiXK) VALUES ('XK000014', 'CTDH0014', 'TK000022', 'NV000010', 15, SYSDATE, N'Tạm giữ')
    INTO XUATKHO (MaXK, MaCTDH, MaTonKho, MaNV, SLXuat, TGCapNhat, TrangThaiXK) VALUES ('XK000015', 'CTDH0015', 'TK000016', 'NV000007', 20, SYSDATE, N'Tạm giữ')
    INTO XUATKHO (MaXK, MaCTDH, MaTonKho, MaNV, SLXuat, TGCapNhat, TrangThaiXK) VALUES ('XK000016', 'CTDH0016', 'TK000019', 'NV000008', 30, SYSDATE, N'Tạm giữ')
    INTO XUATKHO (MaXK, MaCTDH, MaTonKho, MaNV, SLXuat, TGCapNhat, TrangThaiXK) VALUES ('XK000017', 'CTDH0017', 'TK000017', 'NV000009', 40, SYSDATE, N'Tạm giữ')
    INTO XUATKHO (MaXK, MaCTDH, MaTonKho, MaNV, SLXuat, TGCapNhat, TrangThaiXK) VALUES ('XK000018', 'CTDH0018', 'TK000032', 'NV000010', 50, SYSDATE, N'Tạm giữ')
    INTO XUATKHO (MaXK, MaCTDH, MaTonKho, MaNV, SLXuat, TGCapNhat, TrangThaiXK) VALUES ('XK000019', 'CTDH0019', 'TK000020', 'NV000007', 15, SYSDATE, N'Tạm giữ')
    INTO XUATKHO (MaXK, MaCTDH, MaTonKho, MaNV, SLXuat, TGCapNhat, TrangThaiXK) VALUES ('XK000020', 'CTDH0020', 'TK000023', 'NV000008', 100, SYSDATE, N'Tạm giữ')
    INTO XUATKHO (MaXK, MaCTDH, MaTonKho, MaNV, SLXuat, TGCapNhat, TrangThaiXK) VALUES ('XK000021', 'CTDH0021', 'TK000024', 'NV000009', 20, SYSDATE, N'Tạm giữ')
    INTO XUATKHO (MaXK, MaCTDH, MaTonKho, MaNV, SLXuat, TGCapNhat, TrangThaiXK) VALUES ('XK000022', 'CTDH0022', 'TK000025', 'NV000010', 35, SYSDATE, N'Tạm giữ')
    INTO XUATKHO (MaXK, MaCTDH, MaTonKho, MaNV, SLXuat, TGCapNhat, TrangThaiXK) VALUES ('XK000023', 'CTDH0023', 'TK000021', 'NV000007', 45, SYSDATE, N'Tạm giữ')
    INTO XUATKHO (MaXK, MaCTDH, MaTonKho, MaNV, SLXuat, TGCapNhat, TrangThaiXK) VALUES ('XK000024', 'CTDH0024', 'TK000026', 'NV000008', 25, SYSDATE, N'Tạm giữ')
    INTO XUATKHO (MaXK, MaCTDH, MaTonKho, MaNV, SLXuat, TGCapNhat, TrangThaiXK) VALUES ('XK000025', 'CTDH0025', 'TK000041', 'NV000009', 40, SYSDATE, N'Tạm giữ')
    INTO XUATKHO (MaXK, MaCTDH, MaTonKho, MaNV, SLXuat, TGCapNhat, TrangThaiXK) VALUES ('XK000026', 'CTDH0026', 'TK000039', 'NV000010', 15, SYSDATE, N'Tạm giữ')
    INTO XUATKHO (MaXK, MaCTDH, MaTonKho, MaNV, SLXuat, TGCapNhat, TrangThaiXK) VALUES ('XK000027', 'CTDH0027', 'TK000029', 'NV000007', 12, SYSDATE, N'Tạm giữ')
    INTO XUATKHO (MaXK, MaCTDH, MaTonKho, MaNV, SLXuat, TGCapNhat, TrangThaiXK) VALUES ('XK000028', 'CTDH0028', 'TK000040', 'NV000008', 22, SYSDATE, N'Tạm giữ')
    INTO XUATKHO (MaXK, MaCTDH, MaTonKho, MaNV, SLXuat, TGCapNhat, TrangThaiXK) VALUES ('XK000029', 'CTDH0029', 'TK000027', 'NV000009', 10, SYSDATE, N'Tạm giữ')
    INTO XUATKHO (MaXK, MaCTDH, MaTonKho, MaNV, SLXuat, TGCapNhat, TrangThaiXK) VALUES ('XK000030', 'CTDH0030', 'TK000031', 'NV000010', 15, SYSDATE, N'Tạm giữ')
    INTO XUATKHO (MaXK, MaCTDH, MaTonKho, MaNV, SLXuat, TGCapNhat, TrangThaiXK) VALUES ('XK000031', 'CTDH0031', 'TK000030', 'NV000007', 20, SYSDATE, N'Tạm giữ')
    INTO XUATKHO (MaXK, MaCTDH, MaTonKho, MaNV, SLXuat, TGCapNhat, TrangThaiXK) VALUES ('XK000032', 'CTDH0032', 'TK000028', 'NV000008', 8, SYSDATE, N'Tạm giữ')
    INTO XUATKHO (MaXK, MaCTDH, MaTonKho, MaNV, SLXuat, TGCapNhat, TrangThaiXK) VALUES ('XK000033', 'CTDH0033', 'TK000034', 'NV000009', 18, SYSDATE, N'Tạm giữ')
    INTO XUATKHO (MaXK, MaCTDH, MaTonKho, MaNV, SLXuat, TGCapNhat, TrangThaiXK) VALUES ('XK000034', 'CTDH0034', 'TK000030', 'NV000010', 25, SYSDATE, N'Tạm giữ')
    INTO XUATKHO (MaXK, MaCTDH, MaTonKho, MaNV, SLXuat, TGCapNhat, TrangThaiXK) VALUES ('XK000035', 'CTDH0035', 'TK000037', 'NV000007', 12, SYSDATE, N'Tạm giữ')
    INTO XUATKHO (MaXK, MaCTDH, MaTonKho, MaNV, SLXuat, TGCapNhat, TrangThaiXK) VALUES ('XK000036', 'CTDH0036', 'TK000038', 'NV000008', 16, SYSDATE, N'Tạm giữ')
    INTO XUATKHO (MaXK, MaCTDH, MaTonKho, MaNV, SLXuat, TGCapNhat, TrangThaiXK) VALUES ('XK000037', 'CTDH0037', 'TK000033', 'NV000009', 22, SYSDATE, N'Tạm giữ')
    INTO XUATKHO (MaXK, MaCTDH, MaTonKho, MaNV, SLXuat, TGCapNhat, TrangThaiXK) VALUES ('XK000038', 'CTDH0038', 'TK000036', 'NV000010', 5, SYSDATE, N'Tạm giữ')
    INTO XUATKHO (MaXK, MaCTDH, MaTonKho, MaNV, SLXuat, TGCapNhat, TrangThaiXK) VALUES ('XK000039', 'CTDH0039', 'TK000042', 'NV000007', 20, SYSDATE, N'Tạm giữ')
    INTO XUATKHO (MaXK, MaCTDH, MaTonKho, MaNV, SLXuat, TGCapNhat, TrangThaiXK) VALUES ('XK000040', 'CTDH0040', 'TK000042', 'NV000008', 15, SYSDATE, N'Tạm giữ')
    INTO XUATKHO (MaXK, MaCTDH, MaTonKho, MaNV, SLXuat, TGCapNhat, TrangThaiXK) VALUES ('XK000041', 'CTDH0041', 'TK000041', 'NV000009', 25, SYSDATE, N'Tạm giữ')
    INTO XUATKHO (MaXK, MaCTDH, MaTonKho, MaNV, SLXuat, TGCapNhat, TrangThaiXK) VALUES ('XK000042', 'CTDH0042', 'TK000042', 'NV000010', 30, SYSDATE, N'Tạm giữ')
SELECT 1 FROM dual;

-- Đổi trạng thái xuất kho
UPDATE XUATKHO SET TrangThaiXK = N'Đã xuất' WHERE TrangThaiXK = N'Tạm giữ';
COMMIT;
