package gui.dialog;

import bus.LoaiSanPhamBUS;
import dto.LoaiSanPhamDTO;
import util.AppColor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoaiSanPhamForm extends JPanel {
    private final LoaiSanPhamBUS bus = new LoaiSanPhamBUS();
    private final String maLSP; 
    private LoaiSanPhamDTO currentLSP;

    private ModernTextField txtTenLSP;
    private ModernTextField txtMoTa;

    public LoaiSanPhamForm(String maLSP) {
        this.maLSP = maLSP;
        initComponents();
        if (maLSP != null) {
            loadDataForEdit();
        }
    }

    private void initComponents() {
        setBackground(AppColor.SURFACE);
        setBorder(new EmptyBorder(24, 32, 24, 32));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JLabel title = new JLabel(maLSP == null ? "Thêm Loại Sản Phẩm" : "Cập Nhật Loại Sản Phẩm");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(AppColor.TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(title);
        add(Box.createVerticalStrut(24));

        JPanel formGrid = new JPanel(new GridLayout(0, 1, 20, 20)); // Dùng 1 cột cho rộng rãi
        formGrid.setOpaque(false);
        formGrid.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtTenLSP = new ModernTextField("VD: Trái cây nhiệt đới");
        txtMoTa = new ModernTextField("VD: Trái cây được trồng ở vùng nhiệt đới");

        formGrid.add(createInputGroup("Tên loại sản phẩm (*)", txtTenLSP));
        formGrid.add(createInputGroup("Mô tả", txtMoTa));

        add(formGrid);
        setPreferredSize(new Dimension(450, 300));
    }

    private JPanel createInputGroup(String labelText, JComponent inputComp) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(AppColor.TEXT_SECONDARY);
        lbl.setBorder(new EmptyBorder(0, 0, 8, 0));

        inputComp.setPreferredSize(new Dimension(380, 40));
        inputComp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        panel.add(lbl);
        panel.add(inputComp);
        return panel;
    }

    private void loadDataForEdit() {
        for (LoaiSanPhamDTO lsp : bus.getAll()) {
            if (lsp.getMaLSP().equals(maLSP)) {
                currentLSP = lsp;
                break;
            }
        }
        if (currentLSP != null) {
            txtTenLSP.setText(currentLSP.getTenLSP());
            txtMoTa.setText(currentLSP.getMoTa());
        }
    }

    public boolean saveData() {
        if (txtTenLSP.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Tên loại sản phẩm (*)", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        try {
            if (maLSP == null) {
                LoaiSanPhamDTO newLSP = new LoaiSanPhamDTO("", txtTenLSP.getText().trim(), txtMoTa.getText().trim());
                bus.add(newLSP);
                JOptionPane.showMessageDialog(this, "Thêm loại sản phẩm thành công!");
            } else {
                currentLSP.setTenLSP(txtTenLSP.getText().trim());
                currentLSP.setMoTa(txtMoTa.getText().trim());
                bus.update(currentLSP);
                JOptionPane.showMessageDialog(this, "Cập nhật loại sản phẩm thành công!");
            }
            return true;
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi ràng buộc", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi dữ liệu: " + ex.getMessage(), "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    // Giao diện ô nhập text bo góc
    static class ModernTextField extends JTextField {
        private final String placeholder;
        public ModernTextField(String placeholder) {
            this.placeholder = placeholder;
            setOpaque(false); setBorder(new EmptyBorder(0, 16, 0, 16));
            setFont(new Font("Segoe UI", Font.PLAIN, 14)); setForeground(AppColor.TEXT_PRIMARY);
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE); g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
            g2.setColor(isFocusOwner() ? AppColor.PRIMARY : new Color(209, 213, 219));
            if (isFocusOwner()) g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 8, 8);
            super.paintComponent(g);
            if (getText().isEmpty() && !isFocusOwner()) {
                g2.setColor(new Color(156, 163, 175));
                FontMetrics fm = g.getFontMetrics();
                g2.drawString(placeholder, 16, (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
            }
            g2.dispose();
        }
    }
}