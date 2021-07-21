package com.sksamuel.scrimage.nio;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * An ImageSource is a provider of bytes to the ImmutableImageLoader.
 */
public interface ImageSource {

   static ImageSource of(byte[] bytes) {
      return new ByteArrayImageSource(bytes);
   }

   static ImageSource of(File file) {
      return new FileImageSource(file);
   }

   static ImageSource of(InputStream is) throws IOException {
      return new InputStreamImageSource(is);
   }

   byte[] read() throws IOException;
}
