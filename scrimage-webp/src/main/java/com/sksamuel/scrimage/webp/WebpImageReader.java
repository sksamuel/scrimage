package com.sksamuel.scrimage.webp;

import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.nio.ImageIOReader;
import com.sksamuel.scrimage.nio.ImageReader;

import java.io.IOException;

public class WebpImageReader implements ImageReader {

   private final WebpHandler handler = new WebpHandler();

   @Override
   public ImmutableImage read(byte[] bytes) throws IOException {
      try {
         byte[] png = handler.convert(bytes);
         return new ImageIOReader().read(png);
      } catch (InterruptedException e) {
         throw new IOException(e);
      }
   }
}
