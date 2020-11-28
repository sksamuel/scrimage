package com.sksamuel.scrimage.webp;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.TimeUnit;

abstract class WebpHandler {

   protected static Path createPlaceholder(String name) throws IOException {
      return Files.createTempFile(name, "binary");
   }

   protected static void installBinary(String source, Path output) throws IOException {
      InputStream in = WebpHandler.class.getResourceAsStream(source);
      if (in == null)
         throw new IOException("Could not locate webp binary at " + source);
      Files.copy(in, output, StandardCopyOption.REPLACE_EXISTING);
      in.close();
      setExecutable(output);
   }

   private static boolean setExecutable(Path output) throws IOException {
      try {
         return new ProcessBuilder("chmod", "+x", output.toAbsolutePath().toString())
            .start()
            .waitFor(30, TimeUnit.SECONDS);
      } catch (InterruptedException e) {
         throw new IOException(e);
      }
   }
}
