package gui.panel;

import bus.NhaCungCapBUS;
import dto.NhaCungCapDTO;
import gui.component.IntegratedSearch;
import gui.component.Pagination;
import gui.component.ActionCellEditor;
import gui.component.ActionCellRenderer;
import gui.dialog.NhaCungCapForm;
import raven.modal.ModalDialog;
import raven.modal.component.SimpleModalBorder;
import util.AppColor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class NhaCungCapPanel extends JPanel {

    private final NhaCungCapBUS bus = new NhaCungCapBUS();
    private JTable table;
    private DefaultTableModel tableModel;
    private IntegratedSearch searchPanel;

    public NhaCungCapPanel() {
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
        JLabel title = new JLabel("Quản lý Nhà cung cấp");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(AppColor.TEXT_PRIMARY);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        left.setBackground(AppColor.SURFACE);

        JButton btnAdd = createButton("+ Thêm mới", AppColor.PRIMARY, Color.WHITE);
        btnAdd.addActionListener(e -> showForm(null));
        left.add(btnAdd);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setBackground(AppColor.SURFACE);

        searchPanel = new IntegratedSearch();
        searchPanel.setPreferredSize(new Dimension(260, 36));

        JButton btnSearch = createButton("Tìm", AppColor.BORDER, AppColor.TEXT_PRIMARY);
        btnSearch.addActionListener(e -> {
            String kw = searchPanel.getText().trim();
            if (kw.isEmpty()) loadData();
            else loadData(bus.timKiem(kw));
        });
        searchPanel.addActionListener(e -> btnSearch.doClick());

        right.add(searchPanel);
        right.add(btnSearch);

        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setBackground(AppColor.SURFACE);
        titleRow.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        titleRow.add(title, BorderLayout.WEST);

        JPanel actionRow = new JPanel(new BorderLayout());
        actionRow.setBackground(AppColor.SURFACE);
        actionRow.add(left, BorderLayout.WEST);
        actionRow.add(right, BorderLayout.EAST);

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(AppColor.SURFACE);
        top.add(titleRow, BorderLayout.NORTH);
        top.add(actionRow, BorderLayout.CENTER);
        return top;
    }

    // ===================== TABLE PANEL =====================
    private JScrollPane buildTablePanel() {
        String[] cols = {"Mã NCC", "Tên nhà cung cấp", "SĐT", "Email", "Chứng nhận CL", "Trạng thái HT", "Thao tác"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c == 6; }
            @Override public Class<?> getColumnClass(int c) { return c == 5 ? Integer.class : String.class; }
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

        int[] widths = {80, 200, 120, 160, 130, 120, 140};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        // Renderer trạng thái hợp tác (dùng StatusBadgeRenderer với label tùy chỉnh)
        table.getColumnModel().getColumn(5).setCellRenderer(new HopTacBadgeRenderer());
        table.getColumnModel().getColumn(6).setCellRenderer(new ActionCellRenderer());

        // ActionCellEditor: Sửa + Ngừng/Khôi phục hợp tác
        table.getColumnModel().getColumn(6).setCellEditor(new ActionCellEditor(e -> {
            int row = table.getSelectedRow();
            if (row == -1) return;
            String maNCC = tableModel.getValueAt(row, 0).toString();
            String cmd   = e.getActionCommand();

            if ("EDIT".equals(cmd)) {
                showForm(maNCC);

            } else if ("DELETE".equals(cmd)) {
                // Nút "Xóa" dùng để Ngừng/Khôi phục hợp tác
                NhaCungCapDTO ncc = bus.getById(maNCC);
                if (ncc == null) return;

                int trangThai = ncc.getTrangThaiHopTac();
                String msg = trangThai == 1
                        ? "Bạn có chắc muốn NGỪNG HỢP TÁC với " + ncc.getTenNCC() + "?"
                        : "Bạn có chắc muốn KHÔI PHỤC HỢP TÁC với " + ncc.getTenNCC() + "?";
                String ttl = trangThai == 1 ? "Ngừng hợp tác" : "Khôi phục hợp tác";

                int confirm = JOptionPane.showConfirmDialog(this, msg, ttl,
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (confirm != JOptionPane.YES_OPTION) return;

                try {
                    if (trangThai == 1) bus.ngungHopTac(maNCC);
                    else                bus.khoiPhucHopTac(maNCC);
                    loadData();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this,
                            "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        }));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(AppColor.BORDER));
        scroll.getViewport().setBackground(AppColor.SURFACE);
        return scroll;
    }

    // ===================== DIALOG =====================
    private void showForm(String maNCC) {
        NhaCungCapForm form = new NhaCungCapForm(maNCC);
        String title = (maNCC == null) ? "Thêm mới Nhà cung cấp" : "Cập nhật Nhà cung cấp";

        SimpleModalBorder modal = new SimpleModalBorder(
                form, title, SimpleModalBorder.YES_NO_OPTION,
                (ctrl, action) -> {
                    if (action == SimpleModalBorder.YES_OPTION) {
                        if (form.saveData()) {
                            ctrl.close();
                            loadData();
                        }
                    } else {
                        ctrl.close();
                    }
                });
        ModalDialog.showModal(this, modal);
    }

    // ===================== LOAD DỮ LIỆU =====================
    public void loadData() {
        loadData(bus.getAll());
    }

    private void loadData(List<NhaCungCapDTO> list) {
        tableModel.setRowCount(0);
        for (NhaCungCapDTO ncc : list) {
            tableModel.addRow(new Object[]{
                ncc.getMaNCC(), ncc.getTenNCC(), ncc.getSdt(),
                ncc.getEmail(), ncc.getChungNhanCL(),
                ncc.getTrangThaiHopTac(), ""
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

    // Badge renderer riêng cho trạng thái hợp tác (1: Đang HT, 0: Ngừng HT)
    static class HopTacBadgeRenderer extends JLabel implements javax.swing.table.TableCellRenderer {
        HopTacBadgeRenderer() {
            setOpaque(true);
            setHorizontalAlignment(CENTER);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
        }

        @Override
        public Component getTableCellRendererComponent(JTable t, Object v,
                boolean sel, boolean foc, int r, int c) {
            if (v instanceof Integer) {
                if ((Integer) v == 1) {
                    setText("Đang hợp tác");
                    setBackground(new Color(230, 248, 238));
                    setForeground(new Color(0, 137, 84));
                } else {
                    setText("Ngừng hợp tác");
                    setBackground(new Color(255, 235, 235));
                    setForeground(new Color(180, 30, 30));
                }
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