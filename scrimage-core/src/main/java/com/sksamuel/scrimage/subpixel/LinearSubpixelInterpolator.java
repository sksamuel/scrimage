package com.sksamuel.scrimage.subpixel;

import com.sksamuel.scrimage.AwtImage;
import com.sksamuel.scrimage.pixels.PixelTools;

public class LinearSubpixelInterpolator implements SubpixelInterpolator {

    private final AwtImage awt;
    private final int width;
    private final int height;

    public LinearSubpixelInterpolator(AwtImage awt) {
        this.awt = awt;
        width = awt.width;
        height = awt.height;
    }

    @Override
    public int subpixel(double x, double y) {
        assert (x >= 0 && x < width && y >= 0 && y < height);

        // Determine the integer pixel neighbours along each axis and their
        // bilinear weights. Each axis contributes 1 or 2 (coord, weight) pairs.
        int xCount, x0, x1;
        double xw0, xw1;
        if (x <= 0.5) {
            xCount = 1; x0 = 0; xw0 = 1.0; x1 = 0; xw1 = 0.0;
        } else if (x >= width - 0.5) {
            xCount = 1; x0 = width - 1; xw0 = 1.0; x1 = 0; xw1 = 0.0;
        } else {
            double shifted = x - 0.5;
            double floor = Math.floor(shifted);
            double floorWeight = 1 - (shifted - floor);
            xCount = 2;
            x0 = (int) floor; xw0 = floorWeight;
            x1 = (int) Math.ceil(shifted); xw1 = 1 - floorWeight;
        }

        int yCount, y0, y1;
        double yw0, yw1;
        if (y <= 0.5) {
            yCount = 1; y0 = 0; yw0 = 1.0; y1 = 0; yw1 = 0.0;
        } else if (y >= height - 0.5) {
            yCount = 1; y0 = height - 1; yw0 = 1.0; y1 = 0; yw1 = 0.0;
        } else {
            double shifted = y - 0.5;
            double floor = Math.floor(shifted);
            double floorWeight = 1 - (shifted - floor);
            yCount = 2;
            y0 = (int) floor; yw0 = floorWeight;
            y1 = (int) Math.ceil(shifted); yw1 = 1 - floorWeight;
        }

        // Accumulate the weighted channel sums over the up-to-four neighbours.
        double sumA = 0, sumR = 0, sumG = 0, sumB = 0;
        for (int xi = 0; xi < xCount; xi++) {
            int xc = (xi == 0) ? x0 : x1;
            double xw = (xi == 0) ? xw0 : xw1;
            for (int yi = 0; yi < yCount; yi++) {
                int yc = (yi == 0) ? y0 : y1;
                double yw = (yi == 0) ? yw0 : yw1;
                double weight = xw * yw;
                if (weight == 0) continue;
                int p = awt.awt().getRGB(xc, yc);
                sumA += weight * ((p >>> 24) & 0xFF);
                sumR += weight * ((p >> 16) & 0xFF);
                sumG += weight * ((p >> 8) & 0xFF);
                sumB += weight * (p & 0xFF);
            }
        }

        return PixelTools.argb(
                (int) Math.round(sumA),
                (int) Math.round(sumR),
                (int) Math.round(sumG),
                (int) Math.round(sumB)
        );
    }
}
