
SELECT constraint_name
FROM user_constraints
WHERE table_name = 'DONHANG'
AND constraint_type = 'C';


SELECT search_condition
FROM user_constraints
WHERE constraint_name = 'SYS_C008724';

ALTER TABLE DONHANG
DROP CONSTRAINT SYS_C008724;

ALTER TABLE DONHANG
ADD CONSTRAINT CK_TrangThaiDH
CHECK (
    TrangThaiDH IN (
        'Đã đặt',
        'Chờ xử lý',
        'Đang giao',
        'Chờ giao hàng',
        'Hoàn thành',
        'Đã huỷ'
    )
);


CREATE OR REPLACE PROCEDURE SP_GIAOHANG_THANHCONG (
    p_MaDH IN VARCHAR2,
    p_MaNV_GiaoHang IN VARCHAR2
) IS

    v_rows NUMBER;

BEGIN

    UPDATE DONHANG
    SET
        TrangThaiDH = 'Hoàn thành',
        TGGiaoTT = SYSDATE
    WHERE MaDH = p_MaDH
    AND MaNV = p_MaNV_GiaoHang
    AND TrangThaiDH = 'Đang giao';

    v_rows := SQL%ROWCOUNT;

    IF v_rows = 0 THEN
        RAISE_APPLICATION_ERROR(
            -20001,
           'Giao hàng thất bại! Đơn chưa ở trạng thái Đang giao.'
        );
    END IF;

    COMMIT;

EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
/
-- Nhân viên giao hàng bấm xác nhận giao hàng
CREATE OR REPLACE PROCEDURE SP_XACNHAN_GIAOHANG (
    p_MaDH IN VARCHAR2,
    p_MaNV_GiaoHang IN VARCHAR2
) IS

    v_rows NUMBER;

BEGIN

    UPDATE DONHANG
    SET
        MaNV = p_MaNV_GiaoHang,
        TrangThaiDH = 'Đang giao'
    WHERE MaDH = p_MaDH
    AND TrangThaiDH = 'Chờ giao hàng';

    v_rows := SQL%ROWCOUNT;

    IF v_rows = 0 THEN

        RAISE_APPLICATION_ERROR(
            -20002,
            'Đơn hàng không ở trạng thái Chờ giao hàng'
        );

    END IF;

    COMMIT;

EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
/

-- Nhân viên giao hàng bấm xác nhận giao hàng THẤT BẠI
CREATE OR REPLACE PROCEDURE SP_GIAOHANG_THATBAI (
    p_MaDH IN VARCHAR2,
    p_MaNV_GiaoHang IN VARCHAR2,
    p_LyDoHuy IN NVARCHAR2
) IS

    v_rows NUMBER;

BEGIN

    UPDATE DONHANG
    SET
        TrangThaiDH = 'Đã huỷ',
        LyDoHuy = p_LyDoHuy,
        TGGiaoTT = SYSDATE
    WHERE MaDH = p_MaDH
    AND MaNV = p_MaNV_GiaoHang
    AND TrangThaiDH = 'Đang giao';

    v_rows := SQL%ROWCOUNT;

    IF v_rows = 0 THEN

        RAISE_APPLICATION_ERROR(
            -20003,
            'Đơn hàng không ở trạng thái Đang giao'
        );

    END IF;

    COMMIT;

EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
/




