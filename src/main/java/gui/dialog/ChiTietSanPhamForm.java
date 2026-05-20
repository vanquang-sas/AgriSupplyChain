
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
    private JLabel lblTenLoai;
    private JLabel lblChatLuong;
    private JLabel lblGiaBan;
    private JLabel lblDonViTinh;

    private RoundedImageLabel lblHinhAnh;

    private JButton btnDong;

    public ChiTietSanPhamForm(Frame parent, SanPhamDTO sp) {

        super(parent, "Chi tiết sản phẩm", true);

        setSize(700, 420);

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
        lblHinhAnh = new RoundedImageLabel(30);

        lblHinhAnh.setHorizontalAlignment(SwingConstants.CENTER);
        lblHinhAnh.setPreferredSize(new Dimension(320,240));

        try {
            String duongDanAnh = "src/main/resources/images/" + sp.getMaSP() + ".jpg";

            ImageIcon icon = new ImageIcon(duongDanAnh);

            lblHinhAnh.setIcon(icon);

        } catch (Exception e) {
            lblHinhAnh.setText("Không có ảnh");
            lblHinhAnh.setForeground(Color.BLACK);
        }

        pnlCenter.add(lblHinhAnh, BorderLayout.WEST);

        // =====================================
        // THÔNG TIN (Chỉ 5 trường)
        // =====================================
        JPanel pnlInfo = new JPanel(new GridLayout(5, 2, 10, 15));

        pnlInfo.setBackground(BG);

        pnlCenter.add(pnlInfo, BorderLayout.CENTER);

        // Tên SP
        pnlInfo.add(createLabel("Tên sản phẩm:", true));

        lblTenSP = createLabel(sp.getTenSP(), false);

        pnlInfo.add(lblTenSP);

        // Tên Loại SP
        pnlInfo.add(createLabel("Loại sản phẩm:", true));

        String tenLoai = sp.getTenLSP() != null ? sp.getTenLSP() : sp.getMaLSP();
        lblTenLoai = createLabel(tenLoai, false);

        pnlInfo.add(lblTenLoai);

        // Chất lượng
        pnlInfo.add(createLabel("Chất lượng:", true));

        lblChatLuong = createLabel(sp.getChatLuong(), false);

        pnlInfo.add(lblChatLuong);

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

        // =====================================
        // BUTTON ĐÓNG
        // =====================================
        JPanel pnlBottom = new JPanel();
        pnlBottom.setBackground(BG);

        btnDong = new RoundedButton("Đóng", AppColor.PRIMARY);

        pnlBottom.add(btnDong);
        pnlMain.add(pnlBottom, BorderLayout.SOUTH);

        // =====================================
        // EVENT
        // =====================================
        btnDong.addActionListener(e -> {
            dispose();
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