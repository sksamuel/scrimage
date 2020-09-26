package com.sksamuel.scrimage.webp;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class WebpHandler {

   private static final Path binary;

   static {
      try {
         // write out binary to a location we can execute it from
         binary = createBinaryFile();
         installDWebp();
      } catch (IOException | InterruptedException e) {
         throw new RuntimeException(e);
      }
   }

   private static Path createBinaryFile() throws IOException {
      return Files.createTempFile("webp", "binary");
   }

   /**
    * Scrimage does not ship with the dwebp binary. You must place it on the classpath,
    * under a directory called `webp_binaries`.
    * <p>
    * In other orders, this method will attempt to locate the dwebp binary at
    * `/webp_binaries/dwebp`
    * <p>
    * The binary can be downloaded here:
    * https://storage.googleapis.com/downloads.webmproject.org/releases/webp/index.html
    */
   private static void installDWebp() throws IOException, InterruptedException {
      InputStream in = WebpHandler.class.getResourceAsStream("/webp_binaries/dwebp");
      Files.copy(in, binary, StandardCopyOption.REPLACE_EXISTING);
      in.close();
      setExecutable();
   }

   private static boolean setExecutable() throws IOException, InterruptedException {
      return new ProcessBuilder("chmod", "+x", binary.toAbsolutePath().toString())
         .start()
         .waitFor(30, TimeUnit.SECONDS);
   }

   public byte[] convert(byte[] bytes) throws IOException, InterruptedException {
      Path path = Files.createTempFile("input", "webp").toAbsolutePath();
      Files.write(path, bytes, StandardOpenOption.CREATE);
      try {
         return convert(path);
      } finally {
         path.toFile().delete();
      }
   }

   public byte[] convert(Path source) throws IOException, InterruptedException {
      Path target = Files.createTempFile("webp_conversion", "png").toAbsolutePath();
      try {
         convert(source, target);
         return Files.readAllBytes(target);
      } finally {
         target.toFile().delete();
      }
   }

   private void convert(Path input, Path target) throws IOException, InterruptedException {

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
      process.waitFor(5, TimeUnit.MINUTES);
      int exitStatus = process.exitValue();
      if (exitStatus != 0) {
         List<String> error = Files.readAllLines(stdout);
         throw new IOException(error.toString());
      }
   }
}
