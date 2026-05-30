package com.sksamuel.scrimage.color;

public class Grayscale implements Color {

    public final int gray;
    public final int alpha;

    public Grayscale(int gray) {
        this(gray, 255);
    }

    public Grayscale(int gray, int alpha) {

        // Validate at runtime rather than via assert (disabled by default in
        // production JVMs). An out-of-range value would otherwise be stored
        // unchecked and only fail later inside toRGB() when wrapped in an
        // RGBColor, turning a clear construction-site error into a confusing
        // deferred failure. Mirrors the RGBColor validation pattern.
        requireInRange("gray", gray);
        requireInRange("alpha", alpha);

        this.gray = gray;
        this.alpha = alpha;
    }

    private static void requireInRange(String component, int value) {
        if (value < 0 || value > 255)
            throw new IllegalArgumentException(
                "Grayscale " + component + " component must be in [0, 255] but was " + value);
    }

    @Override
    public RGBColor toRGB() {
        return new RGBColor(gray, gray, gray, alpha);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Grayscale grayscale = (Grayscale) o;
        return gray == grayscale.gray &&
                alpha == grayscale.alpha;
    }

    @Override
    public int hashCode() {
        return 31 * gray + alpha;
    }
}