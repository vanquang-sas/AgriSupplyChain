package gui.component;

import javax.swing.*;
import java.awt.*;

public class ActionPanel extends JPanel {
    private JButton cmdEdit;
    private JButton cmdDelete;

    public ActionPanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
        setOpaque(true); // Để nó ăn màu nền của bảng

        // Nút Sửa
        cmdEdit = new JButton("Sửa");
        cmdEdit.setFont(new Font("Segoe UI", Font.BOLD, 12));
        cmdEdit.setForeground(new Color(41, 98, 255)); // Màu xanh dương
        cmdEdit.setContentAreaFilled(false);
        cmdEdit.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        cmdEdit.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Nếu có icon, hãy mở comment dòng dưới:
        // cmdEdit.setIcon(new ImageIcon(getClass().getResource("/icons/edit.png")));
        // cmdEdit.setText(""); // Ẩn chữ đi nếu dùng icon

        // Nút Xóa
        cmdDelete = new JButton("Xóa");
        cmdDelete.setFont(new Font("Segoe UI", Font.BOLD, 12));
        cmdDelete.setForeground(new Color(255, 50, 50)); // Màu đỏ
        cmdDelete.setContentAreaFilled(false);
        cmdDelete.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        cmdDelete.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Nếu có icon, hãy mở comment dòng dưới:
        // cmdDelete.setIcon(new ImageIcon(getClass().getResource("/icons/delete.png")));
        // cmdDelete.setText(""); // Ẩn chữ đi nếu dùng icon

        add(cmdEdit);
        add(cmdDelete);
    }

    // Cung cấp getter để Lớp Editor gắn sự kiện
    public JButton getCmdEdit() {
        return cmdEdit;
    }

    public JButton getCmdDelete() {
        return cmdDelete;
    }
}