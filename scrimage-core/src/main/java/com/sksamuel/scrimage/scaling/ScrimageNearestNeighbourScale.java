package com.sksamuel.scrimage.scaling;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class ScrimageNearestNeighbourScale implements Scale {

    @Override
    public BufferedImage scale(BufferedImage in, int w, int h) {

        BufferedImage out = new BufferedImage(w, h, in.getType());
        int[] pixels = ((DataBufferInt) in.getRaster().getDataBuffer()).getData();
        int[] newpixels = ((DataBufferInt) out.getRaster().getDataBuffer()).getData();

        int n = 0;
        double k = 0d;
        int ow = in.getWidth();
        double xr = in.getWidth() / (double) w;
        double yr = in.getHeight() / (double) h;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                newpixels[n] = pixels[(int) k];
                k = k + xr;
                n = n + 1;
            }
            k = ow * (int) (y * yr);
        }
        return out;
  }
}