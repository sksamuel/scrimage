package com.sksamuel.scrimage.nio;

import java.io.IOException;
import java.io.InputStream;

public class InputStreamImageSource implements ImageSource {

   private final InputStream stream;

   public InputStreamImageSource(InputStream stream) {
      this.stream = stream;
   }

   @Override
   public InputStream open() throws IOException {
      return stream;
   }
}
