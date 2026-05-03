package gui;

import bus.LoaiSanPhamBUS;
import dto.LoaiSanPhamDTO;
import util.AppColor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class LoaiSanPhamGUI extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private LoaiSanPhamBUS bus;

    public LoaiSanPhamGUI() {
        bus = new LoaiSanPhamBUS();
        initComponents();
        loadDataToTable();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(AppColor.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // HEADER: Tiêu đề và Thanh công cụ
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(AppColor.BACKGROUND);

        JLabel lblTitle = new JLabel("QUẢN LÝ LOẠI SẢN PHẨM");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setForeground(AppColor.TEXT_PRIMARY);
        headerPanel.add(lblTitle, BorderLayout.WEST);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setBackground(AppColor.BACKGROUND);

        JButton btnAdd = createButton("+ Thêm Mới", AppColor.SUCCESS, Color.WHITE);
        JButton btnEdit = createButton("Sửa", AppColor.INFO, Color.WHITE);
        JButton btnDelete = createButton("Xóa", AppColor.ERROR, Color.WHITE);
        JButton btnRefresh = createButton("Làm Mới", AppColor.TEXT_SECONDARY, Color.WHITE);

        btnAdd.addActionListener(e -> showFormDialog(null));
        btnEdit.addActionListener(e -> editAction());
        btnDelete.addActionListener(e -> deleteAction());
        btnRefresh.addActionListener(e -> loadDataToTable());

        actionPanel.add(btnAdd);
        actionPanel.add(btnEdit);
        actionPanel.add(btnDelete);
        actionPanel.add(btnRefresh);

        headerPanel.add(actionPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // CENTER: Bảng dữ liệu
        tableModel = new DefaultTableModel(new String[]{"Mã Loại SP", "Tên Loại SP", "Mô Tả"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(30);
        table.setSelectionBackground(AppColor.SECONDARY_HOVER);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(AppColor.SURFACE);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JButton createButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        return btn;
    }

    private void loadDataToTable() {
        tableModel.setRowCount(0);
        try {
            List<LoaiSanPhamDTO> list = bus.layDanhSachLSP();
            for (LoaiSanPhamDTO lsp : list) {
                tableModel.addRow(new Object[]{lsp.getMaLSP(), lsp.getTenLSP(), lsp.getMoTa()});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editAction() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn loại sản phẩm cần sửa!");
            return;
        }
        String maLSP = tableModel.getValueAt(selectedRow, 0).toString();
        String tenLSP = tableModel.getValueAt(selectedRow, 1).toString();
        String moTa = tableModel.getValueAt(selectedRow, 2) != null ? tableModel.getValueAt(selectedRow, 2).toString() : "";
        
        LoaiSanPhamDTO dto = new LoaiSanPhamDTO(maLSP, tenLSP, moTa);
        showFormDialog(dto);
    }

    private void deleteAction() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn loại sản phẩm cần xóa!");
            return;
        }
        String maLSP = tableModel.getValueAt(selectedRow, 0).toString();
        
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa " + maLSP + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (bus.xoaLSP(maLSP)) {
                    JOptionPane.showMessageDialog(this, "Xóa thành công!");
                    loadDataToTable();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Hiển thị Dialog dùng chung cho Thêm & Sửa
    private void showFormDialog(LoaiSanPhamDTO dto) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), dto == null ? "Thêm Loại Sản Phẩm" : "Sửa Loại Sản Phẩm", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("Tên Loại SP (*):"));
        JTextField txtTen = new JTextField(dto != null ? dto.getTenLSP() : "");
        panel.add(txtTen);

        panel.add(new JLabel("Mô Tả:"));
        JTextArea txtMoTa = new JTextArea(dto != null ? dto.getMoTa() : "");
        panel.add(new JScrollPane(txtMoTa));

        dialog.add(panel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        JButton btnSave = createButton("Lưu", AppColor.PRIMARY, Color.WHITE);
        JButton btnCancel = createButton("Hủy", AppColor.ERROR, Color.WHITE);

        btnCancel.addActionListener(e -> dialog.dispose());
        btnSave.addActionListener(e -> {
            try {
                LoaiSanPhamDTO newDto = new LoaiSanPhamDTO();
                newDto.setTenLSP(txtTen.getText());
                newDto.setMoTa(txtMoTa.getText());

                if (dto == null) {
                    bus.themLSP(newDto);
                    JOptionPane.showMessageDialog(dialog, "Thêm thành công!");
                } else {
                    newDto.setMaLSP(dto.getMaLSP());
                    bus.capNhatLSP(newDto);
                    JOptionPane.showMessageDialog(dialog, "Cập nhật thành công!");
                }
                dialog.dispose();
                loadDataToTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPanel.add(btnSave);
        btnPanel.add(btnCancel);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }
}