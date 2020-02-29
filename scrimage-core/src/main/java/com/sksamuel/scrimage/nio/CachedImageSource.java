package com.sksamuel.scrimage.nio;

import java.io.IOException;

public class CachedImageSource implements ImageSource {

   private byte[] bytes = null;

   private final ImageSource source;

   public CachedImageSource(ImageSource source) {
      this.source = source;
   }

   @Override
   public byte[] read() throws IOException {
      if (bytes == null)
         bytes = source.read();
      return bytes;
   }
}
