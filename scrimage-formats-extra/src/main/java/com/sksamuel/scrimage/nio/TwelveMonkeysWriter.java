package com.sksamuel.scrimage.nio;

import com.sksamuel.scrimage.AwtImage;
import com.sksamuel.scrimage.ImageWriter;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.stream.ImageOutputStream;
import java.io.IOException;
import java.io.OutputStream;

abstract class TwelveMonkeysWriter implements ImageWriter {

    public abstract String format();

    @Override
    public void write(AwtImage image, OutputStream out) throws IOException {
        javax.imageio.ImageWriter writer = ImageIO.getImageWritersByFormatName(format()).next();
        ImageOutputStream ios = ImageIO.createImageOutputStream(out);
        ImageWriteParam params = writer.getDefaultWriteParam();
        writer.setOutput(ios);
        writer.write(null, new IIOImage(image.awt(), null, null), params);
        ios.close();
        writer.dispose();
    }
}
