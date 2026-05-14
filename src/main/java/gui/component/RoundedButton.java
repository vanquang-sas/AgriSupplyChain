package gui.component;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JButton;

public class RoundedButton extends JButton{
     private Color bgColor;

    public RoundedButton(String text, Color color) {
        super(text);
        this.bgColor = color;

        setBackground(color);
        setForeground(Color.WHITE);
        setFont(new Font("Segoe UI", Font.BOLD, 14));

        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(170, 40));
    }
     @Override
    protected void paintComponent(Graphics g) {

        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        // nền bo góc
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

        super.paintComponent(g2);

        g2.dispose();
    }
    @Override
    protected void paintBorder(Graphics g) {
        // không vẽ border
    }
}