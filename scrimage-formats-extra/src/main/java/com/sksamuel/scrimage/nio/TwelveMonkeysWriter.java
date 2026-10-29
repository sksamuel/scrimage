package com.sksamuel.scrimage.nio;

import com.sksamuel.scrimage.AwtImage;
import com.sksamuel.scrimage.metadata.ImageMetadata;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.stream.ImageOutputStream;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
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
        BufferedImage awt = image.awt();
        // Some TwelveMonkeys encoders (PNM, TGA, TIFF, PICT) read the backing DataBuffer
        // directly and ignore a raster's origin and parent stride, so a sub-image view
        // (BufferedImage.getSubimage) was written as the top-left of its PARENT image.
        // Copy such views into a standalone raster of the same colour model first.
        if (!mapsOneToOne(awt.getRaster())) {
            awt = standaloneCopy(awt);
        }
        if (!unsupportedTypes().contains(awt.getType())) {
            return awt == image.awt() ? image : new AwtImage(awt);
        }
        int target = awt.getColorModel().hasAlpha() ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB;
        BufferedImage copy = new BufferedImage(image.width, image.height, target);
        Graphics2D g2 = copy.createGraphics();
        try {
            g2.drawImage(awt, 0, 0, null);
        } finally {
            g2.dispose();
        }
        return new AwtImage(copy);
    }

    /**
     * Returns true if the raster covers its whole DataBuffer from the start, i.e. it is not a
     * view into a larger raster.
     */
    private static boolean mapsOneToOne(WritableRaster raster) {
        return raster.getParent() == null
            && raster.getSampleModelTranslateX() == 0
            && raster.getSampleModelTranslateY() == 0
            && raster.getSampleModel().getWidth() == raster.getWidth()
            && raster.getSampleModel().getHeight() == raster.getHeight()
            && raster.getDataBuffer().getOffset() == 0;
    }

    /**
     * Copies the image's samples into a fresh raster with the same colour model, so the type,
     * bit depth and palette are kept exactly.
     */
    private static BufferedImage standaloneCopy(BufferedImage image) {
        ColorModel cm = image.getColorModel();
        WritableRaster raster = image.getRaster().createCompatibleWritableRaster(image.getWidth(), image.getHeight());
        image.copyData(raster);
        return new BufferedImage(cm, raster, cm.isAlphaPremultiplied(), null);
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
