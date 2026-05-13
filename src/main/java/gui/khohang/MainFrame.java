package gui.khohang;

import util.AppColor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MainFrame extends JFrame {

    private static final String PAGE_NHAP = "PAGE_NHAP";
    private static final String PAGE_XUAT = "PAGE_XUAT";
    private static final String PAGE_TON = "PAGE_TON";

    private JPanel mainContent;
    private CardLayout cardLayout;
    private JButton activeBtn;

    public MainFrame() {
        initComponents();
        setTitle("Hệ thống quản lý kho");
        setSize(1280, 760);
        setMinimumSize(new Dimension(1100, 680));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(AppColor.BACKGROUND);

        cardLayout = new CardLayout();
        mainContent = new JPanel(cardLayout);
        mainContent.setBackground(AppColor.BACKGROUND);
        mainContent.add(new NhapKhoPanel(), PAGE_NHAP);
        mainContent.add(new XuatKhoPanel(), PAGE_XUAT);
        mainContent.add(createDummyPanel("Giao diện tồn kho"), PAGE_TON);

        add(buildSideBar(), BorderLayout.WEST);
        add(mainContent, BorderLayout.CENTER);
    }

    private JPanel buildSideBar() {
        JPanel sideBar = new JPanel();
        sideBar.setPreferredSize(new Dimension(220, 0));
        sideBar.setBackground(AppColor.SURFACE);
        sideBar.setBorder(new EmptyBorder(28, 16, 24, 16));
        sideBar.setLayout(new BoxLayout(sideBar, BoxLayout.Y_AXIS));

        JLabel lbLogo = new JLabel("AGRI APP");
        lbLogo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lbLogo.setForeground(AppColor.TEXT_PRIMARY);
        lbLogo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnNhap = createSideBarButton("Nhập kho");
        JButton btnXuat = createSideBarButton("Xuất kho");
        JButton btnTon = createSideBarButton("Tồn kho");

        btnNhap.addActionListener(e -> showPage(PAGE_NHAP, btnNhap));
        btnXuat.addActionListener(e -> showPage(PAGE_XUAT, btnXuat));
        btnTon.addActionListener(e -> showPage(PAGE_TON, btnTon));

        sideBar.add(lbLogo);
        sideBar.add(Box.createVerticalStrut(28));
        sideBar.add(btnNhap);
        sideBar.add(Box.createVerticalStrut(8));
        sideBar.add(btnXuat);
        sideBar.add(Box.createVerticalStrut(8));
        sideBar.add(btnTon);
        sideBar.add(Box.createVerticalGlue());

        updateActiveButton(btnNhap);
        return sideBar;
    }

    private void showPage(String pageName, JButton button) {
        cardLayout.show(mainContent, pageName);
        updateActiveButton(button);
    }

    private void updateActiveButton(JButton clickedBtn) {
        if (activeBtn != null) {
            activeBtn.setBackground(AppColor.SURFACE);
            activeBtn.setForeground(AppColor.TEXT_PRIMARY);
        }

        activeBtn = clickedBtn;
        activeBtn.setBackground(new Color(22, 163, 74, 28));
        activeBtn.setForeground(AppColor.PRIMARY_ACTIVE);
    }

    private JButton createSideBarButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setMaximumSize(new Dimension(188, 42));
        btn.setPreferredSize(new Dimension(188, 42));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMargin(new Insets(0, 18, 0, 18));
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setBackground(AppColor.SURFACE);
        btn.setForeground(AppColor.TEXT_PRIMARY);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (btn != activeBtn) {
                    btn.setBackground(AppColor.SECONDARY_HOVER);
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (btn != activeBtn) {
                    btn.setBackground(AppColor.SURFACE);
                }
            }
        });
        return btn;
    }

    private JPanel createDummyPanel(String text) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(AppColor.BACKGROUND);

        JLabel label = new JLabel(text, JLabel.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 18));
        label.setForeground(AppColor.TEXT_SECONDARY);
        panel.add(label);
        return panel;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        EventQueue.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
