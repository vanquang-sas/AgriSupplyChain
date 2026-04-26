-- ====================================================================================
-- PHẦN 1: THIẾT LẬP MÔI TRƯỜNG VÀ KHỞI TẠO USER
-- Lưu ý: Script này phải được chạy bằng quyền SYSDBA
-- ====================================================================================
SET FEEDBACK ON;
SET ECHO ON;
SET SERVEROUTPUT ON;

PROMPT --- DỌN DẸP VÀ TẠO MỚI USER AGRIAPP ---
BEGIN
   -- Xóa user cũ và toàn bộ dữ liệu đi kèm (CASCADE)
   EXECUTE IMMEDIATE 'DROP USER AGRIAPP CASCADE';
EXCEPTION
   WHEN OTHERS THEN
      IF SQLCODE != -1918 THEN -- Nếu không phải lỗi "User không tồn tại" thì báo lỗi
         RAISE;
      END IF;
END;
/

-- Tạo user với mật khẩu từ DBConnection.java
CREATE USER AGRIAPP IDENTIFIED BY 123456;

-- Cấp các quyền cần thiết để Java có thể thao tác
GRANT CONNECT, RESOURCE, CREATE VIEW TO AGRIAPP;
GRANT UNLIMITED TABLESPACE TO AGRIAPP;

PROMPT --- CHUYỂN ĐỔI KẾT NỐI SANG USER AGRIAPP ---
-- Sau lệnh này, mọi bảng và procedure bên dưới sẽ thuộc về AGRIAPP
CONNECT AGRIAPP/123456;

-- ====================================================================================
-- PHẦN 2: TẠO CẤU TRÚC DATABASE (TABLES, SEQUENCES, LOGIC)
-- ====================================================================================
SPOOL install_log.txt;

PROMPT --- ĐANG CHẠY FILE 1: TẠO BẢNG VÀ CONSTRAINT ---
@@01_Tables.sql;

PROMPT --- ĐANG CHẠY FILE 2: TẠO SEQUENCE ---
@@02_Sequences.sql;

PROMPT --- ĐANG CHẠY FILE 3: TẠO FUNCTION ---
@@03_Functions.sql;

PROMPT --- ĐANG CHẠY FILE 4: TẠO TRIGGER ---
@@04_Triggers.sql;

PROMPT --- ĐANG CHẠY FILE 5: TẠO PROCEDURE ---
@@05_Procedures.sql;

PROMPT --- ĐANG CHẠY FILE 6: THÊM DỮ LIỆU CẦN THIẾT ---
@@06_InsertData.sql;

PROMPT --- ĐANG CHẠY FILE 7: THÊM DỮ LIỆU MẪU ---
@@07_InsertDemoData.sql;

SPOOL OFF;
PROMPT --- TẤT CẢ ĐÃ SẴN SÀNG ĐỂ KẾT NỐI VỚI JAVA ---
EXIT;