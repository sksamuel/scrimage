package com.sksamuel.scrimage.format;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Optional;

public class FormatDetector {

    // inspired by http://stackoverflow.com/questions/22534833/scala-detect-mimetype-of-an-arraybyte-image
    private static byte[] gif = new byte[]{'G', 'I', 'F', '8'};
    private static byte[] png = new byte[]{(byte) 0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A};
    private static byte[] jpeg1 = new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xEE};
    private static byte[] jpeg2 = new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF};
    // see webp spec https://developers.google.com/speed/webp/docs/riff_container
    private static byte[] webp1 = new byte[]{'R', 'I', 'F', 'F'};
    private static byte[] webp2 = new byte[]{'W', 'E', 'B', 'P'};

    public static Optional<Format> detect(InputStream in) throws IOException {
        // InputStream.read(byte[], int, int) is documented as "an attempt is
        // made to read up to len bytes" — it MAY return fewer. For slow
        // streams (network, decompressed) a single read commonly returns
        // less than 12 bytes, leaving the tail of `bytes` zero-filled. WEBP
        // signatures live at offset 8 ("WEBP" after RIFF + length) so a
        // partial read silently breaks WEBP detection. readNBytes blocks
        // until it has all the bytes (or EOF).
        byte[] bytes = in.readNBytes(12);
        return detect(bytes);
    }

    public static Optional<Format> detect(byte[] bytes) {
        if (startsWith(bytes, gif)) return Optional.of(Format.GIF);
        if (startsWith(bytes, png)) return Optional.of(Format.PNG);
        if (startsWith(bytes, jpeg1)) return Optional.of(Format.JPEG);
        if (startsWith(bytes, jpeg2)) return Optional.of(Format.JPEG);
        if (isWebp(bytes)) return Optional.of(Format.WEBP);
        return Optional.empty();
    }

    private static boolean isWebp(byte[] bytes) {
        return startsWith(bytes, webp1) &&
            bytes.length >= 12 &&
            Arrays.equals(bytes, 8, 12, webp2, 0, webp2.length);
    }

    // allocation-free prefix comparison; Arrays.copyOf + equals would copy the prefix per check
    private static boolean startsWith(byte[] bytes, byte[] prefix) {
        return bytes.length >= prefix.length &&
            Arrays.equals(bytes, 0, prefix.length, prefix, 0, prefix.length);
    }
}
