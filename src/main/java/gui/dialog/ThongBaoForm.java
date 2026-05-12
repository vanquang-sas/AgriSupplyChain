package gui.dialog;

import util.AppColor;
import util.DBConnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;

public class ThongBaoForm extends JDialog {

    private JPanel contentPanel;

    public ThongBaoForm(JFrame parent) {
        super(parent, "Thông báo hệ thống", true);
        setSize(450, 600);
        setLocationRelativeTo(parent); // Hiện ở giữa MainFrame
        setLayout(new BorderLayout());
        
        initComponents();
        loadThongBao();
    }

    private void initComponents() {
        // Tiêu đề
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(Color.WHITE);
        JLabel lblTitle = new JLabel("THÔNG BÁO");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setBorder(new EmptyBorder(20, 20, 10, 20));
        pnlHeader.add(lblTitle, BorderLayout.WEST);
        add(pnlHeader, BorderLayout.NORTH);

        // Vùng nội dung
        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(245, 246, 250)); // Màu nền hơi xám nhẹ để nổi bật thẻ trắng
        contentPanel.setBorder(new EmptyBorder(10, 15, 10, 15));

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(12);
        add(scrollPane, BorderLayout.CENTER);

        // --- Ô MÀU KHÁC BIỆT CHỨA NÚT ĐÓNG ---
        JPanel pnlAction = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnlAction.setBackground(new Color(236, 239, 241)); // Màu khác hẳn (Xám xanh nhạt)
        pnlAction.setBorder(new EmptyBorder(12, 0, 12, 0));

        JButton btnClose = new JButton("Đóng") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(71, 85, 105)); // Màu Slate đậm
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnClose.setForeground(Color.WHITE);
        btnClose.setPreferredSize(new Dimension(100, 35));
        btnClose.setContentAreaFilled(false);
        btnClose.setBorderPainted(false);
        btnClose.setFocusPainted(false);
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClose.addActionListener(e -> dispose());

        pnlAction.add(btnClose);
        add(pnlAction, BorderLayout.SOUTH);
    }

    private void loadThongBao() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT LoaiTB, NoiDung, TGTao FROM THONGBAO ORDER BY TGTao DESC")) {
            
            while (rs.next()) {
                // TẠO THẺ TRẮNG BO TRÒN
                JPanel card = new JPanel(new BorderLayout(5, 5)) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(Color.WHITE);
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20); // Bo tròn thẻ
                        g2.dispose();
                    }
                };
                card.setOpaque(false);
                card.setBorder(new EmptyBorder(15, 15, 15, 15));
                card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

                JLabel lblHeader = new JLabel(rs.getString("LoaiTB") + " • " + sdf.format(rs.getTimestamp("TGTao")));
                lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 12));
                lblHeader.setForeground(new Color(100, 116, 139));

                JTextArea txtContent = new JTextArea(rs.getString("NoiDung"));
                txtContent.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                txtContent.setLineWrap(true);
                txtContent.setWrapStyleWord(true);
                txtContent.setOpaque(false);
                txtContent.setEditable(false);

                card.add(lblHeader, BorderLayout.NORTH);
                card.add(txtContent, BorderLayout.CENTER);

                contentPanel.add(card);
                contentPanel.add(Box.createRigidArea(new Dimension(0, 12))); // Khoảng cách giữa các thẻ
            }
        } catch (Exception e) { e.printStackTrace(); }
    }
}