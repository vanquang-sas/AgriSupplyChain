package gui.component;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class StatusBadgeRenderer extends JLabel implements TableCellRenderer {
    
    public StatusBadgeRenderer() {
        setOpaque(true);
        setHorizontalAlignment(CENTER);
        setFont(new Font("Segoe UI", Font.BOLD, 12));
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (value instanceof Integer) {
            int status = (Integer) value;
            if (status == 1) {
                setText("Hoạt động");
                setBackground(new Color(230, 248, 238)); // Nền xanh nhạt
                setForeground(new Color(0, 137, 84));    // Chữ xanh đậm
            } else {
                setText("Ngưng hoạt động");
                setBackground(new Color(240, 240, 240)); // Nền xám nhạt
                setForeground(new Color(120, 120, 120)); // Chữ xám
            }
        }
        
        // Đảm bảo nền hòa hợp với row selection
        if (isSelected) {
            // Có thể làm tối đi một chút hoặc giữ nguyên
        }
        return this;
    }

    // Override paintComponent để vẽ bo góc cho Badge
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(10, 5, getWidth() - 20, getHeight() - 10, 15, 15);
        super.paintComponent(g);
        g2.dispose();
    }
}