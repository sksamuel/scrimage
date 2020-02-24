package com.sksamuel.scrimage.filter;

import com.sksamuel.scrimage.FontUtils;
import com.sksamuel.scrimage.ImmutableImage;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

/**
 * Places a watermark on the image repeated so that it covers the image.
 */
public class WatermarkCoverFilter implements Filter {

    private final String text;
    private final Font font;
    private final boolean antiAlias;
    private final double alpha;
    private final Color color;

    public WatermarkCoverFilter(String text, Font font, boolean antiAlias, double alpha, Color color) {
        this.text = text;
        this.font = font;
        this.antiAlias = antiAlias;
        this.alpha = alpha;
        this.color = color;
    }

    public WatermarkCoverFilter(String text) throws IOException, FontFormatException {
        this(text, FontUtils.createFont(Font.SANS_SERIF, 12), true, 0.1, Color.WHITE);
    }

    private void setupGraphics(Graphics2D g2) {
        if (antiAlias)
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setColor(color);
        AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) alpha);
        g2.setComposite(alphaComposite);
        g2.setFont(font);
    }

    @Override
    public void apply(ImmutableImage image) {
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
