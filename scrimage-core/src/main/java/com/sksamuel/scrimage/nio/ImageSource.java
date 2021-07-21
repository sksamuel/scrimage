package com.sksamuel.scrimage.nio;

import java.io.IOException;

/**
 * An ImageSource is a provider of bytes to the ImmutableImageLoader.
 */
public interface ImageSource {

   static ImageSource of(byte[] bytes) {
      return new ByteArrayImageSource(bytes);
   }

   byte[] read() throws IOException;
}
