package com.sksamuel.scrimage.nio;

import com.sksamuel.scrimage.AwtImage;
import com.sksamuel.scrimage.metadata.ImageMetadata;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Set;

public class BmpWriter extends TwelveMonkeysWriter {

    // The TwelveMonkeys BMP encoder rejects 16-bit grayscale ("Image can not be encoded with
    // compression type BI_RGB and 16 bits per pixel"). 1-bit, indexed and 16-bit packed RGB
    // are encoded natively.
    private static final Set<Integer> UNSUPPORTED_TYPES = Collections.singleton(BufferedImage.TYPE_USHORT_GRAY);

    @Override
    public String format() {
        return "bmp";
    }

    @Override
    protected Set<Integer> unsupportedTypes() {
        return UNSUPPORTED_TYPES;
    }

    // The TwelveMonkeys BMP encoder rejects 32bpp input ("Image can not be encoded with
    // compression type BI_RGB and 32 bits per pixel"), so any image with an alpha channel —
    // including scrimage's default TYPE_INT_ARGB — must be flattened to a non-alpha type
    // first. Composites onto white, matching JpegWriter's handling of the same limitation.
    @Override
    public void write(AwtImage image, ImageMetadata metadata, OutputStream out) throws IOException {
        if (image.awt().getColorModel().hasAlpha()) {
            BufferedImage noAlpha = new BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = noAlpha.createGraphics();
            try {
                g2.setColor(Color.WHITE);
                g2.fillRect(0, 0, image.width, image.height);
                g2.drawImage(image.awt(), 0, 0, null);
            } finally {
                g2.dispose();
            }
            super.write(new AwtImage(noAlpha), metadata, out);
        } else {
            super.write(image, metadata, out);
        }
    }
}
