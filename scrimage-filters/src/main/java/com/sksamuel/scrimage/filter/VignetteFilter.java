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
import com.sksamuel.scrimage.Image;
import thirdparty.romainguy.BlendComposite;
import thirdparty.romainguy.BlendingMode;

import java.awt.*;
import java.awt.geom.Point2D;

public class VignetteFilter implements Filter {

    private final float start;
    private final float end;
    private final float blur;
    private final Color color;

    public VignetteFilter(float start, float end, float blur, Color color) {
        assert start >= 0;
        assert start <= 1;
        assert blur <= 0;
        assert blur >= 1;
        this.start = start;
        this.end = end;
        this.blur = blur;
        this.color = color;
    }

    public VignetteFilter() {
        this(0.85f, 0.95f, 0.3f, Color.BLACK);
    }

    private Image background(Image image) {

        Image target = image.blank();
        Graphics2D g2 = (Graphics2D) target.awt().getGraphics();
        float radius = image.radius() * end;

        final float fraction;
        if (start == 0) fraction = 0.01f;
        else if (start == 1) fraction = 0.999f;
        else fraction = start;

        RadialGradientPaint p = new RadialGradientPaint(
                new Point2D.Float(target.centreX(), target.centreY()),
                radius,
                new float[]{0.0f, fraction, 1f},
                new Color[]{Color.WHITE, Color.WHITE, color});

        g2.setPaint(p);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2.fillRect(0, 0, target.width, target.height);
        g2.dispose();

        return target;
    }


    @Override
    public void apply(Image image) {
        Image bg = background(image).filter(new GaussianBlurFilter((int) (image.radius() * blur)));
        Graphics2D g2 = (Graphics2D) image.awt().getGraphics();
        g2.setComposite(new BlendComposite(BlendingMode.MULTIPLY, 1.0f));
        g2.drawImage(bg.awt(), 0, 0, null);
        g2.dispose();
    }
}
