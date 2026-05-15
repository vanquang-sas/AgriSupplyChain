
package gui.component;

import dto.SanPhamDTO;
import gui.dialog.ChiTietSanPhamForm;
import util.AppColor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import bus.CuaHangBUS;

import java.awt.*;

public class ProductCard extends JPanel {

    private SanPhamDTO sp;
    private CuaHangBUS bus = new CuaHangBUS();
//     private String maKH;

    // =========================
    // UI
    // =========================
    private int radius = 25;

    private Color borderColor =
            new Color(230,230,230);

    public ProductCard(SanPhamDTO sp) {

        this.sp = sp;
        // this.maKH = maKH;

        setOpaque(false);

        initComponents();

        // click toàn bộ card
        addCardClick(this);
    }

    // =========================================================
    // PAINT CARD BO GÓC
    // =========================================================
    @Override
    protected void paintComponent(Graphics g) {

        Graphics2D g2 =
                (Graphics2D) g.create();

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        // background
        g2.setColor(getBackground());

        g2.fillRoundRect(
                0,
                0,
                getWidth(),
                getHeight(),
                radius,
                radius
        );

        g2.dispose();

        super.paintComponent(g);
    }

    @Override
    protected void paintBorder(Graphics g) {

        Graphics2D g2 =
                (Graphics2D) g.create();

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        g2.setColor(borderColor);

        g2.setStroke(new BasicStroke(1.5f));

        g2.drawRoundRect(
                0,
                0,
                getWidth() - 1,
                getHeight() - 1,
                radius,
                radius
        );

        g2.dispose();
    }

    // =========================================================
    // BORDER COLOR
    // =========================================================
    private void setBorderColor(Color color) {

        borderColor = color;

        repaint();
    }

    private void initComponents() {

        // =========================================
        // CARD
        // =========================================
        setLayout(new BorderLayout());

        setPreferredSize(
                new Dimension(210, 340)
        );

        setBackground(Color.WHITE);

        setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );

        setBorder(
                new EmptyBorder(
                        10,
                        10,
                        10,
                        10
                )
        );

        // =========================================
        // IMAGE PANEL
        // =========================================
        JPanel pnlImage =
                new JPanel(new BorderLayout());

        pnlImage.setOpaque(false);

        pnlImage.setPreferredSize(
                new Dimension(180,140)
        );

        // =========================================
        // IMAGE LABEL
        // =========================================
        // JLabel lblImage = new JLabel();

        // lblImage.setHorizontalAlignment(
        //         SwingConstants.CENTER
        // );

        // lblImage.setVerticalAlignment(
        //         SwingConstants.CENTER
        // );
        JLabel lblImage = new JLabel() {

        @Override
        protected void paintComponent(Graphics g) {

                Graphics2D g2 =
                        (Graphics2D) g.create();

                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                // bo góc ảnh
                g2.setClip(new java.awt.geom.RoundRectangle2D.Double(
                        0,
                        0,
                        getWidth(),
                        getHeight(),
                        25,
                        25
                ));

                super.paintComponent(g2);

                g2.dispose();
    }
        };
        lblImage.setOpaque(false);

        // =========================================
        // LOAD IMAGE
        // =========================================
        String imagePath =
                "src/main/resources/images/"
                        + sp.getMaSP()
                        + ".jpg";

        ImageIcon icon =
                new ImageIcon(imagePath);

        // nếu không có ảnh
        if (icon.getIconWidth() <= 0) {

            icon = new ImageIcon(
                    "src/image/default.png"
            );
        }

        Image img =
                icon.getImage().getScaledInstance(
                        190,
                        150,
                        Image.SCALE_SMOOTH
                );

        lblImage.setIcon(
                new ImageIcon(img)
        );

        pnlImage.add(
                lblImage,
                BorderLayout.CENTER
        );

        add(
                pnlImage,
                BorderLayout.NORTH
        );

        // =========================================
        // INFO PANEL
        // =========================================
        JPanel pnlInfo = new JPanel();

        pnlInfo.setLayout(
                new BoxLayout(
                        pnlInfo,
                        BoxLayout.Y_AXIS
                )
        );

        pnlInfo.setOpaque(false);

        pnlInfo.setBorder(
                new EmptyBorder(
                        10,
                        0,
                        0,
                        0
                )
        );

        pnlInfo.setMaximumSize(
                new Dimension(200, 120)
        );

        // =========================================
        // MÃ SP
        // =========================================
        JLabel lblMaSP =
                new JLabel(sp.getMaSP());

        lblMaSP.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        12
                )
        );

        lblMaSP.setForeground(Color.GRAY);

        // =========================================
        // TÊN SP
        // =========================================
        JLabel lblTenSP =
                new JLabel(sp.getTenSP());

        lblTenSP.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        17
                )
        );

        lblTenSP.setForeground(Color.BLACK);

        // =========================================
        // LOẠI SP
        // =========================================
        JLabel lblLoai =
                new JLabel(sp.getMaLSP());

        lblLoai.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        13
                )
        );

        lblLoai.setForeground(
                new Color(120,120,120)
        );

        // =========================================
        // GIÁ
        // =========================================
        JLabel lblGia =
                new JLabel(
                        String.format(
                                "%,.0f đ",
                                sp.getGiaBan()
                        )
                );

        lblGia.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        24
                )
        );

        lblGia.setForeground(
                new Color(0,153,51)
        );

        // =========================================
        // ADD INFO
        // =========================================
        pnlInfo.add(lblMaSP);

        pnlInfo.add(
                Box.createVerticalStrut(5)
        );

        pnlInfo.add(lblTenSP);

        pnlInfo.add(
                Box.createVerticalStrut(5)
        );

        pnlInfo.add(lblLoai);

        pnlInfo.add(
                Box.createVerticalStrut(12)
        );

        pnlInfo.add(lblGia);

        add(
                pnlInfo,
                BorderLayout.CENTER
        );

        // =========================================
        // BUTTON PANEL
        // =========================================
        JPanel pnlBottom =
                new JPanel(
                        new BorderLayout()
                );

        pnlBottom.setOpaque(false);

        pnlBottom.setBorder(
                new EmptyBorder(
                        10,
                        0,
                        0,
                        0
                )
        );

        pnlBottom.setPreferredSize(
                new Dimension(0, 55)
        );

        // =========================================
        // BUTTON +
        // =========================================
        JButton btnAdd =
                new JButton("+");

        btnAdd.setPreferredSize(
                new Dimension(42,42)
        );

        btnAdd.setFocusPainted(false);

        btnAdd.setBorderPainted(false);

        btnAdd.setContentAreaFilled(false);

        btnAdd.setOpaque(false);

        btnAdd.setForeground(Color.WHITE);

        btnAdd.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        22
                )
        );

        btnAdd.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );

        // custom button bo góc
        btnAdd = createRoundButton(btnAdd);

        // =========================================
        // RIGHT BUTTON PANEL
        // =========================================
        JPanel pnlBtnRight =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT,
                                0,
                                0
                        )
                );

        pnlBtnRight.setOpaque(false);

        pnlBtnRight.add(btnAdd);

        pnlBottom.add(
                pnlBtnRight,
                BorderLayout.CENTER
        );

        add(
                pnlBottom,
                BorderLayout.SOUTH
        );

        // =========================================
        // BUTTON EVENT
        // =========================================
        btnAdd.addActionListener(e -> {

            JOptionPane.showMessageDialog(
                    null,
                    "Đã thêm vào giỏ hàng:\n"
                            + sp.getTenSP()
            );
        });
    }

    // =========================================================
    // BUTTON BO GÓC
    // =========================================================
    private JButton createRoundButton(JButton button) {

        return new JButton(button.getText()) {

            {
                setPreferredSize(
                        button.getPreferredSize()
                );

                setFont(button.getFont());

                setForeground(button.getForeground());

                setFocusPainted(false);

                setBorderPainted(false);

                setContentAreaFilled(false);

                setCursor(button.getCursor());
            }

            @Override
            protected void paintComponent(Graphics g) {

                Graphics2D g2 =
                        (Graphics2D) g.create();

                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                if (getModel().isPressed()) {

                    g2.setColor(
                            new Color(0,150,70)
                    );

                } else {

                    g2.setColor(
                            new Color(0,180,90)
                    );
                }

                g2.fillRoundRect(
                        0,
                        0,
                        getWidth(),
                        getHeight(),
                        18,
                        18
                );

                super.paintComponent(g2);

                g2.dispose();
            }
        };
    }

    // =========================================================
    // OPEN DETAIL
    // =========================================================
    private void openDetail() {

        // lấy FULL dữ liệu từ DB
        SanPhamDTO fullSP =
                bus.getById(
                        sp.getMaSP()
                );

        ChiTietSanPhamForm dialog =
                new ChiTietSanPhamForm(
                        null,
                        fullSP
                );

        dialog.setLocationRelativeTo(null);

        dialog.setVisible(true);
    }

    // =========================================================
    // CLICK TOÀN BỘ CARD
    // =========================================================
    private void addCardClick(Component component) {

        component.addMouseListener(
                new java.awt.event.MouseAdapter() {

                    @Override
                    public void mouseClicked(
                            java.awt.event.MouseEvent e
                    ) {

                        openDetail();
                    }

                    @Override
                    public void mouseEntered(
                            java.awt.event.MouseEvent e
                    ) {

                        setBorderColor(
                                AppColor.PRIMARY
                        );
                    }

                    @Override
                    public void mouseExited(
                            java.awt.event.MouseEvent e
                    ) {

                        setBorderColor(
                                new Color(230,230,230)
                        );
                    }
                }
        );

        // add cho component con
        if (component instanceof Container) {

            for (Component child :
                    ((Container) component)
                            .getComponents()) {

                addCardClick(child);
            }
        }
    }
}