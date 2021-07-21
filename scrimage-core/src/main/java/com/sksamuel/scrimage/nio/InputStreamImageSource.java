package com.sksamuel.scrimage.nio;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public class InputStreamImageSource implements ImageSource {

   private final InputStream stream;

   public InputStreamImageSource(InputStream stream) throws IOException {
      if (stream == null) throw new IOException("Stream is null");
      this.stream = stream;
   }

   @Override
   public byte[] read() throws IOException {
      return IOUtils.toByteArray(stream);
   }
}

