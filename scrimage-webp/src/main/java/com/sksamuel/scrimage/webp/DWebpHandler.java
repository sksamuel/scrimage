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
         // try to get the binary path from the system property
         Path pathFromProperty = getPathFromProperty("dwebp");
         if (pathFromProperty != null) {
            binary = pathFromProperty;
         } else {
            // write out binary to a location we can execute it from
            binary = createPlaceholder("dwebp");
            installDWebp();
         }
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
    *
    * This method is executed automatically but can also be invoked manually to check
    * for any installation errors.
    */
   public static void installDWebp() throws IOException {
      installBinary(binary, getBinaryPaths("dwebp"));
   }

   public byte[] convert(byte[] bytes) throws IOException {
      Path input = Files.createTempFile("input", "webp").toAbsolutePath();
      Files.write(input, bytes, StandardOpenOption.CREATE);
      try {
         return convert(input);
      } finally {
         input.toFile().delete();
      }
   }

   public byte[] convert(Path source) throws IOException {
      Path output = Files.createTempFile("from_webp", "png").toAbsolutePath();
      try {
         convert(source, output);
         return Files.readAllBytes(output);
      } finally {
         output.toFile().delete();
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
         // waitFor(timeout, unit) returns false if the process is still
         // running when the timeout expires. The previous code ignored
         // the return value and called exitValue() unconditionally,
         // which throws IllegalThreadStateException if the process
         // hadn't exited — masking the real failure (a hang).
         boolean finished = process.waitFor(5, TimeUnit.MINUTES);
         if (!finished) {
            throw new IOException("dwebp timed out after 5 minutes");
         }
         int exitStatus = process.exitValue();
         if (exitStatus != 0) {
            List<String> error = Files.readAllLines(stdout);
            throw new IOException(error.toString());
         }
      } catch (InterruptedException e) {
         Thread.currentThread().interrupt();
         throw new IOException(e);
      } finally {
         process.destroy();
         stdout.toFile().delete();
      }

   }
}
