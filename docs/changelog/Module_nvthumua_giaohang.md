### Trần Nhụy Tam Tử Phục - 13/05/2026 - feature/login-auth
- **Cập nhật:**
  - `src/main/java/gui/MainFrame.java`: Tách logic hiển thị Menu Sidebar để phân quyền độc lập cho 2 nhóm nhân viên:
    - `NV Thu mua`: Chỉ hiển thị nút `Quản lý Nhà cung cấp` và `Quản lý Sản phẩm`.
    - `NV Giao hàng`: Chỉ hiển thị nút `Phân hệ Giao hàng`.
  - **Kết quả đầu ra:** Khi đăng nhập bằng tài khoản Thu mua hoặc Giao hàng, UI Sidebar chỉ render ra các nút chức năng thuộc phạm vi công việc của nhân sự đó. Các chức năng không liên quan đã bị ẩn đi hoàn toàn và an toàn, đảm bảo không phá vỡ UI gốc cũng như không ảnh hưởng đến bất kỳ API hay kết nối CSDL nào.
