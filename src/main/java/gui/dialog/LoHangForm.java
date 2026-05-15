package gui.dialog;

import bus.LoHangBUS;
import bus.NhaCungCapBUS;
import bus.SanPhamBUS;
import dto.ChiTietLoHangDTO;
import dto.NhaCungCapDTO;
import dto.SanPhamDTO;
import gui.component.RoundedButton;
import util.AppColor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class LoHangForm extends JDialog {

    private final LoHangBUS loHangBUS = new LoHangBUS();
    private final SanPhamBUS sanPhamBUS = new SanPhamBUS();
    private final NhaCungCapBUS nccBUS = new NhaCungCapBUS();

    private JComboBox<NhaCungCapDTO> cbNCC;
    private JTable tblProducts;
    private JTable tblOrder;
    private DefaultTableModel productModel;
    private DefaultTableModel orderModel;

    private JTextField txtTenSP;
    private JTextField txtGiaMua;
    private JTextField txtSoLuong;
    private JLabel lblTotal;

    private final List<ChiTietLoHangDTO> orderItems = new ArrayList<>();
    private SanPhamDTO selectedProduct;

    public LoHangForm(Window parent) {
        super(parent, "Tạo lô hàng nhập mới", ModalityType.APPLICATION_MODAL);
        setSize(1000, 680);
        setLocationRelativeTo(parent);
        initComponents();
    }

    private void initComponents() {
        JPanel main = new JPanel(new BorderLayout(20, 20));
        main.setBorder(new EmptyBorder(20, 20, 20, 20));
        main.setBackground(AppColor.BACKGROUND);
        setContentPane(main);

        JLabel title = new JLabel("Tạo lô hàng nhập");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(AppColor.TEXT_PRIMARY);
        main.add(title, BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(18, 18));
        content.setOpaque(false);
        main.add(content, BorderLayout.CENTER);

        JPanel infoCard = new JPanel(new BorderLayout());
        infoCard.setOpaque(true);
        infoCard.setBackground(AppColor.SURFACE);
        infoCard.setBorder(new EmptyBorder(18, 18, 18, 18));
        infoCard.add(buildInfoPanel(), BorderLayout.CENTER);

        content.add(infoCard, BorderLayout.NORTH);
        content.add(buildTablesPanel(), BorderLayout.CENTER);
        content.add(buildActionPanel(), BorderLayout.SOUTH);

        loadSupplierList();
        loadProductList();
    }

    private JPanel buildInfoPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 24, 0));
        panel.setOpaque(false);

        JPanel left = new JPanel(new GridLayout(3, 1, 12, 12));
        left.setOpaque(false);
        left.add(createInputGroup("Nhà cung cấp (*)", buildSupplierCombo()));
        left.add(createInputGroup("Tên sản phẩm", txtTenSP = createReadonlyField()));
        left.add(createInputGroup("Giá mua (VNĐ)", txtGiaMua = createReadonlyField()));

        JPanel right = new JPanel(new GridLayout(2, 1, 12, 12));
        right.setOpaque(false);
        right.add(createInputGroup("Số lượng nhập (*)", txtSoLuong = createTextField("1")));
        right.add(buildButtonRow());

        panel.add(left);
        panel.add(right);
        return panel;
    }

    private JPanel buildTablesPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 20, 0));
        panel.setOpaque(false);

        JPanel productCard = new JPanel(new BorderLayout(12, 12));
        productCard.setBackground(AppColor.SURFACE);
        productCard.setBorder(new EmptyBorder(16, 16, 16, 16));
        productCard.add(new JLabel("Danh sách sản phẩm"), BorderLayout.NORTH);
        productCard.add(new JScrollPane(buildProductTable()), BorderLayout.CENTER);

        JPanel orderCard = new JPanel(new BorderLayout(12, 12));
        orderCard.setBackground(AppColor.SURFACE);
        orderCard.setBorder(new EmptyBorder(16, 16, 16, 16));
        orderCard.add(new JLabel("Chi tiết lô hàng"), BorderLayout.NORTH);
        orderCard.add(new JScrollPane(buildOrderTable()), BorderLayout.CENTER);
        orderCard.add(buildOrderFooter(), BorderLayout.SOUTH);

        panel.add(productCard);
        panel.add(orderCard);
        return panel;
    }

    private JPanel buildActionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        panel.setOpaque(false);

        RoundedButton btnCreate = new RoundedButton("Nhập hàng", AppColor.PRIMARY);
        RoundedButton btnClose = new RoundedButton("Hủy", new Color(107, 114, 128));

        btnCreate.addActionListener(e -> onCreateOrder());
        btnClose.addActionListener(e -> dispose());

        panel.add(btnClose);
        panel.add(btnCreate);
        return panel;
    }

    private JPanel buildButtonRow() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        row.setOpaque(false);

        RoundedButton btnAdd = new RoundedButton("Thêm vào lô", AppColor.INFO);
        RoundedButton btnRemove = new RoundedButton("Xóa dòng", new Color(220, 38, 38));

        btnAdd.addActionListener(e -> addSelectedProduct());
        btnRemove.addActionListener(e -> removeSelectedOrderLine());

        row.add(btnAdd);
        row.add(btnRemove);
        return row;
    }

    private JPanel buildOrderFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        lblTotal = new JLabel("Tổng tiền: 0 VNĐ");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTotal.setForeground(AppColor.TEXT_PRIMARY);
        footer.add(lblTotal, BorderLayout.EAST);
        return footer;
    }

    private JScrollPane buildProductTable() {
        productModel = new DefaultTableModel(new Object[]{"Mã SP", "Tên SP", "Loại", "Giá mua", "Đơn vị"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tblProducts = new JTable(productModel);
        tblProducts.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblProducts.setRowHeight(38);
        tblProducts.setShowVerticalLines(false);
        tblProducts.setShowHorizontalLines(true);
        tblProducts.setGridColor(new Color(229, 231, 235));
        tblProducts.setBackground(Color.WHITE);
        tblProducts.setSelectionBackground(new Color(220, 252, 231));
        tblProducts.setSelectionForeground(AppColor.TEXT_PRIMARY);
        tblProducts.setFocusable(false);
        tblProducts.setIntercellSpacing(new Dimension(0, 0));

        JTableHeader productHeader = tblProducts.getTableHeader();
        productHeader.setPreferredSize(new Dimension(0, 46));
        productHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(209, 213, 219)));

        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        headerRenderer.setFont(new Font("Segoe UI", Font.BOLD, 14));
        headerRenderer.setBackground(new Color(243, 244, 246));
        headerRenderer.setBorder(new EmptyBorder(0, 16, 0, 0));
        for (int i = 0; i < tblProducts.getColumnCount(); i++) {
            tblProducts.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }

        tblProducts.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (tblProducts.getSelectedRow() >= 0) {
                    int row = tblProducts.getSelectedRow();
                    String maSP = productModel.getValueAt(row, 0).toString();
                    String tenSP = productModel.getValueAt(row, 1).toString();
                    String giaMua = productModel.getValueAt(row, 3).toString();
                    txtTenSP.setText(tenSP);
                    txtGiaMua.setText(giaMua);
                    selectedProduct = findProductByMaSP(maSP);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tblProducts);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);
        return scroll;
    }

    private JScrollPane buildOrderTable() {
        orderModel = new DefaultTableModel(new Object[]{"Mã SP", "Tên SP", "Giá mua", "Số lượng", "Thành tiền"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tblOrder = new JTable(orderModel);
        tblOrder.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblOrder.setRowHeight(36);
        tblOrder.setShowVerticalLines(false);
        tblOrder.setShowHorizontalLines(true);
        tblOrder.setGridColor(new Color(229, 231, 235));
        tblOrder.setBackground(Color.WHITE);
        tblOrder.setSelectionBackground(new Color(220, 252, 231));
        tblOrder.setSelectionForeground(AppColor.TEXT_PRIMARY);
        tblOrder.setFocusable(false);
        tblOrder.setIntercellSpacing(new Dimension(0, 0));

        JTableHeader orderHeader = tblOrder.getTableHeader();
        orderHeader.setPreferredSize(new Dimension(0, 46));
        orderHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(209, 213, 219)));

        DefaultTableCellRenderer orderHeaderRenderer = new DefaultTableCellRenderer();
        orderHeaderRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        orderHeaderRenderer.setFont(new Font("Segoe UI", Font.BOLD, 14));
        orderHeaderRenderer.setBackground(new Color(243, 244, 246));
        orderHeaderRenderer.setBorder(new EmptyBorder(0, 16, 0, 0));
        for (int i = 0; i < tblOrder.getColumnCount(); i++) {
            tblOrder.getColumnModel().getColumn(i).setHeaderRenderer(orderHeaderRenderer);
        }

        JScrollPane scroll = new JScrollPane(tblOrder);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);
        return scroll;
    }

    private Component buildSupplierCombo() {
        cbNCC = new JComboBox<>();
        cbNCC.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbNCC.setBackground(Color.WHITE);
        cbNCC.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof NhaCungCapDTO) {
                    setText(((NhaCungCapDTO) value).getTenNCC());
                }
                return this;
            }
        });
        return cbNCC;
    }

    private JTextField createTextField(String placeholder) {
        JTextField field = new JTextField(placeholder);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(200, 36));
        return field;
    }

    private JTextField createReadonlyField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(200, 36));
        field.setEditable(false);
        field.setBackground(new Color(249, 250, 251));
        return field;
    }

    private JPanel createInputGroup(String labelText, Component inputComp) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(AppColor.TEXT_SECONDARY);
        lbl.setBorder(new EmptyBorder(0, 0, 8, 0));
        panel.add(lbl);
        panel.add(inputComp);
        return panel;
    }

    private void loadSupplierList() {
        cbNCC.removeAllItems();
        for (NhaCungCapDTO ncc : nccBUS.getAll()) {
            if (ncc.getTrangThaiHopTac() == 1) {
                cbNCC.addItem(ncc);
            }
        }
        if (cbNCC.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy nhà cung cấp hợp tác nào.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void loadProductList() {
        productModel.setRowCount(0);
        for (SanPhamDTO sp : sanPhamBUS.getAll()) {
            productModel.addRow(new Object[]{
                    sp.getMaSP(),
                    sp.getTenSP(),
                    sp.getTenLSP(),
                    String.format("%,.0f", sp.getGiaMua()),
                    sp.getDonViTinh()
            });
        }
    }

    private SanPhamDTO findProductByMaSP(String maSP) {
        for (SanPhamDTO sp : sanPhamBUS.getAll()) {
            if (maSP.equals(sp.getMaSP())) {
                return sp;
            }
        }
        return null;
    }

    private void addSelectedProduct() {
        if (selectedProduct == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm trước khi thêm.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        double quantity;
        try {
            quantity = Double.parseDouble(txtSoLuong.getText().trim());
            if (quantity <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Số lượng phải là số lớn hơn 0.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean updated = false;
        for (ChiTietLoHangDTO item : orderItems) {
            if (item.getMaSP().equals(selectedProduct.getMaSP())) {
                item.setSoLuong(item.getSoLuong() + quantity);
                item.setThanhTien(item.getSoLuong() * selectedProduct.getGiaMua());
                updated = true;
                break;
            }
        }
        if (!updated) {
            orderItems.add(new ChiTietLoHangDTO(null, null, selectedProduct.getMaSP(), selectedProduct.getGiaMua(), quantity, quantity * selectedProduct.getGiaMua()));
        }
        refreshOrderTable();
    }

    private void removeSelectedOrderLine() {
        int row = tblOrder.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn dòng cần xóa.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String maSP = orderModel.getValueAt(row, 0).toString();
        orderItems.removeIf(item -> item.getMaSP().equals(maSP));
        refreshOrderTable();
    }

    private void refreshOrderTable() {
        orderModel.setRowCount(0);
        double total = 0;
        DecimalFormat df = new DecimalFormat("#,###");
        for (ChiTietLoHangDTO item : orderItems) {
            double thanhTien = item.getThanhTien();
            orderModel.addRow(new Object[]{
                    item.getMaSP(),
                    findProductByMaSP(item.getMaSP()).getTenSP(),
                    df.format(item.getGiaMua()),
                    item.getSoLuong(),
                    df.format(thanhTien)
            });
            total += thanhTien;
        }
        lblTotal.setText("Tổng tiền: " + df.format(total) + " VNĐ");
    }

    private void onCreateOrder() {
        NhaCungCapDTO supplier = (NhaCungCapDTO) cbNCC.getSelectedItem();
        if (supplier == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn Nhà cung cấp.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (orderItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng thêm ít nhất một sản phẩm vào lô hàng.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            boolean success = loHangBUS.createLoHang(supplier.getMaNCC(), orderItems);
            if (success) {
                JOptionPane.showMessageDialog(this, "Tạo lô hàng thành công. Lô hàng đang chờ nhập kho.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Tạo lô hàng thất bại. Vui lòng thử lại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tạo lô hàng: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
