package com.sksamuel.scrimage.nio;

import com.sksamuel.scrimage.ImmutableImage;

import java.awt.Rectangle;
import java.io.IOException;

public interface ImageReader {

   default ImmutableImage read(byte[] bytes) throws IOException {
      return read(bytes, null);
   }

   default ImmutableImage read(byte[] bytes, Rectangle rectangle) throws IOException {
      ImmutableImage image = read(bytes);
      if (image == null)
         return null;
      else
         return image.subimage(rectangle);
   }
}
