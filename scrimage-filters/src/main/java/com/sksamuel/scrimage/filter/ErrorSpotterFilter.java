package com.sksamuel.scrimage.filter;

import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.color.RGBColor;

/**
 * Created by guw on 23/09/14.
 */
public class ErrorSpotterFilter implements Filter {

    private final ImmutableImage base;
    private final int ratio;

    public ErrorSpotterFilter(ImmutableImage base) {
        this(base, 50);
    }

    public ErrorSpotterFilter(ImmutableImage base, int ratio) {
        this.base = base;
        this.ratio = ratio;
    }

    public int error(RGBColor rgb1, RGBColor rgb2) {
        return errorArgb(rgb1.toARGBInt(), rgb2.toARGBInt());
    }

    private int errorArgb(int argb1, int argb2) {
        int red = 0, blue = 0, delta;
        delta = ((argb1 >> 16) & 0xFF) - ((argb2 >> 16) & 0xFF); // red
        if (delta > 0) red += delta;
        else blue -= delta;
        delta = (argb1 & 0xFF) - (argb2 & 0xFF); // blue
        if (delta > 0) red += delta;
        else blue -= delta;
        delta = ((argb1 >> 8) & 0xFF) - ((argb2 >> 8) & 0xFF); // green
        if (delta > 0) red += delta;
        else blue -= delta;
        delta = ((argb1 >>> 24) & 0xFF) - ((argb2 >>> 24) & 0xFF); // alpha
        if (delta > 0) red += delta;
        else blue -= delta;
        int r = Math.min(ratio * red, 255);
        int b = Math.min(ratio * blue, 255);
        return 0xFF000000 | (r << 16) | b;
    }

    @Override
    public void apply(ImmutableImage src) {
        assert (src.width == base.width);
        assert (src.height == base.height);
        int w = src.width;
        int h = src.height;
        int[] srcArgb = src.awt().getRGB(0, 0, w, h, null, 0, w);
        int[] baseArgb = base.awt().getRGB(0, 0, w, h, null, 0, w);
        for (int i = 0; i < srcArgb.length; i++) {
            srcArgb[i] = errorArgb(baseArgb[i], srcArgb[i]);
        }
        src.awt().setRGB(0, 0, w, h, srcArgb, 0, w);
    }
}
