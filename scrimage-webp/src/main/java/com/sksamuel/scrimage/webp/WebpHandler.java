package com.sksamuel.scrimage.webp;

import org.apache.commons.lang3.SystemUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

abstract class WebpHandler {

   protected static Path createPlaceholder(String name) throws IOException {
      return Files.createTempFile(name, "binary");
   }

   protected static void installBinary(Path output, String... sources) throws IOException {
      for (String source : sources) {
         InputStream in = WebpHandler.class.getResourceAsStream(source);
         if (in != null) {
            Files.copy(in, output, StandardCopyOption.REPLACE_EXISTING);
            in.close();

            if(!SystemUtils.IS_OS_WINDOWS) {
               setExecutable(output);
            }
            return;
         }
      }
      throw new IOException("Could not locate webp binary at " + Arrays.toString(sources));
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
