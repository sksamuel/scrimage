package com.sksamuel.scrimage.nio;

import com.sksamuel.scrimage.AwtImage;
import com.sksamuel.scrimage.metadata.ImageMetadata;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class WriteContext {

   private final ImageWriter writer;
   private final AwtImage image;
   private final ImageMetadata metadata;

   public WriteContext(ImageWriter writer, AwtImage image, ImageMetadata metadata) {
      this.writer = writer;
      this.image = image;
      this.metadata = metadata;
   }

   public byte[] bytes() throws IOException {
      ByteArrayOutputStream bos = new ByteArrayOutputStream(estimatedSize());
      writer.write(image, metadata, bos);
      return bos.toByteArray();
   }

   /**
    * Estimates the encoded size so the buffer does not have to grow from the
    * default 32-byte capacity through repeated array copies. One byte per pixel
    * is a reasonable guess for compressed formats; the result is clamped to
    * keep the initial allocation bounded for very small or very large images.
    */
   private int estimatedSize() {
      long pixels = (long) image.width * image.height;
      return (int) Math.max(1024, Math.min(pixels, 8 * 1024 * 1024));
   }

   public ByteArrayInputStream stream() throws IOException {
      return new ByteArrayInputStream(bytes());
   }

   public Path write(String path) throws IOException {
      return write(Paths.get(path));
   }

   public File write(File file) throws IOException {
      write(file.toPath());
      return file;
   }

   public Path write(Path path) throws IOException {
      try (OutputStream out = Files.newOutputStream(path)) {
         writer.write(image, metadata, out);
      }
      return path;
   }

   public void write(OutputStream out) throws IOException {
      writer.write(image, metadata, out);
   }
}
