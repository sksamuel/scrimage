package com.sksamuel.scrimage.nio;

import com.sksamuel.scrimage.nio.internal.GifSequenceReader;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class AnimatedGifReader {
   public static AnimatedGif read(ImageSource source) throws IOException {
      GifSequenceReader reader = new GifSequenceReader();
      int status = reader.read(new ByteArrayInputStream(source.read()));
      if (status != 0)
         throw new IOException("Error loading animated GIF");
      return new AnimatedGif(reader);
   }
}
