package com.sksamuel.scrimage.nio;

import com.sksamuel.scrimage.ImmutableImage;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class ImageIOReader implements ImageReader {

   private final List<javax.imageio.ImageReader> readers;

   public ImageIOReader() {
      readers = Collections.emptyList();
   }

   public ImageIOReader(List<javax.imageio.ImageReader> readers) {
      this.readers = readers;
   }

   private Optional<ImmutableImage> tryLoad(javax.imageio.ImageReader reader, ImageInputStream iis, Rectangle rectangle) {
      try {
         reader.setInput(iis);

         ImageReadParam params = reader.getDefaultReadParam();
         Iterator<ImageTypeSpecifier> imageTypes = reader.getImageTypes(0);
         if (imageTypes.hasNext()) {
            ImageTypeSpecifier imageTypeSpecifier = imageTypes.next();
            int bufferedImageType = imageTypeSpecifier.getBufferedImageType();
            params.setDestinationType(imageTypeSpecifier);
         }
         if (rectangle != null) {
            params.setSourceRegion(rectangle);
         }

         BufferedImage bufferedImage = reader.read(0, params);
         return Optional.of(ImmutableImage.wrapAwt(bufferedImage));
      } catch (IOException | InvalidImageParameterException e) {
         return Optional.empty();
      }
   }

   @Override
   public ImmutableImage read(byte[] bytes, Rectangle rectangle) throws IOException {

      if (bytes == null)
         throw new IOException("bytes cannot be null");

      ImageInputStream iis = ImageIO.createImageInputStream(new ByteArrayInputStream(bytes));
      if (iis == null)
         throw new IOException("No ImageInputStream supported this image format");

      Iterator<javax.imageio.ImageReader> iter;
      if (readers.isEmpty())
         iter = ImageIO.getImageReaders(iis);
      else
         iter = readers.iterator();

      while (iter.hasNext()) {
         Optional<ImmutableImage> image = tryLoad(iter.next(), iis, rectangle);
         if (image.isPresent())
            return image.get();
      }

      throw new IOException("No ImageInputStream supported this image format; tried " + readers.size() + " readers; which were " + readers);
   }
}
