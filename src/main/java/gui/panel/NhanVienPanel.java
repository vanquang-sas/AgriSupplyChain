package gui.panel;

import bus.NhanVienBUS;
import dto.NhanVienDTO;
import gui.component.IntegratedSearch;
import gui.component.Pagination;
import gui.component.ActionCellEditor;
import gui.component.ActionCellRenderer;
import gui.component.StatusBadgeRenderer;
import gui.dialog.NhanVienForm;
import raven.modal.ModalDialog;
import raven.modal.component.SimpleModalBorder;
import util.AppColor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class NhanVienPanel extends JPanel {

    private final NhanVienBUS bus = new NhanVienBUS();
    private JTable table;
    private DefaultTableModel tableModel;
    private IntegratedSearch searchPanel;

    public NhanVienPanel() {
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
        JLabel title = new JLabel("Quản lý Nhân viên");
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
        String[] cols = {"Mã NV", "Tên nhân viên", "Chức vụ", "SĐT", "Lương (VNĐ)", "Trạng thái", "Thao tác"};
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

        int[] widths = {80, 180, 120, 120, 140, 110, 140};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        table.getColumnModel().getColumn(5).setCellRenderer(new StatusBadgeRenderer());
        table.getColumnModel().getColumn(6).setCellRenderer(new ActionCellRenderer());

        // ✅ SỬA LỖI: Truyền ActionListener đúng kiểu
        table.getColumnModel().getColumn(6).setCellEditor(new ActionCellEditor(e -> {
            int row = table.getSelectedRow();
            if (row == -1) return;
            String maNV = tableModel.getValueAt(row, 0).toString();
            String cmd  = e.getActionCommand();

            if ("EDIT".equals(cmd)) {
                showForm(maNV);

            } else if ("DELETE".equals(cmd)) {
                // Khóa / Mở khóa tài khoản nhân viên
                NhanVienDTO nv = bus.getById(maNV);
                if (nv == null) return;

                int trangThai = nv.getTrangThaiTK();
                String msg  = trangThai == 1
                        ? "Bạn có chắc muốn KHÓA tài khoản của " + nv.getTenNV() + "?"
                        : "Bạn có chắc muốn MỞ KHÓA tài khoản của " + nv.getTenNV() + "?";
                String ttl  = trangThai == 1 ? "Khóa tài khoản" : "Mở khóa tài khoản";

                int confirm = JOptionPane.showConfirmDialog(this, msg, ttl, JOptionPane.YES_NO_OPTION);
                if (confirm != JOptionPane.YES_OPTION) return;

                try {
                    if (trangThai == 1) bus.khoaTaiKhoan(nv.getUsername());
                    else                bus.moKhoaTaiKhoan(nv.getUsername());
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
    private void showForm(String maNV) {
        NhanVienForm form = new NhanVienForm(maNV);
        String title = (maNV == null) ? "Thêm mới Nhân viên" : "Cập nhật Nhân viên";

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

    private void loadData(List<NhanVienDTO> list) {
        tableModel.setRowCount(0);
        NumberFormat fmt = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        for (NhanVienDTO nv : list) {
            tableModel.addRow(new Object[]{
                nv.getMaNV(), nv.getTenNV(), nv.getChucVu(), nv.getSdt(),
                fmt.format(nv.getLuong()),      // Hiển thị lương có dấu phẩy: 10,000,000
                nv.getTrangThaiTK(), ""
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
}