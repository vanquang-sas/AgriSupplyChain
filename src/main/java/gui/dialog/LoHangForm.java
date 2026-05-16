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
import java.awt.event.MouseMotionAdapter;
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
        setSize(1100, 720); 
        setLocationRelativeTo(parent);
        initComponents();
    }

    private void initComponents() {
        JPanel main = new JPanel(new BorderLayout(20, 20));
        main.setBorder(new EmptyBorder(20, 24, 20, 24));
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
        infoCard.setBorder(new EmptyBorder(18, 20, 18, 20));
        infoCard.add(buildInfoPanel(), BorderLayout.CENTER);

        content.add(infoCard, BorderLayout.NORTH);
        content.add(buildTablesPanel(), BorderLayout.CENTER);
        content.add(buildActionPanel(), BorderLayout.SOUTH);

        loadSupplierList();
        loadProductList();
    }

    private JPanel buildInfoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        // 1. Dòng 1: Thông tin chung (Nhà cung cấp)
        JPanel topInfo = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        topInfo.setOpaque(false);
        cbNCC = (JComboBox<NhaCungCapDTO>) buildSupplierCombo();
        cbNCC.setPreferredSize(new Dimension(350, 38));
        topInfo.add(createInputGroup("Chọn Nhà Cung Cấp (*):", cbNCC));
        
        panel.add(topInfo);
        panel.add(Box.createVerticalStrut(16));
        panel.add(new JSeparator());
        panel.add(Box.createVerticalStrut(16));

        // 2. Dòng 2: Hướng dẫn
        JPanel instructPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        instructPanel.setOpaque(false);
        JLabel lblInstruct = new JLabel("💡 Hướng dẫn: Click chọn sản phẩm ở Bảng Danh sách (bên trái) để điền thông tin nhập hàng.");
        lblInstruct.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lblInstruct.setForeground(new Color(0x2563EB)); 
        instructPanel.add(lblInstruct);
        panel.add(instructPanel);
        panel.add(Box.createVerticalStrut(12));

        // 3. Dòng 3: Chi tiết thao tác nhập item
        JPanel itemInputRow = new JPanel(new GridBagLayout());
        itemInputRow.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 16);
        gbc.weightx = 0.3;

        txtTenSP = createReadonlyField();
        txtTenSP.setText("--- Vui lòng chọn sản phẩm ---"); 
        txtTenSP.setForeground(new Color(156, 163, 175)); 
        
        txtGiaMua = createReadonlyField();
        txtSoLuong = createTextField("1");

        gbc.gridx = 0; itemInputRow.add(createInputGroup("Tên sản phẩm:", txtTenSP), gbc);
        gbc.weightx = 0.2;
        gbc.gridx = 1; itemInputRow.add(createInputGroup("Giá mua (VNĐ):", txtGiaMua), gbc);
        gbc.weightx = 0.1;
        gbc.gridx = 2; itemInputRow.add(createInputGroup("SL Nhập:", txtSoLuong), gbc);
        
        gbc.weightx = 0;
        gbc.gridx = 3; 
        gbc.insets = new Insets(24, 0, 0, 0); 
        itemInputRow.add(buildButtonRow(), gbc);

        panel.add(itemInputRow);
        
        return panel;
    }

    private JPanel buildTablesPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 20, 0));
        panel.setOpaque(false);

        // Bảng 1: Danh sách SP
        JPanel productCard = new JPanel(new BorderLayout(12, 12));
        productCard.setBackground(AppColor.SURFACE);
        productCard.setBorder(new EmptyBorder(16, 16, 16, 16));
        JLabel lblTitle1 = new JLabel("Danh sách Sản phẩm");
        lblTitle1.setFont(new Font("Segoe UI", Font.BOLD, 15));
        productCard.add(lblTitle1, BorderLayout.NORTH);
        productCard.add(new JScrollPane(buildProductTable()), BorderLayout.CENTER);

        // Bảng 2: Chi tiết lô hàng
        JPanel orderCard = new JPanel(new BorderLayout(12, 12));
        orderCard.setBackground(AppColor.SURFACE);
        orderCard.setBorder(new EmptyBorder(16, 16, 16, 16));
        JLabel lblTitle2 = new JLabel("Chi tiết Lô hàng (Sản phẩm đã chọn)");
        lblTitle2.setFont(new Font("Segoe UI", Font.BOLD, 15));
        orderCard.add(lblTitle2, BorderLayout.NORTH);
        orderCard.add(new JScrollPane(buildOrderTable()), BorderLayout.CENTER);
        orderCard.add(buildOrderFooter(), BorderLayout.SOUTH);

        panel.add(productCard);
        panel.add(orderCard);
        return panel;
    }

    private JPanel buildActionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        panel.setOpaque(false);

        // Đã sửa text button cho đúng luồng tạo lô hàng
        RoundedButton btnCreate = new RoundedButton("Tạo lô hàng", AppColor.PRIMARY);
        btnCreate.setPreferredSize(new Dimension(160, 42));
        
        RoundedButton btnClose = new RoundedButton("Hủy bỏ", new Color(107, 114, 128));
        btnClose.setPreferredSize(new Dimension(120, 42));

        btnCreate.addActionListener(e -> onCreateOrder());
        btnClose.addActionListener(e -> dispose());

        panel.add(btnClose);
        panel.add(btnCreate);
        return panel;
    }

    private JPanel buildButtonRow() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        row.setOpaque(false);

        RoundedButton btnAdd = new RoundedButton("Thêm >>", AppColor.SUCCESS);
        btnAdd.setPreferredSize(new Dimension(100, 36));
        
        RoundedButton btnRemove = new RoundedButton("Xóa dòng", new Color(220, 38, 38));
        btnRemove.setPreferredSize(new Dimension(100, 36));

        btnAdd.addActionListener(e -> addSelectedProduct());
        btnRemove.addActionListener(e -> removeSelectedOrderLine());

        row.add(btnAdd);
        row.add(btnRemove);
        return row;
    }

    private JPanel buildOrderFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(12, 0, 0, 0));
        
        lblTotal = new JLabel("Tổng tiền: 0 VNĐ");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTotal.setForeground(new Color(220, 38, 38)); 
        footer.add(lblTotal, BorderLayout.EAST);
        return footer;
    }

    private JScrollPane buildProductTable() {
        productModel = new DefaultTableModel(new Object[]{"Mã SP", "Tên SP", "Giá mua", "Đơn vị"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tblProducts = new JTable(productModel);
        setupTableStyle(tblProducts);

        tblProducts.getColumnModel().getColumn(0).setPreferredWidth(70);
        tblProducts.getColumnModel().getColumn(1).setPreferredWidth(180);
        tblProducts.getColumnModel().getColumn(2).setPreferredWidth(100);

        tblProducts.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (tblProducts.getSelectedRow() >= 0) {
                    int row = tblProducts.getSelectedRow();
                    String maSP = productModel.getValueAt(row, 0).toString();
                    String tenSP = productModel.getValueAt(row, 1).toString();
                    String giaMua = productModel.getValueAt(row, 2).toString();
                    
                    txtTenSP.setForeground(AppColor.TEXT_PRIMARY);
                    txtTenSP.setText(tenSP);
                    
                    txtGiaMua.setText(giaMua.replace(",", "")); 
                    txtSoLuong.setText("1");
                    selectedProduct = findProductByMaSP(maSP);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tblProducts);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(229, 231, 235)));
        scroll.getViewport().setBackground(Color.WHITE);
        return scroll;
    }

    private JScrollPane buildOrderTable() {
        orderModel = new DefaultTableModel(new Object[]{"Mã SP", "Tên SP", "Giá mua", "SL", "Thành tiền"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tblOrder = new JTable(orderModel);
        setupTableStyle(tblOrder);

        tblOrder.getColumnModel().getColumn(0).setPreferredWidth(60);
        tblOrder.getColumnModel().getColumn(1).setPreferredWidth(150);
        tblOrder.getColumnModel().getColumn(3).setPreferredWidth(40); 

        JScrollPane scroll = new JScrollPane(tblOrder);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(229, 231, 235)));
        scroll.getViewport().setBackground(Color.WHITE);
        return scroll;
    }

    private void setupTableStyle(JTable table) {
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(42); 
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(229, 231, 235));
        table.setBackground(Color.WHITE);
        table.setSelectionBackground(new Color(220, 252, 231));
        table.setSelectionForeground(AppColor.TEXT_PRIMARY);
        table.setFocusable(false);
        table.setIntercellSpacing(new Dimension(0, 0));

        JTableHeader header = table.getTableHeader();
        header.setPreferredSize(new Dimension(0, 46));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(209, 213, 219)));

        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean isS, boolean hasF, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, isS, hasF, r, c);
                comp.setFont(new Font("Segoe UI", Font.BOLD, 14));
                comp.setBackground(new Color(243, 244, 246));
                ((JLabel) comp).setBorder(new EmptyBorder(0, 16, 0, 8));
                return comp;
            }
        };
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }

        ZebraHoverRenderer zebra = new ZebraHoverRenderer(table);
        for(int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(zebra);
        }
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
                setBorder(new EmptyBorder(4, 8, 4, 8));
                return this;
            }
        });
        return cbNCC;
    }

    private JTextField createTextField(String placeholder) {
        JTextField field = new JTextField(placeholder);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(200, 38));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219)),
            BorderFactory.createEmptyBorder(0, 8, 0, 8)
        ));
        return field;
    }

    private JTextField createReadonlyField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(200, 38));
        field.setEditable(false);
        field.setBackground(new Color(243, 244, 246));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219)),
            BorderFactory.createEmptyBorder(0, 8, 0, 8)
        ));
        return field;
    }

    private JPanel createInputGroup(String labelText, Component inputComp) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(AppColor.TEXT_SECONDARY);
        lbl.setBorder(new EmptyBorder(0, 0, 6, 0));
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
        DecimalFormat df = new DecimalFormat("#,###");
        for (SanPhamDTO sp : sanPhamBUS.getAll()) {
            productModel.addRow(new Object[]{
                    sp.getMaSP(),
                    sp.getTenSP(),
                    df.format(sp.getGiaMua()),
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
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm ở bảng bên trái trước khi thêm.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
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
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng bên bảng Chi tiết lô hàng để xóa.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
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
            JOptionPane.showMessageDialog(this, "Vui lòng chọn Nhà cung cấp ở trên cùng.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (orderItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng thêm ít nhất một sản phẩm vào chi tiết lô hàng.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            boolean success = loHangBUS.createLoHang(supplier.getMaNCC(), orderItems);
            if (success) {
                // Đã sửa lại thông báo đúng với luồng "Chờ kiểm duyệt"
                JOptionPane.showMessageDialog(this, "Tạo lô hàng thành công. Trạng thái hiện tại: 'Chờ kiểm duyệt'.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Tạo lô hàng thất bại. Vui lòng thử lại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tạo lô hàng: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
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
        public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
            super.getTableCellRendererComponent(t, v, s, f, r, c);
            setBorder(new EmptyBorder(0, 16, 0, 8));
            setFont(new Font("Segoe UI", Font.PLAIN, 13));
            setForeground(AppColor.TEXT_PRIMARY);

            if (s) {
                setBackground(new Color(220, 252, 231));
            } else if (r == hoverRow) {
                setBackground(new Color(240, 253, 244));
            } else if (r % 2 == 0) {
                setBackground(Color.WHITE);
            } else {
                setBackground(new Color(250, 250, 250));
            }
            return this;
        }
    }
}