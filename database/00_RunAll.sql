-- ====================================================================================
-- PHẦN 1: THIẾT LẬP MÔI TRƯỜNG VÀ KHỞI TẠO USER
-- Lưu ý: Script này phải được chạy bằng quyền SYSDBA
-- ====================================================================================
SET FEEDBACK ON;
SET ECHO ON;
SET SERVEROUTPUT ON;

-- 1. Bỏ qua kiểm tra bảo mật khắt khe của Oracle 21c
ALTER SESSION SET "_ORACLE_SCRIPT"=true; 

PROMPT --- DỌN DẸP USER CU ---
-- 2. Xóa user cũ. 
DROP USER AGRIAPP CASCADE;

PROMPT --- TAO USER MOI AGRIAPP ---
-- 3. Tạo lại user và cấp quyền
CREATE USER AGRIAPP IDENTIFIED BY 123456;
GRANT CONNECT, RESOURCE, CREATE VIEW TO AGRIAPP;
GRANT UNLIMITED TABLESPACE TO AGRIAPP;

PROMPT --- CHUYEN DOI KET NOI ---
-- 4. BẮT BUỘC: Phải có @localhost:1521/orcldb thì mới kết nối thành công
CONNECT AGRIAPP/123456@localhost:1521/orcldb;

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