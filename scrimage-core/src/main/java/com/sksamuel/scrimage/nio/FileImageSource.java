package com.sksamuel.scrimage.nio;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileImageSource implements ImageSource {

   private final Path path;

   public FileImageSource(File file) {
      this.path = file.toPath();
   }

   public FileImageSource(Path path) {
      this.path = path;
   }

   @Override
   public byte[] read() throws IOException {
      return Files.readAllBytes(path);
   }
}
