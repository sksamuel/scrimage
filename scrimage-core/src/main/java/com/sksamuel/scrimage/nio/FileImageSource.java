package com.sksamuel.scrimage.nio;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
   public InputStream open() throws IOException {
      return Files.newInputStream(path);
   }
}
