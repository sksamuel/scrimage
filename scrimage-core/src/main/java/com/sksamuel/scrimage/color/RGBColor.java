package com.sksamuel.scrimage.color;

import com.sksamuel.scrimage.Pixel;

/**
 * Red/Green/Blue
 * <p>
 * The red, green, blue, and alpha components should be between [0,255].
 */
public class RGBColor implements Color {

    public final int red;
    public final int green;
    public final int blue;
    public final int alpha;

    public RGBColor(int red, int green, int blue, int alpha) {

        assert (0 <= red && red <= 255);
        assert (0 <= green && green <= 255);
        assert (0 <= blue && blue <= 255);
        assert (0 <= alpha && alpha <= 255);

        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    public static RGBColor fromARGBInt(int argb) {
        return new Pixel(argb).toColor();
    }

    public static RGBColor fromAwt(java.awt.Color color) {
        return new RGBColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    /**
     * Returns as an int the value of this color. The RGB and alpha components are packed
     * into the int as byes.
     */
    public int toInt() {
        return toARGBInt();
    }

    public int toARGBInt() {
        return ((alpha & 0xFF) << 24) | ((red & 0xFF) << 16) | ((green & 0xFF) << 8) | blue & 0xFF;
    }

    public RGBColor toRGB() {
        return this;
    }

    /**
     * Returns a conversion of this color into a CYMK color.
     * If this colour is already a CYMK then the same instance will be returned.
     */
    public CMYKColor toCMYK() {
        float max = Math.max(Math.max(red, green), blue) / 255f;
        float k = 1.0f - max;
        if (max > 0.0f) {
            return new CMYKColor(1.0f - (red / 255f) / max, 1.0f - (green / 255f) / max, 1.0f - (blue / 255f) / max, k);
        } else {
            return new CMYKColor(0, 0, 0, k);
        }
    }

    // credit to https://github.com/mjackson/mjijackson.github.com/blob/master/2008/02/rgb-to-hsl-and-rgb-to-hsv-color-model-conversion-algorithms-in-javascript.txt
    public HSVColor toHSV() {

        float r = red / 255f;
        float g = green / 255f;
        float b = blue / 255f;

        float max = Math.max(Math.max(r, g), b);
        float min = Math.min(Math.min(r, g), b);

        float d = (max - min);
        float s;
        if (max == 0) {
            s = 0;
        } else {
            s = d / max;
        }

        float h;
        if (max == min) {
            // achromatic
            h = 0f;
        } else {
            float f;
            if (max == r) {
                f = (g - b) / d + (g < b ? 6 : 0);
            } else if (max == g) {
                f = (b - r) / d + 2;
            } else {
                f = (r - g) / d + 4;
            }
            h = f / 6;
        }

        return new HSVColor(h * 360f, s, max, 1f);
    }

    // credit to https://github.com/mjackson/mjijackson.github.com/blob/master/2008/02/rgb-to-hsl-and-rgb-to-hsv-color-model-conversion-algorithms-in-javascript.txt
    public HSLColor toHSL() {

        float r = red / 255f;
        float g = green / 255f;
        float b = blue / 255f;

        float max = Math.max(Math.max(r, g), b);
        float min = Math.min(Math.min(r, g), b);

        float l = (max + min) / 2f;

        float h;
        float s;
        if (max == min) {
            h = 0;
            s = 0;
        } else {
            float d = max - min;

            if (l > 0.5) s = d / (2 - max - min);
            else s = d / (max + min);

            float f;
            if (max == r) {
                f = (g - b) / d + (g < b ? 6 : 0);
            } else if (max == g) {
                f = (b - r) / d + 2;
            } else {
                f = (r - g) / d + 4;
            }
            h = f / 6;
        }

        return new HSLColor(h * 360f, s, l, 1f);
    }
}
