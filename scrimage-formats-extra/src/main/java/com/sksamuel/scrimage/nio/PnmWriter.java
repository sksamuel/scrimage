package com.sksamuel.scrimage.nio;

import com.sksamuel.scrimage.AwtImage;
import com.sksamuel.scrimage.metadata.ImageMetadata;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PnmWriter extends TwelveMonkeysWriter {

    // The TwelveMonkeys PNM encoder throws IllegalArgumentException("Unknown TupleType") for
    // indexed and 16-bit packed RGB images, and writes TYPE_BYTE_BINARY as a P4 bitmap with one
    // byte per pixel instead of packed bits, which reads back as an all-white image. 8-bit and
    // 16-bit grayscale are encoded natively (PGM).
    private static final Set<Integer> UNSUPPORTED_TYPES = new HashSet<>(Arrays.asList(
        BufferedImage.TYPE_BYTE_BINARY,
        BufferedImage.TYPE_BYTE_INDEXED,
        BufferedImage.TYPE_USHORT_565_RGB,
        BufferedImage.TYPE_USHORT_555_RGB
    ));

    @Override
    public String format() {
        return "pnm";
    }

    @Override
    protected Set<Integer> unsupportedTypes() {
        return UNSUPPORTED_TYPES;
    }

    // The PNM formats have no alpha channel and the TwelveMonkeys PNM encoder throws an
    // undeclared IllegalArgumentException ("Unknown TupleType for BufferedImage") for
    // images with one - including scrimage's default TYPE_INT_ARGB. Flatten alpha onto
    // white first, matching JpegWriter's handling of the same limitation.
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
