package com.sksamuel.scrimage.composite;

import com.sksamuel.scrimage.AwtImage;
import thirdparty.romainguy.BlendComposite;
import thirdparty.romainguy.BlendingMode;

import java.awt.*;
import java.awt.image.BufferedImage;

class BlenderComposite implements Composite {

    private final BlendingMode mode;
    private final double alpha;

    BlenderComposite(BlendingMode mode, double alpha) {
        this.mode = mode;
        this.alpha = alpha;
    }

    /**
     * BlendComposite reads the raw rasters handed to it by Java2D as packed,
     * non-premultiplied INT_ARGB. Any other type either throws (byte / ushort
     * backed images such as the TYPE_3BYTE_BGR and TYPE_4BYTE_ABGR images the
     * loader returns for JPEGs and PNGs) or is silently misread (no alpha byte in
     * TYPE_INT_RGB / TYPE_INT_BGR makes the blend a no-op, TYPE_INT_BGR swaps red
     * and blue, premultiplied samples are treated as straight colour).
     * <p>
     * So both images are presented to the composite as TYPE_INT_ARGB: the overlay
     * is converted, and a non-ARGB source is blended in an ARGB working copy whose
     * result is written back, keeping the source's own image type. When both are
     * already TYPE_INT_ARGB nothing is converted.
     */
    @Override
    public void apply(AwtImage src, AwtImage overlay) {
        BufferedImage dst = src.awt();
        BufferedImage over = toArgb(overlay.awt());
        boolean direct = dst.getType() == BufferedImage.TYPE_INT_ARGB;
        BufferedImage target = direct ? dst : toArgb(dst);
        Graphics2D g2 = target.createGraphics();
        try {
            g2.setComposite(BlendComposite.getInstance(mode, (float) alpha));
            g2.drawImage(over, 0, 0, null);
        } finally {
            g2.dispose();
        }
        if (!direct) {
            int w = dst.getWidth();
            int h = dst.getHeight();
            dst.setRGB(0, 0, w, h, target.getRGB(0, 0, w, h, null, 0, w), 0, w);
        }
    }

    private static BufferedImage toArgb(BufferedImage image) {
        if (image.getType() == BufferedImage.TYPE_INT_ARGB) return image;
        int w = image.getWidth();
        int h = image.getHeight();
        BufferedImage argb = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        argb.setRGB(0, 0, w, h, image.getRGB(0, 0, w, h, null, 0, w), 0, w);
        return argb;
    }
}
