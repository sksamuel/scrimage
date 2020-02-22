package com.sksamuel.scrimage.color;

/**
 * Hue/Saturation/Lightness
 * <p>
 * The hue component should be between0.0and360.0
 * The saturation component should be between0.0and1.0
 * The lightness component should be between0.0and1.0
 * The alpha component should be between0.0and1.0
 */
public class HSLColor {

    public final float hue;
    public final float saturation;
    public final float lightness;
    public final float alpha;

    public HSLColor(float hue, float saturation, float lightness, float alpha) {
        assert (0 <= hue && hue <= 360F);
        assert (0 <= saturation && saturation <= 1f);
        assert (0 <= lightness && lightness <= 1f);
        assert (0 <= alpha && alpha <= 1f);
        this.hue = hue;
        this.saturation = saturation;
        this.lightness = lightness;
        this.alpha = alpha;
    }

    public HSLColor toHSL() {
        return this;
    }

    private float hue2rgb(float p, float q, float t) {
        float tprime;

        if (t < 0) tprime = t + 1f;
        else if (t > 1f) tprime = t - 1f;
        else tprime = t;

        if (tprime < 1f / 6f) return p + (q - p) * 6f * tprime;
        else if (tprime < 1f / 2f) return q;
        else if (tprime < 2f / 3f) return p + (q - p) * (2f / 3f - tprime) * 6f;
        else return p;
    }

    // credit to https://github.com/mjackson/mjijackson.github.com/blob/master/2008/02/rgb-to-hsl-and-rgb-to-hsv-color-model-conversion-algorithms-in-javascript.txt
    public RGBColor toRGB() {

        // assumes h is in th range [0,1] not [0,360) so must convert
        float h = hue / 360f;

        if (saturation == 0) {
            // achromatic
            return new RGBColor(
                    Math.round(lightness * 255),
                    Math.round(lightness * 255),
                    Math.round(lightness * 255),
                    Math.round(alpha * 255)
            );
        } else {

            float q;
            if (lightness < 0.5f) q = lightness * (1f + saturation);
            else q = lightness + saturation - lightness * saturation;

            float p = 2f * lightness - q;
            float r = hue2rgb(p, q, h + 1f / 3f);
            float g = hue2rgb(p, q, h);
            float b = hue2rgb(p, q, h - 1f / 3f);
            return new RGBColor(Math.round(r * 255), Math.round(g * 255), Math.round(b * 255), 255);
        }
    }
}
