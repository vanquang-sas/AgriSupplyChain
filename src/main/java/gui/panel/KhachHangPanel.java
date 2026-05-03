package gui.panel;

import bus.KhachHangBUS;
import dto.KhachHangDTO;
import gui.component.IntegratedSearch;
import gui.component.Pagination;
import gui.component.ActionCellEditor;
import gui.component.ActionCellRenderer;
import gui.component.StatusBadgeRenderer;
import gui.dialog.KhachHangForm;
import raven.modal.ModalDialog;
import raven.modal.component.SimpleModalBorder;
import util.AppColor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class KhachHangPanel extends JPanel {

    private final KhachHangBUS bus = new KhachHangBUS();
    private JTable table;
    private DefaultTableModel tableModel;
    private IntegratedSearch searchPanel;

    public KhachHangPanel() {
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
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(AppColor.SURFACE);

        // Tiêu đề
        JLabel title = new JLabel("Quản lý Khách hàng");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(AppColor.TEXT_PRIMARY);

        // Nút bên trái
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        left.setBackground(AppColor.SURFACE);

        JButton btnAdd = createButton("+ Thêm mới", AppColor.PRIMARY, Color.WHITE);
        btnAdd.addActionListener(e -> showForm(null));

        left.add(btnAdd);

        // Thanh tìm kiếm bên phải
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

        // Tìm kiếm khi nhấn Enter
        searchPanel.addActionListener(e -> btnSearch.doClick());

        right.add(searchPanel);
        right.add(btnSearch);

        // Ghép lại
        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setBackground(AppColor.SURFACE);
        titleRow.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        titleRow.add(title, BorderLayout.WEST);

        JPanel actionRow = new JPanel(new BorderLayout());
        actionRow.setBackground(AppColor.SURFACE);
        actionRow.add(left, BorderLayout.WEST);
        actionRow.add(right, BorderLayout.EAST);

        JPanel top2 = new JPanel(new BorderLayout());
        top2.setBackground(AppColor.SURFACE);
        top2.add(titleRow, BorderLayout.NORTH);
        top2.add(actionRow, BorderLayout.CENTER);

        return top2;
    }

    // ===================== TABLE PANEL =====================
    private JScrollPane buildTablePanel() {
        String[] cols = {"Mã KH", "Tên khách hàng", "SĐT", "Email", "Loại KH", "Trạng thái", "Thao tác"};
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

        // Header
        table.getTableHeader().setBackground(AppColor.PRIMARY);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setPreferredSize(new Dimension(0, 44));
        table.getTableHeader().setReorderingAllowed(false);

        // Độ rộng cột
        int[] widths = {80, 180, 120, 180, 90, 110, 140};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        // Renderer
        table.getColumnModel().getColumn(5).setCellRenderer(new StatusBadgeRenderer());
        table.getColumnModel().getColumn(6).setCellRenderer(new ActionCellRenderer());

        // ✅ SỬA LỖI: Truyền ActionListener (lambda) thay vì ActionEvent
        table.getColumnModel().getColumn(6).setCellEditor(new ActionCellEditor(e -> {
            int row = table.getSelectedRow();
            if (row == -1) return;
            String maKH  = tableModel.getValueAt(row, 0).toString();
            String cmd   = e.getActionCommand();

            if ("EDIT".equals(cmd)) {
                showForm(maKH);

            } else if ("DELETE".equals(cmd)) {
                // Nút "Xóa" ở đây dùng để Khóa/Mở khóa tài khoản
                KhachHangDTO kh = bus.getById(maKH);
                if (kh == null) return;

                int trangThai = kh.getTrangThaiTK();
                String msg    = trangThai == 1
                        ? "Bạn có chắc muốn KHÓA tài khoản của " + kh.getTenKH() + "?"
                        : "Bạn có chắc muốn MỞ KHÓA tài khoản của " + kh.getTenKH() + "?";
                String title  = trangThai == 1 ? "Khóa tài khoản" : "Mở khóa tài khoản";

                int confirm = JOptionPane.showConfirmDialog(this, msg, title, JOptionPane.YES_NO_OPTION);
                if (confirm != JOptionPane.YES_OPTION) return;

                try {
                    if (trangThai == 1) bus.khoaTaiKhoan(kh.getUsername());
                    else                bus.moKhoaTaiKhoan(kh.getUsername());
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
    private void showForm(String maKH) {
        KhachHangForm form  = new KhachHangForm(maKH);
        String title        = (maKH == null) ? "Thêm mới Khách hàng" : "Cập nhật Khách hàng";

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

    private void loadData(List<KhachHangDTO> list) {
        tableModel.setRowCount(0);
        for (KhachHangDTO kh : list) {
            tableModel.addRow(new Object[]{
                kh.getMaKH(), kh.getTenKH(), kh.getSdt(),
                kh.getEmail(), kh.getLoaiKH(), kh.getTrangThaiTK(), ""
            });
        }
    }

    // ===================== HELPER =====================
    private JButton createButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        return btn;
    }
}