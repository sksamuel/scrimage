package com.sksamuel.scrimage;

@FunctionalInterface
public interface PixelMapper {
    Pixel map(int x, int y, Pixel p);
}
