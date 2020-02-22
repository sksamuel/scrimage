package com.sksamuel.scrimage.color;

class Grayscale implements Color {

    public final int gray;
    public final int alpha;

    public Grayscale(int gray, int alpha) {
        this.gray = gray;
        this.alpha = alpha;
    }


    public RGBColor toRGB() {
        return new RGBColor(gray, gray, gray, alpha);
    }
}