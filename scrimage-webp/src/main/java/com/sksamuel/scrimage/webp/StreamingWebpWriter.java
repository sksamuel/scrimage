package com.sksamuel.scrimage.webp;

import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.nio.PngWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Builds an animated WebP one frame at a time, mirroring the contract of
 * {@link com.sksamuel.scrimage.nio.StreamingGifWriter}. Use this when you want
 * to assemble a WebP animation from frames you produce on the fly without
 * having to hold every frame in memory at once, and without going via GIF
 * first ({@link Gif2WebpWriter} covers the GIF route).
 *
 * <p>Typical usage:
 * <pre>{@code
 * StreamingWebpWriter writer = new StreamingWebpWriter()
 *    .withFrameDelay(Duration.ofMillis(100))
 *    .withInfiniteLoop(true);
 *
 * try (StreamingWebpWriter.WebpStream stream = writer.prepareStream(target)) {
 *    stream.writeFrame(frame0);
 *    stream.writeFrame(frame1, Duration.ofMillis(250));
 *    stream.writeFrame(frame2);
 * }
 * }</pre>
 *
 * <p>Implementation notes: img2webp is a one-shot CLI that consumes all of its
 * inputs at once, so {@link WebpStream#writeFrame(ImmutableImage)} buffers each
 * frame to disk as a PNG and the actual encode happens at {@link
 * WebpStream#close()}. Buffering on disk (instead of memory) keeps the working
 * set bounded for long animations.
 */
public class StreamingWebpWriter {

   /**
    * Eagerly load {@link Img2WebpHandler} so that the bundled binary is unpacked
    * and chmod'd the moment {@code StreamingWebpWriter} is first referenced —
    * not on the very first {@link WebpStream#close()} call deep inside a
    * try-with-resources, where a missing binary would otherwise surface as a
    * confusing failure inside an animation pipeline.
    */
   private static final Img2WebpHandler HANDLER = new Img2WebpHandler();

   private final Duration frameDelay;
   private final boolean infiniteLoop;
   private final boolean lossless;
   private final int q;
   private final int m;

   public StreamingWebpWriter() {
      this(Duration.ofSeconds(2), true, true, -1, -1);
   }

   public StreamingWebpWriter(Duration frameDelay,
                              boolean infiniteLoop,
                              boolean lossless,
                              int q,
                              int m) {
      this.frameDelay = frameDelay;
      this.infiniteLoop = infiniteLoop;
      this.lossless = lossless;
      this.q = q;
      this.m = m;
   }

   /**
    * Default delay used by {@link WebpStream#writeFrame(ImmutableImage)}.
    */
   public StreamingWebpWriter withFrameDelay(Duration delay) {
      return new StreamingWebpWriter(delay, infiniteLoop, lossless, q, m);
   }

   /**
    * If {@code true} (the default) the animation loops forever; otherwise it
    * plays exactly once.
    */
   public StreamingWebpWriter withInfiniteLoop(boolean infiniteLoop) {
      return new StreamingWebpWriter(frameDelay, infiniteLoop, lossless, q, m);
   }

   /**
    * If {@code true} (the default) frames are encoded with {@code -lossless};
    * otherwise with {@code -lossy}.
    */
   public StreamingWebpWriter withLossless(boolean lossless) {
      return new StreamingWebpWriter(frameDelay, infiniteLoop, lossless, q, m);
   }

   /**
    * RGB quality factor in {@code [0, 100]}. Ignored unless set explicitly.
    */
   public StreamingWebpWriter withQ(int q) {
      if (q < 0 || q > 100) throw new IllegalArgumentException("q must be between 0 and 100");
      return new StreamingWebpWriter(frameDelay, infiniteLoop, lossless, q, m);
   }

   /**
    * Encoding method in {@code [0, 6]}. Ignored unless set explicitly.
    */
   public StreamingWebpWriter withM(int m) {
      if (m < 0 || m > 6) throw new IllegalArgumentException("m must be between 0 and 6");
      return new StreamingWebpWriter(frameDelay, infiniteLoop, lossless, q, m);
   }

   /**
    * Mirrors {@link com.sksamuel.scrimage.nio.StreamingGifWriter.GifStream}:
    * the caller writes frames incrementally and the actual encode happens on
    * {@link #close()}.
    */
   public interface WebpStream extends AutoCloseable {

      /**
       * Writes the given frame using the writer's default {@code frameDelay}.
       */
      WebpStream writeFrame(ImmutableImage image) throws IOException;

      /**
       * Writes the given frame with an explicit display duration.
       */
      WebpStream writeFrame(ImmutableImage image, Duration delay) throws IOException;

      /**
       * Encodes the buffered frames into the destination supplied to
       * {@code prepareStream} and releases all temp resources.
       */
      @Override
      void close() throws IOException;
   }

   public WebpStream prepareStream(String path) throws IOException {
      return prepareStream(Paths.get(path));
   }

   public WebpStream prepareStream(Path path) throws IOException {
      return prepareStream(path.toFile());
   }

   public WebpStream prepareStream(File file) throws IOException {
      FileOutputStream output = new FileOutputStream(file);
      try {
         return prepareStream(output);
      } catch (RuntimeException e) {
         try {
            output.close();
         } catch (IOException suppressed) {
            e.addSuppressed(suppressed);
         }
         throw e;
      }
   }

   public WebpStream prepareStream(OutputStream output) {

      final List<Path> inputs = new ArrayList<>();
      final List<Integer> delays = new ArrayList<>();

      return new WebpStream() {

         private boolean closed = false;

         @Override
         public WebpStream writeFrame(ImmutableImage image) throws IOException {
            return writeFrame(image, frameDelay);
         }

         @Override
         public WebpStream writeFrame(ImmutableImage image, Duration delay) throws IOException {
            if (closed) throw new IOException("WebpStream is already closed");
            // PngWriter is the lossless format img2webp ingests directly
            // without re-encoding. NoCompression keeps writeFrame fast since
            // these PNGs are intermediates that get discarded at close().
            byte[] pngBytes = image.bytes(PngWriter.NoCompression);
            Path temp = Files.createTempFile("scrimage_webp_frame_", ".png");
            Files.write(temp, pngBytes, StandardOpenOption.CREATE);
            inputs.add(temp);
            delays.add((int) delay.toMillis());
            return this;
         }

         @Override
         public void close() throws IOException {
            if (closed) return;
            closed = true;
            try {
               if (inputs.isEmpty()) {
                  throw new IOException("Cannot close StreamingWebpWriter without writing any frames");
               }
               int[] delayArr = new int[delays.size()];
               for (int i = 0; i < delayArr.length; i++) delayArr[i] = delays.get(i);
               int loopCount = infiniteLoop ? 0 : 1;

               Path target = Files.createTempFile("scrimage_webp_out_", ".webp");
               try {
                  HANDLER.convert(inputs, delayArr, target, loopCount, q, m, lossless);
                  Files.copy(target, output);
               } finally {
                  try {
                     target.toFile().delete();
                  } catch (Exception ignored) {
                  }
               }
            } finally {
               for (Path input : inputs) {
                  try {
                     input.toFile().delete();
                  } catch (Exception ignored) {
                  }
               }
               output.close();
            }
         }
      };
   }
}
