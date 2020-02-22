package com.sksamuel.scrimage.format;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import static java.util.Arrays.copyOf;
import static java.util.Objects.deepEquals;

public class FormatDetector {

    // inspired by http://stackoverflow.com/questions/22534833/scala-detect-mimetype-of-an-arraybyte-image
    private static byte[] gif = new byte[]{'G', 'I', 'F', '8'};
    private static byte[] png = new byte[]{(byte) 0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A};
    private static byte[] jpeg1 = new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xEE};
    private static byte[] jpeg2 = new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF};

    public static Optional<Format> detect(InputStream in) throws IOException {
        byte[] bytes = new byte[8];
        in.read(bytes, 0, 8);
        return detect(bytes);
    }

    public static Optional<Format> detect(byte[] bytes) {
        if (deepEquals(copyOf(bytes, gif.length), gif)) return Optional.of(Format.GIF);
        if (deepEquals(copyOf(bytes, png.length), png)) return Optional.of(Format.PNG);
        if (deepEquals(copyOf(bytes, jpeg1.length), jpeg1)) return Optional.of(Format.JPEG);
        if (deepEquals(copyOf(bytes, jpeg2.length), jpeg2)) return Optional.of(Format.JPEG);
        return Optional.empty();
    }
}