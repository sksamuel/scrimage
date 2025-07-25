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
        int red = 0, blue = 0, delta;
        delta = rgb1.red - rgb2.red;
        if (delta > 0) red += delta;
        else blue -= delta;
        delta = rgb1.blue - rgb2.blue;
        if (delta > 0) red += delta;
        else blue -= delta;
        delta = rgb1.green - rgb2.green;
        if (delta > 0) red += delta;
        else blue -= delta;
        delta = rgb1.alpha - rgb2.alpha;
        if (delta > 0) red += delta;
        else blue -= delta;
        return new RGBColor(Math.min(ratio * red, 255), 0, Math.min(ratio * blue, 255), 255).toARGBInt();
    }

    @Override
    public void apply(ImmutableImage src) {
        assert (src.width == base.width);
        assert (src.height == base.height);
        for (int x = 0; x < src.width; x++) {
            for (int y = 0; y < src.height; y++) {
                int delta = error(RGBColor.fromARGBInt(base.awt().getRGB(x, y)), RGBColor.fromARGBInt(src.awt().getRGB(x, y)));
                src.awt().setRGB(x, y, delta);
            }
        }
    }
}
