package gui.dialog;

import dto.GioHangDTO;
import gui.MainFrame;
import util.Session;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GioHangDialog extends JDialog {

    private final MainFrame parentFrame;
    private JPanel pnlItems;
    private JLabel lblTongTienHang;
    private JLabel lblThanhTien;
    private JLabel lblBadgeCount;
    private SearchField txtSearch;
    private JScrollPane scrollPane;

    private int pX, pY;

    // Format tiền tệ Việt Nam
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    public GioHangDialog(MainFrame parent) {
        super(parent, true);
        this.parentFrame = parent;
        setUndecorated(true);
        // Đặt JDialog trong suốt hoàn toàn
        setBackground(new Color(0, 0, 0, 0)); 
        initComponents();
        loadCartItems();
    }

    private void initComponents() {
        setSize(1000, 650);
        setLocationRelativeTo(parentFrame);

        // Tự vẽ Background và Viền đen bằng Graphics2D
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Vẽ nền màu xám nhạt
                g2.setColor(new Color(245, 247, 250));
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                
                // Vẽ viền đen bao quanh
                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(1.0f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        mainPanel.setOpaque(false);
        mainPanel.setBorder(new EmptyBorder(20, 25, 25, 25));

        // Kéo thả cửa sổ
        mainPanel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent me) { pX = me.getX(); pY = me.getY(); }
        });
        mainPanel.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent me) {
                setLocation(getLocation().x + me.getX() - pX, getLocation().y + me.getY() - pY);
            }
        });

        // --- HEADER ---
        JPanel pnlHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        pnlHeader.setOpaque(false);

        JLabel lblTitle = new JLabel("CHI TIẾT GIỎ HÀNG");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(new Color(15, 23, 42));

        // Custom JLabel vẽ nền bo góc cho số lượng
        lblBadgeCount = new JLabel(Session.getCartItemCount() + " sản phẩm") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(16, 185, 129));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lblBadgeCount.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblBadgeCount.setForeground(Color.WHITE);
        lblBadgeCount.setOpaque(false);
        lblBadgeCount.setBorder(new EmptyBorder(5, 12, 5, 12));

        // SỬA ICON NÚT TẮT X: Vẽ trực tiếp bằng code, dẹp bỏ SVG gây lỗi
        JButton btnClose = new JButton() {
            private boolean isHovered = false;
            {
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { isHovered = true; repaint(); }
                    @Override public void mouseExited(MouseEvent e) { isHovered = false; repaint(); }
                });
            }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int size = 26; // Kích thước hình tròn
                int x = (getWidth() - size) / 2;
                int y = (getHeight() - size) / 2;

                // Nền khi Hover vào
                if (isHovered) {
                    g2.setColor(new Color(226, 232, 240));
                    g2.fillOval(x, y, size, size);
                }

                // Vẽ dấu X màu đỏ
                g2.setColor(new Color(220, 38, 38));
                g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int padding = x + 8;
                g2.drawLine(padding, padding, getWidth() - padding, getHeight() - padding);
                g2.drawLine(getWidth() - padding, padding, padding, getHeight() - padding);
                
                g2.dispose();
            }
        };
        btnClose.setPreferredSize(new Dimension(30, 30));
        btnClose.setContentAreaFilled(false);
        btnClose.setBorderPainted(false);
        btnClose.setFocusPainted(false);
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClose.addActionListener(e -> dispose());
        
        JPanel headerWrapper = new JPanel(new BorderLayout());
        headerWrapper.setOpaque(false);
        pnlHeader.add(lblTitle);
        pnlHeader.add(lblBadgeCount);
        headerWrapper.add(pnlHeader, BorderLayout.WEST);
        headerWrapper.add(btnClose, BorderLayout.EAST);

        // --- MAIN CONTENT (Chia 2 cột) ---
        JPanel pnlContent = new JPanel(new BorderLayout(20, 0));
        pnlContent.setOpaque(false);

        // ===== CỘT TRÁI (Sản phẩm) =====
        JPanel pnlLeft = new JPanel(new BorderLayout(0, 15));
        pnlLeft.setOpaque(false);

        // Toolbar
        JPanel pnlToolbar = new JPanel(new BorderLayout());
        pnlToolbar.setOpaque(false);
        pnlToolbar.setBorder(new EmptyBorder(0, 0, 15, 0)); // Cách danh sách bên dưới 15px
        
        JPanel pnlToolLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlToolLeft.setOpaque(false);
        pnlToolLeft.add(new JLabel("Sắp xếp:"));
        
        JComboBox<String> cbxSort = new JComboBox<>(new String[]{"Mới nhất", "Cũ nhất", "Giá cao - thấp"});
        cbxSort.setPreferredSize(new Dimension(130, 35)); // GIẢM KÍCH THƯỚC (từ 150 -> 130)
        cbxSort.setBorder(new RoundedBorder(8, Color.LIGHT_GRAY));
        cbxSort.setBackground(Color.WHITE);
        pnlToolLeft.add(cbxSort);
        
        RoundedButton btnClearAll = new RoundedButton("Xóa tất cả", 8, new Color(220, 38, 38));
        btnClearAll.setPreferredSize(new Dimension(90, 35)); // GIẢM KÍCH THƯỚC (từ 100 -> 90)
        btnClearAll.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnClearAll.setForeground(Color.WHITE);
        btnClearAll.addActionListener(e -> {
            Session.clearCart();
            if (parentFrame != null) parentFrame.updateCartBadge();
            loadCartItems();
        });
        pnlToolLeft.add(btnClearAll);
        
        // NÚT REFRESH
        JButton btnRefresh = new JButton("↻") {
            private boolean isHovered = false;
            {
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { isHovered = true; repaint(); }
                    @Override public void mouseExited(MouseEvent e) { isHovered = false; repaint(); }
                });
            }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isHovered ? new Color(243, 244, 246) : Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        btnRefresh.setFont(new Font("Segoe UI Emoji", Font.BOLD, 18));
        btnRefresh.setPreferredSize(new Dimension(35, 35));
        btnRefresh.setContentAreaFilled(false);
        btnRefresh.setBorder(new RoundedBorder(8, Color.LIGHT_GRAY));
        btnRefresh.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefresh.addActionListener(e -> loadCartItems());
        pnlToolLeft.add(btnRefresh);

        // THANH TÌM KIẾM
        txtSearch = new SearchField("Tìm tên sản phẩm...");
        txtSearch.setPreferredSize(new Dimension(190, 35)); // GIẢM KÍCH THƯỚC (từ 250 -> 190)
        txtSearch.setBorder(new EmptyBorder(5, 35, 5, 10)); 
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterCart(); }
            public void removeUpdate(DocumentEvent e) { filterCart(); }
            public void changedUpdate(DocumentEvent e) { filterCart(); }
        });
        
        pnlToolbar.add(pnlToolLeft, BorderLayout.WEST);

        // ĐÃ SỬA: Đưa thanh tìm kiếm vào Panel riêng và ÉP CHẾT khoảng cách bên trái 20px
        JPanel pnlToolRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        pnlToolRight.setOpaque(false);
        pnlToolRight.setBorder(new EmptyBorder(0, 20, 0, 0)); // LUÔN LUÔN DUY TRÌ KHOẢNG CÁCH 20PX
        pnlToolRight.add(txtSearch);

        pnlToolbar.add(pnlToolRight, BorderLayout.EAST);

        // Vùng cuộn
        pnlItems = new JPanel();
        pnlItems.setLayout(new BoxLayout(pnlItems, BoxLayout.Y_AXIS));
        pnlItems.setOpaque(false);

        scrollPane = new JScrollPane(pnlItems);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(6, 0));

        pnlLeft.add(pnlToolbar, BorderLayout.NORTH);
        pnlLeft.add(scrollPane, BorderLayout.CENTER);

        // ===== CỘT PHẢI (Hóa đơn) =====
        RoundedPanel pnlRight = new RoundedPanel(20, Color.WHITE);
        pnlRight.setPreferredSize(new Dimension(320, 0));
        pnlRight.setLayout(new BorderLayout());
        pnlRight.setBorder(new EmptyBorder(25, 20, 25, 20));

        JPanel pnlBillDetail = new JPanel();
        pnlBillDetail.setLayout(new BoxLayout(pnlBillDetail, BoxLayout.Y_AXIS));
        pnlBillDetail.setOpaque(false);

        JLabel lblBillTitle = new JLabel("TỔNG THANH TOÁN", SwingConstants.CENTER);
        lblBillTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblBillTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        pnlBillDetail.add(lblBillTitle);
        pnlBillDetail.add(Box.createVerticalStrut(30));
        pnlBillDetail.add(createRow("Tổng tiền hàng", lblTongTienHang = new JLabel("0 đ")));
        pnlBillDetail.add(Box.createVerticalStrut(15));
        pnlBillDetail.add(createRow("Phí vận chuyển", new JLabel("Chưa tính")));
        pnlBillDetail.add(Box.createVerticalStrut(20));
        
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        pnlBillDetail.add(sep);
        pnlBillDetail.add(Box.createVerticalStrut(20));

        JPanel pnlThanhTien = new JPanel(new BorderLayout());
        pnlThanhTien.setOpaque(false);
        JLabel lblTxtThanhTien = new JLabel("Thành tiền:");
        lblTxtThanhTien.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblThanhTien = new JLabel("0 đ");
        lblThanhTien.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblThanhTien.setForeground(new Color(16, 185, 129));
        
        pnlThanhTien.add(lblTxtThanhTien, BorderLayout.NORTH);
        pnlThanhTien.add(lblThanhTien, BorderLayout.SOUTH);
        pnlBillDetail.add(pnlThanhTien);

        RoundedButton btnCheckout = new RoundedButton("TIẾN HÀNH ĐẶT HÀNG", 10, new Color(16, 185, 129));
        btnCheckout.setPreferredSize(new Dimension(0, 45));
        btnCheckout.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnCheckout.setForeground(Color.WHITE);
        btnCheckout.addActionListener(e -> processCheckout());

        pnlRight.add(pnlBillDetail, BorderLayout.CENTER);
        pnlRight.add(btnCheckout, BorderLayout.SOUTH);

        pnlContent.add(pnlLeft, BorderLayout.CENTER);
        pnlContent.add(pnlRight, BorderLayout.EAST);

        mainPanel.add(headerWrapper, BorderLayout.NORTH);
        mainPanel.add(pnlContent, BorderLayout.CENTER);

        add(mainPanel);
    }

    private JPanel createRow(String text, JLabel valueLabel) {
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.setOpaque(false);
        JLabel lblTxt = new JLabel(text);
        lblTxt.setForeground(new Color(107, 114, 128));
        pnl.add(lblTxt, BorderLayout.WEST);
        pnl.add(valueLabel, BorderLayout.EAST);
        pnl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        return pnl;
    }

    // --- LOGIC TÌM KIẾM ---
    private void filterCart() {
        String keyword = txtSearch.getText().trim().toLowerCase();
        if (keyword.isEmpty()) {
            renderCartItems(Session.cartCache);
            return;
        }
        List<GioHangDTO> filteredList = new ArrayList<>();
        if(Session.cartCache != null){
            for (GioHangDTO item : Session.cartCache) {
                if (item.getTenSP().toLowerCase().contains(keyword)) {
                    filteredList.add(item);
                }
            }
        }
        renderCartItems(filteredList);
    }

    private void renderCartItems(List<GioHangDTO> listToRender) {
        pnlItems.removeAll();
        if (listToRender == null || listToRender.isEmpty()) {
            JPanel pnlEmpty = new JPanel(new GridBagLayout());
            pnlEmpty.setOpaque(false);
            JLabel lblEmpty = new JLabel("Không tìm thấy sản phẩm hoặc giỏ hàng trống!");
            lblEmpty.setFont(new Font("Segoe UI", Font.ITALIC, 15));
            lblEmpty.setForeground(Color.GRAY);
            pnlEmpty.add(lblEmpty);
            pnlItems.add(pnlEmpty);
        } else {
            for (GioHangDTO item : listToRender) {
                pnlItems.add(createItemCard(item));
                pnlItems.add(Box.createRigidArea(new Dimension(0, 15))); 
            }
        }
        pnlItems.revalidate();
        pnlItems.repaint();
    }

    private void loadCartItems() {
        renderCartItems(Session.cartCache);
        updateTotalPrice();
        lblBadgeCount.setText(Session.getCartItemCount() + " sản phẩm");
    }

    private JPanel createItemCard(GioHangDTO item) {
        RoundedPanel card = new RoundedPanel(15, Color.WHITE);
        card.setLayout(new BorderLayout(15, 0));
        card.setBorder(new EmptyBorder(15, 15, 15, 15));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        JLabel lblImage = new JLabel("IMG", SwingConstants.CENTER);
        lblImage.setPreferredSize(new Dimension(90, 90));
        lblImage.setBackground(new Color(243, 244, 246));
        lblImage.setOpaque(true);

        JPanel pnlInfo = new JPanel(new BorderLayout());
        pnlInfo.setOpaque(false);
        JPanel pnlNamePrice = new JPanel(new GridLayout(2, 1, 0, 5));
        pnlNamePrice.setOpaque(false);
        
        JLabel lblName = new JLabel(item.getTenSP());
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 16));
        JLabel lblPrice = new JLabel(currencyFormat.format(item.getDonGia()));
        lblPrice.setForeground(Color.GRAY);
        pnlNamePrice.add(lblName);
        pnlNamePrice.add(lblPrice);

        // Control Qty
        JPanel pnlQty = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlQty.setOpaque(false);
        
        JLabel lblItemTotal = new JLabel(currencyFormat.format(item.getThanhTien()));
        lblItemTotal.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblItemTotal.setForeground(new Color(16, 185, 129));

        JButton btnMinus = new JButton("-"); styleQtyButton(btnMinus);
        JLabel lblQty = new JLabel(String.valueOf((int)item.getSoLuong()), SwingConstants.CENTER);
        lblQty.setPreferredSize(new Dimension(40, 30));
        lblQty.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, new Color(209, 213, 219)));
        JButton btnPlus = new JButton("+"); styleQtyButton(btnPlus);

        pnlQty.add(btnMinus); pnlQty.add(lblQty); pnlQty.add(btnPlus);
        pnlInfo.add(pnlNamePrice, BorderLayout.NORTH);
        pnlInfo.add(pnlQty, BorderLayout.SOUTH);

        // Action Delete - CĂN CHỈNH LẠI KHOẢNG CÁCH NÚT CHO ĐẸP
        JPanel pnlAction = new JPanel();
        pnlAction.setLayout(new BoxLayout(pnlAction, BoxLayout.Y_AXIS));
        pnlAction.setOpaque(false);

        RoundedButton btnDelete = new RoundedButton("Xóa", 8, new Color(220, 38, 38));
        btnDelete.setPreferredSize(new Dimension(80, 30));
        btnDelete.setMaximumSize(new Dimension(80, 30)); // Cố định chiều cao ko bị kéo giãn
        btnDelete.setForeground(Color.WHITE);

        lblItemTotal.setAlignmentX(Component.RIGHT_ALIGNMENT);
        btnDelete.setAlignmentX(Component.RIGHT_ALIGNMENT);

        pnlAction.add(lblItemTotal);
        pnlAction.add(Box.createVerticalGlue()); // Tự động đẩy nút Delete xuống và giãn đều khoảng cách
        pnlAction.add(btnDelete);

        // Events
        btnMinus.addActionListener(e -> {
            if (item.getSoLuong() > 1) {
                item.setSoLuong(item.getSoLuong() - 1);
                lblQty.setText(String.valueOf((int)item.getSoLuong()));
                lblItemTotal.setText(currencyFormat.format(item.getThanhTien()));
                Session.upsertCartCache(item);
                updateTotalPrice();
            }
        });

        btnPlus.addActionListener(e -> {
            item.setSoLuong(item.getSoLuong() + 1);
            lblQty.setText(String.valueOf((int)item.getSoLuong()));
            lblItemTotal.setText(currencyFormat.format(item.getThanhTien()));
            Session.upsertCartCache(item);
            updateTotalPrice();
        });

        btnDelete.addActionListener(e -> {
            Session.removeFromCartCache(item.getMaSP());
            if (parentFrame != null) parentFrame.updateCartBadge();
            loadCartItems();
        });

        card.add(lblImage, BorderLayout.WEST);
        card.add(pnlInfo, BorderLayout.CENTER);
        card.add(pnlAction, BorderLayout.EAST);

        return card;
    }

    private void updateTotalPrice() {
        BigDecimal total = BigDecimal.ZERO;
        if (Session.cartCache != null) {
            for (GioHangDTO item : Session.cartCache) {
                total = total.add(item.getThanhTien());
            }
        }
        lblTongTienHang.setText(currencyFormat.format(total));
        lblThanhTien.setText(currencyFormat.format(total));
    }

    private void processCheckout() {
        if (Session.cartCache == null || Session.cartCache.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Giỏ hàng trống!");
            return;
        }
        Session.clearCart();
        if (parentFrame != null) parentFrame.updateCartBadge();
        dispose();
    }

    private void styleQtyButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(35, 30));
        btn.setBorder(BorderFactory.createLineBorder(new Color(209, 213, 219), 1));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    // ================= CLASS TIỆN ÍCH CUSTOM GIAO DIỆN NẰM BÊN TRONG =================

    // 1. Ô Input Tìm Kiếm
    class SearchField extends JTextField {
        private final String placeholder;

        public SearchField(String placeholder) {
            this.placeholder = placeholder;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Vẽ nền trắng bo góc
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
            
            // Vẽ Border xám nhạt
            g2.setColor(Color.LIGHT_GRAY);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);

            // Vẽ Icon kính lúp (Unicode) để không cần hình ảnh
            g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
            g2.setColor(Color.GRAY);
            g2.drawString("🔍", 12, (getHeight() + 10) / 2 - 1);

            super.paintComponent(g);

            // Hiển thị Placeholder
            if (getText().isEmpty()) {
                g2.setColor(Color.GRAY);
                g2.setFont(getFont().deriveFont(Font.ITALIC));
                int y = (getHeight() - g.getFontMetrics().getHeight()) / 2 + g.getFontMetrics().getAscent();
                g2.drawString(placeholder, 35, y); 
            }
            g2.dispose();
        }
    }

    // 2. Nút bấm bo góc custom
    class RoundedButton extends JButton {
        private final int radius;
        private final Color bgColor;

        public RoundedButton(String text, int radius, Color bgColor) {
            super(text);
            this.radius = radius;
            this.bgColor = bgColor;
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getModel().isRollover() ? bgColor.darker() : bgColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.dispose();
            super.paintComponent(g); 
        }
    }

    // 3. Panel bo góc chung
    class RoundedPanel extends JPanel {
        private final int radius;
        private final Color bgColor;
        
        public RoundedPanel(int radius, Color bgColor) {
            this.radius = radius;
            this.bgColor = bgColor;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // 4. Border góc tròn cho Combobox
    class RoundedBorder implements javax.swing.border.Border {
        private int radius;
        private Color color;
        RoundedBorder(int radius, Color color) { this.radius = radius; this.color = color; }
        public Insets getBorderInsets(Component c) { return new Insets(radius/2, radius, radius/2, radius); }
        public boolean isBorderOpaque() { return true; }
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, width-1, height-1, radius, radius);
            g2.dispose();
        }
    }
}