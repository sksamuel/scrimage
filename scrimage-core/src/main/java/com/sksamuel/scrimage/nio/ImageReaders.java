package com.sksamuel.scrimage.nio;

import com.sksamuel.scrimage.ImageParseException;
import com.sksamuel.scrimage.ImmutableImage;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Utilities for reading of an Image to an array of bytes in a specified format.
 */
public class ImageReaders {

   private static final List<ImageReader> defaultReaders = detectReaders();

   private static List<ImageReader> detectReaders() {
      return detectReaders(Thread.currentThread().getContextClassLoader());
   }

   private static List<ImageReader> detectReaders(ClassLoader classloader) {
      return StreamSupport.stream(ServiceLoader.load(ImageReader.class, classloader).spliterator(), false).collect(Collectors.toList());
   }

   public static ImmutableImage read(ImageSource source, Rectangle rectangle) throws IOException {
      return read(source, rectangle, defaultReaders);
   }

   public static ImmutableImage read(ImageSource source, Rectangle rectangle, ClassLoader classloader) throws IOException {
      return read(source, rectangle, classloader == null ? defaultReaders : detectReaders(classloader));
   }

   public static ImmutableImage read(ImageSource source, Rectangle rectangle, List<ImageReader> readers) throws IOException {
      List<Throwable> errors = new ArrayList<>();
      for (ImageReader reader : readers) {
         try {
            ImmutableImage image = reader.read(source.read(), rectangle);
            if (image == null) {
               errors.add(new IOException(reader + " failed"));
            } else {
               return image;
            }
         } catch (Exception e) {
            errors.add(new IOException(reader.toString() + " failed due to " + e.getMessage(), e));
         }
      }
      if (errors.isEmpty())
         throw new ImageParseException();
      else
         throw new ImageParseException(errors);
   }
}
