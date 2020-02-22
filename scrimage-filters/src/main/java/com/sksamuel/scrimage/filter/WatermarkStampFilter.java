/*
   Copyright 2013 Stephen K Samuel

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package com.sksamuel.scrimage.filter;

import com.sksamuel.scrimage.Filter;
import com.sksamuel.scrimage.FontUtils;
import com.sksamuel.scrimage.ImmutableImage;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

public class WatermarkStampFilter implements Filter {

    private final String text;
    private final Font font;
    private final boolean antiAlias;
    private final double alpha;
    private final Color color;

    public WatermarkStampFilter(String text, Font font, boolean antiAlias, double alpha, Color color) {
        this.text = text;
        this.font = font;
        this.antiAlias = antiAlias;
        this.alpha = alpha;
        this.color = color;
    }

    public WatermarkStampFilter(String text) throws IOException, FontFormatException {
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
        Rectangle2D bounds = fontMetrics.getStringBounds(text, g2);

        g2.translate(image.width / 2.0, image.height / 2.0);

        AffineTransform rotation = new AffineTransform();
        double opad = image.height / (double) image.width;
        double angle = Math.toDegrees(Math.atan(opad));
        double idegrees = -1 * angle;
        double theta = (2 * Math.PI * idegrees) / 360;
        rotation.rotate(theta);
        g2.transform(rotation);

        double x1 = bounds.getWidth() / 2.0f * -1;
        double y1 = bounds.getHeight() / 2.0f;
        g2.translate(x1, y1);

        g2.drawString(text, 0.0f, 0.0f);
        g2.dispose();
    }
}
