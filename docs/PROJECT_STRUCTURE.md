# Cấu trúc dự án AgriSupplyChain
## Mô hình Hybrid
Dự án được thiết kế theo mô hình **Hybrid** (Kết hợp) giữa **3-Layer** để phân chia trách nhiệm hệ thống *(Presentation, Business, Data)* và **MVC** để quản lý luồng dữ liệu tại tầng giao diện *(Presentation)*.

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
        │   └── images/                 # Ảnh các sản phẩm...
        └── java/
            │   
            # --- TẦNG DATA ACCESS (DAL) ---
            ├── dto/                     # Data Transfer Object: Ánh xạ bảng CSDL
            │   ├── SanPhamDTO.java      
            │   └── DonHangDTO.java      
            │
            ├── dal/                     # Tương tác CSDL (Code gọi Procedure)
            │   ├── SanPhamDAL.java      
            │   └── DonHangDAL.java      
            │
            # --- TẦNG BUSINESS LOGIC (BLL) ---
            ├── bll/                     # Xử lý tính toán, gọi DAL, quản lý Transaction
            │   ├── SanPhamBLL.java      
            │   └── DonHangBLL.java      
            │
            # --- TẦNG PRESENTATION ---
            ├── view/                    # Chỉ chứa code GUI (JFrame, JButton, JTable)
            │   ├── LoginView.java
            │   └── DonHangView.java     
            │
            ├── controller/              # Lắng nghe sự kiện từ View, gọi xuống BLL
            │   ├── LoginController.java # Chứa ActionListener cho nút Đăng nhập
            │   └── DonHangController.java
            │
            # --- THÀNH PHẦN KHÁC ---
            ├── util/                    # Tiện ích dùng chung
            │   ├── DBConnection.java    # File cấu hình kết nối Oracle
            │   └── Session.java
            │
            └── Main.java     # Chứa public static void main(), khởi tạo View và Controller (Entry Point)
</pre>