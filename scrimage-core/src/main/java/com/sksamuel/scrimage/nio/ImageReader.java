package com.sksamuel.scrimage.nio;

import com.sksamuel.scrimage.ImageParseException;
import com.sksamuel.scrimage.ImmutableImage;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Utilites for reading of an Image to an array of bytes in a specified format.
 */
public class ImageReader {

   private static List<Reader> readers = detectReaders();

   private static List<Reader> detectReaders() {
      return Arrays.asList(
         new JavaImageIOReader(),
         new JavaImageIO2Reader(),
         new PngReader(),
         new OpenGifReader()
      );
   }

   public static ImmutableImage fromFile(File file) throws IOException {
      return fromFile(file, ImmutableImage.CANONICAL_DATA_TYPE);
   }

   public static ImmutableImage fromFile(File file, int type) throws IOException {
      return fromPath(file.toPath(), type);
   }

   public static ImmutableImage fromPath(Path path) throws IOException {
      return fromPath(path, ImmutableImage.CANONICAL_DATA_TYPE);
   }

   public static ImmutableImage fromPath(Path path, int type) throws IOException {
      try (InputStream stream = Files.newInputStream(path)) {
         return fromStream(stream, type);
      }
   }

   public static ImmutableImage fromStream(InputStream in) throws IOException {
      return fromStream(in, ImmutableImage.CANONICAL_DATA_TYPE);
   }

   public static ImmutableImage fromStream(InputStream in, int type) throws IOException {
      byte[] bytes = IOUtils.toByteArray(in);
      return fromBytes(bytes, type);
   }

   public static ImmutableImage fromBytes(byte[] bytes, int type) {
      Optional<ImmutableImage> image = Optional.empty();
      for (Reader reader : readers) {
         if (!image.isPresent()) {
            try {
               image = Optional.ofNullable(reader.fromBytes(bytes, type));
            } catch (Exception ignored) {
            }
         }
      }
      return image.orElseThrow(ImageParseException::new);
   }
}

interface Reader {
   ImmutableImage fromBytes(byte[] bytes, int type) throws IOException;
}
