package com.sksamuel.scrimage.webp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DWebpHandler extends WebpHandler {

   private static final Path binary;

   static {
      try {
         // write out binary to a location we can execute it from
         binary = createPlaceholder("dwebp");
         installDWebp();
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   /**
    * Scrimage includes the latest dwebp binary. Alternatively, you can provide another version by
    * placing it on the classpath at `webp_binaries`, eg, /webp_binaries/dwebp.
    * <p>
    * Binaries can be downloaded here:
    * https://storage.googleapis.com/downloads.webmproject.org/releases/webp/index.html
    */
   private static void installDWebp() throws IOException {
      installBinary(binary, getBinaryPath("dwebp"));
   }

   public byte[] convert(byte[] bytes) throws IOException {
      Path path = Files.createTempFile("input", "webp").toAbsolutePath();
      Files.write(path, bytes, StandardOpenOption.CREATE);
      try {
         return convert(path);
      } finally {
         path.toFile().delete();
      }
   }

   public byte[] convert(Path source) throws IOException {
      Path target = Files.createTempFile("from_webp", "png").toAbsolutePath();
      try {
         convert(source, target);
         return Files.readAllBytes(target);
      } finally {
         target.toFile().delete();
      }
   }

   private void convert(Path input, Path target) throws IOException {

      Path stdout = Files.createTempFile("stdout", "webp");
      ProcessBuilder builder = new ProcessBuilder(
         binary.toAbsolutePath().toString(),
         input.toAbsolutePath().toString(),
         "-o",
         target.toAbsolutePath().toString()
      );
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
      } catch (InterruptedException | IOException e) {
         throw new IOException(e);
      } finally {
         stdout.toFile().delete();
      }

   }
}
