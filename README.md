# Hệ thống Quản lý Chuỗi Cung ứng Nông sản (AgriSupplyChain)

## 📌 Liên kết Dự án

* **Repository URL:** `https://github.com/vanquang-sas/AgriSupplyChain.git`

---

## 🛠 HƯỚNG DẪN CÀI ĐẶT VÀ KHỞI CHẠY DỰ ÁN

Tài liệu này hướng dẫn chi tiết các bước cài đặt và vận hành hệ thống Quản lý chuỗi cung ứng Nông sản (AgriSupplyChain) từ lúc tải mã nguồn trên GitHub, cấu hình & khởi tạo cơ sở dữ liệu Oracle Database, cho đến các bước import và chạy chương trình trên 2 môi trường phát triển phổ biến là **IntelliJ IDEA** và **Visual Studio Code (VS Code)**.

---

### PHẦN 1: TẢI MÃ NGUỒN TỪ GITHUB (CLONE REPOSITORY)

Để tải mã nguồn từ kho lưu trữ GitHub về máy tính cá nhân, bạn thực hiện lần lượt các bước sau:

#### Bước 1.1: Tải và cài đặt công cụ Git

1. Truy cập vào trang chủ chính thức của Git: `https://git-scm.com/downloads`.
2. Chọn phiên bản dành cho hệ điều hành của bạn (ví dụ: **Windows**).
3. Nhấp đúp vào tệp cài đặt `.exe` vừa tải xuống.
4. Nhấn **Next** xuyên suốt quá trình cài đặt để chấp nhận các thiết lập cấu hình mặc định đề xuất, sau đó nhấn **Install** để hoàn tất.

#### Bước 1.2: Xác minh cài đặt Git thành công

1. Mở cửa sổ dòng lệnh **Command Prompt (cmd)** hoặc **PowerShell** trên Windows.
2. Gõ lệnh sau và nhấn Enter:
```bash
git --version

```


3. Nếu hệ thống trả về phiên bản của Git (ví dụ: `git version 2.x.x`), nghĩa là bạn đã cài đặt thành công.

#### Bước 1.3: Tải mã nguồn về máy (Clone Repository)

1. Hãy tìm hoặc tạo một thư mục trống trên máy tính nơi bạn muốn lưu trữ dự án (ví dụ: `C:\Users\Quang\Desktop\code\Java`).
2. Nhấp chuột phải vào khoảng trống bên trong thư mục đó và chọn **Git Bash Here** từ menu ngữ cảnh.
3. Trong cửa sổ lệnh Git Bash vừa xuất hiện, sao chép và nhập vào dòng lệnh sau, rồi nhấn Enter:
```bash
git clone https://github.com/vanquang-sas/AgriSupplyChain.git

```


4. Khi thấy một thư mục mới có tên `AgriSupplyChain` xuất hiện trong thư mục làm việc đã chọn thì là đã cài xong.

---

### PHẦN 2: CẤU HÌNH CƠ SỞ DỮ LIỆU (ORACLE DATABASE)

Dự án sử dụng hệ quản trị cơ sở dữ liệu Oracle Database. Các script khởi tạo hệ thống đã được tổ chức sẵn trong thư mục `database/` của mã nguồn dự án.

Để cấu hình, bạn cần thực thi các tệp tin SQL theo đúng thứ tự thiết kế nhằm đảm bảo tính toàn vẹn dữ liệu:

1. **Khởi tạo cấu trúc bảng:** Chạy file `01_Tables.sql` để tạo toàn bộ hệ thống bảng dữ liệu và ràng buộc khóa chính/khóa ngoại.
2. **Khởi tạo chuỗi tự động tăng:** Chạy file `02_Sequences.sql` để thiết lập các sequence quản lý mã ID tự sinh.
3. **Cài đặt logic nghiệp vụ hệ thống:** Lần lượt thực thi các file chức năng, bao gồm:
* `03_Functions.sql` (Hàm tính toán)
* `04_Triggers.sql` (Kích hoạt tự động)
* `05_Procedures.sql` (Thủ tục lưu trữ)


4. **Import dữ liệu nền tảng và dữ liệu mẫu:**
* Chạy file `06_InsertData.sql` để nạp các tham số, cấu hình hệ thống cốt lõi.
* Chạy file `07_InsertDemoData.sql` để chuẩn bị sẵn dữ liệu thử nghiệm phục vụ kiểm thử luồng nghiệp vụ.


5. **Thiết lập giao diện báo cáo và tác vụ tự động:**
* Chạy file `08_Views.sql` để khởi tạo các khung nhìn tổng hợp thông tin, báo cáo thống kê.
* Chạy file `09_Schedulers.sql` để kích hoạt bộ lập lịch tự động kiểm tra trạng thái và cập nhật định kỳ.



*(Mẹo: Bạn có thể sử dụng file tổng hợp `00_RunAll.sql` thông qua công cụ dòng lệnh SQL*Plus hoặc công cụ quản lý trực quan Oracle SQL Developer để tự động thực thi chuỗi tệp tin theo đúng thứ tự trên).*

---

### PHẦN 3: CÀI ĐẶT VÀ CHẠY ỨNG DỤNG TRÊN IDE

Dự án AgriSupplyChain được xây dựng dưới dạng một dự án chuẩn **Maven**, quản lý thư viện tập trung thông qua tệp `pom.xml`. Bạn có thể lựa chọn chạy ứng dụng trên **IntelliJ IDEA** hoặc **Visual Studio Code**.

#### CÁCH 1: SỬ DỤNG LẬP TRÌNH TRÊN INTELIJ IDEA

##### 1. Nhập (Import) dự án vào IntelliJ:

1. Khởi động phần mềm **IntelliJ IDEA** (khuyến nghị phiên bản Community hoặc Ultimate từ 2023 trở lên).
2. Tại màn hình chào mừng (Welcome), nhấp chọn nút **Open** (hoặc truy cập `File > Open...` nếu đang mở một dự án khác).
3. Duyệt tìm đến đường dẫn thư mục gốc của dự án vừa clone (ví dụ: `C:\Users\Quang\Desktop\code\Java\AgriSupplyChain`), chọn thư mục này hoặc click trực tiếp vào file `pom.xml`, sau đó nhấn **OK**.
4. Chọn chế độ **Open as Project**. Nếu có thông báo bảo mật xuất hiện, chọn **Trust Project**.

##### 2. Tải các thư viện phụ thuộc (Dependencies) qua Maven:

1. Sau khi mở dự án, IntelliJ IDEA sẽ tự động phát hiện tệp cấu hình cấu trúc `pom.xml` và tiến hành tải các thư viện driver kết nối Oracle JDBC cũng như các công cụ bổ trợ khác từ kho lưu trữ trung tâm Maven Central.
2. Hãy quan sát thanh tiến trình chạy ở góc dưới bên phải màn hình. Chờ cho đến khi tiến trình "Importing Maven project" hoàn tất biến mất.
3. *(Trường hợp IDE không tự động tải, bạn có thể click vào biểu tượng Maven ở thanh công cụ dọc ngoài cùng bên phải, sau đó nhấn vào biểu tượng vòng xoáy mũi tên "Reload All Maven Projects").*

##### 3. Khởi chạy ứng dụng:

1. Tại cây thư mục quản lý dự án bên trái (`Project Web View`), bạn mở rộng thư mục theo đường dẫn chính xác: `src > main > java`.
2. Tìm và nhấp đúp để mở tệp tin mã nguồn có tên là `Main.java`.
3. Bên trong file `Main.java`, di chuyển đến hàm khởi chạy `public static void main(String[] args)`.
4. Nhấp chuột vào biểu tượng mũi tên màu xanh lá cây nằm ngay sát bên cạnh dòng khai báo tên hàm `main` và chọn **Run 'Main.main()'**.
5. Chương trình sẽ tiến hành biên dịch (compile) mã nguồn Java sang bytecode và kích hoạt giao diện đồ họa đồ án của bạn lên màn hình.

---

#### CÁCH 2: SỬ DỤNG LẬP TRÌNH TRÊN VISUAL STUDIO CODE (VS CODE)

##### 1. Cài đặt môi trường phát triển Java cho VS Code:

1. Khởi động công cụ phần mềm **Visual Studio Code**.
2. Truy cập vào mục quản lý tiện ích mở rộng **Extensions** bằng cách nhấp biểu tượng các ô vuông ở thanh dọc bên trái (hoặc nhấn tổ hợp phím `Ctrl+Shift+X`).
3. Nhập từ khóa `"Extension Pack for Java"` (gói tiện ích mở rộng chính thức được cung cấp bởi **Microsoft**) vào thanh tìm kiếm và nhấn nút **Install** để cài đặt. Tiện ích này sẽ tự động cài thêm các công cụ quản lý Maven, trình gỡ lỗi Java Debugger và trình hỗ trợ cú pháp.

##### 2. Mở dự án trên VS Code:

1. Truy cập `File > Open Folder...` từ thanh menu trên cùng, duyệt tìm đến thư mục gốc của dự án `C:\Users\Quang\Desktop\code\Java\AgriSupplyChain` và nhấn **Select Folder** để mở dự án.
2. Sau khi mở thư mục, VS Code sẽ tự động khởi chạy máy chủ ngôn ngữ *Java Language Server* để quét cấu trúc mã nguồn và tệp cấu hình Maven `pom.xml`. Hãy kiên nhẫn đợi từ 1 - 2 phút để thanh tiến trạng thái ở góc dưới cùng hoàn tất tiến trình phân tích.

##### 3. Khởi chạy ứng dụng:

1. Truy cập vào tệp tin khởi chạy chính theo đường dẫn: `src / main / java / Main.java`.
2. Mở file `Main.java`. Bạn sẽ thấy các chữ text nhỏ mờ xuất hiện ngay phía trên tên hàm `main` là **Run** và **Debug** (đây là tính năng CodeLens của Microsoft Java Extension).
3. Click chuột vào chữ **Run** để khởi chạy chương trình ngay lập tức.
4. Hoặc bạn có thể nhấn phím **F5** trên bàn phím để khởi động ứng dụng với trình gỡ lỗi.
