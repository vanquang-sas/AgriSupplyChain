package gui.panel;

import bus.ThamSoBUS;
import dto.ThamSoDTO;
import gui.dialog.ThamSoForm;
import util.AppColor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ThamSoPanel extends JPanel {

    private final ThamSoBUS bus = new ThamSoBUS();
    private JPanel cardsContainer;

    public ThamSoPanel() {
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 20));
        setBackground(AppColor.BACKGROUND);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        // --- KHUNG TRẮNG CHỨA TIÊU ĐỀ (HEADER CARD) ---
        RoundedPanel headerCard = new RoundedPanel(16);
        headerCard.setBackground(Color.WHITE);
        headerCard.setLayout(new BorderLayout());
        headerCard.setBorder(new EmptyBorder(24, 24, 24, 24)); 
        
        JLabel title = new JLabel("Cấu hình hệ thống");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(AppColor.TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Tùy chỉnh các ngưỡng vận hành và chính sách ưu đãi của cửa hàng");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(AppColor.TEXT_SECONDARY);

        JPanel titleBlock = new JPanel(new GridLayout(2, 1, 0, 5));
        titleBlock.setOpaque(false);
        titleBlock.add(title);
        titleBlock.add(subtitle);
        headerCard.add(titleBlock, BorderLayout.WEST);

        JButton btnRefresh = createIconButton("icons/refresh.svg");
        btnRefresh.setToolTipText("Làm mới dữ liệu");
        btnRefresh.addActionListener(e -> loadData());
        
        JPanel rightHeader = new JPanel(new GridBagLayout());
        rightHeader.setOpaque(false);
        rightHeader.add(btnRefresh);
        
        headerCard.add(rightHeader, BorderLayout.EAST);

        add(headerCard, BorderLayout.NORTH);

        // --- KHU VỰC GRID CHỨA CÁC THAM SỐ ---
        cardsContainer = new JPanel(new GridLayout(0, 2, 24, 24));
        cardsContainer.setOpaque(false);

        // FIX LỖI BÓP MÉO: Bọc grid vào NORTH để JScrollPane không ép chiều cao
        JPanel gridWrapper = new JPanel(new BorderLayout());
        gridWrapper.setOpaque(false);
        gridWrapper.add(cardsContainer, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(gridWrapper);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0)); 
        
        add(scroll, BorderLayout.CENTER);
    }

    public void loadData() {
        cardsContainer.removeAll();
        List<ThamSoDTO> list = bus.getAll();
        for (ThamSoDTO ts : list) {
            cardsContainer.add(new ThamSoCard(ts));
        }
        cardsContainer.revalidate();
        cardsContainer.repaint();
    }

    // --- Inner Class: Thẻ Tham số ---
    class ThamSoCard extends JPanel {
        private boolean isHover = false;
        private final ThamSoDTO data;

        public ThamSoCard(ThamSoDTO ts) {
            this.data = ts;
            setLayout(new BorderLayout(20, 0));
            setOpaque(false);
            
            // Ép chặt kích thước thẻ để không bị méo
            Dimension cardSize = new Dimension(0, 130);
            setPreferredSize(cardSize);
            setMinimumSize(cardSize);
            
            setBorder(new EmptyBorder(20, 20, 20, 20));

            // Icon Wrapper (Vòng tròn màu đã fix lỗi bóp cạnh)
            JPanel iconCircle = new JPanel(new GridBagLayout()) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(getIconBg(ts.getTenTS()));
                    // Lùi nét vẽ vào 1px để hệ thống không cắt mất viền
                    g2.fillOval(1, 1, getWidth() - 3, getHeight() - 3);
                    g2.dispose();
                }
            };
            iconCircle.setOpaque(false);
            
            Dimension iconSize = new Dimension(60, 60);
            iconCircle.setPreferredSize(iconSize);
            iconCircle.setMinimumSize(iconSize);
            iconCircle.setMaximumSize(iconSize);
            
            JLabel lblIcon = new JLabel(getIcon(ts.getTenTS()));
            lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
            iconCircle.add(lblIcon);

            // Nội dung text
            JPanel info = new JPanel(new GridLayout(3, 1, 0, 2));
            info.setOpaque(false);

            JLabel lblTen = new JLabel(getDisplayName(ts.getTenTS()));
            lblTen.setFont(new Font("Segoe UI", Font.BOLD, 15));
            lblTen.setForeground(AppColor.TEXT_PRIMARY);

            JLabel lblVal = new JLabel(formatValue(ts));
            lblVal.setFont(new Font("Segoe UI", Font.BOLD, 22));
            lblVal.setForeground(getValueColor(ts.getTenTS()));

            JLabel lblMoTa = new JLabel(ts.getMoTa() != null ? ts.getMoTa() : "Chưa có mô tả");
            lblMoTa.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            lblMoTa.setForeground(AppColor.TEXT_SECONDARY);

            info.add(lblTen);
            info.add(lblVal);
            info.add(lblMoTa);

            // Nút sửa (Bo tròn mượt - Chữ căn giữa tuyệt đối)
            JButton btnEdit = new JButton("Sửa") {
                private boolean isBtnHovered = false;
                {
                    addMouseListener(new MouseAdapter() {
                        @Override public void mouseEntered(MouseEvent e) { isBtnHovered = true; repaint(); }
                        @Override public void mouseExited(MouseEvent e) { isBtnHovered = false; repaint(); }
                    });
                }
                
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    if (isBtnHovered) {
                        g2.setColor(AppColor.PRIMARY);
                        g2.fillRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 16, 16);
                        g2.setColor(Color.WHITE);
                    } else {
                        g2.setColor(new Color(250, 250, 250));
                        g2.fillRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 16, 16);
                        
                        g2.setColor(AppColor.PRIMARY);
                        g2.setStroke(new BasicStroke(1.2f));
                        g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 16, 16);
                        
                        g2.setColor(AppColor.PRIMARY); 
                    }

                    // TỰ VẼ CHỮ CĂN GIỮA TUYỆT ĐỐI BẰNG FONTMETRICS
                    g2.setFont(getFont());
                    FontMetrics fm = g2.getFontMetrics();
                    int x = (getWidth() - fm.stringWidth(getText())) / 2;
                    int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                    g2.drawString(getText(), x, y);

                    g2.dispose();
                }
            };
            
            btnEdit.setContentAreaFilled(false);
            btnEdit.setBorderPainted(false);
            btnEdit.setFocusPainted(false);
            btnEdit.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnEdit.setFont(new Font("Segoe UI", Font.BOLD, 13)); 
            
            Dimension btnSize = new Dimension(80, 36);
            btnEdit.setPreferredSize(btnSize);
            btnEdit.setMinimumSize(btnSize);
            btnEdit.setMaximumSize(btnSize);
            
            btnEdit.addActionListener(e -> showEditDialog(ts.getMaTS()));

            JPanel btnWrap = new JPanel(new GridBagLayout());
            btnWrap.setOpaque(false);
            btnWrap.add(btnEdit);

            add(iconCircle, BorderLayout.WEST);
            add(info, BorderLayout.CENTER);
            add(btnWrap, BorderLayout.EAST);

            // Sự kiện Hover cho Card
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { isHover = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { isHover = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // 1. Vẽ bóng đổ (Shadow) lệnh xuống góc dưới bên phải
            g2.setColor(new Color(0, 0, 0, 12));
            g2.fillRoundRect(4, 4, w - 6, h - 6, 20, 20);

            // 2. Vẽ nền Card lùi vào
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(1, 1, w - 6, h - 6, 20, 20);

            // 3. Vẽ viền Card không bị lẹm
            if (isHover) {
                g2.setColor(AppColor.PRIMARY);
                g2.setStroke(new BasicStroke(1.5f));
            } else {
                g2.setColor(new Color(225, 230, 235));
                g2.setStroke(new BasicStroke(1.2f));
            }
            g2.drawRoundRect(1, 1, w - 6, h - 6, 20, 20);

            g2.dispose();
        }
    }

    private void showEditDialog(String maTS) {
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        ThamSoForm form = new ThamSoForm(maTS);
        
        JDialog dialog = new JDialog(parentWindow, "Cấu hình tham số", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setLayout(new BorderLayout());
        dialog.setResizable(false);
        dialog.add(form, BorderLayout.CENTER);

        JPanel bottomWrapper = new JPanel(new BorderLayout());
        bottomWrapper.setBackground(AppColor.SURFACE);
        bottomWrapper.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, AppColor.BORDER), 
                BorderFactory.createEmptyBorder(16, 32, 16, 32)
        ));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0)); 
        buttonPanel.setBackground(AppColor.SURFACE);

        JButton btnCancel = new JButton("Hủy") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AppColor.ERROR);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCancel.setPreferredSize(new Dimension(100, 42)); 
        btnCancel.setContentAreaFilled(false); 
        btnCancel.setBorderPainted(false);
        btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton btnSave = new JButton("Xác nhận") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AppColor.SUCCESS); 
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSave.setPreferredSize(new Dimension(120, 42));
        btnSave.setContentAreaFilled(false);
        btnSave.setBorderPainted(false);
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnCancel.addActionListener(e -> dialog.dispose());
        btnSave.addActionListener(e -> {
            if (form.saveData()) {
                dialog.dispose();
                loadData(); 
            }
        });

        buttonPanel.add(btnCancel);
        buttonPanel.add(btnSave);

        bottomWrapper.add(buttonPanel, BorderLayout.CENTER);
        dialog.add(bottomWrapper, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setLocationRelativeTo(parentWindow);
        dialog.setVisible(true);
    }

    // --- Helper UI ---
    static class RoundedPanel extends JPanel {
        private final int arc;
        RoundedPanel(int arc) { this.arc = arc; setOpaque(false); }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            for (int i = 5; i >= 1; i--) {
                g2.setColor(new Color(0, 0, 0, 6));
                g2.fill(new RoundRectangle2D.Float(i, i + 1,
                        getWidth() - i * 2, getHeight() - i * 2, arc, arc));
            }
            g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Float(1, 1, getWidth() - 5, getHeight() - 5, arc, arc));
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private JButton createIconButton(String svgPath) {
        JButton btnRefresh = new JButton();
        try {
            btnRefresh.setIcon(new com.formdev.flatlaf.extras.FlatSVGIcon(svgPath, 18, 18));
        } catch (Throwable ex) {
            btnRefresh.setText("↻");
            btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 18));
        }
        btnRefresh.setPreferredSize(new Dimension(42, 42)); 
        btnRefresh.setContentAreaFilled(false);
        btnRefresh.setFocusPainted(false);
        btnRefresh.setBorderPainted(false);
        btnRefresh.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        btnRefresh.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btnRefresh.putClientProperty("hover", true); btnRefresh.repaint(); }
            @Override public void mouseExited(MouseEvent e) { btnRefresh.putClientProperty("hover", false); btnRefresh.repaint(); }
        });
        
        btnRefresh.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean isHovered = Boolean.TRUE.equals(c.getClientProperty("hover"));
                
                g2.setColor(isHovered ? new Color(243, 244, 246) : Color.WHITE);
                g2.fillRoundRect(1, 1, c.getWidth() - 3, c.getHeight() - 3, 12, 12);
                
                g2.setColor(new Color(209, 213, 219));
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(1, 1, c.getWidth() - 3, c.getHeight() - 3, 12, 12);
                
                g2.dispose();
                super.paint(g, c);
            }
        });
        return btnRefresh;
    }

    private String getDisplayName(String tenTS) {
        switch (tenTS) {
            case "MIN_TONKHO":          return "Tồn kho tối thiểu";
            case "CANHBAO_HETHAN":      return "Cảnh báo hạn sử dụng";
            case "MAX_TG_THANHTOAN":    return "Thời gian chờ thanh toán";
            case "SHIP_THUONG":         return "Phí ship Khách Thường";
            case "SHIP_THANTHIET":      return "Phí ship Khách Thân thiết";
            case "SHIP_VIP":            return "Phí ship Khách VIP";
            case "GG_THUONG":           return "Ưu đãi Khách Thường";
            case "GG_THANTHIET":        return "Ưu đãi Khách Thân thiết";
            case "GG_VIP":              return "Ưu đãi Khách VIP";
            default:                    return tenTS;
        }
    }

    private String formatValue(ThamSoDTO ts) {
        switch (ts.getTenTS()) {
            case "SHIP_THUONG": case "SHIP_THANTHIET": case "SHIP_VIP":
                return NumberFormat.getCurrencyInstance(new Locale("vi", "VN")).format(ts.getGiaTri());
            case "MIN_TONKHO": return (long) ts.getGiaTri() + " SP";
            case "CANHBAO_HETHAN": return (long) ts.getGiaTri() + " Ngày";
            case "MAX_TG_THANHTOAN": return (long) ts.getGiaTri() + " Giờ";
            case "GG_THUONG": case "GG_THANTHIET": case "GG_VIP":
                return String.format("%.0f%%", ts.getGiaTri() * 100);
            default: return String.valueOf(ts.getGiaTri());
        }
    }

    private String getIcon(String tenTS) {
        switch (tenTS) {
            case "MIN_TONKHO": return "📦";
            case "CANHBAO_HETHAN": return "⌛";
            case "MAX_TG_THANHTOAN": return "💳";
            case "SHIP_THUONG": return "🛵";
            case "SHIP_THANTHIET": return "🚚";
            case "SHIP_VIP": return "🚀";
            case "GG_THUONG": return "🎫";
            case "GG_THANTHIET": return "✨";
            case "GG_VIP": return "💎";
            default: return "⚙️";
        }
    }

    private Color getIconBg(String tenTS) {
        switch (tenTS) {
            case "SHIP_THUONG": case "SHIP_THANTHIET": case "SHIP_VIP": return new Color(255, 247, 237); 
            case "MIN_TONKHO": return new Color(240, 253, 244); 
            case "CANHBAO_HETHAN": return new Color(254, 242, 242); 
            case "MAX_TG_THANHTOAN": return new Color(239, 246, 255); 
            default: return new Color(245, 245, 245);
        }
    }

    private Color getValueColor(String tenTS) {
        if (tenTS.contains("GG_")) return new Color(13, 148, 136); 
        if (tenTS.equals("CANHBAO_HETHAN")) return new Color(220, 38, 38); 
        return AppColor.PRIMARY;
    }
}