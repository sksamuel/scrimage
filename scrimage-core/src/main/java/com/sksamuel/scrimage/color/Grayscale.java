package com.sksamuel.scrimage.color;

import java.util.Objects;

public class Grayscale implements Color {

    public final int gray;
    public final int alpha;

    public Grayscale(int gray) {
        this(gray, 0);
    }

    public Grayscale(int gray, int alpha) {
        this.gray = gray;
        this.alpha = alpha;
    }

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
        return Objects.hash(gray, alpha);
    }
}