
package gui.panel;

import bus.CuaHangBUS;
import dto.SanPhamDTO;
import gui.component.ProductCard;
import gui.component.RoundedButton;
import gui.component.WrapLayout;
import util.AppColor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.util.List;
import javax.swing.plaf.basic.BasicComboPopup;



public class CuaHangPanel extends JPanel {

    // =========================================
    // BUS
    // =========================================
    private final CuaHangBUS bus = new CuaHangBUS();

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

    public CuaHangPanel() {

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

        txtTimKiem.setPreferredSize(new Dimension(260, 44));

        txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        txtTimKiem.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        new Color(225, 225, 225),
                        1,
                        true
                ),
                new EmptyBorder(5, 14, 5, 14)
        ));

        txtTimKiem.setBackground(Color.WHITE);

        txtTimKiem.setForeground(Color.BLACK);

        txtTimKiem.setCaretColor(Color.BLACK);

        // =========================================
        // CATEGORY LABEL
        // =========================================
        JLabel lblLoai = new JLabel("Loại sản phẩm:");

        lblLoai.setFont(new Font("Segoe UI", Font.BOLD, 14));

        lblLoai.setForeground(AppColor.TEXT_PRIMARY);

        // =========================================
        // COMBOBOX
        // =========================================
        // cboLoai = createModernComboBox2(); ok ok
        //  cboLoai = createModernComboBox5(); ok 
        cboLoai = createModernComboBox();
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

        btnReload.setPreferredSize(new Dimension(120, 42));

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

        scrollPane.setBorder(null);

        scrollPane.getViewport().setBackground(
                AppColor.BACKGROUND
        );

        scrollPane.getVerticalScrollBar()
                .setUnitIncrement(16);

        scrollPane.getVerticalScrollBar()
                .setUI(new ModernScrollBarUI());

        scrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        scrollPane.setVerticalScrollBarPolicy(
        JScrollPane.VERTICAL_SCROLLBAR_NEVER
);

        scrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        pnlCenter.add(scrollPane, BorderLayout.CENTER);
    }

    // =========================================
    // MODERN SCROLLBAR
    // =========================================
    private static class ModernScrollBarUI
            extends BasicScrollBarUI {

        @Override
        protected void configureScrollBarColors() {

            thumbColor = new Color(200, 200, 200);

            trackColor = new Color(245, 245, 245);
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {

            return createZeroButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {

            return createZeroButton();
        }

        private JButton createZeroButton() {

            JButton button = new JButton();

            button.setPreferredSize(new Dimension(0, 0));

            button.setMinimumSize(new Dimension(0, 0));

            button.setMaximumSize(new Dimension(0, 0));

            return button;
        }

        @Override
        protected void paintThumb(
                Graphics g,
                JComponent c,
                Rectangle thumbBounds
        ) {

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(thumbColor);

            g2.fillRoundRect(
                    thumbBounds.x + 4,
                    thumbBounds.y,
                    thumbBounds.width - 8,
                    thumbBounds.height,
                    10,
                    10
            );

            g2.dispose();
        }

        @Override
        protected void paintTrack(
                Graphics g,
                JComponent c,
                Rectangle trackBounds
        ) {

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setColor(trackColor);

            g2.fillRect(
                    trackBounds.x,
                    trackBounds.y,
                    trackBounds.width,
                    trackBounds.height
            );

            g2.dispose();
        }
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

            ProductCard card = new ProductCard(sp);

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
    // =========================================
// MODERN COMBOBOX
// =========================================
private JComboBox<String> createModernComboBox() {

    JComboBox<String> combo = new JComboBox<>();

    combo.setPreferredSize(new Dimension(220, 45));

    combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));

    combo.setFocusable(false);

    combo.setBackground(Color.WHITE);

    combo.setForeground(new Color(35, 35, 35));

    combo.setBorder(BorderFactory.createEmptyBorder());

    combo.setOpaque(false);

    // =====================================
    // CUSTOM UI
    // =====================================
    combo.setUI(new BasicComboBoxUI() {

        @Override
        protected JButton createArrowButton() {

            JButton button = new JButton() {

                @Override
                protected void paintComponent(Graphics g) {

                    Graphics2D g2 =
                            (Graphics2D) g.create();

                    g2.setRenderingHint(
                            RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON
                    );

                    g2.setColor(new Color(120, 120, 120));

                    g2.setStroke(new BasicStroke(
                            2f,
                            BasicStroke.CAP_ROUND,
                            BasicStroke.JOIN_ROUND
                    ));

                    int x = getWidth() / 2;
                    int y = getHeight() / 2;

                    // icon dropdown modern
                    g2.drawLine(x - 5, y - 2, x, y + 3);

                    g2.drawLine(x, y + 3, x + 5, y - 2);

                    g2.dispose();
                }
            };

            button.setBorder(null);

            button.setContentAreaFilled(false);

            button.setFocusPainted(false);

            button.setCursor(
                    new Cursor(Cursor.HAND_CURSOR)
            );

            return button;
        }

        @Override
        public void paintCurrentValueBackground(
                Graphics g,
                Rectangle bounds,
                boolean hasFocus
        ) {

            Graphics2D g2 =
                    (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            // background
            g2.setColor(Color.WHITE);

            g2.fillRoundRect(
                    0,
                    0,
                    combo.getWidth(),
                    combo.getHeight(),
                    18,
                    18
            );

            // border
            g2.setColor(new Color(225, 225, 225));

            g2.drawRoundRect(
                    0,
                    0,
                    combo.getWidth() - 1,
                    combo.getHeight() - 1,
                    18,
                    18
            );

            g2.dispose();
        }
    });

    // =====================================
    // RENDER ITEM
    // =====================================
    combo.setRenderer(new DefaultListCellRenderer() {

        @Override
        public Component getListCellRendererComponent(
                JList<?> list,
                Object value,
                int index,
                boolean isSelected,
                boolean cellHasFocus
        ) {

            JLabel lbl =
                    (JLabel) super.getListCellRendererComponent(
                            list,
                            value,
                            index,
                            isSelected,
                            cellHasFocus
                    );

            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            lbl.setBorder(new EmptyBorder(
                    12,
                    16,
                    12,
                    16
            ));

            if (isSelected) {

                lbl.setBackground(
                        new Color(245, 245, 245)
                );

                lbl.setForeground(
                        AppColor.PRIMARY
                );

            } else {

                lbl.setBackground(Color.WHITE);

                lbl.setForeground(
                        new Color(40, 40, 40)
                );
            }

            return lbl;
        }
    });

    // =====================================
    // REMOVE BLACK BORDER POPUP
    // =====================================
    Object child = combo.getAccessibleContext()
            .getAccessibleChild(0);

    if (child instanceof BasicComboPopup popup) {

        popup.setBorder(
                BorderFactory.createEmptyBorder()
        );

        popup.setOpaque(false);

        JList<?> list = popup.getList();

        list.setBorder(
                BorderFactory.createEmptyBorder()
        );

        list.setBackground(Color.WHITE);

        JScrollPane scroll =
                (JScrollPane) popup.getComponent(0);

        scroll.setBorder(
                BorderFactory.createEmptyBorder()
        );

        scroll.getViewport()
                .setBackground(Color.WHITE);
    }

    return combo;
}
}