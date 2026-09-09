package com.sksamuel.scrimage.nio;

import com.sksamuel.scrimage.AwtImage;
import com.sksamuel.scrimage.metadata.ImageMetadata;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.stream.ImageOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

abstract class TwelveMonkeysWriter implements ImageWriter {

    public abstract String format();

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
            writer.write(null, new IIOImage(image.awt(), null, null), params);
        } finally {
            writer.dispose();
        }
    }
}
