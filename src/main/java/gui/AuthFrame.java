package gui;

import util.AppColor;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import gui.panel.DangKyPanel;
import gui.panel.DangNhapPanel;

import java.awt.*;

public class AuthFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel rightCardPanel;

    public AuthFrame() {
        setTitle("AgriSupplyChain - Hệ thống");
        setSize(1200, 750);
        setMinimumSize(new Dimension(1100, 650));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        // --- PANEL TRÁI (Cố định Hình ảnh + Logo) ---
        JPanel leftPanel = createLeftPanel();
        gbc.gridx = 0;
        gbc.weightx = 0.6;
        add(leftPanel, gbc);

        // --- PANEL PHẢI (Sử dụng CardLayout để chuyển đổi Form) ---
        cardLayout = new CardLayout();
        rightCardPanel = new JPanel(cardLayout);
        rightCardPanel.setBackground(AppColor.SURFACE);
        rightCardPanel.setPreferredSize(new Dimension(0, 0));

        // Khởi tạo các Panel con (Truyền instance của AuthFrame vào để có thể gọi hàm switchPanel)
        DangNhapPanel dangNhapPanel = new DangNhapPanel(this);
        DangKyPanel dangKyPanel = new DangKyPanel(this);
        QuenMKPanel quenMKPanel = new QuenMKPanel(this);

        // Gắn Key cho từng màn hình
        rightCardPanel.add(dangNhapPanel, "LOGIN");
        rightCardPanel.add(dangKyPanel, "REGISTER");
        rightCardPanel.add(quenMKPanel, "FORGOT_PASS");

        gbc.gridx = 1;
        gbc.weightx = 0.4;
        add(rightCardPanel, gbc);
    }

    // Hàm gọi để chuyển đổi màn hình từ bên trong các Panel con
    public void switchPanel(String panelName) {
        cardLayout.show(rightCardPanel, panelName);
    }

    private JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                java.net.URL imgURL = getClass().getResource("/images/Background_DangKy.jpg");
                if (imgURL != null) {
                    Image bgImage = new ImageIcon(imgURL).getImage();
                    int panelW = getWidth(), panelH = getHeight();
                    double scale = Math.max((double) panelW / bgImage.getWidth(null), (double) panelH / bgImage.getHeight(null));
                    int w = (int) (bgImage.getWidth(null) * scale);
                    int h = (int) (bgImage.getHeight(null) * scale);
                    g2d.drawImage(bgImage, (panelW - w) / 2, (panelH - h) / 2, w, h, this);
                } else {
                    g2d.setColor(AppColor.PRIMARY);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        leftPanel.setPreferredSize(new Dimension(0, 0));

        JPanel logoContainer = new JPanel();
        logoContainer.setOpaque(false);
        logoContainer.setLayout(new BoxLayout(logoContainer, BoxLayout.Y_AXIS));
        logoContainer.setBorder(new EmptyBorder(20, 30, 20, 30));

        JLabel lblLogo = new JLabel();
        java.net.URL logoURL = getClass().getResource("/images/TestLogo.png");
        if (logoURL != null) lblLogo.setIcon(new ImageIcon(new ImageIcon(logoURL).getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH)));
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblBrand = new JLabel("AGRI-SUPPLY-CHAIN") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                // Bật khử răng cưa để viền bo góc mịn màng hơn
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Thiết lập màu trắng với độ mờ Alpha (thang 0-255). 
                g2.setColor(new Color(255, 255, 255, 150)); 
                
                // Vẽ nền hình chữ nhật bo góc (15, 15 là độ cong của góc)
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15); 
                g2.dispose();
                
                // Gọi super để Swing tiếp tục vẽ dòng chữ lên trên lớp nền
                super.paintComponent(g);
            }
        };
        lblBrand.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblBrand.setForeground(AppColor.PRIMARY_ACTIVE);
        lblBrand.setAlignmentX(Component.CENTER_ALIGNMENT);
        // RẤT QUAN TRỌNG: Phải đặt là false để Swing vẽ lại hình nền phía sau trước, tránh lỗi đồ họa
        lblBrand.setOpaque(false); 
        lblBrand.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        logoContainer.add(lblLogo);
        logoContainer.add(Box.createRigidArea(new Dimension(0, 10)));
        logoContainer.add(lblBrand);
        leftPanel.add(logoContainer);

        return leftPanel;
    }
}