package util;

import java.awt.Color;

public class AppColor {
    // CÁCH DÙNG
    // Thay vì viết formPanel.setBackground(new Color(46, 204, 113));
    // Có thể viết  formPanel.setBackground(AppColor.BACKGROUND);
    
    // Nhóm màu chủ đạo (Brand Colors)
    public static final Color PRIMARY = Color.decode("#16A34A");
    public static final Color PRIMARY_HOVER = Color.decode("#22C55E");
    public static final Color PRIMARY_ACTIVE = Color.decode("#15803D");

    // Nhóm màu nền và bề mặt (Layout Colors)
    public static final Color BACKGROUND = Color.decode("#F9FAFB");
    public static final Color SURFACE = Color.decode("#FFFFFF");
    public static final Color BORDER = Color.decode("#E5E7EB");

    // Nhóm màu chữ (Typography)
    public static final Color TEXT_PRIMARY = Color.decode("#111827");
    public static final Color TEXT_SECONDARY = Color.decode("#6B7280");

    // Nhóm màu trạng thái (Status Colors)
    public static final Color SUCCESS = Color.decode("#16A34A");
    public static final Color ERROR = Color.decode("#DC2626");
    public static final Color WARNING = Color.decode("#F59E0B");
    public static final Color INFO = Color.decode("#0EA5E9");
}
