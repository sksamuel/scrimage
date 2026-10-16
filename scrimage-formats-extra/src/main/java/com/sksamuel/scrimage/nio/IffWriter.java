package com.sksamuel.scrimage.nio;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class IffWriter extends TwelveMonkeysWriter {

    // The TwelveMonkeys IFF encoder throws ArrayIndexOutOfBoundsException for 16-bit grayscale
    // and writes 16-bit packed RGB as a file no reader can decode. 1-bit, indexed and 8-bit
    // grayscale are encoded natively.
    private static final Set<Integer> UNSUPPORTED_TYPES = new HashSet<>(Arrays.asList(
        BufferedImage.TYPE_USHORT_GRAY,
        BufferedImage.TYPE_USHORT_565_RGB,
        BufferedImage.TYPE_USHORT_555_RGB
    ));

    @Override
    public String format() {
        return "iff";
    }

    @Override
    protected Set<Integer> unsupportedTypes() {
        return UNSUPPORTED_TYPES;
    }
}
