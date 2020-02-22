package com.sksamuel.scrimage.pixels;

import com.sksamuel.scrimage.Area;

public interface PixelsExtractor {
    Pixel[] apply(Area area);
}
