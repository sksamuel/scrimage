package com.sksamuel.scrimage.filter;

import com.sksamuel.scrimage.Filter;
import com.sksamuel.scrimage.FontUtils;
import com.sksamuel.scrimage.Image;

import java.awt.*;
import java.io.IOException;

/**
 * Places a watermark at a given location.
 */
public class WatermarkFilter implements Filter {

    private final String text;
    private final int x;
    private final int y;
    private final Font font;
    private final boolean antiAlias;
    private final double alpha;
    private final Color color;

    public WatermarkFilter(String text, int x, int y, Font font, boolean antiAlias, double alpha, Color color) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.font = font;
        this.antiAlias = antiAlias;
        this.alpha = alpha;
        this.color = color;
    }

    public WatermarkFilter(String text, int x, int y) throws IOException, FontFormatException {
        this(text, x, y, FontUtils.createFont(Font.SANS_SERIF, 12), true, 0.1, Color.WHITE);
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
    public void apply(Image image) {
        Graphics2D g2 = (Graphics2D) image.awt().getGraphics();
        setupGraphics(g2);

        g2.drawString(text, x, y);
        g2.dispose();
    }
}
