package com.sksamuel.scrimage.nio;

import com.sksamuel.scrimage.Image;
import com.sksamuel.scrimage.Image$;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Optional;

public class JavaImageIO2Reader implements Reader {

    private Optional<Image> tryLoad(ImageReader reader, byte[] bytes, int type) {
        try {
            reader.setInput(new ByteArrayInputStream(bytes), true, true);
            ImageReadParam params = reader.getDefaultReadParam();
            Iterator<ImageTypeSpecifier> imageTypes = reader.getImageTypes(0);
            while (imageTypes.hasNext()) {
                ImageTypeSpecifier imageTypeSpecifier = imageTypes.next();
                int bufferedImageType = imageTypeSpecifier.getBufferedImageType();
                if (bufferedImageType == BufferedImage.TYPE_BYTE_GRAY) {
                    params.setDestinationType(imageTypeSpecifier);
                }
            }
            BufferedImage bufferedImage = reader.read(0, params);
            return Optional.of(Image$.MODULE$.wrapAwt(bufferedImage));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Image fromBytes(byte[] bytes, int type) throws IOException {
        Iterator<ImageReader> readers = ImageIO.getImageReaders(new ByteArrayInputStream(bytes));
        while (readers.hasNext()) {
            Optional<Image> image = tryLoad(readers.next(), bytes, type);
            if (image.isPresent())
                return image.get();
        }
        return null;
    }
}
