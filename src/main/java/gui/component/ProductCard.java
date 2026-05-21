package gui.component;

import bus.CuaHangBUS;
import bus.GioHangBUS;
import dto.SanPhamDTO;
import gui.MainFrame;
import gui.dialog.ChiTietSanPhamForm;
import util.AppColor;
import util.Session;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ProductCard extends JPanel {

    private SanPhamDTO sp;
    private CuaHangBUS bus = new CuaHangBUS();

    // Khai báo giao diện & giỏ hàng
    private MainFrame parentFrame;
    private int quantity = 1;
    private JTextField txtQty;   // Ô nhập số lượng trực tiếp
    private JLabel lblQty;       // Kept for backward compat – aliased to txtQty display
    private JLabel lblTonKho;
    private JLabel lblWarning;   // Cảnh báo số lượng không hợp lệ (hiện inline)
    private JButton btnMinus;
    private JButton btnPlus;
    private JButton btnAdd;

    // UI
    private int radius = 25;
    private Color borderColor = new Color(230,230,230);

    // Constructor đã nhận thêm MainFrame
    public ProductCard(SanPhamDTO sp, MainFrame parentFrame) {
        this.sp = sp;
        this.parentFrame = parentFrame;
        setOpaque(false);
        initComponents();
        addCardClick(this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        g2.dispose();
        super.paintComponent(g);
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(borderColor);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
        g2.dispose();
    }

    private void setBorderColor(Color color) {
        borderColor = color;
        repaint();
    }

    private double getHienThiTonKho() {
        double slKhaDung = new bus.GioHangBUS().getSlKhaDung(sp.getMaSP());
        if (slKhaDung < 0) slKhaDung = 0;
        
        double dangGiuTrongGio = 0;
        if (Session.isLogged() && Session.currentUser != null && Session.currentUser.getLoaiTK() == 2) {
            try {
                String username = Session.currentUser.getUsername();
                String maKH = null;
                try (Connection con = util.DBConnection.getConnection();
                     PreparedStatement ps = con.prepareStatement("SELECT MaKH FROM KHACHHANG WHERE Username = ?")) {
                    ps.setString(1, username);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            maKH = rs.getString("MaKH");
                        }
                    }
                }
                if (maKH != null) {
                    Session.cartCache = new bus.GioHangBUS().getCart(maKH);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            for (dto.GioHangDTO item : Session.cartCache) {
                if (item.getMaSP().equals(sp.getMaSP())) {
                    dangGiuTrongGio = item.getSoLuong();
                    break;
                }
            }
        }
        double conLai = slKhaDung - dangGiuTrongGio;
        return conLai < 0 ? 0 : conLai;
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(210, 360));
        setBackground(Color.WHITE);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- IMAGE PANEL ---
        JPanel pnlImage = new JPanel(new BorderLayout());
        pnlImage.setOpaque(false);
        pnlImage.setPreferredSize(new Dimension(180,140));

        JLabel lblImage = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setClip(new java.awt.geom.RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 25, 25));
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        lblImage.setOpaque(false);

        String imagePath = "src/main/resources/images/" + sp.getMaSP() + ".jpg";
        ImageIcon icon = new ImageIcon(imagePath);
        if (icon.getIconWidth() <= 0) {
            icon = new ImageIcon("src/image/default.png");
        }
        Image img = icon.getImage().getScaledInstance(190, 150, Image.SCALE_SMOOTH);
        lblImage.setIcon(new ImageIcon(img));

        pnlImage.add(lblImage, BorderLayout.CENTER);
        add(pnlImage, BorderLayout.NORTH);

        // --- INFO PANEL ---
        JPanel pnlInfo = new JPanel();
        pnlInfo.setLayout(new BoxLayout(pnlInfo, BoxLayout.Y_AXIS));
        pnlInfo.setOpaque(false);
        pnlInfo.setBorder(new EmptyBorder(10, 0, 0, 0));
        pnlInfo.setMaximumSize(new Dimension(200, 120));

        double tonKhoHienThi = getHienThiTonKho();

        JLabel lblTenSP = new JLabel(sp.getTenSP());
        lblTenSP.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblTenSP.setForeground(Color.BLACK);

        lblTonKho = new JLabel("Còn lại: " + String.format("%.0f", tonKhoHienThi) + " " + sp.getDonViTinh());
        lblTonKho.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblTonKho.setForeground(new Color(120,120,120));

        JLabel lblGia = new JLabel(String.format("%,.0f đ", sp.getGiaBan()));
        lblGia.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblGia.setForeground(new Color(0,153,51));

        pnlInfo.add(lblTenSP);
        pnlInfo.add(Box.createVerticalStrut(5));
        pnlInfo.add(lblTonKho);
        pnlInfo.add(Box.createVerticalStrut(12));
        pnlInfo.add(lblGia);
        add(pnlInfo, BorderLayout.CENTER);

        // --- BOTTOM PANEL (Cảnh báo + Qty + Nút Thêm) ---
        JPanel pnlBottom = new JPanel();
        pnlBottom.setLayout(new BoxLayout(pnlBottom, BoxLayout.Y_AXIS));
        pnlBottom.setOpaque(false);
        pnlBottom.setBorder(new EmptyBorder(6, 0, 0, 0));

        // Dòng cảnh báo (hiện khi số lượng không hợp lệ)
        lblWarning = new JLabel("⚠ Sản phẩm hiện không đủ hàng");
        lblWarning.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblWarning.setForeground(new Color(220, 38, 38)); // Đỏ
        lblWarning.setVisible(false);
        lblWarning.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Hàng chứa qty selector + nút thêm
        JPanel pnlQtyRow = new JPanel(new BorderLayout());
        pnlQtyRow.setOpaque(false);
        pnlQtyRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlQtyRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        // BỘ NÚT TĂNG GIẢM SỐ LƯỢNG + Ô NHẬP
        JPanel pnlQty = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 5));
        pnlQty.setOpaque(false);

        btnMinus = new JButton("-");
        styleQtyBtn(btnMinus);

        if (tonKhoHienThi <= 0) {
            quantity = 0;
        } else {
            quantity = 1;
        }

        // Ô nhập số lượng
        txtQty = new JTextField(String.valueOf(quantity), 3);
        txtQty.setPreferredSize(new Dimension(44, 30));
        txtQty.setFont(new Font("Segoe UI", Font.BOLD, 14));
        txtQty.setHorizontalAlignment(JTextField.CENTER);
        txtQty.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 1, 0, new Color(209, 213, 219)),
            BorderFactory.createEmptyBorder(0, 4, 0, 4)
        ));
        txtQty.setBackground(Color.WHITE);
        txtQty.setForeground(new Color(31, 41, 55));

        // Giữ biến alias cho các chỗ dùng lblQty.setText() cũ
        lblQty = new JLabel() {
            @Override public void setText(String t) {
                if (txtQty != null) txtQty.setText(t);
            }
        };

        btnPlus = new JButton("+");
        styleQtyBtn(btnPlus);

        // Helper: kiểm tra và hiển/ẩn cảnh báo dựa trên giá trị hiện tại trong txtQty
        Runnable checkWarning = () -> {
            double tonKho = getHienThiTonKho();
            try {
                int val = Integer.parseInt(txtQty.getText().trim());
                boolean over = (tonKho >= 0 && val > (int) tonKho);
                lblWarning.setVisible(over);
                txtQty.setForeground(over ? new Color(220, 38, 38) : new Color(31, 41, 55));
            } catch (NumberFormatException ex) {
                // chưa phải số hợp lệ, không cần hiển cảnh báo
                lblWarning.setVisible(false);
            }
        };

        // Validation helper: clamp + ẩn cảnh báo khi focus-lost / Enter
        Runnable validateInput = () -> {
            double currentTonKho = getHienThiTonKho();
            int max = (int) currentTonKho;
            try {
                int val = Integer.parseInt(txtQty.getText().trim());
                if (val < 1) val = 1;
                if (val > max && max > 0) val = max;
                if (max <= 0) val = 0;
                quantity = val;
            } catch (NumberFormatException ex) {
                // không hợp lệ -> khôi phục giá trị cũ
            }
            txtQty.setText(String.valueOf(quantity));
            lblWarning.setVisible(false);
            txtQty.setForeground(new Color(31, 41, 55));
        };

        // Xác nhận khi nhấn Enter
        txtQty.addActionListener(e -> validateInput.run());
        // Xác nhận khi mất focus
        txtQty.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusLost(java.awt.event.FocusEvent e) { validateInput.run(); }
        });
        // Live warning khi gõ phím
        txtQty.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { checkWarning.run(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { checkWarning.run(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { checkWarning.run(); }
        });
        // Chỉ cho phép gõ chữ số
        ((javax.swing.text.AbstractDocument) txtQty.getDocument()).setDocumentFilter(
            new javax.swing.text.DocumentFilter() {
                @Override public void insertString(FilterBypass fb, int off, String str, javax.swing.text.AttributeSet a)
                        throws javax.swing.text.BadLocationException {
                    if (str != null && str.matches("\\d*")) super.insertString(fb, off, str, a);
                }
                @Override public void replace(FilterBypass fb, int off, int len, String str, javax.swing.text.AttributeSet a)
                        throws javax.swing.text.BadLocationException {
                    if (str != null && str.matches("\\d*")) super.replace(fb, off, len, str, a);
                }
            }
        );

        btnMinus.addActionListener(e -> {
            if (quantity > 1) {
                quantity--;
                txtQty.setText(String.valueOf(quantity));
                // Hiển lại warning nếu cần (thường một bước giảm sẽ hờ warning)
                checkWarning.run();
            }
        });

        btnPlus.addActionListener(e -> {
            double currentTonKho = getHienThiTonKho();
            quantity++;
            txtQty.setText(String.valueOf(quantity));
            if (quantity > (int) currentTonKho) {
                // Hiển cảnh báo inline, không popup
                lblWarning.setVisible(true);
                txtQty.setForeground(new Color(220, 38, 38));
            } else {
                lblWarning.setVisible(false);
                txtQty.setForeground(new Color(31, 41, 55));
            }
        });

        pnlQty.add(btnMinus);
        pnlQty.add(txtQty);
        pnlQty.add(btnPlus);

        // NÚT THÊM (+) - Dấu "+" nhỏ gọn
        btnAdd = new JButton("+") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (!isEnabled()) {
                    g2.setColor(new Color(200, 200, 200));
                } else if (getModel().isPressed()) {
                    g2.setColor(new Color(0, 140, 70));
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(0, 160, 80));
                } else {
                    g2.setColor(new Color(0, 180, 90));
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnAdd.setPreferredSize(new Dimension(36, 36));
        btnAdd.setFont(new Font("Arial", Font.BOLD, 22));
        btnAdd.setMargin(new Insets(0, 0, 0, 0));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFocusPainted(false);
        btnAdd.setBorderPainted(false);
        btnAdd.setContentAreaFilled(false);
        btnAdd.setOpaque(false);
        btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));

        if (tonKhoHienThi <= 0) {
            btnMinus.setEnabled(false);
            btnPlus.setEnabled(false);
            btnAdd.setEnabled(false);
            txtQty.setEnabled(false);
        }

        JPanel pnlBtnRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 4));
        pnlBtnRight.setOpaque(false);
        pnlBtnRight.add(btnAdd);

        pnlQtyRow.add(pnlQty, BorderLayout.WEST);
        pnlQtyRow.add(pnlBtnRight, BorderLayout.EAST);

        pnlBottom.add(lblWarning);
        pnlBottom.add(pnlQtyRow);

        add(pnlBottom, BorderLayout.SOUTH);

        // SỰ KIỆN NÚT THÊM
        btnAdd.addActionListener(e -> themVaoGioHang());
    }

    private void styleQtyBtn(JButton btn) {
        btn.setPreferredSize(new Dimension(30, 30));
        btn.setBackground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(new Color(209, 213, 219)));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    // =========================================================
    // LOGIC CHỐT CHẶN: THÊM GIỎ HÀNG
    // =========================================================
    private void themVaoGioHang() {
        if (!Session.isLogged() || Session.currentUser == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng đăng nhập để mua hàng!", "Lỗi xác thực", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // CHỐT CHẶN 1: Ngăn Admin/Nhân viên mua hàng gây lỗi DB
        if (Session.currentUser.getLoaiTK() != 2) {
            JOptionPane.showMessageDialog(this, 
                "Giỏ hàng chỉ dành cho Khách Hàng (LoaiTK = 2).\nBạn đang dùng tài khoản Quản lý/Nhân viên nên không thể mua hàng!", 
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // CHỐT CHẶN 2: Query trực tiếp xuống bảng KHACHHANG để lấy mã chuẩn
        String username = Session.currentUser.getUsername();
        String maKH = null;

        try (Connection con = util.DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT MaKH FROM KHACHHANG WHERE Username = ?")) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    maKH = rs.getString("MaKH");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (maKH == null || maKH.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Tài khoản này chưa có hồ sơ trong bảng KHACHHANG!\nHệ thống không thể thêm vào giỏ. Vui lòng kiểm tra lại Data.", 
                "Lỗi Dữ Liệu", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // XỬ LÝ LƯU VÀO DB
        GioHangBUS ghBus = new GioHangBUS();
        
        // Double-check real-time stock limits
        double tonKhoHienThi = getHienThiTonKho();
        if (quantity > tonKhoHienThi) {
            JOptionPane.showMessageDialog(this, 
                "Không thể thêm. Số lượng muốn thêm vượt quá số lượng tồn kho khả dụng còn lại (" + String.format("%.0f", tonKhoHienThi) + ")!", 
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String errorMsg = ghBus.addToCart(maKH, sp.getMaSP(), quantity);

        if (errorMsg == null) {
            // Thành công -> Update UI
            Session.cartCache = ghBus.getCart(maKH);
            if (parentFrame != null) {
                parentFrame.updateCartBadge();
            }
            JOptionPane.showMessageDialog(this, "Đã thêm " + quantity + " sản phẩm:\n" + sp.getTenSP() + " vào giỏ hàng!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            
            // Refresh stock label & counters
            double newStock = getHienThiTonKho();
            lblTonKho.setText("Còn lại: " + String.format("%.0f", newStock) + " " + sp.getDonViTinh());
            
            if (newStock <= 0) {
                quantity = 0;
                lblQty.setText("0");
                btnMinus.setEnabled(false);
                btnPlus.setEnabled(false);
                btnAdd.setEnabled(false);
            } else {
                quantity = 1;
                lblQty.setText("1");
            }
        } else {
            JOptionPane.showMessageDialog(this, errorMsg, "Không thể thêm", JOptionPane.WARNING_MESSAGE);
        }
    }

    private JButton createRoundButton(JButton button) {
        return new JButton(button.getText()) {
            {
                setPreferredSize(button.getPreferredSize());
                setFont(button.getFont());
                setForeground(button.getForeground());
                setFocusPainted(false);
                setBorderPainted(false);
                setContentAreaFilled(false);
                setCursor(button.getCursor());
            }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (!isEnabled()) {
                    g2.setColor(new Color(200, 200, 200));
                } else if (getModel().isPressed()) {
                    g2.setColor(new Color(0,150,70));
                } else {
                    g2.setColor(new Color(0,180,90));
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
    }

    private void openDetail() {
        SanPhamDTO fullSP = bus.getById(sp.getMaSP());
        ChiTietSanPhamForm dialog = new ChiTietSanPhamForm(null, fullSP);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    private void addCardClick(Component component) {
        component.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) { openDetail(); }
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { setBorderColor(AppColor.PRIMARY); }
            @Override public void mouseExited(java.awt.event.MouseEvent e) { setBorderColor(new Color(230,230,230)); }
        });

        if (component instanceof Container) {
            for (Component child : ((Container) component).getComponents()) {
                // Không gắn sự kiện click-card vào các nút bấm (JButton) và ô nhập (JTextField)
                if (!(child instanceof JButton) && !(child instanceof JTextField)) {
                    addCardClick(child);
                }
            }
        }
    }
}