package com.sksamuel.scrimage.webp;

import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.nio.ImageSource;
import com.sksamuel.scrimage.nio.ImmutableImageLoader;

import java.awt.Dimension;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * The animated-WebP analogue of {@link com.sksamuel.scrimage.nio.AnimatedGifReader}.
 *
 * <p>Reads an animated WebP from any {@link ImageSource} and decomposes it into
 * its per-frame {@link ImmutableImage}s plus the canvas size, loop count and
 * per-frame display durations.
 *
 * <p>Frame extraction is done by libwebp's {@code anim_dump}, which renders
 * each frame with disposal and blending already applied — so {@link
 * AnimatedWebp#getFrame(int)} returns the final composed image for that frame,
 * not the raw inter-frame patch. Per-frame metadata is parsed out of
 * {@code webpmux -info}.
 */
public class AnimatedWebpReader {

   /**
    * Eager handler initialisation, identical in spirit to
    * {@link StreamingWebpWriter}: any binary install / chmod problem surfaces
    * the moment {@code AnimatedWebpReader} is first referenced rather than
    * deep inside the first {@link #read(ImageSource)} call.
    */
   private static final AnimDumpHandler ANIM_DUMP = new AnimDumpHandler();
   private static final WebpMuxHandler WEBP_MUX = new WebpMuxHandler();

   public static AnimatedWebp read(ImageSource source) throws IOException {
      byte[] bytes = source.read();

      WebpMuxHandler.WebpInfo info = WEBP_MUX.info(bytes);
      List<byte[]> framePngs = ANIM_DUMP.dumpFrames(bytes);

      if (!info.frames.isEmpty() && framePngs.size() != info.frames.size()) {
         throw new IOException(
            "anim_dump produced " + framePngs.size() + " frames but webpmux -info reported "
               + info.frames.size() + " — refusing to associate them");
      }

      ImmutableImageLoader loader = ImmutableImageLoader.create();
      List<ImmutableImage> frames = new ArrayList<>(framePngs.size());
      for (byte[] png : framePngs) {
         frames.add(loader.fromBytes(png));
      }

      List<Duration> delays = new ArrayList<>(framePngs.size());
      if (info.frames.isEmpty()) {
         // Non-animated WebP that anim_dump still emitted as a single frame:
         // there's no duration metadata to attach, so use zero.
         for (int i = 0; i < framePngs.size(); i++) delays.add(Duration.ZERO);
      } else {
         for (WebpMuxHandler.FrameInfo frame : info.frames) {
            delays.add(Duration.ofMillis(frame.durationMs));
         }
      }

      Dimension dimensions = new Dimension(info.canvasWidth, info.canvasHeight);
      return new AnimatedWebp(frames, delays, dimensions, info.loopCount, bytes);
   }
}
