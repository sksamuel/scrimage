package com.sksamuel.scrimage.nio;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PictWriter extends TwelveMonkeysWriter {

    // The TwelveMonkeys PICT encoder throws ArrayIndexOutOfBoundsException for 1-bit images and
    // IIOException("DataBuffer type 1 not supported") for every TYPE_USHORT_* type. Indexed and
    // 8-bit grayscale are encoded natively.
    private static final Set<Integer> UNSUPPORTED_TYPES = new HashSet<>(Arrays.asList(
        BufferedImage.TYPE_BYTE_BINARY,
        BufferedImage.TYPE_USHORT_GRAY,
        BufferedImage.TYPE_USHORT_565_RGB,
        BufferedImage.TYPE_USHORT_555_RGB
    ));

    @Override
    public String format() {
        return "Pict";
    }

    @Override
    protected Set<Integer> unsupportedTypes() {
        return UNSUPPORTED_TYPES;
    }
}
