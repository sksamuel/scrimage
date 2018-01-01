package com.sksamuel.scrimage.filter;

import com.sksamuel.scrimage.Filter;
import com.sksamuel.scrimage.Image;
import com.sksamuel.scrimage.canvas.Font;

import java.awt.*;

/**
 * Places a watermark at a given location.
 */
public class WatermarkFilter implements Filter {

    private final String text;
    private final int x;
    private final int y;
    private final int size;
    private final Font font;
    private final boolean antiAlias;
    private final double alpha;
    private final Color color;

    public WatermarkFilter(String text, int x, int y, int size, Font font, boolean antiAlias, double alpha, Color color) {
        assert (size > 0);
        this.text = text;
        this.x = x;
        this.y = y;
        this.size = size;
        this.font = font;
        this.antiAlias = antiAlias;
        this.alpha = alpha;
        this.color = color;
    }

    public WatermarkFilter(String text, int x, int y) {
        this(text, x, y, 12, Font.SansSerif(), true, 0.1, Color.WHITE);
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

        g2.drawString(text, x, y);
        g2.dispose();
    }
}
