package com.sksamuel.scrimage.webp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CWebpHandler extends WebpHandler {

   private static final Path binary;

   static {
      try {
         // write out binary to a location we can execute it from
         binary = createPlaceholder("cwebp");
         installCWebp();
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   /**
    * Scrimage includes the latest cwebp binary. Alternatively, you can provide another version by
    * placing it on the classpath at `webp_binaries`, eg, /webp_binaries/cwebp.
    * <p>
    * Binaries can be downloaded here:
    * https://storage.googleapis.com/downloads.webmproject.org/releases/webp/index.html
    * <p>
    * This method is executed automatically but can also be invoked manually to check
    * for any installation errors.
    */
   public static void installCWebp() throws IOException {
      installBinary(binary, getBinaryPaths("cwebp"));
   }

   public byte[] convert(byte[] bytes,
                         int m,
                         int q,
                         int z,
                         boolean lossless,
                         boolean withoutAlpha) throws IOException {
      Path input = Files.createTempFile("input", "webp").toAbsolutePath();
      Path output = Files.createTempFile("to_webp", "webp").toAbsolutePath();
      try {
         Files.write(input, bytes, StandardOpenOption.CREATE);
         convert(input, output, m, q, z, lossless, withoutAlpha);
         return Files.readAllBytes(output);
      } finally {
         try {
            input.toFile().delete();
         } catch (Exception e) {
         }
         try {
            output.toFile().delete();
         } catch (Exception e) {
         }
      }
   }

   private void convert(Path input,
                        Path target,
                        int m,
                        int q,
                        int z,
                        boolean lossless,
                        boolean withoutAlpha) throws IOException {

      Path stdout = Files.createTempFile("stdout", "webp");
      List<String> commands = new ArrayList<>();
      commands.add(binary.toAbsolutePath().toString());
      if (m >= 0) {
         commands.add("-m");
         commands.add(m + "");
      }
      if (q >= 0) {
         commands.add("-q");
         commands.add(q + "");
      }
      if (z >= 0) {
         commands.add("-z");
         commands.add(z + "");
      }
      if (lossless) {
         commands.add("-lossless");
      }
      if (withoutAlpha) {
         commands.add("-noalpha");
      }
      commands.add(input.toAbsolutePath().toString());
      commands.add("-o");
      commands.add(target.toAbsolutePath().toString());

      ProcessBuilder builder = new ProcessBuilder(commands);
      builder.redirectErrorStream(true);
      builder.redirectOutput(stdout.toFile());

      Process process = builder.start();
      try {
         process.waitFor(5, TimeUnit.MINUTES);
         int exitStatus = process.exitValue();
         if (exitStatus != 0) {
            List<String> error = Files.readAllLines(stdout);
            throw new IOException(error.toString());
         }
      } catch (InterruptedException e) {
         throw new IOException(e);
      } finally {
         process.destroy();
         stdout.toFile().delete();
      }
   }
}
