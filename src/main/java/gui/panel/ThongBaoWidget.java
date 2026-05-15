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
    private int unreadCount = 0; // Thêm biến lưu số lượng để tối ưu hiệu năng vẽ
    private Timer systemCheckTimer;

    public ThongBaoWidget() {
        initComponents();
        // Chạy kiểm tra hệ thống lần đầu khi mở app
        thongBaoBUS.kiemTraHeThong();
        capNhatNutChuong();
        startLiveUpdate();
    }

    // Tự động cập nhật thông báo
    private void startLiveUpdate() {
        // Timer cập nhật UI (1 giây/lần)
        Timer uiTimer = new Timer(1000, e -> {
            capNhatNutChuong();
        });
        uiTimer.start();
        
        // Timer kiểm tra hệ thống dưới nền (60 giây/lần)
        systemCheckTimer = new Timer(60000, e -> {
            thongBaoBUS.kiemTraHeThong();
        });
        systemCheckTimer.start();
    }

    private void capNhatNutChuong() {
        int newCount = thongBaoBUS.getSoLuongChuaDoc();
        if (newCount != unreadCount) {
            unreadCount = newCount;
            btnBell.repaint();
        }
        if (unreadCount > 0) {
            btnBell.setToolTipText("Bạn có " + unreadCount + " thông báo mới");
        } else {
            btnBell.setToolTipText("Không có thông báo mới");
        }
    }

    private void initComponents() {
        this.setLayout(new BorderLayout());
        this.setOpaque(false);

        // 1. Tạo Nút Chuông với chức năng vẽ Badge (số lượng chưa đọc)
        btnBell = new JButton() {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                if (unreadCount > 0) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    int size = 18;
                    int x = getWidth() - size;
                    int y = 0;
                    
                    g2.setColor(AppColor.ERROR);
                    g2.fillOval(x, y, size, size);
                    
                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font("SansSerif", Font.BOLD, 10));
                    String text = unreadCount > 99 ? "99+" : String.valueOf(unreadCount);
                    FontMetrics fm = g2.getFontMetrics();
                    int tx = x + (size - fm.stringWidth(text)) / 2;
                    int ty = y + ((size - fm.getHeight()) / 2) + fm.getAscent();
                    g2.drawString(text, tx, ty);
                    
                    g2.dispose();
                }
            }
        };

        btnBell.setPreferredSize(new Dimension(40, 40));
        btnBell.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBell.putClientProperty(FlatClientProperties.STYLE, "arc: 999");
        btnBell.setContentAreaFilled(false);
        btnBell.setBorderPainted(false);
        btnBell.setFocusPainted(false);

        // Nạp icon chuông
        try {
            java.net.URL url = getClass().getResource("/icons/bell.png");
            if (url != null) {
                ImageIcon icon = new ImageIcon(url);
                Image img = icon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
                btnBell.setIcon(new ImageIcon(img));
            } else {
                btnBell.setText("🔔");
            }
        } catch (Exception e) {
            btnBell.setText("🔔");
        }

        // 2. Tạo Popup Menu
        popupMenu = new JPopupMenu();
        popupMenu.setPreferredSize(new Dimension(350, 450));
        popupMenu.putClientProperty(FlatClientProperties.STYLE, "arc: 15");
        popupMenu.setBackground(AppColor.SURFACE);

        // Tiêu đề Popup
        JLabel lblTitle = new JLabel("Thông báo hệ thống", SwingConstants.CENTER);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel chứa nội dung
        panelMessages = new JPanel();
        panelMessages.setLayout(new BoxLayout(panelMessages, BoxLayout.Y_AXIS));
        panelMessages.setBackground(AppColor.SURFACE);

        scrollPane = new JScrollPane(panelMessages);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        JPanel mainPopupPanel = new JPanel(new BorderLayout());
        mainPopupPanel.setBackground(AppColor.SURFACE);
        mainPopupPanel.add(lblTitle, BorderLayout.NORTH);
        mainPopupPanel.add(scrollPane, BorderLayout.CENTER);
        
        popupMenu.add(mainPopupPanel);

        // Sự kiện click chuông
        btnBell.addActionListener(e -> {
            // Kiểm tra hệ thống tức thì khi mở (đảm bảo hiển thị hết hàng/sắp hết hàng mới nhất)
            thongBaoBUS.kiemTraHeThong();
            
            loadDuLieuThongBao();
            popupMenu.show(btnBell, -popupMenu.getPreferredSize().width + btnBell.getWidth(), btnBell.getHeight() + 5);
            
            // Cuộn lên đầu ngay sau khi hiện popup
            SwingUtilities.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(0));
            
            // Sau khi mở thì đánh dấu đã đọc
            thongBaoBUS.danhDauDaDocTatCa();
            unreadCount = 0;
            btnBell.repaint();
            btnBell.setToolTipText("Không có thông báo mới");
        });

        this.add(btnBell, BorderLayout.CENTER);
    }

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
                int trangThai = row[3] != null ? (int) row[3] : 1;

                JPanel itemPanel = new JPanel(new BorderLayout(5, 5));
                itemPanel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, AppColor.BORDER),
                        BorderFactory.createEmptyBorder(10, 15, 10, 15)));

                // Tô màu nền cho thông báo chưa đọc
                if (trangThai == 0) {
                    itemPanel.setBackground(AppColor.SECONDARY_HOVER);
                } else {
                    itemPanel.setBackground(AppColor.SURFACE);
                }

                JLabel lblType = new JLabel(loaiTB);
                lblType.setFont(new Font("SansSerif", Font.BOLD, 12));
                
                // Phân loại màu sắc tiêu đề
                String typeLower = loaiTB.toLowerCase();
                if (typeLower.contains("hết hạn") || typeLower.contains("hết hàng")) {
                    lblType.setForeground(AppColor.ERROR);
                } else if (typeLower.contains("sắp hết")) {
                    lblType.setForeground(AppColor.WARNING);
                } else {
                    lblType.setForeground(AppColor.PRIMARY);
                }

                JTextArea txtContent = new JTextArea(noiDung);
                txtContent.setWrapStyleWord(true);
                txtContent.setLineWrap(true);
                txtContent.setEditable(false);
                txtContent.setOpaque(false);
                txtContent.setFont(new Font("SansSerif", Font.PLAIN, 13));
                txtContent.setForeground(AppColor.TEXT_PRIMARY);

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