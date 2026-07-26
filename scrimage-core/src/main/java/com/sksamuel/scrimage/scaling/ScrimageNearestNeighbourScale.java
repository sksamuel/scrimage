package com.sksamuel.scrimage.scaling;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.Raster;

public class ScrimageNearestNeighbourScale implements Scale {

    @Override
    public BufferedImage scale(BufferedImage in, int w, int h) {

        // The fast path reads/writes the int[] backing store directly,
        // so it only works on int-buffer BufferedImages. Previously a
        // non-int buffer threw a ClassCastException from deep inside
        // this method with no useful context. ImmutableImage.scaleTo
        // guards this for its callers but the class is public so a
        // direct user can still land here.
        Raster raster = in.getRaster();
        DataBuffer inBuf = raster.getDataBuffer();
        if (!(inBuf instanceof DataBufferInt))
            throw new IllegalArgumentException(
                "ScrimageNearestNeighbourScale requires an int-buffer BufferedImage, got "
                    + inBuf.getClass().getSimpleName() + " (image type " + in.getType() + ")");

        DataBufferInt intBuf = (DataBufferInt) inBuf;
        int[] pixels = intBuf.getData();

        // Indexing the backing array directly is only valid when the raster
        // maps 1:1 onto the buffer. A sub-image view (BufferedImage.getSubimage)
        // shares the parent's buffer with a translated origin and the parent's
        // scanline stride, so raw indexing would read the wrong pixels.
        // Delegate those to the getRGB/setRGB based implementation, which
        // handles any raster layout.
        boolean mapsOneToOne = raster.getSampleModelTranslateX() == 0
            && raster.getSampleModelTranslateY() == 0
            && intBuf.getOffset() == 0
            && pixels.length == in.getWidth() * in.getHeight();
        if (!mapsOneToOne)
            return new AwtNearestNeighbourScale().scale(in, w, h);

        BufferedImage out = new BufferedImage(w, h, in.getType());
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
            k = ow * (int) ((y + 1) * yr);
        }
        return out;
  }
}