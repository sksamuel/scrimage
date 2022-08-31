package com.sksamuel.scrimage.webp;

import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

abstract class WebpHandler {

   private static Logger logger = LoggerFactory.getLogger(WebpHandler.class);

   protected static Path createPlaceholder(String name) throws IOException {
      return Files.createTempFile(name, "binary");
   }

   protected static void installBinary(Path output, String... sources) throws IOException {
      logger.info("Installing binary at " + output);
      for (String source : sources) {
         logger.info("Trying source from " + source);
         InputStream in = WebpHandler.class.getResourceAsStream(source);
         if (in != null) {
            logger.info("Source detected " + source);
            Files.copy(in, output, StandardCopyOption.REPLACE_EXISTING);
            in.close();

            if (!SystemUtils.IS_OS_WINDOWS) {
               logger.info("Setting executable " + output);
               setExecutable(output);
            }
            return;
         }
      }
      throw new IOException("Could not locate webp binary at " + Arrays.toString(sources));
   }

   protected static String[] getBinaryPath(String binaryName) {
      String os = "linux";

      if (SystemUtils.IS_OS_WINDOWS) {
         os = "window";
      } else if (SystemUtils.IS_OS_MAC) {
         os = "mac";
      }

      return new String[]{
         "/webp_binaries/" + binaryName,
         "/webp_binaries/" + os + "/" + binaryName,
         "/dist_webp_binaries/" + os + "/" + binaryName,
      };
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
