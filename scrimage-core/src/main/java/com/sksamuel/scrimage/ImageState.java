package com.sksamuel.scrimage;

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


}
