package test;

import bus.DonHangBUS;
import dto.CartItemDTO;
import gui.panel.GioHangPanel;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;

public class TestGioHang {

    public static void main(String[] args) {
        // Cài FlatLaf theme cho đẹp
        FlatLightLaf.setup();

        SwingUtilities.invokeLater(() -> {

            // 1. Tạo BUS
            DonHangBUS bus = new DonHangBUS();

            // 2. Thêm vài sản phẩm giả vào giỏ để test giao diện
            bus.themVaoGio(new CartItemDTO(
                    "SP001", "Cà rốt Đà Lạt", null, "kg", 25000, 2));
            bus.themVaoGio(new CartItemDTO(
                    "SP002", "Rau cải xanh", null, "bó", 15000, 3));
            bus.themVaoGio(new CartItemDTO(
                    "SP003", "Khoai tây", null, "kg", 30000, 1));

            // 3. Tạo panel với maKH giả
            GioHangPanel panel = new GioHangPanel(bus, "KH001");

            // 4. Bọc vào JFrame để hiển thị
            JFrame frame = new JFrame("Test Giỏ Hàng");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(900, 600);
            frame.setLocationRelativeTo(null);
            frame.add(panel);
            frame.setVisible(true);
        });
    }
}