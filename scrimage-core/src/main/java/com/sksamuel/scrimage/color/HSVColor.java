package com.sksamuel.scrimage.color;

/**
 * Hue/Saturation/Value
 * <p>
 * The hue component should be between 0.0 and 360.0
 * The saturation component should be between 0.0 and 1.0
 * The lightness component should be between 0.0 and 1.0
 * The alpha component should be between 0.0 and 1.0
 */
class HSVColor implements Color {

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

    // credit to https://github.com/mjackson/mjijackson.github.com/blob/master/2008/02/rgb-to-hsl-and-rgb-to-hsv-color-model-conversion-algorithms-in-javascript.txt
    public RGBColor toRGB() {

        // assumes h is in th range [0,1] not [0,360) so must convert
        float h = hue / 360f;
        float s = saturation;
        float v = value;

        float i = (float) Math.floor(h * 6f);
        float f = h * 6f - i;
        float p = v * (1 - s);
        float q = v * (1 - f * s);
        float t = v * (1 - (1 - f) * s);

        float r, g, b;

        switch ((int) (i % 6)) {
            case 0:
                r = v;
                g = t;
                b = p;
                break;
            case 1:
                r = q;
                g = v;
                b = p;
                break;
            case 2:
                r = q;
                g = v;
                b = t;
                break;
            case 3:
                r = p;
                g = q;
                b = v;
                break;
            case 4:
                r = t;
                g = p;
                b = v;
                break;
            case 5:
                r = v;
                g = p;
                b = q;
                break;
            default:
                throw new RuntimeException("Cannot convert from HSV to RGB: from " + this);
        }

        return new RGBColor(Math.round(r * 255), Math.round(g * 255), Math.round(b * 255), 255);
    }
}
