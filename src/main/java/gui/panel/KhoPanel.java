package gui.panel;

import bus.KhoBUS;
import dto.KhoDTO;
import gui.component.IntegratedSearch;
import gui.component.Pagination;
import gui.component.ActionCellEditor;
import gui.component.ActionCellRenderer;
import gui.dialog.KhoForm;
import raven.modal.ModalDialog;
import raven.modal.component.SimpleModalBorder;
import util.AppColor;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class KhoPanel extends JPanel {

    private final KhoBUS bus = new KhoBUS();
    private JTable table;
    private DefaultTableModel tableModel;
    private IntegratedSearch searchPanel;

    // Badge màu cho LoaiKho
    private static final Color MAT_BG   = new Color(224, 242, 254);
    private static final Color MAT_FG   = new Color(3, 105, 161);
    private static final Color LANH_BG  = new Color(219, 234, 254);
    private static final Color LANH_FG  = new Color(29, 78, 216);
    private static final Color DONG_BG  = new Color(237, 233, 254);
    private static final Color DONG_FG  = new Color(109, 40, 217);

    public KhoPanel() {
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 12));
        setBackground(AppColor.SURFACE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(buildTopPanel(), BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);
        add(new Pagination(), BorderLayout.SOUTH);
    }

    // ===================== TOP PANEL =====================
    private JPanel buildTopPanel() {
        JLabel title = new JLabel("Quản lý Kho bãi");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(AppColor.TEXT_PRIMARY);

        // Stats summary
        JPanel statsRow = buildStatsRow();

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        left.setBackground(AppColor.SURFACE);
        JButton btnAdd = createButton("+ Thêm kho mới", AppColor.PRIMARY, Color.WHITE);
        btnAdd.addActionListener(e -> showForm(null));
        left.add(btnAdd);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setBackground(AppColor.SURFACE);
        searchPanel = new IntegratedSearch();
        searchPanel.setPreferredSize(new Dimension(260, 36));
        JButton btnSearch = createButton("Tìm", AppColor.BORDER, AppColor.TEXT_PRIMARY);
        btnSearch.addActionListener(e -> {
            String kw = searchPanel.getText().trim();
            if (kw.isEmpty()) loadData(); else loadData(bus.timKiem(kw));
        });
        searchPanel.addActionListener(e -> btnSearch.doClick());
        right.add(searchPanel);
        right.add(btnSearch);

        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setBackground(AppColor.SURFACE);
        titleRow.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        titleRow.add(title, BorderLayout.WEST);

        JPanel actionRow = new JPanel(new BorderLayout());
        actionRow.setBackground(AppColor.SURFACE);
        actionRow.add(left, BorderLayout.WEST);
        actionRow.add(right, BorderLayout.EAST);

        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setBackground(AppColor.SURFACE);
        top.add(titleRow);
        top.add(statsRow);
        top.add(Box.createRigidArea(new Dimension(0, 8)));
        top.add(actionRow);
        return top;
    }

    // 3 ô thống kê nhỏ: Tổng / Kho Mát / Kho Lạnh / Kho Đông
    private JPanel buildStatsRow() {
        JPanel row = new JPanel(new GridLayout(1, 4, 10, 0));
        row.setBackground(AppColor.SURFACE);
        row.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));

        List<KhoDTO> all = bus.getAll();
        long tongSo  = all.size();
        long soMat   = all.stream().filter(k -> "Mát".equals(k.getLoaiKho())).count();
        long soLanh  = all.stream().filter(k -> "Lạnh".equals(k.getLoaiKho())).count();
        long soDong  = all.stream().filter(k -> "Đông".equals(k.getLoaiKho())).count();

        row.add(buildStatCard("Tổng kho",    String.valueOf(tongSo), AppColor.PRIMARY,  new Color(236, 253, 245)));
        row.add(buildStatCard("Kho Mát",     String.valueOf(soMat),  MAT_FG,            MAT_BG));
        row.add(buildStatCard("Kho Lạnh",    String.valueOf(soLanh), LANH_FG,           LANH_BG));
        row.add(buildStatCard("Kho Đông",    String.valueOf(soDong), DONG_FG,           DONG_BG));
        return row;
    }

    private JPanel buildStatCard(String label, String value, Color fg, Color bg) {
        JPanel card = new JPanel(new BorderLayout(8, 0));
        card.setBackground(bg);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(fg.getRed(), fg.getGreen(), fg.getBlue(), 60), 1, true),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)));

        JLabel lblVal = new JLabel(value);
        lblVal.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblVal.setForeground(fg);

        JLabel lblLbl = new JLabel(label);
        lblLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblLbl.setForeground(fg);

        JPanel text = new JPanel(new GridLayout(2, 1, 0, 2));
        text.setBackground(bg);
        text.add(lblLbl);
        text.add(lblVal);
        card.add(text, BorderLayout.CENTER);
        return card;
    }

    // ===================== TABLE PANEL =====================
    private JScrollPane buildTablePanel() {
        String[] cols = {"Mã kho", "Tên kho", "Loại kho", "Địa chỉ", "Mô tả", "Thao tác"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c == 5; }
        };

        table = new JTable(tableModel);
        table.setRowHeight(44);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(235, 248, 240));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        table.getTableHeader().setBackground(AppColor.PRIMARY);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setPreferredSize(new Dimension(0, 44));
        table.getTableHeader().setReorderingAllowed(false);

        int[] widths = {80, 180, 90, 200, 220, 120};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        // Badge renderer cho cột LoaiKho
        table.getColumnModel().getColumn(2).setCellRenderer(new LoaiKhoBadgeRenderer());
        table.getColumnModel().getColumn(5).setCellRenderer(new ActionCellRenderer());
        table.getColumnModel().getColumn(5).setCellEditor(new ActionCellEditor(e -> {
            int row = table.getSelectedRow();
            if (row == -1) return;
            String maKho = tableModel.getValueAt(row, 0).toString();
            if ("EDIT".equals(e.getActionCommand())) {
                showForm(maKho);
            }
            // Kho không cho xóa (ràng buộc FK với TONKHO) → ẩn nút Delete ở ActionPanel
            // hoặc có thể thêm logic disable sau
        }));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(AppColor.BORDER));
        scroll.getViewport().setBackground(AppColor.SURFACE);
        return scroll;
    }

    // ===================== DIALOG =====================
    private void showForm(String maKho) {
        KhoForm form   = new KhoForm(maKho);
        String title   = (maKho == null) ? "Thêm kho mới" : "Cập nhật thông tin kho";
        SimpleModalBorder modal = new SimpleModalBorder(
                form, title, SimpleModalBorder.YES_NO_OPTION,
                (ctrl, action) -> {
                    if (action == SimpleModalBorder.YES_OPTION) {
                        if (form.saveData()) { ctrl.close(); loadData(); }
                    } else ctrl.close();
                });
        ModalDialog.showModal(this, modal);
    }

    // ===================== LOAD DỮ LIỆU =====================
    public void loadData() { loadData(bus.getAll()); }

    private void loadData(List<KhoDTO> list) {
        tableModel.setRowCount(0);
        for (KhoDTO k : list) {
            tableModel.addRow(new Object[]{
                k.getMaKho(), k.getTenKho(), k.getLoaiKho(),
                k.getDiaChi(), k.getMoTa(), ""
            });
        }
    }

    // ===================== HELPER =====================
    private JButton createButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg); btn.setForeground(fg);
        btn.setFocusPainted(false); btn.setBorderPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        return btn;
    }

    // Badge renderer cho cột Loại kho
    static class LoaiKhoBadgeRenderer extends JLabel implements javax.swing.table.TableCellRenderer {
        LoaiKhoBadgeRenderer() {
            setOpaque(true); setHorizontalAlignment(CENTER);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
        }
        @Override
        public Component getTableCellRendererComponent(JTable t, Object v,
                boolean sel, boolean foc, int r, int c) {
            String loai = v != null ? v.toString() : "";
            switch (loai) {
                case "Mát":  setBackground(MAT_BG);  setForeground(MAT_FG);  setText("🌿 Mát");  break;
                case "Lạnh": setBackground(LANH_BG); setForeground(LANH_FG); setText("❄ Lạnh"); break;
                case "Đông": setBackground(DONG_BG); setForeground(DONG_FG); setText("🧊 Đông"); break;
                default:     setBackground(Color.WHITE); setForeground(Color.GRAY); setText(loai);
            }
            return this;
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(8, 5, getWidth() - 16, getHeight() - 10, 14, 14);
            super.paintComponent(g);
            g2.dispose();
        }
    }
}