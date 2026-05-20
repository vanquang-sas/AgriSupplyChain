package gui.panel;

import bus.CuaHangBUS;
import dto.SanPhamDTO;
import gui.MainFrame;
import gui.component.ProductCard;
import gui.component.RoundedButton;
import util.AppColor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.List;
import gui.component.WrapLayout;
public class CuaHangPanel extends JPanel {

    private MainFrame parentFrame;

    // private String maKH;

    // =========================================
    // BUS
    // =========================================
    private CuaHangBUS bus = new CuaHangBUS();

    // =========================================
    // COMPONENT
    // =========================================
    private JTextField txtTimKiem;
    private JComboBox<String> cboLoai;

    private JButton btnReload;

    // =========================================
    // PRODUCT PANEL
    // =========================================
    private JPanel pnlProducts;

    private JScrollPane scrollPane;

    public CuaHangPanel(MainFrame parentFrame) {

        this.parentFrame = parentFrame;

        // this.maKH = maKH;

        initComponents();

        initEvents();

        loadData(bus.getAllSanPham());
    }

    // =========================================
    // INIT UI
    // =========================================
    private void initComponents() {

        setLayout(new BorderLayout());

        setBackground(AppColor.BACKGROUND);

        // =========================================
        // TITLE
        // =========================================
        JLabel lblTitle = new JLabel("CỬA HÀNG SẢN PHẨM");

        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));

        lblTitle.setForeground(AppColor.TEXT_PRIMARY);

        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel pnlTitle = new JPanel(new BorderLayout());

        pnlTitle.setBackground(AppColor.BACKGROUND);

        pnlTitle.setBorder(new EmptyBorder(20, 10, 20, 10));

        pnlTitle.add(lblTitle, BorderLayout.CENTER);

        add(pnlTitle, BorderLayout.NORTH);

        // =========================================
        // CENTER PANEL
        // =========================================
        JPanel pnlCenter = new JPanel(new BorderLayout(10, 10));

        pnlCenter.setBackground(AppColor.BACKGROUND);

        pnlCenter.setBorder(new EmptyBorder(10, 20, 20, 20));

        add(pnlCenter, BorderLayout.CENTER);

        // =========================================
        // SEARCH PANEL
        // =========================================
        JPanel pnlSearch = new JPanel(new FlowLayout(
                FlowLayout.LEFT,
                15,
                10
        ));

        pnlSearch.setBackground(AppColor.BACKGROUND);

        // =========================================
        // SEARCH LABEL
        // =========================================
        JLabel lblTim = new JLabel("Tìm kiếm:");

        lblTim.setFont(new Font("Segoe UI", Font.BOLD, 14));

        lblTim.setForeground(AppColor.TEXT_PRIMARY);

        // =========================================
        // SEARCH TEXTFIELD
        // =========================================
        txtTimKiem = new JTextField();

        txtTimKiem.setPreferredSize(new Dimension(250, 40));

        txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        txtTimKiem.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        new Color(220,220,220),
                        1,
                        true
                ),
                new EmptyBorder(5,10,5,10)
        ));

        txtTimKiem.setBackground(Color.WHITE);

        txtTimKiem.setForeground(Color.BLACK);

        txtTimKiem.setCaretColor(Color.BLACK);

        // =========================================
        // CATEGORY
        // =========================================
        JLabel lblLoai = new JLabel("Loại sản phẩm:");

        lblLoai.setFont(new Font("Segoe UI", Font.BOLD, 14));

        lblLoai.setForeground(AppColor.TEXT_PRIMARY);

        cboLoai = new JComboBox<>();

        cboLoai.setPreferredSize(new Dimension(180, 40));

        cboLoai.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        cboLoai.addItem("Tất cả");
        cboLoai.addItem("LSP00001");
        cboLoai.addItem("LSP00002");
        cboLoai.addItem("LSP00003");
        cboLoai.addItem("LSP00004");
        cboLoai.addItem("LSP00005");

        // =========================================
        // RELOAD BUTTON
        // =========================================
        btnReload = new RoundedButton(
                "Reload",
                AppColor.PRIMARY
        );

        btnReload.setPreferredSize(new Dimension(120, 40));

        // =========================================
        // ADD COMPONENT
        // =========================================
        pnlSearch.add(lblTim);

        pnlSearch.add(txtTimKiem);

        pnlSearch.add(lblLoai);

        pnlSearch.add(cboLoai);

        pnlSearch.add(btnReload);

        pnlCenter.add(pnlSearch, BorderLayout.NORTH);

        // =========================================
        // PRODUCT GRID PANEL
        // =========================================
        pnlProducts = new JPanel();

        // pnlProducts.setLayout(new FlowLayout(
        //         FlowLayout.LEFT,
        //         20,
        //         20
        // ));
        pnlProducts.setLayout(new WrapLayout(
            FlowLayout.LEFT,
            20,
            20
        ));

        pnlProducts.setBackground(AppColor.BACKGROUND);

        // =========================================
        // SCROLL
        // =========================================
        scrollPane = new JScrollPane(pnlProducts);
        // Ẩn thanh cuộn dọc
        scrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_NEVER
        );

        // Ẩn thanh cuộn ngang
        scrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        scrollPane.setBorder(null);

        scrollPane.getViewport().setBackground(
                AppColor.BACKGROUND
        );

        scrollPane.getVerticalScrollBar()
                .setUnitIncrement(16);

        pnlCenter.add(scrollPane, BorderLayout.CENTER);
    }

    // =========================================
    // EVENTS
    // =========================================
    private void initEvents() {

        // =========================================
        // REALTIME SEARCH
        // =========================================
        txtTimKiem.getDocument().addDocumentListener(
                new DocumentListener() {

                    @Override
                    public void insertUpdate(DocumentEvent e) {
                        filterSanPham();
                    }

                    @Override
                    public void removeUpdate(DocumentEvent e) {
                        filterSanPham();
                    }

                    @Override
                    public void changedUpdate(DocumentEvent e) {
                        filterSanPham();
                    }
                }
        );

        // =========================================
        // FILTER CATEGORY
        // =========================================
        cboLoai.addActionListener(e -> {
            filterSanPham();
        });

        // =========================================
        // RELOAD
        // =========================================
        btnReload.addActionListener(e -> {

            txtTimKiem.setText("");

            cboLoai.setSelectedIndex(0);

            loadData(bus.getAllSanPham());
        });
    }

    // =========================================
    // LOAD DATA
    // =========================================
    private void loadData(List<SanPhamDTO> list) {

        pnlProducts.removeAll();

        for (SanPhamDTO sp : list) {

            // ProductCard card = new ProductCard(sp,maKH);
            ProductCard card = new ProductCard(sp, parentFrame);

            pnlProducts.add(card);
        }

        pnlProducts.revalidate();

        pnlProducts.repaint();
    }

    // =========================================
    // FILTER
    // =========================================
    private void filterSanPham() {

        String keyword = txtTimKiem.getText().trim();

        String loai = cboLoai.getSelectedItem().toString();

        // =====================================
        // SEARCH + FILTER
        // =====================================
        if (!keyword.isEmpty()) {

            List<SanPhamDTO> list =
                    bus.timKiem(keyword);

            if (!loai.equals("Tất cả")) {

                list.removeIf(sp ->
                        !sp.getMaLSP().equals(loai)
                );
            }

            loadData(list);
        }

        // =====================================
        // FILTER CATEGORY
        // =====================================
        else if (!loai.equals("Tất cả")) {

            loadData(
                    bus.locTheoLoai(loai)
            );
        }

        // =====================================
        // LOAD ALL
        // =====================================
        else {

            loadData(
                    bus.getAllSanPham()
            );
        }
    }
}