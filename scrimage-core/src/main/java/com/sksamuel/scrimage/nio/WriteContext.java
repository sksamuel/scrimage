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
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      writer.write(image, metadata, bos);
      return bos.toByteArray();
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
