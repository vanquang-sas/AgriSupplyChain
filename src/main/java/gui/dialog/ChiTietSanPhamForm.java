
package gui.dialog;

import dto.SanPhamDTO;
import gui.component.RoundedButton;
import util.AppColor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import gui.component.RoundedImageLabel;

public class ChiTietSanPhamForm extends JDialog {


    private JLabel lblTenSP;
    private JLabel lblChatLuong;
    private JLabel lblGiaBan;
    private JLabel lblDonViTinh;
    private JLabel lblBaoQuan;

    // private JLabel lblHinhAnh;
    private RoundedImageLabel lblHinhAnh;

    private JButton btnThemGio;

    public ChiTietSanPhamForm(Frame parent, SanPhamDTO sp) {

        super(parent, "Chi tiết sản phẩm", true);

        setSize(700, 500);

        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        Color BG = AppColor.BACKGROUND;

        JPanel pnlMain = new JPanel(new BorderLayout(15, 15));

        pnlMain.setBackground(BG);

        pnlMain.setBorder(new EmptyBorder(20, 20, 20, 20));

        add(pnlMain);

        // =====================================
        // TITLE
        // =====================================
        JLabel lblTitle = new JLabel("CHI TIẾT SẢN PHẨM");

        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));

        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setForeground(Color.BLACK);

        pnlMain.add(lblTitle, BorderLayout.NORTH);

        // =====================================
        // CENTER
        // =====================================
        JPanel pnlCenter = new JPanel(new BorderLayout(20, 20));

        pnlCenter.setBackground(BG);

        pnlMain.add(pnlCenter, BorderLayout.CENTER);
    // =====================================
    // HÌNH ẢNH
    // =====================================
        lblHinhAnh = new RoundedImageLabel(30); // 30 là độ bo góc

        lblHinhAnh.setHorizontalAlignment(SwingConstants.CENTER);
        lblHinhAnh.setPreferredSize(new Dimension(320,240));

        try {
            String fileName = sp.getHinhAnh();
            java.net.URL imageURL =
                getClass().getResource(
                        "/images/" + fileName
                );
            // String duongDanAnh = "src/main/resources/images/" + sp.getMaSP() + ".jpg";

            ImageIcon icon = new ImageIcon(imageURL);

            // Image img = icon.getImage().getScaledInstance(
            //         240,
            //         240,
            //         Image.SCALE_SMOOTH
            // );

            // lblHinhAnh.setIcon(new ImageIcon(img));
                lblHinhAnh.setIcon(icon);

        } catch (Exception e) {
            lblHinhAnh.setText("Không có ảnh");
            lblHinhAnh.setForeground(Color.BLACK);
        }

            pnlCenter.add(lblHinhAnh, BorderLayout.WEST);
                // =====================================
                // THÔNG TIN
                // =====================================
                JPanel pnlInfo = new JPanel(new GridLayout(8, 2, 10, 15));

                pnlInfo.setBackground(BG);

                pnlCenter.add(pnlInfo, BorderLayout.CENTER);

                // Mã SP
                // pnlInfo.add(createLabel("Mã sản phẩm:", true));

                // lblMaSP = createLabel(sp.getMaSP(), false);

                // pnlInfo.add(lblMaSP);

                // Tên SP
                pnlInfo.add(createLabel("Tên sản phẩm:", true));

                lblTenSP = createLabel(sp.getTenSP(), false);

                pnlInfo.add(lblTenSP);

                // Loại SP
            // pnlInfo.add(createLabel("Mã loại:", true));

            // lblLoai = createLabel(sp.getMaLSP(), false);

            // pnlInfo.add(lblLoai);

            // Chất lượng
            pnlInfo.add(createLabel("Chất lượng:", true));

            lblChatLuong = createLabel(sp.getChatLuong(), false);

            pnlInfo.add(lblChatLuong);

            // Giá mua
            // pnlInfo.add(createLabel("Giá mua:", true));

            // lblGiaMua = createLabel(
            //         String.format("%,.0f VNĐ", sp.getGiaMua()),
            //         false
            // );

            // pnlInfo.add(lblGiaMua);

            // Giá bán
            pnlInfo.add(createLabel("Giá bán:", true));

            lblGiaBan = createLabel(
                    String.format("%,.0f VNĐ", sp.getGiaBan()),
                    false
            );

            pnlInfo.add(lblGiaBan);

            // Đơn vị tính
            pnlInfo.add(createLabel("Đơn vị tính:", true));

            lblDonViTinh = createLabel(sp.getDonViTinh(), false);

            pnlInfo.add(lblDonViTinh);

            // Bảo quản
            pnlInfo.add(createLabel("Bảo quản:", true));

            lblBaoQuan = createLabel(sp.getBaoQuan(), false);

            pnlInfo.add(lblBaoQuan);

            // =====================================
            // BUTTON
            // =====================================
            JPanel pnlBottom = new JPanel();
            pnlBottom.setBackground(BG);

            // dùng component thay vì JButton thường
            btnThemGio = new RoundedButton("Thêm vào giỏ hàng", AppColor.PRIMARY);

            pnlBottom.add(btnThemGio);
            pnlMain.add(pnlBottom, BorderLayout.SOUTH);

            // =====================================
            // EVENT
            // =====================================
            btnThemGio.addActionListener(e -> {

                JOptionPane.showMessageDialog(
                        this,
                        "Đã thêm vào giỏ hàng:\n" + sp.getTenSP()
                );
            });
        }

        // =====================================
        // CUSTOM LABEL
        // =====================================
        private JLabel createLabel(String text, boolean bold) {

            JLabel lbl = new JLabel(text);

            lbl.setFont(new Font(
                    "Segoe UI",
                    bold ? Font.BOLD : Font.PLAIN,
                    16
            ));
            // ÉP MÀU CHỮ ĐEN
            lbl.setForeground(Color.BLACK); 
            return lbl;
        }
}