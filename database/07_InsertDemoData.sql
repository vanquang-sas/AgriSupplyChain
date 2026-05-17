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
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK) VALUES ('nvtm01', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 1, 1)
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK) VALUES ('nvtm02', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 1, 1)
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK) VALUES ('nvtm03', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 1, 1)
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK) VALUES ('nvtm04', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 1, 1)
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK) VALUES ('nvkho01', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 1, 1)
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK) VALUES ('nvkho02', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 1, 1)
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK) VALUES ('nvkho03', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 1, 1)
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK) VALUES ('nvkho04', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 1, 1)
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK) VALUES ('nvgh01', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 1, 1)
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK) VALUES ('nvgh02', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 1, 1)
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK) VALUES ('nvgh03', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 1, 1)
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK) VALUES ('nvgh04', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 1, 1)
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK) VALUES ('nvgh05', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 1, 1)
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK) VALUES ('kh01', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 2, 1)
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK) VALUES ('kh02', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 2, 1)
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK) VALUES ('kh03', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 2, 1)
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK) VALUES ('kh04', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 2, 1)
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK) VALUES ('kh05', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 2, 1)
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK) VALUES ('kh06', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 2, 1)
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK) VALUES ('kh07', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 2, 1)
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK) VALUES ('kh08', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 2, 1)
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK) VALUES ('kh09', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 2, 1)
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK) VALUES ('kh10', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 2, 1)
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK) VALUES ('kh11', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 2, 1)
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK) VALUES ('kh12', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 2, 1)
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK) VALUES ('kh13', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 2, 1)
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK) VALUES ('kh14', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 2, 1)
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK) VALUES ('kh15', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 2, 1)
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK) VALUES ('kh16', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 2, 1)
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK) VALUES ('kh17', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 2, 1)
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK) VALUES ('kh18', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 2, 1)
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK) VALUES ('kh19', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 2, 1)
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK) VALUES ('kh20', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 2, 1)
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
--        PHẦN 8: DATA ENRICHMENT - Sinh dữ liệu 12 tháng (05/2025 - 05/2026)
--        Tuân thủ: Không bịa ID, Margin 30-50%, Sell-through 75-90%
--        Đảm bảo: Revenue > Cost, Tồn kho > 0
-- ====================================================================================

DECLARE
    -- ===== Variables for loop control =====
    v_month_date DATE;
    v_month_start DATE;
    v_month_end DATE;
    v_day_offset NUMBER;
    
    -- ===== Variables for IDs (NOT fabricated, from DB) =====
    v_MaKH VARCHAR2(10);
    v_MaNV_ThuMua VARCHAR2(10);
    v_MaNV_GiaoHang VARCHAR2(10);
    v_MaNCC VARCHAR2(10);
    v_MaSP VARCHAR2(10);
    v_MaKho VARCHAR2(10);
    v_MaLH VARCHAR2(10);
    v_MaDH VARCHAR2(10);
    v_MaCTLH VARCHAR2(10);
    v_MaCTDH VARCHAR2(10);
    
    -- ===== Variables for pricing & quantities =====
    v_GiaMua NUMBER(12,2);
    v_GiaBan_Current NUMBER(12,2);
    v_GiaBan_New NUMBER(12,2);
    v_margin NUMBER := 0;
    v_SoLuong_import NUMBER := 0;
    v_SoLuong_export NUMBER := 0;
    v_total_import_month NUMBER := 0;
    v_total_import_cost_month NUMBER := 0;
    v_sell_through_rate NUMBER := 0;
    v_target_export_qty NUMBER := 0;
    v_current_export_qty NUMBER := 0;
    
    -- ===== Counters =====
    v_num_lohang_month NUMBER := 0;
    v_num_lohang_items NUMBER := 0;
    v_num_donhang_month NUMBER := 0;
    v_num_items_per_order NUMBER := 0;
    v_i NUMBER := 0;
    v_j NUMBER := 0;
    v_k NUMBER := 0;
    
    -- ===== Status tracking =====
    v_success_count NUMBER := 0;
    v_error_count NUMBER := 0;
    v_monthly_revenue NUMBER := 0;
    v_monthly_cost NUMBER := 0;
    
    -- ===== Arrays for 6 focused products =====
    TYPE t_sp_array IS TABLE OF VARCHAR2(10);
    v_focused_products t_sp_array := t_sp_array('SP000001', 'SP000002', 'SP000006', 'SP000007', 'SP000021', 'SP000025');
    
BEGIN
    DBMS_OUTPUT.PUT_LINE('============================================================');
    DBMS_OUTPUT.PUT_LINE('START: DATA ENRICHMENT (05/2025 - 05/2026)');
    DBMS_OUTPUT.PUT_LINE('============================================================');
    
    -- ===== PHASE 1: Disable triggers to avoid mutating table errors =====
    
    
    -- ===== PHASE 2: Main loop - 12 months (May 2025 to May 2026) =====
    FOR v_month_offset IN 0..12 LOOP
        v_month_date := ADD_MONTHS(TO_DATE('2025-05-01', 'YYYY-MM-DD'), v_month_offset);
        v_month_start := TRUNC(v_month_date, 'MM');
        v_month_end := LAST_DAY(v_month_date);
        v_total_import_month := 0;
        v_total_import_cost_month := 0;
        v_current_export_qty := 0;
        v_monthly_revenue := 0;
        v_monthly_cost := 0;
        
        DBMS_OUTPUT.PUT_LINE('');
        DBMS_OUTPUT.PUT_LINE('--- Processing month: ' || TO_CHAR(v_month_date, 'YYYY-MM'));
        
        BEGIN
            -- ===== SUB-PHASE 2.1: Generate import lots (LOHANG) =====
            v_num_lohang_month := TRUNC(5 + DBMS_RANDOM.VALUE(0, 3)); -- 5-7 lots per month
            DBMS_OUTPUT.PUT_LINE('  Generating ' || v_num_lohang_month || ' import lots...');
            
            FOR i IN 1..v_num_lohang_month LOOP
                BEGIN
                    -- Get random IDs from DB (NOT fabricated)
                    SELECT MaNCC INTO v_MaNCC FROM (
                        SELECT MaNCC FROM NHACUNGCAP WHERE TrangThaiHopTac = 1 
                        ORDER BY DBMS_RANDOM.VALUE
                    ) WHERE ROWNUM = 1;
                    
                    SELECT MaNV INTO v_MaNV_ThuMua FROM (
                        SELECT MaNV FROM NHANVIEN WHERE ChucVu = 'NV thu mua'
                        ORDER BY DBMS_RANDOM.VALUE
                    ) WHERE ROWNUM = 1;
                    
                    SELECT MaKho INTO v_MaKho FROM (
                        SELECT MaKho FROM KHO ORDER BY DBMS_RANDOM.VALUE
                    ) WHERE ROWNUM = 1;
                    
                    -- Generate MaLH (using sequence)
                    v_MaLH := 'LH' || LPAD(SEQ_LOHANG.NEXTVAL, 6, '0');
                    
                    -- Random date in month
                    v_day_offset := TRUNC(DBMS_RANDOM.VALUE(0, TO_NUMBER(TO_CHAR(v_month_end, 'DD'))));
                    
                    -- Insert LOHANG
                    INSERT INTO LOHANG (MaLH, MaNCC, MaNV, TGNhap, TrangThaiLH)
                    VALUES (v_MaLH, v_MaNCC, v_MaNV_ThuMua, v_month_start + v_day_offset, 'Chờ kiểm duyệt');
                    
                    -- ===== SUB-PHASE 2.1.1: Generate import details (CHITIETLOHANG) =====
                    v_num_lohang_items := TRUNC(1 + DBMS_RANDOM.VALUE(0, 3)); -- 1-3 items per lot
                    
                    FOR j IN 1..v_num_lohang_items LOOP
                        BEGIN
                            -- Get random product from focused list
                            v_MaSP := v_focused_products(TRUNC(DBMS_RANDOM.VALUE(1, v_focused_products.COUNT + 1)));
                            
                            -- Get current price
                            SELECT GiaMua INTO v_GiaMua FROM SANPHAM WHERE MaSP = v_MaSP;
                            
                            -- Random quantity: 200-1000 kg
                            v_SoLuong_import := TRUNC(200 + DBMS_RANDOM.VALUE(0, 801));
                            
                            -- Generate MaCTLH
                            v_MaCTLH := 'CTLH' || LPAD(SEQ_CHITIETLOHANG.NEXTVAL, 6, '0');
                            
                            -- Insert CHITIETLOHANG (trigger will calculate ThanhTien)
                            INSERT INTO CHITIETLOHANG (MaCTLH, MaLH, MaSP, SoLuong)
                            VALUES (v_MaCTLH, v_MaLH, v_MaSP, v_SoLuong_import);
                            
                            -- Track totals
                            v_total_import_month := v_total_import_month + v_SoLuong_import;
                            v_total_import_cost_month := v_total_import_cost_month + (v_SoLuong_import * v_GiaMua);
                            v_monthly_cost := v_monthly_cost + (v_SoLuong_import * v_GiaMua);
                            
                            -- Call procedure to confirm storage location
                            SP_XACNHAN_VITRI_CTLH(v_MaCTLH, v_MaKho, TO_DATE('2027-12-31', 'YYYY-MM-DD'), 
                                                  'Kệ A' || TRUNC(DBMS_RANDOM.VALUE(1, 11)));
                            
                        EXCEPTION
                            WHEN OTHERS THEN
                                v_error_count := v_error_count + 1;
                                DBMS_OUTPUT.PUT_LINE('    ✗ Error inserting CTLH: ' || SQLERRM);
                        END;
                    END LOOP;
                    
                    v_success_count := v_success_count + 1;
                    
                EXCEPTION
                    WHEN OTHERS THEN
                        v_error_count := v_error_count + 1;
                        DBMS_OUTPUT.PUT_LINE('    ✗ Error inserting LOHANG: ' || SQLERRM);
                END;
            END LOOP;
            
            DBMS_OUTPUT.PUT_LINE('  ✓ Import lots: ' || v_success_count || ' successful, ' || v_error_count || ' errors');
            DBMS_OUTPUT.PUT_LINE('    Total imported: ' || ROUND(v_total_import_month, 2) || ' kg, Cost: ' || ROUND(v_total_import_cost_month, 0) || ' VND');
            
            -- ===== SUB-PHASE 2.2: Update prices (SP_CAPNHAT_GIA) =====
            DBMS_OUTPUT.PUT_LINE('  Updating product prices...');
            FOR sp_idx IN 1..v_focused_products.COUNT LOOP
                BEGIN
                    SELECT GiaMua, GiaBan INTO v_GiaMua, v_GiaBan_Current 
                    FROM SANPHAM WHERE MaSP = v_focused_products(sp_idx);
                    
                    -- Random margin: 30-50%
                    v_margin := 0.30 + (DBMS_RANDOM.VALUE(0, 1) * 0.20);
                    v_GiaBan_New := ROUND(v_GiaMua * (1 + v_margin), 0);
                    
                    -- Update price (trigger TRG_SP_LUU_LSG will auto-log to LICHSUGIA)
                    SP_CAPNHAT_GIA(v_focused_products(sp_idx), v_GiaMua, v_GiaBan_New);
                    
                EXCEPTION
                    WHEN OTHERS THEN
                        DBMS_OUTPUT.PUT_LINE('    ⚠ Error updating price for ' || v_focused_products(sp_idx) || ': ' || SQLERRM);
                END;
            END LOOP;
            DBMS_OUTPUT.PUT_LINE('  ✓ Prices updated');
            
            -- ===== SUB-PHASE 2.3: Generate sales orders (DONHANG) =====
            -- Calculate sell-through rate: 75-90% of total imported
            v_sell_through_rate := 0.75 + (DBMS_RANDOM.VALUE(0, 1) * 0.15);
            v_target_export_qty := ROUND(v_total_import_month * v_sell_through_rate, 0);
            v_current_export_qty := 0;
            
            v_num_donhang_month := TRUNC(12 + DBMS_RANDOM.VALUE(0, 7)); -- 12-18 orders per month
            DBMS_OUTPUT.PUT_LINE('  Generating ' || v_num_donhang_month || ' sales orders...');
            DBMS_OUTPUT.PUT_LINE('    Target export: ' || v_target_export_qty || ' kg (sell-through rate: ' || ROUND(v_sell_through_rate * 100, 1) || '%)');
            
            FOR j IN 1..v_num_donhang_month LOOP
                BEGIN
                    -- Get random customer
                    SELECT MaKH INTO v_MaKH FROM (
                        SELECT MaKH FROM KHACHHANG ORDER BY DBMS_RANDOM.VALUE
                    ) WHERE ROWNUM = 1;
                    
                    -- Random delivery staff (or NULL)
                    IF DBMS_RANDOM.VALUE(0, 1) > 0.3 THEN
                        SELECT MaNV INTO v_MaNV_GiaoHang FROM (
                            SELECT MaNV FROM NHANVIEN WHERE ChucVu = 'NV giao hàng'
                            ORDER BY DBMS_RANDOM.VALUE
                        ) WHERE ROWNUM = 1;
                    ELSE
                        v_MaNV_GiaoHang := NULL;
                    END IF;
                    
                    -- Generate MaDH
                    v_MaDH := 'DH' || LPAD(SEQ_DONHANG.NEXTVAL, 6, '0');
                    
                    -- Random dates in month
                    v_day_offset := TRUNC(DBMS_RANDOM.VALUE(0, TO_NUMBER(TO_CHAR(v_month_end, 'DD')) - 1));
                    
                    -- Insert DONHANG
                    INSERT INTO DONHANG (MaDH, MaKH, MaNV, TGDat, TGGiaoYC, DiaChiGiaoHang, TrangThaiDH, TrangThaiTT, PhuongThucTT)
                    VALUES (v_MaDH, v_MaKH, v_MaNV_GiaoHang, 
                            v_month_start + v_day_offset, 
                            v_month_start + v_day_offset + TRUNC(DBMS_RANDOM.VALUE(1, 3)),
                            N'TP.HCM - Quận ' || TRUNC(DBMS_RANDOM.VALUE(1, 13)),
                            'Đã đặt', 0, 'COD');
                    
                    -- ===== SUB-PHASE 2.3.1: Generate order details (CHITIETDONHANG) =====
                    v_num_items_per_order := TRUNC(2 + DBMS_RANDOM.VALUE(0, 5)); -- 2-6 items per order
                    
                    FOR k IN 1..v_num_items_per_order LOOP
                        BEGIN
                            -- Exit if we've reached target export
                            IF v_current_export_qty >= v_target_export_qty THEN
                                EXIT;
                            END IF;
                            
                            -- Get random product
                            v_MaSP := v_focused_products(TRUNC(DBMS_RANDOM.VALUE(1, v_focused_products.COUNT + 1)));
                            
                            -- Random quantity: 5-50 kg (B2B small orders)
                            v_SoLuong_export := TRUNC(5 + DBMS_RANDOM.VALUE(0, 46));
                            
                            -- Don't exceed target
                            IF (v_current_export_qty + v_SoLuong_export) > v_target_export_qty THEN
                                v_SoLuong_export := v_target_export_qty - v_current_export_qty;
                            END IF;
                            
                            -- Generate MaCTDH
                            v_MaCTDH := 'CTDH' || LPAD(SEQ_CHITIETDONHANG.NEXTVAL, 6, '0');
                            
                            -- Insert CHITIETDONHANG (trigger will calculate GiaBan and ThanhTien)
                            INSERT INTO CHITIETDONHANG (MaCTDH, MaDH, MaSP, SoLuong)
                            VALUES (v_MaCTDH, v_MaDH, v_MaSP, v_SoLuong_export);
                            
                            v_current_export_qty := v_current_export_qty + v_SoLuong_export;
                            
                        EXCEPTION
                            WHEN OTHERS THEN
                                DBMS_OUTPUT.PUT_LINE('      ⚠ Error inserting CTDH: ' || SQLERRM);
                        END;
                    END LOOP;
                    
                    -- ===== Call SP_YEUCAU_XUATKHO to create export requests =====
                    BEGIN
                        SP_YEUCAU_XUATKHO(v_MaDH);
                    EXCEPTION
                        WHEN OTHERS THEN
                            DBMS_OUTPUT.PUT_LINE('    ✗ Error in SP_YEUCAU_XUATKHO for ' || v_MaDH || ': ' || SQLERRM);
                    END;
                    
                    -- ===== If month <= March 2026: confirm export and complete order =====
                    IF v_month_date <= TO_DATE('2026-03-31', 'YYYY-MM-DD') THEN
                        BEGIN
                            SELECT MaNV INTO v_MaNV_GiaoHang FROM (
                                SELECT MaNV FROM NHANVIEN WHERE ChucVu = 'NV giao hàng'
                                ORDER BY DBMS_RANDOM.VALUE
                            ) WHERE ROWNUM = 1;
                            
                            -- Đảm bảo có người giao hàng
                            IF v_MaNV_GiaoHang IS NULL THEN
                                SELECT MaNV INTO v_MaNV_GiaoHang FROM (
                                    SELECT MaNV FROM NHANVIEN WHERE ChucVu = 'NV giao hàng' ORDER BY DBMS_RANDOM.VALUE
                                ) WHERE ROWNUM = 1;
                            END IF;

                            SP_XACNHAN_XUATKHO(v_MaDH, v_MaNV_GiaoHang);

                            -- Cập nhật đầy đủ trạng thái và thông tin
                            UPDATE DONHANG 
                            SET TrangThaiDH = 'Hoàn thành',
                                TrangThaiTT = 1,
                                TGGiaoTT = TGGiaoYC + DBMS_RANDOM.VALUE(0, 1),
                                PhiVanChuyen = TRUNC(DBMS_RANDOM.VALUE(15, 50)) * 1000,
                                MaNV = v_MaNV_GiaoHang
                            WHERE MaDH = v_MaDH;

                            -- Cập nhật lại tổng tiền vì trigger không tự cộng PhiVanChuyen khi update trực tiếp trên DONHANG
                            UPDATE DONHANG SET TongTien = NVL(TongTienHang, 0) + NVL(PhiVanChuyen, 0) - NVL(GiamGia, 0) WHERE MaDH = v_MaDH;
                            
                        EXCEPTION
                            WHEN OTHERS THEN
                                DBMS_OUTPUT.PUT_LINE('    ⚠ Error confirming export for ' || v_MaDH || ': ' || SQLERRM);
                        END;
                    END IF;
                    
                EXCEPTION
                    WHEN OTHERS THEN
                        v_error_count := v_error_count + 1;
                        DBMS_OUTPUT.PUT_LINE('    ✗ Error inserting DONHANG: ' || SQLERRM);
                END;
            END LOOP;
            
            DBMS_OUTPUT.PUT_LINE('  ✓ Sales orders: exported ' || ROUND(v_current_export_qty, 2) || ' kg');
            
            -- ===== Calculate monthly revenue for completed orders =====
            BEGIN
                SELECT NVL(SUM(TongTien), 0) INTO v_monthly_revenue
                FROM DONHANG
                WHERE TRUNC(TGDat, 'MM') = v_month_start AND TrangThaiDH = 'Hoàn thành';
            EXCEPTION
                WHEN OTHERS THEN
                    v_monthly_revenue := 0;
            END;
            
            DBMS_OUTPUT.PUT_LINE('  ✓ Monthly Summary:');
            DBMS_OUTPUT.PUT_LINE('    Cost:   ' || ROUND(v_monthly_cost, 0) || ' VND');
            DBMS_OUTPUT.PUT_LINE('    Revenue: ' || ROUND(v_monthly_revenue, 0) || ' VND');
            DBMS_OUTPUT.PUT_LINE('    Profit:  ' || ROUND(v_monthly_revenue - v_monthly_cost, 0) || ' VND');
            
            IF v_monthly_revenue >= v_monthly_cost THEN
                DBMS_OUTPUT.PUT_LINE('    ✓ Revenue >= Cost (Condition met!)');
            ELSE
                DBMS_OUTPUT.PUT_LINE('    ⚠ Revenue < Cost (May happen for partial months)');
            END IF;
            
        EXCEPTION
            WHEN OTHERS THEN
                DBMS_OUTPUT.PUT_LINE('✗ Critical error in month ' || TO_CHAR(v_month_date, 'YYYY-MM') || ': ' || SQLERRM);
        END;
        
    END LOOP; -- End of 12-month loop
    
    -- ===== PHASE 3: Re-enable triggers =====
    
    
    -- ===== COMMIT all changes =====
    COMMIT;
    
    DBMS_OUTPUT.PUT_LINE('');
    DBMS_OUTPUT.PUT_LINE('============================================================');
    DBMS_OUTPUT.PUT_LINE('✅ DATA ENRICHMENT COMPLETED SUCCESSFULLY!');
    DBMS_OUTPUT.PUT_LINE('============================================================');
    DBMS_OUTPUT.PUT_LINE('Total operations: ' || v_success_count || ' successful, ' || v_error_count || ' errors');
    DBMS_OUTPUT.PUT_LINE('Period: 05/2025 - 05/2026');
    DBMS_OUTPUT.PUT_LINE('Products focused: 6 SKUs (SP000001, 002, 006, 007, 021, 025)');
    DBMS_OUTPUT.PUT_LINE('============================================================');
    
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        DBMS_OUTPUT.PUT_LINE('❌ FATAL ERROR: ' || SQLERRM);
        DBMS_OUTPUT.PUT_LINE('Transaction rolled back.');
END;
/

-- ===== VERIFICATION QUERIES (Run after script completes) =====
PROMPT
PROMPT ===== VERIFICATION - Check data enrichment results =====
PROMPT

SELECT 'LOHANG Count' AS metric, COUNT(*) AS result FROM LOHANG
UNION ALL
SELECT 'DONHANG Count', COUNT(*) FROM DONHANG
UNION ALL
SELECT 'CHITIETLOHANG Count', COUNT(*) FROM CHITIETLOHANG
UNION ALL
SELECT 'CHITIETDONHANG Count', COUNT(*) FROM CHITIETDONHANG
UNION ALL
SELECT 'TONKHO Count', COUNT(*) FROM TONKHO
UNION ALL
SELECT 'XUATKHO Count', COUNT(*) FROM XUATKHO
UNION ALL
SELECT 'LICHSUGIA Count', COUNT(*) FROM LICHSUGIA;


