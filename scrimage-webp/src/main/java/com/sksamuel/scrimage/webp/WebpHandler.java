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

   private static final Logger logger = LoggerFactory.getLogger(WebpHandler.class);

   protected static Path createPlaceholder(String name) throws IOException {
      return Files.createTempFile(name, "binary");
   }

   protected static void installBinary(Path output, String... sources) throws IOException {
      logger.info("Installing binary at " + output);
      for (String source : sources) {
         logger.debug("Trying source from " + source);
         InputStream in = WebpHandler.class.getResourceAsStream(source);
         if (in != null) {
            logger.debug("Source detected " + source);
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

   /**
    * Returns the search paths to locate the webp binaries for the given binary.
    * Looks inside
    */
   protected static String[] getBinaryPaths(String binaryName) {
      if (SystemUtils.IS_OS_WINDOWS) {
         return new String[]{
            "/webp_binaries/" + binaryName,
            "/webp_binaries/" + binaryName + ".exe",
            // typo from previous versions must be left in
            "/webp_binaries/window/" + binaryName,
            "/webp_binaries/window/" + binaryName + ".exe",
            "/webp_binaries/windows/" + binaryName,
            "/webp_binaries/windows/" + binaryName + ".exe",
            "/dist_webp_binaries/libwebp-1.3.0-windows-x64/bin/" + binaryName,
            "/dist_webp_binaries/libwebp-1.3.0-windows-x64/bin/" + binaryName + ".exe",
         };
      } else if (SystemUtils.IS_OS_MAC) {
         return new String[]{
            "/webp_binaries/" + binaryName,
            "/webp_binaries/mac/" + binaryName,
            "/dist_webp_binaries/libwebp-1.3.0-mac-x86-64/bin/" + binaryName,
         };
      } else {
         return new String[]{
            "/webp_binaries/" + binaryName,
            "/webp_binaries/linux/" + binaryName,
            "/dist_webp_binaries/libwebp-1.3.0-linux-x86-64/bin/" + binaryName,
         };
      }
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
