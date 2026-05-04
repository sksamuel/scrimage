package com.sksamuel.scrimage.webp;

import com.sksamuel.scrimage.ImmutableImage;

import java.awt.Dimension;
import java.time.Duration;
import java.util.Collections;
import java.util.List;

/**
 * The animated-WebP analogue of {@link com.sksamuel.scrimage.nio.AnimatedGif}.
 *
 * <p>Holds the per-frame {@link ImmutableImage}s (already composed by
 * {@code anim_dump}, so disposal and blending have been applied) along with
 * the canvas size, loop count and per-frame display durations parsed from the
 * source file's metadata.
 *
 * <p>Construct via {@link AnimatedWebpReader#read(com.sksamuel.scrimage.nio.ImageSource)}.
 */
public class AnimatedWebp {

   private final List<ImmutableImage> frames;
   private final List<Duration> delays;
   private final Dimension dimensions;
   private final int loopCount;
   private final byte[] bytes;

   public AnimatedWebp(List<ImmutableImage> frames,
                       List<Duration> delays,
                       Dimension dimensions,
                       int loopCount,
                       byte[] bytes) {
      if (frames.size() != delays.size()) {
         throw new IllegalArgumentException(
            "frames and delays must have the same length; got "
               + frames.size() + " frames and " + delays.size() + " delays");
      }
      this.frames = Collections.unmodifiableList(frames);
      this.delays = Collections.unmodifiableList(delays);
      this.dimensions = dimensions;
      this.loopCount = loopCount;
      this.bytes = bytes;
   }

   public int getFrameCount() {
      return frames.size();
   }

   public Dimension getDimensions() {
      return dimensions;
   }

   /**
    * Number of times the animation loops; {@code 0} means loop forever.
    */
   public int getLoopCount() {
      return loopCount;
   }

   public Duration getDelay(int frame) {
      return delays.get(frame);
   }

   public ImmutableImage getFrame(int n) {
      return frames.get(n);
   }

   public List<ImmutableImage> getFrames() {
      return frames;
   }

   /**
    * The original WebP bytes the reader was given.
    */
   public byte[] getBytes() {
      return bytes;
   }
}
