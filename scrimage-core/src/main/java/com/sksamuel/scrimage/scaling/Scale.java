package com.sksamuel.scrimage.scaling;

import java.awt.image.BufferedImage;

public interface Scale {
    BufferedImage scale(BufferedImage in, int w, int h);
}
