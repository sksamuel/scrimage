package com.sksamuel.scrimage.webp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Wraps the libwebp {@code webpmux} binary, exposing only the metadata-readout
 * subset that {@link AnimatedWebpReader} needs ({@code -info}). The underlying
 * tool can also mux frames, set ICC/EXIF/XMP and the like — those are not
 * exposed yet.
 */
public class WebpMuxHandler extends WebpHandler {

   private static final Path binary;

   static {
      try {
         Path pathFromProperty = getPathFromProperty("webpmux");
         if (pathFromProperty != null) {
            binary = pathFromProperty;
         } else {
            binary = createPlaceholder("webpmux");
            installWebpMux();
         }
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   private static void installWebpMux() throws IOException {
      installBinary(binary, getBinaryPaths("webpmux"));
   }

   /**
    * Parsed result of {@code webpmux -info}.
    *
    * <p>For non-animated WebPs the per-frame list will be empty and
    * {@code loopCount} will be {@code 0}.
    */
   public static final class WebpInfo {
      public final int canvasWidth;
      public final int canvasHeight;
      public final int loopCount;
      public final List<FrameInfo> frames;

      public WebpInfo(int canvasWidth, int canvasHeight, int loopCount, List<FrameInfo> frames) {
         this.canvasWidth = canvasWidth;
         this.canvasHeight = canvasHeight;
         this.loopCount = loopCount;
         this.frames = Collections.unmodifiableList(frames);
      }
   }

   /**
    * One row of the per-frame table from {@code webpmux -info}.
    */
   public static final class FrameInfo {
      public final int width;
      public final int height;
      public final int xOffset;
      public final int yOffset;
      /** Frame display duration in milliseconds. */
      public final int durationMs;

      public FrameInfo(int width, int height, int xOffset, int yOffset, int durationMs) {
         this.width = width;
         this.height = height;
         this.xOffset = xOffset;
         this.yOffset = yOffset;
         this.durationMs = durationMs;
      }
   }

   /**
    * Run {@code webpmux -info} against the given WebP bytes and parse the
    * canvas size, loop count and per-frame table out of its stdout.
    */
   public WebpInfo info(byte[] webpBytes) throws IOException {
      // Nest the two temp-file lifetimes: if the second createTempFile
      // throws (disk full between the two calls), `input` is still cleaned
      // up. Previously both creates lived outside the try and a
      // mid-creation failure leaked the first one.
      Path input = Files.createTempFile("webpmux_in_", ".webp").toAbsolutePath();
      try {
         Path stdout = Files.createTempFile("webpmux_stdout_", ".log");
         try {
            Files.write(input, webpBytes, StandardOpenOption.CREATE);

            List<String> commands = new ArrayList<>();
            commands.add(binary.toAbsolutePath().toString());
            commands.add("-info");
            commands.add(input.toAbsolutePath().toString());

            ProcessBuilder builder = new ProcessBuilder(commands);
            builder.redirectErrorStream(true);
            builder.redirectOutput(stdout.toFile());

            Process process = builder.start();
            try {
               boolean finished = process.waitFor(1, TimeUnit.MINUTES);
               if (!finished) {
                  throw new IOException("webpmux did not complete within 1 minute");
               }
               int exitStatus = process.exitValue();
               List<String> lines = Files.readAllLines(stdout);
               if (exitStatus != 0) {
                  throw new IOException("webpmux exited with status " + exitStatus + ": " + lines);
               }
               return parseInfo(lines);
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
      } finally {
         try {
            input.toFile().delete();
         } catch (Exception ignored) {
         }
      }
   }

   /**
    * Parse the textual output of {@code webpmux -info}. Sample:
    * <pre>
    * Canvas size: 648 x 648
    * Features present: animation transparency
    * Background color : 0xFF18183D  Loop Count : 0
    * Number of frames: 34
    * No.: width height alpha x_offset y_offset duration   dispose blend image_size  compression
    *   1:   648   648    no        0        0       30       none    no      20042    lossless
    *   ...
    * </pre>
    */
   static WebpInfo parseInfo(List<String> lines) throws IOException {
      int canvasWidth = -1;
      int canvasHeight = -1;
      int loopCount = 0;
      List<FrameInfo> frames = new ArrayList<>();
      boolean inFrameTable = false;

      for (String line : lines) {
         String trimmed = line.trim();
         if (trimmed.startsWith("Canvas size:")) {
            String[] parts = trimmed.substring("Canvas size:".length()).trim().split("x");
            if (parts.length == 2) {
               canvasWidth = Integer.parseInt(parts[0].trim());
               canvasHeight = Integer.parseInt(parts[1].trim());
            }
         } else if (trimmed.contains("Loop Count")) {
            // "Background color : 0xFF18183D  Loop Count : 0" or just "Loop Count : 0"
            int idx = trimmed.indexOf("Loop Count");
            String tail = trimmed.substring(idx + "Loop Count".length()).trim();
            if (tail.startsWith(":")) tail = tail.substring(1).trim();
            // tail may contain trailing tokens; take the first whitespace-separated token
            String[] toks = tail.split("\\s+");
            if (toks.length > 0) {
               try {
                  loopCount = Integer.parseInt(toks[0]);
               } catch (NumberFormatException ignored) {
                  // leave default
               }
            }
         } else if (trimmed.startsWith("No.:")) {
            inFrameTable = true;
         } else if (inFrameTable && !trimmed.isEmpty()) {
            // Parse frame row: index: width height alpha x y duration dispose blend image_size compression
            String[] toks = trimmed.split("\\s+");
            // toks[0] is "1:", "2:", etc.
            if (toks.length >= 7 && toks[0].endsWith(":")) {
               try {
                  int width = Integer.parseInt(toks[1]);
                  int height = Integer.parseInt(toks[2]);
                  // toks[3] = alpha (yes/no)
                  int xOffset = Integer.parseInt(toks[4]);
                  int yOffset = Integer.parseInt(toks[5]);
                  int duration = Integer.parseInt(toks[6]);
                  frames.add(new FrameInfo(width, height, xOffset, yOffset, duration));
               } catch (NumberFormatException e) {
                  // not a frame row; ignore
               }
            }
         }
      }

      if (canvasWidth < 0 || canvasHeight < 0) {
         throw new IOException("Could not parse canvas size from webpmux -info output: " + lines);
      }
      return new WebpInfo(canvasWidth, canvasHeight, loopCount, frames);
   }
}
