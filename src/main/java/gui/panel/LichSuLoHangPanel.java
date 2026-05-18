package gui.panel;

import bus.LoHangBUS;
import dto.ChiTietLoHangDTO;
import dto.LoHangDTO;
import util.AppColor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

public class LichSuLoHangPanel extends JPanel {

    private final LoHangBUS loHangBUS = new LoHangBUS();
    private final DefaultTableModel tableModel;
    private final JTable table;
    private JLabel lblTong, lblChoNhap, lblDaNhap;

    public LichSuLoHangPanel() {
        setLayout(new BorderLayout());
        setBackground(AppColor.BACKGROUND);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        // Top: title card + stats (match other panels)
        JPanel topWrapper = new JPanel();
        topWrapper.setLayout(new BoxLayout(topWrapper, BoxLayout.Y_AXIS));
        topWrapper.setOpaque(false);

        RoundedPanel titleCard = new RoundedPanel(16);
        titleCard.setBackground(AppColor.SURFACE);
        titleCard.setLayout(new BorderLayout());
        titleCard.setBorder(new EmptyBorder(24, 24, 24, 24));
        titleCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        titleCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = new JLabel("LỊCH SỬ LÔ HÀNG");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(AppColor.TEXT_PRIMARY);
        titleCard.add(title, BorderLayout.WEST);

        topWrapper.add(titleCard);
        topWrapper.add(Box.createVerticalStrut(18));
        topWrapper.add(buildStats());
        topWrapper.add(Box.createVerticalStrut(18));

        add(topWrapper, BorderLayout.NORTH);

        // Table card
        RoundedPanel card = new RoundedPanel(16);
        card.setBackground(AppColor.SURFACE);
        card.setLayout(new BorderLayout());

        JPanel toolBar = new JPanel(new BorderLayout());
        toolBar.setOpaque(false);
        toolBar.setBorder(new EmptyBorder(16, 16, 16, 16));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        left.setOpaque(false);
        JButton btnRefresh = createIconButton("icons/refresh.svg");
        JButton btnView = createActionButton("Xem chi tiết");
        left.add(btnView);
        left.add(btnRefresh);
        toolBar.add(left, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);
        ModernSearchField search = new ModernSearchField("Tìm kiếm lịch sử lô hàng...");
        search.setPreferredSize(new Dimension(300, 36));
        right.add(search);
        toolBar.add(right, BorderLayout.EAST);

        card.add(toolBar, BorderLayout.NORTH);

        String[] columns = { "Mã lô", "Nhà cung cấp", "Mã nhân viên", "Ngày nhập", "Tổng tiền", "Trạng thái" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setAutoCreateRowSorter(true);
        table.setRowHeight(48);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTableHeader headerTable = table.getTableHeader();
        headerTable.setFont(new Font("Segoe UI", Font.BOLD, 13));
        headerTable.setBackground(AppColor.HEADER);
        headerTable.setForeground(AppColor.TEXT_PRIMARY);
        headerTable.setReorderingAllowed(false);

        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setForeground(AppColor.TEXT_PRIMARY);
        table.setGridColor(AppColor.BORDER);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setSelectionBackground(new Color(220, 252, 231));
        table.setSelectionForeground(AppColor.TEXT_PRIMARY);

        // Header renderer and column sizing to match other panels
        DefaultTableCellRenderer hdrRdr = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean isS, boolean hasF, int r,
                    int c) {
                Component comp = super.getTableCellRendererComponent(t, v, isS, hasF, r, c);
                comp.setFont(new Font("Segoe UI", Font.BOLD, 14));
                comp.setBackground(AppColor.SECONDARY_HOVER);
                ((JLabel) comp).setBorder(new EmptyBorder(0, 16, 0, 8));
                return comp;
            }
        };
        for (int i = 0; i < columns.length; i++)
            table.getColumnModel().getColumn(i).setHeaderRenderer(hdrRdr);

        // Cell renderers for zebra and badges
        ZebraHoverRenderer zebraRdr = new ZebraHoverRenderer(table);
        for (int i = 0; i < columns.length; i++)
            table.getColumnModel().getColumn(i).setCellRenderer(zebraRdr);
        // Status column
        table.getColumnModel().getColumn(5).setCellRenderer(new BadgeRenderer(table));

        // Column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(120);
        table.getColumnModel().getColumn(1).setPreferredWidth(220);
        table.getColumnModel().getColumn(2).setPreferredWidth(120);
        table.getColumnModel().getColumn(3).setPreferredWidth(140);
        table.getColumnModel().getColumn(4).setPreferredWidth(120);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(AppColor.SURFACE);
        card.add(scrollPane, BorderLayout.CENTER);

        add(card, BorderLayout.CENTER);

        // Actions
        btnRefresh.addActionListener(e -> loadHistory());
        btnView.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một lô hàng để xem.", "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            String maLH = tableModel.getValueAt(table.convertRowIndexToModel(r), 0).toString();
            showDetailDialog(maLH);
        });

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int r = table.rowAtPoint(e.getPoint());
                    if (r >= 0) {
                        String maLH = tableModel.getValueAt(table.convertRowIndexToModel(r), 0).toString();
                        showDetailDialog(maLH);
                    }
                }
            }
        });

        search.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void onSearch() {
                String kw = search.getText().trim();
                loadHistory(kw.isEmpty() ? null : kw);
            }

            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                onSearch();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                onSearch();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                onSearch();
            }
        });

        loadHistory();
    }

    private JPanel buildStats() {
        JPanel row = new JPanel(new GridLayout(1, 3, 16, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblTong = new JLabel("0");
        lblChoNhap = new JLabel("0");
        lblDaNhap = new JLabel("0");
        row.add(createStatCard("Tổng lô hàng", lblTong, AppColor.INFO, "📦"));
        row.add(createStatCard("Đang chờ nhập", lblChoNhap, AppColor.WARNING, "⏳"));
        row.add(createStatCard("Đã nhập kho", lblDaNhap, AppColor.SUCCESS, "✅"));
        return row;
    }

    private JPanel createStatCard(String cardTitle, JLabel valueLabel, Color accent, String icon) {
        RoundedPanel card = new RoundedPanel(12);
        card.setBackground(AppColor.SURFACE);
        card.setLayout(new BorderLayout(14, 0));
        card.setBorder(new EmptyBorder(14, 18, 14, 18));

        JPanel iconWrap = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 35));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        iconWrap.setOpaque(false);
        iconWrap.setPreferredSize(new Dimension(52, 52));
        iconWrap.setLayout(new GridBagLayout());
        JLabel iconLbl = new JLabel(icon);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        iconWrap.add(iconLbl);

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        textPanel.setOpaque(false);
        JLabel lTitle = new JLabel(cardTitle);
        lTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lTitle.setForeground(AppColor.TEXT_SECONDARY);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(AppColor.TEXT_PRIMARY);
        textPanel.add(lTitle);
        textPanel.add(valueLabel);

        card.add(iconWrap, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);
        return card;
    }

    private void loadHistory() {
        loadHistory(null);
    }

    private void loadHistory(String keyword) {
        tableModel.setRowCount(0);
        java.util.List<LoHangDTO> history = loHangBUS.getLichSuLoHang();
        int total = 0, pending = 0, done = 0;
        DecimalFormat currency = new DecimalFormat("#,###");
        for (LoHangDTO lh : history) {
            if (keyword != null && !keyword.isEmpty()) {
                String lower = keyword.toLowerCase();
                if (!(lh.getMaLH().toLowerCase().contains(lower)
                        || (lh.getTenNCC() != null && lh.getTenNCC().toLowerCase().contains(lower))
                        || (lh.getMaNCC() != null && lh.getMaNCC().toLowerCase().contains(lower))
                        || (lh.getTrangThaiLH() != null && lh.getTrangThaiLH().toLowerCase().contains(lower)))) {
                    continue;
                }
            }
            String formattedTongTien = currency.format(lh.getTongTien());
            tableModel.addRow(new Object[] {
                    lh.getMaLH(), lh.getTenNCC() != null ? lh.getTenNCC() : lh.getMaNCC(), lh.getMaNV(), lh.getTgNhap(),
                    formattedTongTien, lh.getTrangThaiLH()
            });
            total++;
            if (lh.getTrangThaiLH() != null && lh.getTrangThaiLH().equalsIgnoreCase("Đã nhập kho"))
                done++;
            else
                pending++;
        }
        lblTong.setText(String.valueOf(total));
        lblChoNhap.setText(String.valueOf(pending));
        lblDaNhap.setText(String.valueOf(done));
    }

    private JButton createIconButton(String svgPath) {
        JButton btn = new JButton();
        try {
            btn.setIcon(new com.formdev.flatlaf.extras.FlatSVGIcon(svgPath, 18, 18));
        } catch (Throwable ex) {
            btn.setText("↻");
            btn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        }
        btn.setPreferredSize(new Dimension(36, 36));
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.putClientProperty("hover", true);
                btn.repaint();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.putClientProperty("hover", false);
                btn.repaint();
            }
        });
        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean isHovered = Boolean.TRUE.equals(c.getClientProperty("hover"));
                g2.setColor(isHovered ? AppColor.SECONDARY_HOVER : AppColor.SURFACE);
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 10, 10);
                g2.setColor(AppColor.BORDER);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(1, 1, c.getWidth() - 3, c.getHeight() - 3, 10, 10);
                g2.dispose();
                super.paint(g, c);
            }
        });
        return btn;
    }

    private JButton createActionButton(String text) {
        return createActionButton(text, AppColor.INFO, AppColor.INFO_HOVER, AppColor.INFO_ACTIVE);
    }

    private JButton createActionButton(String text, Color bg, Color hoverColor, Color activeColor) {
        JButton btn = new JButton(text) {
            private Color current = bg;
            {
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        current = hoverColor;
                        repaint();
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        current = bg;
                        repaint();
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {
                        current = activeColor;
                        repaint();
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        current = hoverColor;
                        repaint();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(current);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        Font font = new Font("Segoe UI", Font.BOLD, 13);
        btn.setFont(font);
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        int textWidth = btn.getFontMetrics(font).stringWidth(text);
        int width = Math.max(100, textWidth + 32);
        btn.setPreferredSize(new Dimension(width, 36));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void showDetailDialog(String maLH) {
        if (maLH == null || maLH.trim().isEmpty())
            return;

        LoHangDTO current = null;
        for (LoHangDTO lh : loHangBUS.getLichSuLoHang()) {
            if (maLH.equals(lh.getMaLH())) {
                current = lh;
                break;
            }
        }
        if (current == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy lô hàng.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        java.util.List<ChiTietLoHangDTO> details = loHangBUS.getChiTietLoHang(maLH);

        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Chi tiết lô hàng",
                Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setLayout(new BorderLayout(16, 16));
        dialog.getContentPane().setBackground(AppColor.BACKGROUND);

        JPanel header = new RoundedPanel(16);
        header.setBackground(AppColor.SURFACE);
        header.setBorder(new EmptyBorder(20, 20, 20, 20));
        header.setLayout(new GridLayout(3, 2, 14, 14));
        header.add(createLabel("Mã lô:"));
        header.add(createValueLabel(current.getMaLH()));
        header.add(createLabel("Nhà cung cấp:"));
        header.add(createValueLabel(current.getTenNCC() != null ? current.getTenNCC() : current.getMaNCC()));
        header.add(createLabel("Mã nhân viên:"));
        header.add(createValueLabel(current.getMaNV()));
        dialog.add(header, BorderLayout.NORTH);

        String[] detailCols = { "Mã CT", "Mã SP", "Giá mua", "Số lượng", "Thành tiền" };
        DefaultTableModel detailModel = new DefaultTableModel(detailCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable detailTable = new JTable(detailModel);
        detailTable.setRowHeight(40);
        detailTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        detailTable.setGridColor(AppColor.BORDER);
        detailTable.setShowVerticalLines(false);
        detailTable.setShowHorizontalLines(true);
        detailTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < detailCols.length; i++)
            detailTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);

        DecimalFormat currency = new DecimalFormat("#,##0.##");
        for (ChiTietLoHangDTO item : details) {
            detailModel.addRow(new Object[] {
                    item.getMaCTLH(), item.getMaSP(), currency.format(item.getGiaMua()),
                    currency.format(item.getSoLuong()), currency.format(item.getThanhTien())
            });
        }

        JScrollPane detailScroll = new JScrollPane(detailTable);
        detailScroll.setBorder(BorderFactory.createEmptyBorder());
        detailScroll.getViewport().setBackground(AppColor.SURFACE);
        detailScroll.setPreferredSize(new Dimension(680, 280));

        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.setBorder(new EmptyBorder(0, 20, 0, 20));
        centerWrapper.add(detailScroll, BorderLayout.CENTER);
        dialog.add(centerWrapper, BorderLayout.CENTER);

        JButton btnClose = createActionButton("Đóng", AppColor.ERROR, AppColor.ERROR_HOVER, AppColor.ERROR_ACTIVE);
        btnClose.addActionListener(e -> dialog.dispose());
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(0, 0, 20, 20));
        footer.add(btnClose);
        dialog.add(footer, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(AppColor.TEXT_SECONDARY);
        return lbl;
    }

    private JLabel createValueLabel(String text) {
        JLabel lbl = new JLabel(text != null ? text : "");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(AppColor.TEXT_PRIMARY);
        return lbl;
    }

    // Simple rounded panel used by many other panels
    static class RoundedPanel extends JPanel {
        private final int arc;

        RoundedPanel(int arc) {
            this.arc = arc;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            for (int i = 5; i >= 1; i--) {
                g2.setColor(new Color(0, 0, 0, 6));
                g2.fill(new RoundRectangle2D.Float(i, i + 1, getWidth() - i * 2, getHeight() - i * 2, arc, arc));
            }
            g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 5, getHeight() - 5, arc, arc));
            g2.dispose();
            super.paintComponent(g);
        }
    }

    static class ZebraHoverRenderer extends DefaultTableCellRenderer {
        private int hoverRow = -1;

        ZebraHoverRenderer(JTable tbl) {
            tbl.addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    int r = tbl.rowAtPoint(e.getPoint());
                    if (r != hoverRow) {
                        hoverRow = r;
                        tbl.repaint();
                    }
                }
            });
            tbl.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseExited(MouseEvent e) {
                    hoverRow = -1;
                    tbl.repaint();
                }
            });
        }

        @Override
        public Component getTableCellRendererComponent(JTable t, Object value, boolean selected, boolean focused,
                int row, int col) {
            super.getTableCellRendererComponent(t, value, selected, focused, row, col);
            setBorder(new EmptyBorder(0, 16, 0, 8));
            setFont(new Font("Segoe UI", Font.PLAIN, 13));
            setForeground(AppColor.TEXT_SECONDARY);

            if (selected)
                setBackground(new Color(220, 252, 231));
            else if (row == hoverRow)
                setBackground(new Color(240, 253, 244));
            else if (row % 2 == 0)
                setBackground(AppColor.SURFACE);
            else
                setBackground(new Color(250, 250, 250));
            return this;
        }
    }

    static class BadgeRenderer extends DefaultTableCellRenderer {
        private int hoverRow = -1;

        BadgeRenderer(JTable tbl) {
            tbl.addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    int r = tbl.rowAtPoint(e.getPoint());
                    if (r != hoverRow) {
                        hoverRow = r;
                        tbl.repaint();
                    }
                }
            });
            tbl.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseExited(MouseEvent e) {
                    hoverRow = -1;
                    tbl.repaint();
                }
            });
        }

        @Override
        public Component getTableCellRendererComponent(JTable t, Object value, boolean selected, boolean focused,
                int row, int col) {
            JPanel cell = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 8));
            cell.setOpaque(true);
            if (selected)
                cell.setBackground(new Color(220, 252, 231));
            else if (row == hoverRow)
                cell.setBackground(new Color(240, 253, 244));
            else if (row % 2 == 0)
                cell.setBackground(AppColor.SURFACE);
            else
                cell.setBackground(new Color(250, 250, 250));

            String text = value == null ? "" : value.toString();
            final Color badgeBg;
            final Color badgeFg;
            if ("Đang chờ nhập".equalsIgnoreCase(text)) {
                badgeBg = new Color(254, 243, 199);
                badgeFg = AppColor.WARNING_ACTIVE;
            } else if ("Đã nhập kho".equalsIgnoreCase(text)) {
                badgeBg = AppColor.SUCCESS_HOVER;
                badgeFg = Color.WHITE;
            } else {
                badgeBg = AppColor.SECONDARY_HOVER;
                badgeFg = AppColor.TEXT_PRIMARY;
            }

            JLabel badge = new JLabel(text) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(badgeBg);
                    g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            badge.setFont(new Font("Segoe UI", Font.BOLD, 11));
            badge.setForeground(badgeFg);
            badge.setOpaque(false);
            badge.setBorder(new EmptyBorder(3, 10, 3, 10));

            cell.add(badge);
            return cell;
        }
    }

    static class ModernSearchField extends JTextField {
        private final String placeholder;

        public ModernSearchField(String placeholder) {
            this.placeholder = placeholder;
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 36));
            setFont(new Font("Segoe UI", Font.PLAIN, 13));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(AppColor.SURFACE);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            g2.setColor(isFocusOwner() ? AppColor.PRIMARY : AppColor.BORDER);
            g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 12, 12);
            super.paintComponent(g);
            if (getText().isEmpty() && !isFocusOwner()) {
                g2.setColor(AppColor.TEXT_SECONDARY);
                g2.drawString(placeholder, 16,
                        (getHeight() - g.getFontMetrics().getHeight()) / 2 + g.getFontMetrics().getAscent());
            }
            g2.dispose();
        }
    }

}
