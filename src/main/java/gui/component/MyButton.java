package gui.component;

import gui.ThemeColor;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;
import javax.swing.JButton;

public class MyButton extends JButton {

    public enum ButtonType {
        PRIMARY, DANGER, WARNING, SECONDARY, SUCCESS
    }

    private ButtonType style = ButtonType.PRIMARY; 

    private boolean over;
    private Color color;
    private Color colorOver;
    private Color colorClick;
    private Color borderColor;
    private int radius = 15;

    // Hàm khởi tạo trung tâm (
    private void initComponent(String text, ButtonType type, int fontSize, int width, int height) {
        this.setText(text);
        this.setFont(new Font("Arial", Font.BOLD, fontSize)); // Có thể đổi font tùy ý muốn là robonto nhma t k import đc font này nên để tạm arial
        this.setStyle(type);
        
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        if (width > 0 && height > 0) {
            this.setPreferredSize(new Dimension(width, height));
        }

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent me) {
                setBackground(colorOver);
                over = true;
            }
            @Override
            public void mouseExited(MouseEvent me) {
                setBackground(color);
                over = false;
            }
            @Override
            public void mousePressed(MouseEvent me) {
                setBackground(colorClick);
            }
            @Override
            public void mouseReleased(MouseEvent me) {
                if (over) setBackground(colorOver);
                else setBackground(color);
            }
        });
    }
    // 1. Constructor rỗng để NetBeans có thể kéo thả
    public MyButton() {
        initComponent("", ButtonType.PRIMARY, 14, 0, 0);
    }

    // 2. Tạo nhanh nút chỉ có chữ
    public MyButton(String text, ButtonType type) {
        initComponent(text, type, 14, 150, 40);
    }

    // 3. Tạo nhanh nút có chữ và icon
    public MyButton(String text, ButtonType type, String iconPath) {
        initComponent(text, type, 14, 150, 40);
        try {
            this.setIcon(new ImageIcon(getClass().getResource(iconPath)));
        } catch (Exception e) {
            System.err.println("Không tìm thấy icon: " + iconPath);
        }
    }

    public ButtonType getStyle() {
        return style;
    }

    public void setStyle(ButtonType style) {
        this.style = style;
        switch (style) {
            case PRIMARY:
                color = ThemeColor.PRIMARY;
                colorOver = ThemeColor.PRIMARY_HOVER;
                colorClick = ThemeColor.PRIMARY_ACTIVE;
                borderColor = ThemeColor.PRIMARY;
                setForeground(Color.WHITE);
                break;
            case DANGER:
                color = ThemeColor.ERROR;
                colorOver = ThemeColor.ERROR_HOVER; // Thêm vào ThemeColor
                colorClick = ThemeColor.ERROR_ACTIVE; // Thêm vào ThemeColor
                borderColor = ThemeColor.ERROR;
                setForeground(Color.WHITE);
                break;
            case WARNING:
                color = ThemeColor.WARNING;
                colorOver = new Color(251, 191, 36); 
                colorClick = new Color(217, 119, 6);  
                borderColor = ThemeColor.WARNING;
                setForeground(Color.WHITE);
                break;
            case SUCCESS:
                color = ThemeColor.SUCCESS;
                colorOver = new Color(34, 197, 94);
                colorClick = new Color(21, 128, 61);
                borderColor = ThemeColor.SUCCESS;
                setForeground(Color.WHITE);
                break;
            case SECONDARY:
                color = ThemeColor.SURFACE; 
                colorOver = new Color(243, 244, 246);
                colorClick = new Color(229, 231, 235);
                borderColor = ThemeColor.BORDER; 
                setForeground(ThemeColor.TEXT_PRIMARY); 
                break;
        }
        setBackground(color);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics grphcs) {
        Graphics2D g2 = (Graphics2D) grphcs;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(borderColor);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        g2.setColor(getBackground());
        g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, radius, radius);
        super.paintComponent(grphcs);
    }

    public int getRadius() { return radius; }
    public void setRadius(int radius) { this.radius = radius; repaint(); }
}