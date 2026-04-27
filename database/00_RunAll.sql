-- ====================================================================================
-- PHẦN 1: THIẾT LẬP MÔI TRƯỜNG VÀ KHỞI TẠO USER
-- Lưu ý: Script này phải được chạy bằng quyền SYSDBA (VD: user SYS)
-- ====================================================================================
SET FEEDBACK ON;
SET ECHO ON;
SET SERVEROUTPUT ON;

-- Nhảy vào CSDL con (orclpdb)
ALTER SESSION SET CONTAINER = orclpdb;

PROMPT --- DON DEP USER CU ---
-- Xóa user cũ và toàn bộ dữ liệu. Dùng BEGIN..END để bỏ qua lỗi nếu user chưa tồn tại.
BEGIN
   EXECUTE IMMEDIATE 'DROP USER AGRIAPP CASCADE';
EXCEPTION
   WHEN OTHERS THEN
      IF SQLCODE != -1918 THEN -- Lỗi -1918 là user không tồn tại
         RAISE;
      END IF;
END;
/

PROMPT --- TAO USER MOI AGRIAPP ---
-- Tạo lại user và cấp quyền
CREATE USER AGRIAPP IDENTIFIED BY 123456;
GRANT CREATE SESSION TO AGRIAPP;
GRANT CREATE TABLE TO AGRIAPP;
GRANT CREATE VIEW TO AGRIAPP;
GRANT CREATE SEQUENCE TO AGRIAPP;
GRANT CREATE PROCEDURE TO AGRIAPP;
GRANT CREATE TRIGGER TO AGRIAPP;
GRANT UNLIMITED TABLESPACE TO AGRIAPP;

PROMPT --- CHUYEN DOI KET NOI ---
-- 4. Chuyển kết nối sang user vừa tạo để bắt đầu chạy script tạo bảng
CONNECT AGRIAPP/123456@localhost:1521/orclpdb;

-- ====================================================================================
-- PHẦN 2: TẠO CẤU TRÚC DATABASE (TABLES, SEQUENCES, LOGIC)
-- ====================================================================================
SPOOL install_log.txt;

PROMPT --- DANG CHAY FILE 1: TAO BANG VA CONSTRAINT ---
@@01_Tables.sql;

PROMPT --- DANG CHAY FILE 2: TAO SEQUENCE ---
@@02_Sequences.sql;

PROMPT --- DANG CHAY FILE 3: TAO FUNCTION ---
@@03_Functions.sql;

PROMPT --- DANG CHAY FILE 4: TAO TRIGGER ---
@@04_Triggers.sql;

PROMPT --- DANG CHAY FILE 5: TAO PROCEDURE ---
@@05_Procedures.sql;

PROMPT --- DANG CHAY FILE 6: THEM DU LIEU CAN THIET ---
@@06_InsertData.sql;

PROMPT --- DANG CHAY FILE 7: THEM DU LIEU MAU ---
@@07_InsertDemoData.sql;

SPOOL OFF;
PROMPT --- TAT CA DA SAN SANG DE KET NOI VOI JAVA ---
EXIT;