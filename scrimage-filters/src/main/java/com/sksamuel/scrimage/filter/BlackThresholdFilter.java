package com.sksamuel.scrimage.filter;

import com.sksamuel.scrimage.Filter;
import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.pixels.Pixel;
import thirdparty.jhlabs.image.PixelUtils;

/**
 * Works in a similar way to ThresholdFilter, but only blacks those pixels that are below given threshold.
 */
public class BlackThresholdFilter implements Filter {

    private final double thresholdPercentage;

    public BlackThresholdFilter(double thresholdPercentage) {
        assert (thresholdPercentage >= 0.0 && thresholdPercentage <= 100.0);
        this.thresholdPercentage = thresholdPercentage;
    }

    @Override
    public void apply(ImmutableImage image) {
        int threshold = (int) ((255 * thresholdPercentage) / 100.0);

        image.mapInPlace((p) -> {
            int brightness = PixelUtils.brightness(p.argb);
            if (brightness < threshold) {
                return new Pixel(p.x, p.y, p.argb & 0xff000000);
            } else {
                return p;
            }
        });
    }
}
