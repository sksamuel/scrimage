package com.sksamuel.scrimage.nio;

import com.sksamuel.scrimage.AwtImage;
import com.sksamuel.scrimage.metadata.ImageMetadata;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.stream.ImageOutputStream;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

abstract class TwelveMonkeysWriter implements ImageWriter {

    public abstract String format();

    /**
     * The {@link BufferedImage} types that the TwelveMonkeys encoder for {@link #format()} cannot
     * encode correctly: it either throws, or writes a file that no reader can decode. Images of
     * these types are redrawn into {@code TYPE_INT_RGB} (or {@code TYPE_INT_ARGB} if the colour
     * model has alpha) before encoding, see {@link #normalise(AwtImage)}.
     * <p>
     * Types that the encoder handles natively (for example 1-bit or 16-bit grayscale TIFF) must
     * not be listed, so that their bit depth is preserved on disk.
     */
    protected Set<Integer> unsupportedTypes() {
        return Collections.emptySet();
    }

    /**
     * Returns the image unchanged if its type is encodable by this writer, otherwise a copy
     * drawn into {@code TYPE_INT_RGB} ({@code TYPE_INT_ARGB} when the colour model has alpha).
     */
    protected AwtImage normalise(AwtImage image) {
        if (!unsupportedTypes().contains(image.awt().getType())) {
            return image;
        }
        int target = image.awt().getColorModel().hasAlpha() ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB;
        BufferedImage copy = new BufferedImage(image.width, image.height, target);
        Graphics2D g2 = copy.createGraphics();
        try {
            g2.drawImage(image.awt(), 0, 0, null);
        } finally {
            g2.dispose();
        }
        return new AwtImage(copy);
    }

    @Override
    public void write(AwtImage image, ImageMetadata metadata, OutputStream out) throws IOException {
        Iterator<javax.imageio.ImageWriter> writers = ImageIO.getImageWritersByFormatName(format());
        if (!writers.hasNext()) {
            throw new IOException(
               "No ImageIO encoder is available for format '" + format() + "'. " +
                  "Some formats (such as PCX and SGI) are read-only: the TwelveMonkeys plugin provides a decoder but no encoder.");
        }
        javax.imageio.ImageWriter writer = writers.next();
        try (ImageOutputStream ios = ImageIO.createImageOutputStream(out)) {
            ImageWriteParam params = writer.getDefaultWriteParam();
            writer.setOutput(ios);
            writer.write(null, new IIOImage(normalise(image).awt(), null, null), params);
        } finally {
            writer.dispose();
        }
    }
}
