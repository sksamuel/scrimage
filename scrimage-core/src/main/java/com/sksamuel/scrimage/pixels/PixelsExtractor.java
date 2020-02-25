package com.sksamuel.scrimage.pixels;

import java.awt.Rectangle;

public interface PixelsExtractor {
    Pixel[] apply(Rectangle rectangle);
}
