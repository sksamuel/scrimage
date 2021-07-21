package com.sksamuel.scrimage.nio;

import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.nio.internal.GifSequenceReader;

import java.awt.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class AnimatedGif {

   private final GifSequenceReader reader;

   public AnimatedGif(GifSequenceReader reader) {
      this.reader = reader;
   }

   public int getFrameCount() {
      return reader.getFrameCount();
   }

   public Dimension getDimensions() {
      return reader.getFrameSize();
   }

   public int getLoopCount() {
      return reader.getLoopCount();
   }

   public Duration getDelay(int frame) {
      return Duration.ofMillis(reader.getDelay(frame));
   }

   public ImmutableImage getFrame(int n) {
      return ImmutableImage.wrapAwt(reader.getFrame(n));
   }

   public List<ImmutableImage> getFrames() {
      List<ImmutableImage> frames = new ArrayList<>();
      for (int k = 0; k < getFrameCount(); k++) {
         frames.add(getFrame(k));
      }
      return frames;
   }
}
