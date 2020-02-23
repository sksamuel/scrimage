package com.sksamuel.scrimage.color;

import java.util.Objects;

import static java.lang.Math.round;

/**
 * Hue/Saturation/Value
 * <p>
 * The hue component should be between 0.0 and 360.0
 * The saturation component should be between 0.0 and 1.0
 * The lightness component should be between 0.0 and 1.0
 * The alpha component should be between 0.0 and 1.0
 */
public class HSVColor implements Color {

    public final float hue;
    public final float saturation;
    public final float value;
    public final float alpha;

    public HSVColor(float hue, float saturation, float value, float alpha) {
        assert (0 <= hue && hue <= 360f);
        assert (0 <= saturation && saturation <= 1f);
        assert (0 <= value && value <= 1f);
        assert (0 <= alpha && alpha <= 1f);

        this.hue = hue;
        this.saturation = saturation;
        this.value = value;
        this.alpha = alpha;
    }

    public HSVColor toHSV() {
        return this;
    }

    // credit to https://stackoverflow.com/questions/7896280/converting-from-hsv-hsb-in-java-to-rgb-without-using-java-awt-color-disallowe
    public RGBColor toRGB() {

        // assumes h is in the range [0,1] not [0,360) so must convert
        float h01 = hue / 360f;
        int h = (int) (h01 * 6);
        float f = h01 * 6 - h;
        float p = value * (1 - saturation) * 256;
        float q = value * (1 - f * saturation) * 256;
        float t = value * (1 - (1 - f) * saturation) * 256;

        switch (h) {
            case 0:
                return new RGBColor(round(value * 256), round(t), round(p));
            case 1:
                return new RGBColor(round(q), round(value * 256), round(p));
            case 2:
                return new RGBColor(round(p), round(value * 256), round(t));
            case 3:
                return new RGBColor(round(p), round(q), round(value * 256));
            case 4:
                return new RGBColor(round(t), round(p), round(value * 256));
            case 5:
                return new RGBColor(round(value * 256), round(p), round(q));
            default:
                throw new RuntimeException("Error converting HSV to RGB. HSV was " + hue + ", " + saturation + ", " + value);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HSVColor hsvColor = (HSVColor) o;
        return Float.compare(hsvColor.hue, hue) == 0 &&
                Float.compare(hsvColor.saturation, saturation) == 0 &&
                Float.compare(hsvColor.value, value) == 0 &&
                Float.compare(hsvColor.alpha, alpha) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(hue, saturation, value, alpha);
    }
}
