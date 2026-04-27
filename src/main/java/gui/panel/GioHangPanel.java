package gui.panel;

import bus.DonHangBUS;
import dto.CartItemDTO;
import gui.ThemeColor;
import gui.component.IntegratedSearch; 
import gui.dialog.DatHangDialog;

import com.formdev.flatlaf.FlatClientProperties;
import raven.modal.Toast;
import raven.modal.component.SimpleModalBorder;
import raven.modal.ModalDialog;
import raven.modal.option.Option;

import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class GioHangPanel extends JPanel {

    private final DonHangBUS bus;
    private final String maKH;

    private JPanel pnlCartItems;
    private JLabel lblTongTienHang;
    private JLabel lblPhiShip; 
    private JLabel lblTongCongThanhToan;
    private JButton btnCheckout;
    private IntegratedSearch txtSearch; 
    private JComboBox<String> cboSort;
    
    // --- KHAI BÁO LABEL HIỂN THỊ SỐ SẢN PHẨM ---
    private JLabel lblCountBadge;

    private static final NumberFormat FMT = NumberFormat.getCurrencyInstance(Locale.of("vi", "VN"));
    
    private String toHex(Color c) {
        return String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
    }

    public GioHangPanel(DonHangBUS bus, String maKH) {
        this.bus  = bus;
        this.maKH = maKH;
        initUI();
        loadGioHang();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 0));
        setBackground(ThemeColor.BACKGROUND);

        // ================================================================== //
        // 1. TIÊU ĐỀ & BADGE SỐ LƯỢNG
        // ================================================================== //
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(ThemeColor.SURFACE);
        pnlHeader.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, ThemeColor.BORDER),
                BorderFactory.createEmptyBorder(20, 30, 20, 30)
        ));
        
        // Tạo Panel con để gom Tiêu đề và Badge lại nằm cạnh nhau
        JPanel pnlTitleWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        pnlTitleWrapper.setOpaque(false);
        
        JLabel lblTitle = new JLabel("CHI TIẾT GIỎ HÀNG");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24)); 
        lblTitle.setForeground(ThemeColor.TEXT_PRIMARY);

        // Khởi tạo Badge bo góc xịn xò
        lblCountBadge = new JLabel("0 sản phẩm");
        lblCountBadge.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblCountBadge.putClientProperty(FlatClientProperties.STYLE, "" +
                "arc: 999;" + // Bo tròn hai đầu
                "background: #187C54;" + // Màu xanh lá giống ảnh của bạn
                "foreground: #FFFFFF;" + // Chữ màu trắng
                "border: 4,14,4,14;" // Padding cho chữ không sát lề
        );

        pnlTitleWrapper.add(lblTitle);
        pnlTitleWrapper.add(lblCountBadge); // Thêm Badge vào ngay cạnh tiêu đề
        
        pnlHeader.add(pnlTitleWrapper, BorderLayout.WEST);

        // ================================================================== //
        // 2. VÙNG NỘI DUNG CHÍNH
        // ================================================================== //
        JPanel pnlContainer = new JPanel(new BorderLayout(0, 20));
        pnlContainer.setBackground(ThemeColor.BACKGROUND);
        pnlContainer.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // --- THANH CHỨC NĂNG ---
        JPanel pnlToolbar = new JPanel(new BorderLayout(15, 0));
        pnlToolbar.setBackground(ThemeColor.SURFACE);
        pnlToolbar.putClientProperty(FlatClientProperties.STYLE, "arc: 12; border: 1,1,1,1," + toHex(ThemeColor.BORDER));
        pnlToolbar.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JPanel pnlLeftTools = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        pnlLeftTools.setOpaque(false);

        cboSort = new JComboBox<>(new String[]{"Mới nhất", "Tổng tiền: Thấp đến Cao", "Tổng tiền: Cao đến Thấp", "Tên: A-Z"});
        cboSort.setPreferredSize(new Dimension(200, 38));
        cboSort.addActionListener(e -> loadGioHang()); 

        JButton btnDeleteAll = new JButton("Xóa tất cả");
        btnDeleteAll.setBackground(ThemeColor.ERROR);
        btnDeleteAll.setForeground(ThemeColor.SURFACE);
        btnDeleteAll.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnDeleteAll.setPreferredSize(new Dimension(110, 38));
        btnDeleteAll.putClientProperty(FlatClientProperties.STYLE, "arc: 8");
        btnDeleteAll.addActionListener(e -> {
            int cf = JOptionPane.showConfirmDialog(this, "Xóa toàn bộ giỏ hàng?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if(cf == JOptionPane.YES_OPTION) {
                bus.xoaToanBoGio();
                loadGioHang();
            }
        });

        pnlLeftTools.add(new JLabel("Sắp xếp:"));
        pnlLeftTools.add(cboSort);
        pnlLeftTools.add(btnDeleteAll);

        // --- TÍCH HỢP INTEGRATED SEARCH ---
        txtSearch = new IntegratedSearch();
        txtSearch.setPreferredSize(new Dimension(280, 38));
        txtSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Tìm tên sản phẩm..."); 
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) { loadGioHang(); }
        });

        pnlToolbar.add(pnlLeftTools, BorderLayout.WEST);
        pnlToolbar.add(txtSearch, BorderLayout.EAST);

        // --- VÙNG HIỂN THỊ SẢN PHẨM & THANH TOÁN ---
        JPanel pnlMainContent = new JPanel(new BorderLayout(25, 0)); 
        pnlMainContent.setOpaque(false);

        pnlCartItems = new JPanel();
        pnlCartItems.setLayout(new BoxLayout(pnlCartItems, BoxLayout.Y_AXIS));
        pnlCartItems.setBackground(ThemeColor.BACKGROUND); 

        JScrollPane scrollPane = new JScrollPane(pnlCartItems);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        pnlMainContent.add(scrollPane, BorderLayout.CENTER);
        pnlMainContent.add(buildRightBillPanel(), BorderLayout.EAST);

        pnlContainer.add(pnlToolbar, BorderLayout.NORTH);
        pnlContainer.add(pnlMainContent, BorderLayout.CENTER);

        add(pnlHeader, BorderLayout.NORTH);
        add(pnlContainer, BorderLayout.CENTER);
    }

    private JPanel buildRightBillPanel() {
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(ThemeColor.SURFACE); 
        rightPanel.setPreferredSize(new Dimension(350, 0));
        rightPanel.putClientProperty(FlatClientProperties.STYLE, "arc: 15; border: 1,1,1,1, " + toHex(ThemeColor.BORDER));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(25, 20, 25, 20));

        JPanel billInfo = new JPanel();
        billInfo.setLayout(new BoxLayout(billInfo, BoxLayout.Y_AXIS));
        billInfo.setOpaque(false);

        JLabel lblBillTitle = new JLabel("TỔNG THANH TOÁN");
        lblBillTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblBillTitle.setForeground(ThemeColor.TEXT_PRIMARY);
        
        billInfo.add(lblBillTitle);
        billInfo.add(Box.createVerticalStrut(25));
        
        lblTongTienHang = new JLabel("0 ₫");
        lblPhiShip = new JLabel("Chưa tính"); 
        
        billInfo.add(createBillRow("Tổng tiền hàng", lblTongTienHang));
        billInfo.add(Box.createVerticalStrut(15));
        billInfo.add(createBillRow("Phí vận chuyển", lblPhiShip));
        billInfo.add(Box.createVerticalStrut(25));
        billInfo.add(new JSeparator());
        billInfo.add(Box.createVerticalStrut(20));

        lblTongCongThanhToan = new JLabel("0 ₫");
        lblTongCongThanhToan.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTongCongThanhToan.setForeground(ThemeColor.PRIMARY);

        JPanel pnlTotal = new JPanel(new BorderLayout());
        pnlTotal.setOpaque(false);
        pnlTotal.add(new JLabel("Thành tiền:"), BorderLayout.NORTH);
        pnlTotal.add(lblTongCongThanhToan, BorderLayout.SOUTH);
        billInfo.add(pnlTotal);

        btnCheckout = new JButton("TIẾN HÀNH ĐẶT HÀNG");
        btnCheckout.setBackground(ThemeColor.PRIMARY);
        btnCheckout.setForeground(ThemeColor.SURFACE);
        btnCheckout.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnCheckout.setPreferredSize(new Dimension(0, 55));
        btnCheckout.putClientProperty(FlatClientProperties.STYLE, "arc: 12");
        btnCheckout.addActionListener(e -> moDialogDatHang());

        rightPanel.add(billInfo, BorderLayout.NORTH);
        rightPanel.add(btnCheckout, BorderLayout.SOUTH);

        return rightPanel;
    }

    public void loadGioHang() {
        pnlCartItems.removeAll();
        String query = txtSearch.getText().trim().toLowerCase();
        int sortType = cboSort.getSelectedIndex();

        List<CartItemDTO> items = new ArrayList<>(bus.getGioHang());

        if (!query.isEmpty()) {
            items = items.stream()
                .filter(i -> i.getTenSP().toLowerCase().contains(query))
                .collect(Collectors.toList());
        }

        switch (sortType) {
            case 1 -> items.sort((a, b) -> Double.compare(a.getGiaBan() * a.getSoLuong(), b.getGiaBan() * b.getSoLuong())); 
            case 2 -> items.sort((a, b) -> Double.compare(b.getGiaBan() * b.getSoLuong(), a.getGiaBan() * a.getSoLuong())); 
            case 3 -> items.sort((a, b) -> a.getTenSP().compareToIgnoreCase(b.getTenSP())); 
        }

        double tongTienHang = 0;
        int tongSoLuongSanPham = 0; // Biến dùng để đếm tổng số lượng hàng

        for (CartItemDTO item : items) {
            double thanhTienTungMon = item.getGiaBan() * item.getSoLuong();
            tongTienHang += thanhTienTungMon;
            
            // Cộng dồn số lượng từng món để hiện lên Badge
            tongSoLuongSanPham += item.getSoLuong();

            pnlCartItems.add(createCartItemCard(item, thanhTienTungMon));
            pnlCartItems.add(Box.createVerticalStrut(15));
        }

        pnlCartItems.revalidate();
        pnlCartItems.repaint();
        
        // --- CẬP NHẬT GIAO DIỆN SAU KHI TÍNH TOÁN ---
        lblTongTienHang.setText(FMT.format(tongTienHang));
        lblTongCongThanhToan.setText(FMT.format(tongTienHang));
        lblCountBadge.setText(tongSoLuongSanPham + " sản phẩm"); // Cập nhật text cho Badge
        btnCheckout.setEnabled(!bus.isGioHangRong());
    }

    private JPanel createCartItemCard(CartItemDTO item, double thanhTienReal) {
        JPanel card = new JPanel(new BorderLayout(20, 0));
        card.setBackground(ThemeColor.SURFACE); 
        card.putClientProperty(FlatClientProperties.STYLE, "arc: 15; border: 1,1,1,1, " + toHex(ThemeColor.BORDER));
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 125));

        JLabel lblImg = new JLabel(" PRODUCT ", SwingConstants.CENTER);
        lblImg.setOpaque(true);
        lblImg.setBackground(ThemeColor.BACKGROUND);
        lblImg.setPreferredSize(new Dimension(90, 90));
        lblImg.putClientProperty(FlatClientProperties.STYLE, "arc: 10");

        JPanel pnlInfo = new JPanel(new GridLayout(2, 1, 0, 5));
        pnlInfo.setOpaque(false);
        JLabel lblName = new JLabel(item.getTenSP());
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 15));
        JLabel lblPrice = new JLabel(FMT.format(item.getGiaBan()) + " / " + item.getDonViTinh());
        lblPrice.setForeground(ThemeColor.TEXT_SECONDARY);
        lblPrice.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pnlInfo.add(lblName);
        pnlInfo.add(lblPrice);

        JPanel pnlQtyWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlQtyWrapper.setOpaque(false);
        
        JPanel pnlQty = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        pnlQty.putClientProperty(FlatClientProperties.STYLE, "arc: 8; border: 1,1,1,1," + toHex(ThemeColor.BORDER));
        
        JButton btnM = new JButton("-"); btnM.setPreferredSize(new Dimension(32, 32));
        JTextField txt = new JTextField(String.valueOf(item.getSoLuong()));
        txt.setPreferredSize(new Dimension(45, 32)); txt.setHorizontalAlignment(0); txt.setEditable(false);
        JButton btnP = new JButton("+"); btnP.setPreferredSize(new Dimension(32, 32));

        btnM.addActionListener(e -> { 
            if(item.getSoLuong() > 1) { 
                bus.capNhatSoLuong(item.getMaSP(), item.getSoLuong()-1); 
                loadGioHang(); 
            } 
        });
        btnP.addActionListener(e -> { 
            bus.capNhatSoLuong(item.getMaSP(), item.getSoLuong()+1); 
            loadGioHang(); 
        });

        pnlQty.add(btnM); pnlQty.add(txt); pnlQty.add(btnP);
        pnlQtyWrapper.add(pnlQty);

        JPanel pnlCenter = new JPanel(new BorderLayout());
        pnlCenter.setOpaque(false);
        pnlCenter.add(pnlInfo, BorderLayout.NORTH);
        pnlCenter.add(pnlQtyWrapper, BorderLayout.SOUTH);

        JPanel pnlAction = new JPanel(new BorderLayout());
        pnlAction.setOpaque(false);
        
        JLabel lblSubTotal = new JLabel(FMT.format(thanhTienReal));
        lblSubTotal.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblSubTotal.setForeground(ThemeColor.PRIMARY);
        lblSubTotal.setHorizontalAlignment(SwingConstants.RIGHT);

        JButton btnDel = new JButton("Xóa");
        btnDel.setForeground(ThemeColor.ERROR);
        btnDel.putClientProperty(FlatClientProperties.STYLE, "arc: 8; borderWidth: 1; borderColor: " + toHex(ThemeColor.ERROR));
        btnDel.addActionListener(e -> { bus.xoaKhoiGio(item.getMaSP()); loadGioHang(); });

        pnlAction.add(lblSubTotal, BorderLayout.NORTH);
        pnlAction.add(btnDel, BorderLayout.SOUTH);

        card.add(lblImg, BorderLayout.WEST);
        card.add(pnlCenter, BorderLayout.CENTER);
        card.add(pnlAction, BorderLayout.EAST);

        return card;
    }

    private JPanel createBillRow(String text, JLabel lblValue) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        JLabel l = new JLabel(text); l.setForeground(ThemeColor.TEXT_SECONDARY);
        p.add(l, BorderLayout.WEST);
        p.add(lblValue, BorderLayout.EAST);
        return p;
    }

    private void moDialogDatHang() {
        if (bus.isGioHangRong()) return;
        
        DatHangDialog dialog = new DatHangDialog(bus, maKH, () -> { 
            loadGioHang(); 
        });
        
        // 1. Căn giữa màn hình
        Option op = ModalDialog.createOption();
        op.getLayoutOption()
            .setSize(-1f, -1f) 
            .setLocation(0.5f, 0.5f) 
            .setAnimateDistance(0f, -0.1f); 
            
        // 2. Dùng SimpleModalBorder.Option để tạo nút Tiếng Việt
        raven.modal.component.SimpleModalBorder.Option[] actions = new raven.modal.component.SimpleModalBorder.Option[]{
            new raven.modal.component.SimpleModalBorder.Option("Không", SimpleModalBorder.NO_OPTION),
            new raven.modal.component.SimpleModalBorder.Option("Có, tiếp tục", SimpleModalBorder.YES_OPTION)
        };

        // 3. Gọi Modal với mảng actions tùy chỉnh
        ModalDialog.showModal(this, new SimpleModalBorder(
                dialog, 
                "Xác nhận thông tin giao hàng", 
                actions, 
                (controller, action) -> { 
                    if (action == SimpleModalBorder.YES_OPTION) {
                        dialog.xacNhanDatHang(controller); 
                    } 
                }
        ), op);
    }
}