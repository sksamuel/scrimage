package com.sksamuel.scrimage.nio;

import at.dhyan.open_imaging.GifDecoder;
import com.sksamuel.scrimage.ImmutableImage;

import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

   /**
    * Returns all frames from a GIF.
    */
   public List<ImmutableImage> readAll(byte[] bytes) throws IOException {
      final GifDecoder.GifImage gif = GifDecoder.read(bytes);
      List<ImmutableImage> images = new ArrayList<>(gif.getFrameCount());
      for (int k = 0; k < gif.getFrameCount(); k++) {
         ImmutableImage image = ImmutableImage.wrapAwt(gif.getFrame(k));
         images.add(image);
      }
      return images;
   }
}
