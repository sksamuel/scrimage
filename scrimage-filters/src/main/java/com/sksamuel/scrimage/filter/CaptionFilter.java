package com.sksamuel.scrimage.filter;

import com.sksamuel.scrimage.Dimension;
import com.sksamuel.scrimage.Filter;
import com.sksamuel.scrimage.Graphics2DUtils;
import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.Position;
import com.sksamuel.scrimage.canvas.Canvas;
import com.sksamuel.scrimage.canvas.drawables.FilledRect;
import com.sksamuel.scrimage.canvas.drawables.Text;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

public class CaptionFilter implements Filter {

    private final String text;
    private final Position position;
    private final int x;
    private final int y;
    private final Font font;
    private final Color textColor;
    private final double textAlpha;
    private final boolean antiAlias;
    private final boolean fullWidth;
    private final Color captionBackground;
    private final double captionAlpha;
    private final Padding padding;

    public CaptionFilter(String text, Position position, Font font, Color textColor, double textAlpha, boolean antiAlias, boolean fullWidth, Color captionBackground, double captionAlpha, Padding padding) {
        this.text = text;
        this.position = position;
        this.x = -1;
        this.y = -1;
        this.font = font;
        this.textColor = textColor;
        this.textAlpha = textAlpha;
        this.antiAlias = antiAlias;
        this.fullWidth = fullWidth;
        this.captionBackground = captionBackground;
        this.captionAlpha = captionAlpha;
        this.padding = padding;
    }

    public CaptionFilter(String text, int x, int y, Font font, Color textColor, double textAlpha, boolean antiAlias, boolean fullWidth, Color captionBackground, double captionAlpha, Padding padding) {
        this.text = text;
        this.position = null;
        this.x = x;
        this.y = y;
        this.font = font;
        this.textColor = textColor;
        this.textAlpha = textAlpha;
        this.antiAlias = antiAlias;
        this.fullWidth = fullWidth;
        this.captionBackground = captionBackground;
        this.captionAlpha = captionAlpha;
        this.padding = padding;
    }

    @Override
    public void apply(ImmutableImage image) {

        Graphics2D g2 = (Graphics2D) image.awt().getGraphics();
        g2.setFont(font);

        Rectangle2D bounds = g2.getFontMetrics().getStringBounds(text, g2);
        int descent = g2.getFontMetrics().getDescent();

        int captionWidth;
        if (fullWidth) {
            captionWidth = image.width;
        } else {
            captionWidth = (int) (bounds.getWidth() + padding.left + padding.right);
        }

        int captionHeight = (int) (bounds.getHeight() + padding.top + padding.bottom);

        // captionx/y are the top/left coordinates for the caption box
        int captionX, captionY;
        if (position != null) {
            Dimension dim = position.calculateXY(image.width, image.height, captionWidth, captionHeight);
            captionX = dim.getX();
            captionY = dim.getY();
        } else {
            captionX = x;
            captionY = y;
        }

        FilledRect bg = new FilledRect(
                captionX,
                image.height - captionHeight,
                captionWidth,
                captionHeight,
                g -> {
                    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) captionAlpha));
                    g.setColor(captionBackground);
                    Graphics2DUtils.setAntiAlias(g, antiAlias);
                }
        );

        Text string = new Text(
                text,
                captionX + padding.left,
                captionY + padding.top + g2.getFontMetrics().getHeight() - descent,
                g -> {
                    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) textAlpha));
                    g.setColor(textColor);
                    g.setFont(font);
                    Graphics2DUtils.setAntiAlias(g, antiAlias);
                });

        Canvas canvas = new Canvas(image);
        canvas.drawInPlace(bg);
        canvas.drawInPlace(string);
    }
}

