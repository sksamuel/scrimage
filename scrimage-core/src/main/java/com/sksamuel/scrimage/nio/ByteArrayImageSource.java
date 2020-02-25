package com.sksamuel.scrimage.nio;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ByteArrayImageSource implements ImageSource {

   private final byte[] bytes;

   public ByteArrayImageSource(byte[] bytes) {
      this.bytes = bytes;
   }

   @Override
   public InputStream open() throws IOException {
      return new ByteArrayInputStream(bytes);
   }
}
