/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gui;

import bus.ThongBaoBUS;
import util.AppColor;
import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ThongBaoWidget extends JPanel {
    private JButton btnBell;
    private JPopupMenu popupMenu;
    private JPanel panelMessages;
    private ThongBaoBUS thongBaoBUS = new ThongBaoBUS();

    public ThongBaoWidget() {
        initComponents();
        capNhatNutChuong(); // <-- GỌI HÀM NÀY ĐỂ HIỂN THỊ SỐ TRÊN CHUÔNG LÚC MỚI MỞ APP
    }

    // Thêm hàm này để cập nhật số lượng trên nút chuông
    private void capNhatNutChuong() {
        int soLuong = thongBaoBUS.getSoLuongChuaDoc();
        if (soLuong > 0) {
            btnBell.setText("(" + soLuong + ")");
            btnBell.setForeground(AppColor.ERROR); // Tô màu đỏ cho số lượng để gây chú ý
            btnBell.setFont(new Font("SansSerif", Font.BOLD, 12));
        } else {
            btnBell.setText(""); // Xóa số đi nếu không có thông báo mới
        }
    }
    
    private void initComponents() {
        this.setLayout(new BorderLayout());
        this.setOpaque(false); // Trong suốt để ăn theo nền của MainFrame sau này

        // 1. Tạo Nút Chuông
        btnBell = new JButton();
        btnBell.setPreferredSize(new Dimension(40, 40));
        btnBell.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBell.putClientProperty(FlatClientProperties.STYLE, "buttonType: toolBarButton; arc: 15");

        // --- CẬP NHẬT ĐƯỜNG DẪN ẢNH ICON TỪ /icons/bell.png THÀNH /icons/notice.png ---
        try {
            // Dựa trên hình ảnh image_5.png, tên tệp là notice.png
            java.net.URL url = getClass().getResource("/icons/notice.png");
            if (url != null) {
                ImageIcon icon = new ImageIcon(url);
                // Giữ nguyên kích thước 20x20 cho nút 40x40
                Image img = icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
                btnBell.setIcon(new ImageIcon(img));
            } else {
                btnBell.setText("🔔"); // Backup bằng ký tự biểu cảm chuông nếu không tìm thấy ảnh
            }
        } catch (Exception e) {
            btnBell.setText("🔔");
        }
        // ----------------------------------------------------------------------------------

        // 2. Tạo Popup Menu xổ xuống
        popupMenu = new JPopupMenu();
        popupMenu.setPreferredSize(new Dimension(350, 450));
        popupMenu.putClientProperty(FlatClientProperties.STYLE, "arc: 15");
        popupMenu.setBackground(AppColor.SURFACE);

        // Header của Popup
        JLabel lblTitle = new JLabel("Thông báo hệ thống", SwingConstants.CENTER);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        popupMenu.add(lblTitle);
        popupMenu.addSeparator();

        // Panel chứa các dòng thông báo (cho vào JScrollPane để cuộn)
        panelMessages = new JPanel();
        panelMessages.setLayout(new BoxLayout(panelMessages, BoxLayout.Y_AXIS));
        panelMessages.setBackground(AppColor.SURFACE);

        JScrollPane scrollPane = new JScrollPane(panelMessages);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Lăn chuột mượt hơn
        popupMenu.add(scrollPane);

        // 3. Sự kiện Click vào chuông
        btnBell.addActionListener(e -> {
        loadDuLieuThongBao(); // 1. Kéo data lên Popup (lúc này data vẫn hiển thị trạng thái chưa đọc)
        
        // Hiển thị popup
        popupMenu.show(btnBell, -popupMenu.getPreferredSize().width + btnBell.getWidth(), btnBell.getHeight() + 5);

        // 2. Cập nhật DB thành đã đọc và xóa số trên nút chuông
        thongBaoBUS.danhDauDaDocTatCa(); 
        capNhatNutChuong(); 
        });

        this.add(btnBell, BorderLayout.CENTER);
    }

    // Hàm render dữ liệu ra Popup
    private void loadDuLieuThongBao() {
        panelMessages.removeAll();
        ArrayList<Object[]> list = thongBaoBUS.getDanhSachThongBao();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        if (list == null || list.isEmpty()) {
            JLabel lblEmpty = new JLabel("Chưa có thông báo nào", SwingConstants.CENTER);
            lblEmpty.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
            lblEmpty.setForeground(AppColor.TEXT_SECONDARY);
            panelMessages.add(lblEmpty);
        } else {
            for (Object[] row : list) {
            String loaiTB = row[0] != null ? row[0].toString() : "Thông báo";
            String noiDung = row[1] != null ? row[1].toString() : "";
            String thoiGian = row[2] != null ? sdf.format(row[2]) : "";
            int trangThai = row[3] != null ? (int) row[3] : 1; // Lấy trạng thái từ DB

            JPanel itemPanel = new JPanel(new BorderLayout(5, 5));
            itemPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, AppColor.BORDER),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
            ));

            // TÔ MÀU NỀN ĐỂ PHÂN BIỆT ĐỌC / CHƯA ĐỌC
            if (trangThai == 0) {
                // Chưa đọc: Tô màu nền xám nhạt (hoặc màu xanh nhạt tuỳ bạn)
                itemPanel.setBackground(AppColor.SECONDARY_HOVER); 
            } else {
                // Đã đọc: Nền trắng bình thường
                itemPanel.setBackground(AppColor.SURFACE);
            }

                // Tiêu đề (Loại thông báo)
                JLabel lblType = new JLabel(loaiTB);
                lblType.setFont(new Font("SansSerif", Font.BOLD, 12));
                // Tô màu đỏ nếu là cảnh báo/hết hạn, ngược lại màu xanh
                if (loaiTB.toLowerCase().contains("cảnh báo") || loaiTB.toLowerCase().contains("hết hạn")) {
                    lblType.setForeground(AppColor.ERROR);
                } else {
                    lblType.setForeground(AppColor.PRIMARY);
                }

                // Nội dung (Dùng JTextArea để tự động xuống dòng)
                JTextArea txtContent = new JTextArea(noiDung);
                txtContent.setWrapStyleWord(true);
                txtContent.setLineWrap(true);
                txtContent.setEditable(false);
                txtContent.setOpaque(false);
                txtContent.setFont(new Font("SansSerif", Font.PLAIN, 13));
                txtContent.setForeground(AppColor.TEXT_PRIMARY);

                // Thời gian
                JLabel lblTime = new JLabel(thoiGian);
                lblTime.setFont(new Font("SansSerif", Font.ITALIC, 11));
                lblTime.setForeground(AppColor.TEXT_SECONDARY);

                itemPanel.add(lblType, BorderLayout.NORTH);
                itemPanel.add(txtContent, BorderLayout.CENTER);
                itemPanel.add(lblTime, BorderLayout.SOUTH);

                panelMessages.add(itemPanel);
                
               
            }
        }
        
        panelMessages.revalidate();
        panelMessages.repaint();
    }
}