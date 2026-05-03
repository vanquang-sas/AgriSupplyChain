package gui.component;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class ActionCellRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        // Gọi super để lấy màu nền (khi được chọn hoặc không)
        Component com = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        
        // Khởi tạo ActionPanel để hiển thị
        ActionPanel action = new ActionPanel();
        
        // Đồng bộ màu nền của Panel với màu nền của dòng hiện tại trong bảng
        if (isSelected == false && row % 2 == 0) {
            action.setBackground(Color.WHITE);
        } else {
            action.setBackground(com.getBackground());
        }
        
        return action;
    }
}