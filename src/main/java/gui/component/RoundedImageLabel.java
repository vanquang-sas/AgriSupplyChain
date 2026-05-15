package gui.component;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import com.itextpdf.text.Image;

import java.awt.RenderingHints;

// =========================================================
// ROUNDED IMAGE LABEL
// =========================================================
public class RoundedImageLabel extends JLabel {

    private int radius;

    public RoundedImageLabel(Icon icon, int radius) {

        super(icon);

        this.radius = radius;

        setHorizontalAlignment(SwingConstants.CENTER);
        setVerticalAlignment(SwingConstants.CENTER);

        setOpaque(false);
    }

   // Constructor truyền radius
    public RoundedImageLabel(int radius) {
        this.radius = radius;
        setHorizontalAlignment(SwingConstants.CENTER);
        setVerticalAlignment(SwingConstants.CENTER);
        setOpaque(false);
    }
        @Override
    protected void paintComponent(Graphics g) {
        if (getIcon() == null) {
            super.paintComponent(g);
            return;
        }

        Graphics2D g2 = (Graphics2D) g.create();

        // Làm mượt ảnh
        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );
        g2.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC
        );

        int w = getWidth();
        int h = getHeight();

        // Bo góc toàn bộ label
        RoundRectangle2D round = new RoundRectangle2D.Double(
                0,
                0,
                w,
                h,
                radius,
                radius
        );
        g2.setClip(round);

        // Lấy ảnh gốc
        java.awt.Image image = ((ImageIcon) getIcon()).getImage();
        int imgW = image.getWidth(this);
        int imgH = image.getHeight(this);

        if (imgW > 0 && imgH > 0) {
            /*
            * Tính scale để ảnh phủ kín label nhưng vẫn giữ nguyên tỉ lệ,
            * nhờ đó ảnh không bị méo.
            */
            double scale = Math.max(
                    (double) w / imgW,
                    (double) h / imgH
            );

            int drawW = (int) (imgW * scale);
            int drawH = (int) (imgH * scale);

            // Căn giữa ảnh
            int x = (w - drawW) / 2;
            int y = (h - drawH) / 2;

            g2.drawImage(
                    image,
                    x,
                    y,
                    drawW,
                    drawH,
                    this
            );
        }

        g2.dispose();
    }
}