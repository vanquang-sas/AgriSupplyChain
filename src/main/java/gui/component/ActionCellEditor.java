package gui.component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ActionCellEditor extends DefaultCellEditor {
    private ActionPanel action;

    public ActionCellEditor(ActionListener event) {
        super(new JCheckBox()); // Kế thừa mặc định
        action = new ActionPanel();
        
        // Bắt sự kiện click nút Sửa
        action.getCmdEdit().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fireEditingStopped(); // Dừng trạng thái edit của JTable
                // Bắn sự kiện "EDIT" ra ngoài cho Panel chính xử lý
                event.actionPerformed(new ActionEvent(this, 0, "EDIT"));
            }
        });

        // Bắt sự kiện click nút Xóa
        action.getCmdDelete().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fireEditingStopped(); 
                // Bắn sự kiện "DELETE" ra ngoài cho Panel chính xử lý
                event.actionPerformed(new ActionEvent(this, 0, "DELETE"));
            }
        });
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        // Đồng bộ màu nền khi click
        action.setBackground(table.getSelectionBackground());
        return action;
    }
}