package com.sksamuel.scrimage.nio;

import com.sksamuel.scrimage.ImageParseException;
import com.sksamuel.scrimage.ImmutableImage;

import java.awt.Rectangle;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Utilites for reading of an Image to an array of bytes in a specified format.
 */
public class ImageReaders {

   private static List<ImageReader> readers = detectReaders();

   private static List<ImageReader> detectReaders() {
      return Arrays.asList(
         new ImageIOReader(),
         new PngReader(),
         new OpenGifReader()
      );
   }

   public static ImmutableImage read(ImageSource source, Rectangle rectangle) throws IOException {
      Optional<ImmutableImage> image = Optional.empty();
      for (ImageReader reader : readers) {
         if (!image.isPresent()) {
            try {
               image = Optional.ofNullable(reader.read(source.open(), rectangle));
            } catch (Exception ignored) {
            }
         }
      }
      return image.orElseThrow(ImageParseException::new);
   }
}

