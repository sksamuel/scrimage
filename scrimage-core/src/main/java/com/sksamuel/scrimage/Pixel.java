package com.sksamuel.scrimage;

import com.sksamuel.scrimage.color.RGBColor;

import java.util.function.Function;

/**
 * A pixel is an encoding of a color value used in rasters.
 * The pixel is encoded using an ARGB packed int
 */
public class Pixel {

    private final int argb;

    public Pixel(int argb) {
        this.argb = argb;
    }

    public Pixel(int r, int g, int b, int alpha) {
        this.argb = alpha << 24 | (r & 0xFF) << 16 | (g & 0xFF) << 8 | (b & 0xFF);
    }

    public int alpha() {
        return argb >> 24 & 0xFF;
    }

    public int red() {
        return argb >> 16 & 0xFF;
    }

    public int green() {
        return argb >> 8 & 0xFF;
    }

    public int blue() {
        return argb & 0xFF;
    }

    public int toInt() {
        return toARGBInt();
    }

    public int toARGBInt() {
        return argb;
    }

    public RGBColor toColor() {
        return new RGBColor(red(), green(), blue(), alpha());
    }

    public Pixel mapByComponent(Function<Integer, Integer> f) {
        return new Pixel(f.apply(red()), f.apply(blue()), f.apply(green()), f.apply(alpha()));
    }
}