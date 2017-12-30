package com.sksamuel.scrimage;

public interface PixelPredicate {
    boolean test(int x, int y, Pixel p);
}

interface PixelFunction {
    void apply(int x, int y, Pixel p);
}