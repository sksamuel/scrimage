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
        // Asserts are disabled by default in production JVMs, so the
        // previous `assert src.width == base.width` checks were no-ops
        // in practice and a dimension mismatch surfaced as a cryptic
        // ArrayIndexOutOfBoundsException from base.awt().getRGB(x, y)
        // deep inside the loop. Validate explicitly with a useful
        // diagnostic.
        if (src.width != base.width || src.height != base.height) {
            throw new IllegalArgumentException(
                "ErrorSpotterFilter requires src and base to have the same dimensions; "
                    + "src=" + src.width + "x" + src.height
                    + ", base=" + base.width + "x" + base.height);
        }
        // Read both rasters in a single bulk getRGB each, rather than a slow
        // scalar getRGB(x, y) per pixel on both images plus a scalar
        // setRGB(x, y) per pixel. Each pixel is computed independently, so the
        // row-major iteration order produces identical results.
        int w = src.width;
        int h = src.height;
        int[] basePixels = base.awt().getRGB(0, 0, w, h, null, 0, w);
        int[] srcPixels = src.awt().getRGB(0, 0, w, h, null, 0, w);
        for (int i = 0; i < srcPixels.length; i++) {
            srcPixels[i] = error(RGBColor.fromARGBInt(basePixels[i]), RGBColor.fromARGBInt(srcPixels[i]));
        }
        src.awt().setRGB(0, 0, w, h, srcPixels, 0, w);
    }
}
