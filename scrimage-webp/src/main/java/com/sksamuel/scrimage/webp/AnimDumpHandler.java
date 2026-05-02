package com.sksamuel.scrimage.webp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * Wraps the libwebp {@code anim_dump} binary, which renders each frame of an
 * animated WebP into a separate PNG (with disposal and blending applied) so
 * the caller gets the final composed pixels for each frame.
 *
 * <p>Use {@link AnimatedWebpReader} for the higher-level API; this class is
 * exposed for advanced callers who want the raw frame bytes.
 */
public class AnimDumpHandler extends WebpHandler {

   private static final Path binary;

   static {
      try {
         Path pathFromProperty = getPathFromProperty("anim_dump");
         if (pathFromProperty != null) {
            binary = pathFromProperty;
         } else {
            binary = createPlaceholder("anim_dump");
            installAnimDump();
         }
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   private static void installAnimDump() throws IOException {
      installBinary(binary, getBinaryPaths("anim_dump"));
   }

   /**
    * Renders each frame of the given animated WebP and returns the per-frame
    * PNG bytes in playback order.
    */
   public List<byte[]> dumpFrames(byte[] webpBytes) throws IOException {
      Path input = Files.createTempFile("anim_dump_in_", ".webp").toAbsolutePath();
      Path folder = Files.createTempDirectory("anim_dump_out_");
      try {
         Files.write(input, webpBytes, StandardOpenOption.CREATE);
         dumpFrames(input, folder);

         List<Path> dumped;
         try (Stream<Path> stream = Files.list(folder)) {
            dumped = new ArrayList<>();
            stream.forEach(dumped::add);
         }
         // anim_dump names files like dump_0000.png, dump_0001.png, ...; sort
         // lexicographically to recover playback order.
         dumped.sort(Comparator.comparing(p -> p.getFileName().toString()));

         List<byte[]> frames = new ArrayList<>(dumped.size());
         for (Path p : dumped) {
            frames.add(Files.readAllBytes(p));
         }
         return frames;
      } finally {
         try {
            input.toFile().delete();
         } catch (Exception ignored) {
         }
         try (Stream<Path> stream = Files.list(folder)) {
            stream.forEach(p -> p.toFile().delete());
         } catch (Exception ignored) {
         }
         try {
            folder.toFile().delete();
         } catch (Exception ignored) {
         }
      }
   }

   /**
    * Lower-level overload that runs anim_dump directly against on-disk files,
    * leaving the PNG outputs in {@code folder}.
    */
   public void dumpFrames(Path input, Path folder) throws IOException {
      Path stdout = Files.createTempFile("anim_dump_stdout_", ".log");
      try {
         List<String> commands = new ArrayList<>();
         commands.add(binary.toAbsolutePath().toString());
         commands.add("-folder");
         commands.add(folder.toAbsolutePath().toString());
         commands.add("-prefix");
         commands.add("dump_");
         commands.add(input.toAbsolutePath().toString());

         ProcessBuilder builder = new ProcessBuilder(commands);
         builder.redirectErrorStream(true);
         builder.redirectOutput(stdout.toFile());

         Process process = builder.start();
         try {
            boolean finished = process.waitFor(5, TimeUnit.MINUTES);
            if (!finished) {
               throw new IOException("anim_dump did not complete within 5 minutes");
            }
            int exitStatus = process.exitValue();
            if (exitStatus != 0) {
               List<String> error = Collections.unmodifiableList(Files.readAllLines(stdout));
               throw new IOException("anim_dump exited with status " + exitStatus + ": " + error);
            }
         } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException(e);
         } finally {
            process.destroy();
         }
      } finally {
         try {
            stdout.toFile().delete();
         } catch (Exception ignored) {
         }
      }
   }
}
