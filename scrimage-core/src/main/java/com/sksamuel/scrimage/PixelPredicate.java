package com.sksamuel.scrimage;

public interface PixelPredicate {
    boolean test(int x, int y, Pixel p);
}

interface PixelFunction {
    boolean apply(int x, int y, Pixel p);
}