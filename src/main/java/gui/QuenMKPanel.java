package gui;

import util.AppColor;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class QuenMKPanel extends JPanel {
    private AuthFrame parentFrame;

    public QuenMKPanel(AuthFrame parentFrame) {
        this.parentFrame = parentFrame;
        setLayout(new GridBagLayout());
        setOpaque(false);

        JPanel formWrapper = new JPanel();
        formWrapper.setOpaque(false);
        formWrapper.setLayout(new BoxLayout(formWrapper, BoxLayout.Y_AXIS));
        formWrapper.setBorder(new EmptyBorder(0, 50, 0, 50));

        JLabel lblTitle = new JLabel("KHÔI PHỤC MẬT KHẨU");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(AppColor.TEXT_PRIMARY);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblDesc = new JLabel("Tính năng đang được phát triển.");
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblDesc.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblDesc.setBorder(new EmptyBorder(10, 0, 20, 0));

        JLabel lblBackLogin = new JLabel("Quay lại đăng nhập");
        lblBackLogin.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblBackLogin.setForeground(AppColor.PRIMARY);
        lblBackLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblBackLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblBackLogin.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                parentFrame.switchPanel("LOGIN"); // Quay lại trang Đăng nhập
            }
        });

        formWrapper.add(lblTitle);
        formWrapper.add(lblDesc);
        formWrapper.add(lblBackLogin);

        add(formWrapper, new GridBagConstraints() {{
            gridx = 0; gridy = 0; weightx = 1.0; fill = GridBagConstraints.HORIZONTAL;
        }});
    }
}