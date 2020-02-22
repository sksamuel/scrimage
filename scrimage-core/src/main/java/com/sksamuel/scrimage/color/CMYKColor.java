package com.sksamuel.scrimage.color;

public class CMYKColor implements Color {

    public final float c;
    public final float m;
    public final float y;
    public final float k;

    public CMYKColor(float c, float m, float y, float k) {
        assert (0 <= c && c <= 1f);
        assert (0 <= m && m <= 1f);
        assert (0 <= y && y <= 1f);
        assert (0 <= k && k <= 1f);

        this.c = c;
        this.m = m;
        this.y = y;
        this.k = k;
    }

    public CMYKColor toCMYK() {
        return this;
    }

    public RGBColor toRGB() {
        float red = 1.0f + c * k - k - c;
        float green = 1.0f + m * k - k - m;
        float blue = 1.0f + y * k - k - y;
        return new RGBColor(Math.round(red * 255), Math.round(green * 255), Math.round(blue * 255), 255);
    }
}
