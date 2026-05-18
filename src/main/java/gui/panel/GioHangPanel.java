package gui.panel;

import dto.GioHangDTO;
import gui.MainFrame;
import gui.dialog.ThanhToanForm;
import util.AppColor;
import util.Session;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GioHangPanel extends JPanel {

    private final MainFrame parentFrame;
    private JPanel pnlItems;
    private JLabel lblTongTienHang;
    private JLabel lblThanhTien;
    private JLabel lblBadgeCount;
    private ModernSearchField txtSearch;
    private JScrollPane scrollPane;

    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    public GioHangPanel(MainFrame parent) {
        this.parentFrame = parent;
        initComponents();
        loadCartItems();
    }

    private void initComponents() {
        setLayout(new BorderLayout(24, 24));
        setBackground(AppColor.BACKGROUND);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        // Bắt sự kiện click ra ngoài để bỏ focus khỏi ô tìm kiếm
        setFocusable(true);
        addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) { requestFocusInWindow(); }
        });

        // =========================================
        // 1. HEADER CÓ BÓNG ĐỔ (TITLE CARD)
        // =========================================
        RoundedPanel pnlHeaderCard = new RoundedPanel(16);
        pnlHeaderCard.setBackground(AppColor.SURFACE != null ? AppColor.SURFACE : Color.WHITE);
        pnlHeaderCard.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10));
        pnlHeaderCard.setBorder(new EmptyBorder(14, 20, 14, 20));

        JLabel lblTitle = new JLabel("Giỏ Hàng Của Tôi");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(AppColor.TEXT_PRIMARY != null ? AppColor.TEXT_PRIMARY : new Color(15, 23, 42));

        lblBadgeCount = new JLabel(Session.getCartItemCount() + " sản phẩm") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(16, 185, 129)); // Xanh lục
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lblBadgeCount.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblBadgeCount.setForeground(Color.WHITE);
        lblBadgeCount.setOpaque(false);
        lblBadgeCount.setBorder(new EmptyBorder(6, 14, 6, 14));

        pnlHeaderCard.add(lblTitle);
        pnlHeaderCard.add(lblBadgeCount);

        add(pnlHeaderCard, BorderLayout.NORTH);

        // =========================================
        // 2. MAIN CONTENT (CHIA 2 CỘT)
        // =========================================
        JPanel pnlContent = new JPanel(new BorderLayout(24, 0));
        pnlContent.setOpaque(false);

        // --- CỘT TRÁI (Danh sách Sản phẩm) ---
        JPanel pnlLeft = new JPanel(new BorderLayout(0, 16));
        pnlLeft.setOpaque(false);

        // Toolbar: Sắp xếp, Nút xóa, Refresh
        JPanel pnlToolbar = new JPanel(new BorderLayout());
        pnlToolbar.setOpaque(false);
        
        JPanel pnlToolLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlToolLeft.setOpaque(false);
        
        // TRẢ LẠI: Label và JComboBox Sắp xếp
        JLabel lblSort = new JLabel("Sắp xếp:");
        lblSort.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblSort.setForeground(new Color(75, 85, 99));
        
        JComboBox<String> cbxSort = new JComboBox<>(new String[]{"Mới nhất", "Cũ nhất", "Giá cao - thấp"});
        cbxSort.setPreferredSize(new Dimension(130, 36));
        cbxSort.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbxSort.setBackground(Color.WHITE);
        
        pnlToolLeft.add(lblSort);
        pnlToolLeft.add(cbxSort);
        
        // Nút Xóa tất cả
        RoundedButton btnClearAll = new RoundedButton("Xóa tất cả", 10, new Color(239, 68, 68));
        btnClearAll.setPreferredSize(new Dimension(100, 36));
        btnClearAll.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnClearAll.setForeground(Color.WHITE);
        btnClearAll.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa tất cả sản phẩm?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if(confirm == JOptionPane.YES_OPTION){
                Session.clearCart();
                if (parentFrame != null) parentFrame.updateCartBadge();
                loadCartItems();
            }
        });
        
        // TRẢ LẠI: Nút Refresh
        JButton btnRefresh = createIconButton("icons/refresh.svg", new Dimension(36, 36));
        btnRefresh.setToolTipText("Làm mới giỏ hàng");
        btnRefresh.addActionListener(e -> loadCartItems());
        
        pnlToolLeft.add(btnClearAll);
        pnlToolLeft.add(btnRefresh);

        JPanel pnlToolRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        pnlToolRight.setOpaque(false);
        txtSearch = new ModernSearchField("Tìm sản phẩm trong giỏ...");
        txtSearch.setPreferredSize(new Dimension(240, 36));
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterCart(); }
            public void removeUpdate(DocumentEvent e) { filterCart(); }
            public void changedUpdate(DocumentEvent e) { filterCart(); }
        });
        pnlToolRight.add(txtSearch);

        pnlToolbar.add(pnlToolLeft, BorderLayout.WEST);
        pnlToolbar.add(pnlToolRight, BorderLayout.EAST);

        // Vùng cuộn chứa item
        pnlItems = new JPanel();
        pnlItems.setLayout(new BoxLayout(pnlItems, BoxLayout.Y_AXIS));
        pnlItems.setOpaque(false);
        pnlItems.setBorder(new EmptyBorder(4, 4, 4, 4)); // Padding để bóng đổ không bị cắt

        scrollPane = new JScrollPane(pnlItems);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));

        pnlLeft.add(pnlToolbar, BorderLayout.NORTH);
        pnlLeft.add(scrollPane, BorderLayout.CENTER);

        // --- CỘT PHẢI (Hóa đơn - Có bóng đổ) ---
        RoundedPanel pnlRight = new RoundedPanel(16);
        pnlRight.setBackground(Color.WHITE);
        pnlRight.setPreferredSize(new Dimension(340, 0));
        pnlRight.setLayout(new BorderLayout());
        pnlRight.setBorder(new EmptyBorder(24, 24, 24, 24));

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
        pnlBillDetail.add(createRow("Phí vận chuyển", new JLabel("Miễn phí", SwingConstants.RIGHT) {{ setForeground(new Color(16, 185, 129)); }}));
        pnlBillDetail.add(Box.createVerticalStrut(20));
        
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setForeground(new Color(229, 231, 235));
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

        RoundedButton btnCheckout = new RoundedButton("TIẾN HÀNH ĐẶT HÀNG", 12, new Color(16, 185, 129));
        btnCheckout.setPreferredSize(new Dimension(0, 50));
        btnCheckout.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnCheckout.setForeground(Color.WHITE);
        btnCheckout.addActionListener(e -> processCheckout());

        pnlRight.add(pnlBillDetail, BorderLayout.CENTER);
        pnlRight.add(btnCheckout, BorderLayout.SOUTH);

        pnlContent.add(pnlLeft, BorderLayout.CENTER);
        pnlContent.add(pnlRight, BorderLayout.EAST);

        add(pnlContent, BorderLayout.CENTER);
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
            lblEmpty.setForeground(new Color(156, 163, 175));
            pnlEmpty.add(lblEmpty);
            pnlItems.add(pnlEmpty);
        } else {
            for (GioHangDTO item : listToRender) {
                pnlItems.add(createItemCard(item));
                pnlItems.add(Box.createRigidArea(new Dimension(0, 16))); // Khoảng cách giữa các thẻ
            }
        }
        pnlItems.revalidate();
        pnlItems.repaint();
    }

    public void loadCartItems() {
        renderCartItems(Session.cartCache);
        updateTotalPrice();
        lblBadgeCount.setText(Session.getCartItemCount() + " sản phẩm");
    }

    // =========================================
    // CARD SẢN PHẨM TRONG GIỎ HÀNG (CÓ BÓNG ĐỔ)
    // =========================================
    private JPanel createItemCard(GioHangDTO item) {
        RoundedPanel card = new RoundedPanel(12);
        card.setBackground(Color.WHITE);
        card.setLayout(new BorderLayout(16, 0));
        card.setBorder(new EmptyBorder(16, 16, 16, 16));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 125));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Ảnh giả lập
        JLabel lblImage = new JLabel("IMG", SwingConstants.CENTER);
        lblImage.setPreferredSize(new Dimension(90, 90));
        lblImage.setBackground(new Color(243, 244, 246));
        lblImage.setOpaque(true);
        lblImage.setForeground(new Color(156, 163, 175));
        lblImage.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JPanel pnlInfo = new JPanel(new BorderLayout());
        pnlInfo.setOpaque(false);
        
        JPanel pnlNamePrice = new JPanel(new GridLayout(2, 1, 0, 4));
        pnlNamePrice.setOpaque(false);
        JLabel lblName = new JLabel(item.getTenSP());
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblName.setForeground(new Color(31, 41, 55));
        JLabel lblPrice = new JLabel(currencyFormat.format(item.getDonGia()));
        lblPrice.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblPrice.setForeground(new Color(107, 114, 128));
        
        pnlNamePrice.add(lblName);
        pnlNamePrice.add(lblPrice);

        // Tăng giảm số lượng
        JPanel pnlQty = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlQty.setOpaque(false);
        
        JLabel lblItemTotal = new JLabel(currencyFormat.format(item.getThanhTien()));
        lblItemTotal.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblItemTotal.setForeground(new Color(16, 185, 129));

        JButton btnMinus = new JButton("–"); styleQtyButton(btnMinus);
        JLabel lblQty = new JLabel(String.valueOf((int)item.getSoLuong()), SwingConstants.CENTER);
        lblQty.setPreferredSize(new Dimension(45, 32));
        lblQty.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblQty.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, new Color(209, 213, 219)));
        JButton btnPlus = new JButton("+"); styleQtyButton(btnPlus);

        pnlQty.add(btnMinus); pnlQty.add(lblQty); pnlQty.add(btnPlus);
        pnlInfo.add(pnlNamePrice, BorderLayout.NORTH);
        pnlInfo.add(pnlQty, BorderLayout.SOUTH);

        JPanel pnlAction = new JPanel();
        pnlAction.setLayout(new BoxLayout(pnlAction, BoxLayout.Y_AXIS));
        pnlAction.setOpaque(false);

        RoundedButton btnDelete = new RoundedButton("Xóa", 8, new Color(239, 68, 68));
        btnDelete.setPreferredSize(new Dimension(75, 32));
        btnDelete.setMaximumSize(new Dimension(75, 32)); 
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setFont(new Font("Segoe UI", Font.BOLD, 12));

        lblItemTotal.setAlignmentX(Component.RIGHT_ALIGNMENT);
        btnDelete.setAlignmentX(Component.RIGHT_ALIGNMENT);

        pnlAction.add(lblItemTotal);
        pnlAction.add(Box.createVerticalGlue()); 
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
        BigDecimal total = BigDecimal.ZERO;
        for (GioHangDTO item : Session.cartCache) {
            total = total.add(item.getThanhTien());
        }
        ThanhToanForm thanhToanForm = new ThanhToanForm(parentFrame, total);
        thanhToanForm.setVisible(true);
    }

    // =========================================
    // HỖ TRỢ GIAO DIỆN & EFFECTS
    // =========================================

    private void styleQtyButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(Color.WHITE);
        btn.setForeground(new Color(55, 65, 81));
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(32, 32));
        btn.setBorder(BorderFactory.createLineBorder(new Color(209, 213, 219), 1));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(243, 244, 246)); }
            public void mouseExited(MouseEvent e) { btn.setBackground(Color.WHITE); }
        });
    }

    private JButton createIconButton(String iconPath, Dimension size) {
        JButton btn = new JButton();
        btn.setPreferredSize(size);
        
        // Load icon SVG, nếu lỗi sẽ tự động lùi về dùng icon text "↻"
        try {
            btn.setIcon(new com.formdev.flatlaf.extras.FlatSVGIcon(iconPath, 18, 18));
        } catch (Exception ex) {
            btn.setText("↻");
            btn.setFont(new Font("Segoe UI Emoji", Font.BOLD, 18));
        }
        
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                AbstractButton b = (AbstractButton) c;
                boolean isHovered = b.getModel().isRollover();
                
                g2.setColor(isHovered ? new Color(243, 244, 246) : Color.WHITE);
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 10, 10);
                g2.setColor(new Color(209, 213, 219)); g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(1, 1, c.getWidth() - 3, c.getHeight() - 3, 10, 10);
                g2.dispose(); super.paint(g, c);
            }
        });
        return btn;
    }
    
    static class ModernSearchField extends JTextField {
        private final String placeholder;
        private boolean isHovered = false;
        public ModernSearchField(String placeholder) {
            this.placeholder = placeholder; setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 36));
            setFont(new Font("Segoe UI", Font.PLAIN, 13)); 
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { isHovered = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { isHovered = false; repaint(); }
            });
            addFocusListener(new FocusAdapter() {
                @Override public void focusGained(FocusEvent e) { repaint(); }
                @Override public void focusLost(FocusEvent e) { isHovered = false; repaint(); }
            });
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE); g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            if (isFocusOwner()) { g2.setColor(new Color(59, 130, 246)); g2.setStroke(new BasicStroke(1.5f)); } 
            else if (isHovered) { g2.setColor(new Color(156, 163, 175)); g2.setStroke(new BasicStroke(1f)); } 
            else { g2.setColor(new Color(209, 213, 219)); g2.setStroke(new BasicStroke(1f)); }
            g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 12, 12);
            super.paintComponent(g);
            if (getText().isEmpty() && !isFocusOwner()) {
                g2.setColor(new Color(156, 163, 175));
                FontMetrics fm = g.getFontMetrics();
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(placeholder, 16, y);
            }
            g2.setColor(new Color(156, 163, 175));
            int x = getWidth() - 26; int y = (getHeight() - 14) / 2;
            g2.setStroke(new BasicStroke(2f)); g2.drawOval(x, y, 10, 10); g2.drawLine(x + 8, y + 8, x + 13, y + 13);
            g2.dispose();
        }
    }

    public class RoundedButton extends JButton {
        private final int radius; private final Color bgColor;
        public RoundedButton(String text, int radius, Color bgColor) {
            super(text); this.radius = radius; this.bgColor = bgColor;
            setOpaque(false); setContentAreaFilled(false); setBorderPainted(false); setFocusPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getModel().isRollover() ? bgColor.darker() : bgColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.dispose(); super.paintComponent(g); 
        }
    }

    // PANEL CÓ BÓNG ĐỔ (SHADOW)
    static class RoundedPanel extends JPanel {
        private final int arc;
        RoundedPanel(int arc) { this.arc = arc; setOpaque(false); }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // Vẽ 5 lớp shadow mờ dần tạo hiệu ứng bóng đổ
            for (int i = 5; i >= 1; i--) {
                g2.setColor(new Color(0, 0, 0, 6)); // Độ mờ rất thấp
                g2.fill(new RoundRectangle2D.Float(i, i + 1, getWidth() - i * 2, getHeight() - i * 2, arc, arc));
            }
            // Vẽ nền chính
            g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 5, getHeight() - 5, arc, arc));
            g2.dispose(); super.paintComponent(g);
        }
    }
}