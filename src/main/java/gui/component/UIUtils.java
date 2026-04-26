package gui.component;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import javax.swing.*;
import java.awt.*;

public class UIUtils {

    public static Icon getIcon(String key, Icon defaultValue) {
        Icon icon = UIManager.getIcon(key);
        return (icon == null) ? defaultValue : icon;
    }

    public static Insets getInsets(String key, Insets defaultValue) {
        Insets insets = UIManager.getInsets(key);
        return (insets == null) ? defaultValue : insets;
    }

    public static String getString(String key, String defaultValue) {
        String string = UIManager.getString(key);
        return (string == null) ? defaultValue : string;
    }

    public static Icon createIcon(String path, Color color, float scale) {
        FlatSVGIcon icon = new FlatSVGIcon(path, scale);
        if (color != null) {
            FlatSVGIcon.ColorFilter colorFilter = new FlatSVGIcon.ColorFilter();
            colorFilter.add(new Color(150, 150, 150), color);
            icon.setColorFilter(colorFilter);
        }
        return icon;
    }
}