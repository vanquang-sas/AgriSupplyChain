-- ====================================================================================
--                  PHẦN 9: TẠO CÁC JOB TỰ ĐỘNG HÓA (DBMS_SCHEDULER)
-- Script này định nghĩa các công việc chạy ngầm định kỳ bằng Oracle Scheduler.
-- Phải chạy dưới quyền của user AGRIAPP (hoặc có quyền CREATE JOB).
-- ====================================================================================
SET FEEDBACK ON;
SET SERVEROUTPUT ON;

------------------------------------------------------------------------------------
-- KIỂM TRA HẾT HẠN VÀ CẬP NHẬT TRẠNG THÁI TỒN KHO HÀNG NGÀY
-- Mục tiêu: Vào đầu ngày mới (00:00:05 hàng ngày), thực hiện quét và cập nhật trạng thái
-- tồn kho (từ Còn hạn -> Sắp hết hạn/Hết hạn), tự động thêm thông báo cho Nhân viên kho.
-- ------------------------------------------------------------------------------------
BEGIN
    -- Xoá job cũ nếu đã tồn tại
    BEGIN
        DBMS_SCHEDULER.DROP_JOB(job_name => 'JOB_CHECK_TONKHO_DAILY', force => TRUE);
    EXCEPTION
        WHEN OTHERS THEN
            NULL;
    END;

    -- Tạo Job mới gọi Procedure kiểm tra hạn sử dụng và sinh thông báo cho Nhân viên kho
    DBMS_SCHEDULER.CREATE_JOB (
        job_name        => 'JOB_CHECK_TONKHO_DAILY',
        job_type        => 'PLSQL_BLOCK',
        job_action      => 'BEGIN SP_KIEMTRA_HETHAN_THONGBAO; END;',
        start_date      => SYSTIMESTAMP,
        repeat_interval => 'FREQ=DAILY; BYHOUR=0; BYMINUTE=0; BYSECOND=05',
        enabled         => TRUE,
        comments        => 'Tu dong quet kiem tra thoi han ton kho va sinh canh bao vao luc 00:00:05 hang ngay'
    );

    DBMS_OUTPUT.PUT_LINE('-> Khoi tao thanh cong JOB_CHECK_TONKHO_DAILY.');
END;
/

PROMPT --- DA THIET LAP XONG TOAN BO JOB TU DONG HOA ---