package com.sksamuel.scrimage.filter;

import com.sksamuel.scrimage.Filter;
import com.sksamuel.scrimage.Image;
import com.sksamuel.scrimage.canvas.Font;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * Places a watermark on the image repeated so that it covers the image.
 */
public class WatermarkCoverFilter implements Filter {

    private final String text;
    private final int size;
    private final Font font;
    private final boolean antiAlias;
    private final double alpha;
    private final Color color;

    public WatermarkCoverFilter(String text, int size, Font font, boolean antiAlias, double alpha, Color color) {
        assert (size > 0);
        this.text = text;
        this.size = size;
        this.font = font;
        this.antiAlias = antiAlias;
        this.alpha = alpha;
        this.color = color;
    }

    public WatermarkCoverFilter(String text) {
        this(text, 12, Font.SansSerif(), true, 0.1, Color.WHITE);
    }

    private void setupGraphics(Graphics2D g2) {
        if (antiAlias)
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setColor(color);
        AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) alpha);
        g2.setComposite(alphaComposite);
        g2.setFont(new java.awt.Font(font.name(), java.awt.Font.PLAIN, size));
    }

    @Override
    public void apply(Image image) {
        Graphics2D g2 = (Graphics2D) image.awt().getGraphics();
        setupGraphics(g2);

        FontMetrics fontMetrics = g2.getFontMetrics();
        Rectangle2D bounds = fontMetrics.getStringBounds(text + " ", g2);

        int x = 0;
        int xstart = 0;
        int y = (int) bounds.getY();
        while (y < image.height + bounds.getHeight()) {
            while (x < image.width + bounds.getWidth()) {
                g2.drawString(text, x, y);
                x = (int) (x + bounds.getWidth());
            }
            y = (int) (y + (bounds.getHeight() + bounds.getHeight() * 0.2));
            xstart = (int) (xstart - bounds.getWidth() * 0.2f);
            x = xstart;
        }

        g2.dispose();
    }
}