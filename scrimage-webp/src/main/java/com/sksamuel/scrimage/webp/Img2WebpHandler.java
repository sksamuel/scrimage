package com.sksamuel.scrimage.webp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Wraps the libwebp {@code img2webp} binary, which encodes a sequence of input
 * frame images (PNG / JPEG / TIFF / PNM / WebP) into a single animated WebP.
 *
 * <p>This is the counterpart to {@link Gif2WebpHandler}: where {@code gif2webp}
 * takes an existing animated GIF and re-encodes it as an animated WebP,
 * {@code img2webp} composes a brand-new animation from individually supplied
 * frames. Use this when you have already-rendered frames (e.g. produced by
 * scrimage) rather than a GIF.
 *
 * <p>See the upstream documentation:
 * <a href="https://developers.google.com/speed/webp/docs/img2webp">img2webp</a>
 */
public class Img2WebpHandler extends WebpHandler {

   private static final Path binary;

   static {
      try {
         Path pathFromProperty = getPathFromProperty("img2webp");
         if (pathFromProperty != null) {
            binary = pathFromProperty;
         } else {
            binary = createPlaceholder("img2webp");
            installImg2Webp();
         }
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   /**
    * Scrimage includes the latest img2webp binary. Alternatively, you can provide another
    * version by placing it on the classpath at {@code /webp_binaries/img2webp}.
    * <p>
    * Binaries can be downloaded here:
    * https://storage.googleapis.com/downloads.webmproject.org/releases/webp/index.html
    */
   private static void installImg2Webp() throws IOException {
      installBinary(binary, getBinaryPaths("img2webp"));
   }

   /**
    * Encode a list of in-memory frames into a single animated WebP, using the same
    * frame delay, loop count, and encoding parameters for every frame.
    *
    * @param frames        list of frame bytes; each frame must be in a format
    *                      img2webp accepts (PNG, JPEG, TIFF, PNM, WebP)
    * @param frameDelayMs  per-frame display duration in milliseconds, or {@code -1}
    *                      to leave img2webp's default (100 ms)
    * @param loopCount     number of times the animation should loop, or {@code -1}
    *                      to leave img2webp's default (0 = loop forever)
    * @param q             RGB quality factor 0..100, or {@code -1} for the default
    * @param m             encoding method 0..6, or {@code -1} for the default
    * @param lossless      if {@code true} emits {@code -lossless}; otherwise emits
    *                      {@code -lossy} (img2webp's overall default is lossless)
    * @return the encoded animated WebP bytes
    */
   public byte[] convert(List<byte[]> frames,
                         int frameDelayMs,
                         int loopCount,
                         int q,
                         int m,
                         boolean lossless) throws IOException {
      if (frames == null || frames.isEmpty()) {
         throw new IllegalArgumentException("At least one frame is required");
      }
      List<Path> inputs = new ArrayList<>(frames.size());
      Path output = Files.createTempFile("img2webp_out", ".webp").toAbsolutePath();
      try {
         for (int i = 0; i < frames.size(); i++) {
            Path input = Files.createTempFile("img2webp_in_" + i + "_", ".bin").toAbsolutePath();
            inputs.add(input);
            Files.write(input, frames.get(i), StandardOpenOption.CREATE);
         }
         convert(inputs, output, frameDelayMs, loopCount, q, m, lossless);
         return Files.readAllBytes(output);
      } finally {
         for (Path input : inputs) {
            try {
               input.toFile().delete();
            } catch (Exception ignored) {
            }
         }
         try {
            output.toFile().delete();
         } catch (Exception ignored) {
         }
      }
   }

   /**
    * Encode the given frame files into an animated WebP at {@code target},
    * using a single uniform delay for every frame.
    */
   public void convert(List<Path> inputs,
                       Path target,
                       int frameDelayMs,
                       int loopCount,
                       int q,
                       int m,
                       boolean lossless) throws IOException {
      int[] delays = new int[inputs.size()];
      for (int i = 0; i < delays.length; i++) delays[i] = frameDelayMs;
      convert(inputs, delays, target, loopCount, q, m, lossless);
   }

   /**
    * Encode the given frame files into an animated WebP at {@code target},
    * with a per-frame delay supplied alongside each input.
    *
    * <p>Per the img2webp CLI, per-frame options ({@code -d}, {@code -lossy} /
    * {@code -lossless}, {@code -q}, {@code -m}) are emitted before each frame.
    * The settings are sticky across frames, but emitting them every time keeps
    * the call unambiguous regardless of frame count and lets each frame have
    * its own delay.
    *
    * @param inputs         the frame files in playback order
    * @param frameDelaysMs  one entry per input giving that frame's display
    *                       duration in milliseconds, or {@code -1} to leave
    *                       img2webp's default of 100 ms for that frame
    * @param target         the output webp path
    * @param loopCount      number of times the animation should loop, or
    *                       {@code -1} to leave img2webp's default of 0
    *                       (loop forever)
    * @param q              RGB quality factor 0..100, or {@code -1} for default
    * @param m              encoding method 0..6, or {@code -1} for default
    * @param lossless       if {@code true} emits {@code -lossless}; otherwise
    *                       {@code -lossy}
    */
   public void convert(List<Path> inputs,
                       int[] frameDelaysMs,
                       Path target,
                       int loopCount,
                       int q,
                       int m,
                       boolean lossless) throws IOException {

      if (inputs == null || inputs.isEmpty()) {
         throw new IllegalArgumentException("At least one frame is required");
      }
      if (frameDelaysMs == null || frameDelaysMs.length != inputs.size()) {
         throw new IllegalArgumentException(
            "frameDelaysMs must have one entry per input frame; got "
               + (frameDelaysMs == null ? "null" : Integer.toString(frameDelaysMs.length))
               + " for " + inputs.size() + " frames");
      }

      Path stdout = Files.createTempFile("img2webp_stdout_", ".log");
      try {
         List<String> commands = new ArrayList<>();
         commands.add(binary.toAbsolutePath().toString());
         if (loopCount >= 0) {
            commands.add("-loop");
            commands.add(Integer.toString(loopCount));
         }
         for (int i = 0; i < inputs.size(); i++) {
            int delay = frameDelaysMs[i];
            if (delay >= 0) {
               commands.add("-d");
               commands.add(Integer.toString(delay));
            }
            commands.add(lossless ? "-lossless" : "-lossy");
            if (q >= 0) {
               commands.add("-q");
               commands.add(Integer.toString(q));
            }
            if (m >= 0) {
               commands.add("-m");
               commands.add(Integer.toString(m));
            }
            commands.add(inputs.get(i).toAbsolutePath().toString());
         }
         commands.add("-o");
         commands.add(target.toAbsolutePath().toString());

         ProcessBuilder builder = new ProcessBuilder(commands);
         builder.redirectErrorStream(true);
         builder.redirectOutput(stdout.toFile());

         Process process = builder.start();
         try {
            boolean finished = process.waitFor(5, TimeUnit.MINUTES);
            if (!finished) {
               throw new IOException("img2webp did not complete within 5 minutes");
            }
            int exitStatus = process.exitValue();
            if (exitStatus != 0) {
               List<String> error = Files.readAllLines(stdout);
               throw new IOException("img2webp exited with status " + exitStatus + ": " + error);
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
