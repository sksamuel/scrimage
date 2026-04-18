package com.sksamuel.scrimage.color;

import java.util.Objects;

public class CMYKColor implements Color {

    public final float c;
    public final float m;
    public final float y;
    public final float k;
    public final float alpha;

    public CMYKColor(float c, float m, float y, float k) {
        this(c, m, y, k, 1.0f);
    }

    public CMYKColor(float c, float m, float y, float k, float alpha) {
        assert (0 <= c && c <= 1f);
        assert (0 <= m && m <= 1f);
        assert (0 <= y && y <= 1f);
        assert (0 <= k && k <= 1f);
        assert (0 <= alpha && alpha <= 1f);

        this.c = c;
        this.m = m;
        this.y = y;
        this.k = k;
        this.alpha = alpha;
    }

    public CMYKColor toCMYK() {
        return this;
    }

    public RGBColor toRGB() {
        float red = 1.0f + c * k - k - c;
        float green = 1.0f + m * k - k - m;
        float blue = 1.0f + y * k - k - y;
        return new RGBColor(Math.round(red * 255), Math.round(green * 255), Math.round(blue * 255), Math.round(alpha * 255));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CMYKColor cmykColor = (CMYKColor) o;
        return Float.compare(cmykColor.c, c) == 0 &&
                Float.compare(cmykColor.m, m) == 0 &&
                Float.compare(cmykColor.y, y) == 0 &&
                Float.compare(cmykColor.k, k) == 0 &&
                Float.compare(cmykColor.alpha, alpha) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(c, m, y, k, alpha);
    }
}
