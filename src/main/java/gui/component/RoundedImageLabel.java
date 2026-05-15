package gui.component;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import java.awt.RenderingHints;
// =========================================================
// ROUNDED IMAGE LABEL
// =========================================================
class RoundedImageLabel extends JLabel {

    private int radius;

    public RoundedImageLabel(Icon icon, int radius) {

        super(icon);

        this.radius = radius;

        setHorizontalAlignment(SwingConstants.CENTER);
        setVerticalAlignment(SwingConstants.CENTER);

        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {

        Graphics2D g2 =
                (Graphics2D) g.create();

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        // cắt ảnh theo bo góc
        g2.setClip(new RoundRectangle2D.Double(
                0,
                0,
                getWidth(),
                getHeight(),
                radius,
                radius
        ));

        super.paintComponent(g2);

        g2.dispose();
    }
}