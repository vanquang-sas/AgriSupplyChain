
package gui.panel;

import bus.GiaoHangBUS;
import dto.DonHangDTO;

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

        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabbedPane.setBackground(Color.WHITE);
        tabbedPane.setFocusable(false);

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

        JLabel lblChoGiao = new JLabel("đơn giao hàng");

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

        customTable(tblChoGiao);

        JScrollPane scrollChoGiao = new JScrollPane(tblChoGiao);

        scrollChoGiao.setBorder(null);

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

        // =====================================================
        // PANEL LỊCH SỬ
        // =====================================================
        JPanel pnlLichSu = new JPanel(new BorderLayout(10, 10));

        pnlLichSu.setBackground(BG);
        pnlLichSu.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel lblLichSu = new JLabel("lịch sử giao hàng");

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

        customTable(tblLichSu);

        JScrollPane scrollLichSu = new JScrollPane(tblLichSu);

        scrollLichSu.setBorder(null);

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
                        "Nhận đơn thất bại!"
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
                        "Đơn chưa ở trạng thái đang giao"
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
                        "Cập nhật thất bại"
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
    // CUSTOM TABLE
    // =====================================================
    private void customTable(JTable table) {

        table.setRowHeight(42);

        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        table.setForeground(new Color(90, 90, 90));

        table.setBackground(Color.WHITE);

        table.setShowVerticalLines(false);

        table.setGridColor(new Color(240, 240, 240));

        table.setIntercellSpacing(new Dimension(0, 1));

        table.setSelectionBackground(
                new Color(232, 245, 233)
        );

        table.setSelectionForeground(Color.BLACK);

        table.setFocusable(false);

        JTableHeader header = table.getTableHeader();

        header.setBackground(Color.WHITE);

        header.setForeground(Color.BLACK);

        header.setFont(
                new Font("Segoe UI", Font.BOLD, 14)
        );

        header.setBorder(
                BorderFactory.createMatteBorder(
                        0,
                        0,
                        1,
                        0,
                        new Color(230, 230, 230)
                )
        );

        header.setPreferredSize(
                new Dimension(header.getWidth(), 42)
        );

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

                        Component c =
                                super.getTableCellRendererComponent(
                                        table,
                                        value,
                                        isSelected,
                                        hasFocus,
                                        row,
                                        column
                                );

                        if (!isSelected) {

                            if (row % 2 == 0) {

                                c.setBackground(Color.WHITE);

                            } else {

                                c.setBackground(
                                        new Color(248, 249, 251)
                                );
                            }
                        }

                        setBorder(
                                new EmptyBorder(0, 10, 0, 10)
                        );

                        return c;
                    }
                }
        );
    }

    // =====================================================
    // BUTTON STYLE
    // =====================================================
    private JButton createButton(String text, Color color) {

        JButton btn = new JButton(text);

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
}

