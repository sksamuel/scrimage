package com.sksamuel.scrimage.pixels;

import com.sksamuel.scrimage.color.RGBColor;

import java.util.Objects;
import java.util.function.Function;

/**
 * A pixel is an encoding of a color value at a specific location in a Raster.
 * The pixel is encoded using an ARGB packed int.
 */
public class Pixel {

    public final int argb;
    public final int x;
    public final int y;

    public Pixel(int x, int y, int argb) {
        this.x = x;
        this.y = y;
        this.argb = argb;
    }

    public Pixel(int x, int y, int r, int g, int b, int alpha) {
        this.x = x;
        this.y = y;
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

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    public int blue() {
        return argb & 0xFF;
    }

    // use toARGBInt() or .argb
    @Deprecated
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
        return new Pixel(x, y, f.apply(red()), f.apply(blue()), f.apply(green()), f.apply(alpha()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pixel pixel = (Pixel) o;
        return argb == pixel.argb &&
                x == pixel.x &&
                y == pixel.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(argb, x, y);
    }
}