-- ====================================================================================
--                           PHẦN 7: Thêm dữ liệu cho demo
-- ====================================================================================

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
    INTO SANPHAM(MaSP, TenSP, MaLSP, ChatLuong, GiaMua, GiaBan, DonViTinh, BaoQuan, HinhAnh) VALUES ('SP000001', N'Thịt heo Iberico', 'LSP00001', N'Loại 1', 150000, 200000, 'Kg', N'Đông', 'SP000001.jpg')
    INTO SANPHAM(MaSP, TenSP, MaLSP, ChatLuong, GiaMua, GiaBan, DonViTinh, BaoQuan, HinhAnh) VALUES ('SP000002', N'Thịt bò Kobe', 'LSP00001', N'Loại 1', 1500000, 2200000, 'Kg', N'Đông', 'SP000001.jpg')
    INTO SANPHAM(MaSP, TenSP, MaLSP, ChatLuong, GiaMua, GiaBan, DonViTinh, BaoQuan, HinhAnh) VALUES ('SP000003', N'Thịt gà ta thả vườn', 'LSP00001', N'Loại 2', 90000, 130000, 'Kg', N'Lạnh', 'SP000001.jpg')
    INTO SANPHAM(MaSP, TenSP, MaLSP, ChatLuong, GiaMua, GiaBan, DonViTinh, BaoQuan, HinhAnh) VALUES ('SP000004', N'Đùi cừu Úc', 'LSP00001', N'Loại 1', 300000, 420000, 'Kg', N'Đông', 'SP000001.jpg')
    INTO SANPHAM(MaSP, TenSP, MaLSP, ChatLuong, GiaMua, GiaBan, DonViTinh, BaoQuan, HinhAnh) VALUES ('SP000005', N'Thịt vịt xiêm', 'LSP00001', N'Loại 2', 80000, 110000, 'Kg', N'Lạnh', 'SP000001.jpg')
    INTO SANPHAM(MaSP, TenSP, MaLSP, ChatLuong, GiaMua, GiaBan, DonViTinh, BaoQuan, HinhAnh) VALUES ('SP000006', N'Cá hồi NaUy nguyên con', 'LSP00002', N'Loại 1', 350000, 500000, 'Kg', N'Lạnh', 'SP000001.jpg')
    INTO SANPHAM(MaSP, TenSP, MaLSP, ChatLuong, GiaMua, GiaBan, DonViTinh, BaoQuan, HinhAnh) VALUES ('SP000007', N'Mực ống Trường Sa', 'LSP00002', N'Loại 1', 250000, 320000, 'Kg', N'Đông', 'SP000001.jpg')
    INTO SANPHAM(MaSP, TenSP, MaLSP, ChatLuong, GiaMua, GiaBan, DonViTinh, BaoQuan, HinhAnh) VALUES ('SP000008', N'Tôm sú Cà Mau', 'LSP00002', N'Loại 2', 200000, 280000, 'Kg', N'Đông', 'SP000001.jpg')
    INTO SANPHAM(MaSP, TenSP, MaLSP, ChatLuong, GiaMua, GiaBan, DonViTinh, BaoQuan, HinhAnh) VALUES ('SP000009', N'Cua thịt Năm Căn', 'LSP00002', N'Loại 1', 400000, 550000, 'Kg', N'Mát', 'SP000001.jpg')
    INTO SANPHAM(MaSP, TenSP, MaLSP, ChatLuong, GiaMua, GiaBan, DonViTinh, BaoQuan, HinhAnh) VALUES ('SP000010', N'Bạch tuộc sữa', 'LSP00002', N'Loại 3', 120000, 160000, 'Kg', N'Đông', 'SP000001.jpg')
    INTO SANPHAM(MaSP, TenSP, MaLSP, ChatLuong, GiaMua, GiaBan, DonViTinh, BaoQuan, HinhAnh) VALUES ('SP000011', N'Cải bắp Đà Lạt', 'LSP00003', N'Loại 1', 15000, 25000, 'Kg', N'Mát', 'SP000001.jpg')
    INTO SANPHAM(MaSP, TenSP, MaLSP, ChatLuong, GiaMua, GiaBan, DonViTinh, BaoQuan, HinhAnh) VALUES ('SP000012', N'Cà chua Cherry', 'LSP00003', N'Loại 1', 40000, 60000, 'Kg', N'Mát', 'SP000001.jpg')
    INTO SANPHAM(MaSP, TenSP, MaLSP, ChatLuong, GiaMua, GiaBan, DonViTinh, BaoQuan, HinhAnh) VALUES ('SP000013', N'Súp lơ xanh', 'LSP00003', N'Loại 2', 20000, 35000, 'Kg', N'Mát', 'SP000001.jpg')
    INTO SANPHAM(MaSP, TenSP, MaLSP, ChatLuong, GiaMua, GiaBan, DonViTinh, BaoQuan, HinhAnh) VALUES ('SP000014', N'Cà rốt Baby', 'LSP00003', N'Loại 1', 30000, 50000, 'Kg', N'Mát', 'SP000001.jpg')
    INTO SANPHAM(MaSP, TenSP, MaLSP, ChatLuong, GiaMua, GiaBan, DonViTinh, BaoQuan, HinhAnh) VALUES ('SP000015', N'Khoai tây mầm', 'LSP00003', N'Loại 3', 10000, 18000, 'Kg', N'Mát', 'SP000001.jpg')
    INTO SANPHAM(MaSP, TenSP, MaLSP, ChatLuong, GiaMua, GiaBan, DonViTinh, BaoQuan, HinhAnh) VALUES ('SP000016', N'Táo Envy Mỹ', 'LSP00004', N'Loại 1', 180000, 250000, 'Kg', N'Mát', 'SP000001.jpg')
    INTO SANPHAM(MaSP, TenSP, MaLSP, ChatLuong, GiaMua, GiaBan, DonViTinh, BaoQuan, HinhAnh) VALUES ('SP000017', N'Nho mẫu đơn Hàn Quốc', 'LSP00004', N'Loại 1', 500000, 750000, 'Chùm', N'Mát', 'SP000001.jpg')
    INTO SANPHAM(MaSP, TenSP, MaLSP, ChatLuong, GiaMua, GiaBan, DonViTinh, BaoQuan, HinhAnh) VALUES ('SP000018', N'Dâu tây Mộc Châu', 'LSP00004', N'Loại 2', 120000, 180000, 'Hộp', N'Mát', 'SP000001.jpg')
    INTO SANPHAM(MaSP, TenSP, MaLSP, ChatLuong, GiaMua, GiaBan, DonViTinh, BaoQuan, HinhAnh) VALUES ('SP000019', N'Dưa lưới Taki', 'LSP00004', N'Loại 1', 60000, 95000, 'Kg', N'Mát', 'SP000001.jpg')
    INTO SANPHAM(MaSP, TenSP, MaLSP, ChatLuong, GiaMua, GiaBan, DonViTinh, BaoQuan, HinhAnh) VALUES ('SP000020', N'Bơ sáp Đắk Lắk', 'LSP00004', N'Loại 2', 35000, 55000, 'Kg', N'Mát', 'SP000001.jpg')
    INTO SANPHAM(MaSP, TenSP, MaLSP, ChatLuong, GiaMua, GiaBan, DonViTinh, BaoQuan, HinhAnh) VALUES ('SP000021', N'Hạt điều rang củi', 'LSP00005', N'Loại 1', 250000, 350000, 'Kg', N'Mát', 'SP000001.jpg')
    INTO SANPHAM(MaSP, TenSP, MaLSP, ChatLuong, GiaMua, GiaBan, DonViTinh, BaoQuan, HinhAnh) VALUES ('SP000022', N'Hạt macca Úc', 'LSP00005', N'Loại 1', 300000, 420000, 'Kg', N'Mát', 'SP000001.jpg')
    INTO SANPHAM(MaSP, TenSP, MaLSP, ChatLuong, GiaMua, GiaBan, DonViTinh, BaoQuan, HinhAnh) VALUES ('SP000023', N'Đậu phộng sấy', 'LSP00005', N'Loại 3', 50000, 80000, 'Kg', N'Mát', 'SP000001.jpg')
    INTO SANPHAM(MaSP, TenSP, MaLSP, ChatLuong, GiaMua, GiaBan, DonViTinh, BaoQuan, HinhAnh) VALUES ('SP000024', N'Hạt dẻ cười Mỹ', 'LSP00005', N'Loại 1', 280000, 400000, 'Kg', N'Mát', 'SP000001.jpg')
    INTO SANPHAM(MaSP, TenSP, MaLSP, ChatLuong, GiaMua, GiaBan, DonViTinh, BaoQuan, HinhAnh) VALUES ('SP000025', N'Gạo ST25', 'LSP00005', N'Loại 1', 30000, 45000, 'Kg', N'Mát', 'SP000001.jpg')
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
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK, DiaChi, SDT, Email) VALUES ('nvtm01', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 1, 1, NULL, '0981112221', NULL)
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK, DiaChi, SDT, Email) VALUES ('nvtm02', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 1, 1, NULL, '0981112222', NULL)
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK, DiaChi, SDT, Email) VALUES ('nvtm03', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 1, 1, NULL, '0981112223', NULL)
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK, DiaChi, SDT, Email) VALUES ('nvtm04', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 1, 1, NULL, '0981112224', NULL)
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK, DiaChi, SDT, Email) VALUES ('nvkho01', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 1, 1, NULL, '0982223331', NULL)
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK, DiaChi, SDT, Email) VALUES ('nvkho02', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 1, 1, NULL, '0982223332', NULL)
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK, DiaChi, SDT, Email) VALUES ('nvkho03', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 1, 1, NULL, '0982223333', NULL)
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK, DiaChi, SDT, Email) VALUES ('nvkho04', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 1, 1, NULL, '0982223334', NULL)
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK, DiaChi, SDT, Email) VALUES ('nvgh01', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 1, 1, NULL, '0983334441', NULL)
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK, DiaChi, SDT, Email) VALUES ('nvgh02', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 1, 1, NULL, '0983334442', NULL)
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK, DiaChi, SDT, Email) VALUES ('nvgh03', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 1, 1, NULL, '0983334443', NULL)
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK, DiaChi, SDT, Email) VALUES ('nvgh04', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 1, 1, NULL, '0983334444', NULL)
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK, DiaChi, SDT, Email) VALUES ('nvgh05', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 1, 1, NULL, '0983334445', NULL)
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK, DiaChi, SDT, Email) VALUES ('kh01', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 2, 1, N'Quận 1, TP.HCM', '0912345001', 'tra.nguyen@gmail.com')
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK, DiaChi, SDT, Email) VALUES ('kh02', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 2, 1, N'Quận 3, TP.HCM', '0912345002', 'lap.tran@gmail.com')
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK, DiaChi, SDT, Email) VALUES ('kh03', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 2, 1, N'Thủ Đức, TP.HCM', '0912345003', 'nam.le@yahoo.com')
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK, DiaChi, SDT, Email) VALUES ('kh04', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 2, 1, N'Bình Thạnh, TP.HCM', '0912345004', 'dung.pham@gmail.com')
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK, DiaChi, SDT, Email) VALUES ('kh05', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 2, 1, N'Gò Vấp, TP.HCM', '0912345005', 'yen.vo@hotmail.com')
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK, DiaChi, SDT, Email) VALUES ('kh06', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 2, 1, N'Tân Bình, TP.HCM', '0912345006', 'son.dang@gmail.com')
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK, DiaChi, SDT, Email) VALUES ('kh07', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 2, 1, N'Quận 7, TP.HCM', '0912345007', 'hanh.bui@gmail.com')
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK, DiaChi, SDT, Email) VALUES ('kh08', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 2, 1, N'Quận 10, TP.HCM', '0912345008', 'nghia.do@gmail.com')
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK, DiaChi, SDT, Email) VALUES ('kh09', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 2, 1, N'Quận 4, TP.HCM', '0912345009', 'thao.ho@yahoo.com')
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK, DiaChi, SDT, Email) VALUES ('kh10', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 2, 1, N'Quận 5, TP.HCM', '0912345010', 'vuong.duong@gmail.com')
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK, DiaChi, SDT, Email) VALUES ('kh11', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 2, 1, N'Quận 8, TP.HCM', '0912345011', 'canh.vu@gmail.com')
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK, DiaChi, SDT, Email) VALUES ('kh12', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 2, 1, N'Quận 2, TP.HCM', '0912345012', 'cuong.ngo@gmail.com')
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK, DiaChi, SDT, Email) VALUES ('kh13', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 2, 1, N'Bình Tân, TP.HCM', '0912345013', 'kiet.doan@hotmail.com')
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK, DiaChi, SDT, Email) VALUES ('kh14', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 2, 1, N'Phú Nhuận, TP.HCM', '0912345014', 'ngoc.ly@gmail.com')
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK, DiaChi, SDT, Email) VALUES ('kh15', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 2, 1, N'Quận 12, TP.HCM', '0912345015', 'thang.nguyen@gmail.com')
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK, DiaChi, SDT, Email) VALUES ('kh16', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 2, 1, N'Củ Chi, TP.HCM', '0912345016', 'cam.phan@gmail.com')
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK, DiaChi, SDT, Email) VALUES ('kh17', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 2, 1, N'Hóc Môn, TP.HCM', '0912345017', 'bach.trinh@yahoo.com')
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK, DiaChi, SDT, Email) VALUES ('kh18', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 2, 1, N'Bình Chánh, TP.HCM', '0912345018', 'muoi.dinh@gmail.com')
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK, DiaChi, SDT, Email) VALUES ('kh19', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 2, 1, N'Quận 11, TP.HCM', '0912345019', 'tuyet.lam@gmail.com')
    INTO TAIKHOAN(Username, Password, LoaiTK, TrangThaiTK, DiaChi, SDT, Email) VALUES ('kh20', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 2, 1, N'Quận 6, TP.HCM', '0912345020', 'thong.ta@hotmail.com')
SELECT * FROM dual;

-- -------------------------------------------------------------------------
-- 6. NHANVIEN (13 Nhân viên mới)
-- -------------------------------------------------------------------------
INSERT ALL
    INTO NHANVIEN(MaNV, Username, TenNV, ChucVu, Luong) VALUES ('NV000003', 'nvtm01', N'Trần Hữu Trọng', N'NV thu mua', 12000000)
    INTO NHANVIEN(MaNV, Username, TenNV, ChucVu, Luong) VALUES ('NV000004', 'nvtm02', N'Phạm Tấn Tài', N'NV thu mua', 12500000)
    INTO NHANVIEN(MaNV, Username, TenNV, ChucVu, Luong) VALUES ('NV000005', 'nvtm03', N'Lê Tú Anh', N'NV thu mua', 11000000)
    INTO NHANVIEN(MaNV, Username, TenNV, ChucVu, Luong) VALUES ('NV000006', 'nvtm04', N'Đinh Hoàng Hải', N'NV thu mua', 13000000)
    INTO NHANVIEN(MaNV, Username, TenNV, ChucVu, Luong) VALUES ('NV000007', 'nvkho01', N'Nguyễn Hữu Quyết', N'NV kho', 10000000)
    INTO NHANVIEN(MaNV, Username, TenNV, ChucVu, Luong) VALUES ('NV000008', 'nvkho02', N'Lâm Tiến Đạt', N'NV kho', 9500000)
    INTO NHANVIEN(MaNV, Username, TenNV, ChucVu, Luong) VALUES ('NV000009', 'nvkho03', N'Phan Công Minh', N'NV kho', 10500000)
    INTO NHANVIEN(MaNV, Username, TenNV, ChucVu, Luong) VALUES ('NV000010', 'nvkho04', N'Bùi Trọng Đạo', N'NV kho', 11000000)
    INTO NHANVIEN(MaNV, Username, TenNV, ChucVu, Luong) VALUES ('NV000011', 'nvgh01', N'Võ Thanh Hùng', N'NV giao hàng', 8000000)
    INTO NHANVIEN(MaNV, Username, TenNV, ChucVu, Luong) VALUES ('NV000012', 'nvgh02', N'Đỗ Quốc Cường', N'NV giao hàng', 8500000)
    INTO NHANVIEN(MaNV, Username, TenNV, ChucVu, Luong) VALUES ('NV000013', 'nvgh03', N'Lê Thanh Sang', N'NV giao hàng', 9000000)
    INTO NHANVIEN(MaNV, Username, TenNV, ChucVu, Luong) VALUES ('NV000014', 'nvgh04', N'Nguyễn Văn Tuấn', N'NV giao hàng', 8000000)
    INTO NHANVIEN(MaNV, Username, TenNV, ChucVu, Luong) VALUES ('NV000015', 'nvgh05', N'Trần Đình Bảo', N'NV giao hàng', 8500000)
SELECT * FROM dual;

-- -------------------------------------------------------------------------
-- 7. KHACHHANG (20 Khách hàng)
-- -------------------------------------------------------------------------
INSERT ALL
    INTO KHACHHANG(MaKH, Username, TenKH, LoaiKH) VALUES ('KH000001', 'kh01', N'Nguyễn Thu Trà', N'VIP')
    INTO KHACHHANG(MaKH, Username, TenKH, LoaiKH) VALUES ('KH000002', 'kh02', N'Trần Bích Lập', N'Thường')
    INTO KHACHHANG(MaKH, Username, TenKH, LoaiKH) VALUES ('KH000003', 'kh03', N'Lê Hoàng Nam', N'Thân thiết')
    INTO KHACHHANG(MaKH, Username, TenKH, LoaiKH) VALUES ('KH000004', 'kh04', N'Phạm Quang Dũng', N'Thường')
    INTO KHACHHANG(MaKH, Username, TenKH, LoaiKH) VALUES ('KH000005', 'kh05', N'Võ Thị Yến', N'Thường')
    INTO KHACHHANG(MaKH, Username, TenKH, LoaiKH) VALUES ('KH000006', 'kh06', N'Đặng Thái Sơn', N'VIP')
    INTO KHACHHANG(MaKH, Username, TenKH, LoaiKH) VALUES ('KH000007', 'kh07', N'Bùi Thúy Hạnh', N'Thường')
    INTO KHACHHANG(MaKH, Username, TenKH, LoaiKH) VALUES ('KH000008', 'kh08', N'Đỗ Hữu Nghĩa', N'Thân thiết')
    INTO KHACHHANG(MaKH, Username, TenKH, LoaiKH) VALUES ('KH000009', 'kh09', N'Hồ Thanh Thảo', N'Thường')
    INTO KHACHHANG(MaKH, Username, TenKH, LoaiKH) VALUES ('KH000010', 'kh10', N'Dương Quốc Vượng', N'Thân thiết')
    INTO KHACHHANG(MaKH, Username, TenKH, LoaiKH) VALUES ('KH000011', 'kh11', N'Vũ Đức Cảnh', N'Thường')
    INTO KHACHHANG(MaKH, Username, TenKH, LoaiKH) VALUES ('KH000012', 'kh12', N'Ngô Nhật Cường', N'VIP')
    INTO KHACHHANG(MaKH, Username, TenKH, LoaiKH) VALUES ('KH000013', 'kh13', N'Đoàn Tuấn Kiệt', N'Thường')
    INTO KHACHHANG(MaKH, Username, TenKH, LoaiKH) VALUES ('KH000014', 'kh14', N'Lý Thảo Ngọc', N'Thân thiết')
    INTO KHACHHANG(MaKH, Username, TenKH, LoaiKH) VALUES ('KH000015', 'kh15', N'Nguyễn Bá Thắng', N'Thường')
    INTO KHACHHANG(MaKH, Username, TenKH, LoaiKH) VALUES ('KH000016', 'kh16', N'Phan Thị Cẩm', N'Thường')
    INTO KHACHHANG(MaKH, Username, TenKH, LoaiKH) VALUES ('KH000017', 'kh17', N'Trịnh Xuân Bách', N'Thân thiết')
    INTO KHACHHANG(MaKH, Username, TenKH, LoaiKH) VALUES ('KH000018', 'kh18', N'Đinh Thị Mười', N'Thường')
    INTO KHACHHANG(MaKH, Username, TenKH, LoaiKH) VALUES ('KH000019', 'kh19', N'Lâm Ánh Tuyết', N'VIP')
    INTO KHACHHANG(MaKH, Username, TenKH, LoaiKH) VALUES ('KH000020', 'kh20', N'Tạ Văn Thông', N'Thường')
SELECT * FROM dual;

COMMIT;

-- ====================================================================================
--        PHẦN 8: DATA ENRICHMENT - Sinh dữ liệu (11/2025 - 05/2026)     
-- ====================================================================================

SET DEFINE OFF;
DECLARE
    -- ===== Variables for loop control =====
    v_month_date DATE;
    v_month_start DATE;
    v_month_end DATE;
    v_days_in_month NUMBER;
    v_day_offset NUMBER;
    v_simulated_date DATE;
    
    -- ===== Variables for IDs =====
    v_MaKH VARCHAR2(10);
    v_MaNV_ThuMua VARCHAR2(10);
    v_MaNV_GiaoHang VARCHAR2(10);
    v_MaNCC VARCHAR2(10);
    v_MaSP VARCHAR2(10);
    v_MaKho VARCHAR2(10);
    v_MaLH VARCHAR2(10);
    v_MaDH VARCHAR2(10);
    v_DiaChiGiao NVARCHAR2(255);
    v_PhuongThucTT NVARCHAR2(50);
    
    -- ===== Variables for pricing & quantities =====
    v_GiaMua NUMBER(15,2);
    v_GiaBan_New NUMBER(15,2);
    v_margin NUMBER := 0;
    v_SoLuong_import NUMBER := 0;
    v_SoLuong_export NUMBER := 0;
    v_BaoQuan NVARCHAR2(100);
    
    -- ===== Counters & Control =====
    v_import_sp_idx NUMBER := 1;
    v_num_items NUMBER := 0;
    
    -- ===== Arrays =====
    -- Tất cả 25 sản phẩm
    TYPE t_sp_all IS TABLE OF VARCHAR2(10);
    v_all_products t_sp_all := t_sp_all(
        'SP000001', 'SP000002', 'SP000003', 'SP000004', 'SP000005',
        'SP000006', 'SP000007', 'SP000008', 'SP000009', 'SP000010',
        'SP000011', 'SP000012', 'SP000013', 'SP000014', 'SP000015',
        'SP000016', 'SP000017', 'SP000018', 'SP000019', 'SP000020',
        'SP000021', 'SP000022', 'SP000023', 'SP000024', 'SP000025'
    );
    
    -- Danh sách sản phẩm cho mỗi loại (để cập nhật biến động giá 1-2 lần/tháng cho mỗi loại)
    TYPE t_sp_array IS TABLE OF VARCHAR2(10);
    -- Cập nhật giá nhóm 1 (tuần 1)
    v_sp_week1 t_sp_array := t_sp_array('SP000002', 'SP000007', 'SP000012', 'SP000017', 'SP000022');
    -- Cập nhật giá nhóm 2 (tuần 3)
    v_sp_week3 t_sp_array := t_sp_array('SP000004', 'SP000009', 'SP000014', 'SP000019', 'SP000024');
    
    -- Helper procedure to perform price update and shift TGApDung
    PROCEDURE P_UPDATE_PRICE_SIMULATED(p_MaSP IN VARCHAR2, p_Date IN DATE) IS
        v_current_GiaMua NUMBER(15,2);
        v_new_GiaMua NUMBER(15,2);
        v_new_GiaBan NUMBER(15,2);
        v_rand_margin NUMBER;
    BEGIN
        SELECT GiaMua INTO v_current_GiaMua FROM SANPHAM WHERE MaSP = p_MaSP;
        -- Dao động từ -3% đến +5%
        v_new_GiaMua := ROUND(v_current_GiaMua * (1 + DBMS_RANDOM.VALUE(-0.03, 0.05)), -3);
        -- Biên lợi nhuận 30% - 50%
        v_rand_margin := 0.30 + DBMS_RANDOM.VALUE(0, 0.20);
        v_new_GiaBan := ROUND(v_new_GiaMua * (1 + v_rand_margin), -3);
        
        -- Gọi procedure hệ thống để cập nhật bảng SANPHAM (tự kích hoạt trigger thêm vào LICHSUGIA với TGApDung = SYSDATE)
        SP_CAPNHAT_GIA(p_MaSP, v_new_GiaMua, v_new_GiaBan);
        
        -- Cập nhật lại TGApDung của dòng lịch sử giá vừa tạo thành ngày mô phỏng
        UPDATE LICHSUGIA 
        SET TGApDung = p_Date 
        WHERE MaGia = (SELECT MaGia FROM (SELECT MaGia FROM LICHSUGIA WHERE MaSP = p_MaSP ORDER BY MaGia DESC) WHERE ROWNUM = 1)
          AND TRUNC(TGApDung) = TRUNC(SYSDATE);
    EXCEPTION 
        WHEN OTHERS THEN 
            DBMS_OUTPUT.PUT_LINE('Lỗi cập nhật giá ' || p_MaSP || ': ' || SQLERRM);
    END;

BEGIN
    DBMS_OUTPUT.PUT_LINE('BẮT ĐẦU CHẠY DATA ENRICHMENT - GIAI ĐOẠN 11/2025 - 05/2026');
    
    -- Xoá dữ liệu cũ của các bảng liên quan đến giao dịch trước khi nạp demo
    -- (Để tránh trùng lặp nếu chạy lại script)
    DELETE FROM XUATKHO;
    DELETE FROM TONKHO;
    DELETE FROM CHITIETDONHANG;
    DELETE FROM DONHANG;
    DELETE FROM CHITIETLOHANG;
    DELETE FROM LOHANG;
    DELETE FROM THONGBAO;
    DELETE FROM LICHSUGIA;
    
    -- Khởi tạo lại giá trị ban đầu cho LICHSUGIA từ bảng SANPHAM để đồng bộ
    FOR r IN (SELECT MaSP, GiaMua, GiaBan FROM SANPHAM) LOOP
        INSERT INTO LICHSUGIA (MaSP, GiaMua, GiaBan, TGApDung)
        VALUES (r.MaSP, r.GiaMua, r.GiaBan, TO_DATE('2025-10-15', 'YYYY-MM-DD'));
    END LOOP;
    
    -- Lặp qua 7 tháng (11/2025 - 05/2026, tương ứng offset 0..6)
    FOR v_month_offset IN 0..6 LOOP
        v_month_date := ADD_MONTHS(TO_DATE('2025-11-01', 'YYYY-MM-DD'), v_month_offset);
        v_month_start := TRUNC(v_month_date, 'MM');
        v_month_end := LAST_DAY(v_month_date);
        v_days_in_month := TO_NUMBER(TO_CHAR(v_month_end, 'DD'));
        
        -- =================================================================
        -- 1. BIẾN ĐỘNG GIÁ SẢN PHẨM TRONG THÁNG
        -- =================================================================
        -- 1.1 Sản phẩm SP000001 (Thịt heo Iberico) biến động giá 4 lần/tháng (vào ngày 3, 10, 17, 24)
        P_UPDATE_PRICE_SIMULATED('SP000001', v_month_start + 2);   -- Ngày 3
        P_UPDATE_PRICE_SIMULATED('SP000001', v_month_start + 9);   -- Ngày 10
        P_UPDATE_PRICE_SIMULATED('SP000001', v_month_start + 16);  -- Ngày 17
        P_UPDATE_PRICE_SIMULATED('SP000001', v_month_start + 23);  -- Ngày 24
        
        -- 1.2 Mỗi danh mục sản phẩm (LSP00001 - LSP00005) đều có biến động giá 2 lần/tháng
        -- Lần 1 vào ngày 5 (các sản phẩm thuộc v_sp_week1)
        FOR i IN 1..v_sp_week1.COUNT LOOP
            P_UPDATE_PRICE_SIMULATED(v_sp_week1(i), v_month_start + 4);
        END LOOP;
        
        -- Lần 2 vào ngày 20 (các sản phẩm thuộc v_sp_week3)
        FOR i IN 1..v_sp_week3.COUNT LOOP
            P_UPDATE_PRICE_SIMULATED(v_sp_week3(i), v_month_start + 19);
        END LOOP;

        -- =================================================================
        -- 2. NHẬP HÀNG (LOHANG, CHITIETLOHANG)
        -- =================================================================
        -- 6 đợt nhập hàng mỗi tháng vào các ngày 3, 8, 13, 18, 23, 28 (ít nhất 1 lần/tuần)
        FOR d IN 1..6 LOOP
            v_day_offset := CASE d
                WHEN 1 THEN 2   -- Ngày 3
                WHEN 2 THEN 7   -- Ngày 8
                WHEN 3 THEN 12  -- Ngày 13
                WHEN 4 THEN 17  -- Ngày 18
                WHEN 5 THEN 22  -- Ngày 23
                ELSE 27         -- Ngày 28
            END;
            v_simulated_date := v_month_start + v_day_offset;
            
            SELECT MaNCC INTO v_MaNCC FROM (SELECT MaNCC FROM NHACUNGCAP WHERE TrangThaiHopTac = 1 ORDER BY DBMS_RANDOM.VALUE) WHERE ROWNUM = 1;
            SELECT MaNV INTO v_MaNV_ThuMua FROM (SELECT MaNV FROM NHANVIEN WHERE ChucVu = 'NV thu mua' ORDER BY DBMS_RANDOM.VALUE) WHERE ROWNUM = 1;
            
            v_MaLH := 'LH' || LPAD(SEQ_LOHANG.NEXTVAL, 6, '0');
            
            INSERT INTO LOHANG (MaLH, MaNCC, MaNV, TGNhap, TrangThaiLH)
            VALUES (v_MaLH, v_MaNCC, v_MaNV_ThuMua, v_simulated_date, 'Chờ kiểm duyệt');
            
            -- Số lượng sản phẩm trong lô hàng:
            -- 1-2 SP (đợt 1, 2, 4, 5) hoặc 3-4 SP (đợt 3, 6) -> phần lớn là 1-2 sản phẩm
            IF d IN (1, 2, 4, 5) THEN
                v_num_items := TRUNC(DBMS_RANDOM.VALUE(1, 3)); -- 1 hoặc 2
            ELSE
                v_num_items := TRUNC(DBMS_RANDOM.VALUE(3, 5)); -- 3 hoặc 4
            END IF;
            
            DECLARE
                TYPE t_sp_temp IS TABLE OF VARCHAR2(10);
                v_used_sps t_sp_temp := t_sp_temp();
            BEGIN
                FOR j IN 1..v_num_items LOOP
                    -- Chọn sản phẩm tuần tự (round-robin) từ danh sách 25 sản phẩm để đảm bảo nạp đều tất cả sản phẩm
                    v_MaSP := v_all_products(v_import_sp_idx);
                    v_import_sp_idx := MOD(v_import_sp_idx, 25) + 1;
                    
                    v_SoLuong_import := TRUNC(DBMS_RANDOM.VALUE(1500, 3501)); -- 1500 đến 3500 đơn vị (B2B nhập)
                    
                    INSERT INTO CHITIETLOHANG (MaLH, MaSP, SoLuong)
                    VALUES (v_MaLH, v_MaSP, v_SoLuong_import);
                    
                    -- Xác định kho phù hợp với tính chất bảo quản của sản phẩm
                    SELECT BaoQuan INTO v_BaoQuan FROM SANPHAM WHERE MaSP = v_MaSP;
                    SELECT MaKho INTO v_MaKho FROM (SELECT MaKho FROM KHO WHERE LoaiKho = v_BaoQuan ORDER BY DBMS_RANDOM.VALUE) WHERE ROWNUM = 1;
                    
                    -- Kiểm tra trạng thái lô hàng phù hợp theo dòng thời gian
                    -- Tháng 11/2025 -> 4/2026 và các đợt 1,2,3 của tháng 5/2026 sẽ được nhập kho đầy đủ
                    IF v_month_date < TO_DATE('2026-05-01', 'YYYY-MM-DD') OR d <= 3 THEN
                        -- Xác nhận nhập kho bằng procedure (sinh bản ghi TONKHO)
                        SP_XACNHAN_NHAPKHO(v_MaLH, v_MaSP, v_MaKho, v_simulated_date + 365, 'Kệ A' || TRUNC(DBMS_RANDOM.VALUE(1, 11)));
                        -- Cập nhật ngày nhập kho của TONKHO thành ngày mô phỏng
                        UPDATE TONKHO SET TGNhapKho = v_simulated_date WHERE MaLH = v_MaLH AND MaSP = v_MaSP;
                    END IF;
                END LOOP;
                
                -- Đối với tháng 5/2026 (tháng hiện tại), phân bổ trạng thái LOHANG phù hợp:
                IF v_month_date >= TO_DATE('2026-05-01', 'YYYY-MM-DD') THEN
                    IF d = 4 THEN
                        -- Đợt 4: chuyển sang trạng thái "Chờ nhập kho"
                        SP_YEUCAU_NHAPKHO(v_MaLH);
                    -- Đợt 5 và 6 giữ trạng thái "Chờ kiểm duyệt"
                    END IF;
                END IF;
            END;
        END LOOP;

        -- =================================================================
        -- 3. BÁN HÀNG (DONHANG, CHITIETDONHANG)
        -- =================================================================
        -- Sinh 14 đơn hàng mỗi tháng trải đều các ngày (khoảng 10-20 lần bán, ít nhất 2 lần/tuần)
        FOR j IN 1..14 LOOP
            -- Trải đều các ngày trong tháng (ít nhất 2 lần/tuần)
            v_day_offset := TRUNC(((j - 1) / 14) * v_days_in_month) + 1;
            IF v_day_offset > v_days_in_month THEN v_day_offset := v_days_in_month; END IF;
            v_simulated_date := v_month_start + (v_day_offset - 1);
            
            -- Chọn khách hàng ngẫu nhiên
            SELECT MaKH INTO v_MaKH FROM (SELECT MaKH FROM KHACHHANG ORDER BY DBMS_RANDOM.VALUE) WHERE ROWNUM = 1;
            -- Chọn nhân viên giao hàng ngẫu nhiên
            SELECT MaNV INTO v_MaNV_GiaoHang FROM (SELECT MaNV FROM NHANVIEN WHERE ChucVu = 'NV giao hàng' ORDER BY DBMS_RANDOM.VALUE) WHERE ROWNUM = 1;
            
            -- Xác định phương thức thanh toán: 60% Ghi nợ, 40% phương thức khác
            IF j IN (1, 3, 5, 7, 9, 11, 13) THEN
                v_PhuongThucTT := N'Ghi nợ';
            ELSE
                v_PhuongThucTT := CASE TRUNC(DBMS_RANDOM.VALUE(0, 3))
                    WHEN 0 THEN N'COD'
                    WHEN 1 THEN N'Chuyển khoản'
                    ELSE N'Ví điện tử'
                END;
            END IF;
            
            v_MaDH := 'DH' || LPAD(SEQ_DONHANG.NEXTVAL, 6, '0');
            v_DiaChiGiao := N'TP.HCM - Quận ' || TRUNC(DBMS_RANDOM.VALUE(1, 13));
            
            -- Khởi tạo đơn hàng với trạng thái 'Đã đặt'
            INSERT INTO DONHANG (MaDH, MaKH, MaNV, TGDat, TGGiaoYC, DiaChiGiaoHang, TrangThaiDH, TrangThaiTT, PhuongThucTT)
            VALUES (v_MaDH, v_MaKH, v_MaNV_GiaoHang, v_simulated_date, v_simulated_date + 2, v_DiaChiGiao, 'Đã đặt', 0, v_PhuongThucTT);
            
            -- Xác định số lượng sản phẩm trong đơn hàng:
            -- 1-2 SP (15%), 3-5 SP (65%), 6-10 SP (20%) -> phần lớn từ 3-5 sản phẩm
            v_num_items := CASE
                WHEN j IN (1, 8) THEN TRUNC(DBMS_RANDOM.VALUE(1, 3))       -- 1 đến 2 sản phẩm
                WHEN j IN (2, 3, 5, 6, 7, 10, 11, 12, 13) THEN TRUNC(DBMS_RANDOM.VALUE(3, 6)) -- 3 đến 5 sản phẩm
                ELSE TRUNC(DBMS_RANDOM.VALUE(6, 11))                       -- 6 đến 10 sản phẩm
            END;
            
            DECLARE
                TYPE t_sp_temp IS TABLE OF VARCHAR2(10);
                v_used_sps t_sp_temp := t_sp_temp();
                v_is_sp_dup BOOLEAN;
            BEGIN
                FOR k IN 1..v_num_items LOOP
                    -- Đảm bảo không trùng lặp sản phẩm trong một đơn hàng
                    LOOP
                        v_MaSP := v_all_products(TRUNC(DBMS_RANDOM.VALUE(1, 26)));
                        v_is_sp_dup := FALSE;
                        IF v_used_sps.COUNT > 0 THEN
                            FOR idx IN 1..v_used_sps.COUNT LOOP
                                IF v_used_sps(idx) = v_MaSP THEN
                                    v_is_sp_dup := TRUE;
                                    EXIT;
                                END IF;
                            END LOOP;
                        END IF;
                        EXIT WHEN NOT v_is_sp_dup;
                    END LOOP;
                    v_used_sps.EXTEND;
                    v_used_sps(v_used_sps.COUNT) := v_MaSP;
                    
                    -- Số lượng bán B2B hợp lý: vài chục kg (từ 20 đến 120 kg/sp) và ít hơn nhiều so với lượng nhập lô hàng
                    v_SoLuong_export := TRUNC(DBMS_RANDOM.VALUE(20, 121));
                    
                    INSERT INTO CHITIETDONHANG (MaDH, MaSP, SoLuong)
                    VALUES (v_MaDH, v_MaSP, v_SoLuong_export);
                END LOOP;
            END;
            
            -- Chạy nghiệp vụ tạo yêu cầu xuất kho (Tạm giữ tồn kho và điền bảng XUATKHO)
            BEGIN
                SP_YEUCAU_XUATKHO(v_MaDH);
                -- Đồng bộ thời gian trong XUATKHO
                UPDATE XUATKHO SET TGCapNhat = v_simulated_date WHERE MaDH = v_MaDH;
            EXCEPTION 
                WHEN OTHERS THEN 
                    DBMS_OUTPUT.PUT_LINE('Lỗi tạo yêu cầu xuất kho ' || v_MaDH || ': ' || SQLERRM);
            END;
            
            -- Phân bổ trạng thái đơn hàng theo dòng thời gian:
            -- 1. Với các tháng trước tháng 5/2026 (Quá khứ hoàn tất):
            IF v_month_date < TO_DATE('2026-05-01', 'YYYY-MM-DD') THEN
                BEGIN
                    -- Xác nhận xuất kho (chuyển XUATKHO thành "Đã xuất", trừ tồn kho thực tế, và đổi TrangThaiDH = "Chờ giao hàng")
                    SP_XACNHAN_XUATKHO(v_MaDH, v_MaNV_GiaoHang);
                    
                    -- Đổi trạng thái sang "Đang giao"
                    SP_XACNHAN_GIAOHANG(v_MaDH, v_MaNV_GiaoHang);
                    
                    -- Xác nhận giao thành công
                    SP_GIAOHANG_THANHCONG(v_MaDH, v_MaNV_GiaoHang);
                    
                    -- Xử lý cụ thể đối với đơn hàng Ghi nợ:
                    -- 70% đã thanh toán (Hoàn thành, TrangThaiTT = 1), 30% vẫn nợ (Chờ thanh toán, TrangThaiTT = 0)
                    IF v_PhuongThucTT = N'Ghi nợ' THEN
                        IF j IN (1, 5, 9, 13) THEN
                            -- Chuyển trạng thái nợ thành đã thanh toán thành công
                            UPDATE DONHANG 
                            SET TrangThaiTT = 1, 
                                TrangThaiDH = N'Hoàn thành', 
                                TGGiaoTT = v_simulated_date + 1 
                            WHERE MaDH = v_MaDH;
                        ELSE
                            -- Giữ nguyên trạng thái chưa thanh toán (Chờ thanh toán)
                            UPDATE DONHANG 
                            SET TrangThaiTT = 0, 
                                TrangThaiDH = N'Chờ thanh toán', 
                                TGGiaoTT = v_simulated_date + 1 
                            WHERE MaDH = v_MaDH;
                        END IF;
                    ELSE
                        -- Các đơn thường thì đã hoàn thành và thanh toán
                        UPDATE DONHANG 
                        SET TGGiaoTT = v_simulated_date + 1 
                        WHERE MaDH = v_MaDH;
                    END IF;
                    
                    -- Đồng bộ hóa ngày đặt, giao yêu cầu và giao thực tế
                    UPDATE DONHANG 
                    SET TGDat = v_simulated_date,
                        TGGiaoYC = v_simulated_date + 2
                    WHERE MaDH = v_MaDH;
                    
                    UPDATE XUATKHO SET TGCapNhat = v_simulated_date WHERE MaDH = v_MaDH;
                    
                EXCEPTION WHEN OTHERS THEN NULL;
                END;
            
            -- 2. Đối với tháng 5/2026 (Mô phỏng động cho tháng hiện tại):
            ELSE
                BEGIN
                    IF j <= 6 THEN
                        -- Đơn 1 đến 6: Đã giao thành công (Hoàn thành / Chờ thanh toán)
                        SP_XACNHAN_XUATKHO(v_MaDH, v_MaNV_GiaoHang);
                        SP_XACNHAN_GIAOHANG(v_MaDH, v_MaNV_GiaoHang);
                        SP_GIAOHANG_THANHCONG(v_MaDH, v_MaNV_GiaoHang);
                        IF v_PhuongThucTT = N'Ghi nợ' AND j IN (1, 5) THEN
                            UPDATE DONHANG SET TrangThaiTT = 1, TrangThaiDH = N'Hoàn thành', TGGiaoTT = v_simulated_date + 1 WHERE MaDH = v_MaDH;
                        ELSE
                            UPDATE DONHANG SET TGGiaoTT = v_simulated_date + 1 WHERE MaDH = v_MaDH;
                        END IF;
                        
                    ELSIF j IN (7, 8) THEN
                        -- Đơn 7 và 8: Đang giao hàng
                        SP_XACNHAN_XUATKHO(v_MaDH, v_MaNV_GiaoHang);
                        SP_XACNHAN_GIAOHANG(v_MaDH, v_MaNV_GiaoHang);
                        
                    ELSIF j IN (9, 10) THEN
                        -- Đơn 9 và 10: Chờ giao hàng (đã xuất kho nhưng shipper chưa nhận)
                        SP_XACNHAN_XUATKHO(v_MaDH, v_MaNV_GiaoHang);
                        
                    ELSIF j IN (11, 12) THEN
                        -- Đơn 11 và 12: Đặt hàng thành công, đang giữ kho tạm thời (Đã đặt) -> XUATKHO giữ trạng thái 'Tạm giữ'
                        NULL;
                        
                    ELSIF j = 13 THEN
                        -- Đơn 13: Đã hủy đơn hàng (hoàn lại kho qua SP_HUY_DH)
                        SP_HUY_DH(v_MaDH, N'Khách hàng báo hủy do thay đổi kế hoạch sản xuất B2B');
                        
                    ELSIF j = 14 THEN
                        -- Đơn 14: Giữ nguyên trạng thái mới đặt (Đã đặt)
                        NULL;
                    END IF;
                    
                    -- Đồng bộ hóa ngày đặt, giao yêu cầu và giao thực tế
                    UPDATE DONHANG 
                    SET TGDat = v_simulated_date,
                        TGGiaoYC = v_simulated_date + 2
                    WHERE MaDH = v_MaDH;
                    
                    UPDATE XUATKHO SET TGCapNhat = v_simulated_date WHERE MaDH = v_MaDH;
                EXCEPTION WHEN OTHERS THEN NULL;
                END;
            END IF;
            
        END LOOP;
    END LOOP;
    
    COMMIT;
    DBMS_OUTPUT.PUT_LINE('HOÀN TẤT SINH DATA KÈM LỊCH SỬ BIẾN ĐỘNG GIÁ MUA & BÁN!');
EXCEPTION WHEN OTHERS THEN ROLLBACK; DBMS_OUTPUT.PUT_LINE('LỖI: ' || SQLERRM);
END;
/

-- ===== CÁC CÂU LỆNH TRUY VẤN XÁC MINH DỮ LIỆU SAU KHI CHẠY =====
PROMPT
PROMPT ====================================================================================
PROMPT                     KẾT QUẢ KIỂM TRA & XÁC MINH DỮ LIỆU DEMO
PROMPT ====================================================================================
PROMPT

-- 1. Tổng số lượng bản ghi trong các bảng giao dịch chính
SELECT 'LOHANG (Lô hàng)' AS "Bảng", COUNT(*) AS "Số bản ghi" FROM LOHANG
UNION ALL
SELECT 'CHITIETLOHANG (Chi tiết lô)', COUNT(*) FROM CHITIETLOHANG
UNION ALL
SELECT 'DONHANG (Đơn hàng)', COUNT(*) FROM DONHANG
UNION ALL
SELECT 'CHITIETDONHANG (Chi tiết đơn)', COUNT(*) FROM CHITIETDONHANG
UNION ALL
SELECT 'TONKHO (Lưu kho thực tế)', COUNT(*) FROM TONKHO
UNION ALL
SELECT 'XUATKHO (Xuất kho chi tiết)', COUNT(*) FROM XUATKHO
UNION ALL
SELECT 'LICHSUGIA (Biến động giá)', COUNT(*) FROM LICHSUGIA;

-- 2. Phân bổ các trạng thái đơn hàng (Xác nhận tính đa dạng và logic thời gian)
PROMPT
PROMPT ====================================================================================
PROMPT 2. Thống kê số lượng đơn hàng theo Trạng thái đơn và Trạng thái thanh toán:
PROMPT ====================================================================================
SELECT TrangThaiDH AS "Trạng thái Đơn", 
       TrangThaiTT AS "TT Thanh Toán", 
       PhuongThucTT AS "Phương thức", 
       COUNT(*) AS "Số đơn hàng", 
       SUM(TongTien) AS "Tổng doanh thu"
FROM DONHANG
GROUP BY TrangThaiDH, TrangThaiTT, PhuongThucTT
ORDER BY TrangThaiDH, TrangThaiTT;

-- 3. Phân bổ các trạng thái lô hàng nhập kho
PROMPT
PROMPT ====================================================================================
PROMPT 3. Thống kê số lượng lô hàng nhập theo trạng thái:
PROMPT ====================================================================================
SELECT TrangThaiLH AS "Trạng thái Lô hàng", COUNT(*) AS "Số lô hàng", SUM(TongTien) AS "Tổng giá trị nhập"
FROM LOHANG
GROUP BY TrangThaiLH;

-- 4. Xác minh tổng số lượng nhập kho của từng sản phẩm trong 25 sản phẩm
-- (Đảm bảo 100 < số lượng < 10000)
PROMPT
PROMPT ====================================================================================
PROMPT 4. Tổng số lượng nhập kho của từng sản phẩm trong 25 sản phẩm (Định mức 100 - 10000):
PROMPT ====================================================================================
SELECT sp.MaSP, sp.TenSP, NVL(SUM(ctl.SoLuong), 0) AS "Tổng SL Nhập"
FROM SANPHAM sp
LEFT JOIN CHITIETLOHANG ctl ON sp.MaSP = ctl.MaSP
GROUP BY sp.MaSP, sp.TenSP
ORDER BY sp.MaSP;

-- 5. Xác minh số lần biến động giá của sản phẩm đặc biệt SP000001 (chi tiết)
PROMPT
PROMPT ====================================================================================
PROMPT 5. Số lần biến động giá của sản phẩm chi tiết SP000001 (Yêu cầu ít nhất 4 lần/tháng):
PROMPT ====================================================================================
SELECT TO_CHAR(TGApDung, 'MM-YYYY') AS "Tháng-Năm", COUNT(*) AS "Số lần biến động"
FROM LICHSUGIA
WHERE MaSP = 'SP000001'
GROUP BY TO_CHAR(TGApDung, 'MM-YYYY')
ORDER BY TO_CHAR(TGApDung, 'MM-YYYY');

-- 6. Xác minh số lần biến động giá của các nhóm loại sản phẩm mỗi tháng
PROMPT
PROMPT ====================================================================================
PROMPT 6. Số lần biến động giá trung bình theo từng Loại Sản Phẩm mỗi tháng (Yêu cầu 1-2 lần/tháng):
PROMPT ====================================================================================
SELECT lsp.TenLSP AS "Loại Sản Phẩm", TO_CHAR(lsg.TGApDung, 'MM-YYYY') AS "Tháng-Năm", COUNT(*) AS "Số lần biến động"
FROM LICHSUGIA lsg
JOIN SANPHAM sp ON lsg.MaSP = sp.MaSP
JOIN LOAISANPHAM lsp ON sp.MaLSP = lsp.MaLSP
GROUP BY lsp.TenLSP, TO_CHAR(lsg.TGApDung, 'MM-YYYY')
ORDER BY TO_CHAR(lsg.TGApDung, 'MM-YYYY'), lsp.TenLSP;

-- 7. Xác minh tính nhất quán giữa tồn kho khả dụng và số lượng tạm giữ
PROMPT
PROMPT ====================================================================================
PROMPT 7. Kiểm tra tính đồng bộ giữa SL tồn kho thực tế, SL khả dụng và SL tạm giữ trong XUATKHO:
PROMPT ====================================================================================
SELECT tk.MaTonKho, tk.MaSP, sp.TenSP, tk.SLConLai AS "Tồn kho thực tế (ConLai)", tk.SLKhaDung AS "SL Khả dụng (KhaDung)",
       (tk.SLConLai - tk.SLKhaDung) AS "Chênh lệch (Tạm giữ)",
       NVL((SELECT SUM(SLXuat) FROM XUATKHO WHERE MaTonKho = tk.MaTonKho AND TrangThaiXK = 'Tạm giữ'), 0) AS "Tạm giữ ở XUATKHO"
FROM TONKHO tk
JOIN SANPHAM sp ON tk.MaSP = sp.MaSP
WHERE tk.SLConLai <> tk.SLKhaDung OR (tk.SLConLai - tk.SLKhaDung) <> 0
ORDER BY tk.MaTonKho;