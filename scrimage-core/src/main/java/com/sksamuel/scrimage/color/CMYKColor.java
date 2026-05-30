package com.sksamuel.scrimage.color;

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
        // Validate explicitly rather than with `assert`, which is disabled by
        // default in production JVMs (so out-of-range values would slip through
        // and produce wrong colours in toRGB()).
        requireInRange("c", c);
        requireInRange("m", m);
        requireInRange("y", y);
        requireInRange("k", k);
        requireInRange("alpha", alpha);

        this.c = c;
        this.m = m;
        this.y = y;
        this.k = k;
        this.alpha = alpha;
    }

    private static void requireInRange(String component, float value) {
        if (value < 0 || value > 1f)
            throw new IllegalArgumentException(
                "CMYKColor " + component + " component must be in [0, 1] but was " + value);
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
        int result = Float.floatToIntBits(c);
        result = 31 * result + Float.floatToIntBits(m);
        result = 31 * result + Float.floatToIntBits(y);
        result = 31 * result + Float.floatToIntBits(k);
        result = 31 * result + Float.floatToIntBits(alpha);
        return result;
    }
}
