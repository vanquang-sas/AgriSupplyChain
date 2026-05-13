/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gui.panel;

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
    private JScrollPane scrollPane;
    private ThongBaoBUS thongBaoBUS = new ThongBaoBUS();

    public ThongBaoWidget() {
        initComponents();
        thongBaoBUS.kiemTraHetHan(); // Kiểm tra hết hạn ngay khi mở app
        capNhatNutChuong();
        startLiveUpdate(); // Bắt đầu bộ đếm thời gian cập nhật tự động
    }

    // Bộ đếm thời gian để tự động cập nhật số lượng thông báo mỗi 30 giây
    private void startLiveUpdate() {
        Timer timer = new Timer(30000, e -> {
            thongBaoBUS.kiemTraHetHan(); // Quét lại kho xem có gì mới hết hạn/hết hàng không
            capNhatNutChuong();
        });
        timer.start();
    }

    // Thêm hàm này để cập nhật số lượng trên nút chuông
    private void capNhatNutChuong() {
        int soLuong = thongBaoBUS.getSoLuongChuaDoc();
        if (soLuong > 0) {
            // Thay vì dùng setText, ta sẽ dùng ToolTip hoặc biến để vẽ Badge
            btnBell.setToolTipText("Bạn có " + soLuong + " thông báo mới");
        } else {
            btnBell.setToolTipText("Không có thông báo mới");
        }
        btnBell.repaint(); // Vẽ lại để cập nhật Badge
    }

    // Ghi đè paint để vẽ Badge (Dấu chấm đỏ) lên nút chuông
    @Override
    protected void paintChildren(Graphics g) {
        super.paintChildren(g);
        
        int soLuong = thongBaoBUS.getSoLuongChuaDoc();
        if (soLuong > 0) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Cấu hình vòng tròn đỏ
            int size = 18;
            // Căn chỉnh lại vị trí để không đè quá nhiều lên icon chuông
            int x = btnBell.getX() + btnBell.getWidth() - size; 
            int y = btnBell.getY();
            
            g2.setColor(AppColor.ERROR);
            g2.fillOval(x, y, size, size);
            
            // Vẽ số lượng
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("SansSerif", Font.BOLD, 10));
            String text = soLuong > 99 ? "99+" : String.valueOf(soLuong);
            FontMetrics fm = g2.getFontMetrics();
            int tx = x + (size - fm.stringWidth(text)) / 2;
            int ty = y + ((size - fm.getHeight()) / 2) + fm.getAscent();
            g2.drawString(text, tx, ty);
            
            g2.dispose();
        }
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setOpaque(false); // Đảm bảo tệp với nền thanh header
        
        // 1. Tạo Nút Chuông
        btnBell = new JButton();
        btnBell.setPreferredSize(new Dimension(40, 40));
        btnBell.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBell.setContentAreaFilled(false); // Loại bỏ nền mặc định
        btnBell.setBorderPainted(false);     // Loại bỏ viền mặc định
        btnBell.setFocusPainted(false);
        btnBell.putClientProperty(FlatClientProperties.STYLE, "arc: 999"); // Bo tròn tuyệt đối khi hover

        // --- CẬP NHẬT ĐƯỜNG DẪN ẢNH ICON
        try {
            java.net.URL url = getClass().getResource("/icons/bell.png");
            if (url != null) {
                ImageIcon icon = new ImageIcon(url);
                Image img = icon.getImage().getScaledInstance(26, 26, Image.SCALE_SMOOTH);
                btnBell.setIcon(new ImageIcon(img));
            } else {
                btnBell.setText("🔔");
            }
        } catch (Exception e) {
            btnBell.setText("🔔");
        }
        
        // Thiết lập button trong suốt hoàn toàn
        btnBell.setOpaque(false);
        btnBell.setContentAreaFilled(false);
        btnBell.setBorderPainted(false);
        btnBell.setFocusPainted(false);
        btnBell.setBorder(BorderFactory.createEmptyBorder());
        btnBell.setBackground(new Color(0,0,0,0));
        
        // Sử dụng style ToolBarButton của FlatLaf để loại bỏ mọi nền mặc định
        btnBell.putClientProperty(FlatClientProperties.BUTTON_TYPE, "toolBarButton");
        btnBell.putClientProperty(FlatClientProperties.STYLE, "arc: 999; margin: 0,0,0,0; focusWidth: 0; focusColor: #00000000"); 
        
        // Loại bỏ text trên button vì ta sẽ dùng badge vẽ đè lên
        btnBell.setText("");
        btnBell.setHorizontalTextPosition(SwingConstants.CENTER);
        btnBell.setVerticalTextPosition(SwingConstants.BOTTOM);
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

        // Panel chứa các dòng thông báo (cho vào JScrollPane để cuộn)
        panelMessages = new JPanel();
        panelMessages.setLayout(new BoxLayout(panelMessages, BoxLayout.Y_AXIS));
        panelMessages.setBackground(AppColor.SURFACE);

        scrollPane = new JScrollPane(panelMessages);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Lăn chuột mượt hơn
        
        JPanel mainPopupPanel = new JPanel(new BorderLayout());
        mainPopupPanel.setBackground(AppColor.SURFACE);
        mainPopupPanel.add(lblTitle, BorderLayout.NORTH);
        mainPopupPanel.add(scrollPane, BorderLayout.CENTER);
        
        popupMenu.add(mainPopupPanel);

        // 3. Sự kiện Click vào chuông
        btnBell.addActionListener(e -> {
            showPopupWithAnimation();
            
            // Cuộn lên đầu ngay lập tức
            SwingUtilities.invokeLater(() -> {
                scrollPane.getVerticalScrollBar().setValue(0);
            });

            // 2. Cập nhật DB thành đã đọc và xóa số trên nút chuông
            thongBaoBUS.danhDauDaDocTatCa();
            capNhatNutChuong();
        });

        this.add(btnBell, BorderLayout.CENTER);
    }

    // Hiển thị Popup ngay lập tức (không kéo từ từ)
    private void showPopupWithAnimation() {
        loadDuLieuThongBao();
        
        int width = 350;
        int targetHeight = 450;
        
        popupMenu.setPopupSize(width, targetHeight);
        popupMenu.show(btnBell, -width + btnBell.getWidth(), btnBell.getHeight() + 5);
    }

    // Hàm render dữ liệu ra Popup
    private void loadDuLieuThongBao() {
        // 1. Gọi Database quét lại toàn bộ kho (Hết hạn, Hết hàng) để cập nhật dữ liệu mới nhất
        thongBaoBUS.kiemTraHetHan();
        
        // 2. Xóa danh sách cũ trên giao diện và nạp danh sách mới
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
                        BorderFactory.createEmptyBorder(10, 15, 10, 15)));

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
                // Tô màu dựa trên mức độ nghiêm trọng
                String loaiLower = loaiTB.toLowerCase();
                if (loaiLower.contains("hết hạn") || loaiLower.contains("hết hàng")) {
                    lblType.setForeground(AppColor.ERROR); // Đỏ cho trường hợp khẩn cấp
                } else if (loaiLower.contains("sắp hết")) {
                    lblType.setForeground(AppColor.WARNING); // Màu cam cảnh báo sớm
                } else {
                    lblType.setForeground(AppColor.PRIMARY); // Màu xanh cho thông báo thường
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