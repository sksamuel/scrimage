package com.sksamuel.scrimage.webp;

import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.nio.ImageIOReader;
import com.sksamuel.scrimage.nio.ImageReader;

import java.io.IOException;

public class WebpImageReader implements ImageReader {

   @Override
   public ImmutableImage read(byte[] bytes) throws IOException {
      final DWebpHandler handler;
      try {
         handler = new DWebpHandler();
      } catch (Throwable t) {
         throw new IOException("Could not initialize dwebp handler", t);
      }
      byte[] png = handler.convert(bytes);
      return new ImageIOReader().read(png);
   }
}
