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
    private JLabel lblQty;
    private JLabel lblTonKho;
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
        setPreferredSize(new Dimension(210, 340));
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

        // --- BOTTOM PANEL (Nút Thêm Giỏ Hàng) ---
        JPanel pnlBottom = new JPanel(new BorderLayout());
        pnlBottom.setOpaque(false);
        pnlBottom.setBorder(new EmptyBorder(10, 0, 0, 0));
        pnlBottom.setPreferredSize(new Dimension(0, 55));

        // BỘ NÚT TĂNG GIẢM SỐ LƯỢNG
        JPanel pnlQty = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 6));
        pnlQty.setOpaque(false);

        btnMinus = new JButton("-");
        styleQtyBtn(btnMinus);

        if (tonKhoHienThi <= 0) {
            quantity = 0;
        } else {
            quantity = 1;
        }

        lblQty = new JLabel(String.valueOf(quantity), SwingConstants.CENTER);
        lblQty.setPreferredSize(new Dimension(26, 30));
        lblQty.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnPlus = new JButton("+");
        styleQtyBtn(btnPlus);

        btnMinus.addActionListener(e -> {
            if (quantity > 1) {
                quantity--;
                lblQty.setText(String.valueOf(quantity));
            }
        });

        btnPlus.addActionListener(e -> {
            double currentTonKho = getHienThiTonKho();
            if (quantity + 1 > currentTonKho) {
                JOptionPane.showMessageDialog(this,
                    "Không thể thêm vượt quá số lượng tồn kho khả dụng (" + String.format("%.0f", currentTonKho) + ")!",
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            quantity++;
            lblQty.setText(String.valueOf(quantity));
        });

        pnlQty.add(btnMinus);
        pnlQty.add(lblQty);
        pnlQty.add(btnPlus);
        pnlBottom.add(pnlQty, BorderLayout.WEST);

        // NÚT THÊM (+)
        btnAdd = new JButton("+");
        btnAdd.setPreferredSize(new Dimension(42,42));
        btnAdd.setFocusPainted(false);
        btnAdd.setBorderPainted(false);
        btnAdd.setContentAreaFilled(false);
        btnAdd.setOpaque(false);
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 22));
        btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAdd = createRoundButton(btnAdd);

        if (tonKhoHienThi <= 0) {
            btnMinus.setEnabled(false);
            btnPlus.setEnabled(false);
            btnAdd.setEnabled(false);
        }

        JPanel pnlBtnRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        pnlBtnRight.setOpaque(false);
        pnlBtnRight.add(btnAdd);

        pnlBottom.add(pnlBtnRight, BorderLayout.EAST);
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
                // Đảm bảo không gắn sự kiện click-card vào các nút bấm (JButton)
                if (!(child instanceof JButton)) {
                    addCardClick(child);
                }
            }
        }
    }
}