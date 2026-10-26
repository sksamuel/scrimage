package com.sksamuel.scrimage.filter;

import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.color.RGBColor;
import thirdparty.romainguy.BlendComposite;
import thirdparty.romainguy.BlendingMode;

import java.awt.*;
import java.awt.image.BufferedImage;

public class BackgroundBlendFilter implements Filter {

    /**
     * The background is composited with {@link BlendComposite}, which requires an int-backed
     * raster and reads alpha from the raw data elements (0 for TYPE_INT_RGB / TYPE_INT_BGR),
     * so only TYPE_INT_ARGB is supported; other images are converted first.
     */
    @Override
    public int[] types() {
        return new int[]{BufferedImage.TYPE_INT_ARGB};
    }

    @Override
    public void apply(ImmutableImage image) {
        ImmutableImage background = image.fill(new RGBColor(51, 0, 0, 255).toAWT());
        Graphics2D g = (Graphics2D) image.awt().getGraphics();
        try {
            g.setComposite(BlendComposite.getInstance(BlendingMode.ADD, 1f));
            g.drawImage(background.awt(), 0, 0, null);
        } finally {
            g.dispose();
        }
    }
}
