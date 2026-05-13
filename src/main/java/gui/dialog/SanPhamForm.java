package gui.dialog;

import bus.LoaiSanPhamBUS;
import bus.SanPhamBUS;
import dto.LoaiSanPhamDTO;
import dto.SanPhamDTO;
import util.AppColor;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class SanPhamForm extends JPanel {
    private final SanPhamBUS bus = new SanPhamBUS();
    private final LoaiSanPhamBUS loaiBUS = new LoaiSanPhamBUS();
    private final String maSP;
    private SanPhamDTO currentSP;

    private ModernTextField txtTenSP, txtGiaMua, txtGiaBan, txtDonViTinh;
    private JComboBox<LoaiSanPhamDTO> cbLoaiSP;
    private JComboBox<String> cbChatLuong, cbBaoQuan;

    // Các Component xử lý ảnh
    private JLabel lblImagePreview;
    private File selectedImageFile = null;
    private String currentImageName = "";

    public SanPhamForm(String maSP) {
        this.maSP = maSP;
        initComponents();
        if (maSP != null) {
            loadDataForEdit();
        }
    }

    private void initComponents() {
        setBackground(AppColor.SURFACE);
        setBorder(new EmptyBorder(24, 32, 24, 32));
        setLayout(new BorderLayout(20, 0));

        JLabel title = new JLabel(maSP == null ? "Thêm Sản Phẩm Mới" : "Cập Nhật Sản Phẩm");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(AppColor.TEXT_PRIMARY);
        title.setBorder(new EmptyBorder(0, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        // --- KHU VỰC NHẬP TEXT (Bên Trái) ---
        JPanel formGrid = new JPanel(new GridLayout(0, 2, 20, 20));
        formGrid.setOpaque(false);

        txtTenSP = new ModernTextField("VD: Cà phê ABC");
        txtGiaMua = new ModernTextField("VD: 50000");
        txtGiaBan = new ModernTextField("VD: 60000");
        txtDonViTinh = new ModernTextField("VD: Kg, Trái...");

        cbLoaiSP = new JComboBox<>();
        loadCategories();
        cbLoaiSP.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbLoaiSP.setBackground(Color.WHITE);

        cbChatLuong = new JComboBox<>(new String[]{"Loại 1", "Loại 2", "Loại 3"});
        cbChatLuong.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbChatLuong.setBackground(Color.WHITE);

        cbBaoQuan = new JComboBox<>(new String[]{"Mát", "Lạnh", "Đông"});
        cbBaoQuan.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbBaoQuan.setBackground(Color.WHITE);

        formGrid.add(createInputGroup("Tên sản phẩm (*)", txtTenSP));
        formGrid.add(createInputGroup("Loại sản phẩm (*)", cbLoaiSP));
        formGrid.add(createInputGroup("Chất lượng", cbChatLuong));
        formGrid.add(createInputGroup("Bảo quản", cbBaoQuan));
        formGrid.add(createInputGroup("Giá mua (VNĐ) (*)", txtGiaMua));
        formGrid.add(createInputGroup("Giá bán (VNĐ) (*)", txtGiaBan));
        formGrid.add(createInputGroup("Đơn vị tính (*)", txtDonViTinh));

        add(formGrid, BorderLayout.CENTER);

        // --- KHU VỰC TẢI ẢNH LÊN (Bên Phải) ---
        JPanel imagePanel = new JPanel();
        imagePanel.setLayout(new BoxLayout(imagePanel, BoxLayout.Y_AXIS));
        imagePanel.setOpaque(false);
        imagePanel.setPreferredSize(new Dimension(200, 300));
        imagePanel.setBorder(new EmptyBorder(0, 20, 0, 0));

        JLabel lblImgTitle = new JLabel("Ảnh Sản Phẩm");
        lblImgTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblImgTitle.setForeground(AppColor.TEXT_SECONDARY);
        lblImgTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblImagePreview = new JLabel();
        lblImagePreview.setPreferredSize(new Dimension(160, 160));
        lblImagePreview.setMaximumSize(new Dimension(160, 160));
        lblImagePreview.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblImagePreview.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        lblImagePreview.setHorizontalAlignment(SwingConstants.CENTER);
        lblImagePreview.setText("Chưa có ảnh");

        JButton btnChooseImg = new JButton("Chọn Ảnh...");
        btnChooseImg.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnChooseImg.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnChooseImg.addActionListener(e -> chooseImage());

        imagePanel.add(lblImgTitle);
        imagePanel.add(Box.createVerticalStrut(10));
        imagePanel.add(lblImagePreview);
        imagePanel.add(Box.createVerticalStrut(15));
        imagePanel.add(btnChooseImg);

        add(imagePanel, BorderLayout.EAST);
    }

    private void chooseImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn ảnh sản phẩm");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files", "jpg", "png", "jpeg");
        fileChooser.setFileFilter(filter);

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedImageFile = fileChooser.getSelectedFile();
            try {
                // Hiển thị Preview
                BufferedImage img = ImageIO.read(selectedImageFile);
                Image scaledImg = img.getScaledInstance(160, 160, Image.SCALE_SMOOTH);
                lblImagePreview.setIcon(new ImageIcon(scaledImg));
                lblImagePreview.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Không thể đọc file ảnh!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadCategories() {
        List<LoaiSanPhamDTO> list = loaiBUS.getAll();
        for (LoaiSanPhamDTO lsp : list) {
            cbLoaiSP.addItem(lsp);
        }
        cbLoaiSP.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof LoaiSanPhamDTO) {
                    setText(((LoaiSanPhamDTO) value).getTenLSP());
                }
                return this;
            }
        });
    }

    private JPanel createInputGroup(String labelText, JComponent inputComp) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(AppColor.TEXT_SECONDARY);
        lbl.setBorder(new EmptyBorder(0, 0, 8, 0));
        inputComp.setPreferredSize(new Dimension(250, 40));
        inputComp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        panel.add(lbl);
        panel.add(inputComp);
        return panel;
    }

    private void loadDataForEdit() {
        for (SanPhamDTO sp : bus.getAll()) {
            if (sp.getMaSP().equals(maSP)) {
                currentSP = sp;
                break;
            }
        }

        if (currentSP != null) {
            txtTenSP.setText(currentSP.getTenSP());
            for (int i = 0; i < cbLoaiSP.getItemCount(); i++) {
                if (cbLoaiSP.getItemAt(i).getMaLSP().equals(currentSP.getMaLSP())) {
                    cbLoaiSP.setSelectedIndex(i);
                    break;
                }
            }
            cbChatLuong.setSelectedItem(currentSP.getChatLuong());
            cbBaoQuan.setSelectedItem(currentSP.getBaoQuan());
            txtGiaMua.setText(String.format("%.0f", currentSP.getGiaMua()));
            txtGiaBan.setText(String.format("%.0f", currentSP.getGiaBan()));
            txtDonViTinh.setText(currentSP.getDonViTinh());

            // Load ảnh hiện tại nếu có
            currentImageName = currentSP.getHinhAnh();
            if (currentImageName != null && !currentImageName.isEmpty()) {
                try {
                    File imgFile = new File("src/main/resources/images/" + currentImageName);
                    if (imgFile.exists()) {
                        BufferedImage img = ImageIO.read(imgFile);
                        Image scaledImg = img.getScaledInstance(160, 160, Image.SCALE_SMOOTH);
                        lblImagePreview.setIcon(new ImageIcon(scaledImg));
                        lblImagePreview.setText("");
                    }
                } catch (Exception e) {
                    System.out.println("Không tìm thấy ảnh: " + currentImageName);
                }
            }
        }
    }

    private String saveImageFile() {
        if (selectedImageFile == null) return currentImageName; // Không đổi ảnh thì giữ nguyên tên cũ
        
        try {
            // Đảm bảo thư mục images tồn tại
            File dir = new File("src/main/resources/images");
            if (!dir.exists()) dir.mkdirs();

            // Lấy đuôi mở rộng của file (.jpg, .png)
            String extension = "";
            String originalName = selectedImageFile.getName();
            int i = originalName.lastIndexOf('.');
            if (i > 0) extension = originalName.substring(i);

            // Tạo tên file mới dạng: IMG_1715421000.jpg
            String newFileName = "IMG_" + System.currentTimeMillis() + extension;
            File destFile = new File(dir, newFileName);

            // Copy file vào project
            Files.copy(selectedImageFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return newFileName;

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu file ảnh!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return currentImageName;
        }
    }

    public boolean saveData() {
        if (txtTenSP.getText().isBlank() || txtGiaMua.getText().isBlank() || txtGiaBan.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ các trường bắt buộc (*)", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        try {
            double giaMua = Double.parseDouble(txtGiaMua.getText());
            double giaBan = Double.parseDouble(txtGiaBan.getText());
            LoaiSanPhamDTO selectedLoai = (LoaiSanPhamDTO) cbLoaiSP.getSelectedItem();

            // Gọi hàm copy ảnh vật lý vào ổ cứng và lấy tên file trả về
            String finalImageName = saveImageFile();

            if (maSP == null) {
                // Thêm Mới: ID để trống ("") để Trigger DB tự sinh
                SanPhamDTO newSP = new SanPhamDTO("", txtTenSP.getText().trim(), selectedLoai.getMaLSP(),
                        "", cbChatLuong.getSelectedItem().toString(), giaMua, giaBan,
                        txtDonViTinh.getText().trim(), cbBaoQuan.getSelectedItem().toString(), finalImageName);
                
                bus.add(newSP);
                JOptionPane.showMessageDialog(this, "Thêm sản phẩm thành công!");
            } else {
                // Cập Nhật
                currentSP.setTenSP(txtTenSP.getText().trim());
                currentSP.setMaLSP(selectedLoai.getMaLSP());
                currentSP.setChatLuong(cbChatLuong.getSelectedItem().toString());
                currentSP.setBaoQuan(cbBaoQuan.getSelectedItem().toString());
                currentSP.setGiaMua(giaMua);
                currentSP.setGiaBan(giaBan);
                currentSP.setDonViTinh(txtDonViTinh.getText().trim());
                currentSP.setHinhAnh(finalImageName);
                
                bus.update(currentSP);
                JOptionPane.showMessageDialog(this, "Cập nhật sản phẩm thành công!");
            }
            return true;

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Giá mua và Giá bán phải là số hợp lệ!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi dữ liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    // --- Giữ nguyên Inner class ModernTextField ---
    static class ModernTextField extends JTextField {
        private final String placeholder;
        public ModernTextField(String placeholder) {
            this.placeholder = placeholder; setOpaque(false); setBorder(new EmptyBorder(0, 16, 0, 16));
            setFont(new Font("Segoe UI", Font.PLAIN, 14)); setForeground(AppColor.TEXT_PRIMARY);
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE); g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
            g2.setColor(isFocusOwner() ? AppColor.PRIMARY : new Color(209, 213, 219));
            if (isFocusOwner()) g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 8, 8); super.paintComponent(g);
            if (getText().isEmpty() && !isFocusOwner()) {
                g2.setColor(new Color(156, 163, 175));
                g2.drawString(placeholder, 16, (getHeight() - g.getFontMetrics().getHeight()) / 2 + g.getFontMetrics().getAscent());
            }
            g2.dispose();
        }
    }
}