package com.sksamuel.scrimage.nio;

import com.sksamuel.scrimage.DisposeMethod;
import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.nio.internal.GifSequenceReader;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
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

   public DisposeMethod getDisposeMethod(int frame) {
      return reader.getDisposeMethod(frame);
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

   public byte[] getBytes() throws IOException {
      return reader.bytes();
   }

   public Path output(AnimatedImageWriter writer, String path) throws IOException {
      return forWriter(writer).write(Paths.get(path));
   }

   public File output(AnimatedImageWriter writer, File file) throws IOException {
      return forWriter(writer).write(file);
   }

   public Path output(AnimatedImageWriter writer, Path path) throws IOException {
      return forWriter(writer).write(path);
   }

   public byte[] bytes(AnimatedImageWriter writer) throws IOException {
      return forWriter(writer).bytes();
   }

   public AnimatedWriteContext forWriter(AnimatedImageWriter writer) throws IOException {
      return new AnimatedWriteContext(writer, this);
   }
}
