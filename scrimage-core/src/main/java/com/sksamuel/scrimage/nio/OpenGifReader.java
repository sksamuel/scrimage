package com.sksamuel.scrimage.nio;

import at.dhyan.open_imaging.GifDecoder;
import com.sksamuel.scrimage.ImmutableImage;

import java.awt.Rectangle;
import java.io.IOException;

public class OpenGifReader implements ImageReader {

   @Override
   public ImmutableImage read(byte[] bytes, Rectangle rectangle) throws IOException {
      final GifDecoder.GifImage gif = GifDecoder.read(bytes);
      ImmutableImage image = ImmutableImage.wrapAwt(gif.getFrame(0));
      if (rectangle != null) {
         image = image.subimage(rectangle);
      }
      return image;
   }
}
