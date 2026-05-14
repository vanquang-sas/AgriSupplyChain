
package gui.panel;

import bus.GiaoHangBUS;
import dto.DonHangDTO;
import util.AppColor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class GiaoHangPanel extends JPanel {

    // =====================================================
    // NHÂN VIÊN
    // =====================================================
    private String maNV;

    // =====================================================
    // TABLE
    // =====================================================
    private JTable tblChoGiao;
    private JTable tblLichSu;

    // =====================================================
    // MODEL
    // =====================================================
    private DefaultTableModel modelChoGiao;
    private DefaultTableModel modelLichSu;

    // =====================================================
    // BUTTON
    // =====================================================
    private JButton btnNhanDon;
    private JButton btnThanhCong;
    private JButton btnThatBai;
    private JButton btnReload;

    // =====================================================
    // BUS
    // =====================================================
    private GiaoHangBUS bus = new GiaoHangBUS();

    // =====================================================
    // COLOR
    // =====================================================
    private final Color PRIMARY = new Color(46, 125, 50);
    private final Color SUCCESS = new Color(56, 142, 60);
    private final Color DANGER = new Color(211, 47, 47);
    private final Color INFO = new Color(2, 136, 209);
    private final Color BG = new Color(245, 247, 250);

    // =====================================================
    // CONSTRUCTOR
    // =====================================================
    public GiaoHangPanel(String maNV) {

        this.maNV = maNV;

        setLayout(new BorderLayout());
        setBackground(BG);

        // =====================================================
        // TITLE
        // =====================================================
        JLabel lblTitle = new JLabel(
                "PHÂN HỆ GIAO HÀNG - NHÂN VIÊN: " + maNV
        );

        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(Color.BLACK);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel pnlTitle = new JPanel(new BorderLayout());
        pnlTitle.setBackground(BG);
        pnlTitle.setBorder(new EmptyBorder(20, 10, 15, 10));

        pnlTitle.add(lblTitle, BorderLayout.CENTER);

        add(pnlTitle, BorderLayout.NORTH);

        // =====================================================
        // TABBED PANE
        // =====================================================
        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 17));
        tabbedPane.setBackground(Color.WHITE);
        tabbedPane.setFocusable(false);

        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 17));

        // NỀN TAB
        tabbedPane.setBackground(Color.WHITE);

        // CHỮ TAB MÀU ĐEN
        tabbedPane.setForeground(Color.BLACK);

        tabbedPane.setOpaque(true);
        tabbedPane.setBackground(Color.WHITE);

        tabbedPane.setForeground(Color.BLACK);

        tabbedPane.setOpaque(true);

        // FONT
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // MÀU NỀN CHUNG
        tabbedPane.setBackground(Color.WHITE);
        tabbedPane.setForeground(new Color(40, 40, 40));

        // TAB ACTIVE
        UIManager.put("TabbedPane.selected", new Color(22, 163, 74));

        // CUSTOM UI
        tabbedPane.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {

                @Override
                protected void installDefaults() {
                        super.installDefaults();

                        highlight = new Color(22, 163, 74);
                        lightHighlight = new Color(22, 163, 74);
                        shadow = Color.WHITE;
                        darkShadow = Color.WHITE;

                        tabAreaInsets = new Insets(8, 10, 0, 10);
                        selectedTabPadInsets = new Insets(0, 0, 0, 0);
                }

                @Override
                protected void paintTabBackground(
                        Graphics g,
                        int tabPlacement,
                        int tabIndex,
                        int x,
                        int y,
                        int w,
                        int h,
                        boolean isSelected) {

                        Graphics2D g2 = (Graphics2D) g;
                        g2.setRenderingHint(
                                RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);

                        if (isSelected) {
                        g2.setColor(new Color(22, 163, 74)); // xanh lá
                        } else {
                        g2.setColor(new Color(230, 230, 230));
                        }

                        g2.fillRoundRect(x, y + 2, w, h - 2, 12, 12);
                }

                @Override
                protected void paintText(
                        Graphics g,
                        int tabPlacement,
                        Font font,
                        FontMetrics metrics,
                        int tabIndex,
                        String title,
                        Rectangle textRect,
                        boolean isSelected) {

                        Graphics2D g2 = (Graphics2D) g;

                        g2.setFont(new Font("Segoe UI", Font.BOLD, 14));

                        if (isSelected) {
                        g2.setColor(Color.WHITE);
                        } else {
                        g2.setColor(new Color(60, 60, 60));
                        }

                        g2.drawString(
                                title,
                                textRect.x,
                                textRect.y + metrics.getAscent());
                }

                @Override
                protected void paintFocusIndicator(
                        Graphics g,
                        int tabPlacement,
                        Rectangle[] rects,
                        int tabIndex,
                        Rectangle iconRect,
                        Rectangle textRect,
                        boolean isSelected) {
                        // XÓA VIỀN FOCUS
                }

                @Override
                protected void paintContentBorder(
                        Graphics g,
                        int tabPlacement,
                        int selectedIndex) {

                        g.setColor(new Color(220, 220, 220));
                        g.drawLine(0, 0, tabPane.getWidth(), 0);
                }
                });

        // ============================================================================================================
        // =====================================================
        // PANEL ĐƠN GIAO
        // =====================================================
        JPanel pnlChoGiao = new JPanel(new BorderLayout(10, 10));

        pnlChoGiao.setBackground(BG);
        pnlChoGiao.setBorder(new EmptyBorder(15, 15, 15, 15));

        // =====================================================
        // TOP ACTION
        // =====================================================
        JPanel pnlTopAction = new JPanel(new BorderLayout());

        pnlTopAction.setBackground(BG);
        pnlTopAction.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel lblChoGiao = new JLabel("Đơn giao hàng");

        lblChoGiao.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        lblChoGiao.setForeground(new Color(120, 120, 120));

        pnlTopAction.add(lblChoGiao, BorderLayout.WEST);

        // =====================================================
        // BUTTON PANEL
        // =====================================================
        JPanel pnlButton = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 12, 0)
        );

        pnlButton.setOpaque(false);

        btnNhanDon = createButton("Nhận đơn", INFO);
        btnThanhCong = createButton("Giao thành công", SUCCESS);
        btnThatBai = createButton("Giao thất bại", DANGER);
        btnReload = createButton("Reload", PRIMARY);

        pnlButton.add(btnNhanDon);
        pnlButton.add(btnThanhCong);
        pnlButton.add(btnThatBai);
        pnlButton.add(btnReload);

        pnlTopAction.add(pnlButton, BorderLayout.EAST);

        pnlChoGiao.add(pnlTopAction, BorderLayout.NORTH);

        // =====================================================
        // TABLE ĐƠN GIAO
        // =====================================================
        modelChoGiao = new DefaultTableModel(
                new String[]{
                        "Mã ĐH",
                        "Mã KH",
                        "NV Giao",
                        "Địa chỉ giao",
                        "Ngày đặt",
                        "Tổng tiền",
                        "Trạng thái"
                },
                0
        );

        tblChoGiao = new JTable(modelChoGiao);
        UIManager.put("TableHeader.cellBorder", BorderFactory.createEmptyBorder());
        customTables(tblChoGiao);
    

        JScrollPane scrollChoGiao = new JScrollPane(tblChoGiao);

        scrollChoGiao.setBorder(null);
        scrollChoGiao.getViewport().setBackground(Color.WHITE);

        // XÓA SCROLLBAR
        scrollChoGiao.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
        );

        scrollChoGiao.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        JPanel tableWrapper = new JPanel(new BorderLayout());

        tableWrapper.setBackground(Color.WHITE);

        tableWrapper.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(230, 230, 230)
                        ),
                        new EmptyBorder(10, 10, 10, 10)
                )
        );

        tableWrapper.add(scrollChoGiao, BorderLayout.CENTER);

        pnlChoGiao.add(tableWrapper, BorderLayout.CENTER);

        // =======================================================================================================================
        // =====================================================
        // PANEL LỊCH SỬ
        // =====================================================
       
        JPanel pnlLichSu = new JPanel(new BorderLayout(10, 10));

        pnlLichSu.setBackground(BG);
        pnlLichSu.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel lblLichSu = new JLabel("Lịch sử giao hàng");

        lblLichSu.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        lblLichSu.setForeground(new Color(120, 120, 120));

        pnlLichSu.add(lblLichSu, BorderLayout.NORTH);

        // =====================================================
        // TABLE LỊCH SỬ
        // =====================================================
        modelLichSu = new DefaultTableModel(
                new String[]{
                        "Mã ĐH",
                        "Mã KH",
                        "NV Giao",
                        "Ngày đặt",
                        "Tổng tiền",
                        "Trạng thái"
                },
                0
        );

        tblLichSu = new JTable(modelLichSu);

        customTables(tblLichSu);

        JScrollPane scrollLichSu = new JScrollPane(tblLichSu);

        scrollLichSu.setBorder(null);
        scrollLichSu.getViewport().setBackground(Color.WHITE);

        // XÓA SCROLLBAR
        scrollLichSu.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_NEVER
        );

        scrollLichSu.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        JPanel tableHistoryWrapper = new JPanel(new BorderLayout());

        tableHistoryWrapper.setBackground(Color.WHITE);

        tableHistoryWrapper.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(230, 230, 230)
                        ),
                        new EmptyBorder(10, 10, 10, 10)
                )
        );

        tableHistoryWrapper.add(scrollLichSu, BorderLayout.CENTER);

        pnlLichSu.add(tableHistoryWrapper, BorderLayout.CENTER);

        // =====================================================
        // ADD TAB
        // =====================================================
        tabbedPane.addTab("ĐƠN GIAO HÀNG", pnlChoGiao);
        tabbedPane.addTab("LỊCH SỬ GIAO HÀNG", pnlLichSu);

        // ÉP MÀU CHỮ TAB
        tabbedPane.setForegroundAt(0, Color.BLACK);
        tabbedPane.setForegroundAt(1, Color.BLACK);

        // ÉP NỀN TRẮNG
        tabbedPane.setBackgroundAt(0, Color.WHITE);
        tabbedPane.setBackgroundAt(1, Color.WHITE);

        add(tabbedPane, BorderLayout.CENTER);

        // =====================================================
        // LOAD DATA
        // =====================================================
        loadDanhSachChoGiao();
        loadLichSu();

        // =====================================================
        // EVENT NHẬN ĐƠN
        // =====================================================
        btnNhanDon.addActionListener(e -> {

            int row = tblChoGiao.getSelectedRow();

            if (row == -1) {

                JOptionPane.showMessageDialog(
                        this,
                        "Vui lòng chọn đơn hàng"
                );

                return;
            }

            String maDH =
                    modelChoGiao.getValueAt(row, 0).toString();

            boolean result =
                    bus.nhanDonGiao(maDH, maNV);

            if (result) {

                JOptionPane.showMessageDialog(
                        this,
                        "Nhận đơn thành công"
                );

                loadDanhSachChoGiao();
                loadLichSu();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Nhận đơn thất bại! Đơn đang ở trạng thái đang giao."
                );
            }
        });

        // =====================================================
        // EVENT THÀNH CÔNG
        // =====================================================
        btnThanhCong.addActionListener(e -> {

            int row = tblChoGiao.getSelectedRow();

            if (row == -1) {

                JOptionPane.showMessageDialog(
                        this,
                        "Vui lòng chọn đơn hàng"
                );

                return;
            }

            String maDH =
                    modelChoGiao.getValueAt(row, 0).toString();

            boolean result =
                    bus.giaoThanhCong(maDH, maNV);

            if (result) {

                JOptionPane.showMessageDialog(
                        this,
                        "Giao hàng thành công"
                );

                loadDanhSachChoGiao();
                loadLichSu();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Đơn hàng đang ở trạng thái chờ giao hàng"
                );
            }
        });

        // =====================================================
        // EVENT THẤT BẠI
        // =====================================================
        btnThatBai.addActionListener(e -> {

            int row = tblChoGiao.getSelectedRow();

            if (row == -1) {

                JOptionPane.showMessageDialog(
                        this,
                        "Vui lòng chọn đơn hàng"
                );

                return;
            }

            String trangThai =
                    modelChoGiao.getValueAt(row, 6).toString();

            if (!trangThai.equals("Đang giao")) {

                JOptionPane.showMessageDialog(
                        this,
                        "Chỉ được cập nhật khi đơn đang giao"
                );

                return;
            }

            String maDH =
                    modelChoGiao.getValueAt(row, 0).toString();

            JTextArea txtLyDo = new JTextArea();

            txtLyDo.setRows(5);
            txtLyDo.setColumns(30);

            txtLyDo.setLineWrap(true);
            txtLyDo.setWrapStyleWord(true);

            txtLyDo.setFont(
                    new Font("Segoe UI", Font.PLAIN, 14)
            );

            JScrollPane scrollPane =
                    new JScrollPane(txtLyDo);

            JPanel panel =
                    new JPanel(new BorderLayout(5, 5));

            JLabel lbl =
                    new JLabel("nhập lý do giao thất bại");

            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            lbl.setForeground(new Color(120, 120, 120));

            panel.add(lbl, BorderLayout.NORTH);
            panel.add(scrollPane, BorderLayout.CENTER);

            int option = JOptionPane.showConfirmDialog(
                    this,
                    panel,
                    "Giao hàng thất bại",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );

            if (option != JOptionPane.OK_OPTION) {
                return;
            }

            String lyDo =
                    txtLyDo.getText().trim();

            if (lyDo.isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "Vui lòng nhập lý do"
                );

                return;
            }

            boolean result =
                    bus.giaoThatBai(maDH, maNV, lyDo);

            if (result) {

                JOptionPane.showMessageDialog(
                        this,
                        "Cập nhật thành công"
                );

                loadDanhSachChoGiao();
                loadLichSu();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                         "Đơn hàng đang ở trạng thái chờ giao hàng"
                );
            }
        });

        // =====================================================
        // EVENT RELOAD
        // =====================================================
        btnReload.addActionListener(e -> {

            loadDanhSachChoGiao();
            loadLichSu();
        });
    }

    // =====================================================
    // BUTTON STYLE
    // =====================================================
    private JButton createButton(String text, Color color) {

        // JButton btn = new JButton(text);
         JButton btn = new JButton(text) {

        @Override
        protected void paintComponent(Graphics g) {

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            // nền nút
            g2.setColor(getBackground());

            g2.fillRoundRect(
                    0,
                    0,
                    getWidth(),
                    getHeight(),
                    12,
                    12
            );

            g2.dispose();

            super.paintComponent(g);
        }

        @Override
        protected void paintBorder(Graphics g) {
            // không vẽ border
        }
    };

        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));

        btn.setBackground(color);

        btn.setForeground(Color.WHITE);

        btn.setFocusPainted(false);

        btn.setBorderPainted(false);

        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.setPreferredSize(new Dimension(170, 42));

        btn.setBorder(
                new EmptyBorder(8, 18, 8, 18)
        );
        btn.addMouseListener(
                new java.awt.event.MouseAdapter() {

                    @Override
                    public void mouseEntered(
                            java.awt.event.MouseEvent evt
                    ) {

                        btn.setBackground(
                                color.darker()
                        );
                    }

                    @Override
                    public void mouseExited(
                            java.awt.event.MouseEvent evt
                    ) {

                        btn.setBackground(color);
                    }
                }
        );
        //  // MÀU NỀN
        //     btn.setBackground(color);

        //     // MÀU CHỮ
        //     btn.setForeground(Color.BLACK);

        //     btn.setFocusPainted(false);

        //     btn.setContentAreaFilled(false);

        //     btn.setOpaque(false);

        //     btn.setBorderPainted(false);

        //     btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        //     btn.setPreferredSize(new Dimension(170, 42));

        //     btn.setBorder(
        //             new EmptyBorder(8, 18, 8, 18)
        //     );
        return btn;
    }

    // =====================================================
    // LOAD ĐƠN CHỜ GIAO
    // =====================================================
    private void loadDanhSachChoGiao() {

        modelChoGiao.setRowCount(0);

        List<DonHangDTO> list =
                bus.layDonChoGiao(maNV);

        for (DonHangDTO dh : list) {

            modelChoGiao.addRow(new Object[]{

                    dh.getMaDH(),
                    dh.getMaKH(),
                    dh.getMaNV(),
                    dh.getDiaChiGiaoHang(),
                    dh.getTgDat(),
                    dh.getTongTien(),
                    dh.getTrangThaiDH()
            });
        }
    }

    // =====================================================
    // LOAD LỊCH SỬ
    // =====================================================
    private void loadLichSu() {

        modelLichSu.setRowCount(0);

        List<DonHangDTO> list =
                bus.lichSuGiaoHang(maNV);

        for (DonHangDTO dh : list) {

            modelLichSu.addRow(new Object[]{

                    dh.getMaDH(),
                    dh.getMaKH(),
                    dh.getMaNV(),
                    dh.getTgDat(),
                    dh.getTongTien(),
                    dh.getTrangThaiDH()
            });
        }
    }
    // =====================================================
// CUSTOM TABLE
// =====================================================
        private void customTable(JTable table) {

        // =====================================================
        // TABLE
        // =====================================================
        table.setRowHeight(45);

        table.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        table.setForeground(new Color(60, 60, 60));

        table.setBackground(Color.WHITE);

        table.setShowVerticalLines(false);

        table.setShowHorizontalLines(false);

        table.setIntercellSpacing(new Dimension(0, 0));

        table.setFocusable(false);

        table.setBorder(null);

        table.setSelectionBackground(
                new Color(232, 245, 233)
        );

        table.setSelectionForeground(Color.BLACK);

        // =====================================================
        // HEADER
        // =====================================================
        JTableHeader header = table.getTableHeader();

        header.setBackground(AppColor.HEADER);

        header.setForeground(Color.BLACK);

        header.setFont(
                new Font("Segoe UI", Font.BOLD, 15)
        );

        // XÓA VIỀN HEADER
        header.setBorder(null);

        header.setPreferredSize(
                new Dimension(header.getWidth(), 45)
        );

        // =====================================================
        // HEADER TITLE IN HOA
        // =====================================================
        for (int i = 0; i < table.getColumnCount(); i++) {

                String title =
                        table.getColumnName(i).toUpperCase();

                table.getColumnModel()
                        .getColumn(i)
                        .setHeaderValue(title);
        }

        // =====================================================
        // RENDER CELL
        // =====================================================
        table.setDefaultRenderer(
                Object.class,
                new DefaultTableCellRenderer() {

                        @Override
                        public Component getTableCellRendererComponent(
                                JTable table,
                                Object value,
                                boolean isSelected,
                                boolean hasFocus,
                                int row,
                                int column
                        ) {

                        JLabel lbl =
                                (JLabel)
                                        super.getTableCellRendererComponent(
                                                table,
                                                value,
                                                isSelected,
                                                hasFocus,
                                                row,
                                                column
                                        );
                       
                        // CAN GIUA TAT CA
                        // setHorizontalAlignment(SwingConstants.CENTER);

                        // IN DAM TAT CA
                        // lbl.setFont(new Font("Arial", Font.BOLD, 13));

                        // =========================================
                        // PADDING
                        // =========================================
                        lbl.setBorder(
                                new EmptyBorder(0, 12, 0, 12)
                        );

                        // =========================================
                        // ROW BACKGROUND
                        // =========================================
                        if (!isSelected) {

                                if (row % 2 == 0) {

                                lbl.setBackground(Color.WHITE);

                                } else {

                                lbl.setBackground(
                                        new Color(248, 249, 251)
                                );
                                }       
                        }

                    // =========================================
                    // MÃ ĐH -> IN HOA
                    // =========================================
                    if (column == 0 && value != null) {

                        lbl.setText(
                                value.toString().toUpperCase()
                        );
                        // IN ĐẬM
                        setFont(getFont().deriveFont(Font.BOLD));
                        setHorizontalAlignment(SwingConstants.CENTER);
                    }

                    // =========================================
                    // STATUS COLOR
                    // =========================================
                    if (column == 6 && value != null) {

                        String status =
                                value.toString();

                        if (status.equalsIgnoreCase(
                                "Chờ giao hàng"
                        )) {

                            lbl.setForeground(
                                    new Color(46, 125, 50)
                            );

                        } else if (status.equalsIgnoreCase(
                                "Giao thất bại"
                        )) {

                            lbl.setForeground(
                                    new Color(211, 47, 47)
                            );

                        } else {

                            lbl.setForeground(
                                    new Color(60, 60, 60)
                            );
                        }

                    } else {

                        lbl.setForeground(
                                new Color(60, 60, 60)
                        );
                    }
                    lbl.setBorder(BorderFactory.createEmptyBorder());
                    return lbl;
                }
            }
    );
}
        // =====================================================
        // CUSTOM TABLE
        // =====================================================
        private void customTables(JTable table) {

        // =====================================================
        // TABLE
        // =====================================================
        table.setRowHeight(44);

        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        table.setForeground(new Color(70, 70, 70));

        table.setBackground(Color.WHITE);

        table.setShowVerticalLines(false);

        table.setShowHorizontalLines(false);

        table.setIntercellSpacing(new Dimension(0, 0));

        table.setFocusable(false);

        table.setBorder(null);

        table.setSelectionBackground(
                new Color(232, 245, 233)
        );

        table.setSelectionForeground(Color.BLACK);

        // =====================================================
        // HEADER
        // =====================================================
        JTableHeader header = table.getTableHeader();

        header.setBackground(new Color(245, 247, 250));

        header.setForeground(Color.BLACK);

        header.setFont(
                new Font("Segoe UI", Font.BOLD, 14)
        );

        // XÓA VIỀN HEADER
        header.setBorder(BorderFactory.createEmptyBorder());

        UIManager.put(
                "TableHeader.cellBorder",
                BorderFactory.createEmptyBorder()
        );

        header.setPreferredSize(
                new Dimension(header.getWidth(), 42)
        );

    // =====================================================
    // HEADER IN HOA
    // =====================================================
    for (int i = 0; i < table.getColumnCount(); i++) {

        String title =
                table.getColumnName(i).toUpperCase();

        table.getColumnModel()
                .getColumn(i)
                .setHeaderValue(title);
    }

    // =====================================================
    // CELL RENDERER
    // =====================================================
    table.setDefaultRenderer(
            Object.class,
            new DefaultTableCellRenderer() {

                @Override
                public Component getTableCellRendererComponent(
                        JTable table,
                        Object value,
                        boolean isSelected,
                        boolean hasFocus,
                        int row,
                        int column
                ) {

                    JLabel lbl =
                            (JLabel) super.getTableCellRendererComponent(
                                    table,
                                    value,
                                    isSelected,
                                    hasFocus,
                                    row,
                                    column
                            );

                    // =====================================
                    // FONT
                    // =====================================
                    lbl.setFont(
                            new Font("Segoe UI", Font.PLAIN, 14)
                    );

                    // =====================================
                    // PADDING
                    // =====================================
                    lbl.setBorder(
                            new EmptyBorder(0, 12, 0, 12)
                    );

                    // =====================================
                    // ALIGN
                    // =====================================
                    lbl.setHorizontalAlignment(
                            SwingConstants.CENTER
                    );

                    // =====================================
                    // ROW COLOR
                    // =====================================
                    if (!isSelected) {

                        if (row % 2 == 0) {

                            lbl.setBackground(Color.WHITE);

                        } else {

                            lbl.setBackground(
                                    new Color(248, 249, 251)
                            );
                        }

                        lbl.setForeground(
                                new Color(70, 70, 70)
                        );
                    }

                    // =====================================
                    // MÃ ĐH IN HOA + ĐẬM
                    // =====================================
                    if (column == 0 && value != null) {

                        lbl.setText(
                                value.toString().toUpperCase()
                        );

                        lbl.setFont(
                                new Font(
                                        "Segoe UI",
                                        Font.BOLD,
                                        14
                                )
                        );
                    }

                    // =====================================
                    // TRẠNG THÁI COLOR
                    // =====================================
                    int statusColumn =
                            table.getColumnCount() - 1;

                    if (column == statusColumn
                            && value != null) {

                        String status =
                                value.toString();

                        switch (status) {

                            case "Chờ giao hàng":

                                lbl.setForeground(
                                        new Color(46, 125, 50)
                                );

                                break;

                            case "Đang giao":

                                lbl.setForeground(
                                        new Color(2, 136, 209)
                                );

                                break;

                            case "Giao thành công":

                                lbl.setForeground(
                                        new Color(56, 142, 60)
                                );

                                break;

                            case "Giao thất bại":

                                lbl.setForeground(
                                        new Color(211, 47, 47)
                                );

                                break;
                        }
                    }

                    return lbl;
                }
            }
    );
}
}

