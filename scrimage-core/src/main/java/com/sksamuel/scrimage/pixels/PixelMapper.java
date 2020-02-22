package com.sksamuel.scrimage.pixels;

@FunctionalInterface
public interface PixelMapper {
    Pixel map(int x, int y, Pixel p);
}
