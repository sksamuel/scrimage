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
         installDWebp();
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   /**
    * Scrimage does not ship with the cwebp binary. You must place it on the classpath,
    * under a directory called `webp_binaries`.
    * <p>
    * In other orders, this method will attempt to locate the dwebp binary at
    * `/webp_binaries/cwebp`
    * <p>
    * The binary can be downloaded here:
    * https://storage.googleapis.com/downloads.webmproject.org/releases/webp/index.html
    */
   private static void installDWebp() throws IOException {
      installBinary("/webp_binaries/cwebp", binary);
   }

   public byte[] convert(byte[] bytes,
                         int m,
                         int q,
                         int z,
                         boolean lossless) throws IOException {
      Path input = Files.createTempFile("input", "webp").toAbsolutePath();
      try {
         Files.write(input, bytes, StandardOpenOption.CREATE);
         Path target = Files.createTempFile("to_webp", "webp").toAbsolutePath();
         convert(input, target, m, q, z, lossless);
         return Files.readAllBytes(target);
      } finally {
         input.toFile().delete();
      }
   }

   private void convert(Path input,
                        Path target,
                        int m,
                        int q,
                        int z,
                        boolean lossless) throws IOException {

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
      commands.add(input.toAbsolutePath().toString());
      commands.add("-o");
      commands.add(target.toAbsolutePath().toString());

      ProcessBuilder builder = new ProcessBuilder(commands);
      builder.redirectErrorStream(true);
      builder.redirectOutput(stdout.toFile());

      Process process = builder.start();
      try {
         process.waitFor(5, TimeUnit.MINUTES);
      } catch (InterruptedException e) {
         throw new IOException(e);
      }
      int exitStatus = process.exitValue();
      if (exitStatus != 0) {
         List<String> error = Files.readAllLines(stdout);
         throw new IOException(error.toString());
      }
   }
}
