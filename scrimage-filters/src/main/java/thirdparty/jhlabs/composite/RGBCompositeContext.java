package thirdparty.jhlabs.composite;

import java.awt.*;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

public abstract class RGBCompositeContext implements CompositeContext {

    private float alpha;
    private ColorModel srcColorModel;
    private ColorModel dstColorModel;

    public RGBCompositeContext(float alpha, ColorModel srcColorModel, ColorModel dstColorModel) {
        this.alpha = alpha;
        this.srcColorModel = srcColorModel;
        this.dstColorModel = dstColorModel;
    }

    public void dispose() {
    }

    public abstract void composeRGB(int[] src, int[] dst, float alpha);

    public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {
        float alpha = this.alpha;

        int[] srcPix = null;
        int[] dstPix = null;

        int x = dstOut.getMinX();
        int w = dstOut.getWidth();
        int y0 = dstOut.getMinY();
        int y1 = y0 + dstOut.getHeight();

        for (int y = y0; y < y1; y++) {
            srcPix = src.getPixels(x, y, w, 1, srcPix);
            dstPix = dstIn.getPixels(x, y, w, 1, dstPix);
            composeRGB(srcPix, dstPix, alpha);
            dstOut.setPixels(x, y, w, 1, dstPix);
        }
    }
}