# Cấu trúc dự án AgriSupplyChain

## Mô hình 3 Lớp (3-Tier Architecture)
Dự án được thiết kế theo mô hình **3 Lớp** tiêu chuẩn. Hệ thống được phân chia thành 3 tầng độc lập để dễ dàng quản lý code, phân chia công việc và bảo trì: **Presentation Layer** (Giao diện), **Business Logic Layer** (Xử lý nghiệp vụ), và **Data Access Layer** (Truy cập dữ liệu).

## Cây cấu trúc
<pre>
AgriSupplyChain/
├── pom.xml
├── .gitignore
├── database/                         # Chứa các file .sql 
└── src/
    └── main/
        ├── resources/
        │   ├── icons/                  # Ảnh icon các tính năng...
        │   └── images/                 # Ảnh minh họa nông sản...
        └── java/
            │   
            # --- TẦNG DATA ACCESS (DAL) ---
            ├── dto/                     # Data Transfer Object: Ánh xạ các thực thể từ Oracle Database
            │   ├── SanPhamDTO.java      
            │   └── DonHangDTO.java      
            │
            ├── dao/                     # Chứa code JDBC, thực thi truy vấn hoặc gọi Procedure từ DB
            │   ├── SanPhamDAO.java      
            │   └── DonHangDAO.java      
            │
            # --- TẦNG BUSINESS LOGIC (BLL) ---
            ├── bus/                     # Xử lý logic của hệ thống: Tính toán giá sàn, xử lý nghiệp vụ, gọi DAL
            │   ├── SanPhamBUS.java      
            │   └── DonHangBUS.java      
            │
            # --- TẦNG PRESENTATION (GUI) ---
            ├── gui/                     # Chứa code giao diện (JFrame/JPanel) VÀ Xử lý sự kiện (ActionListener)
            │   ├── LoginGUI.java        # Giao diện đăng nhập và code bắt sự kiện nút "Đăng nhập"
            │   └── DonHangGUI.java      # Giao diện quản lý hợp đồng/đơn hàng
            │
            # --- TEST ---
            ├── test/                    # Test các chức năng của hệ thống
            │
            # --- THÀNH PHẦN KHÁC ---
            ├── util/                    # Các lớp hỗ trợ dùng chung toàn hệ thống
            │   ├── DBConnection.java    # Quản lý kết nối Oracle 
            │   └── Session.java         # Lưu trữ phiên làm việc (Người dùng đang đăng nhập)
            │
            └── Main.java     # Entry Point: Hàm main() khởi chạy giao diện đầu tiên của ứng dụng
</pre>