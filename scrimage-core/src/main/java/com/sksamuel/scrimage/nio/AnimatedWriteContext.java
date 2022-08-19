package com.sksamuel.scrimage.nio;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AnimatedWriteContext {

   private final AnimatedImageWriter writer;
   private final AnimatedGif gif;

   public AnimatedWriteContext(AnimatedImageWriter writer, AnimatedGif gif) {
      this.writer = writer;
      this.gif = gif;
   }

   public byte[] bytes() throws IOException {
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      writer.write(gif, bos);
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
         writer.write(gif, out);
      }
      return path;
   }

   public void write(OutputStream out) throws IOException {
      writer.write(gif, out);
   }
}
