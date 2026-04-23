-- Thiết lập môi trường để theo dõi quá trình chạy
SET FEEDBACK ON;
SET ECHO ON;
SET SERVEROUTPUT ON;

-- Ghi nhật ký ra file log để kiểm tra sau khi chạy xong
SPOOL install_log.txt;

PROMPT --- BẮT ĐẦU TẠO DATABASE ---

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

-- Kết thúc ghi log
SPOOL OFF;
PROMPT --- ĐÃ HOÀN THÀNH TẤT CẢ CÁC BƯỚC ---
EXIT;