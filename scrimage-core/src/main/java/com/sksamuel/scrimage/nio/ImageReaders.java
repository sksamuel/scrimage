package com.sksamuel.scrimage.nio;

import com.sksamuel.scrimage.ImageParseException;
import com.sksamuel.scrimage.ImmutableImage;

import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Utilities for reading of an Image to an array of bytes in a specified format.
 */
public class ImageReaders {

   private static final List<ImageReader> readers = detectReaders();

   private static List<ImageReader> detectReaders() {
      try {
         return StreamSupport.stream(ServiceLoader.load(ImageReader.class).spliterator(), false).collect(Collectors.toList());
      } catch (Throwable e) {
         throw new RuntimeException("Could not load service classes for image reader" + e);
      }
   }

   public static ImmutableImage read(ImageSource source, Rectangle rectangle) throws IOException {
      List<Throwable> errors = new ArrayList<>();
      Optional<ImmutableImage> image = Optional.empty();
      for (ImageReader reader : readers) {
         if (!image.isPresent()) {
            try {
               image = Optional.ofNullable(reader.read(source.read(), rectangle));
            } catch (Exception e) {
               errors.add(e);
            }
         }
      }
      return image.orElseThrow(() -> {
         if (errors.isEmpty())
            return new ImageParseException();
         else
            return new ImageParseException(errors);
      });
   }
}

