package com.sksamuel.scrimage;

import java.util.Arrays;

public class ImageState {

    private final int width;
    private final int height;

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Pixel[] getPixels() {
        return pixels;
    }

    private final Pixel[] pixels;

    public ImageState(int width, int height, Pixel[] pixels) {
        this.width = width;
        this.height = height;
        this.pixels = pixels;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImageState that = (ImageState) o;

        if (width != that.width) return false;
        if (height != that.height) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(pixels, that.pixels);
    }

    @Override
    public int hashCode() {
        int result = width;
        result = 31 * result + height;
        result = 31 * result + Arrays.hashCode(pixels);
        return result;
    }
}
